package com.grupo1.sgi_fia.controller;

public class Auditoria {
    private String numeroSerie;
    private String marcaModelo;
    private String responsable;
    private String periodo;
    private String ubicacionReal;
    private String estadoConfirmado;
    private String observaciones;

    public Auditoria(String numeroSerie, String marcaModelo, String responsable,
                     String periodo, String ubicacionReal, String estadoConfirmado, String observaciones) {
        this.numeroSerie = numeroSerie;
        this.marcaModelo = marcaModelo;
        this.responsable = responsable;
        this.periodo = periodo;
        this.ubicacionReal = ubicacionReal;
        this.estadoConfirmado = estadoConfirmado;
        this.observaciones = observaciones;
    }

    public String getNumeroSerie() { return numeroSerie; }
    public String getMarcaModelo() { return marcaModelo; }
    public String getResponsable() { return responsable; }
    public String getPeriodo() { return periodo; }
    public String getUbicacionReal() { return ubicacionReal; }
    public String getEstadoConfirmado() { return estadoConfirmado; }
    public String getObservaciones() { return observaciones; }
}
