package com.grupo1.sgi_fia.controller;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.firestore.DocumentSnapshot;
import com.grupo1.sgi_fia.R;
import com.grupo1.sgi_fia.data.SgiFirebase;

import java.util.Calendar;
import java.util.Locale;
import java.util.Map;

public class DevolucionTesisActivity extends AppCompatActivity {

    private EditText etIdentificador;
    private EditText etFechaDevolucion;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_devolucion_tesis);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        etIdentificador = findViewById(R.id.etIdentificadorDevolucionTesis);
        etFechaDevolucion = findViewById(R.id.etFechaDevolucionTesis);
        View btnLeerIdentificador = findViewById(R.id.btnLeerIdentificadorTesis);
        View btnRegistrar = findViewById(R.id.btnRegistrarDevolucionTesis);
        View btnCancelar = findViewById(R.id.btnCancelarDevolucionTesis);

        etFechaDevolucion.setOnClickListener(view -> mostrarSelectorFecha());
        btnLeerIdentificador.setOnClickListener(view ->
                Toast.makeText(this, "Lectura de identificador pendiente", Toast.LENGTH_SHORT).show());
        btnRegistrar.setOnClickListener(view -> registrarDevolucion());
        btnCancelar.setOnClickListener(view -> finish());
    }

    private void registrarDevolucion() {
        String idPrestamo = etIdentificador.getText().toString().trim();
        String fechaDevolucion = etFechaDevolucion.getText().toString().trim();

        if (idPrestamo.isEmpty() || fechaDevolucion.isEmpty()) {
            Toast.makeText(this, "Complete los campos obligatorios", Toast.LENGTH_SHORT).show();
            return;
        }

        SgiFirebase.db(this)
                .collection(SgiFirebase.PRESTAMOS_TESIS)
                .document(idPrestamo)
                .get()
                .addOnSuccessListener(prestamo -> {
                    if (!prestamo.exists()) {
                        Toast.makeText(this, "No existe ese prestamo de tesis en Firebase",
                                Toast.LENGTH_SHORT).show();
                        return;
                    }
                    cerrarPrestamoTesis(prestamo, fechaDevolucion);
                })
                .addOnFailureListener(this::mostrarErrorFirebase);
    }

    private void cerrarPrestamoTesis(DocumentSnapshot prestamo, String fechaDevolucion) {
        Map<String, Object> actualizacion = SgiFirebase.values();
        actualizacion.put("estado_prestamo", "Devuelto");

        SgiFirebase.update(this, SgiFirebase.PRESTAMOS_TESIS, prestamo.getId(), actualizacion,
                new SgiFirebase.Callback<String>() {
                    @Override
                    public void onSuccess(String id) {
                        guardarHistorialDevolucion(prestamo, fechaDevolucion);
                    }

                    @Override
                    public void onError(Exception exception) {
                        mostrarErrorFirebase(exception);
                    }
                });
    }

    private void guardarHistorialDevolucion(DocumentSnapshot prestamo, String fechaDevolucion) {
        Map<String, Object> devolucion = SgiFirebase.values();
        devolucion.put("id_prestamo", prestamo.getId());
        devolucion.put("titulo_tesis", SgiFirebase.string(prestamo, "titulo_tesis"));
        devolucion.put("marcar_devuelto", true);
        devolucion.put("fecha_devolucion", fechaDevolucion);

        SgiFirebase.add(this, SgiFirebase.DEVOLUCIONES_TESIS, devolucion,
                new SgiFirebase.Callback<String>() {
                    @Override
                    public void onSuccess(String id) {
                        etIdentificador.setText("");
                        etFechaDevolucion.setText("");
                        Toast.makeText(DevolucionTesisActivity.this,
                                "Devolucion de tesis registrada en Firebase",
                                Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onError(Exception exception) {
                        mostrarErrorFirebase(exception);
                    }
                });
    }

    private void mostrarSelectorFecha() {
        Calendar calendario = Calendar.getInstance();
        DatePickerDialog dialog = new DatePickerDialog(
                this,
                (view, year, month, dayOfMonth) -> etFechaDevolucion.setText(
                        String.format(Locale.US, "%04d-%02d-%02d", year, month + 1, dayOfMonth)),
                calendario.get(Calendar.YEAR),
                calendario.get(Calendar.MONTH),
                calendario.get(Calendar.DAY_OF_MONTH));
        dialog.show();
    }

    private void mostrarErrorFirebase(Exception exception) {
        Toast.makeText(this, "No se pudo actualizar Firebase: " + exception.getMessage(),
                Toast.LENGTH_LONG).show();
    }
}
