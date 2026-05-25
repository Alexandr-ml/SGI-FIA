package com.grupo1.sgi_fia.controller;

import android.os.Bundle;
import android.view.Gravity;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.firestore.DocumentSnapshot;
import com.grupo1.sgi_fia.R;
import com.grupo1.sgi_fia.data.SgiFirebase;

import java.util.List;

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

        contenedorResultados.addView(textoEstado("Buscando en Firebase"));
        cargarDocumentos(termino);
    }

    private void cargarDocumentos(String termino) {
        SgiFirebase.list(this, SgiFirebase.DOCUMENTOS, new SgiFirebase.Callback<List<DocumentSnapshot>>() {
            @Override
            public void onSuccess(List<DocumentSnapshot> documentos) {
                contenedorResultados.removeAllViews();
                int encontrados = 0;
                String filtro = SgiFirebase.normalize(termino);

                for (DocumentSnapshot documento : documentos) {
                    String titulo = SgiFirebase.string(documento, "titulo");
                    String tipo = SgiFirebase.string(documento, "tipo");
                    if (SgiFirebase.normalize(titulo).contains(filtro)
                            || SgiFirebase.normalize(tipo).contains(filtro)) {
                        agregarTarjeta("Libro o tesis", tipo + ": " + titulo, R.drawable.ic_book_24);
                        encontrados++;
                    }
                }

                cargarEquipos(termino, encontrados);
            }

            @Override
            public void onError(Exception exception) {
                contenedorResultados.removeAllViews();
                contenedorResultados.addView(textoEstado("No se pudo consultar Firebase"));
            }
        });
    }

    private void cargarEquipos(String termino, int encontradosPrevios) {
        SgiFirebase.list(this, SgiFirebase.EQUIPOS, new SgiFirebase.Callback<List<DocumentSnapshot>>() {
            @Override
            public void onSuccess(List<DocumentSnapshot> equipos) {
                int encontrados = encontradosPrevios;
                String filtro = SgiFirebase.normalize(termino);

                for (DocumentSnapshot equipo : equipos) {
                    String nombre = SgiFirebase.string(equipo, "nombre");
                    String modelo = SgiFirebase.string(equipo, "modelo");
                    if (SgiFirebase.normalize(nombre).contains(filtro)
                            || SgiFirebase.normalize(modelo).contains(filtro)) {
                        agregarTarjeta("Hardware", nombre + " - Modelo " + modelo,
                                R.drawable.ic_hardware_24);
                        encontrados++;
                    }
                }

                if (encontrados == 0) {
                    agregarTarjeta("Libro o tesis", "descripcion del libro", R.drawable.ic_book_24);
                    agregarTarjeta("Hardware", "descripcion del hardware", R.drawable.ic_hardware_24);
                }
            }

            @Override
            public void onError(Exception exception) {
                if (encontradosPrevios == 0) {
                    contenedorResultados.addView(textoEstado("No se pudo consultar Firebase"));
                }
            }
        });
    }

    private TextView textoEstado(String texto) {
        TextView estado = new TextView(this);
        estado.setText(texto);
        estado.setTextColor(0xFF777777);
        estado.setTextSize(12);
        estado.setGravity(Gravity.CENTER);
        return estado;
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
