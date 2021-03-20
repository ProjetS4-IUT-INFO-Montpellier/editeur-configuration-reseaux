package cheron_ezzaamari_soysal.model;

import javafx.scene.image.ImageView;

public class Places {
    private double x;
    private double y;
    private double droite;
    private double gauche;
    private double haut;
    private double bas;

    public Places(ImageView img) {
        this.x = img.getX();
        this.y = img.getY();
        this.droite = img.getX() + img.getFitWidth() / 1.45;
        this.gauche = img.getX();
        this.haut = img.getY() + img.getFitHeight() / 8;
        this.bas = img.getY() + img.getFitHeight() / 1.45;
    }

    public boolean isIn(double x, double y) {
        return (x < droite) && (x > gauche) && (y > haut) && (y < bas);
    }
}
