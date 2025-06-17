<!DOCTYPE html>
<html lang="it">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Test PHP - Docker Nginx</title>
    <style>
        body {
            font-family: Arial, sans-serif;
            max-width: 800px;
            margin: 50px auto;
            padding: 20px;
            background-color: #f5f5f5;
        }
        .container {
            background-color: white;
            padding: 30px;
            border-radius: 10px;
            box-shadow: 0 2px 10px rgba(0,0,0,0.1);
        }
        h1 {
            color: #333;
            text-align: center;
        }
        .info-box {
            background-color: #e3f2fd;
            padding: 15px;
            border-radius: 5px;
            margin: 20px 0;
        }
        .success {
            color: #2e7d32;
            background-color: #e8f5e9;
            padding: 10px;
            border-radius: 5px;
            margin: 10px 0;
        }
        table {
            width: 100%;
            border-collapse: collapse;
            margin: 20px 0;
        }
        th, td {
            padding: 10px;
            text-align: left;
            border-bottom: 1px solid #ddd;
        }
        th {
            background-color: #f0f0f0;
            font-weight: bold;
        }
        .form-group {
            margin: 15px 0;
        }
        input[type="text"], input[type="submit"] {
            padding: 8px 15px;
            margin: 5px;
            border: 1px solid #ddd;
            border-radius: 4px;
        }
        input[type="submit"] {
            background-color: #2196F3;
            color: white;
            cursor: pointer;
        }
        input[type="submit"]:hover {
            background-color: #1976D2;
        }
    </style>
</head>
<body>
    <div class="container">
        <h1>🚀 PHP su Docker con Nginx</h1>
        
        <div class="success">
            <?php
                $wsdlUrl = 'http://localhost:8000?wsdl'; 
                $options = [
                    'trace' => 1, // Abilita il tracciamento per il debug
                    'exceptions' => true, // Lancia eccezioni SoapFault in caso di errore
                    'cache_wsdl' => WSDL_CACHE_NONE, // Disabilita il caching del WSDL durante lo sviluppo
                ];
                try{
                    $client = new SoapClient('./BankService.wsdl', $options);
                    $params = [
                        'username' => 'demo',
                        'password' => 'demo'
                    ];
                    $response = $client->login($params);
                    echo '✅ WSDL funziona correttamente';
                    print_r($response); 
                } catch (Exception $e) {
                    echo '❌ errore WSDL ';
                    print_r($e); 
                }
            ?>

            
        </div>






    </div>
</body>
</html>