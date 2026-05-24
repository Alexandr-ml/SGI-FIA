package com.grupo1.sgi_fia.utils;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import com.grupo1.sgi_fia.model.Inventario;

import java.util.List;

@Dao
public interface InventarioDao {
    @Query("SELECT * FROM inventario")
    List<Inventario> getAll();

    @Insert
    void insert(Inventario inventario);
}
