package com.grupo1.sgi_fia.controller;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.grupo1.sgi_fia.AdminSQLiteOpenHelper;
import com.grupo1.sgi_fia.R;

public class DevolucionLibroActivity extends AppCompatActivity {

    private Spinner spinnerTipoDocumento;
    private EditText etIdPrestamo, etFechaDevolucion;
    private CheckBox cbMarcarDevuelto;
    private Button btnRegistrar, btnCancelar;
    private AdminSQLiteOpenHelper adminHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_devolucion_libro);

        // Inicializar Base de Datos con tu nombre y versión exacta (Biblioteca.db, versión 3)
        adminHelper = new AdminSQLiteOpenHelper(this, "Biblioteca.db", null, 3);

        // Vincular vistas
        spinnerTipoDocumento = findViewById(R.id.spinnerTipoDocumento);
        etIdPrestamo = findViewById(R.id.etIdPrestamo);
        etFechaDevolucion = findViewById(R.id.etFechaDevolucion);
        cbMarcarDevuelto = findViewById(R.id.cbMarcarDevuelto);
        btnRegistrar = findViewById(R.id.btnRegistrarDevolucion);
        btnCancelar = findViewById(R.id.btnCancelarDevolucion);

        // Configurar las opciones del Spinner (Libro / Tesis)
        String[] opciones = {"Libro", "Tesis"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, opciones);
        spinnerTipoDocumento.setAdapter(adapter);

        // Cambiar dinámicamente el texto del CheckBox según la selección
        spinnerTipoDocumento.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String seleccion = opciones[position];
                cbMarcarDevuelto.setText("Marcar " + seleccion.toLowerCase() + " como devuelto");
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });

        // Botón Cancelar (Cierra la pantalla de forma limpia)
        btnCancelar.setOnClickListener(v -> finish());

        // Botón Registrar
        btnRegistrar.setOnClickListener(v -> ejecutarDevolucion());
    }

    private void ejecutarDevolucion() {
        String idPrestamo = etIdPrestamo.getText().toString().trim();
        String fecha = etFechaDevolucion.getText().toString().trim();
        String tipoDoc = spinnerTipoDocumento.getSelectedItem().toString();
        boolean estaChequeado = cbMarcarDevuelto.isChecked();

        // Validaciones obligatorias
        if (idPrestamo.isEmpty()) {
            Toast.makeText(this, "⚠️ Por favor, ingresa el ID del préstamo", Toast.LENGTH_SHORT).show();
            return;
        }
        if (fecha.isEmpty()) {
            Toast.makeText(this, "⚠️ Por favor, ingresa la fecha de devolución", Toast.LENGTH_SHORT).show();
            return;
        }
        if (!estaChequeado) {
            Toast.makeText(this, "⚠️ Debes marcar la casilla para confirmar la devolución", Toast.LENGTH_SHORT).show();
            return;
        }

        SQLiteDatabase db = adminHelper.getWritableDatabase();

        try {
            //  Verificar si el ID de préstamo coincide con el tipo elegido (Libro o Tesis)
            String queryValidar = "SELECT p.id_prestamo FROM prestamos p " +
                    "INNER JOIN documentos d ON p.id_documento = d.id_documento " +
                    "WHERE p.id_prestamo = ? AND d.tipo = ?";

            Cursor cursor = db.rawQuery(queryValidar, new String[]{idPrestamo, tipoDoc});

            if (!cursor.moveToFirst()) {
                Toast.makeText(this, "❌ El ID ingresado no corresponde a un préstamo de tipo: " + tipoDoc, Toast.LENGTH_LONG).show();
                cursor.close();
                db.close();
                return;
            }
            cursor.close();

            // PASO 1: Cambiar el estado en la tabla 'prestamos' a 'Devuelto'
            ContentValues valoresPrestamos = new ContentValues();
            valoresPrestamos.put("estado", "Devuelto");
            db.update("prestamos", valoresPrestamos, "id_prestamo = ?", new String[]{idPrestamo});

            //  PASO 2: Insertar el registro en la tabla 'devoluciones'
            ContentValues valoresDevoluciones = new ContentValues();
            valoresDevoluciones.put("id_prestamo", Integer.parseInt(idPrestamo));
            valoresDevoluciones.put("marcar_devuelto", 1); // 1 significa que sí está devuelto
            valoresDevoluciones.put("fecha_devolucion", fecha);

            db.insert("devoluciones", null, valoresDevoluciones);
            db.close();

            Toast.makeText(this, "✅ Devolución de " + tipoDoc + " guardada exitosamente", Toast.LENGTH_LONG).show();
            finish(); // Cierra y regresa al menú principal

        } catch (Exception e) {
            Toast.makeText(this, "❌ Error en la base de datos: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }
}