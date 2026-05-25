package com.grupo1.sgi_fia.controller;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.grupo1.sgi_fia.R;
import com.grupo1.sgi_fia.data.SgiFirebase;

import java.util.Map;

public class DevolucionLibroActivity extends AppCompatActivity {

    private EditText etIdPrestamo;
    private EditText etFechaDevolucion;
    private CheckBox cbMarcarDevuelto;
    private Button btnRegistrar;
    private Button btnCancelar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_devolucion_libro);

        etIdPrestamo = findViewById(R.id.etIdPrestamo);
        cbMarcarDevuelto = findViewById(R.id.cbMarcarDevuelto);
        etFechaDevolucion = findViewById(R.id.etFechaDevolucion);
        btnRegistrar = findViewById(R.id.btnRegistrarDevolucion);
        btnCancelar = findViewById(R.id.btnCancelarDevolucion);

        btnRegistrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                registrarDevolucion();
            }
        });

        btnCancelar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void registrarDevolucion() {
        String idPrestamo = etIdPrestamo.getText().toString().trim();
        String fechaDevolucion = etFechaDevolucion.getText().toString().trim();

        if (idPrestamo.isEmpty() || fechaDevolucion.isEmpty()) {
            Toast.makeText(this, "Por favor, complete el ID de prestamo y la fecha",
                    Toast.LENGTH_SHORT).show();
            return;
        }

        if (!cbMarcarDevuelto.isChecked()) {
            Toast.makeText(this, "Marque la casilla para confirmar la devolucion",
                    Toast.LENGTH_SHORT).show();
            return;
        }

        Map<String, Object> prestamo = SgiFirebase.values();
        prestamo.put("estado", "Devuelto");

        SgiFirebase.update(this, SgiFirebase.PRESTAMOS, idPrestamo, prestamo,
                new SgiFirebase.Callback<String>() {
                    @Override
                    public void onSuccess(String id) {
                        guardarHistorialDevolucion(idPrestamo, fechaDevolucion);
                    }

                    @Override
                    public void onError(Exception exception) {
                        mostrarErrorFirebase(exception);
                    }
                });
    }

    private void guardarHistorialDevolucion(String idPrestamo, String fechaDevolucion) {
        Map<String, Object> devolucion = SgiFirebase.values();
        devolucion.put("id_prestamo", idPrestamo);
        devolucion.put("marcar_devuelto", true);
        devolucion.put("fecha_devolucion", fechaDevolucion);

        SgiFirebase.add(this, SgiFirebase.DEVOLUCIONES, devolucion,
                new SgiFirebase.Callback<String>() {
                    @Override
                    public void onSuccess(String id) {
                        limpiarFormulario();
                        Toast.makeText(DevolucionLibroActivity.this,
                                "Devolucion registrada en Firebase",
                                Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onError(Exception exception) {
                        mostrarErrorFirebase(exception);
                    }
                });
    }

    private void limpiarFormulario() {
        etIdPrestamo.setText("");
        etFechaDevolucion.setText("");
        cbMarcarDevuelto.setChecked(false);
    }

    private void mostrarErrorFirebase(Exception exception) {
        Toast.makeText(this, "No se pudo actualizar Firebase: " + exception.getMessage(),
                Toast.LENGTH_LONG).show();
    }
}
