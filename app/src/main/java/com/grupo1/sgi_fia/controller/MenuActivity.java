package com.grupo1.sgi_fia.controller;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
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

        Button btnNuevoEquipo = findViewById(R.id.btnNuevoEquipoInformatico);
        btnNuevoEquipo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MenuActivity.this, AgregarEquipoActivity.class);
                startActivity(intent);
            }
        });

        Button btnLevantamiento = findViewById(R.id.btnLevantamientoFisico);
        btnLevantamiento.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MenuActivity.this, LevantamientoFisicoActivity.class);
                startActivity(intent);
            }
        });

        Button btnPrestamoRecurrente = findViewById(R.id.btnPrestamoRecurrente);
        // Se deja habilitado con sus colores originales pero sin acción de apertura
        btnPrestamoRecurrente.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // No hace nada para que no se abra la pantalla
            }
        });
    }
}