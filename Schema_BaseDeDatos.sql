-- Scripts de Creación de Base de Datos (SQLite)
-- Proyecto: SGI-FIA (Sistema de Gestión de Inventario)

-- Tabla: usuarios
CREATE TABLE IF NOT EXISTS usuarios (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    nombre TEXT,
    email TEXT,
    tipo_usuario TEXT
);

-- Tabla: unidades
CREATE TABLE IF NOT EXISTS unidades (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    nombre TEXT,
    descripcion TEXT
);

-- Tabla: equipos
CREATE TABLE IF NOT EXISTS equipos (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    nombre TEXT,
    clasificacion TEXT,
    estado TEXT,
    unidad_id INTEGER,
    numero_serie TEXT,
    marca TEXT,
    modelo TEXT,
    ubicacion TEXT,
    costo_unidad REAL,
    unidades INTEGER,
    descripcion TEXT,
    fecha_ultimo_levantamiento TEXT
);

-- Tabla: inventario
CREATE TABLE IF NOT EXISTS inventario (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    fecha TEXT,
    descripcion TEXT
);

-- Tabla: inventario_detalle
CREATE TABLE IF NOT EXISTS inventario_detalle (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    inventario_id INTEGER,
    equipo_id INTEGER,
    cantidad_sistema INTEGER,
    cantidad_fisica INTEGER,
    diferencia INTEGER
);

-- Tabla: prestatario
CREATE TABLE IF NOT EXISTS prestatario (
    id_prestatario INTEGER PRIMARY KEY AUTOINCREMENT,
    carnet TEXT,
    nombre TEXT,
    apellido TEXT,
    correo TEXT,
    telefono TEXT
);
