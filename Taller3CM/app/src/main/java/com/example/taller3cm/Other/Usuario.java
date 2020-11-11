package com.example.taller3cm.Other;

public class Usuario {

    String id;
    String nombre;
    String apellido;
    String documento;
    double longitud;
    double latitud;
    String uriFoto;
    boolean disponible;

    public Usuario() {
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public void setApellido(String apellido) {
        this.apellido = apellido;
    }

    public void setDocumento(String documento) {
        this.documento = documento;
    }

    public void setLongitud(double longitud) {
        this.longitud = longitud;
    }

    public void setLatitud(double latitud) {
        this.latitud = latitud;
    }

    public void setUriFoto(String uriFoto) {
        this.uriFoto = uriFoto;
    }

    public void setDisponible(boolean disponible) {
        this.disponible = disponible;
    }

    public Usuario(String id, String nombre, String apellido) {
        this.id = id;
        this.nombre = nombre;
        this.apellido = apellido;
    }

    public Usuario(String nombre, String apellido, String documento, double longitud, double latitud, boolean disponible) {
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

    public double getLongitud() {
        return longitud;
    }

    public double getLatitud() {
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
