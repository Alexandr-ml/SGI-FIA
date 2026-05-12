package com.grupo1.sgi_fia.model;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class EquipoInformatico {
    @PrimaryKey
    public int id_equipo;
    public String numero_serie;
    public String modelo;
    public String marca;
    public String estado_funcional;
    public String estado_prestamo;
    public int id_usuario;

}
