package com.grupo1.sgi_fia.model;

public final class EquiposRegistrados {

    public static final String MONITOR_DELL = "Monitor Dell - Modelo S2725HSM";
    public static final String IMPRESORA_HP = "Impresora HP - Modelo Smart Tank 580";

    private static final String[] EQUIPOS = {
            MONITOR_DELL,
            IMPRESORA_HP
    };

    private EquiposRegistrados() {
    }

    public static String obtenerListadoParaPrestamo() {
        return String.join("; ", EQUIPOS);
    }
}
