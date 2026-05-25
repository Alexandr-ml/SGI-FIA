package com.grupo1.sgi_fia.controller;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.firestore.DocumentSnapshot;
import com.grupo1.sgi_fia.R;
import com.grupo1.sgi_fia.data.SgiFirebase;

import java.util.Calendar;
import java.util.Locale;
import java.util.Map;

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

        if (nombre.isEmpty() || tituloLibro.isEmpty()
                || fechaPrestamo.isEmpty() || fechaLimite.isEmpty()) {
            Toast.makeText(this, "Complete los campos obligatorios", Toast.LENGTH_SHORT).show();
            return;
        }

        SgiFirebase.upsertPrestatario(this, CARNET_TEMPORAL, nombre,
                new SgiFirebase.Callback<String>() {
                    @Override
                    public void onSuccess(String id) {
                        obtenerOCrearLibro(tituloLibro, documentId ->
                                guardarPrestamo(documentId, nombre, tituloLibro, fechaPrestamo, fechaLimite));
                    }

                    @Override
                    public void onError(Exception exception) {
                        mostrarErrorFirebase(exception);
                    }
                });
    }

    private interface DocumentReady {
        void onReady(String documentId);
    }

    private void obtenerOCrearLibro(String tituloLibro, DocumentReady callback) {
        SgiFirebase.findFirst(
                this,
                SgiFirebase.DOCUMENTOS,
                documento -> SgiFirebase.equalsNormalized(SgiFirebase.string(documento, "tipo"), "Libro")
                        && SgiFirebase.equalsNormalized(SgiFirebase.string(documento, "titulo"), tituloLibro),
                new SgiFirebase.Callback<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot existente) {
                        if (existente != null) {
                            callback.onReady(existente.getId());
                            return;
                        }

                        Map<String, Object> documento = SgiFirebase.values();
                        documento.put("titulo", tituloLibro);
                        documento.put("tipo", "Libro");
                        documento.put("isbn", "");
                        documento.put("idioma", "");
                        documento.put("anio", 0);
                        documento.put("ejemplares", 1);

                        SgiFirebase.add(PrestamoLibroActivity.this, SgiFirebase.DOCUMENTOS, documento,
                                new SgiFirebase.Callback<String>() {
                                    @Override
                                    public void onSuccess(String id) {
                                        callback.onReady(id);
                                    }

                                    @Override
                                    public void onError(Exception exception) {
                                        mostrarErrorFirebase(exception);
                                    }
                                });
                    }

                    @Override
                    public void onError(Exception exception) {
                        mostrarErrorFirebase(exception);
                    }
                });
    }

    private void guardarPrestamo(
            String documentId,
            String nombre,
            String tituloLibro,
            String fechaPrestamo,
            String fechaLimite) {
        Map<String, Object> prestamo = SgiFirebase.values();
        prestamo.put("carnet_prestatario", CARNET_TEMPORAL);
        prestamo.put("nombre_prestatario", nombre);
        prestamo.put("id_documento", documentId);
        prestamo.put("titulo_documento", tituloLibro);
        prestamo.put("fecha_prestamo", fechaPrestamo);
        prestamo.put("fecha_limite", fechaLimite);
        prestamo.put("estado", "Pendiente");

        SgiFirebase.add(this, SgiFirebase.PRESTAMOS, prestamo, new SgiFirebase.Callback<String>() {
            @Override
            public void onSuccess(String id) {
                etIdPrestamo.setText(id);
                etNombrePrestatario.setText("");
                etTituloLibro.setText("");
                etFechaPrestamo.setText("");
                etFechaLimite.setText("");
                Toast.makeText(PrestamoLibroActivity.this,
                        "Prestamo de libro registrado en Firebase", Toast.LENGTH_SHORT).show();
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
}
