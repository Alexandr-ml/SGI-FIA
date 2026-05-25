package com.grupo1.sgi_fia.data;

import android.content.Context;

import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;

import java.text.Normalizer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

public final class SgiFirebase {

    public static final String DOCUMENTOS = "documentos";
    public static final String EQUIPOS = "equipos_informaticos";
    public static final String PRESTATARIOS = "prestatarios";
    public static final String PRESTAMOS = "prestamos";
    public static final String PRESTAMOS_TESIS = "prestamos_tesis";
    public static final String PRESTAMOS_EQUIPO_HORAS = "prestamos_equipo_horas";
    public static final String PRESTAMOS_EQUIPO_RECURRENTE = "prestamos_equipo_recurrente";
    public static final String DEVOLUCIONES = "devoluciones";
    public static final String DEVOLUCIONES_TESIS = "devoluciones_tesis";
    public static final String LEVANTAMIENTOS = "levantamientos_fisicos";

    private static final String API_KEY = "AIzaSyBxGl-iCfD8e-RcKwQOS32NO4w01PjIzis";
    private static final String PROJECT_ID = "pdm-ues";
    private static final String APPLICATION_ID = "1:607923927954:web:b597bfe53ceb2d5ca41f4b";
    private static final String STORAGE_BUCKET = "pdm-ues.firebasestorage.app";

    private SgiFirebase() {
    }

    public interface Callback<T> {
        void onSuccess(T value);

        void onError(Exception exception);
    }

    public interface Matcher {
        boolean matches(DocumentSnapshot document);
    }

    public static FirebaseFirestore db(Context context) {
        initialize(context);
        return FirebaseFirestore.getInstance();
    }

    public static synchronized void initialize(Context context) {
        Context appContext = context.getApplicationContext();
        if (!FirebaseApp.getApps(appContext).isEmpty()) {
            return;
        }

        FirebaseOptions options = new FirebaseOptions.Builder()
                .setApiKey(API_KEY)
                .setApplicationId(APPLICATION_ID)
                .setProjectId(PROJECT_ID)
                .setStorageBucket(STORAGE_BUCKET)
                .build();
        FirebaseApp.initializeApp(appContext, options);
    }

    public static void list(Context context, String collection, Callback<List<DocumentSnapshot>> callback) {
        db(context)
                .collection(collection)
                .get()
                .addOnSuccessListener(snapshot -> callback.onSuccess(snapshot.getDocuments()))
                .addOnFailureListener(callback::onError);
    }

    public static void findFirst(
            Context context,
            String collection,
            Matcher matcher,
            Callback<DocumentSnapshot> callback) {
        list(context, collection, new Callback<List<DocumentSnapshot>>() {
            @Override
            public void onSuccess(List<DocumentSnapshot> documents) {
                for (DocumentSnapshot document : documents) {
                    if (matcher.matches(document)) {
                        callback.onSuccess(document);
                        return;
                    }
                }
                callback.onSuccess(null);
            }

            @Override
            public void onError(Exception exception) {
                callback.onError(exception);
            }
        });
    }

    public static void add(
            Context context,
            String collection,
            Map<String, Object> values,
            Callback<String> callback) {
        Map<String, Object> data = withAddTimestamps(values);
        db(context)
                .collection(collection)
                .add(data)
                .addOnSuccessListener(documentReference -> callback.onSuccess(documentReference.getId()))
                .addOnFailureListener(callback::onError);
    }

    public static void update(
            Context context,
            String collection,
            String documentId,
            Map<String, Object> values,
            Callback<String> callback) {
        Map<String, Object> data = new HashMap<>(values);
        data.put("updatedAt", FieldValue.serverTimestamp());
        db(context)
                .collection(collection)
                .document(documentId)
                .update(data)
                .addOnSuccessListener(unused -> callback.onSuccess(documentId))
                .addOnFailureListener(callback::onError);
    }

    public static void upsert(
            Context context,
            String collection,
            String documentId,
            Map<String, Object> values,
            Callback<String> callback) {
        Map<String, Object> data = new HashMap<>(values);
        data.put("updatedAt", FieldValue.serverTimestamp());
        db(context)
                .collection(collection)
                .document(documentId)
                .set(data, SetOptions.merge())
                .addOnSuccessListener(unused -> callback.onSuccess(documentId))
                .addOnFailureListener(callback::onError);
    }

    public static void upsertPrestatario(
            Context context,
            String carnet,
            String nombre,
            Callback<String> callback) {
        String id = slug(carnet == null || carnet.trim().isEmpty() ? nombre : carnet);
        Map<String, Object> prestatario = values();
        prestatario.put("carnet", clean(carnet));
        prestatario.put("nombre", clean(nombre));
        upsert(context, PRESTATARIOS, id, prestatario, callback);
    }

    public static void seedInitialEquipment(Context context) {
        list(context, EQUIPOS, new Callback<List<DocumentSnapshot>>() {
            @Override
            public void onSuccess(List<DocumentSnapshot> documents) {
                Set<String> existingSeries = new HashSet<>();
                for (DocumentSnapshot document : documents) {
                    existingSeries.add(normalize(string(document, "numero_serie")));
                }

                for (Map<String, Object> equipo : initialEquipment()) {
                    String serie = normalize(String.valueOf(equipo.get("numero_serie")));
                    if (!existingSeries.contains(serie)) {
                        add(context, EQUIPOS, equipo, emptyCallback());
                    }
                }
            }

            @Override
            public void onError(Exception exception) {
                // Las pantallas mostraran el error si necesitan leer datos y Firebase no responde.
            }
        });
    }

    public static Map<String, Object> values() {
        return new HashMap<>();
    }

    public static Map<String, Object> withAddTimestamps(Map<String, Object> values) {
        Map<String, Object> data = new HashMap<>(values);
        data.put("createdAt", FieldValue.serverTimestamp());
        data.put("updatedAt", FieldValue.serverTimestamp());
        return data;
    }

    public static String string(DocumentSnapshot document, String field) {
        Object value = document == null ? null : document.get(field);
        return value == null ? "" : String.valueOf(value);
    }

    public static int integer(DocumentSnapshot document, String field, int fallback) {
        Object value = document == null ? null : document.get(field);
        if (value instanceof Number) {
            return ((Number) value).intValue();
        }
        try {
            return value == null ? fallback : Integer.parseInt(String.valueOf(value));
        } catch (NumberFormatException exception) {
            return fallback;
        }
    }

    public static double decimal(DocumentSnapshot document, String field, double fallback) {
        Object value = document == null ? null : document.get(field);
        if (value instanceof Number) {
            return ((Number) value).doubleValue();
        }
        try {
            return value == null ? fallback : Double.parseDouble(String.valueOf(value));
        } catch (NumberFormatException exception) {
            return fallback;
        }
    }

    public static boolean equalsNormalized(String left, String right) {
        return normalize(left).equals(normalize(right));
    }

    public static String normalize(String value) {
        String normalized = Normalizer.normalize(clean(value), Normalizer.Form.NFD)
                .replaceAll("[\\p{InCombiningDiacriticalMarks}]", "");
        return normalized.toLowerCase(Locale.ROOT);
    }

    public static String slug(String value) {
        String normalized = normalize(value).replaceAll("[^a-z0-9]+", "-");
        normalized = normalized.replaceAll("(^-+|-+$)", "");
        return normalized.isEmpty() ? "sin-id" : normalized;
    }

    public static String clean(String value) {
        return value == null ? "" : value.trim();
    }

    public static Callback<String> emptyCallback() {
        return new Callback<String>() {
            @Override
            public void onSuccess(String value) {
            }

            @Override
            public void onError(Exception exception) {
            }
        };
    }

    private static List<Map<String, Object>> initialEquipment() {
        List<Map<String, Object>> equipos = new ArrayList<>();
        equipos.add(equipo("Monitor Dell", "MON-001", "Dell", "S2725HSM",
                "Centro de Computo", "Monitor para estaciones de trabajo"));
        equipos.add(equipo("Impresora HP", "IMP-001", "HP", "Smart Tank 580",
                "Secretaria de Facultad", "Impresora multifuncional"));
        equipos.add(equipo("Laptop Dell x985", "LAP-985", "Dell", "x985",
                "Unidad de Ciencias Basicas", "Laptop asignada a unidad academica"));
        equipos.add(equipo("Proyector Spectra Q891", "PRO-891", "Spectra", "Q891",
                "Edificio B - Nivel 1 - FIA", "Proyector para aulas"));
        return equipos;
    }

    private static Map<String, Object> equipo(
            String nombre,
            String numeroSerie,
            String marca,
            String modelo,
            String ubicacion,
            String descripcion) {
        Map<String, Object> equipo = values();
        equipo.put("nombre", nombre);
        equipo.put("numero_serie", numeroSerie);
        equipo.put("marca", marca);
        equipo.put("modelo", modelo);
        equipo.put("ubicacion", ubicacion);
        equipo.put("costo_unidad", 0);
        equipo.put("unidades", 1);
        equipo.put("descripcion", descripcion);
        equipo.put("estado_funcional", "Activo");
        equipo.put("estado_prestamo", "Disponible");
        return equipo;
    }
}
