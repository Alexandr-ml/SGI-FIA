package com.grupo1.sgi_fia.controller;

import android.app.DatePickerDialog;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
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

import com.grupo1.sgi_fia.AdminSQLiteOpenHelper;
import com.grupo1.sgi_fia.R;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class LevantamientoFisicoActivity extends AppCompatActivity {

    private EditText etFechaLevantamiento;
    private EditText etNumeroSerie;
    private LinearLayout contenedorActivos;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_levantamiento_fisico);

        etFechaLevantamiento = findViewById(R.id.etFechaLevantamientoFisico);
        etNumeroSerie = findViewById(R.id.etNumeroSerieLevantamiento);
        contenedorActivos = findViewById(R.id.contenedorActivosLevantamiento);
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
    }

    private void cargarActivos(String filtroSerie) {
        contenedorActivos.removeAllViews();

        AdminSQLiteOpenHelper admin = new AdminSQLiteOpenHelper(
                this,
                AdminSQLiteOpenHelper.NOMBRE_BD,
                null,
                AdminSQLiteOpenHelper.VERSION_BD);
        SQLiteDatabase db = admin.getReadableDatabase();
        Cursor cursor;
        if (filtroSerie.isEmpty()) {
            cursor = db.rawQuery(
                    "SELECT nombre, modelo, ubicacion, numero_serie FROM equipos_informaticos ORDER BY id_equipo LIMIT 6",
                    null);
        } else {
            String filtro = "%" + filtroSerie + "%";
            cursor = db.rawQuery(
                    "SELECT nombre, modelo, ubicacion, numero_serie FROM equipos_informaticos WHERE numero_serie LIKE ? ORDER BY id_equipo LIMIT 6",
                    new String[]{filtro});
        }

        while (cursor.moveToNext()) {
            String nombre = valor(cursor, 0);
            String modelo = valor(cursor, 1);
            String ubicacion = valor(cursor, 2);
            String titulo = nombre.isEmpty() ? modelo : nombre;
            String subtitulo = ubicacion.isEmpty() ? "Ubicacion pendiente" : ubicacion;
            contenedorActivos.addView(crearFilaActivo(titulo, subtitulo));
        }

        cursor.close();
        db.close();

        if (contenedorActivos.getChildCount() == 0) {
            contenedorActivos.addView(crearFilaActivo("Sin activos registrados", "Agregue equipos al inventario"));
        }
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

    private void finalizarLevantamiento() {
        String fecha = etFechaLevantamiento.getText().toString().trim();
        String numeroSerie = etNumeroSerie.getText().toString().trim();

        if (fecha.isEmpty()) {
            Toast.makeText(this, "Seleccione la fecha de levantamiento", Toast.LENGTH_SHORT).show();
            return;
        }

        AdminSQLiteOpenHelper admin = new AdminSQLiteOpenHelper(
                this,
                AdminSQLiteOpenHelper.NOMBRE_BD,
                null,
                AdminSQLiteOpenHelper.VERSION_BD);
        SQLiteDatabase db = admin.getWritableDatabase();

        ContentValues levantamiento = new ContentValues();
        levantamiento.put("fecha_levantamiento", fecha);
        levantamiento.put("numero_serie", numeroSerie);
        levantamiento.put("observaciones", "Auditoria fisica de activos");
        db.insert("levantamientos_fisicos", null, levantamiento);

        if (numeroSerie.isEmpty()) {
            db.execSQL("UPDATE equipos_informaticos SET fecha_ultimo_levantamiento = ?", new Object[]{fecha});
        } else {
            ContentValues equipo = new ContentValues();
            equipo.put("fecha_ultimo_levantamiento", fecha);
            db.update("equipos_informaticos", equipo, "numero_serie = ?", new String[]{numeroSerie});
        }

        db.close();
        Toast.makeText(this, "Levantamiento fisico finalizado", Toast.LENGTH_SHORT).show();
        finish();
    }

    private void mostrarSelectorFecha() {
        Calendar calendario = Calendar.getInstance();
        DatePickerDialog dialog = new DatePickerDialog(
                this,
                (view, year, month, dayOfMonth) -> etFechaLevantamiento.setText(
                        String.format(Locale.US, "%02d/%02d/%04d", dayOfMonth, month + 1, year)),
                calendario.get(Calendar.YEAR),
                calendario.get(Calendar.MONTH),
                calendario.get(Calendar.DAY_OF_MONTH));
        dialog.show();
    }

    private String valor(Cursor cursor, int indice) {
        return cursor.isNull(indice) ? "" : cursor.getString(indice);
    }

    private String formatearFechaActual() {
        return new SimpleDateFormat("dd/MM/yyyy", Locale.US).format(new Date());
    }

    private int dp(int valor) {
        return (int) (valor * getResources().getDisplayMetrics().density + 0.5f);
    }
}
