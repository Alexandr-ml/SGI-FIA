package com.grupo1.sgi_fia.controller;

import android.app.DatePickerDialog;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.grupo1.sgi_fia.R;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class PrestamoDeEquipoRecurrente extends AppCompatActivity {

    private EditText etNombrePrestatario;
    private EditText etFechaInicio;
    private EditText etFechaFin;
    private LinearLayout contenedorEquipos;
    private final List<String> equiposSeleccionados = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_prestamo_de_equipo_recurrente);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        etNombrePrestatario = findViewById(R.id.etNombrePrestatarioRecurrente);
        etFechaInicio = findViewById(R.id.etFechaInicioRecurrente);
        etFechaFin = findViewById(R.id.etFechaFinRecurrente);
        contenedorEquipos = findViewById(R.id.contenedorEquiposRecurrente);
        View btnBuscarPrestatario = findViewById(R.id.btnBuscarPrestatarioRecurrente);
        View btnAnadir = findViewById(R.id.btnAnadirEquipoRecurrente);
        View btnEscaneo = findViewById(R.id.btnAnadirEquipoEscaneoRecurrente);
        View btnRegistrar = findViewById(R.id.btnRegistrarPrestamoRecurrente);
        View btnCancelar = findViewById(R.id.btnCancelarPrestamoRecurrente);

        cargarEquiposDisponibles();

        etFechaInicio.setOnClickListener(view -> mostrarSelectorFecha(etFechaInicio));
        etFechaFin.setOnClickListener(view -> mostrarSelectorFecha(etFechaFin));
        btnBuscarPrestatario.setOnClickListener(view ->
                Toast.makeText(this, "Búsqueda de prestatario", Toast.LENGTH_SHORT).show());
        btnAnadir.setOnClickListener(view -> confirmarEquiposSeleccionados());
        btnEscaneo.setOnClickListener(view ->
                Toast.makeText(this, "Escaneo de equipo pendiente", Toast.LENGTH_SHORT).show());
        btnRegistrar.setOnClickListener(view -> registrarPrestamo());
        btnCancelar.setOnClickListener(view -> finish());
    }

    private void cargarEquiposDisponibles() {
        contenedorEquipos.removeAllViews();
        contenedorEquipos.addView(crearFilaEquipo("Monitor Dell", "Modelo S2725HSM"));
        contenedorEquipos.addView(crearFilaEquipo("Impresora HP", "Modelo Smart Tank 580"));
    }

    private View crearFilaEquipo(String nombre, String modelo) {
        String equipo = nombre + " - " + modelo;

        LinearLayout fila = new LinearLayout(this);
        fila.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                dp(34)));
        fila.setClickable(true);
        fila.setFocusable(true);
        fila.setGravity(Gravity.CENTER_VERTICAL);
        fila.setOrientation(LinearLayout.HORIZONTAL);

        LinearLayout textos = new LinearLayout(this);
        textos.setLayoutParams(new LinearLayout.LayoutParams(
                0,
                LinearLayout.LayoutParams.WRAP_CONTENT,
                1));
        textos.setOrientation(LinearLayout.VERTICAL);

        TextView titulo = new TextView(this);
        titulo.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT));
        titulo.setIncludeFontPadding(false);
        titulo.setText(nombre);
        titulo.setTextColor(Color.parseColor("#1A1A1A"));
        titulo.setTextSize(11);

        TextView subtitulo = new TextView(this);
        subtitulo.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT));
        subtitulo.setIncludeFontPadding(false);
        subtitulo.setText(modelo);
        subtitulo.setTextColor(Color.parseColor("#444444"));
        subtitulo.setTextSize(8);

        TextView indicador = new TextView(this);
        indicador.setLayoutParams(new LinearLayout.LayoutParams(
                dp(18),
                LinearLayout.LayoutParams.WRAP_CONTENT));
        indicador.setGravity(Gravity.END);
        indicador.setTextColor(Color.parseColor("#1A1A1A"));
        indicador.setTextSize(14);

        textos.addView(titulo);
        textos.addView(subtitulo);
        fila.addView(textos);
        fila.addView(indicador);

        aplicarEstadoFila(fila, indicador, equipo);
        fila.setOnClickListener(view -> cambiarSeleccionEquipo(fila, indicador, equipo));

        return fila;
    }

    private void cambiarSeleccionEquipo(LinearLayout fila, TextView indicador, String equipo) {
        if (equiposSeleccionados.contains(equipo)) {
            equiposSeleccionados.remove(equipo);
        } else {
            equiposSeleccionados.add(equipo);
        }
        aplicarEstadoFila(fila, indicador, equipo);
    }

    private void aplicarEstadoFila(LinearLayout fila, TextView indicador, String equipo) {
        boolean seleccionado = equiposSeleccionados.contains(equipo);
        fila.setBackgroundColor(seleccionado ? Color.parseColor("#EEF2F6") : Color.TRANSPARENT);
        indicador.setText(seleccionado ? "\u2713" : ">");
    }

    private void confirmarEquiposSeleccionados() {
        if (equiposSeleccionados.isEmpty()) {
            Toast.makeText(this, "Seleccione al menos un equipo", Toast.LENGTH_SHORT).show();
            return;
        }
        Toast.makeText(this, "Equipo agregado al préstamo", Toast.LENGTH_SHORT).show();
    }

    private void registrarPrestamo() {
        String nombre = obtenerTexto(etNombrePrestatario);
        String fechaInicio = obtenerTexto(etFechaInicio);
        String fechaFin = obtenerTexto(etFechaFin);

        if (nombre.isEmpty() || fechaInicio.isEmpty() || fechaFin.isEmpty()) {
            Toast.makeText(this, "Complete los campos obligatorios", Toast.LENGTH_SHORT).show();
            return;
        }

        if (equiposSeleccionados.isEmpty()) {
            Toast.makeText(this, "Seleccione al menos un equipo", Toast.LENGTH_SHORT).show();
            return;
        }

        limpiarFormulario();
        Toast.makeText(this, "Préstamo recurrente registrado", Toast.LENGTH_SHORT).show();
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
        etFechaInicio.setText("");
        etFechaFin.setText("");
        equiposSeleccionados.clear();
        cargarEquiposDisponibles();
    }

    private int dp(int valor) {
        return (int) (valor * getResources().getDisplayMetrics().density + 0.5f);
    }
}
