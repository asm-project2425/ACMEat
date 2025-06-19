import express from "express";
import { pool } from "./index.js";

export const router = express.Router();

router.get('/api/v1/vehicles', async function (req, res) {
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

router.use(express.static('frontend'));