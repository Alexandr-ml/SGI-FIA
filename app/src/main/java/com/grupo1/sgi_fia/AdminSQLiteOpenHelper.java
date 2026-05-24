package com.grupo1.sgi_fia;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

public class AdminSQLiteOpenHelper extends SQLiteOpenHelper {

    public static final String NOMBRE_BD = "SGIFIA.db";
    public static final int VERSION_BD = 7;

    public AdminSQLiteOpenHelper(@Nullable Context context, @Nullable String name,
                                 @Nullable SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    @Override
    public void onConfigure(SQLiteDatabase db) {
        super.onConfigure(db);
        db.setForeignKeyConstraintsEnabled(true);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE IF NOT EXISTS usuarios (" +
                "id_usuario INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "email TEXT UNIQUE, " +
                "password TEXT)");

        db.execSQL("CREATE TABLE IF NOT EXISTS prestatarios (" +
                "carnet TEXT PRIMARY KEY, " +
                "nombre TEXT)");

        db.execSQL("CREATE TABLE IF NOT EXISTS documentos (" +
                "id_documento INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "titulo TEXT, " +
                "tipo TEXT, " +
                "isbn TEXT, " +
                "idioma TEXT, " +
                "anio INTEGER, " +
                "ejemplares INTEGER)");

        db.execSQL("CREATE TABLE IF NOT EXISTS prestamos (" +
                "id_prestamo INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "carnet_prestatario TEXT, " +
                "id_documento INTEGER, " +
                "fecha_prestamo TEXT, " +
                "fecha_limite TEXT, " +
                "estado TEXT, " +
                "FOREIGN KEY(carnet_prestatario) REFERENCES prestatarios(carnet), " +
                "FOREIGN KEY(id_documento) REFERENCES documentos(id_documento))");

        db.execSQL("CREATE TABLE IF NOT EXISTS devoluciones (" +
                "id_devolucion INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "id_prestamo INTEGER, " +
                "marcar_devuelto INTEGER, " +
                "fecha_devolucion TEXT, " +
                "FOREIGN KEY(id_prestamo) REFERENCES prestamos(id_prestamo))");

        crearTablaEquiposInformaticos(db);
        insertarEquiposIniciales(db);
        crearTablaLevantamientosFisicos(db);

        db.execSQL("CREATE TABLE IF NOT EXISTS prestamos_equipo_horas (" +
                "id_prestamo INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "nombre_prestatario TEXT NOT NULL, " +
                "carnet_prestatario TEXT NOT NULL, " +
                "equipo TEXT NOT NULL, " +
                "fecha_prestamo TEXT NOT NULL, " +
                "hora_inicio TEXT NOT NULL, " +
                "hora_fin TEXT NOT NULL, " +
                "actividad TEXT NOT NULL, " +
                "observaciones TEXT)");

        db.execSQL("CREATE TABLE IF NOT EXISTS prestamos_tesis (" +
                "id_prestamo INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "nombre_prestatario TEXT NOT NULL, " +
                "carnet_prestatario TEXT NOT NULL, " +
                "codigo_tesis TEXT NOT NULL, " +
                "titulo_tesis TEXT NOT NULL, " +
                "fecha_prestamo TEXT NOT NULL, " +
                "fecha_devolucion TEXT NOT NULL, " +
                "estado_prestamo TEXT NOT NULL, " +
                "observaciones TEXT)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (oldVersion < 5) {
            db.execSQL("DROP TABLE IF EXISTS prestamos_tesis");
            db.execSQL("DROP TABLE IF EXISTS prestamos_equipo_horas");
            db.execSQL("DROP TABLE IF EXISTS equipos_informaticos");
            db.execSQL("DROP TABLE IF EXISTS devoluciones");
            db.execSQL("DROP TABLE IF EXISTS prestamos");
            db.execSQL("DROP TABLE IF EXISTS documentos");
            db.execSQL("DROP TABLE IF EXISTS prestatarios");
            db.execSQL("DROP TABLE IF EXISTS usuarios");
            onCreate(db);
            return;
        }

        if (oldVersion < 6) {
            crearTablaEquiposInformaticos(db);
            insertarEquiposIniciales(db);
        }

        if (oldVersion < 7) {
            actualizarTablaEquiposInformaticos(db);
            crearTablaLevantamientosFisicos(db);
            insertarEquiposIniciales(db);
        }
    }

    private void crearTablaEquiposInformaticos(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE IF NOT EXISTS equipos_informaticos (" +
                "id_equipo INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "nombre TEXT NOT NULL, " +
                "numero_serie TEXT, " +
                "marca TEXT, " +
                "modelo TEXT NOT NULL, " +
                "ubicacion TEXT, " +
                "costo_unidad REAL DEFAULT 0, " +
                "unidades INTEGER DEFAULT 1, " +
                "descripcion TEXT, " +
                "estado_funcional TEXT NOT NULL, " +
                "estado_prestamo TEXT NOT NULL, " +
                "fecha_ultimo_levantamiento TEXT)");
    }

    private void insertarEquiposIniciales(SQLiteDatabase db) {
        db.execSQL("INSERT OR IGNORE INTO equipos_informaticos " +
                "(id_equipo, nombre, numero_serie, marca, modelo, ubicacion, costo_unidad, unidades, descripcion, estado_funcional, estado_prestamo) VALUES " +
                "(1, 'Monitor Dell', 'MON-001', 'Dell', 'S2725HSM', 'Centro de Computo', 0, 1, 'Monitor para estaciones de trabajo', 'Activo', 'Disponible')");
        db.execSQL("INSERT OR IGNORE INTO equipos_informaticos " +
                "(id_equipo, nombre, numero_serie, marca, modelo, ubicacion, costo_unidad, unidades, descripcion, estado_funcional, estado_prestamo) VALUES " +
                "(2, 'Impresora HP', 'IMP-001', 'HP', 'Smart Tank 580', 'Secretaria de Facultad', 0, 1, 'Impresora multifuncional', 'Activo', 'Disponible')");
        db.execSQL("INSERT OR IGNORE INTO equipos_informaticos " +
                "(id_equipo, nombre, numero_serie, marca, modelo, ubicacion, costo_unidad, unidades, descripcion, estado_funcional, estado_prestamo) VALUES " +
                "(3, 'Laptop Dell x985', 'LAP-985', 'Dell', 'x985', 'Unidad de Ciencias Basicas', 0, 1, 'Laptop asignada a unidad academica', 'Activo', 'Disponible')");
        db.execSQL("INSERT OR IGNORE INTO equipos_informaticos " +
                "(id_equipo, nombre, numero_serie, marca, modelo, ubicacion, costo_unidad, unidades, descripcion, estado_funcional, estado_prestamo) VALUES " +
                "(4, 'Proyector Spectra Q891', 'PRO-891', 'Spectra', 'Q891', 'Edificio B - Nivel 1 - FIA', 0, 1, 'Proyector para aulas', 'Activo', 'Disponible')");
        completarEquipoInicial(db, 1, "Monitor Dell", "MON-001", "Dell", "S2725HSM", "Centro de Computo", "Monitor para estaciones de trabajo");
        completarEquipoInicial(db, 2, "Impresora HP", "IMP-001", "HP", "Smart Tank 580", "Secretaria de Facultad", "Impresora multifuncional");
        completarEquipoInicial(db, 3, "Laptop Dell x985", "LAP-985", "Dell", "x985", "Unidad de Ciencias Basicas", "Laptop asignada a unidad academica");
        completarEquipoInicial(db, 4, "Proyector Spectra Q891", "PRO-891", "Spectra", "Q891", "Edificio B - Nivel 1 - FIA", "Proyector para aulas");
    }

    private void completarEquipoInicial(SQLiteDatabase db, int idEquipo, String nombre, String numeroSerie,
                                        String marca, String modelo, String ubicacion, String descripcion) {
        db.execSQL("UPDATE equipos_informaticos SET " +
                        "nombre = ?, numero_serie = ?, marca = ?, modelo = ?, ubicacion = ?, " +
                        "costo_unidad = 0, unidades = 1, descripcion = ? " +
                        "WHERE id_equipo = ? AND (numero_serie IS NULL OR numero_serie = '')",
                new Object[]{nombre, numeroSerie, marca, modelo, ubicacion, descripcion, idEquipo});
    }

    private void crearTablaLevantamientosFisicos(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE IF NOT EXISTS levantamientos_fisicos (" +
                "id_levantamiento INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "fecha_levantamiento TEXT NOT NULL, " +
                "numero_serie TEXT, " +
                "observaciones TEXT)");
    }

    private void actualizarTablaEquiposInformaticos(SQLiteDatabase db) {
        agregarColumnaSiNoExiste(db, "equipos_informaticos", "numero_serie", "TEXT");
        agregarColumnaSiNoExiste(db, "equipos_informaticos", "marca", "TEXT");
        agregarColumnaSiNoExiste(db, "equipos_informaticos", "ubicacion", "TEXT");
        agregarColumnaSiNoExiste(db, "equipos_informaticos", "costo_unidad", "REAL DEFAULT 0");
        agregarColumnaSiNoExiste(db, "equipos_informaticos", "unidades", "INTEGER DEFAULT 1");
        agregarColumnaSiNoExiste(db, "equipos_informaticos", "descripcion", "TEXT");
        agregarColumnaSiNoExiste(db, "equipos_informaticos", "fecha_ultimo_levantamiento", "TEXT");
    }

    private void agregarColumnaSiNoExiste(SQLiteDatabase db, String tabla, String columna, String definicion) {
        try {
            db.execSQL("ALTER TABLE " + tabla + " ADD COLUMN " + columna + " " + definicion);
        } catch (Exception ignored) {
            // La columna ya existe en bases creadas con versiones recientes.
        }
    }
}
