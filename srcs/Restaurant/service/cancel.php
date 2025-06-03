<?php
require_once './lib/Utils.php';
header("Access-Control-Allow-Origin: *");
header("Content-Type: application/json; charset=UTF-8");
header("Access-Control-Allow-Methods: GET");

try {
    if ($_SERVER['REQUEST_METHOD'] !== 'GET') {
        http_response_code(405);
        echo json_encode([
            "status" => "error",
            "message" => "Metodo non consentito. Usa GET"
        ]);
        exit;
    }
    // Ottieni parametri
    $id_ordine = $_GET['id_ordine'] ?? null;
    error_log("\n\nCancel request id_ordine=". $id_ordine ."");
    

    // Imposta codice risposta
    if ($result['status'] === 'error') {
        http_response_code(400);
    } else {
        http_response_code(200);
    }    
    // Restituisci JSON
    echo json_encode($result, JSON_PRETTY_PRINT | JSON_UNESCAPED_UNICODE);
    
} catch (Exception $e) {
    http_response_code(500);
    echo json_encode([
        "status" => "error",
        "message" => "Errore interno del server",
        "error" => $e->getMessage()
    ]);
}
?>