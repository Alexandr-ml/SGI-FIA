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
    private Button btnNuevoEquipo, btnLevantamiento, btnRegistrosAuditoria;
    private Button btnPrestamoRecurrente;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // 1. Primero se asigna la vista (el XML) para que Android sepa qué botones existen
        setContentView(R.layout.menu);

        // 2. Ahora sí mapeamos todos los botones de forma segura
        btnRegistrosAuditoria = findViewById(R.id.btnRegistrosAuditoria);
        btnNuevoEquipo = findViewById(R.id.btnNuevoEquipoInformatico);
        btnLevantamiento = findViewById(R.id.btnLevantamientoFisico);
        btnAnadirLibro = findViewById(R.id.btnNuevoLibro);
        btnDevolucionLibro = findViewById(R.id.btn_dev_libro);

        btnPrestamoRecurrente = findViewById(R.id.btnPrestamoRecurrente);


        // Evento seguro para el Historial de Auditorías
        if (btnRegistrosAuditoria != null) {
            btnRegistrosAuditoria.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        Intent intent = new Intent(MenuActivity.this, HistorialAuditoriaActivity.class);
                        startActivity(intent);
                    } catch (Exception e) {
                        Toast.makeText(MenuActivity.this, "Error al abrir Historial: " + e.getMessage(), Toast.LENGTH_LONG).show();
                    }
                }
            });
        } else {
            Toast.makeText(this, "Error: No se encontró el ID btnRegistrosAuditoria en el XML", Toast.LENGTH_LONG).show();
        }

        // Evento seguro para el Historial de Auditorías
        if (btnRegistrosAuditoria != null) {
            btnRegistrosAuditoria.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        Intent intent = new Intent(MenuActivity.this, HistorialAuditoriaActivity.class);
                        startActivity(intent);
                    } catch (Exception e) {
                        Toast.makeText(MenuActivity.this, "Error al abrir Historial: " + e.getMessage(), Toast.LENGTH_LONG).show();
                    }
                }
            });
        } else {
            Toast.makeText(this, "Error: No se encontró el ID btnRegistrosAuditoria en el XML", Toast.LENGTH_LONG).show();
        }

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

        // Evento seguro para Levantamiento Físico
        if (btnLevantamiento != null) {
            btnLevantamiento.setOnClickListener(v -> {
                try {
                    Intent intent = new Intent(MenuActivity.this, LevantamientoFisicoActivity.class);
                    startActivity(intent);
                } catch (Exception e) {
                    Toast.makeText(MenuActivity.this, "Error en Levantamiento: " + e.getMessage(), Toast.LENGTH_LONG).show();
                    e.printStackTrace();
                }
            });
        }

        // Eventos para Libros y Devoluciones
        if (btnAnadirLibro != null) {
            btnAnadirLibro.setOnClickListener(v -> {
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



        if (btnPrestamoRecurrente != null) {
            btnPrestamoRecurrente.setOnClickListener(v -> {
                try {
                    Intent intent = new Intent(MenuActivity.this, PrestamoDeEquipoRecurrente.class);
                    startActivity(intent);
                } catch (Exception e) {
                    Toast.makeText(MenuActivity.this, "Error al abrir Préstamo: " + e.getMessage(), Toast.LENGTH_LONG).show();
                }
            });
        }
    }
}