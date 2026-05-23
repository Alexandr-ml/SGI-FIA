package com.grupo1.sgi_fia.controller;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.grupo1.sgi_fia.AdminSQLiteOpenHelper;
import com.grupo1.sgi_fia.R;

public class DevolucionLibroActivity extends AppCompatActivity {

    // 1. Defino las variables globales para los componentes de control de la devolución
    private EditText etIdPrestamo, etFechaDevolucion;
    private CheckBox cbMarcarDevuelto;
    private Button btnRegistrar, btnCancelar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Vinculo la actividad con el archivo de diseño XML de devoluciones
        setContentView(R.layout.activity_devolucion_libro);

        // 2. Mapeo las variables de Java con las referencias de los objetos en el archivo XML
        etIdPrestamo = findViewById(R.id.etIdPrestamo);
        cbMarcarDevuelto = findViewById(R.id.cbMarcarDevuelto);
        etFechaDevolucion = findViewById(R.id.etFechaDevolucion);
        btnRegistrar = findViewById(R.id.btnRegistrarDevolucion);
        btnCancelar = findViewById(R.id.btnCancelarDevolucion);

        // 3. Configuro el evento click para procesar el guardado de la devolución
        btnRegistrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                registrarDevolucion();
            }
        });

        // 4. Configuro el botón Cancelar para finalizar la actividad y liberar la pila de ejecución
        btnCancelar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    // 5. Método técnico para procesar la devolución e impactar tanto la tabla devoluciones como prestamos
    private void registrarDevolucion() {
        // Inicializo el helper del motor de SQLite y abro la base de datos en modo escritura
        AdminSQLiteOpenHelper admin = new AdminSQLiteOpenHelper(
                this,
                AdminSQLiteOpenHelper.NOMBRE_BD,
                null,
                AdminSQLiteOpenHelper.VERSION_BD);
        SQLiteDatabase bd = admin.getWritableDatabase();

        // Extraigo los valores de las entradas de texto limpios de espacios extras
        String idPrestamoStr = etIdPrestamo.getText().toString().trim();
        String fechaDevolucion = etFechaDevolucion.getText().toString().trim();

        // Convierto el estado booleano del CheckBox a un tipo entero compatible con SQLite (1 = True, 0 = False)
        int estadoDevuelto = cbMarcarDevuelto.isChecked() ? 1 : 0;

        // Validación condicional: El ID de préstamo, la fecha y el check de confirmación son necesarios
        if (!idPrestamoStr.isEmpty() && !fechaDevolucion.isEmpty()) {

            if (estadoDevuelto == 1) {
                try {
                    int idPrestamo = Integer.parseInt(idPrestamoStr);

                    // =========================================================================
                    // PASO A: Insertar el registro histórico en la tabla "devoluciones"
                    // =========================================================================
                    ContentValues registroDevolucion = new ContentValues();
                    registroDevolucion.put("id_prestamo", idPrestamo); // Como entero para la FK
                    registroDevolucion.put("marcar_devuelto", estadoDevuelto);
                    registroDevolucion.put("fecha_devolucion", fechaDevolucion);

                    bd.insert("devoluciones", null, registroDevolucion);

                    // =========================================================================
                    // PASO B: EL TRUCO DE LÓGICA. Actualizar el estado en la tabla "prestamos"
                    // =========================================================================
                    ContentValues valoresPrestamo = new ContentValues();
                    valoresPrestamo.put("estado", "Devuelto"); // Cambiamos el estado de 'Pendiente' a 'Devuelto'

                    // Aplicamos el UPDATE filtrando estrictamente por el ID de este préstamo
                    int filasActualizadas = bd.update("prestamos", valoresPrestamo, "id_prestamo = ?", new String[]{String.valueOf(idPrestamo)});

                    bd.close(); // Cerramos la conexión

                    // Restablezco los componentes visuales a su estado inicial por defecto
                    etIdPrestamo.setText("");
                    etFechaDevolucion.setText("");
                    cbMarcarDevuelto.setChecked(false);

                    if (filasActualizadas > 0) {
                        Toast.makeText(this, "¡Devolución registrada y préstamo cerrado con éxito!", Toast.LENGTH_SHORT).show();
                    } else {
                        // Alerta por si digitan un ID de préstamo que no existe en el inventario
                        Toast.makeText(this, "Devolución guardada, pero el ID de préstamo no existía en el registro.", Toast.LENGTH_LONG).show();
                    }

                } catch (NumberFormatException e) {
                    Toast.makeText(this, "El ID de préstamo debe ser un número válido", Toast.LENGTH_SHORT).show();
                    bd.close();
                }
            } else {
                Toast.makeText(this, "Por favor, marque la casilla 'Marcar como devuelto' para confirmar", Toast.LENGTH_SHORT).show();
                bd.close();
            }
        } else {
            Toast.makeText(this, "Por favor, complete el ID de préstamo y la fecha", Toast.LENGTH_SHORT).show();
            bd.close();
        }
    }
}
