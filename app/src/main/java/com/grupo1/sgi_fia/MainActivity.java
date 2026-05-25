package com.grupo1.sgi_fia;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.grupo1.sgi_fia.controller.MenuActivity;
import com.grupo1.sgi_fia.data.SgiFirebase;

public class MainActivity extends AppCompatActivity {

    private static final String USUARIO_VALIDO = "Administracion FIA";
    private static final String CONTRASENA_VALIDA = "FIA20268";

    private EditText edTxtUsuario;
    private EditText edTxtContrasena;
    private Button btnIniciarSesion;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        edTxtUsuario = findViewById(R.id.edTxtUsuario);
        edTxtContrasena = findViewById(R.id.edTxtContrasena);
        btnIniciarSesion = findViewById(R.id.btnIniciarSesion);

        btnIniciarSesion.setOnClickListener((view) -> validarInicioSesion());
    }

    private void validarInicioSesion() {
        String usuario = edTxtUsuario.getText().toString().trim();
        String contrasena = edTxtContrasena.getText().toString().trim();

        if (!USUARIO_VALIDO.equals(usuario) || !CONTRASENA_VALIDA.equals(contrasena)) {
            Toast.makeText(this, "Usuario o contrasena incorrectos.", Toast.LENGTH_SHORT).show();
            return;
        }

        SgiFirebase.initialize(this);
        SgiFirebase.seedInitialEquipment(this);

        Intent menu = new Intent(this, MenuActivity.class);
        startActivity(menu);
    }
}
