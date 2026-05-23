package com.grupo1.sgi_fia.controller;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;


import com.grupo1.sgi_fia.R;
import com.grupo1.sgi_fia.AdminSQLiteOpenHelper;

public class AnadirEquipoActivity extends AppCompatActivity {

    private EditText editSerie, editMarca, editModelo, editCantidad, editUbicacion, editResponsable, editContacto;
    private Spinner spinnerClasificacion;
    private Button btnGuardar, btnCancelar;
    private AdminSQLiteOpenHelper adminHelper;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_anadir_equipo);

        // Conexión con la Base de Datos (Versión 3)
        adminHelper = new AdminSQLiteOpenHelper(this, "Biblioteca.db", null, 3);

        // Enlace de componentes sin errores de R
        editSerie = findViewById(R.id.edit_num_serie);
        editMarca = findViewById(R.id.edit_marca);
        editModelo = findViewById(R.id.edit_modelo);
        editCantidad = findViewById(R.id.edit_cantidad_hardware);
        editUbicacion = findViewById(R.id.edit_ubicacion);
        editResponsable = findViewById(R.id.edit_responsable);
        editContacto = findViewById(R.id.edit_contacto);
        spinnerClasificacion = findViewById(R.id.spinner_clasificacion);
        btnGuardar = findViewById(R.id.btn_guardar_equipo);
        btnCancelar = findViewById(R.id.btn_cancelar_equipo);

        // Configurar el Spinner de Clasificación
        String[] opciones = {"Cañón / Proyector", "Laptop", "PC de Escritorio", "Switch de Red", "Router", "Impresora"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, opciones);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerClasificacion.setAdapter(adapter);

        btnGuardar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                guardarNuevoEquipo();
            }
        });

        btnCancelar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void guardarNuevoEquipo() {
        String serie = editSerie.getText().toString().trim();
        String marca = editMarca.getText().toString().trim();
        String modelo = editModelo.getText().toString().trim();
        String cantStr = editCantidad.getText().toString().trim();
        String ubicacion = editUbicacion.getText().toString().trim();
        String responsable = editResponsable.getText().toString().trim();
        String contacto = editContacto.getText().toString().trim();
        String clasif = spinnerClasificacion.getSelectedItem().toString();

        if (serie.isEmpty() || marca.isEmpty() || cantStr.isEmpty()) {
            Toast.makeText(this, "⚠️ Por favor complete la Serie, Marca y Cantidad", Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            SQLiteDatabase db = adminHelper.getWritableDatabase();
            ContentValues registro = new ContentValues();

            registro.put("numero_serie", serie);
            registro.put("marca", marca);
            registro.put("modelo", modelo);
            registro.put("clasificacion", clasif);
            registro.put("ubicacion", ubicacion);
            registro.put("responsable", responsable);
            registro.put("contacto", contacto);
            registro.put("unidades", Integer.parseInt(cantStr));
            registro.put("estado", "Disponible");
            registro.put("descripcion", "Ingreso nuevo de equipo FIA");

            long result = db.insert("hardware", null, registro);
            db.close();

            if (result != -1) {
                Toast.makeText(this, "✅ Equipo registrado con éxito en la FIA", Toast.LENGTH_LONG).show();
                limpiarCampos();
            } else {
                Toast.makeText(this, "❌ Error: Es posible que esta serie ya exista", Toast.LENGTH_SHORT).show();
            }

        } catch (Exception e) {
            Toast.makeText(this, "Error técnico: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private void limpiarCampos() {
        editSerie.setText("");
        editMarca.setText("");
        editModelo.setText("");
        editCantidad.setText("");
        editUbicacion.setText("");
        editResponsable.setText("");
        editContacto.setText("");
    }
}