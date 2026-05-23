package com.grupo1.sgi_fia.controller;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
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

public class PrestamoEquipoPorHorasActivity extends AppCompatActivity {

    private EditText etNombrePrestatario;
    private EditText etFechaPrestamo;
    private EditText etHoraInicio;
    private EditText etHoraFin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_prestamo_equipo_por_horas);

        etNombrePrestatario = findViewById(R.id.etNombrePrestatarioEquipo);
        etFechaPrestamo = findViewById(R.id.etFechaPrestamoEquipo);
        etHoraInicio = findViewById(R.id.etHoraInicioEquipo);
        etHoraFin = findViewById(R.id.etHoraFinEquipo);
        Button btnRegistrar = findViewById(R.id.btnRegistrarPrestamoEquipo);
        Button btnCancelar = findViewById(R.id.btnCancelarPrestamoEquipo);
        Button btnBuscarPrestatario = findViewById(R.id.btnBuscarPrestatarioEquipo);
        Button btnAnadirEquipo = findViewById(R.id.btnAnadirEquipoPrestamo);

        etFechaPrestamo.setOnClickListener(view -> mostrarSelectorFecha(etFechaPrestamo));
        etHoraInicio.setOnClickListener(view -> mostrarSelectorHora(etHoraInicio));
        etHoraFin.setOnClickListener(view -> mostrarSelectorHora(etHoraFin));
        btnRegistrar.setOnClickListener(view -> registrarPrestamo());
        btnCancelar.setOnClickListener(view -> finish());
        btnBuscarPrestatario.setOnClickListener(view ->
                Toast.makeText(this, "Busqueda de prestatario", Toast.LENGTH_SHORT).show());
        btnAnadirEquipo.setOnClickListener(view ->
                Toast.makeText(this, "Equipo agregado al prestamo", Toast.LENGTH_SHORT).show());
    }

    private void registrarPrestamo() {
        String nombre = obtenerTexto(etNombrePrestatario);
        String carnet = "N/A";
        String equipo = "Monitor Dell S2725HSM; Impresora HP Smart Tank 580";
        String fecha = obtenerTexto(etFechaPrestamo);
        String horaInicio = obtenerTexto(etHoraInicio);
        String horaFin = obtenerTexto(etHoraFin);
        String actividad = "Prestamo por horas";
        String observaciones = "";

        if (nombre.isEmpty() || fecha.isEmpty() || horaInicio.isEmpty() || horaFin.isEmpty()) {
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
        registro.put("equipo", equipo);
        registro.put("fecha_prestamo", fecha);
        registro.put("hora_inicio", horaInicio);
        registro.put("hora_fin", horaFin);
        registro.put("actividad", actividad);
        registro.put("observaciones", observaciones);

        long resultado = db.insert("prestamos_equipo_horas", null, registro);
        db.close();

        if (resultado == -1) {
            Toast.makeText(this, "No se pudo registrar el prestamo", Toast.LENGTH_SHORT).show();
            return;
        }

        limpiarFormulario();
        Toast.makeText(this, "Prestamo de equipo registrado", Toast.LENGTH_SHORT).show();
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

    private void mostrarSelectorHora(EditText campoHora) {
        Calendar calendario = Calendar.getInstance();
        TimePickerDialog dialog = new TimePickerDialog(
                this,
                (view, hourOfDay, minute) -> campoHora.setText(
                        String.format(Locale.US, "%02d:%02d", hourOfDay, minute)),
                calendario.get(Calendar.HOUR_OF_DAY),
                calendario.get(Calendar.MINUTE),
                true);
        dialog.show();
    }

    private String obtenerTexto(EditText editText) {
        return editText.getText().toString().trim();
    }

    private void limpiarFormulario() {
        etNombrePrestatario.setText("");
        etFechaPrestamo.setText("");
        etHoraInicio.setText("");
        etHoraFin.setText("");
    }
}
