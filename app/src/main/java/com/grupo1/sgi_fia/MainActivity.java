package com.grupo1.sgi_fia;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.grupo1.sgi_fia.controller.MenuActivity;
import com.grupo1.sgi_fia.data.SgiFirebase;

public class MainActivity extends AppCompatActivity {

    Button btnIniciarSesion;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        SgiFirebase.initialize(this);
        SgiFirebase.seedInitialEquipment(this);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        btnIniciarSesion = (Button) findViewById(R.id.btnIniciarSesion);

        btnIniciarSesion.setOnClickListener((view)->{

            Intent menu = new Intent(this, MenuActivity.class);
            startActivity(menu);

                }
        );

    }
}
