package com.grupo1.sgi_fia.controller;

import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.firestore.DocumentSnapshot;
import com.grupo1.sgi_fia.R;
import com.grupo1.sgi_fia.data.SgiFirebase;

import java.util.Map;

public class RegistroDocumentosActivity extends AppCompatActivity {

    private EditText etId;
    private EditText etTitulo;
    private EditText etIsbn;
    private EditText etIdioma;
    private EditText etAnio;
    private EditText etEjemplares;
    private Spinner spinnerTipo;
    private Button btnGuardar;
    private Button btnRegresar;

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
        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_dropdown_item,
                opcionesDocumento);
        spinnerTipo.setAdapter(adapter);

        if (etId != null) {
            etId.setEnabled(false);
            etId.setHint("ID: Automatico");
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
        String titulo = etTitulo.getText().toString().trim();
        String isbn = etIsbn.getText().toString().trim();
        String idioma = etIdioma.getText().toString().trim();
        String anioStr = etAnio.getText().toString().trim();
        String ejemplaresStr = etEjemplares != null ? etEjemplares.getText().toString().trim() : "";
        String tipo = spinnerTipo.getSelectedItem() != null ? spinnerTipo.getSelectedItem().toString() : "";

        if (titulo.isEmpty() || tipo.isEmpty()) {
            Toast.makeText(this, "Por favor, ingresa al menos el titulo", Toast.LENGTH_SHORT).show();
            return;
        }

        int anio;
        int nuevosEjemplares;
        try {
            anio = !anioStr.isEmpty() ? Integer.parseInt(anioStr) : 0;
            nuevosEjemplares = !ejemplaresStr.isEmpty() ? Integer.parseInt(ejemplaresStr) : 1;
        } catch (NumberFormatException exception) {
            Toast.makeText(this, "Anio y ejemplares deben ser numericos", Toast.LENGTH_SHORT).show();
            return;
        }

        SgiFirebase.findFirst(
                this,
                SgiFirebase.DOCUMENTOS,
                documento -> SgiFirebase.equalsNormalized(SgiFirebase.string(documento, "titulo"), titulo)
                        && SgiFirebase.equalsNormalized(SgiFirebase.string(documento, "tipo"), tipo)
                        && SgiFirebase.equalsNormalized(SgiFirebase.string(documento, "isbn"), isbn)
                        && SgiFirebase.integer(documento, "anio", 0) == anio,
                new SgiFirebase.Callback<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot existente) {
                        if (existente != null) {
                            actualizarStock(existente, nuevosEjemplares);
                        } else {
                            crearDocumento(titulo, tipo, isbn, idioma, anio, nuevosEjemplares);
                        }
                    }

                    @Override
                    public void onError(Exception exception) {
                        Toast.makeText(RegistroDocumentosActivity.this,
                                "No se pudo consultar Firebase: " + exception.getMessage(),
                                Toast.LENGTH_LONG).show();
                    }
                });
    }

    private void actualizarStock(DocumentSnapshot existente, int nuevosEjemplares) {
        int stockActualizado = SgiFirebase.integer(existente, "ejemplares", 0) + nuevosEjemplares;
        Map<String, Object> actualizacion = SgiFirebase.values();
        actualizacion.put("ejemplares", stockActualizado);

        SgiFirebase.update(this, SgiFirebase.DOCUMENTOS, existente.getId(), actualizacion,
                new SgiFirebase.Callback<String>() {
                    @Override
                    public void onSuccess(String id) {
                        limpiarFormulario();
                        Toast.makeText(RegistroDocumentosActivity.this,
                                "Documento existente actualizado. Stock: " + stockActualizado,
                                Toast.LENGTH_LONG).show();
                    }

                    @Override
                    public void onError(Exception exception) {
                        Toast.makeText(RegistroDocumentosActivity.this,
                                "No se pudo actualizar Firebase: " + exception.getMessage(),
                                Toast.LENGTH_LONG).show();
                    }
                });
    }

    private void crearDocumento(
            String titulo,
            String tipo,
            String isbn,
            String idioma,
            int anio,
            int nuevosEjemplares) {
        Map<String, Object> registro = SgiFirebase.values();
        registro.put("titulo", titulo);
        registro.put("tipo", tipo);
        registro.put("isbn", isbn);
        registro.put("idioma", idioma);
        registro.put("anio", anio);
        registro.put("ejemplares", nuevosEjemplares);

        SgiFirebase.add(this, SgiFirebase.DOCUMENTOS, registro, new SgiFirebase.Callback<String>() {
            @Override
            public void onSuccess(String id) {
                limpiarFormulario();
                Toast.makeText(RegistroDocumentosActivity.this,
                        "Documento registrado en Firebase. ID: " + id,
                        Toast.LENGTH_LONG).show();
            }

            @Override
            public void onError(Exception exception) {
                Toast.makeText(RegistroDocumentosActivity.this,
                        "No se pudo guardar en Firebase: " + exception.getMessage(),
                        Toast.LENGTH_LONG).show();
            }
        });
    }

    private void limpiarFormulario() {
        if (etId != null) {
            etId.setText("");
            etId.setHint("ID: Automatico");
        }
        etTitulo.setText("");
        etIsbn.setText("");
        etIdioma.setText("");
        etAnio.setText("");
        if (etEjemplares != null) {
            etEjemplares.setText("");
        }
        spinnerTipo.setSelection(0);
    }
}
