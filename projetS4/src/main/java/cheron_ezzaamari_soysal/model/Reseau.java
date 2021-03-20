package cheron_ezzaamari_soysal.model;

import javafx.scene.image.ImageView;

//mdr c un singleton genre dès qu on crée un réseau il obtient un identifiant
public abstract class Reseau {
    private String adresseIP;
    private int id;
    private double width;
    private double height;
    private ImageView img;
    private Places places;

    public void setImg(ImageView img) {
        this.img = img;
        this.places = new Places(img);
    }

    public void setPlaces() {
        this.places = new Places(img);
    }

    public ImageView getImg() {
        return img;
    }

    public Reseau(String adresseIP) {
        this.adresseIP = adresseIP;
        id = Id.getId().getNumber();
    }

    public void setWidth(double width) {
        this.width = width;
    }

    public void setHeight(double height) {
        this.height = height;
    }

    public Places getPlaces() {
        return places;
    }

    public double getWidth() {
        return width;
    }

    public double getHeight() {
        return height;
    }

    public String getAdresseIP() {
        return adresseIP;
    }

    public void setAdresseIP(String adresseIP) {
        this.adresseIP = adresseIP;
    }

    public int getId() {
        return id;
    }

}