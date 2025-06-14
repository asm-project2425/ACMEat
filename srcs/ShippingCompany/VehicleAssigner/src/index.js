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

app.post('/api/v1/reserve', async function (req, res) {
    if (!req.body || !req.body.deliveryTime || req.body.orderId == null ||
        !req.body.cost || !req.body.restaurantAddress || !req.body.deliveryAddress) {
        res.sendStatus(400);
        return;
    }

    const deliveryTime = new Date(req.body.deliveryTime);

    const vehiclesInUse = "SELECT vehicle_id FROM deliveries WHERE \
status != 'cancelled' AND status != 'delivered' AND \
(($1::TIMESTAMPTZ - make_interval(mins => 5)) >= (time - make_interval(mins => 5)) AND \
($1::TIMESTAMPTZ - make_interval(mins => 5)) < (time + make_interval(mins => 5))) OR \
(($1::TIMESTAMPTZ + make_interval(mins => 5)) > (time - make_interval(mins => 5)) AND \
($1::TIMESTAMPTZ + make_interval(mins => 5)) <= (time + make_interval(mins => 5)))";

    let vehicle;
    try {
        vehicle = await pool.query(
            `SELECT id
             FROM vehicles
             WHERE id NOT IN (${vehiclesInUse})
             LIMIT 1`,
            [deliveryTime]
        );
    } catch (e) {
        console.error("DB error:");
        console.error(e);
        res.status(500).send('Internal db error');
        return;
    }

    if (vehicle.rows.length === 0) {
        res.status(409).send('No vehicles available');
        return;
    }

    try {
        let deliveryId = await pool.query(
            "INSERT INTO deliveries (vehicle_id, order_id, cost, time, local_address, client_address) VALUES ($1, $2, $3, $4, $5, $6) RETURNING id",
            [
                vehicle.rows[0].id,
                req.body.orderId,
                req.body.cost,
                deliveryTime,
                req.body.restaurantAddress,
                req.body.deliveryAddress
            ]
        );
        res.status(201).json({
            deliveryId: deliveryId.rows[0].id
        });
    } catch (e) {
        console.error("DB error:");
        console.error(e);
        res.status(500).send('Internal db error');
    }
});

const backgroundTask = async function() {
    try {
        await pool.query("UPDATE deliveries SET status = 'cancelled' WHERE status = 'created' AND (created_at + make_interval(mins => 2)) < now()");
    } catch (e) {
        console.error("Background task: DB error:");
        console.error(e);
    }

    setTimeout(backgroundTask, 1000 * 60); // Every minute
};
setTimeout(backgroundTask, 1000 * 60); // Every minute

app.listen(port, () => console.log(`VehicleAssigner service listening on port ${port}`));
