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
        <h1>Frontend banca</h1>
        
        <div class="success">
            <?php
                $wsdlFile = './BankService.wsdl';
                $options = [
                        'trace' => 1, // Abilita il tracciamento per il debug
                        'exceptions' => true, // Lancia eccezioni SoapFault in caso di errore
                        'cache_wsdl' => WSDL_CACHE_NONE, // Disabilita il caching del WSDL durante lo sviluppo
                        'features'   => SOAP_SINGLE_ELEMENT_ARRAYS
                ];
                if(!file_exists($wsdlFile)){
                    echo "file wsdl not found <br>";
                }

                if($_SERVER["REQUEST_METHOD"] == "POST") {
                    $username = isset($_POST['username']) ? trim($_POST['username']) : '';
                    $password = isset($_POST['password']) ? trim($_POST['password']) : '';
                    $paymentId = isset($_POST['paymentId']) ? trim($_POST['paymentId']) : '';
                
                    try{
                        $client = new SoapClient($wsdlFile, $options);
                        $params = [
                            'username' => $username,
                            'password' => $password
                        ];
                        $response = $client->login($params);
                        if(!isset($response->success) || !isset($response->sessionId)){
                            echo '❌ Errore durante il login <br>';
                            error_log('❌ Errore durante il login');
                            print_r($response); 
                        }else{
                            $sessionId = $response->sessionId;
                            echo '✅ Login avvenuto con successo, sessionId: '.$sessionId.'<br>';
                            $response = $client->completePayment(['paymentId' => $paymentId, 'sessionId'=> $sessionId]);

                            if(!isset($response->token)){
                                echo '❌ Errore durante il pagamento <br>';
                                print_r($response);
                            }else{
                                $token = $response->token;
                                echo '✅ pagamento avvenuto con successo, token: '.$token.'<br>';
                                $url = 'host.docker.internal:4321/paymentSuccess?token='.$token;
                                echo '<a href="'.$url.'">Redirect manuale</a>';
                                echo '<script type="text/javascript">';
                                echo 'window.location.href = "'.$url.'";';
                                echo '</script>';
                                die("Redirect to ACME...");
                            }

                        }
                        
                    } catch (Exception $e) {
                        echo '❌ errore WSDL <br>';
                        print_r($e); 
                    }
                }else{
                    try{
                        $client = new SoapClient($wsdlFile, $options);
                        $params = [
                            'username' => 'demo',
                            'password' => 'demo'
                        ];
                        $response = $client->login($params);
                        echo '✅ WSDL funziona correttamente <br>';
                        //print_r($response); 
                    } catch (Exception $e) {
                        echo '❌ errore WSDL <br>';
                        print_r($e); 
                    }
                }
            ?>

            
        </div>
        <div class = "info-box">
            <form action="" method="POST">
                <div class="form-group">
                    <label for="username">Username</label>
                    <input type="text" id="username" name="username" required>
                </div>
                <div class="form-group">
                    <label for="password">Password</label>
                    <input type="password" id="password" name="password" required>
                </div>
                <?php
                    echo '<input type="text" id="paymentId" name="paymentId" value="'.$_GET["paymentId"].'" hidden>';
                ?>
                <button type="submit">Paga</button>
            </form>
        </div>






    </div>
</body>
</html>