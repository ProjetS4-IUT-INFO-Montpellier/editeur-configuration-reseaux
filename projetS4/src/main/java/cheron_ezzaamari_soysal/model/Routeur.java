package cheron_ezzaamari_soysal.model;

import javafx.scene.image.ImageView;

public class Routeur extends Composant {

    public Routeur() {
        super("Routeur");
    }

    public Routeur(ImageView imageView, String adresseIP) {
        super(imageView, adresseIP);
    }
}