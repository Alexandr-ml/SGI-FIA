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

        Button btnPrestamoPorHoras = findViewById(R.id.btnPrestamoPorHoras);
        Button btnPrestamoRecurrente = findViewById(R.id.btnPrestamoRecurrente);
        Button btnPrestamoTesis = findViewById(R.id.btnPrestamoTesis);

        btnPrestamoPorHoras.setOnClickListener(view -> abrirPantalla(PrestamoEquipoPorHorasActivity.class));
        btnPrestamoRecurrente.setOnClickListener(view -> abrirPantalla(PrestamoDeEquipoRecurrente.class));
        btnPrestamoTesis.setOnClickListener(view -> abrirPantalla(PrestamoTesisActivity.class));
    }

    private void abrirPantalla(Class<?> pantalla) {
        Intent intent = new Intent(this, pantalla);
        startActivity(intent);
    }
}
