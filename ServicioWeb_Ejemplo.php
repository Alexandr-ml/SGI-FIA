<?php
/**
 * Ejemplo de Servicio Web (API) para SGI-FIA
 * Requisito: Código fuente de los servicios web realizados
 */

header("Content-Type: application/json");
$method = $_SERVER['REQUEST_METHOD'];

// Configuración de base de datos remota (MySQL)
$host = "localhost";
$dbname = "fia_inventario";
$user = "root";
$pass = "";

try {
    $pdo = new PDO("mysql:host=$host;dbname=$dbname", $user, $pass);
    
    if ($method == 'GET') {
        // Obtener todos los equipos
        $stmt = $pdo->query("SELECT * FROM equipos");
        echo json_encode($stmt->fetchAll(PDO::FETCH_ASSOC));
    } 
    else if ($method == 'POST') {
        // Guardar un nuevo equipo (Sincronización desde Android)
        $data = json_decode(file_get_contents('php://input'), true);
        
        $sql = "INSERT INTO equipos (nombre, clasificacion, estado, unidad_id, numero_serie, marca, modelo, ubicacion) 
                VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        
        $stmt = $pdo->prepare($sql);
        $stmt->execute([
            $data['nombre'],
            $data['clasificacion'],
            $data['estado'],
            $data['unidad_id'],
            $data['numero_serie'],
            $data['marca'],
            $data['modelo'],
            $data['ubicacion']
        ]);
        
        echo json_encode(["status" => "success", "message" => "Equipo sincronizado"]);
    }

} catch (PDOException $e) {
    echo json_encode(["error" => $e->getMessage()]);
}
?>