package cheron_ezzaamari_soysal.model;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import javafx.scene.image.ImageView;

class PlacesTest {

    @ParameterizedTest(name = "Les positions x {0} et y {1} doivent être comprit dans l'image")
    @CsvSource({ "0, 0", "10, 0", "0, 10", "49, 10", "10, 49", "49, 49" })
    void test_isIn_vrais(int x, int y) {
        Places places = new Places(genererImage(0, 0, 50, 50));
        assertTrue(places.isIn(x, y));
    }

    @ParameterizedTest(name = "Les positions x {0} et y {1} ne doivent pas être comprit dans l'image")
    @CsvSource({ "-1, 0", "0, -1", "-1, -1", "51, 10", "10, 51", "51, 51" })
    void test_isIn_faux(int x, int y) {
        Places places = new Places(genererImage(0, 0, 50, 50));
        assertFalse(places.isIn(x, y));
    }

    @Test
    void test_constructeur_sansErreur() {
        assertAll(() -> new Places(genererImage(0, 0, 0, 0)));
    }

    private static ImageView genererImage(double posX, double posY, double largeur, double hauteur) {
        ImageView result = new ImageView();
        result.setX(posX);
        result.setY(posY);
        result.setFitHeight(hauteur);
        result.setFitWidth(largeur);
        return result;
    }

}
