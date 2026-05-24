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

import com.grupo1.sgi_fia.R;

import java.util.Calendar;
import java.util.Locale;

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
        String identificador = etIdentificador.getText().toString().trim();
        String fechaDevolucion = etFechaDevolucion.getText().toString().trim();

        if (identificador.isEmpty() || fechaDevolucion.isEmpty()) {
            Toast.makeText(this, "Complete los campos obligatorios", Toast.LENGTH_SHORT).show();
            return;
        }

        etIdentificador.setText("");
        etFechaDevolucion.setText("");
        Toast.makeText(this, "Devolución de tesis registrada", Toast.LENGTH_SHORT).show();
    }

    private void mostrarSelectorFecha() {
        Calendar calendario = Calendar.getInstance();
        DatePickerDialog dialog = new DatePickerDialog(
                this,
                (view, year, month, dayOfMonth) -> etFechaDevolucion.setText(
                        String.format(Locale.US, "%02d/%02d/%04d", dayOfMonth, month + 1, year)),
                calendario.get(Calendar.YEAR),
                calendario.get(Calendar.MONTH),
                calendario.get(Calendar.DAY_OF_MONTH));
        dialog.show();
    }
}
