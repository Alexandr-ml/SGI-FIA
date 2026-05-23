package com.grupo1.sgi_fia.controller;
import com.grupo1.sgi_fia.controller.Auditoria;
import com.grupo1.sgi_fia.controller.AuditoriaAdapter;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.grupo1.sgi_fia.R;
import com.grupo1.sgi_fia.AdminSQLiteOpenHelper;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;

public class HistorialAuditoriaActivity extends AppCompatActivity {

    private TextView txtTotalAuditorias;
    private RecyclerView recyclerAuditorias;
    private AdminSQLiteOpenHelper adminHelper;
    private List<Auditoria> listaAuditorias;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_historial_auditoria);

        adminHelper = new AdminSQLiteOpenHelper(this, "Biblioteca.db", null, 3);

        txtTotalAuditorias = findViewById(R.id.txt_total_auditorias);
        recyclerAuditorias = findViewById(R.id.recycler_historial_auditorias);
        Button btnRegresar = findViewById(R.id.btn_regresar_historial);

        // 🌟 ENLACE DEL NUEVO BOTÓN
        Button btnExportar = findViewById(R.id.btn_exportar_reporte);

        recyclerAuditorias.setLayoutManager(new LinearLayoutManager(this));
        listaAuditorias = new ArrayList<>();

        btnRegresar.setOnClickListener(v -> finish());

        // 🌟 ACCIÓN AL DAR CLIC EN EXPORTAR
        btnExportar.setOnClickListener(v -> generarReporteCSV());

        cargarHistorialDesdeBD();
    }

    @SuppressLint("SetTextI18n")
    private void cargarHistorialDesdeBD() {
        listaAuditorias.clear();
        SQLiteDatabase db = adminHelper.getReadableDatabase();

        String query = "SELECT h.numero_serie, h.marca || ' ' || h.modelo AS equipo, h.responsable, " +
                "a.anio_periodo, a.ubicacion_encontrado, a.estado_confirmado, a.observaciones " +
                "FROM auditorias a " +
                "INNER JOIN hardware h ON a.id_hardware = h.id_hardware " +
                "ORDER BY a.id_hardware DESC";

        Cursor cursor = db.rawQuery(query, null);

        if (cursor.moveToFirst()) {
            do {
                Auditoria aud = new Auditoria(
                        cursor.getString(0),
                        cursor.getString(1),
                        cursor.getString(2),
                        cursor.getString(3),
                        cursor.getString(4),
                        cursor.getString(5),
                        cursor.getString(6)
                );
                listaAuditorias.add(aud);
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();

        AuditoriaAdapter adapter = new AuditoriaAdapter(listaAuditorias);
        recyclerAuditorias.setAdapter(adapter);

        txtTotalAuditorias.setText("Total de Equipos Auditados: " + listaAuditorias.size());
    }

    // 🌟 NUEVO MÉTODO: CONVERTIR DATOS Y CREAR EL ARCHIVO EXCEL/CSV
    private void generarReporteCSV() {
        if (listaAuditorias == null || listaAuditorias.isEmpty()) {
            Toast.makeText(this, "⚠️ No hay datos para generar el reporte", Toast.LENGTH_SHORT).show();
            return;
        }

        // 1. Crear el encabezado de las columnas
        StringBuilder csvContenido = new StringBuilder();
        csvContenido.append("Numero de Serie,Equipo/Hardware,Responsable Asignado,Periodo,Ubicacion de Hallazgo,Estado Confirmado,Observaciones\n");

        // 2. Recorrer la lista y añadir las filas con los datos
        for (Auditoria aud : listaAuditorias) {
            // Reemplazamos comas en observaciones por punto y coma para evitar descuadrar el CSV en Excel
            String obsLimpia = aud.getObservaciones() != null ? aud.getObservaciones().replace(",", ";") : "";

            csvContenido.append(aud.getNumeroSerie()).append(",")
                    .append(aud.getMarcaModelo()).append(",")
                    .append(aud.getResponsable()).append(",")
                    .append(aud.getPeriodo()).append(",")
                    .append(aud.getUbicacionReal()).append(",")
                    .append(aud.getEstadoConfirmado()).append(",")
                    .append(obsLimpia).append("\n");
        }

        try {
            // 3. Crear una carpeta temporal interna dentro de la app para almacenar el archivo seguro
            String nombreArchivo = "Reporte_Auditoria_FIA_" + System.currentTimeMillis() + ".csv";
            File carpetaCache = new File(getCacheDir(), "reportes");
            if (!carpetaCache.exists()) {
                carpetaCache.mkdirs();
            }

            File archivoReporte = new File(carpetaCache, nombreArchivo);
            FileOutputStream stream = new FileOutputStream(archivoReporte);
            stream.write(csvContenido.toString().getBytes());
            stream.close();

            // 4. Compartir el archivo generado usando FileProvider (Medida de seguridad de Android)
            Uri uriArchivo = FileProvider.getUriForFile(this, getPackageName() + ".fileprovider", archivoReporte);

            Intent intentCompartir = new Intent(Intent.ACTION_SEND);
            intentCompartir.setType("text/csv");
            intentCompartir.putExtra(Intent.EXTRA_SUBJECT, "Reporte de Levantamiento Físico de Activos FIA");
            intentCompartir.putExtra(Intent.EXTRA_STREAM, uriArchivo);
            intentCompartir.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION); // Otorga permiso temporal para que Excel lo abra

            startActivity(Intent.createChooser(intentCompartir, "Enviar reporte vía..."));

        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "❌ Error al generar el reporte", Toast.LENGTH_SHORT).show();
        }
    }
}