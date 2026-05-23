package com.grupo1.sgi_fia;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

public class AdminSQLiteOpenHelper extends SQLiteOpenHelper {

    public static final String NOMBRE_BD = "SGIFIA.db";
    public static final int VERSION_BD = 6;

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
    }

    private void crearTablaEquiposInformaticos(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE IF NOT EXISTS equipos_informaticos (" +
                "id_equipo INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "nombre TEXT NOT NULL, " +
                "modelo TEXT NOT NULL, " +
                "estado_funcional TEXT NOT NULL, " +
                "estado_prestamo TEXT NOT NULL)");
    }

    private void insertarEquiposIniciales(SQLiteDatabase db) {
        db.execSQL("INSERT OR IGNORE INTO equipos_informaticos " +
                "(id_equipo, nombre, modelo, estado_funcional, estado_prestamo) VALUES " +
                "(1, 'Monitor Dell', 'S2725HSM', 'Activo', 'Disponible')");
        db.execSQL("INSERT OR IGNORE INTO equipos_informaticos " +
                "(id_equipo, nombre, modelo, estado_funcional, estado_prestamo) VALUES " +
                "(2, 'Impresora HP', 'Smart Tank 580', 'Activo', 'Disponible')");
    }
}
