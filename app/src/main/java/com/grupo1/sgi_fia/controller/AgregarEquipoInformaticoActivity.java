package com.grupo1.sgi_fia.controller;

import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.grupo1.sgi_fia.R;
import com.grupo1.sgi_fia.data.SgiFirebase;

import java.util.Map;

public class AgregarEquipoInformaticoActivity extends AppCompatActivity {

    private EditText etNumeroSerie;
    private EditText etMarca;
    private EditText etModelo;
    private EditText etUbicacion;
    private EditText etCosto;
    private EditText etDescripcion;
    private Spinner spinnerUnidades;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_agregar_equipo_informatico);

        etNumeroSerie = findViewById(R.id.etNumeroSerieEquipoInventario);
        etMarca = findViewById(R.id.etMarcaEquipoInventario);
        etModelo = findViewById(R.id.etModeloEquipoInventario);
        etUbicacion = findViewById(R.id.etUbicacionEquipoInventario);
        etCosto = findViewById(R.id.etCostoEquipoInventario);
        etDescripcion = findViewById(R.id.etDescripcionEquipoInventario);
        spinnerUnidades = findViewById(R.id.spinnerUnidadesEquipoInventario);
        View btnEscanear = findViewById(R.id.btnEscanearEquipoInventario);
        View btnAnadir = findViewById(R.id.btnAnadirEquipoInventario);
        View btnCancelar = findViewById(R.id.btnCancelarEquipoInventario);

        configurarUnidades();
        btnEscanear.setOnClickListener(view ->
                Toast.makeText(this, "Escaneo de numero de serie pendiente", Toast.LENGTH_SHORT).show());
        btnAnadir.setOnClickListener(view -> guardarEquipo());
        btnCancelar.setOnClickListener(view -> finish());
    }

    private void configurarUnidades() {
        String[] unidades = {"1", "2", "3", "4", "5", "6", "7", "8", "9", "10"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_dropdown_item,
                unidades);
        spinnerUnidades.setAdapter(adapter);
    }

    private void guardarEquipo() {
        String numeroSerie = obtenerTexto(etNumeroSerie);
        String marca = obtenerTexto(etMarca);
        String modelo = obtenerTexto(etModelo);
        String ubicacion = obtenerTexto(etUbicacion);
        String costoTexto = obtenerTexto(etCosto).replace("$", "").trim();
        String descripcion = obtenerTexto(etDescripcion);
        int unidades = Integer.parseInt(spinnerUnidades.getSelectedItem().toString());

        if (numeroSerie.isEmpty() || marca.isEmpty() || modelo.isEmpty()) {
            Toast.makeText(this, "Complete numero de serie, marca y modelo", Toast.LENGTH_SHORT).show();
            return;
        }

        double costoUnidad;
        try {
            costoUnidad = costoTexto.isEmpty() ? 0 : Double.parseDouble(costoTexto.replace(",", "."));
        } catch (NumberFormatException exception) {
            Toast.makeText(this, "Ingrese un costo valido", Toast.LENGTH_SHORT).show();
            return;
        }

        Map<String, Object> equipo = SgiFirebase.values();
        equipo.put("nombre", marca + " " + modelo);
        equipo.put("numero_serie", numeroSerie);
        equipo.put("marca", marca);
        equipo.put("modelo", modelo);
        equipo.put("ubicacion", ubicacion);
        equipo.put("costo_unidad", costoUnidad);
        equipo.put("unidades", unidades);
        equipo.put("descripcion", descripcion);
        equipo.put("estado_funcional", "Activo");
        equipo.put("estado_prestamo", "Disponible");

        SgiFirebase.add(this, SgiFirebase.EQUIPOS, equipo, new SgiFirebase.Callback<String>() {
            @Override
            public void onSuccess(String id) {
                limpiarFormulario();
                Toast.makeText(AgregarEquipoInformaticoActivity.this,
                        "Equipo informatico agregado en Firebase", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onError(Exception exception) {
                Toast.makeText(AgregarEquipoInformaticoActivity.this,
                        "No se pudo guardar en Firebase: " + exception.getMessage(),
                        Toast.LENGTH_LONG).show();
            }
        });
    }

    private void limpiarFormulario() {
        etNumeroSerie.setText("");
        etMarca.setText("");
        etModelo.setText("");
        etUbicacion.setText("");
        etCosto.setText("");
        etDescripcion.setText("");
        spinnerUnidades.setSelection(0);
    }

    private String obtenerTexto(EditText editText) {
        return editText.getText().toString().trim();
    }
}
