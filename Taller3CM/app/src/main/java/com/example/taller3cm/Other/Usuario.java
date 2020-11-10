package com.example.taller3cm.Other;

public class Usuario {

    String id;
    String nombre;
    String apellido;
    String documento;
    String longitud;
    String latitud;
    String uriFoto;
    boolean disponible;

    public Usuario() {
    }

    public Usuario(String nombre, String apellido, String documento, String longitud, String latitud, boolean disponible) {
        this.nombre = nombre;
        this.apellido = apellido;
        this.documento = documento;
        this.longitud = longitud;
        this.latitud = latitud;
        this.disponible = disponible;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public boolean isDisponible() {
        return disponible;
    }

    public String getNombre() {
        return nombre;
    }

    public String getLongitud() {
        return longitud;
    }

    public String getLatitud() {
        return latitud;
    }

    public String getUriFoto() {
        return uriFoto;
    }

    public String getApellido() {
        return apellido;
    }

    public String getDocumento() {
        return documento;
    }
}
