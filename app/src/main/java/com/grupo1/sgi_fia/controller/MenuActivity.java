package com.grupo1.sgi_fia.controller;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.grupo1.sgi_fia.R;

public class MenuActivity extends AppCompatActivity {

    private Button btnAnadirLibro, btnAnadirTesis, btnDevolucionLibro;
    private Button btnNuevoEquipo, btnLevantamiento;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // 🌟 CORRECCIÓN 1: Usar el nombre exacto de tu layout (activity_menu)
        setContentView(R.layout.menu);

        // Mapeo seguro de tus nuevos botones de hardware
        btnNuevoEquipo = findViewById(R.id.btnNuevoEquipoInformatico);
        btnLevantamiento = findViewById(R.id.btnLevantamientoFisico);

        // Evento seguro para Añadir Equipo
        if (btnNuevoEquipo != null) {
            btnNuevoEquipo.setOnClickListener(v -> {
                try {
                    Intent intent = new Intent(MenuActivity.this, AnadirEquipoActivity.class);
                    startActivity(intent);
                } catch (Exception e) {
                    Toast.makeText(MenuActivity.this, "Error al abrir Añadir Equipo", Toast.LENGTH_SHORT).show();
                }
            });
        }

        // Evento seguro para Levantamiento Físico con protección
        if (btnLevantamiento != null) {
            btnLevantamiento.setOnClickListener(v -> {
                try {
                    Intent intent = new Intent(MenuActivity.this, LevantamientoFisicoActivity.class);
                    startActivity(intent);
                } catch (Exception e) {
                    // Si el error es por la Base de datos corrupta, este mensaje te salvará avisándote
                    Toast.makeText(MenuActivity.this, "Error en Levantamiento: " + e.getMessage(), Toast.LENGTH_LONG).show();
                    e.printStackTrace();
                }
            });
        } else {
            Toast.makeText(this, "Error: No se encontró el ID btnLevantamientoFisico en el XML", Toast.LENGTH_LONG).show();
        }

        // Mapeo seguro de tus botones antiguos (Libros, Tesis, Devoluciones)
        btnAnadirLibro = findViewById(R.id.btnNuevoLibro);
        btnAnadirTesis = findViewById(R.id.btnNuevaTesis);
        btnDevolucionLibro = findViewById(R.id.btn_dev_libro);

        if (btnAnadirLibro != null) {
            btnAnadirLibro.setOnClickListener(v -> {
                Intent intent = new Intent(MenuActivity.this, RegistroDocumentosActivity.class);
                startActivity(intent);
            });
        }

        if (btnAnadirTesis != null) {
            btnAnadirTesis.setOnClickListener(v -> {
                Intent intent = new Intent(MenuActivity.this, RegistroDocumentosActivity.class);
                startActivity(intent);
            });
        }

        if (btnDevolucionLibro != null) {
            btnDevolucionLibro.setOnClickListener(v -> {
                Intent intent = new Intent(MenuActivity.this, DevolucionLibroActivity.class);
                startActivity(intent);
            });
        }
    }
}