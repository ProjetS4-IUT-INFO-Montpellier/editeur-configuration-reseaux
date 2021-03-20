package cheron_ezzaamari_soysal.model;

import java.util.ArrayList;
import java.util.List;

/**
 * 
 */
public class ReseauCompose extends Reseau {
    private List<Reseau> sousreseaux = new ArrayList<Reseau>();

    public ReseauCompose(String adresseIP) {
        super(adresseIP);
    }

    public List<Reseau> getSousreseaux() {
        return sousreseaux;
    }

    public void setSousreseaux(List<Reseau> sousreseaux) {
        this.sousreseaux = sousreseaux;
    }
}