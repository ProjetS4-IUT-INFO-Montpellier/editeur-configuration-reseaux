package cheron_ezzaamari_soysal.model;

import javafx.scene.image.ImageView;

public class Ordinateur extends Composant {

    public Ordinateur() {
        super("Ordinateur");
    }

    public Ordinateur(ImageView imageView, String adresseIP) {
        super(imageView, adresseIP);
    }
}