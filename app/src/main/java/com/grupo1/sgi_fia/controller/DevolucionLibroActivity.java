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
        setContentView(R.layout.activity_devolucion_libro);// OJO: Verifica si tu XML se llama exactamente así

        // 2. Mapéo las variables de Java con las referencias de los objetos en el archivo XML
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

    // 5. Método técnico para la inserción del estado de devolución en la base de datos local
    private void registrarDevolucion() {
        // Inicializo el helper del motor de SQLite y abro la base de datos en modo escritura
        AdminSQLiteOpenHelper admin = new AdminSQLiteOpenHelper(this, "Biblioteca.db", null, 1);
        SQLiteDatabase bd = admin.getWritableDatabase();

        // Extraigo los valores de las entradas de texto
        String idPrestamo = etIdPrestamo.getText().toString();
        String fechaDevolucion = etFechaDevolucion.getText().toString();

        // Convierto el estado booleano del CheckBox a un tipo entero compatible con SQLite (1 = True, 0 = False)
        int estadoDevuelto = cbMarcarDevuelto.isChecked() ? 1 : 0;

        // Validación condicional para certificar que el campo clave de búsqueda no esté vacío
        if (!idPrestamo.isEmpty() && !fechaDevolucion.isEmpty()) {

            // Instancio el contenedor de datos estructurados para la consulta
            ContentValues registro = new ContentValues();

            // Enlazo las variables extraídas con los nombres de columna de la tabla "devoluciones"
            registro.put("id_prestamo", idPrestamo);
            registro.put("marcar_devuelto", estadoDevuelto);
            registro.put("fecha_devolucion", fechaDevolucion);

            // Ejecuto la sentencia de inserción en la base de datos y cierro la instancia de conexión
            bd.insert("devoluciones", null, registro);
            bd.close();

            // Restablezco los componentes visuales a su estado inicial por defecto
            etIdPrestamo.setText("");
            etFechaDevolucion.setText("");
            cbMarcarDevuelto.setChecked(false);

            // Despliego una notificación temporal Toast confirmando la persistencia de los datos
            Toast.makeText(this, "¡Devolución registrada con éxito!", Toast.LENGTH_SHORT).show();
        } else {
            // Manejo de excepción visual si las condiciones de validación primaria fallan
            Toast.makeText(this, "Por favor, complete el ID de préstamo y la fecha", Toast.LENGTH_SHORT).show();
        }
    }
}