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
            ✅ PHP sta funzionando correttamente!
        </div>

        <div class="info-box">
            <h2>📊 Informazioni Server</h2>
            <table>
                <tr>
                    <th>Parametro</th>
                    <th>Valore</th>
                </tr>
                <tr>
                    <td>Versione PHP</td>
                    <td><?php echo phpversion(); ?></td>
                </tr>
                <tr>
                    <td>Sistema Operativo</td>
                    <td><?php echo php_uname(); ?></td>
                </tr>
                <tr>
                    <td>Server Software</td>
                    <td><?php echo $_SERVER['SERVER_SOFTWARE'] ?? 'N/D'; ?></td>
                </tr>
                <tr>
                    <td>Data/Ora Server</td>
                    <td><?php echo date('d/m/Y H:i:s'); ?></td>
                </tr>
                <tr>
                    <td>Timezone</td>
                    <td><?php echo date_default_timezone_get(); ?></td>
                </tr>
            </table>
        </div>

        <div class="info-box">
            <h2>🔧 Test Form PHP</h2>
            <form method="POST" action="">
                <div class="form-group">
                    <label for="nome">Inserisci il tuo nome:</label>
                    <input type="text" id="nome" name="nome" placeholder="Il tuo nome">
                    <input type="submit" value="Invia">
                </div>
            </form>
            
            <?php
            if ($_SERVER["REQUEST_METHOD"] == "POST" && !empty($_POST["nome"])) {
                $nome = htmlspecialchars($_POST["nome"]);
                echo "<div class='success'>👋 Ciao $nome! Il form PHP funziona perfettamente!</div>";
            }
            ?>
        </div>

        <div class="info-box">
            <h2>📦 Estensioni PHP Caricate</h2>
            <p>
                <?php 
                $extensions = get_loaded_extensions();
                echo "<strong>" . count($extensions) . " estensioni caricate:</strong><br>";
                echo implode(", ", array_slice($extensions, 0, 10)) . "...";
                ?>
            </p>
        </div>

        <div class="info-box">
            <h2>🧪 Test Connessione Database</h2>
            <?php
            // Test opzionale per MySQL/MariaDB
            try {
                if (extension_loaded('mysqli')) {
                    echo "<p>✅ Estensione MySQLi disponibile</p>";
                }
                if (extension_loaded('pdo_mysql')) {
                    echo "<p>✅ PDO MySQL disponibile</p>";
                }
            } catch (Exception $e) {
                echo "<p>⚠️ Errore: " . $e->getMessage() . "</p>";
            }
            ?>
        </div>

        <div style="text-align: center; margin-top: 30px; color: #666;">
            <p>
                <a href="/phpinfo.php" style="color: #2196F3;">Visualizza phpinfo()</a> | 
                <a href="https://github.com" style="color: #2196F3;">GitHub</a>
            </p>
        </div>
    </div>
</body>
</html>