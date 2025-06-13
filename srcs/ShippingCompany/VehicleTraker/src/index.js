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

app.get('/api/v1', async function (req, res) {
    res.status(200).send("VehicleTraker running");
});

app.get('/api/v1/testDB', async function (req, res) {
    res.status(200).json((await pool.query("SELECT * FROM vehicles")).rows.map((row) => {
        return {
            "id": row.id,
            "status": row.status,
            "current_delivery_id": row.current_delivery_id
        };
    }));
});

app.listen(port, () => console.log(`VehicleTraker service listening on port ${port}`));
