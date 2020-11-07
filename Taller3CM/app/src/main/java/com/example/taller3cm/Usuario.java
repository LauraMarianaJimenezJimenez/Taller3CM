package com.example.taller3cm;

public class Usuario {

    String nombre;
    String apellido;
    String documento;
    String longitud;
    String latitud;
    String uriFoto;

    public Usuario() {
    }

    public Usuario(String nombre, String apellido, String documento, String longitud, String latitud) {
        this.nombre = nombre;
        this.apellido = apellido;
        this.documento = documento;
        this.longitud = longitud;
        this.latitud = latitud;
        //this.uriFoto = uriFoto;
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
