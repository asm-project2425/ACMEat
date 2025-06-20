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
    if (!req.body || req.body.vehicleId == null || req.body.deliveryId == null) {
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
    if (!req.body || req.body.vehicleId == null) {
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

app.listen(port, () => console.log(`VehicleTracker service listening on port ${port}`));
