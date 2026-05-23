package com.grupo1.sgi_fia.controller;

import android.app.DatePickerDialog;
import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.grupo1.sgi_fia.AdminSQLiteOpenHelper;
import com.grupo1.sgi_fia.R;

import java.util.Calendar;
import java.util.Locale;

public class PrestamoTesisActivity extends AppCompatActivity {

    private EditText etNombrePrestatario;
    private EditText etTituloTesis;
    private EditText etFechaPrestamo;
    private EditText etFechaDevolucion;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_prestamo_tesis);

        etNombrePrestatario = findViewById(R.id.etNombrePrestatarioTesis);
        etTituloTesis = findViewById(R.id.etTituloTesis);
        etFechaPrestamo = findViewById(R.id.etFechaPrestamoTesis);
        etFechaDevolucion = findViewById(R.id.etFechaDevolucionTesis);
        Button btnRegistrar = findViewById(R.id.btnRegistrarPrestamoTesis);
        Button btnCancelar = findViewById(R.id.btnCancelarPrestamoTesis);
        Button btnBuscarPrestatario = findViewById(R.id.btnBuscarPrestatarioTesis);

        etFechaPrestamo.setOnClickListener(view -> mostrarSelectorFecha(etFechaPrestamo));
        etFechaDevolucion.setOnClickListener(view -> mostrarSelectorFecha(etFechaDevolucion));
        btnRegistrar.setOnClickListener(view -> registrarPrestamo());
        btnCancelar.setOnClickListener(view -> finish());
        btnBuscarPrestatario.setOnClickListener(view ->
                Toast.makeText(this, "Busqueda de prestatario", Toast.LENGTH_SHORT).show());
    }

    private void registrarPrestamo() {
        String nombre = obtenerTexto(etNombrePrestatario);
        String carnet = "N/A";
        String codigoTesis = "Automatico";
        String tituloTesis = obtenerTexto(etTituloTesis);
        String fechaPrestamo = obtenerTexto(etFechaPrestamo);
        String fechaDevolucion = obtenerTexto(etFechaDevolucion);
        String estadoPrestamo = "Pendiente";
        String observaciones = "";

        if (nombre.isEmpty() || tituloTesis.isEmpty() || fechaPrestamo.isEmpty()
                || fechaDevolucion.isEmpty()) {
            Toast.makeText(this, "Complete los campos obligatorios", Toast.LENGTH_SHORT).show();
            return;
        }

        AdminSQLiteOpenHelper admin = new AdminSQLiteOpenHelper(
                this,
                AdminSQLiteOpenHelper.NOMBRE_BD,
                null,
                AdminSQLiteOpenHelper.VERSION_BD);
        SQLiteDatabase db = admin.getWritableDatabase();

        ContentValues registro = new ContentValues();
        registro.put("nombre_prestatario", nombre);
        registro.put("carnet_prestatario", carnet);
        registro.put("codigo_tesis", codigoTesis);
        registro.put("titulo_tesis", tituloTesis);
        registro.put("fecha_prestamo", fechaPrestamo);
        registro.put("fecha_devolucion", fechaDevolucion);
        registro.put("estado_prestamo", estadoPrestamo);
        registro.put("observaciones", observaciones);

        long resultado = db.insert("prestamos_tesis", null, registro);
        db.close();

        if (resultado == -1) {
            Toast.makeText(this, "No se pudo registrar el prestamo", Toast.LENGTH_SHORT).show();
            return;
        }

        limpiarFormulario();
        Toast.makeText(this, "Prestamo de tesis registrado", Toast.LENGTH_SHORT).show();
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

    private void limpiarFormulario() {
        etNombrePrestatario.setText("");
        etTituloTesis.setText("");
        etFechaPrestamo.setText("");
        etFechaDevolucion.setText("");
    }
}
