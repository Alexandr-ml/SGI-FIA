package com.grupo1.sgi_fia.controller;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.grupo1.sgi_fia.R;

public class MenuActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.menu);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        configurarBoton(R.id.btnNuevoLibro, RegistroDocumentosActivity.class);
        configurarBoton(R.id.btnNuevaTesis, RegistroDocumentosActivity.class);
        configurarBoton(R.id.btnPrestamoPorHoras, PrestamoEquipoPorHorasActivity.class);
        configurarBoton(R.id.btnPrestamoRecurrente, PrestamoDeEquipoRecurrente.class);
        configurarBoton(R.id.btnPrestamoTesis, PrestamoTesisActivity.class);
        configurarBoton(R.id.btn_dev_libro, DevolucionLibroActivity.class);
    }

    private void configurarBoton(int idBoton, Class<?> pantalla) {
        Button boton = findViewById(idBoton);
        if (boton != null) {
            boton.setOnClickListener(view -> abrirPantalla(pantalla));
        }
    }

    private void abrirPantalla(Class<?> pantalla) {
        Intent intent = new Intent(this, pantalla);
        startActivity(intent);
    }
}
