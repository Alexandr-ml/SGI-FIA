package com.grupo1.sgi_fia.model;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "equipos")
public class Equipo {
    @PrimaryKey(autoGenerate = true)
    public int id;
    public String nombre; // De SQL
    public String clasificacion; // De SQL
    public String estado; // De SQL
    public int unidad_id; // De SQL
    
    // Campos adicionales de la UI (Imagen) para que no se pierda funcionalidad
    public String numero_serie;
    public String marca;
    public String modelo;
    public String ubicacion;
    public double costo_unidad;
    public int unidades;
    public String descripcion;
    public String fecha_ultimo_levantamiento; // Nueva fecha para auditoría
}