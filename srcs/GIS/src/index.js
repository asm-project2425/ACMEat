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

function closest(req, locations) {
    const lat = req.query.lat;
    const lon = req.query.lon;

    if (!lat || !lon) { // No closest parameters
        return locations[0];
    }

    let closest_loc = locations[0];
    let closest_dist = getDistance(
        { latitude: lat, longitude: lon },
        { latitude: closest_loc.lat, longitude: closest_loc.lon }
    );

    for (let i = 1; i < locations.length; i++) {
        const dist = getDistance(
            { latitude: lat, longitude: lon },
            { latitude: locations[i].lat, longitude: locations[i].lon }
        );

        if (dist < closest_dist) {
            closest_loc = locations[i];
            closest_dist = dist;
        }
    }

    return closest_loc;
}

async function locate(query, paramName, cache, req, res) {
    const cached = cache.get(query);
    if (cached) {
        res.status(200).json(closest(req, cached));
        return;
    }

    // Use multiple nominatim instances and rate limiters given the limit of 1 request per second
    const url = urls[current_url];
    const limiter = limiters[current_url];
    current_url = (current_url + 1) % urls.length;
    const response = await limiter.schedule(() => fetch(`${url}/search?format=json&${paramName}=${query}`, {
        headers: {
            'User-Agent': 'ACMEat',
        }
    }));

    if (!response.ok) {
        res.status(500).send('Internal error');
        return;
    }

    const data = await response.json();

    if (!data || data.length == null || data.length === 0) {
        res.status(500).send('Internal error');
        return;
    }

    const locations = [];
    for (const loc of data) {
        if (!loc.lat || !loc.lon) {
            res.status(500).send('Internal error');
            return;
        }

        let lat = Number(loc.lat);
        let lon = Number(loc.lon);

        if (!validateNum(lat) || !validateNum(lon)) {
            res.status(500).send('Internal error');
            return;
        }

        locations.push({
            lat,
            lon
        });
    }

    cache.set(query, locations);

    res.status(200).json(closest(req, locations));
}

app.get('/api/v1/locate', async function (req, res) {
    if (validateParam(req, 'query', res)) {
        return;
    }

    await locate(req.query.query, 'q', queryCache, req, res);
});

app.get('/api/v1/locateCity', async function (req, res) {
    if (validateParam(req, 'city', res)) {
        return;
    }

    await locate(req.query.city, 'city', cityCache, req, res);
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
