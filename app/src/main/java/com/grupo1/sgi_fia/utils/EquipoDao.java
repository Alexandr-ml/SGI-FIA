package com.grupo1.sgi_fia.utils;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.grupo1.sgi_fia.model.Equipo;

import java.util.List;

@Dao
public interface EquipoDao {
    @Query("SELECT * FROM equipos")
    List<Equipo> getAll();

    @Query("SELECT * FROM equipos WHERE id = :id")
    Equipo getById(int id);

    @Query("SELECT * FROM equipos WHERE numero_serie LIKE :serie")
    List<Equipo> searchBySerie(String serie);

    @Insert
    void insert(Equipo equipo);

    @Update
    void update(Equipo equipo);

    @Delete
    void delete(Equipo equipo);
}