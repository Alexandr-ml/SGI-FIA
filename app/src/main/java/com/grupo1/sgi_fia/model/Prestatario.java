package com.grupo1.sgi_fia.model;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "prestatario")
public class Prestatario {
    @PrimaryKey(autoGenerate = true)
    public int id_prestatario;
    public String carnet;
    public String nombre;
    public String apellido;
    public String correo;
    public String telefono;
}