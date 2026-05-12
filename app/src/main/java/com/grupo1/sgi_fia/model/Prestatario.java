package com.grupo1.sgi_fia.model;

import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.PrimaryKey;

@Entity
public class Prestatario {

   @PrimaryKey
    public int id_prestatario;
    public int id_rol;
}
