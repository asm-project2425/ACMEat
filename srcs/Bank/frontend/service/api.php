<?php

if (!isset($_GET['orderId']) || empty(trim($_GET['orderId']))) {
    // Se manca, restituiamo un errore 400 Bad Request.
    http_response_code(400);
    header('Content-Type: application/json');
    echo json_encode(['error' => 'Il parametro orderId è obbligatorio.']);
    exit; // Terminiamo l'esecuzione.
}

$orderId = $_GET['orderId'];

$targetUrl = 'http://acmeat:8080/api/v1/orders/' . urlencode($orderId) . '/status';


// --- 3. Inizializzazione e configurazione di cURL ---
$ch = curl_init();

// Imposta l'URL della richiesta
curl_setopt($ch, CURLOPT_URL, $targetUrl);

// Imposta il metodo della richiesta a GET (è il default, ma esplicitarlo è una buona pratica)
curl_setopt($ch, CURLOPT_HTTPGET, true);

// Chiedi a cURL di restituire il corpo della risposta come stringa invece di stamparlo direttamente
curl_setopt($ch, CURLOPT_RETURNTRANSFER, true);

// Includi gli header della risposta nell'output di curl_exec() per poterli inoltrare.
// Questo è fondamentale per un proxy.
curl_setopt($ch, CURLOPT_HEADER, true);

// Imposta un timeout per la connessione per evitare attese infinite
curl_setopt($ch, CURLOPT_CONNECTTIMEOUT, 5); // 5 secondi
curl_setopt($ch, CURLOPT_TIMEOUT, 10);      // 10 secondi in totale


// --- 4. Esecuzione della richiesta e gestione degli errori ---
$response = curl_exec($ch);

// Controlla se ci sono stati errori di cURL (es. l'host non è raggiungibile)
if (curl_errno($ch)) {
    // Se c'è un errore di rete, restituiamo un errore 502 Bad Gateway.
    http_response_code(502);
    header('Content-Type: application/json');
    echo json_encode(['error' => 'Impossibile contattare il servizio ordini.', 'details' => curl_error($ch)]);
    curl_close($ch);
    exit;
}

// --- 5. Elaborazione e inoltro della risposta ---

// Otteniamo informazioni sulla richiesta, come il codice di stato HTTP e la dimensione degli header
$httpCode = curl_getinfo($ch, CURLINFO_HTTP_CODE);
$headerSize = curl_getinfo($ch, CURLINFO_HEADER_SIZE);

// Chiudiamo la connessione cURL
curl_close($ch);

// Separiamo gli header dal corpo della risposta
$responseHeaders = substr($response, 0, $headerSize);
$responseBody = substr($response, $headerSize);

// Impostiamo il codice di stato HTTP della nostra risposta uguale a quello della risposta originale
http_response_code($httpCode);

// Inoltriamo gli header ricevuti dal servizio di destinazione al client originale.
// Filtriamo alcuni header che non dovrebbero essere inoltrati.
$headers = explode("\r\n", $responseHeaders);
foreach ($headers as $header) {
    // Non inoltrare gli header di 'Transfer-Encoding' perché PHP gestisce la risposta da solo.
    // Puoi aggiungere altri header da escludere qui se necessario.
    if (!empty($header) && strpos(strtolower($header), 'transfer-encoding:') === false) {
        header($header, true);
    }
}

// Infine, stampiamo il corpo della risposta.
echo $responseBody;

?>