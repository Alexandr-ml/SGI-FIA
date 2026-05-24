package com.grupo1.sgi_fia.utils;

import com.grupo1.sgi_fia.model.Equipo;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;

public interface ApiService {
    @GET("equipos")
    Call<List<Equipo>> getEquiposRemotos();

    @POST("equipos")
    Call<Equipo> saveEquipoRemoto(@Body Equipo equipo);
}