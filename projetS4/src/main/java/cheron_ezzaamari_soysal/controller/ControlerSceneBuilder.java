package cheron_ezzaamari_soysal.controller;

import com.itextpdf.text.Document;
import com.itextpdf.text.pdf.PdfWriter;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Cursor;
import javafx.scene.SnapshotParameters;
import javafx.scene.control.MenuItem;
import javafx.scene.control.*;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;
import javafx.scene.input.*;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.util.Duration;
import cheron_ezzaamari_soysal.model.*;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.TreeMap;

public class ControlerSceneBuilder {
    private List<SousReseau> listReseaux = new ArrayList<>();
    private Composant[] liaisonsCompTab = new Composant[2];
    private List<LiaisonComposant> liaisonComposants = new ArrayList<>();
    private ImageView source;
    private ImageView source2;
    private Tooltip tooltip;
    @FXML
    public Pane destination;
    private int lien = 0;

    @FXML
    // Se déclenche lorsque l'on sélectionne un élément dans la barre de gauche
    public void handlerDragDetect(MouseEvent event) {
        source = ((ImageView) event.getSource());
        if (event.isPrimaryButtonDown() && !source.getId().equals("câble") && lien == 0) {
            // ne permet de n'utiliser que le clique gauche de la souris pour déplacer les
            // éléments
            Dragboard db = source.startDragAndDrop(TransferMode.ANY);
            ClipboardContent cb = new ClipboardContent();
            cb.putImage(source.getImage());
            db.setContent(cb);
            event.consume();
        }
    }

    @FXML
    // Se déclenche lorsque l'on reste appuyer sur l'élément
    public void handlerDragOver(DragEvent event) {
        event.acceptTransferModes(TransferMode.COPY_OR_MOVE);
    }

    int indexReseau = 0;

    @FXML
    // Se déclenche lorsque l'on relâche l'élément
    public void handlerDragDrop(DragEvent event) {
        Image img = event.getDragboard().getImage();
        source2 = new ImageView();
        source2.setImage(img);

        source2.setFitHeight(75.0);
        source2.setFitWidth(75.0);
        source2.setPreserveRatio(true);

        // event.getX() == Coordonnée X du curseur dans la fenêtre
        // img.getWidth() == Largeur de l'image d'origine avant modification
        // source.getFitWidth() == Largeur image redimensionnée
        // Le code ci-dessous permet de centrer l'image sur le curseur de la souris
        double imageOrigineCentreX = img.getWidth() / 2;
        double imageOrigineCentreY = img.getHeight() / 2;
        double nouveauCentreX = (imageOrigineCentreX * source.getFitWidth()) / img.getWidth();
        double nouveauCentreY = (imageOrigineCentreY * source.getFitHeight()) / img.getHeight();
        source2.setX(event.getX() - nouveauCentreX);
        source2.setY(event.getY() - nouveauCentreY);

        if (source.getId().equalsIgnoreCase("reseau")) {
            SousReseau reseau = new SousReseau("0.0.0.0");
            listReseaux.add(reseau);
            indexReseau++;
            source2.setId("reseau");
            listReseaux.get(indexReseau - 1).setImg(source2);
            zoomable(listReseaux.get(indexReseau - 1));
            destination.getChildren().add(source2);
            menu(reseau);
        } else {
            if (indexReseau > 0) {
                SousReseau sr = null;
                for (SousReseau Reseau : listReseaux) {
                    if (Reseau.getPlaces().isIn(source2.getX(), source2.getY())) {
                        sr = Reseau;
                        break;
                    }
                }
                if (sr != null) {
                    Composant cpt = null;
                    if (source.getId().equalsIgnoreCase("routeur")) {
                        source2.setId("routeur");
                        cpt = new Routeur(source2, "168.199.1.0");
                        cpt.setNom("routeur");// il me faut le type pour la sauvegarde
                    } else if (source.getId().equalsIgnoreCase("ordinateur")) {
                        source2.setId("ordinateur");
                        cpt = new Ordinateur(source2, "168.199.1.0");
                        cpt.setNom("ordinateur");
                    } else if (source.getId().equalsIgnoreCase("switch")) {
                        source2.setId("switch");
                        cpt = new Switch(source2, "168.199.1.0");
                        cpt.setNom("switch");
                    } else if (source.getId().equalsIgnoreCase("serveur")) {
                        source2.setId("server");
                        cpt = new Serveur(source2, "168.199.1.0");
                        cpt.setNom("serveur");
                    }
                    if (cpt != null) {
                        destination.getChildren().add(cpt.getImg());
                        sr.setComposants(cpt);
                        makeDraggable(cpt);
                        OnClickLiaison(cpt);
                        source2.setId(String.valueOf(cpt.getId()));
                        menu(cpt);
                    }
                } else {
                    Alert info = new Alert(Alert.AlertType.WARNING);
                    info.setHeaderText("Un composant doit être placé dans un réseau");
                    info.show();
                }
            } else {
                Alert info = new Alert(Alert.AlertType.WARNING);
                info.setHeaderText("Veuillez d'abord ajouter un réseau");
                info.show();
            }
        }
    }

    public void zoomable(Reseau reseau) {
        final double[] xy = new double[2];
        final double[] xy2 = new double[2];
        ImageView img = reseau.getImg();
        SousReseau sr = null;
        for (SousReseau sousReseau : listReseaux) {
            if (reseau.getId() == sousReseau.getId()) {
                sr = sousReseau;
                break;
            }
        }
        if (lien == 0 && sr != null) {
            SousReseau finalSr = sr;
            reseau.getImg().setOnMousePressed(me -> {
                xy[0] = reseau.getImg().getFitWidth();
                xy[1] = reseau.getImg().getFitHeight();

                xy2[0] = me.getX();
                xy2[1] = me.getY();
            });

            reseau.getImg().setOnMouseDragged(me -> {
                if (me.isPrimaryButtonDown() && !me.isControlDown()) {
                    destination.getScene().setCursor(Cursor.N_RESIZE);
                    if ((xy[0] + (me.getX() - xy2[0])) > 0 || (xy[1] + (me.getY() - xy2[1]) > 0)) { // sinon ça bug
                        img.setFitWidth(xy[0] + (me.getX() - xy2[0]));
                        img.setFitHeight(xy[1] + (me.getY() - xy2[1]));
                    }
                } else if (me.isControlDown() && me.isPrimaryButtonDown()) {
                    destination.getScene().setCursor(Cursor.MOVE);
                    img.setLayoutX(img.getLayoutX() + me.getX() - xy2[0]);
                    img.setLayoutY(img.getLayoutY() + me.getY() - xy2[1]);
                    for (Composant composant : finalSr.getComposants()) {
                        composant.getImg().setLayoutX(img.getLayoutX() + me.getX() - xy2[0]);
                        composant.getImg().setLayoutY(img.getLayoutY() + me.getY() - xy2[1]);
                        for (LiaisonComposant liaisonComposant : liaisonComposants) {
                            if (liaisonComposant.getArrivee() == composant
                                    || liaisonComposant.getDepart() == composant) {
                                ImageView depart = liaisonComposant.getDepart().getImg();
                                ImageView arrivee = liaisonComposant.getArrivee().getImg();
                                liaisonComposant.getLine().setStartX(depart.getX() + 40 + depart.getLayoutX());
                                liaisonComposant.getLine().setStartY(depart.getY() + 25 + depart.getLayoutY());
                                liaisonComposant.getLine().setEndX(arrivee.getX() + 40 + arrivee.getLayoutX());
                                liaisonComposant.getLine().setEndY(arrivee.getY() + 25 + arrivee.getLayoutY());
                            }
                        }
                    }
                }
            });
            // on met à jour les positions et le curseur
            img.setOnMouseReleased(me -> {
                // remise du curseur à défault
                destination.getScene().setCursor(Cursor.DEFAULT);
                // maj position du réseau
                img.setX(img.getX() + img.getLayoutX());
                img.setY(img.getY() + img.getLayoutY());
                reseau.setPlaces();
                img.setLayoutX(0);
                img.setLayoutY(0);
                for (Composant composant : finalSr.getComposants()) {
                    composant.getImg().setX(composant.getImg().getLayoutX() + composant.getImg().getX());
                    composant.getImg().setY(composant.getImg().getLayoutY() + composant.getImg().getY());
                    composant.getImg().setLayoutX(0);
                    composant.getImg().setLayoutY(0);
                    for (LiaisonComposant liaisonComposant : liaisonComposants) {
                        if (liaisonComposant.getArrivee() == composant || liaisonComposant.getDepart() == composant) {
                            liaison(liaisonComposant);
                        }
                    }
                }
            });
        }
    }

    public void OnClickLiaison(Composant cp) {// se déclenche lorsqu’on clique sur un composant
        cp.getImg().setOnMouseClicked(me -> {
            if (lien == 1) {
                if (liaisonsCompTab[0] == null && liaisonsCompTab[1] == null) {
                    liaisonsCompTab[0] = cp;
                } else if (liaisonsCompTab[0] != null && liaisonsCompTab[1] == null) {
                    if (liaisonsCompTab[0] != cp) {
                        liaisonsCompTab[1] = cp;
                        boolean b = false;
                        // on vérifie si une liaison entre ces deux composants n'existe pas déjà
                        for (LiaisonComposant liaisonComposant : liaisonComposants) {
                            Composant depart = liaisonComposant.getDepart();
                            Composant arrivee = liaisonComposant.getArrivee();
                            if ((arrivee == liaisonsCompTab[0] && depart == liaisonsCompTab[1])
                                    || (arrivee == liaisonsCompTab[1] && depart == liaisonsCompTab[0])) {
                                b = true;
                                break;
                            }
                        }
                        if (!b) {
                            LiaisonComposant l = new LiaisonComposant(liaisonsCompTab[0], liaisonsCompTab[1]);
                            liaison(l);
                        } else {
                            Alert info = new Alert(Alert.AlertType.WARNING);
                            info.setHeaderText("Ces deux composants sont déjà reliés");
                            info.show();
                        }
                    }
                } else {
                    liaisonsCompTab[1] = null;
                    liaisonsCompTab[0] = cp;
                }
            }
        });
    }

    public void liaison(LiaisonComposant l) {
        if (l.getLine() == null) { // ligne qui n existe pas encore donc on la crée
            Line line = new Line();
            line.setStartX(l.getDepart().getImg().getX() + 40);
            line.setStartY(l.getDepart().getImg().getY() + 25);
            line.setEndX(l.getArrivee().getImg().getX() + 40);
            line.setEndY(l.getArrivee().getImg().getY() + 25);
            line.setStrokeWidth(2);
            line.setStroke(Color.BLACK);
            line.setCursor(Cursor.HAND);
            line.setOnMouseClicked(me -> OnClickLigne(line));
            l.setLine(line);
            destination.getChildren().addAll(line);
            liaisonComposants.add(l);
        } else {
            l.getLine().setStartX(l.getDepart().getImg().getX() + 40);
            l.getLine().setStartY(l.getDepart().getImg().getY() + 25);
            l.getLine().setEndX(l.getArrivee().getImg().getX() + 40);
            l.getLine().setEndY(l.getArrivee().getImg().getY() + 25);
        }
    }

    public void lier(MouseEvent event) {
        if (lien == 0) {
            lien = 1;// on peut lier
            source = ((ImageView) event.getSource());
            DropShadow dropShadow = new DropShadow();
            dropShadow.setRadius(5.0);
            dropShadow.setOffsetX(3.0);
            dropShadow.setOffsetY(3.0);
            dropShadow.setColor(Color.color(0.4, 0.4, 0.7));
            source.setEffect(dropShadow);
        } else if (lien == 1) {
            lien = 0;// on ne peut plus
            source.setEffect(null);
        }
    }

    private void makeDraggable(Composant composant) {
        ImageView img = composant.getImg();
        // on récupère le réseau dans lequel se trouve le composant
        SousReseau sr = null;
        for (SousReseau sousReseau : listReseaux) {
            if (sousReseau.getComposants().contains(composant)) {
                sr = sousReseau;
                break;
            }
        }
        System.out.println(sr);
        if (lien == 0 && sr != null) {
            final double[] xy = new double[2];

            img.setOnMousePressed(me -> {
                xy[0] = me.getX();
                xy[1] = me.getY();
            });

            // !\ il faut interdire le composant de sortir du réseau dans lequel il
            // appartient
            SousReseau finalSr = sr;
            img.setOnMouseDragged(me -> {
                if (me.isPrimaryButtonDown()) {
                    destination.getScene().setCursor(Cursor.HAND);
                    img.setLayoutX(img.getLayoutX() + me.getX() - xy[0]);
                    img.setLayoutY(img.getLayoutY() + me.getY() - xy[1]);
                    for (LiaisonComposant liaisonComposant : liaisonComposants) {
                        if (liaisonComposant.getArrivee() == composant || liaisonComposant.getDepart() == composant) {
                            ImageView depart = liaisonComposant.getDepart().getImg();
                            ImageView arrivee = liaisonComposant.getArrivee().getImg();
                            liaisonComposant.getLine().setStartX(depart.getX() + 40 + depart.getLayoutX());
                            liaisonComposant.getLine().setStartY(depart.getY() + 25 + depart.getLayoutY());
                            liaisonComposant.getLine().setEndX(arrivee.getX() + 40 + arrivee.getLayoutX());
                            liaisonComposant.getLine().setEndY(arrivee.getY() + 25 + arrivee.getLayoutY());
                        }
                    }

                }
            });

            // lorsqu'on lâche l'élément on le met à jour ainsi que ses liaisons
            // https://docs.oracle.com/javase/7/docs/api/java/awt/event/MouseEvent.html#MOUSE_DRAGGED
            img.setOnMouseReleased(me -> {
                destination.getScene().setCursor(Cursor.DEFAULT);
                double x = img.getX() + img.getLayoutX();
                double y = img.getY() + img.getLayoutY();
                if (finalSr.getPlaces().isIn(x, y)) {
                    composant.getImg().setX(x);
                    composant.getImg().setY(y);
                } else {
                    Alert info = new Alert(Alert.AlertType.WARNING);
                    info.setHeaderText("Le composant doit rester dans son réseau actuel");
                    info.setContentText("PS: Vous ne pouvez pas déplacer ce composant dans un autre réseau");
                    info.show();
                }
                img.setLayoutX(0);
                img.setLayoutY(0);
                for (LiaisonComposant liaisonComposant : liaisonComposants) {
                    if (liaisonComposant.getArrivee() == composant || liaisonComposant.getDepart() == composant) {
                        liaison(liaisonComposant);
                    }
                }
            });
        }
    }

    private void getInfos(Composant c) {
        if (c != null) {
            Alert info = new Alert(Alert.AlertType.INFORMATION);
            info.setTitle("Caractéristiques");
            info.setHeaderText(c.getNom());
            String ip = c.getAdresseIP();
            double posx = c.getImg().getX();
            double posy = c.getImg().getY();
            info.setContentText("Adresse ip : " + ip + "\n" + "X : " + posx + "\n" + "Y : " + posy);
            info.show();
        }
    }

    private void getInfos(Reseau r) {
        SousReseau sr = null;
        for (SousReseau sousReseau : listReseaux) {
            if (r.getId() == sousReseau.getId()) {
                sr = sousReseau;
                break;
            }
        }
        if (sr != null) {
            Alert info = new Alert(Alert.AlertType.INFORMATION);
            info.setTitle("Caractéristiques");
            info.setHeaderText("Réseau");
            String ip = r.getAdresseIP();
            List<Composant> c = sr.getComposants();
            info.setContentText(
                    "Adresse ip : " + ip + "\n" + "Nombre de composant : " + c.size() + "\n" + "Nombre de câble : "
                            + liaisonComposants.stream()
                                    .filter(liaisonComposant -> c.contains(liaisonComposant.getArrivee())
                                            || c.contains(liaisonComposant.getDepart()))
                                    .count()
                            + "\n");
            info.show();
        }
    }

    // https://o7planning.org/fr/11115/tutoriel-javafx-contextmenu
    public void OnClickLigne(Line ligne) {
        ContextMenu contextMenu = new ContextMenu();
        MenuItem supp = new MenuItem("Supprimer");
        contextMenu.getItems().addAll(supp);
        supp.setOnAction((ActionEvent event) -> {
            liaisonComposants.removeIf(liaisonComposant -> liaisonComposant.getLine() == ligne);
            destination.getChildren().remove(ligne);
        });
        ligne.setOnContextMenuRequested(
                (ContextMenuEvent event) -> contextMenu.show(ligne, event.getScreenX(), event.getScreenY()));
    }

    private void menu(Reseau r) {
        if (r != null) {
            ContextMenu contextMenu = new ContextMenu();// Créer un menu lors du clique droit
            MenuItem infos = new MenuItem("Caractéristiques");// Initialisation des champs dans le menu
            MenuItem supp = new MenuItem("Supprimer");
            MenuItem setInfo = new MenuItem("Changer l'adresse");
            contextMenu.getItems().addAll(infos, supp, setInfo);

            infos.setOnAction((ActionEvent event) -> getInfos(r));
            setInfo.setOnAction((ActionEvent event) -> saisirIp(r));
            supp.setOnAction((ActionEvent event) -> {
                // on demande la validation avant
                Alert info = new Alert(Alert.AlertType.CONFIRMATION);
                info.setHeaderText("Action à risque");
                info.setContentText(
                        "Cette action effacera tout le contenu de votre réseau, êtes-vous sûr de vouloir continuer ?");
                info.showAndWait();
                if (info.getResult().getText().equals("OK")) {
                    // si c'est un réseau on supprime tous les éléments de celui-ci
                    SousReseau sr = null;
                    for (SousReseau sousReseau : listReseaux) {
                        if (r.getId() == sousReseau.getId()) {
                            sr = sousReseau;
                            break;
                        }
                    }
                    if (sr != null) {
                        for (Composant c : sr.getComposants()) {
                            destination.getChildren().remove(c.getImg());
                            // on supprime aussi les liaisons des composants
                            for (LiaisonComposant liaisonComposant : liaisonComposants) {
                                if (liaisonComposant.getArrivee() == c || liaisonComposant.getDepart() == c) {
                                    destination.getChildren().remove(liaisonComposant.getLine());
                                }
                            }
                            liaisonComposants.removeIf(liaisonComposant -> liaisonComposant.getArrivee() == c
                                    || liaisonComposant.getDepart() == c);
                        }
                        sr.getComposants().clear();
                    }
                    listReseaux.remove(sr);
                    indexReseau--;
                    destination.getChildren().remove(r.getImg());
                }
            });
            r.getImg().setOnContextMenuRequested(
                    (ContextMenuEvent event) -> contextMenu.show(r.getImg(), event.getScreenX(), event.getScreenY()));
        }
    }

    private void menu(Composant cp) {
        if (cp != null) {
            ContextMenu contextMenu = new ContextMenu();// Créer un menu lors du clique droit
            MenuItem infos = new MenuItem("Caractéristiques");// Initialisation des champs dans le menu
            MenuItem supp = new MenuItem("Supprimer");
            MenuItem setInfo = new MenuItem("Changer l'adresse");
            contextMenu.getItems().addAll(infos, supp, setInfo);

            infos.setOnAction((ActionEvent event) -> getInfos(cp));
            setInfo.setOnAction((ActionEvent event) -> saisirIp(cp));
            supp.setOnAction((ActionEvent event) -> {
                // demander validation avant ?
                // on récupère le réseau dans lequel se trouve le composant
                SousReseau sr = null;
                for (SousReseau sousReseau : listReseaux) {
                    if (sousReseau.getComposants().contains(cp)) {
                        sr = sousReseau;
                    }
                }
                // on supprime les liaisons du composant
                for (LiaisonComposant liaisonComposant : liaisonComposants) {
                    if (liaisonComposant.getArrivee() == cp || liaisonComposant.getDepart() == cp) {
                        destination.getChildren().remove(liaisonComposant.getLine());
                    }
                }
                liaisonComposants.removeIf(
                        liaisonComposant -> liaisonComposant.getArrivee() == cp || liaisonComposant.getDepart() == cp);
                // on retire le composant de son réseau
                if (sr != null) {
                    sr.getComposants().remove(cp);
                }
                // on retire l'image du composant dans notre schéma
                destination.getChildren().remove(cp.getImg());
            });
            cp.getImg().setOnContextMenuRequested(
                    (ContextMenuEvent event) -> contextMenu.show(cp.getImg(), event.getScreenX(), event.getScreenY()));
        }
    }

    // source
    // http://remy-manu.no-ip.biz/Java/Tutoriels/JavaFX/PDF/ihm1_fx_10_man.pdf
    private void saisirIp(Composant cp) {// Fonction qui permet de generer l'alert pour changer l'IP d'un composant
        if (cp != null) {
            TextInputDialog inDialog = new TextInputDialog();
            inDialog.setTitle("adressage");
            inDialog.setHeaderText("Saisissez l'adresse ip");
            inDialog.setContentText("Ip actuelle :" + cp.getAdresseIP() + "\n" + "Nouvelle Ip : ");
            Optional<String> textIn = inDialog.showAndWait();// il permet de recuperer ce qui à été saisie
            // s'il n'est pas vide
            if (textIn.isPresent()) {
                if (!textIn.get().equals("") && textIn.get()
                        .matches("^(([0-9]|[1-9][0-9]|1[0-9][0-9]|2[0-4][0-9]|25[0-5])(\\.(?!$)|$)){4}$")) {
                    cp.setAdresseIP(textIn.get());
                } else {
                    Alert info = new Alert(Alert.AlertType.WARNING);
                    info.setHeaderText("Seules les adresses du type IpV4 sont autorisées");
                    info.setContentText(
                            "Une adresse IPv4 (Internet Protocol version 4) est une identification unique pour un hôte sur un réseau IP. Une adresse IP est un nombre d'une valeur de 32 bits représentée par 4 valeurs décimales pointées ; chacune a un poids de 8 bits (1 octet) prenant des valeurs décimales de 0 à 255 séparées par des points.");
                    info.show();
                }
            }
        }
    }

    private void saisirIp(Reseau r) {// Fonction qui permet de generer l'alert pour changer l'IP d'un composant
        if (r != null) {
            TextInputDialog inDialog = new TextInputDialog();
            inDialog.setTitle("adressage");
            inDialog.setHeaderText("Saisissez l'adresse ip");
            inDialog.setContentText("Ip actuelle :" + r.getAdresseIP() + "\n" + "Nouvelle Ip : ");
            Optional<String> textIn = inDialog.showAndWait();// il permet de recuperer ce qui à été saisie
            // s'il n'est pas vide
            if (textIn.isPresent()) {
                if (!textIn.get().equals("") && textIn.get()
                        .matches("^(([0-9]|[1-9][0-9]|1[0-9][0-9]|2[0-4][0-9]|25[0-5])(\\.(?!$)|$)){4}$")) {
                    r.setAdresseIP(textIn.get());
                } else {
                    Alert info = new Alert(Alert.AlertType.WARNING);
                    info.setHeaderText("Seules les adresses du type IpV4 sont autorisées");
                    info.setContentText(
                            "Une adresse IPv4 (Internet Protocol version 4) est une identification unique pour un hôte sur un réseau IP. Une adresse IP est un nombre d'une valeur de 32 bits représentée par 4 valeurs décimales pointées ; chacune a un poids de 8 bits (1 octet) prenant des valeurs décimales de 0 à 255 séparées par des points.");
                    info.show();
                }
            }
        }
    }

    @FXML
    public void SaveAs() {
        if (listReseaux.size() > 0) {// si le schéma n'est pas vide on peut enregistrer
            try {
                JFileChooser chooser = new JFileChooser();
                // Dossier Courant
                chooser.setCurrentDirectory(new File("." + File.separator));
                // Affichage et récupération de la réponse de l'utilisateur
                int reponse = chooser.showDialog(chooser, "Enregistrer sous");
                // Si l'utilisateur clique sur OK
                if (reponse == JFileChooser.APPROVE_OPTION) {
                    // Récupération du chemin du fichier
                    String fichier = chooser.getSelectedFile().toString();
                    if (fichier.contains(".json")) {
                        fichier = fichier.replace(".json", "");
                    }
                    // Ecriture du fichier
                    // https://www.codeflow.site/fr/article/java-org-json
                    // https://www.cyril-rabat.fr/articles/index.php?article=50
                    // https://www.codeflow.site/fr/article/java__json-simple-example-read-and-write-json
                    JSONObject obj = new JSONObject();
                    JSONArray list = new JSONArray();
                    for (SousReseau s : listReseaux) {
                        JSONObject sr = new JSONObject();
                        sr.put("id", s.getId());
                        sr.put("name", "réseau");
                        sr.put("IP", s.getAdresseIP());
                        sr.put("width", s.getImg().getFitWidth());
                        sr.put("height", s.getImg().getFitHeight());
                        sr.put("posx", s.getImg().getX());
                        sr.put("posy", s.getImg().getY());
                        JSONArray components = new JSONArray();
                        for (Composant c : s.getComposants()) {
                            JSONObject cp = new JSONObject();
                            cp.put("id", c.getId());
                            cp.put("name", c.getNom());
                            cp.put("IP", c.getAdresseIP());
                            cp.put("posx", c.getImg().getX());
                            cp.put("posy", c.getImg().getY());
                            components.put(cp);
                        }
                        sr.put("components", components);
                        list.put(sr);
                    }
                    obj.put("réseaux", list);

                    // on stock toutes les liaisons dans une autre liste
                    JSONArray liaisons = new JSONArray();
                    for (LiaisonComposant lc : liaisonComposants) {
                        JSONObject lien = new JSONObject();
                        lien.put("id_1", lc.getDepart().getId());
                        lien.put("id_2", lc.getArrivee().getId());
                        liaisons.put(lien);
                    }
                    obj.put("liaisons", liaisons);

                    try (FileWriter file = new FileWriter(fichier + ".json")) {

                        file.write(obj.toString());
                        file.flush();

                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            } catch (HeadlessException he) {
                he.printStackTrace();
            }
        }
    }

    @FXML
    public void Import() {
        try {
            // vérifié s'il a sauvegarder son schéma courant (lui demander la permission)
            // faire un "popup" pour demander la validation de l'utilisateur
            JFileChooser chooser = new JFileChooser();
            // Dossier Courant
            chooser.setCurrentDirectory(new File("." + File.separator));
            // Affichage et récupération de la réponse de l'utilisateur
            int reponse = chooser.showDialog(chooser, "Ouvrir");
            // Si l'utilisateur clique sur OK
            if (reponse == JFileChooser.APPROVE_OPTION) {
                // Récupération du chemin du fichier
                String fichier = chooser.getSelectedFile().toString();
                if (fichier.contains(".json")) {
                    // http://guyonst.free.fr/android/lectureJSON.pdf
                    // lecture du fichier avec un buffer
                    StringBuilder sb = new StringBuilder();
                    BufferedReader br = null;
                    try {
                        br = new BufferedReader(new FileReader(fichier));
                        String temp;
                        while ((temp = br.readLine()) != null) {
                            sb.append(temp);
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    } finally {
                        try {
                            assert br != null;
                            br.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                    String myjsonstring = sb.toString();// on récupère le contenu du fichier
                    // effacé le schéma déjà présent
                    New();
                    if (listReseaux.size() <= 0) {
                        try {
                            JSONObject jsonO = new JSONObject(myjsonstring);
                            JSONArray jsonArray = jsonO.optJSONArray("réseaux");
                            // création d'un liste clé/valeur contenant l'id du composant actuel et son
                            // nouveau
                            TreeMap<Integer, Composant> tmap = new TreeMap<>();
                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject jsonObject = jsonArray.getJSONObject(i);
                                int id;
                                String nom;
                                String IP = jsonObject.getString("IP");
                                double posx = jsonObject.getDouble("posx");
                                double posy = jsonObject.getDouble("posy");
                                double witdh = jsonObject.getDouble("width");
                                double height = jsonObject.getDouble("height");
                                // on ajoute le réseau
                                SousReseau sr = new SousReseau(IP);
                                Image img = new Image("main/resources/reseau.png");
                                ImageView ViewReseau = AddElement(img, witdh, height, posx, posy, "reseau");
                                listReseaux.add(sr);
                                indexReseau++;
                                listReseaux.get(indexReseau - 1).setImg(ViewReseau);
                                zoomable(listReseaux.get(indexReseau - 1));
                                menu(sr);
                                // on récupère les composants du réseau
                                JSONArray components = jsonObject.getJSONArray("components");
                                for (int j = 0; j < components.length(); j++) {
                                    JSONObject c = components.getJSONObject(j);
                                    id = c.getInt("id");
                                    if (c.isNull("name")) {
                                        nom = "routeur";
                                    } else {
                                        nom = c.getString("name");
                                    }
                                    IP = c.getString("IP");
                                    posx = c.getDouble("posx");
                                    posy = c.getDouble("posy");
                                    switch (nom) {
                                    case "routeur" -> img = new Image("main/resources/router.png");
                                    case "ordinateur" -> img = new Image("main/resources/pc_editeur.png");
                                    case "serveur" -> img = new Image("main/resources/server_edit.png");
                                    case "switch" -> img = new Image("main/resources/switch_edit.png");
                                    }
                                    // on ajoute le composant
                                    ImageView Component = AddElement(img, 75.0, 75.0, posx, posy, nom);
                                    Composant cp = new Composant(Component, IP);
                                    tmap.put(id, cp);
                                    cp.setNom(nom);
                                    sr.getComposants().add(cp);
                                    makeDraggable(cp);
                                    OnClickLiaison(cp);
                                    menu(cp);
                                }
                            }
                            // de même pour les liaisons
                            jsonArray = jsonO.optJSONArray("liaisons");
                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject jsonObject = jsonArray.getJSONObject(i);
                                int id_1 = jsonObject.getInt("id_1");
                                int id_2 = jsonObject.getInt("id_2");
                                LiaisonComposant l = new LiaisonComposant(tmap.get(id_1), tmap.get(id_2));
                                liaison(l);
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                } else {
                    Alert info = new Alert(Alert.AlertType.INFORMATION);
                    info.setTitle("Erreur de format");
                    info.setContentText("Seuls les fichiers JSON sont autorisés");
                    info.show();
                }
            }
        } catch (HeadlessException he) {
            he.printStackTrace();
        }
    }

    public ImageView AddElement(Image img, double width, double height, double posx, double posy, String name) {
        source2 = new ImageView();
        source2.setImage(img);
        source2.setFitHeight(height);
        source2.setFitWidth(width);
        source2.setPreserveRatio(true);
        source2.setX(posx);
        source2.setY(posy);
        source2.setId(name);
        destination.getChildren().add(source2);
        return source2;
    }

    public void New() {
        // on demande la validation avant
        Alert info = new Alert(Alert.AlertType.CONFIRMATION);
        info.setHeaderText("Cette action effacera votre schéma courant, êtes-vous sûr de vouloir continuer ?");
        info.setContentText("Si vous avez effectuez une sauvegarde vérifier bien son fonctionnement");
        info.showAndWait();
        if (info.getResult().getText().equals("OK")) {
            listReseaux.clear();
            destination.getChildren().clear();
            liaisonsCompTab = new Composant[2];
            liaisonComposants = new ArrayList<>();
            indexReseau = 0;
            lien = 0;
        }
    }

    @FXML
    public void hover(MouseEvent event) {
        source = ((ImageView) event.getSource());
        if (tooltip == null) {
            tooltip = new Tooltip(source.getId());
        } else {
            if (!tooltip.getText().equals(source.getId())) {
                tooltip = new Tooltip(source.getId());
            }
        }
        tooltip.setAutoHide(true);
        tooltip.setHideDelay(new Duration(1));
        tooltip.setPrefWidth(100);
        tooltip.setWrapText(true);
        tooltip.show(source, event.getScreenX() + 35, event.getScreenY());
        source.setOnMouseExited(me -> {
            if (tooltip != null) {
                tooltip.hide();
            }
        });
    }

    @FXML
    public void ExportToPng() throws IOException {
        if (listReseaux.size() > 0) {// si le schéma n'est pas vide on peut l'exporter
            try {
                JFileChooser chooser = new JFileChooser();
                // Dossier Courant
                chooser.setCurrentDirectory(new File("." + File.separator));
                // Affichage et récupération de la réponse de l'utilisateur
                int reponse = chooser.showDialog(chooser, "Enregistrer sous");
                // Si l'utilisateur clique sur OK
                if (reponse == JFileChooser.APPROVE_OPTION) {
                    // Récupération du chemin du fichier
                    String fichier = chooser.getSelectedFile().toString();
                    if (fichier.contains(".png")) {
                        fichier = fichier.replace(".png", "");
                    }
                    WritableImage image = destination.snapshot(new SnapshotParameters(), null);
                    File file = new File(fichier + ".png");
                    ImageIO.write(SwingFXUtils.fromFXImage(image, null), "png", file);
                }
            } catch (HeadlessException he) {
                he.printStackTrace();
            }
        }
    }

    // Librairie : http://www.java2s.com/Code/Jar/i/Downloaditextpdf510jar.htm
    // http://java.mesexemples.com/fichiersrepertoires/java-creer-un-fichier-pdf/
    @FXML
    public void ExportToPdf() {
        if (listReseaux.size() > 0) {// si le schéma n'est pas vide on peut l'exporter
            try {
                JFileChooser chooser = new JFileChooser();
                // Dossier Courant
                chooser.setCurrentDirectory(new File("." + File.separator));
                // Affichage et récupération de la réponse de l'utilisateur
                int reponse = chooser.showDialog(chooser, "Enregistrer sous");
                // Si l'utilisateur clique sur OK
                if (reponse == JFileChooser.APPROVE_OPTION) {
                    // Récupération du chemin du fichier
                    String fichier = chooser.getSelectedFile().toString();
                    if (fichier.contains(".png")) {
                        fichier = fichier.replace(".png", "");
                    }
                    if (fichier.contains(".pdf")) {
                        fichier = fichier.replace(".pdf", "");
                    }
                    WritableImage image = destination.snapshot(new SnapshotParameters(), null);
                    File file = new File(fichier + ".png");
                    ImageIO.write(SwingFXUtils.fromFXImage(image, null), "png", file);

                    // https://stackoverflow.com/questions/8361901/how-can-i-convert-a-png-file-to-pdf-using-java
                    Document document = new Document();
                    String input = fichier + ".png";
                    String output = fichier + ".pdf";
                    try {
                        FileOutputStream fos = new FileOutputStream(output);
                        PdfWriter writer = PdfWriter.getInstance(document, fos);
                        writer.open();
                        document.open();
                        com.itextpdf.text.Image i = com.itextpdf.text.Image.getInstance(input);
                        i.setAlignment(com.itextpdf.text.Image.TEXTWRAP);
                        i.scaleAbsolute(154, 94);
                        i.scalePercent(50);
                        document.add(i);
                        document.close();
                        writer.close();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    file.delete();
                }
            } catch (HeadlessException | IOException he) {
                he.printStackTrace();
            }
        }
    }
}