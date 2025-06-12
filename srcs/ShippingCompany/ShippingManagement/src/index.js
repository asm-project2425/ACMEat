import express from "express";

const app = express();
const port = process.env.PORT || 3000;

app.get('/api/v1', async function (req, res) {
    res.status(200).send("ShippingManagement running");
});

app.listen(port, () => console.log(`ShippingManagement service listening on port ${port}`));
