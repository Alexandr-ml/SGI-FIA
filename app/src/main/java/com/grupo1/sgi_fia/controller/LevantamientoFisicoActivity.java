package com.grupo1.sgi_fia.controller;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import com.grupo1.sgi_fia.R;
import com.grupo1.sgi_fia.AdminSQLiteOpenHelper;

public class LevantamientoFisicoActivity extends AppCompatActivity {

    private Spinner spinnerPeriodo, spinnerUbicacion;
    private EditText editBuscar, editObservaciones;
    private TextView txtInfoEquipo, txtResumen;
    private RadioGroup radioGroupEstado;

    private AdminSQLiteOpenHelper adminHelper;
    private int idHardwareEncontrado = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_levantamiento_fisico);

        // Inicializamos la base de datos con la versión 3 fija
        adminHelper = new AdminSQLiteOpenHelper(
                this,
                AdminSQLiteOpenHelper.NOMBRE_BD,
                null,
                AdminSQLiteOpenHelper.VERSION_BD);

        // Enlace de componentes mapeados perfectamente con el XML
        spinnerPeriodo = findViewById(R.id.spinner_periodo_auditoria);
        spinnerUbicacion = findViewById(R.id.spinner_ubicacion_auditoria);
        editBuscar = findViewById(R.id.edit_buscar_auditoria);
        editObservaciones = findViewById(R.id.edit_observaciones_auditoria);
        txtInfoEquipo = findViewById(R.id.txt_info_equipo_auditoria);
        txtResumen = findViewById(R.id.txt_resumen_auditoria);
        radioGroupEstado = findViewById(R.id.radio_group_estado_auditoria);

        // 🔥 Forzar que los componentes respondan a los toques del dedo
        spinnerPeriodo.setFocusable(true);
        spinnerPeriodo.setClickable(true);
        spinnerUbicacion.setFocusable(true);
        spinnerUbicacion.setClickable(true);
        radioGroupEstado.setFocusable(true);

        Button btnRegresar = findViewById(R.id.btn_regresar_auditoria);
        Button btnGuardar = findViewById(R.id.btn_guardar_auditoria);

        // Spinners con adaptadores de datos
        String[] periodos = {"Periodo 2026", "Periodo 2027", "Periodo 2028"};
        ArrayAdapter<String> adapterPeriodo = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, periodos);
        adapterPeriodo.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerPeriodo.setAdapter(adapterPeriodo);

        String[] ubicaciones = {"Cubículo 1 Ing. Industrial", "Jefatura Ing. de Sistemas", "Secretaría Ing. Mecánica", "Dirección Ing. Civil"};
        ArrayAdapter<String> adapterUbicacion = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, ubicaciones);
        adapterUbicacion.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerUbicacion.setAdapter(adapterUbicacion);

        // Escucha de teclado para búsqueda en tiempo real por número de serie
        editBuscar.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                buscarEquipoPorSerie(s.toString().trim());
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        // Eventos de los botones controlados limpiamente
        btnRegresar.setOnClickListener(v -> finish());
        btnGuardar.setOnClickListener(v -> guardarRegistroAuditoria());
    }

    @SuppressLint("SetTextI18n")
    private void buscarEquipoPorSerie(String serie) {
        if (serie.isEmpty()) {
            txtInfoEquipo.setText("Detalles del Equipo:\nEsperando búsqueda de activo...");
            idHardwareEncontrado = -1;
            return;
        }

        SQLiteDatabase db = adminHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT id_hardware, marca, modelo, responsable FROM hardware WHERE numero_serie = ?", new String[]{serie});

        if (cursor.moveToFirst()) {
            idHardwareEncontrado = cursor.getInt(0);
            String marca = cursor.getString(1);
            String modelo = cursor.getString(2);
            String responsable = cursor.getString(3);

            txtInfoEquipo.setText("📋 ACTIVO DETECTADO:\n" +
                    "• Marca: " + marca + "\n" +
                    "• Modelo: " + modelo + "\n" +
                    "• Asignado a: " + responsable);
        } else {
            txtInfoEquipo.setText("❌ No se encontró ningún equipo con esa serie.");
            idHardwareEncontrado = -1;
        }
        cursor.close();
        db.close();
    }

    @SuppressLint("SetTextI18n")
    private void guardarRegistroAuditoria() {
        String observaciones = editObservaciones.getText().toString().trim();
        String periodo = spinnerPeriodo.getSelectedItem().toString();
        String ubicacionReal = spinnerUbicacion.getSelectedItem().toString();

        if (idHardwareEncontrado == -1) {
            Toast.makeText(this, "⚠️ Primero busque un equipo válido", Toast.LENGTH_SHORT).show();
            return;
        }

        SQLiteDatabase db = adminHelper.getWritableDatabase();

        // Buscamos cuál RadioButton está seleccionado dinámicamente en el grupo
        int selectedId = radioGroupEstado.getCheckedRadioButtonId();
        RadioButton seleccionado = findViewById(selectedId);
        String estadoConfirmado = (seleccionado != null) ? seleccionado.getText().toString() : "Encontrado";

        ContentValues registro = new ContentValues();
        registro.put("id_hardware", idHardwareEncontrado);
        registro.put("anio_periodo", periodo);
        registro.put("ubicacion_encontrado", ubicacionReal);
        registro.put("estado_confirmado", estadoConfirmado);
        registro.put("observaciones", observaciones);

        long resultado = db.insert("auditorias", null, registro);
        db.close();

        if (resultado != -1) {
            Toast.makeText(this, "✅ Auditoría guardada exitosamente", Toast.LENGTH_SHORT).show();
            editBuscar.setText("");
            editObservaciones.setText("");
            txtResumen.setText("Resumen: ¡Último guardado como " + estadoConfirmado + "!");
        } else {
            Toast.makeText(this, "❌ Error al guardar en la BD", Toast.LENGTH_SHORT).show();
        }
    }
}
