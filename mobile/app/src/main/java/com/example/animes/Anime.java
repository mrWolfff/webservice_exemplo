package com.example.animes;

import android.graphics.Bitmap;
import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;

public class Anime implements Parcelable {
    private Bitmap imagem;
    private String titulo;
    private String genero;
    private int episodios;
    private int ano;


    public Anime() {
    }

    public Anime(Bitmap imagem, String titulo, String genero,int episodios, int ano) {
        this.imagem = imagem;
        this.titulo = titulo;
        this.genero = genero;
        this.episodios = episodios;
        this.ano = ano;

    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getGenero() {
        return genero;
    }

    public void setGenero(String genero) {
        this.genero = genero;
    }

    public int getAno() {
        return ano;
    }

    public void setAno(int ano) {
        this.ano = ano;
    }

    public int getEpisodios() {
        return episodios;
    }

    public void setEpisodios(int episodios) {
        this.episodios = episodios;
    }

    public Bitmap getImagem() {
        return imagem;
    }

    public void setImagem(Bitmap imagem) {
        this.imagem = imagem;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeValue(this.imagem);
        dest.writeString(this.titulo);
        dest.writeString(this.genero);
        dest.writeInt(this.episodios);
        dest.writeInt(this.ano);
    }

    public void readFromParcel(Parcel parcel){
        this.imagem = (Bitmap) parcel.readValue(Bitmap.class.getClassLoader());
        this.titulo = parcel.readString();
        this.genero = parcel.readString();
        this.episodios = parcel.readInt();
        this.ano = parcel.readInt();
    }

    public static final Parcelable.Creator<Anime> CREATOR = new Parcelable.Creator<Anime>(){
        @Override
        public Anime createFromParcel (Parcel p){
            Anime f = new Anime();
            f.readFromParcel(p);
            return f;
        }
        @Override
        public Anime[] newArray(int size){
            return new Anime[size];
        }
    };

}


