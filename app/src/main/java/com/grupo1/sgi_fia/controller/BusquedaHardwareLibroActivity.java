package com.grupo1.sgi_fia.controller;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.Gravity;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.grupo1.sgi_fia.AdminSQLiteOpenHelper;
import com.grupo1.sgi_fia.R;

public class BusquedaHardwareLibroActivity extends AppCompatActivity {

    private EditText etBusqueda;
    private LinearLayout contenedorResultados;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_busqueda_hardware_libro);

        etBusqueda = findViewById(R.id.etBusquedaHardwareLibro);
        contenedorResultados = findViewById(R.id.contenedorResultadosBusqueda);
        TextView btnSubmit = findViewById(R.id.btnSubmitBusqueda);

        btnSubmit.setOnClickListener(view -> cargarResultados());
        etBusqueda.setOnEditorActionListener((view, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                cargarResultados();
                return true;
            }
            return false;
        });

        cargarResultados();
    }

    private void cargarResultados() {
        String termino = etBusqueda.getText().toString().trim();
        contenedorResultados.removeAllViews();

        if (termino.isEmpty()) {
            agregarTarjeta("Libro o tesis", "descripcion del libro", R.drawable.ic_book_24);
            agregarTarjeta("Hardware", "descripcion del hardware", R.drawable.ic_hardware_24);
            return;
        }

        boolean tieneResultados = cargarDocumentos(termino);
        tieneResultados = cargarEquipos(termino) || tieneResultados;

        if (!tieneResultados) {
            agregarTarjeta("Libro o tesis", "descripcion del libro", R.drawable.ic_book_24);
            agregarTarjeta("Hardware", "descripcion del hardware", R.drawable.ic_hardware_24);
        }
    }

    private boolean cargarDocumentos(String termino) {
        AdminSQLiteOpenHelper admin = new AdminSQLiteOpenHelper(
                this,
                AdminSQLiteOpenHelper.NOMBRE_BD,
                null,
                AdminSQLiteOpenHelper.VERSION_BD);
        SQLiteDatabase db = admin.getReadableDatabase();
        String filtro = "%" + termino + "%";
        Cursor cursor = db.rawQuery(
                "SELECT titulo, tipo FROM documentos WHERE titulo LIKE ? OR tipo LIKE ? ORDER BY titulo",
                new String[]{filtro, filtro});

        boolean tieneResultados = false;
        while (cursor.moveToNext()) {
            String titulo = cursor.getString(0);
            String tipo = cursor.getString(1);
            agregarTarjeta("Libro o tesis", tipo + ": " + titulo, R.drawable.ic_book_24);
            tieneResultados = true;
        }

        cursor.close();
        db.close();
        return tieneResultados;
    }

    private boolean cargarEquipos(String termino) {
        AdminSQLiteOpenHelper admin = new AdminSQLiteOpenHelper(
                this,
                AdminSQLiteOpenHelper.NOMBRE_BD,
                null,
                AdminSQLiteOpenHelper.VERSION_BD);
        SQLiteDatabase db = admin.getReadableDatabase();
        String filtro = "%" + termino + "%";
        Cursor cursor = db.rawQuery(
                "SELECT nombre, modelo FROM equipos_informaticos WHERE nombre LIKE ? OR modelo LIKE ? ORDER BY nombre",
                new String[]{filtro, filtro});

        boolean tieneResultados = false;
        while (cursor.moveToNext()) {
            String nombre = cursor.getString(0);
            String modelo = cursor.getString(1);
            agregarTarjeta("Hardware", nombre + " - Modelo " + modelo, R.drawable.ic_hardware_24);
            tieneResultados = true;
        }

        cursor.close();
        db.close();
        return tieneResultados;
    }

    private void agregarTarjeta(String titulo, String descripcion, int icono) {
        LinearLayout tarjeta = new LinearLayout(this);
        LinearLayout.LayoutParams tarjetaParams = new LinearLayout.LayoutParams(
                dp(136),
                LinearLayout.LayoutParams.WRAP_CONTENT);
        tarjetaParams.setMargins(dp(7), 0, 0, dp(2));
        tarjeta.setLayoutParams(tarjetaParams);
        tarjeta.setBackgroundResource(R.drawable.bg_card_compact);
        tarjeta.setOrientation(LinearLayout.VERTICAL);
        tarjeta.setPadding(dp(10), dp(10), dp(10), dp(12));

        ImageView imagen = new ImageView(this);
        LinearLayout.LayoutParams imagenParams = new LinearLayout.LayoutParams(dp(76), dp(76));
        imagen.setLayoutParams(imagenParams);
        imagen.setBackgroundResource(R.drawable.bg_placeholder_image);
        imagen.setImageResource(R.drawable.ic_placeholder_image);
        imagen.setPadding(dp(8), dp(8), dp(8), dp(8));
        imagen.setContentDescription(titulo);

        TextView tituloView = new TextView(this);
        LinearLayout.LayoutParams tituloParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        tituloParams.setMargins(0, dp(12), 0, 0);
        tituloView.setLayoutParams(tituloParams);
        tituloView.setIncludeFontPadding(false);
        tituloView.setText(titulo);
        tituloView.setTextColor(0xFF1A1A1A);
        tituloView.setTextSize(15);
        tituloView.setTypeface(tituloView.getTypeface(), android.graphics.Typeface.BOLD);

        TextView descripcionView = new TextView(this);
        LinearLayout.LayoutParams descripcionParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        descripcionParams.setMargins(0, dp(6), 0, 0);
        descripcionView.setLayoutParams(descripcionParams);
        descripcionView.setIncludeFontPadding(false);
        descripcionView.setText(descripcion);
        descripcionView.setTextColor(0xFF777777);
        descripcionView.setTextSize(10);

        tarjeta.setGravity(Gravity.START);
        tarjeta.addView(imagen);
        tarjeta.addView(tituloView);
        tarjeta.addView(descripcionView);
        contenedorResultados.addView(tarjeta);
    }

    private int dp(int valor) {
        return (int) (valor * getResources().getDisplayMetrics().density + 0.5f);
    }
}
