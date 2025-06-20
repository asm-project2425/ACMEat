import express from "express";
import { pool, endTracking, notifyOrderDelivered } from "./index.js";

export const router = express.Router();

router.get('/backend/vehicles', async function (req, res) {
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
                vehicle_id: delivery.vehicle_id,
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

router.get('/backend/deliveries', async function (req, res) {
    let deliveries;
    try {
        deliveries = await pool.query(
            `SELECT *
             FROM deliveries
             WHERE status = 'confirmed'
                OR status = 'delivering'
             ORDER BY id`
        );
    } catch (e) {
        console.error("DB error:");
        console.error(e);
        res.status(500).send('Internal db error');
        return;
    }
    res.status(200).json(deliveries.rows.map(delivery => {
        return {
            delivery_id: delivery.id,
            vehicle_id: delivery.vehicle_id,
            order_id: delivery.order_id,
            status: delivery.status,
            cost: delivery.cost,
            time: delivery.time,
            local_address: delivery.local_address,
            client_address: delivery.client_address,
        };
    }));
});

router.post('/backend/simulateDelivery', async function (req, res) {
    if (!req.body || req.body.deliveryId == null) {
        res.sendStatus(400);
        return;
    }

    let result1, result2;
    try {
        result1 = await pool.query(
            `UPDATE deliveries
             SET status = 'completed'
             WHERE id = $1 AND status = 'confirmed'
             RETURNING vehicle_id, order_id`,
            [req.body.deliveryId]
        );
        result2 = await pool.query(
            `UPDATE deliveries
             SET status = 'completed'
             WHERE id = $1 AND status = 'delivering'
             RETURNING vehicle_id, order_id`,
            [req.body.deliveryId]
        );
    } catch (e) {
        console.error("DB error:");
        console.error(e);
        res.status(500).send('Internal db error');
        return;
    }

    if (result1.rows.length === 0 && result2.rows.length === 0) {
        res.sendStatus(404);
        return;
    }
    res.sendStatus(200);

    if (result2.rows.length !== 0) {
        // The vehicle is being tracked
        await endTracking(result2.rows[0].vehicle_id);
    }
    await notifyOrderDelivered((result1.rows[0] ?? result2.rows[0]).order_id);
});

router.use(express.static('frontend'));