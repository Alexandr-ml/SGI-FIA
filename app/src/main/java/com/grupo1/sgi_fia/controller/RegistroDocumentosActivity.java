package com.grupo1.sgi_fia.controller;

import android.content.ContentValues;
import android.database.Cursor; // 🛠️ NUEVO: Necesario para buscar en la base de datos
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.grupo1.sgi_fia.AdminSQLiteOpenHelper;
import com.grupo1.sgi_fia.R;

public class RegistroDocumentosActivity extends AppCompatActivity {

    private EditText etId, etTitulo, etIsbn, etIdioma, etAnio, etEjemplares;
    private Spinner spinnerTipo;
    private Button btnGuardar, btnRegresar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registro_documentos);

        etId = findViewById(R.id.etIdDocumento);
        etTitulo = findViewById(R.id.etTituloDocumento);
        spinnerTipo = findViewById(R.id.spinnerTipo);
        etIsbn = findViewById(R.id.etIsbn);
        etIdioma = findViewById(R.id.etIdioma);
        etAnio = findViewById(R.id.etAnio);
        etEjemplares = findViewById(R.id.etEjemplaresDocumento);

        btnGuardar = findViewById(R.id.btnGuardar);
        btnRegresar = findViewById(R.id.btnRegresar);

        String[] opcionesDocumento = {"Libro", "Tesis"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, opcionesDocumento);
        spinnerTipo.setAdapter(adapter);

        if (etId != null) {
            etId.setEnabled(false);
            etId.setHint("ID: Automático");
        }

        btnGuardar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                registrarDocumento();
            }
        });

        btnRegresar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void registrarDocumento() {
        AdminSQLiteOpenHelper admin = new AdminSQLiteOpenHelper(
                this,
                AdminSQLiteOpenHelper.NOMBRE_BD,
                null,
                AdminSQLiteOpenHelper.VERSION_BD);
        SQLiteDatabase bd = admin.getWritableDatabase();

        String titulo = etTitulo.getText().toString().trim();
        String isbn = etIsbn.getText().toString().trim();
        String idioma = etIdioma.getText().toString().trim();
        String anioStr = etAnio.getText().toString().trim();
        String ejemplaresStr = (etEjemplares != null) ? etEjemplares.getText().toString().trim() : "";
        String tipo = spinnerTipo.getSelectedItem() != null ? spinnerTipo.getSelectedItem().toString() : "";

        if (!titulo.isEmpty() && !tipo.isEmpty()) {

            int anio = !anioStr.isEmpty() ? Integer.parseInt(anioStr) : 0;
            int nuevosEjemplares = (!ejemplaresStr.isEmpty()) ? Integer.parseInt(ejemplaresStr) : 1;

            // 🛠️ LÓGICA DE CONTROL: Buscamos si ya existe un documento idéntico
            // Hacemos un SELECT filtrando por titulo, anio, isbn y tipo
            Cursor cursor = bd.rawQuery(
                    "SELECT id_documento, ejemplares FROM documentos WHERE titulo=? AND anio=? AND isbn=? AND tipo=?",
                    new String[]{titulo, String.valueOf(anio), isbn, tipo}
            );

            if (cursor.moveToFirst()) {
                // 💥 ¡SÍ EXISTE! Encontró una coincidencia exacta.
                int idExistente = cursor.getInt(0);
                int ejemplaresActuales = cursor.getInt(1);
                cursor.close(); // Cerramos el cursor rápido

                // Sumamos el stock actual más lo que el usuario acaba de digitar
                int stockActualizado = ejemplaresActuales + nuevosEjemplares;

                ContentValues actualizacion = new ContentValues();
                actualizacion.put("ejemplares", stockActualizado);

                // Ejecutamos el UPDATE en base al ID que encontramos
                bd.update("documentos", actualizacion, "id_documento=" + idExistente, null);
                bd.close();

                Toast.makeText(this, "¡Libro existente! ID: " + idExistente + " incrementó stock a: " + stockActualizado, Toast.LENGTH_LONG).show();

            } else {
                // 📝 ¡NO EXISTE! Es un libro completamente nuevo.
                cursor.close();

                ContentValues registro = new ContentValues();
                registro.put("titulo", titulo);
                registro.put("tipo", tipo);
                registro.put("isbn", isbn);
                registro.put("idioma", idioma);
                registro.put("anio", anio);
                registro.put("ejemplares", nuevosEjemplares);

                long nuevoId = bd.insert("documentos", null, registro);
                bd.close();

                if (nuevoId != -1) {
                    Toast.makeText(this, "¡Nuevo registro! ID asignado: " + nuevoId + " (" + nuevosEjemplares + " ej.)", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(this, "Error crítico al intentar insertar en la base de datos", Toast.LENGTH_SHORT).show();
                    return;
                }
            }

            // Limpieza de pantalla (se ejecuta en ambos casos tras un éxito)
            if (etId != null) {
                etId.setText("");
                etId.setHint("ID: Automático");
            }
            etTitulo.setText("");
            etIsbn.setText("");
            etIdioma.setText("");
            etAnio.setText("");
            if (etEjemplares != null) etEjemplares.setText("");
            spinnerTipo.setSelection(0);

        } else {
            Toast.makeText(this, "Por favor, ingresa al menos el Título", Toast.LENGTH_SHORT).show();
        }
    }
}
