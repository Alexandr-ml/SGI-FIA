package com.grupo1.sgi_fia.model;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class Ubicacion {

    @PrimaryKey
    public int id_ubicacion;
    public String nombre;
    public String descripcion;
    public String tipo_ubicacion;


}
