package com.grupo1.sgi_fia.controller;

import android.app.DatePickerDialog;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.grupo1.sgi_fia.AdminSQLiteOpenHelper;
import com.grupo1.sgi_fia.R;

import java.util.Calendar;
import java.util.Locale;

public class PrestamoLibroActivity extends AppCompatActivity {

    private static final String CARNET_TEMPORAL = "N/A";

    private EditText etIdPrestamo;
    private EditText etNombrePrestatario;
    private EditText etTituloLibro;
    private EditText etFechaPrestamo;
    private EditText etFechaLimite;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_prestamo_libro);

        etIdPrestamo = findViewById(R.id.etIdPrestamoLibro);
        etNombrePrestatario = findViewById(R.id.etNombrePrestatarioLibro);
        etTituloLibro = findViewById(R.id.etTituloLibro);
        etFechaPrestamo = findViewById(R.id.etFechaPrestamoLibro);
        etFechaLimite = findViewById(R.id.etFechaLimiteLibro);
        View btnBuscarPrestatario = findViewById(R.id.btnBuscarPrestatarioLibro);
        View btnRegistrar = findViewById(R.id.btnRegistrarPrestamoLibro);
        View btnCancelar = findViewById(R.id.btnCancelarPrestamoLibro);

        etFechaPrestamo.setOnClickListener(view -> mostrarSelectorFecha(etFechaPrestamo));
        etFechaLimite.setOnClickListener(view -> mostrarSelectorFecha(etFechaLimite));
        btnBuscarPrestatario.setOnClickListener(view ->
                Toast.makeText(this, "Busqueda de prestatario", Toast.LENGTH_SHORT).show());
        btnRegistrar.setOnClickListener(view -> registrarPrestamo());
        btnCancelar.setOnClickListener(view -> finish());
    }

    private void registrarPrestamo() {
        String nombre = obtenerTexto(etNombrePrestatario);
        String tituloLibro = obtenerTexto(etTituloLibro);
        String fechaPrestamo = obtenerTexto(etFechaPrestamo);
        String fechaLimite = obtenerTexto(etFechaLimite);
        String estado = "Pendiente";

        if (nombre.isEmpty() || tituloLibro.isEmpty()
                || fechaPrestamo.isEmpty() || fechaLimite.isEmpty()) {
            Toast.makeText(this, "Complete los campos obligatorios", Toast.LENGTH_SHORT).show();
            return;
        }

        AdminSQLiteOpenHelper admin = new AdminSQLiteOpenHelper(
                this,
                AdminSQLiteOpenHelper.NOMBRE_BD,
                null,
                AdminSQLiteOpenHelper.VERSION_BD);
        SQLiteDatabase db = admin.getWritableDatabase();

        int idDocumento = obtenerOCrearLibro(db, tituloLibro);
        asegurarPrestatarioTemporal(db, nombre);

        ContentValues prestamo = new ContentValues();
        prestamo.put("carnet_prestatario", CARNET_TEMPORAL);
        prestamo.put("id_documento", idDocumento);
        prestamo.put("fecha_prestamo", fechaPrestamo);
        prestamo.put("fecha_limite", fechaLimite);
        prestamo.put("estado", estado);

        long resultado = db.insert("prestamos", null, prestamo);
        db.close();

        if (resultado == -1) {
            Toast.makeText(this, "No se pudo registrar el prestamo", Toast.LENGTH_SHORT).show();
            return;
        }

        etIdPrestamo.setText(String.valueOf(resultado));
        etNombrePrestatario.setText("");
        Toast.makeText(this, "Prestamo de libro registrado", Toast.LENGTH_SHORT).show();
    }

    private int obtenerOCrearLibro(SQLiteDatabase db, String tituloLibro) {
        Cursor cursor = db.rawQuery(
                "SELECT id_documento FROM documentos WHERE titulo=? AND tipo=? LIMIT 1",
                new String[]{tituloLibro, "Libro"});

        if (cursor.moveToFirst()) {
            int idDocumento = cursor.getInt(0);
            cursor.close();
            return idDocumento;
        }
        cursor.close();

        ContentValues documento = new ContentValues();
        documento.put("titulo", tituloLibro);
        documento.put("tipo", "Libro");
        documento.put("isbn", "");
        documento.put("idioma", "");
        documento.put("anio", 0);
        documento.put("ejemplares", 1);

        return (int) db.insert("documentos", null, documento);
    }

    private void asegurarPrestatarioTemporal(SQLiteDatabase db, String nombre) {
        ContentValues prestatario = new ContentValues();
        prestatario.put("carnet", CARNET_TEMPORAL);
        prestatario.put("nombre", nombre);
        db.insertWithOnConflict(
                "prestatarios",
                null,
                prestatario,
                SQLiteDatabase.CONFLICT_IGNORE);
    }

    private void mostrarSelectorFecha(EditText campoFecha) {
        Calendar calendario = Calendar.getInstance();
        DatePickerDialog dialog = new DatePickerDialog(
                this,
                (view, year, month, dayOfMonth) -> campoFecha.setText(
                        String.format(Locale.US, "%02d/%02d/%04d", dayOfMonth, month + 1, year)),
                calendario.get(Calendar.YEAR),
                calendario.get(Calendar.MONTH),
                calendario.get(Calendar.DAY_OF_MONTH));
        dialog.show();
    }

    private String obtenerTexto(EditText editText) {
        return editText.getText().toString().trim();
    }
}
