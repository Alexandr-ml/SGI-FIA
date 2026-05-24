package com.grupo1.sgi_fia.utils;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.sqlite.db.SupportSQLiteDatabase;

import com.grupo1.sgi_fia.model.Equipo;
import com.grupo1.sgi_fia.model.Usuario;
import com.grupo1.sgi_fia.model.Unidad;
import com.grupo1.sgi_fia.model.Inventario;
import com.grupo1.sgi_fia.model.InventarioDetalle;
import com.grupo1.sgi_fia.model.Prestatario;

import java.util.concurrent.Executors;

@Database(entities = {
        Equipo.class, 
        Usuario.class, 
        Unidad.class, 
        Inventario.class, 
        InventarioDetalle.class, 
        Prestatario.class
}, version = 5)
public abstract class BaseDeDatosLocal extends RoomDatabase {
    public abstract EquipoDao equipoDao();
    public abstract PrestatarioDao prestatarioDao();
    public abstract InventarioDao inventarioDao();

    private static volatile BaseDeDatosLocal INSTANCE;

    public static BaseDeDatosLocal getDatabase(final Context context) {
        if (INSTANCE == null) {
            synchronized (BaseDeDatosLocal.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                                    BaseDeDatosLocal.class, "sgi_fia_db")
                            .fallbackToDestructiveMigration()
                            .allowMainThreadQueries()
                            .addCallback(new RoomDatabase.Callback() {
                                @Override
                                public void onCreate(@NonNull SupportSQLiteDatabase db) {
                                    super.onCreate(db);
                                    // Insertar datos iniciales en un hilo separado
                                    Executors.newSingleThreadExecutor().execute(() -> {
                                        preCargarDatos(INSTANCE);
                                    });
                                }
                            })
                            .build();
                }
            }
        }
        return INSTANCE;
    }

    private static void preCargarDatos(BaseDeDatosLocal db) {
        EquipoDao dao = db.equipoDao();
        
        // Insertar los 15 registros solicitados
        dao.insert(crearEquipo("SN-001", "Dell", "Latitude 5420", "Unidad de Ciencias Básicas"));
        dao.insert(crearEquipo("SN-002", "Lenovo", "ThinkPad X1", "Cubículo 12 - Escuela de Ing. Industrial"));
        dao.insert(crearEquipo("SN-003", "Proyector", "Spectra Q891", "Edificio B - Nivel 1 - FIA"));
        dao.insert(crearEquipo("SN-004", "Apple", "MacBook Pro", "Laboratorio de Informática"));
        dao.insert(crearEquipo("SN-005", "HP", "HP LaserJet", "Secretaría de Facultad"));
        dao.insert(crearEquipo("SN-006", "Samsung", "Monitor 24\"", "Centro de Cómputo 1"));
        dao.insert(crearEquipo("SN-007", "Apple", "iPad Air", "Biblioteca Central"));
        dao.insert(crearEquipo("SN-008", "Dell", "Server PowerEdge", "Data Center FIA"));
        dao.insert(crearEquipo("SN-009", "Cisco", "Switch 24 Ports", "Data Center FIA"));
        dao.insert(crearEquipo("SN-010", "HP", "Scanner ScanJet", "Archivo Académico"));
        dao.insert(crearEquipo("SN-011", "APC", "UPS 1500VA", "Data Center FIA"));
        dao.insert(crearEquipo("SN-012", "Logitech", "Mouse Wireless", "Bodega Activos"));
        dao.insert(crearEquipo("SN-013", "Razer", "Keyboard Mech", "Laboratorio de Computación"));
        dao.insert(crearEquipo("SN-014", "Logitech", "Webcam C920", "Sala de Conferencias"));
        dao.insert(crearEquipo("SN-015", "Seagate", "External HDD 2TB", "Unidad de Investigación"));
    }

    private static Equipo crearEquipo(String sn, String marca, String modelo, String ubicacion) {
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
}
