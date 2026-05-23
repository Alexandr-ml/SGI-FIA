package com.grupo1.sgi_fia;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

public class AdminSQLiteOpenHelper extends SQLiteOpenHelper {

    public static final String NOMBRE_BD = "SGIFIA.db";
    public static final int VERSION_BD = 4;

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

        db.execSQL("CREATE TABLE IF NOT EXISTS hardware (" +
                "id_hardware INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "numero_serie TEXT UNIQUE, " +
                "marca TEXT, " +
                "modelo TEXT, " +
                "clasificacion TEXT, " +
                "ubicacion TEXT, " +
                "responsable TEXT, " +
                "contacto TEXT, " +
                "estado TEXT, " +
                "costo REAL, " +
                "unidades INTEGER, " +
                "descripcion TEXT, " +
                "fecha_levantamiento TEXT)");

        db.execSQL("CREATE TABLE IF NOT EXISTS prestamos (" +
                "id_prestamo INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "carnet_prestatario TEXT, " +
                "id_documento INTEGER, " +
                "id_hardware INTEGER, " +
                "fecha_prestamo TEXT, " +
                "fecha_limite TEXT, " +
                "tipo_prestamo TEXT, " +
                "materia_horario TEXT, " +
                "actividad_especifica TEXT, " +
                "estado TEXT, " +
                "FOREIGN KEY(carnet_prestatario) REFERENCES prestatarios(carnet), " +
                "FOREIGN KEY(id_documento) REFERENCES documentos(id_documento), " +
                "FOREIGN KEY(id_hardware) REFERENCES hardware(id_hardware))");

        db.execSQL("CREATE TABLE IF NOT EXISTS devoluciones (" +
                "id_devolucion INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "id_prestamo INTEGER, " +
                "marcar_devuelto INTEGER, " +
                "fecha_devolucion TEXT, " +
                "FOREIGN KEY(id_prestamo) REFERENCES prestamos(id_prestamo))");

        db.execSQL("CREATE TABLE IF NOT EXISTS sustituciones (" +
                "id_sustitucion INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "id_hardware_viejo INTEGER, " +
                "id_hardware_nuevo INTEGER, " +
                "motivo TEXT, " +
                "fecha_sustitucion TEXT, " +
                "FOREIGN KEY(id_hardware_viejo) REFERENCES hardware(id_hardware), " +
                "FOREIGN KEY(id_hardware_nuevo) REFERENCES hardware(id_hardware))");

        db.execSQL("CREATE TABLE IF NOT EXISTS auditorias (" +
                "id_auditoria INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "id_hardware INTEGER, " +
                "anio_periodo TEXT, " +
                "ubicacion_encontrado TEXT, " +
                "estado_confirmado TEXT, " +
                "observaciones TEXT, " +
                "FOREIGN KEY(id_hardware) REFERENCES hardware(id_hardware))");

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
        db.execSQL("DROP TABLE IF EXISTS prestamos_tesis");
        db.execSQL("DROP TABLE IF EXISTS prestamos_equipo_horas");
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
