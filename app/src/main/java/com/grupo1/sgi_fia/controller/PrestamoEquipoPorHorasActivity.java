package com.grupo1.sgi_fia.controller;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.firestore.DocumentSnapshot;
import com.grupo1.sgi_fia.R;
import com.grupo1.sgi_fia.data.SgiFirebase;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class PrestamoEquipoPorHorasActivity extends AppCompatActivity {

    private EditText etNombrePrestatario;
    private EditText etFechaPrestamo;
    private EditText etHoraInicio;
    private EditText etHoraFin;
    private LinearLayout contenedorEquipos;
    private final List<String> equiposRegistrados = new ArrayList<>();
    private final List<String> equiposSeleccionados = new ArrayList<>();
    private final Map<String, String> equipoIdsPorEtiqueta = new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_prestamo_equipo_por_horas);

        etNombrePrestatario = findViewById(R.id.etNombrePrestatarioEquipo);
        etFechaPrestamo = findViewById(R.id.etFechaPrestamoEquipo);
        etHoraInicio = findViewById(R.id.etHoraInicioEquipo);
        etHoraFin = findViewById(R.id.etHoraFinEquipo);
        contenedorEquipos = findViewById(R.id.contenedorEquiposPrestamo);
        View btnRegistrar = findViewById(R.id.btnRegistrarPrestamoEquipo);
        View btnCancelar = findViewById(R.id.btnCancelarPrestamoEquipo);
        View btnBuscarPrestatario = findViewById(R.id.btnBuscarPrestatarioEquipo);
        View btnAnadirEquipo = findViewById(R.id.btnAnadirEquipoPrestamo);

        cargarEquiposRegistrados();
        etFechaPrestamo.setOnClickListener(view -> mostrarSelectorFecha(etFechaPrestamo));
        etHoraInicio.setOnClickListener(view -> mostrarSelectorHora(etHoraInicio));
        etHoraFin.setOnClickListener(view -> mostrarSelectorHora(etHoraFin));
        btnRegistrar.setOnClickListener(view -> registrarPrestamo());
        btnCancelar.setOnClickListener(view -> finish());
        btnBuscarPrestatario.setOnClickListener(view ->
                Toast.makeText(this, "Busqueda de prestatario", Toast.LENGTH_SHORT).show());
        btnAnadirEquipo.setOnClickListener(view -> confirmarEquiposSeleccionados());
    }

    private void registrarPrestamo() {
        String nombre = obtenerTexto(etNombrePrestatario);
        String carnet = "N/A";
        String equipo = obtenerListadoEquiposParaPrestamo();
        String fecha = obtenerTexto(etFechaPrestamo);
        String horaInicio = obtenerTexto(etHoraInicio);
        String horaFin = obtenerTexto(etHoraFin);

        if (equiposRegistrados.isEmpty()) {
            Toast.makeText(this, "No hay equipos registrados en Firebase", Toast.LENGTH_SHORT).show();
            return;
        }

        if (equipo.isEmpty()) {
            Toast.makeText(this, "Seleccione al menos un equipo", Toast.LENGTH_SHORT).show();
            return;
        }

        if (nombre.isEmpty() || fecha.isEmpty() || horaInicio.isEmpty() || horaFin.isEmpty()) {
            Toast.makeText(this, "Complete los campos obligatorios", Toast.LENGTH_SHORT).show();
            return;
        }

        SgiFirebase.upsertPrestatario(this, carnet, nombre, new SgiFirebase.Callback<String>() {
            @Override
            public void onSuccess(String id) {
                guardarPrestamo(nombre, carnet, equipo, fecha, horaInicio, horaFin);
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
            String equipo,
            String fecha,
            String horaInicio,
            String horaFin) {
        Map<String, Object> registro = SgiFirebase.values();
        registro.put("nombre_prestatario", nombre);
        registro.put("carnet_prestatario", carnet);
        registro.put("equipo", equipo);
        registro.put("equipos_ids", obtenerIdsEquiposSeleccionados());
        registro.put("fecha_prestamo", fecha);
        registro.put("hora_inicio", horaInicio);
        registro.put("hora_fin", horaFin);
        registro.put("actividad", "Prestamo por horas");
        registro.put("observaciones", "");

        SgiFirebase.add(this, SgiFirebase.PRESTAMOS_EQUIPO_HORAS, registro,
                new SgiFirebase.Callback<String>() {
                    @Override
                    public void onSuccess(String id) {
                        limpiarFormulario();
                        Toast.makeText(PrestamoEquipoPorHorasActivity.this,
                                "Prestamo de equipo registrado en Firebase",
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
        equiposSeleccionados.clear();
        cargarEquiposRegistrados();
    }

    private void cargarEquiposRegistrados() {
        contenedorEquipos.removeAllViews();
        equiposRegistrados.clear();
        equipoIdsPorEtiqueta.clear();
        contenedorEquipos.addView(crearFilaEquipo("Cargando equipos", "Firebase", ""));

        SgiFirebase.list(this, SgiFirebase.EQUIPOS, new SgiFirebase.Callback<List<DocumentSnapshot>>() {
            @Override
            public void onSuccess(List<DocumentSnapshot> documentos) {
                contenedorEquipos.removeAllViews();
                for (DocumentSnapshot documento : documentos) {
                    String nombre = SgiFirebase.string(documento, "nombre");
                    String modelo = SgiFirebase.string(documento, "modelo");
                    String modeloFormateado = formatearModelo(modelo);
                    String equipo = nombre + " - " + modeloFormateado;
                    equiposRegistrados.add(equipo);
                    equipoIdsPorEtiqueta.put(equipo, documento.getId());
                    contenedorEquipos.addView(crearFilaEquipo(nombre, modeloFormateado, equipo));
                }

                if (equiposRegistrados.isEmpty()) {
                    contenedorEquipos.addView(crearFilaEquipo(
                            "Sin equipos registrados", "Revise Firebase", ""));
                }
            }

            @Override
            public void onError(Exception exception) {
                contenedorEquipos.removeAllViews();
                contenedorEquipos.addView(crearFilaEquipo(
                        "No se pudo cargar Firebase", exception.getMessage(), ""));
            }
        });
    }

    private View crearFilaEquipo(String nombre, String modelo, String equipo) {
        LinearLayout fila = new LinearLayout(this);
        fila.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                dp(34)));
        fila.setClickable(!equipo.isEmpty());
        fila.setFocusable(!equipo.isEmpty());
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

        TextView flecha = new TextView(this);
        flecha.setLayoutParams(new LinearLayout.LayoutParams(
                dp(18),
                LinearLayout.LayoutParams.WRAP_CONTENT));
        flecha.setGravity(Gravity.END);
        flecha.setText(equiposSeleccionados.contains(equipo) ? "\u2713" : ">");
        flecha.setTextColor(Color.parseColor("#1A1A1A"));
        flecha.setTextSize(14);

        textos.addView(titulo);
        textos.addView(subtitulo);
        fila.addView(textos);
        fila.addView(flecha);

        if (!equipo.isEmpty()) {
            aplicarEstadoFilaEquipo(fila, flecha, equipo);
            fila.setOnClickListener(view -> cambiarSeleccionEquipo(fila, flecha, equipo));
        }

        return fila;
    }

    private String obtenerListadoEquiposParaPrestamo() {
        return String.join("; ", equiposSeleccionados);
    }

    private List<String> obtenerIdsEquiposSeleccionados() {
        List<String> ids = new ArrayList<>();
        for (String equipo : equiposSeleccionados) {
            String id = equipoIdsPorEtiqueta.get(equipo);
            if (id != null) {
                ids.add(id);
            }
        }
        return ids;
    }

    private String formatearModelo(String modelo) {
        if (modelo == null || modelo.trim().isEmpty()) {
            return "Modelo pendiente";
        }
        if (modelo.startsWith("Modelo ")) {
            return modelo;
        }
        return "Modelo " + modelo;
    }

    private int dp(int valor) {
        return (int) (valor * getResources().getDisplayMetrics().density + 0.5f);
    }

    private void cambiarSeleccionEquipo(LinearLayout fila, TextView indicador, String equipo) {
        if (equiposSeleccionados.contains(equipo)) {
            equiposSeleccionados.remove(equipo);
        } else {
            equiposSeleccionados.add(equipo);
        }
        aplicarEstadoFilaEquipo(fila, indicador, equipo);
    }

    private void aplicarEstadoFilaEquipo(LinearLayout fila, TextView indicador, String equipo) {
        boolean seleccionado = equiposSeleccionados.contains(equipo);
        fila.setBackgroundColor(seleccionado ? Color.parseColor("#EEF2F6") : Color.TRANSPARENT);
        indicador.setText(seleccionado ? "\u2713" : ">");
    }

    private void confirmarEquiposSeleccionados() {
        if (equiposSeleccionados.isEmpty()) {
            Toast.makeText(this, "Seleccione al menos un equipo", Toast.LENGTH_SHORT).show();
            return;
        }
        Toast.makeText(this, "Equipo agregado al prestamo", Toast.LENGTH_SHORT).show();
    }

    private void mostrarErrorFirebase(Exception exception) {
        Toast.makeText(this, "No se pudo guardar en Firebase: " + exception.getMessage(),
                Toast.LENGTH_LONG).show();
    }
}
