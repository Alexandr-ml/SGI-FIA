package com.grupo1.sgi_fia;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import androidx.annotation.Nullable;

public class AdminSQLiteOpenHelper extends SQLiteOpenHelper {

    // Constructor que necesita Android para inicializar la base de datos
    public AdminSQLiteOpenHelper(@Nullable Context context, @Nullable String name, @Nullable SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    // Aquí se crean tus dos tablas en SQLite
    @Override
    public void onCreate(SQLiteDatabase db) {
        // Tabla para tu pantalla de Registro de Documentos
        db.execSQL("CREATE TABLE documentos (" +
                "id_documento INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "nombre_documento TEXT, " +
                "tipo_documento TEXT, " +
                "fecha_registro TEXT)");

        // Tabla para tu pantalla de Devolución de Libros
        db.execSQL("CREATE TABLE devoluciones (" +
                "id_prestamo TEXT PRIMARY KEY, " +
                "marcar_devuelto INTEGER, " +
                "fecha_devolucion TEXT)");
    }

    // Por si editas las tablas en el futuro
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS documentos");
        db.execSQL("DROP TABLE IF EXISTS devoluciones");
        onCreate(db);
    }
}