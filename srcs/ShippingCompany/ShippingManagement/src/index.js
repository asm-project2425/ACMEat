import express from "express";
import { Pool } from 'pg';
import { router as backendRouter } from "./backend.js";

const app = express();
const port = process.env.PORT || 3000;
const vehicle_assigner_url = "http://vehicle_assigner:3000/api/v1";
const vehicle_tracker_url = "http://vehicle_tracker:3000/api/v1";
const acmeat_backend_url = "http://acmeat:8080/api/v1";
const gis_url = "http://gis:6002/api/v1";

app.use(express.json());

export const pool = new Pool();
pool.on('error', (err, client) => {
    console.error('Unexpected error on idle client', err);
    process.exit(-1);
});

app.use(backendRouter);

app.post('/api/v1/availability', async function (req, res) {
    if (!req.body || req.body.correlationKey == null || req.body.orderId == null ||
        !req.body.deliveryTime || !req.body.restaurantAddress || !req.body.deliveryAddress) {
        res.sendStatus(400);
        return;
    }

    // Availability request received
    res.sendStatus(202);

    const restaurantAddr = await fetch(`${gis_url}/locate?query=${req.body.restaurantAddress}`);
    if (!restaurantAddr.ok) {
        console.error('Internal error locating restaurant address');
        return;
    }

    const { lat: restaurantLat, lon: restaurantLon } = await restaurantAddr.json();

    const deliveryAddr = await fetch(`${gis_url}/locate?query=${req.body.deliveryAddress}&lat=${restaurantLat}&lon=${restaurantLon}`);
    if (!deliveryAddr.ok) {
        console.error('Internal error locating delivery address');
        return;
    }
    const { lat: deliveryLat, lon: deliveryLon } = await deliveryAddr.json();

    const responseDistance = await fetch(`${gis_url}/distance?lat1=${restaurantLat}&lon1=${restaurantLon}&lat2=${deliveryLat}&lon2=${deliveryLon}`);
    if (!responseDistance.ok) {
        console.error(`Error calculating delivery distance: ${await responseDistance.text()}`);
        return;
    }
    const { distance } = await responseDistance.json();

    const cost = round(1 + Math.random() + 0.00015 * distance);

    const assignerRes = await fetch(`${vehicle_assigner_url}/reserve`, {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json'
        },
        body: JSON.stringify({
            deliveryTime: req.body.deliveryTime,
            orderId: req.body.orderId,
            cost,
            restaurantAddress: req.body.restaurantAddress,
            deliveryAddress: req.body.deliveryAddress
        })
    });

    if (!assignerRes.ok) {
        if (assignerRes.status !== 409) { // 409 => no vehicles available
            console.error(`Error reserving vehicle: ${await assignerRes.text()}`);
        }
        return;
    }

    let { deliveryId } = await assignerRes.json();

    console.log(`Delivery id: ${deliveryId}, cost: ${cost}, distance: ${distance} m`);

    // Response to ACMEat
    try {
        const response = await fetch(`${acmeat_backend_url}/shipping-company/cost`, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify({
                correlationKey: req.body.correlationKey,
                shippingCost: cost,
                deliveryId,
            })
        });
        if (!response.ok) {
            throw await response.text();
        }
    } catch (e) {
        console.error(`Error responding to ACMEat: ${e}`);
    }
});

app.post('/api/v1/confirm', async function (req, res) {
    if (!req.body || req.body.deliveryId == null) {
        res.sendStatus(400);
        return;
    }

    const response = await fetch(`${vehicle_assigner_url}/confirmDelivery`, {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json'
        },
        body: JSON.stringify({
            deliveryId: req.body.deliveryId,
        })
    });
    if (!response.ok) {
        res.sendStatus(response.status);
        return;
    }

    res.sendStatus(200);

    runUpdateDeliveries();
});

app.post('/api/v1/cancel', async function (req, res) {
    if (!req.body || req.body.deliveryId == null) {
        res.sendStatus(400);
        return;
    }

    const response = await fetch(`${vehicle_assigner_url}/cancelDelivery`, {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json'
        },
        body: JSON.stringify({
            deliveryId: req.body.deliveryId,
        })
    });
    if (!response.ok) {
        res.sendStatus(response.status);
        return;
    }

    res.sendStatus(200);

    runUpdateDeliveries();
});

let taskId;
async function updateDeliveries() {
    taskId = null;

    let delivering = await pool.query(
        `UPDATE deliveries
         SET status = 'delivering'
         WHERE status = 'confirmed'
           AND now() >= (time - make_interval(mins => 5))
         RETURNING id, vehicle_id`
    );

    for (let row of delivering.rows) {
        await startTracking(row.vehicle_id, row.id);
    }

    let delivered = await pool.query(
        `UPDATE deliveries
         SET status = 'delivered'
         WHERE status = 'delivering'
           AND now() >= time
         RETURNING order_id`
    );

    for (let row of delivered.rows) {
        await notifyOrderDelivered(row.order_id);
    }

    let completed = await pool.query(
        `UPDATE deliveries
         SET status = 'completed'
         WHERE status = 'delivered'
           AND now() >= (time + make_interval(mins => 5))
         RETURNING vehicle_id`
    );

    for (let row of completed.rows) {
        await endTracking(row.vehicle_id);
    }

    taskId = setTimeout(updateDeliveries, 1000 * 10); // Every 10 seconds
}
setImmediate(updateDeliveries);

function runUpdateDeliveries() {
    if (taskId == null) {
        // Task is being run by setImmediate
        return;
    }
    clearTimeout(taskId);
    taskId = null;
    setImmediate(updateDeliveries);
}

async function startTracking(vehicleId, deliveryId) {
    try {
        const response = await fetch(`${vehicle_tracker_url}/deliveryStarted`, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify({
                vehicleId,
                deliveryId
            })
        });
        if (!response.ok) {
            throw await response.text();
        }
    } catch (e) {
        console.error(`Error starting tracking of vehicle ${vehicleId} for delivery ${deliveryId}: ${e}`);
    }
}

export async function endTracking(vehicleId) {
    try {
        const response = await fetch(`${vehicle_tracker_url}/deliveryEnded`, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify({
                vehicleId
            })
        });
        if (!response.ok) {
            throw await response.text();
        }
    } catch (e) {
        console.error(`Error ending tracking of vehicle ${vehicleId}: ${e}`);
    }
}

export async function notifyOrderDelivered(orderId) {
    try {
        const response = await fetch(`${acmeat_backend_url}/orders/delivered?orderId=${orderId}`, {
            method: 'POST'
        });
        if (!response.ok) {
            throw await response.text();
        }
    } catch (e) {
        console.error(`Error notifying order delivery to ACMEat: ${e}`);
    }
}

function round(n) {
    return Math.round(n * 100) / 100;
}

app.listen(port, () => console.log(`ShippingManagement service listening on port ${port}`));
