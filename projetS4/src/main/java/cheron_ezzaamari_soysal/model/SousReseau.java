package cheron_ezzaamari_soysal.model;

import java.util.ArrayList;
import java.util.List;

/**
 * 
 */
public class SousReseau extends Reseau {

    private List<Composant> composants = new ArrayList<Composant>();


    public SousReseau(String adresseIP) {
        super(adresseIP);
    }

    public List<Composant> getComposants() {
        return composants;
    }

    public void setComposants(Composant composant) {
        this.composants.add(composant);
    }

}