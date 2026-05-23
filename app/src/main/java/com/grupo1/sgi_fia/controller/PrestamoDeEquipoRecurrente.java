package com.grupo1.sgi_fia.controller;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.grupo1.sgi_fia.AdminSQLiteOpenHelper;
import com.grupo1.sgi_fia.R;

public class PrestamoDeEquipoRecurrente extends AppCompatActivity {

    private EditText etResponsable, etContacto, etMateriaHorario, etFechaInicio, etFechaFin, etSerieEquipo;
    private Button btnRegistrar, btnCancelar;
    private AdminSQLiteOpenHelper adminHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_prestamo_de_equipo_recurrente);

        adminHelper = new AdminSQLiteOpenHelper(this, "Biblioteca.db", null, 3);

        etResponsable = findViewById(R.id.etResponsablePrestamo);
        etContacto = findViewById(R.id.etContactoPrestamo);
        etMateriaHorario = findViewById(R.id.etMateriaHorario);
        etFechaInicio = findViewById(R.id.etFechaInicio);
        etFechaFin = findViewById(R.id.etFechaFin);
        etSerieEquipo = findViewById(R.id.etSerieEquipo);
        btnRegistrar = findViewById(R.id.btnRegistrarPrestamo);
        btnCancelar = findViewById(R.id.btnCancelarPrestamo);

        btnCancelar.setOnClickListener(v -> finish());
        btnRegistrar.setOnClickListener(v -> procesarPrestamoConExistencia());
    }

    private void procesarPrestamoConExistencia() {
        String responsable = etResponsable.getText().toString().trim();
        String contacto = etContacto.getText().toString().trim();
        String materiaHorario = etMateriaHorario.getText().toString().trim();
        String fechaInicio = etFechaInicio.getText().toString().trim();
        String fechaFin = etFechaFin.getText().toString().trim();
        String serie = etSerieEquipo.getText().toString().trim();

        if (responsable.isEmpty() || serie.isEmpty() || fechaInicio.isEmpty() || fechaFin.isEmpty()) {
            Toast.makeText(this, "⚠️ Por favor, llena los campos obligatorios", Toast.LENGTH_SHORT).show();
            return;
        }

        SQLiteDatabase db = adminHelper.getWritableDatabase();
        Cursor cursor = null;

        try {
            // 🔍 PASO 1: Buscar el equipo por número de serie
            String queryExistencia = "SELECT id_hardware, estado, marca, modelo, unidades FROM hardware WHERE numero_serie = ?";
            cursor = db.rawQuery(queryExistencia, new String[]{serie});

            if (!cursor.moveToFirst()) {
                Toast.makeText(this, "❌ Error: La serie '" + serie + "' no existe.", Toast.LENGTH_LONG).show();
                cursor.close();
                return;
            }

            int idHardware = cursor.getInt(0);
            String infoEquipo = cursor.getString(2) + " " + cursor.getString(3);
            int unidadesActuales = cursor.getInt(4);

            cursor.close();
            cursor = null;

            // 🚫 PASO 2: Validar existencias numéricas fijas
            if (unidadesActuales <= 0) {
                Toast.makeText(this, "⚠️ No hay existencias físicas de este equipo. (Unidades: 0)", Toast.LENGTH_LONG).show();
                return;
            }

            // 🔄 PASO 3: Transacciones concurrentes limpias
            ContentValues valoresPrestamo = new ContentValues();
            valoresPrestamo.put("id_hardware", idHardware);
            valoresPrestamo.put("fecha_prestamo", fechaInicio);
            valoresPrestamo.put("fecha_limite", fechaFin);
            valoresPrestamo.put("tipo_prestamo", "Recurrente");
            valoresPrestamo.put("materia_horario", materiaHorario);
            valoresPrestamo.put("actividad_especifica", "Préstamo de ciclo asignado a: " + responsable);
            valoresPrestamo.put("estado", "Activo");
            valoresPrestamo.put("carnet_prestatario", responsable);

            db.insert("prestamos", null, valoresPrestamo);

            // B) Modificar la tabla 'hardware' descontando una unidad de stock
            String sqlUpdateHardware = "UPDATE hardware " +
                    "SET estado = 'Prestado', " +
                    "responsable = ?, " +
                    "contacto = ?, " +
                    "unidades = unidades - 1 " +
                    "WHERE id_hardware = ?";

            db.execSQL(sqlUpdateHardware, new Object[]{responsable, contacto, idHardware});

            Toast.makeText(this, "✅ Préstamo registrado con éxito.", Toast.LENGTH_LONG).show();

            finish();

        } catch (Exception e) {
            Toast.makeText(this, "❌ Error en base de datos: " + e.getMessage(), Toast.LENGTH_LONG).show();
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }
}