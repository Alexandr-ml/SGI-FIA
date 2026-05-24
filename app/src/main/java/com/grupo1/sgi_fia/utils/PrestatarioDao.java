package com.grupo1.sgi_fia.utils;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.grupo1.sgi_fia.model.Prestatario;

import java.util.List;

@Dao
public interface PrestatarioDao {
    @Query("SELECT * FROM prestatario")
    List<Prestatario> getAll();

    @Insert
    void insert(Prestatario prestatario);

    @Update
    void update(Prestatario prestatario);

    @Delete
    void delete(Prestatario prestatario);
}