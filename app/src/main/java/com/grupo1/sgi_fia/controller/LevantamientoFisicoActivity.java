package com.grupo1.sgi_fia.controller;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.grupo1.sgi_fia.R;
import com.grupo1.sgi_fia.model.Equipo;
import com.grupo1.sgi_fia.model.Inventario;
import com.grupo1.sgi_fia.utils.BaseDeDatosLocal;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class LevantamientoFisicoActivity extends AppCompatActivity {

    private RecyclerView rvEquipos;
    private EquipoAdapter adapter;
    private BaseDeDatosLocal db;
    private EditText etNumeroSerieLev, etFechaLevantamiento;
    private List<Equipo> todosLosEquipos;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_levantamiento_fisico);

        db = BaseDeDatosLocal.getDatabase(this);
        etNumeroSerieLev = findViewById(R.id.etNumeroSerieLev);
        etFechaLevantamiento = findViewById(R.id.etFechaLevantamiento);
        rvEquipos = findViewById(R.id.rvEquipos);
        rvEquipos.setLayoutManager(new LinearLayoutManager(this));

        todosLosEquipos = db.equipoDao().getAll();
        adapter = new EquipoAdapter(todosLosEquipos, equipo -> {
            // Acción al tocar un equipo: Abrir el formulario para modificar
            Intent intent = new Intent(LevantamientoFisicoActivity.this, AgregarEquipoActivity.class);
            intent.putExtra("equipo_id", equipo.id);
            startActivity(intent);
        });
        rvEquipos.setAdapter(adapter);

        etFechaLevantamiento.setOnClickListener(v -> mostrarDatePicker());

        etNumeroSerieLev.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                filtrarEquipos(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        Button btnCancelar = findViewById(R.id.btnCancelarLev);
        btnCancelar.setOnClickListener(v -> finish());

        Button btnEscanear = findViewById(R.id.btnEscanearLev);
        btnEscanear.setOnClickListener(v -> iniciarEscaneo());

        Button btnFinalizar = findViewById(R.id.btnFinalizar);
        btnFinalizar.setOnClickListener(v -> {
            String fecha = etFechaLevantamiento.getText().toString();
            if (fecha.isEmpty()) {
                Toast.makeText(this, "Por favor seleccione la fecha del levantamiento", Toast.LENGTH_SHORT).show();
                return;
            }

            Inventario inv = new Inventario();
            inv.fecha = fecha;
            inv.descripcion = "Levantamiento físico de equipos";
            
            db.inventarioDao().insert(inv);

            // ACTUALIZAR FECHA EN TODOS LOS EQUIPOS
            if (todosLosEquipos != null) {
                for (Equipo e : todosLosEquipos) {
                    e.fecha_ultimo_levantamiento = fecha;
                    db.equipoDao().update(e);
                }
            }

            Toast.makeText(this, "Levantamiento guardado exitosamente", Toast.LENGTH_SHORT).show();
            finish();
        });
    }

    private void mostrarDatePicker() {
        final Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(this,
                (view, year1, monthOfYear, dayOfMonth) -> {
                    String fecha = dayOfMonth + "/" + (monthOfYear + 1) + "/" + year1;
                    etFechaLevantamiento.setText(fecha);
                }, year, month, day);
        datePickerDialog.show();
    }

    private void iniciarEscaneo() {
        IntentIntegrator integrator = new IntentIntegrator(this);
        integrator.setDesiredBarcodeFormats(IntentIntegrator.ALL_CODE_TYPES);
        integrator.setPrompt("Escaneando código del equipo para búsqueda");
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
                etNumeroSerieLev.setText(result.getContents());
                Toast.makeText(this, "Buscando: " + result.getContents(), Toast.LENGTH_LONG).show();
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        cargarDatos();
    }

    private void cargarDatos() {
        todosLosEquipos = db.equipoDao().getAll();
        if (todosLosEquipos.isEmpty()) {
            // Si por alguna razón está vacío, forzamos una carga rápida para que el usuario vea algo
            Toast.makeText(this, "Cargando registros de prueba...", Toast.LENGTH_SHORT).show();
            preCargarDatosManual();
            todosLosEquipos = db.equipoDao().getAll();
        }
        adapter.setEquipos(todosLosEquipos);
    }

    private void preCargarDatosManual() {
        db.equipoDao().insert(crearEquipo("SN-001", "Dell", "Latitude 5420", "Unidad de Ciencias Básicas"));
        db.equipoDao().insert(crearEquipo("SN-002", "Lenovo", "ThinkPad X1", "Cubículo 12 - Escuela de Ing. Industrial"));
        db.equipoDao().insert(crearEquipo("SN-003", "Proyector", "Spectra Q891", "Edificio B - Nivel 1 - FIA"));
        db.equipoDao().insert(crearEquipo("SN-004", "Apple", "MacBook Pro", "Laboratorio de Informática"));
        db.equipoDao().insert(crearEquipo("SN-005", "HP", "HP LaserJet", "Secretaría de Facultad"));
        db.equipoDao().insert(crearEquipo("SN-006", "Samsung", "Monitor 24\"", "Centro de Cómputo 1"));
        db.equipoDao().insert(crearEquipo("SN-007", "Apple", "iPad Air", "Biblioteca Central"));
        db.equipoDao().insert(crearEquipo("SN-008", "Dell", "Server PowerEdge", "Data Center FIA"));
        db.equipoDao().insert(crearEquipo("SN-009", "Cisco", "Switch 24 Ports", "Data Center FIA"));
        db.equipoDao().insert(crearEquipo("SN-010", "HP", "Scanner ScanJet", "Archivo Académico"));
        db.equipoDao().insert(crearEquipo("SN-011", "APC", "UPS 1500VA", "Data Center FIA"));
        db.equipoDao().insert(crearEquipo("SN-012", "Logitech", "Mouse Wireless", "Bodega Activos"));
        db.equipoDao().insert(crearEquipo("SN-013", "Razer", "Keyboard Mech", "Laboratorio de Computación"));
        db.equipoDao().insert(crearEquipo("SN-014", "Logitech", "Webcam C920", "Sala de Conferencias"));
        db.equipoDao().insert(crearEquipo("SN-015", "Seagate", "External HDD 2TB", "Unidad de Investigación"));
    }

    private Equipo crearEquipo(String sn, String marca, String modelo, String ubicacion) {
        Equipo e = new Equipo();
        e.numero_serie = sn;
        e.marca = marca;
        e.modelo = modelo;
        e.ubicacion = ubicacion;
        e.nombre = marca + " " + modelo;
        e.estado = "Disponible";
        e.clasificacion = "Activo Fijo";
        e.unidades = 1;
        e.costo_unidad = 100.0;
        return e;
    }

    private void filtrarEquipos(String texto) {
        if (todosLosEquipos == null) return;
        List<Equipo> filtrados = new ArrayList<>();
        for (Equipo equipo : todosLosEquipos) {
            if (equipo.numero_serie != null && equipo.numero_serie.toLowerCase().contains(texto.toLowerCase())) {
                filtrados.add(equipo);
            }
        }
        adapter.setEquipos(filtrados);
    }
}