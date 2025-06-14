import express from "express";

const app = express();
const port = process.env.PORT || 3000;
const vehicle_assigner_url = "http://vehicle_assigner:3000/api/v1";
const vehicle_tracker_url = "http://vehicle_tracker:3000/api/v1";
const gis_url = "http://gis:6002/api/v1";

app.use(express.json());

function round(n) {
    return Math.round(n * 100) / 100;
}

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

    const deliveryAddr = await fetch(`${gis_url}/locate?query=${req.body.deliveryAddress}`);
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

    const cost = round(Math.random() + 0.00015 * distance);

    const assignerRes = await fetch(`${vehicle_assigner_url}/reserve`, {
        method: 'POST',
        headers: {
            'Accept': 'application/json',
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

    // TODO: Response to ACMEat
    console.log(`Delivery id: ${deliveryId}, cost: ${cost}, distance: ${distance} m`);
});

app.post('/api/v1/confirmDelivery', async function (req, res) {
    if (!req.body || !req.body.id) {
        res.sendStatus(400);
        return;
    }

    res.sendStatus(200);
    // TODO
});

app.post('/api/v1/cancelDelivery', async function (req, res) {
    if (!req.body || !req.body.id) {
        res.sendStatus(400);
        return;
    }

    res.sendStatus(200);
    // TODO
});

app.listen(port, () => console.log(`ShippingManagement service listening on port ${port}`));
