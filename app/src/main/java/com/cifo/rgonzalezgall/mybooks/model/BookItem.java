package com.cifo.rgonzalezgall.mybooks.model;

import java.util.Date;

/**
 * A dummy bookitem representing a simple book
 */
public class BookItem {

    public BookItem(int identificador, String titulo, String autor, Date dataPublicacion, String descripcion, String urlImagenPortada) {
        this.identificador = identificador;
        this.titulo = titulo;
        this.autor = autor;
        this.dataPublicacion = dataPublicacion;
        this.descripcion = descripcion;
        this.urlImagenPortada = urlImagenPortada;
    }

    private int identificador;
    private String titulo;
    private String autor;
    private Date dataPublicacion;
    private String descripcion;
    private String urlImagenPortada;

    public int getIdentificador() {
        return identificador;
    }

    public void setIdentificador(int identificador) {
        this.identificador = identificador;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getAutor() {
        return autor;
    }

    public void setAutor(String autor) {
        this.autor = autor;
    }

    public Date getDataPublicacion() {
        return dataPublicacion;
    }

    public void setDataPublicacion(Date dataPublicacion) {
        this.dataPublicacion = dataPublicacion;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getUrlImagenPortada() {
        return urlImagenPortada;
    }

    public void setUrlImagenPortada(String urlImagenPortada) {
        this.urlImagenPortada = urlImagenPortada;
    }

    @Override
    public String toString() {
        return this.titulo + ", " + this.autor + ", " + dataPublicacion + ", " + descripcion ;
    }
}

