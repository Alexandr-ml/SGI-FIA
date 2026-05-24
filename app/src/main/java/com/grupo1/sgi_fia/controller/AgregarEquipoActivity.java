package com.grupo1.sgi_fia.controller;

import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.grupo1.sgi_fia.R;
import com.grupo1.sgi_fia.model.Equipo;
import com.grupo1.sgi_fia.utils.BaseDeDatosLocal;
import com.grupo1.sgi_fia.utils.RetrofitClient;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import android.content.Intent;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AgregarEquipoActivity extends AppCompatActivity {

    private EditText etNumeroSerie, etMarca, etModelo, etUbicacion, etCostoUnidad, etDescripcion, etUnidades, etUltimoLevantamiento;
    private BaseDeDatosLocal db;
    private int equipoId = -1; // -1 indica nuevo equipo, >0 indica edición

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_agregar_equipo);

        db = BaseDeDatosLocal.getDatabase(this);

        etNumeroSerie = findViewById(R.id.etNumeroSerie);
        etMarca = findViewById(R.id.etMarca);
        etModelo = findViewById(R.id.etModelo);
        etUbicacion = findViewById(R.id.etUbicacion);
        etCostoUnidad = findViewById(R.id.etCostoUnidad);
        etDescripcion = findViewById(R.id.etDescripcion);
        etUnidades = findViewById(R.id.etUnidades);
        etUltimoLevantamiento = findViewById(R.id.etUltimoLevantamiento);

        // Revisar si venimos de modo edición
        if (getIntent().hasExtra("equipo_id")) {
            equipoId = getIntent().getIntExtra("equipo_id", -1);
            cargarDatosEquipo(equipoId);
            
            Button btnEliminar = findViewById(R.id.btnEliminar);
            btnEliminar.setVisibility(View.VISIBLE);
            btnEliminar.setOnClickListener(v -> eliminarEquipo());
        }

        Button btnEscanear = findViewById(R.id.btnEscanear);
        btnEscanear.setOnClickListener(v -> iniciarEscaneo());

        Button btnCancelar = findViewById(R.id.btnCancelar);
        btnCancelar.setOnClickListener(v -> finish());

        Button btnAnadir = findViewById(R.id.btnAnadir);
        if (equipoId != -1) btnAnadir.setText("Modificar"); // Cambiar texto si es edición
        btnAnadir.setOnClickListener(v -> guardarEquipo());
    }

    private void cargarDatosEquipo(int id) {
        // Obtenemos los datos actuales para mostrarlos en el formulario
        Equipo e = db.equipoDao().getById(id);
        if (e != null) {
            etNumeroSerie.setText(e.numero_serie);
            etMarca.setText(e.marca);
            etModelo.setText(e.modelo);
            etUbicacion.setText(e.ubicacion);
            etCostoUnidad.setText(String.valueOf(e.costo_unidad));
            etDescripcion.setText(e.descripcion);
            etUnidades.setText(String.valueOf(e.unidades));
            if (e.fecha_ultimo_levantamiento != null) {
                etUltimoLevantamiento.setText(e.fecha_ultimo_levantamiento);
            } else {
                etUltimoLevantamiento.setText("Sin levantamiento");
            }
        }
    }

    private void eliminarEquipo() {
        if (equipoId != -1) {
            Equipo e = db.equipoDao().getById(equipoId);
            if (e != null) {
                db.equipoDao().delete(e);
                Toast.makeText(this, "Equipo eliminado correctamente", Toast.LENGTH_SHORT).show();
                finish();
            }
        }
    }

    private void iniciarEscaneo() {
        IntentIntegrator integrator = new IntentIntegrator(this);
        integrator.setDesiredBarcodeFormats(IntentIntegrator.ALL_CODE_TYPES);
        integrator.setPrompt("Escaneando código de barras del equipo");
        integrator.setCameraId(0);
        integrator.setBeepEnabled(true);
        integrator.setBarcodeImageEnabled(true);
        integrator.initiateScan();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if (result != null) {
            if (result.getContents() == null) {
                Toast.makeText(this, "Escaneo cancelado", Toast.LENGTH_LONG).show();
            } else {
                etNumeroSerie.setText(result.getContents());
                Toast.makeText(this, "Código escaneado: " + result.getContents(), Toast.LENGTH_LONG).show();
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    private void guardarEquipo() {
        String serie = etNumeroSerie.getText().toString();
        String marca = etMarca.getText().toString();
        String modelo = etModelo.getText().toString();
        String ubicacion = etUbicacion.getText().toString();
        String costoStr = etCostoUnidad.getText().toString();
        String descripcion = etDescripcion.getText().toString();
        String unidadesStr = etUnidades.getText().toString();
        int unidades = unidadesStr.isEmpty() ? 1 : Integer.parseInt(unidadesStr);

        if (serie.isEmpty() || marca.isEmpty() || modelo.isEmpty()) {
            Toast.makeText(this, "Por favor complete los campos obligatorios", Toast.LENGTH_SHORT).show();
            return;
        }

        Equipo equipo = new Equipo();
        if (equipoId != -1) {
            Equipo existente = db.equipoDao().getById(equipoId);
            if (existente != null) {
                equipo = existente;
            } else {
                equipo.id = equipoId;
            }
        }
        
        equipo.numero_serie = serie;
        equipo.marca = marca;
        equipo.modelo = modelo;
        equipo.ubicacion = ubicacion;
        equipo.costo_unidad = costoStr.isEmpty() ? 0.0 : Double.parseDouble(costoStr);
        equipo.unidades = unidades;
        equipo.descripcion = descripcion;
        equipo.estado = "Disponible";
        equipo.nombre = marca + " " + modelo;

        if (equipoId == -1) {
            db.equipoDao().insert(equipo);
            Toast.makeText(this, "Equipo guardado correctamente", Toast.LENGTH_SHORT).show();
        } else {
            db.equipoDao().update(equipo);
            Toast.makeText(this, "Equipo modificado correctamente", Toast.LENGTH_SHORT).show();
        }
        
        sincronizarConServidor(equipo);
        finish();
    }

    private void sincronizarConServidor(Equipo equipo) {
        RetrofitClient.getApiService().saveEquipoRemoto(equipo).enqueue(new Callback<Equipo>() {
            @Override
            public void onResponse(Call<Equipo> call, Response<Equipo> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(AgregarEquipoActivity.this, "Sincronizado con el servidor", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Equipo> call, Throwable t) {
                // Si falla, el dato ya está guardado localmente (SQLite) como pide la guía
            }
        });
    }
}