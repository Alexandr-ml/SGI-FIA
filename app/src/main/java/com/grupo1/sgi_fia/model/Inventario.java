package com.grupo1.sgi_fia.model;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "inventario")
public class Inventario {
    @PrimaryKey(autoGenerate = true)
    public int id;
    public String fecha;
    public String descripcion;
}