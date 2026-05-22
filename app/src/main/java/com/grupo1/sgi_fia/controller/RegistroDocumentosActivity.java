package com.grupo1.sgi_fia.controller;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.grupo1.sgi_fia.AdminSQLiteOpenHelper;
import com.grupo1.sgi_fia.R;

public class RegistroDocumentosActivity extends AppCompatActivity {

    // 1. Defino las variables globales para los componentes de la interfaz de usuario
    private EditText etId, etTitulo, etIsbn, etIdioma, etAnio;
    private Spinner spinnerTipo;
    private Button btnGuardar, btnCancelar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Vinculo la actividad con su respectivo archivo de diseño XML
        setContentView(R.layout.activity_registro_documentos);

        // 2. Enlazo las variables de Java con los IDs definidos en el XML usando findViewById
        etId = findViewById(R.id.etIdDocumento);
        etTitulo = findViewById(R.id.etTituloDocumento);
        spinnerTipo = findViewById(R.id.spinnerTipo);
        etIsbn = findViewById(R.id.etIsbn);
        etIdioma = findViewById(R.id.etIdioma);
        etAnio = findViewById(R.id.etAnio);

        btnGuardar = findViewById(R.id.btnGuardar);
        btnCancelar = findViewById(R.id.btnCancelar);

        // 3. Configuro el listener del botón Guardar para ejecutar el método de registro
        btnGuardar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                registrarDocumento();
            }
        });

        // 4. Configuro el botón Cancelar para destruir la actividad actual y volver al menú principal
        btnCancelar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    // 5. Método principal para procesar e insertar los datos en SQLite
    private void registrarDocumento() {
        // Instancio el helper de la base de datos y la abro en modo lectura/escritura
        AdminSQLiteOpenHelper admin = new AdminSQLiteOpenHelper(this, "Biblioteca.db", null, 1);
        SQLiteDatabase bd = admin.getWritableDatabase();

        // Obtengo los valores ingresados por el usuario y los convierto a cadenas de texto
        String idDoc = etId.getText().toString();
        String titulo = etTitulo.getText().toString();
        String isbn = etIsbn.getText().toString();
        String idioma = etIdioma.getText().toString();
        String anio = etAnio.getText().toString();

        // Recupero el elemento seleccionado actualmente en el Spinner
        String tipo = spinnerTipo.getSelectedItem() != null ? spinnerTipo.getSelectedItem().toString() : "";

        // Estructura de control para validar que los campos obligatorios no estén vacíos
        if (!idDoc.isEmpty() && !titulo.isEmpty() && !tipo.isEmpty()) {

            // Instancio ContentValues para almacenar los pares columna-valor a insertar
            ContentValues registro = new ContentValues();

            // Mapeo los datos de la interfaz con las columnas existentes en la tabla "documentos"
            registro.put("nombre_documento", titulo);
            registro.put("tipo_documento", tipo);
            registro.put("fecha_registro", anio); // Almaceno el año temporalmente en este campo

            // Executo la inserción en la tabla y cierro la conexión para liberar recursos de memoria
            bd.insert("documentos", null, registro);
            bd.close();

            // Seteo las cajas de texto en blanco para limpiar la pantalla después de guardar
            etId.setText("");
            etTitulo.setText("");
            etIsbn.setText("");
            etIdioma.setText("");
            etAnio.setText("");

            // Muestro una notificación push temporal (Toast) confirmando el éxito de la operación
            Toast.makeText(this, "¡Documento guardado localmente!", Toast.LENGTH_SHORT).show();
        } else {
            // Muestro un mensaje de advertencia si la validación de campos falla
            Toast.makeText(this, "Por favor, ingresa el ID, Título y Tipo", Toast.LENGTH_SHORT).show();
        }
    }
}