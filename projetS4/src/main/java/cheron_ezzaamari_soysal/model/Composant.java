package cheron_ezzaamari_soysal.model;

import javafx.scene.image.ImageView;

public class Composant {
    private String nom;
    private int id;
    private String adresseIP;
    private ImageView img;
    private double width;
    private double height;

    public void setNom(String nom) {
        this.nom = nom;
    }

    public Composant(ImageView imageView, String adresseIP) {
        this.img = imageView;
        this.adresseIP = adresseIP;
        id = Id.getId().getNumber();
    }

    public Composant() {
    }

    public void setImg(ImageView img) {
        this.img = img;
    }

    public ImageView getImg() {
        return img;
    }

    public void setWidth(double width) {
        this.width = width;
    }

    public void setHeight(double height) {
        this.height = height;
    }

    public double getWidth() {
        return width;
    }

    public double getHeight() {
        return height;
    }

    public Composant(String nom) {
        this.nom = nom;
        id = Id.getId().getNumber();
    }

    public void setAdresseIP(String adresseIP) {
        this.adresseIP = adresseIP;
    }

    public String getNom() {
        return nom;
    }

    public int getId() {
        return id;
    }

    public String getAdresseIP() {
        return adresseIP;
    }
}
