package com.grupo1.sgi_fia;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import androidx.annotation.Nullable;

public class AdminSQLiteOpenHelper extends SQLiteOpenHelper {

    // 🛠️ Mantenemos tu constructor original idéntico para que no te falle ninguna otra Activity
    public AdminSQLiteOpenHelper(@Nullable Context context, @Nullable String name, @Nullable SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // Activación de llaves foráneas obligatoria
        db.execSQL("PRAGMA foreign_keys = ON;");

        // 1. Tabla de Usuarios (Para el Login)
        db.execSQL("CREATE TABLE usuarios (" +
                "id_usuario INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "email TEXT UNIQUE, " +
                "password TEXT)");

        // 2. Tabla de Prestatarios (Estudiantes / Docentes)
        db.execSQL("CREATE TABLE prestatarios (" +
                "carnet TEXT PRIMARY KEY, " +
                "nombre TEXT)");

        // 3. Tabla de Documentos (Intacta, con control de stock/ejemplares)
        db.execSQL("CREATE TABLE documentos (" +
                "id_documento INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "titulo TEXT, " +
                "tipo TEXT, " +
                "isbn TEXT, " +
                "idioma TEXT, " +
                "anio INTEGER, " +
                "ejemplares INTEGER)");

        // 4. Tabla de Hardware (🔥 POTENCIADA: Con los requerimientos nuevos de la FIA)
        db.execSQL("CREATE TABLE hardware (" +
                "id_hardware INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "numero_serie TEXT UNIQUE, " + // UNIQUE para evitar que clonen series
                "marca TEXT, " +
                "modelo TEXT, " +
                "clasificacion TEXT, " +       // Cañón, Laptop, PC, Switch, etc.
                "ubicacion TEXT, " +           // Cubículo, Jefatura, etc.
                "responsable TEXT, " +         // Quién tiene asignado el equipo (Docente/Secretaria)
                "contacto TEXT, " +            // Extensión o correo del responsable
                "estado TEXT, " +              // Disponible, Prestado, Arruinado, Descargado
                "costo REAL, " +               // Manteniendo tu campo original
                "unidades INTEGER, " +         // Manteniendo tu campo original
                "descripcion TEXT, " +
                "fecha_levantamiento TEXT)");

        // 5. Tabla de Préstamos (🔥 MEJORADA: Con tipos de préstamo y horarios FIA)
        db.execSQL("CREATE TABLE prestamos (" +
                "id_prestamo INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "carnet_prestatario TEXT, " +
                "id_documento INTEGER, " +
                "id_hardware INTEGER, " +
                "fecha_prestamo TEXT, " +
                "fecha_limite TEXT, " +
                "tipo_prestamo TEXT, " +        // Express (Actividad), Ciclo Completo, Definitivo
                "materia_horario TEXT, " +     // Estándares FIA (Ej: Lu-Mi 4:50 - 6:30 pm)
                "actividad_especifica TEXT, " + // Ej: Para el foro de Bases de Datos
                "estado TEXT, " +              // 'Activo' o 'Devuelto'
                "FOREIGN KEY(carnet_prestatario) REFERENCES prestatarios(carnet), " +
                "FOREIGN KEY(id_documento) REFERENCES documentos(id_documento), " +
                "FOREIGN KEY(id_hardware) REFERENCES hardware(id_hardware))");

        // 6. Tabla de Devoluciones (Conecta con Préstamos)
        db.execSQL("CREATE TABLE devoluciones (" +
                "id_devolucion INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "id_prestamo INTEGER, " +
                "marcar_devuelto INTEGER, " +
                "fecha_devolucion TEXT, " +
                "FOREIGN KEY(id_prestamo) REFERENCES prestamos(id_prestamo))");

        // 7. 🌟 NUEVA TABLA: Sustituciones (Historial de cambios por daño o renovación)
        db.execSQL("CREATE TABLE sustituciones (" +
                "id_sustitucion INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "id_hardware_viejo INTEGER, " +
                "id_hardware_nuevo INTEGER, " +
                "motivo TEXT, " +              // Renovación, Equipo Arruinado, etc.
                "fecha_sustitucion TEXT, " +
                "FOREIGN KEY(id_hardware_viejo) REFERENCES hardware(id_hardware), " +
                "FOREIGN KEY(id_hardware_nuevo) REFERENCES hardware(id_hardware))");

        // 8. 🌟 NUEVA TABLA: Auditorías (Para el Levantamiento Físico Anual y Faltantes)
        db.execSQL("CREATE TABLE auditorias (" +
                "id_auditoria INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "id_hardware INTEGER, " +
                "anio_periodo TEXT, " +         // Ej: "2026"
                "ubicacion_encontrado TEXT, " + // Dónde se halló en la revisión real
                "estado_confirmado TEXT, " +   // Encontrado / No Encontrado (Faltante)
                "observaciones TEXT, " +
                "FOREIGN KEY(id_hardware) REFERENCES hardware(id_hardware))");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Eliminamos las nuevas primero por dependencias
        db.execSQL("DROP TABLE IF EXISTS auditorias");
        db.execSQL("DROP TABLE IF EXISTS sustituciones");
        db.execSQL("DROP TABLE IF EXISTS devoluciones");
        db.execSQL("DROP TABLE IF EXISTS prestamos");
        db.execSQL("DROP TABLE IF EXISTS hardware");
        db.execSQL("DROP TABLE IF EXISTS documentos");
        db.execSQL("DROP TABLE IF EXISTS prestatarios");
        db.execSQL("DROP TABLE IF EXISTS usuarios");
        onCreate(db);
    }
}
