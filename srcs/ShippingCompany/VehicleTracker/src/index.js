import express from "express";
import { Pool } from 'pg';

const app = express();
const port = process.env.PORT || 3000;

app.use(express.json());

const pool = new Pool();
pool.on('error', (err, client) => {
    console.error('Unexpected error on idle client', err);
    process.exit(-1);
});

app.post('/api/v1/deliveryStarted', async function (req, res) {
    if (!req.body || !req.body.vehicleId || !req.body.deliveryId) {
        res.sendStatus(400);
        return;
    }

    let result;
    try {
        result = await pool.query(
            "UPDATE vehicles SET status = 'in_use', current_delivery_id = $1 WHERE id = $2 RETURNING id",
            [req.body.deliveryId, req.body.vehicleId]
        );
    } catch (e) {
        console.error("DB error:");
        console.error(e);
        res.status(500).send('Internal db error');
        return;
    }
    res.sendStatus(result.rows.length === 0 ? 404 : 200);
});

app.post('/api/v1/deliveryEnded', async function (req, res) {
    if (!req.body || !req.body.vehicleId) {
        res.sendStatus(400);
        return;
    }

    let result;
    try {
        result = await pool.query(
            "UPDATE vehicles SET status = 'available', current_delivery_id = NULL WHERE id = $1 RETURNING id",
            [req.body.vehicleId]
        );
    } catch (e) {
        console.error("DB error:");
        console.error(e);
        res.status(500).send('Internal db error');
        return;
    }
    res.sendStatus(result.rows.length === 0 ? 404 : 200);
});

app.get('/api/v1/vehicles', async function (req, res) {
    let result;
    try {
        result = await pool.query("SELECT * FROM vehicles ORDER BY id");
    } catch (e) {
        console.error("DB error:");
        console.error(e);
        res.status(500).send('Internal db error');
        return;
    }

    const vehicles = [];
    for (const vehicle of result.rows) {
        let current_delivery = null;
        if (vehicle.current_delivery_id != null) {
            let delivery;
            try {
                delivery = await pool.query("SELECT * FROM deliveries WHERE id = $1", [vehicle.current_delivery_id]);
            } catch (e) {
                console.error("DB error:");
                console.error(e);
                res.status(500).send('Internal db error');
                return;
            }
            delivery = delivery.rows[0];
            current_delivery = {
                delivery_id: delivery.id,
                order_id: delivery.order_id,
                status: delivery.status,
                cost: delivery.cost,
                time: delivery.time,
                local_address: delivery.local_address,
                client_address: delivery.client_address,
            };
        }

        vehicles.push({
            id: vehicle.id,
            status: vehicle.status,
            current_delivery,
        });
    }

    res.status(200).json(vehicles);
});

app.use(express.static('frontend'));

app.listen(port, () => console.log(`VehicleTracker service listening on port ${port}`));
