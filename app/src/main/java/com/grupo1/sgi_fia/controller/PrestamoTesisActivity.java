package com.grupo1.sgi_fia.controller;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.grupo1.sgi_fia.R;
import com.grupo1.sgi_fia.data.SgiFirebase;

import java.util.Calendar;
import java.util.Locale;
import java.util.Map;

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
        View btnRegistrar = findViewById(R.id.btnRegistrarPrestamoTesis);
        View btnCancelar = findViewById(R.id.btnCancelarPrestamoTesis);
        View btnBuscarPrestatario = findViewById(R.id.btnBuscarPrestatarioTesis);

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
        String tituloTesis = obtenerTexto(etTituloTesis);
        String fechaPrestamo = obtenerTexto(etFechaPrestamo);
        String fechaDevolucion = obtenerTexto(etFechaDevolucion);

        if (nombre.isEmpty() || tituloTesis.isEmpty() || fechaPrestamo.isEmpty()
                || fechaDevolucion.isEmpty()) {
            Toast.makeText(this, "Complete los campos obligatorios", Toast.LENGTH_SHORT).show();
            return;
        }

        SgiFirebase.upsertPrestatario(this, carnet, nombre, new SgiFirebase.Callback<String>() {
            @Override
            public void onSuccess(String id) {
                guardarPrestamo(nombre, carnet, tituloTesis, fechaPrestamo, fechaDevolucion);
            }

            @Override
            public void onError(Exception exception) {
                mostrarErrorFirebase(exception);
            }
        });
    }

    private void guardarPrestamo(
            String nombre,
            String carnet,
            String tituloTesis,
            String fechaPrestamo,
            String fechaDevolucion) {
        Map<String, Object> registro = SgiFirebase.values();
        registro.put("nombre_prestatario", nombre);
        registro.put("carnet_prestatario", carnet);
        registro.put("codigo_tesis", "Automatico");
        registro.put("titulo_tesis", tituloTesis);
        registro.put("fecha_prestamo", fechaPrestamo);
        registro.put("fecha_devolucion", fechaDevolucion);
        registro.put("estado_prestamo", "Pendiente");
        registro.put("observaciones", "");

        SgiFirebase.add(this, SgiFirebase.PRESTAMOS_TESIS, registro,
                new SgiFirebase.Callback<String>() {
                    @Override
                    public void onSuccess(String id) {
                        limpiarFormulario();
                        Toast.makeText(PrestamoTesisActivity.this,
                                "Prestamo de tesis registrado en Firebase",
                                Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onError(Exception exception) {
                        mostrarErrorFirebase(exception);
                    }
                });
    }

    private void mostrarSelectorFecha(EditText campoFecha) {
        Calendar calendario = Calendar.getInstance();
        DatePickerDialog dialog = new DatePickerDialog(
                this,
                (view, year, month, dayOfMonth) -> campoFecha.setText(
                        String.format(Locale.US, "%04d-%02d-%02d", year, month + 1, dayOfMonth)),
                calendario.get(Calendar.YEAR),
                calendario.get(Calendar.MONTH),
                calendario.get(Calendar.DAY_OF_MONTH));
        dialog.show();
    }

    private void mostrarErrorFirebase(Exception exception) {
        Toast.makeText(this, "No se pudo guardar en Firebase: " + exception.getMessage(),
                Toast.LENGTH_LONG).show();
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
