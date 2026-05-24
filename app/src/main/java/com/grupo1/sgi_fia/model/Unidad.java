package com.grupo1.sgi_fia.model;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "unidades")
public class Unidad {
    @PrimaryKey(autoGenerate = true)
    public int id;
    public String nombre;
    public String descripcion;
}