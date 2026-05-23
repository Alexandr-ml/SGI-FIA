package com.grupo1.sgi_fia;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

public class AdminSQLiteOpenHelper extends SQLiteOpenHelper {

    public static final String NOMBRE_BD = "SGIFIA.db";
    public static final int VERSION_BD = 2;

    public AdminSQLiteOpenHelper(@Nullable Context context, @Nullable String name,
                                 @Nullable SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
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
        db.execSQL("DROP TABLE IF EXISTS prestamos_equipo_horas");
        db.execSQL("DROP TABLE IF EXISTS prestamos_tesis");
        onCreate(db);
    }
}
