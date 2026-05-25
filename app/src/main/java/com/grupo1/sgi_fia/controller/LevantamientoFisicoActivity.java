package com.grupo1.sgi_fia.controller;

import android.app.DatePickerDialog;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
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

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Comparator;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class LevantamientoFisicoActivity extends AppCompatActivity {

    private EditText etFechaLevantamiento;
    private EditText etNumeroSerie;
    private LinearLayout contenedorActivos;
    private LinearLayout contenedorHistorial;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_levantamiento_fisico);

        etFechaLevantamiento = findViewById(R.id.etFechaLevantamientoFisico);
        etNumeroSerie = findViewById(R.id.etNumeroSerieLevantamiento);
        contenedorActivos = findViewById(R.id.contenedorActivosLevantamiento);
        contenedorHistorial = findViewById(R.id.contenedorHistorialAuditoria);
        View btnEscanear = findViewById(R.id.btnEscanearLevantamiento);
        View btnFinalizar = findViewById(R.id.btnFinalizarLevantamiento);
        View btnCancelar = findViewById(R.id.btnCancelarLevantamiento);

        etFechaLevantamiento.setText(formatearFechaActual());
        etFechaLevantamiento.setOnClickListener(view -> mostrarSelectorFecha());
        etNumeroSerie.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                cargarActivos(s.toString().trim());
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        btnEscanear.setOnClickListener(view ->
                Toast.makeText(this, "Escaneo de activo pendiente", Toast.LENGTH_SHORT).show());
        btnFinalizar.setOnClickListener(view -> finalizarLevantamiento());
        btnCancelar.setOnClickListener(view -> finish());

        cargarActivos("");
        cargarHistorialAuditoria();
    }

    private void cargarActivos(String filtroSerie) {
        contenedorActivos.removeAllViews();
        contenedorActivos.addView(crearFilaActivo("Cargando activos", "Firebase"));

        SgiFirebase.list(this, SgiFirebase.EQUIPOS, new SgiFirebase.Callback<List<DocumentSnapshot>>() {
            @Override
            public void onSuccess(List<DocumentSnapshot> documentos) {
                contenedorActivos.removeAllViews();
                String filtro = SgiFirebase.normalize(filtroSerie);
                int agregados = 0;

                for (DocumentSnapshot documento : documentos) {
                    String numeroSerie = SgiFirebase.string(documento, "numero_serie");
                    if (!filtro.isEmpty() && !SgiFirebase.normalize(numeroSerie).contains(filtro)) {
                        continue;
                    }

                    String nombre = SgiFirebase.string(documento, "nombre");
                    String modelo = SgiFirebase.string(documento, "modelo");
                    String ubicacion = SgiFirebase.string(documento, "ubicacion");
                    String titulo = nombre.isEmpty() ? modelo : nombre;
                    String subtitulo = ubicacion.isEmpty() ? "Ubicacion pendiente" : ubicacion;
                    contenedorActivos.addView(crearFilaActivo(titulo, subtitulo));
                    agregados++;
                    if (agregados >= 6) {
                        break;
                    }
                }

                if (contenedorActivos.getChildCount() == 0) {
                    contenedorActivos.addView(crearFilaActivo(
                            "Sin activos registrados", "Agregue equipos al inventario"));
                }
            }

            @Override
            public void onError(Exception exception) {
                contenedorActivos.removeAllViews();
                contenedorActivos.addView(crearFilaActivo(
                        "No se pudo cargar Firebase", exception.getMessage()));
            }
        });
    }

    private View crearFilaActivo(String tituloTexto, String subtituloTexto) {
        LinearLayout fila = new LinearLayout(this);
        fila.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                dp(44)));
        fila.setGravity(Gravity.CENTER_VERTICAL);
        fila.setOrientation(LinearLayout.HORIZONTAL);

        LinearLayout textos = new LinearLayout(this);
        textos.setLayoutParams(new LinearLayout.LayoutParams(
                0,
                LinearLayout.LayoutParams.WRAP_CONTENT,
                1));
        textos.setOrientation(LinearLayout.VERTICAL);

        TextView titulo = new TextView(this);
        titulo.setIncludeFontPadding(false);
        titulo.setText(tituloTexto);
        titulo.setTextColor(Color.parseColor("#1A1A1A"));
        titulo.setTextSize(9);

        TextView subtitulo = new TextView(this);
        subtitulo.setIncludeFontPadding(false);
        subtitulo.setText(subtituloTexto);
        subtitulo.setTextColor(Color.parseColor("#444444"));
        subtitulo.setTextSize(7);

        TextView indicador = new TextView(this);
        indicador.setLayoutParams(new LinearLayout.LayoutParams(dp(18), LinearLayout.LayoutParams.WRAP_CONTENT));
        indicador.setGravity(Gravity.END);
        indicador.setText(">");
        indicador.setTextColor(Color.parseColor("#1A1A1A"));
        indicador.setTextSize(10);

        textos.addView(titulo);
        textos.addView(subtitulo);
        fila.addView(textos);
        fila.addView(indicador);

        return fila;
    }

    private void cargarHistorialAuditoria() {
        contenedorHistorial.removeAllViews();
        contenedorHistorial.addView(crearFilaHistorial("Cargando historial", "Firebase", ""));

        SgiFirebase.list(this, SgiFirebase.LEVANTAMIENTOS, new SgiFirebase.Callback<List<DocumentSnapshot>>() {
            @Override
            public void onSuccess(List<DocumentSnapshot> documentos) {
                contenedorHistorial.removeAllViews();
                List<DocumentSnapshot> historial = new ArrayList<>(documentos);
                Collections.sort(historial, new Comparator<DocumentSnapshot>() {
                    @Override
                    public int compare(DocumentSnapshot left, DocumentSnapshot right) {
                        return Long.compare(fechaOrdenable(right), fechaOrdenable(left));
                    }
                });

                int agregados = 0;
                for (DocumentSnapshot levantamiento : historial) {
                    String fecha = formatearFechaVisible(SgiFirebase.string(levantamiento, "fecha_levantamiento"));
                    String serie = SgiFirebase.string(levantamiento, "numero_serie");
                    String alcance = serie.isEmpty() ? "Inventario completo" : "Serie " + serie;
                    String observaciones = SgiFirebase.string(levantamiento, "observaciones");
                    if (observaciones.isEmpty()) {
                        observaciones = "Sin observaciones";
                    }

                    contenedorHistorial.addView(crearFilaHistorial(fecha, alcance, observaciones));
                    agregados++;
                    if (agregados >= 8) {
                        break;
                    }
                }

                if (contenedorHistorial.getChildCount() == 0) {
                    contenedorHistorial.addView(crearFilaHistorial(
                            "Sin auditorias registradas", "Firebase", ""));
                }
            }

            @Override
            public void onError(Exception exception) {
                contenedorHistorial.removeAllViews();
                contenedorHistorial.addView(crearFilaHistorial(
                        "No se pudo cargar historial", exception.getMessage(), ""));
            }
        });
    }

    private View crearFilaHistorial(String fecha, String alcance, String observaciones) {
        LinearLayout fila = new LinearLayout(this);
        fila.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT));
        fila.setGravity(Gravity.CENTER_VERTICAL);
        fila.setOrientation(LinearLayout.HORIZONTAL);
        fila.setPadding(0, dp(5), 0, dp(5));

        LinearLayout textos = new LinearLayout(this);
        textos.setLayoutParams(new LinearLayout.LayoutParams(
                0,
                LinearLayout.LayoutParams.WRAP_CONTENT,
                1));
        textos.setOrientation(LinearLayout.VERTICAL);

        TextView titulo = new TextView(this);
        titulo.setIncludeFontPadding(false);
        titulo.setText(fecha);
        titulo.setTextColor(Color.parseColor("#1A1A1A"));
        titulo.setTextSize(9);

        TextView subtitulo = new TextView(this);
        subtitulo.setIncludeFontPadding(false);
        subtitulo.setText(alcance);
        subtitulo.setTextColor(Color.parseColor("#444444"));
        subtitulo.setTextSize(7);

        TextView detalle = new TextView(this);
        detalle.setLayoutParams(new LinearLayout.LayoutParams(
                dp(128),
                LinearLayout.LayoutParams.WRAP_CONTENT));
        detalle.setGravity(Gravity.END);
        detalle.setText(observaciones);
        detalle.setTextColor(Color.parseColor("#555555"));
        detalle.setTextSize(7);
        detalle.setMaxLines(2);

        textos.addView(titulo);
        textos.addView(subtitulo);
        fila.addView(textos);
        fila.addView(detalle);

        return fila;
    }

    private void finalizarLevantamiento() {
        String fecha = etFechaLevantamiento.getText().toString().trim();
        String numeroSerie = etNumeroSerie.getText().toString().trim();

        if (fecha.isEmpty()) {
            Toast.makeText(this, "Seleccione la fecha de levantamiento", Toast.LENGTH_SHORT).show();
            return;
        }

        Map<String, Object> levantamiento = SgiFirebase.values();
        levantamiento.put("fecha_levantamiento", fecha);
        levantamiento.put("numero_serie", numeroSerie);
        levantamiento.put("observaciones", "Auditoria fisica de activos");

        SgiFirebase.add(this, SgiFirebase.LEVANTAMIENTOS, levantamiento,
                new SgiFirebase.Callback<String>() {
                    @Override
                    public void onSuccess(String id) {
                        actualizarEquiposLevantados(fecha, numeroSerie);
                    }

                    @Override
                    public void onError(Exception exception) {
                        mostrarErrorFirebase(exception);
                    }
                });
    }

    private void actualizarEquiposLevantados(String fecha, String numeroSerie) {
        SgiFirebase.list(this, SgiFirebase.EQUIPOS, new SgiFirebase.Callback<List<DocumentSnapshot>>() {
            @Override
            public void onSuccess(List<DocumentSnapshot> documentos) {
                List<DocumentSnapshot> objetivos = new ArrayList<>();
                for (DocumentSnapshot documento : documentos) {
                    if (numeroSerie.isEmpty()
                            || SgiFirebase.string(documento, "numero_serie").equals(numeroSerie)) {
                        objetivos.add(documento);
                    }
                }

                if (objetivos.isEmpty()) {
                    Toast.makeText(LevantamientoFisicoActivity.this,
                            "Levantamiento registrado, sin equipos coincidentes",
                            Toast.LENGTH_SHORT).show();
                    finish();
                    return;
                }

                actualizarSiguienteEquipo(objetivos, 0, fecha);
            }

            @Override
            public void onError(Exception exception) {
                mostrarErrorFirebase(exception);
            }
        });
    }

    private void actualizarSiguienteEquipo(List<DocumentSnapshot> objetivos, int indice, String fecha) {
        if (indice >= objetivos.size()) {
            Toast.makeText(this, "Levantamiento fisico finalizado en Firebase", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        Map<String, Object> equipo = SgiFirebase.values();
        equipo.put("fecha_ultimo_levantamiento", fecha);
        SgiFirebase.update(this, SgiFirebase.EQUIPOS, objetivos.get(indice).getId(), equipo,
                new SgiFirebase.Callback<String>() {
                    @Override
                    public void onSuccess(String id) {
                        actualizarSiguienteEquipo(objetivos, indice + 1, fecha);
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
                (view, year, month, dayOfMonth) -> etFechaLevantamiento.setText(
                        String.format(Locale.US, "%04d-%02d-%02d", year, month + 1, dayOfMonth)),
                calendario.get(Calendar.YEAR),
                calendario.get(Calendar.MONTH),
                calendario.get(Calendar.DAY_OF_MONTH));
        dialog.show();
    }

    private String formatearFechaActual() {
        return new SimpleDateFormat("yyyy-MM-dd", Locale.US).format(new Date());
    }

    private String formatearFechaVisible(String fecha) {
        if (fecha == null || fecha.trim().isEmpty()) {
            return "Sin fecha";
        }

        try {
            Date date = new SimpleDateFormat("yyyy-MM-dd", Locale.US).parse(fecha);
            return new SimpleDateFormat("dd/MM/yyyy", Locale.US).format(date);
        } catch (ParseException exception) {
            return fecha;
        }
    }

    private long fechaOrdenable(DocumentSnapshot document) {
        String fecha = SgiFirebase.string(document, "fecha_levantamiento");
        try {
            Date date = new SimpleDateFormat("yyyy-MM-dd", Locale.US).parse(fecha);
            return date == null ? 0 : date.getTime();
        } catch (ParseException exception) {
            return 0;
        }
    }

    private void mostrarErrorFirebase(Exception exception) {
        Toast.makeText(this, "No se pudo actualizar Firebase: " + exception.getMessage(),
                Toast.LENGTH_LONG).show();
    }

    private int dp(int valor) {
        return (int) (valor * getResources().getDisplayMetrics().density + 0.5f);
    }
}
