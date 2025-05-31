import express from "express";
import { getDistance } from 'geolib';
import Bottleneck from "bottleneck";

const app = express();
const port = process.env.PORT || 6002;
const queryCache = new Map();
const cityCache = new Map();

let current_url = 0;
const urls = [
    "https://nominatim.openstreetmap.org",
    "https://nominatim.geocoding.ai"
];
const limiters = [
    new Bottleneck({
        minTime: 1000
    }),
    new Bottleneck({
        minTime: 1000
    })
];

function validateParam(req, name, res) {
    if (!req.query[name]) {
        res.status(400).send(`Bad request: missing '${name}' parameter`);
        return true;
    }
    return false;
}

function validateNum(num) {
    return !isNaN(num) && isFinite(num);
}

function validateNumParam(num, name, res) {
    if (!validateNum(num)) {
        res.status(400).send(`Bad request: invalid '${name}' parameter`);
        return true;
    }
    return false;
}

async function locate(query, paramName, cache, res) {
    const cached = cache.get(query);
    if (cached) {
        res.status(200).json(cached);
        return;
    }

    // Use multiple nominatim instances and rate limiters given the limit of 1 request per second
    const url = urls[current_url];
    const limiter = limiters[current_url];
    current_url = (current_url + 1) % urls.length;
    const response = await limiter.schedule(() => fetch(`${url}/search?format=json&limit=1&${paramName}=${query}`, {
        headers: {
            'User-Agent': 'ACMEat',
        }
    }));

    if (!response.ok) {
        res.status(500).send('Internal error');
        return;
    }
    const data = (await response.json())[0];

    if (!data || !data.lat || !data.lon) {
        res.status(500).send('Internal error');
        return;
    }

    let lat = Number(data.lat);
    let lon = Number(data.lon);

    if (!validateNum(lat) || !validateNum(lon)) {
        res.status(500).send('Internal error');
        return;
    }

    cache.set(query, {
        lat: lat,
        lon: lon
    });

    res.status(200).json({
        lat: lat,
        lon: lon
    });
}

app.get('/api/v1/locate', async function (req, res) {
    if (validateParam(req, 'query', res)) {
        return;
    }

    await locate(req.query.query, 'q', queryCache, res);
});

app.get('/api/v1/locateCity', async function (req, res) {
    if (validateParam(req, 'city', res)) {
        return;
    }

    await locate(req.query.city, 'city', cityCache, res);
});

app.get('/api/v1/distance', async function (req, res) {
    if (validateParam(req, 'lat1', res) || validateParam(req, 'lon1', res) || validateParam(req, 'lat2', res) || validateParam(req, 'lon2', res)) {
        return;
    }

    let lat1 = Number(req.query.lat1);
    let lon1 = Number(req.query.lon1);
    let lat2 = Number(req.query.lat2);
    let lon2 = Number(req.query.lon2);

    if (validateNumParam(lat1, 'lat1', res) || validateNumParam(lon1, 'lon1', res) || validateNumParam(lat2, 'lat2', res) || validateNumParam(lon2, 'lon2', res)) {
        return;
    }

    let distance = getDistance(
        { latitude: lat1, longitude: lon1 },
        { latitude: lat2, longitude: lon2 }
    );

    res.status(200).json({
        distance: distance,
    });
});

app.listen(port, () => console.log(`GIS service listening on port ${port}`));
