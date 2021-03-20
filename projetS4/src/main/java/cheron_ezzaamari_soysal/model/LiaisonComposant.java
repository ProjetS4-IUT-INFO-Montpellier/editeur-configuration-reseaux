package cheron_ezzaamari_soysal.model;

import javafx.scene.shape.Line;

public class LiaisonComposant {
    Composant depart;
    Composant arrivee;
    Line line;

    public LiaisonComposant(Composant depart, Composant arrivee) {
        this.depart = depart;
        this.arrivee = arrivee;
    }

    public void setLine(Line line) {
        this.line = line;
    }

    public Line getLine() {
        return line;
    }

    public Composant getDepart() {
        return depart;
    }

    public Composant getArrivee() {
        return arrivee;
    }

    public void setDepart(Composant depart) {
        this.depart = depart;
    }

    public void setArrivee(Composant arrivee) {
        this.arrivee = arrivee;
    }
}
