-- Script de llenado inicial (DML) para SGI-FIA
-- Requisito: Mínimo 15 registros para pruebas de funcionalidad

-- Tabla: equipo_informatico
INSERT INTO equipo_informatico (numero_serie, modelo, marca, ubicacion, costo_unidad, unidades, descripcion, estado_funcional, estado_prestamo, id_usuario) VALUES
('SN-001', 'Latitude 5420', 'Dell', 'Unidad de Ciencias Básicas', 1200.00, 1, 'Laptop de alto rendimiento', 'Nuevo', 'Disponible', 1),
('SN-002', 'ThinkPad X1', 'Lenovo', 'Cubículo 12 - Escuela de Ing. Industrial', 1500.00, 1, 'Laptop ejecutiva', 'Nuevo', 'Disponible', 1),
('SN-003', 'Spectra Q891', 'Proyector', 'Edificio B - Nivel 1 - FIA', 800.00, 1, 'Proyector multimedia', 'Bueno', 'Disponible', 1),
('SN-004', 'MacBook Pro', 'Apple', 'Laboratorio de Informática', 2500.00, 1, 'Desarrollo de software', 'Nuevo', 'En Préstamo', 1),
('SN-005', 'HP LaserJet', 'HP', 'Secretaría de Facultad', 300.00, 1, 'Impresora láser', 'Bueno', 'Disponible', 1),
('SN-006', 'Monitor 24"', 'Samsung', 'Centro de Cómputo 1', 200.00, 5, 'Monitores para laboratorios', 'Nuevo', 'Disponible', 1),
('SN-007', 'iPad Air', 'Apple', 'Biblioteca Central', 600.00, 3, 'Tabletas para consulta', 'Nuevo', 'Disponible', 1),
('SN-008', 'Server PowerEdge', 'Dell', 'Data Center FIA', 5000.00, 1, 'Servidor de base de datos', 'Excelente', 'Disponible', 1),
('SN-009', 'Switch 24 Ports', 'Cisco', 'Data Center FIA', 1200.00, 2, 'Equipamiento de red', 'Bueno', 'Disponible', 1),
('SN-010', 'Scanner ScanJet', 'HP', 'Archivo Académico', 400.00, 1, 'Escáner de documentos', 'Regular', 'Disponible', 1),
('SN-011', 'UPS 1500VA', 'APC', 'Data Center FIA', 350.00, 4, 'Respaldo de energía', 'Bueno', 'Disponible', 1),
('SN-012', 'Mouse Wireless', 'Logitech', 'Bodega Activos', 25.00, 20, 'Periféricos de repuesto', 'Nuevo', 'Disponible', 1),
('SN-013', 'Keyboard Mech', 'Razer', 'Laboratorio de Videojuegos', 150.00, 10, 'Teclados mecánicos', 'Nuevo', 'Disponible', 1),
('SN-014', 'Webcam C920', 'Logitech', 'Sala de Conferencias', 100.00, 2, 'Cámara para streaming', 'Bueno', 'Disponible', 1),
('SN-015', 'External HDD 2TB', 'Seagate', 'Unidad de Investigación', 90.00, 5, 'Almacenamiento externo', 'Nuevo', 'Disponible', 1);

-- Tabla: prestatario
INSERT INTO prestatario (carnet, nombre, apellido, correo, telefono) VALUES
('VV19033', 'Brayan', 'Villalobos', 'vv19033@ues.edu.sv', '7700-0001'),
('AA20001', 'Juan', 'Pérez', 'aa20001@ues.edu.sv', '7700-0002'),
('BB20002', 'María', 'López', 'bb20002@ues.edu.sv', '7700-0003'),
('CC20003', 'Carlos', 'Gómez', 'cc20003@ues.edu.sv', '7700-0004'),
('DD20004', 'Ana', 'Martínez', 'dd20004@ues.edu.sv', '7700-0005');
