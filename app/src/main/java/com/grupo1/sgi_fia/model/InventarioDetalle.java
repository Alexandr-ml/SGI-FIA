package com.grupo1.sgi_fia.model;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "inventario_detalle")
public class InventarioDetalle {
    @PrimaryKey(autoGenerate = true)
    public int id;
    public int inventario_id;
    public int equipo_id;
    public int cantidad_sistema;
    public int cantidad_fisica;
    public int diferencia;
}