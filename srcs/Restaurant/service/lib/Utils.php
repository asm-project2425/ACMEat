<?php
/**
 * Utils - Helper per funzioni
 */
// Percorso del file per salvare gli ordini
define('ORDERS_FILE', __DIR__ . '/data/orders.json');
define('ORDERS_DIR', __DIR__ . '/data');
define("MAX_ORDERS", 3);

if (!file_exists(ORDERS_DIR)) {
    mkdir(ORDERS_DIR, 0777, true);
}

class Utils {
    public static function checkAvailability(string $orario,int $id_ordine) {
        // Validazione parametri
        if (empty($orario) || empty($id_ordine)) {
            return [
                "status" => "error",
                "message" => "Parametri mancanti: orario e id_ordine sono obbligatori"
            ];
        }
        // Validazione formato orario
        if (!preg_match("/^([0-1]?[0-9]|2[0-3]):[0-5][0-9]$/", $orario)) {
            return [
                "status" => "error",
                "message" => "Formato orario non valido. Usa HH:MM"
            ];
        }

        $orders = Utils::readOrdersFromFile();
        $order = Utils::getOrderByIdAndTime($orders, $id_ordine, $orario);
        if($order){
            error_log("Ordine gia inserito");
            return ["status" => "order alredy inserted", "avaible" => true, "order" =>$order];
        }

        

        // Conta ordini per l'orario
        $numero_ordini = Utils::getOrdersCountByTime($orders, $orario);
        $disponibile = $numero_ordini < MAX_ORDERS;
        
        if($disponibile){
            $order = [
                "id" => $id_ordine,
                "orario" => $orario,
                "data" => date('Y-m-d')
            ];
            $orders[] = $order;
            Utils::saveOrdersToFile($orders);
            $numero_ordini ++;
        }

        return [
            "status" => "success",
            "avaible" => $disponibile,
            "order" => $order,
            "data" => [
                "ordini_attuali" => $numero_ordini,
                "ordini_massimi" => MAX_ORDERS,
                "posti_disponibili" => $disponibile ? (MAX_ORDERS - $numero_ordini) : 0,
                "timestamp" => date('Y-m-d H:i:s')
            ]
        ];
    }

    public static function readOrdersFromFile() : array {
        if (!file_exists(ORDERS_FILE)) {
            error_log("File orders non trovato, creando file nuovo..");
            // Se il file non esiste, crea un array vuoto
            file_put_contents(ORDERS_FILE, "[]", LOCK_EX);
        }
        
        $content = file_get_contents(ORDERS_FILE);
        return json_decode($content, true) ?? [];
    }

    public static function getOrderByIdAndTime(array $orders ,int $id_ordine, string $orario){
        $order = null;

        foreach($orders as $o){
            if($o["id"] === $id_ordine && $o["orario"] === $orario){
                $order = $o;
            }
        }
        return $order;
    }

    public static function getOrdersCountByTime(array $orders, string $orario) : int {
        $today = date('Y-m-d');
        $count = 0;
        
        // Conta solo gli ordini di oggi per l'orario specificato
        foreach ($orders as $order) {
            if ($order['data'] === $today && 
                $order['orario'] === $orario) {
                $count++;
            }
        }
        
        return $count;
    }

    public static function saveOrdersToFile($orders) {
        error_log("Saving file");
        //error_log("". print_r($orders, true));
        $json = json_encode($orders, JSON_PRETTY_PRINT | JSON_UNESCAPED_UNICODE);
        file_put_contents(ORDERS_FILE, $json, LOCK_EX);
    }

    public static function removeOrder($id_ordine):bool{
        $orders = Utils::readOrdersFromFile();
        $deleted =false;

        $new_orders = [];

        foreach($orders as $o){
            if($o["id"] == $id_ordine){
                $deleted=true;
            }else{
                $new_orders[] = $o;
            }

        }

        Utils::saveOrdersToFile($new_orders);

        return $deleted;
    }


}