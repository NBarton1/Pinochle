package com.example.pinochle;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import java.net.URL;
import java.util.*;


public class Pinochle implements Initializable {

    @FXML
    private Label otherTeamScoreLabel;

    @FXML
    private HBox playerCardsHBOX;

    @FXML
    private HBox teammate;

    @FXML
    private VBox opponent1;

    @FXML
    private VBox opponent2;

    @FXML
    private Label playerTeamScoreLabel;

    @FXML
    private GridPane suitsGrid;

    @FXML
    private VBox playerMeldLayVBox;

    @FXML
    private VBox opponent1MeldLayVBox;

    @FXML
    private VBox teammateMeldLayVBox;

    @FXML
    private VBox opponent2MeldLayVBox;



    private int PLAYERCOUNT;
    private Image BACK;
    private Player[] players;
    private ImageView[] playerCardImages;
    private Deck deck;
    private int playerTeamScore;
    private int otherTeamScore;
    private String trumpSuit;
    private int dealer;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        getVariables();
        playHand();
    }

    /**
     * Initializes variables
     */
    private void getVariables() {
        PLAYERCOUNT = 4;
        playerTeamScore = otherTeamScore = 0;
        BACK = new Image(Objects.requireNonNull(Card.class.getResourceAsStream("/images/back.png")));
        deck = new Deck();
        players = new Player[PLAYERCOUNT];
        for (int i = 0; i < PLAYERCOUNT; i++) {
            players[i] = new Player();
        }
    }

    /**
     * Starts the hand by dealing and appropriately setting images
     */
    private void playHand() {
        clearHBox(playerCardsHBOX);
        clearVBox(opponent1);
        clearHBox(teammate);
        clearVBox(opponent2);

        Card[][] deal = deck.deal();

        for (int i = 0; i < PLAYERCOUNT; i++) {
            players[i].setHand(deal[i]);
        }

        setCards();
        for(int i=0; i<playerCardsHBOX.getChildren().size(); i++) {
            setImageViewUserData(i);
        }
        getPlayerCardImages();

        setSuitsGrid();
    }

    /**
     * Gets an ArrayList of images to easily access them
     */
    private void getPlayerCardImages() {
        playerCardImages = new ImageView[20];
        for(int i = 0; i< playerCardsHBOX.getChildren().size(); i++) {
            playerCardImages[i] = (ImageView) playerCardsHBOX.getChildren().get(i);
        }
    }

    /**
     * Sets data to an ImageView so that it can be accessed when clicked
     * @param i data to input
     */
    private void setImageViewUserData(int i) {
            ImageView imageView = (ImageView) playerCardsHBOX.getChildren().get(i);
            imageView.setUserData(i);

            imageView.setOnMouseClicked(event -> playCard((int) imageView.getUserData()));
    }

    /**
     * ON CLICKING A CARD: if card is legal to play, play the card and remove it from the hand and sort the hand.
     * TODO: all of that
     * @param i card clicked
     */
    private void playCard(int i) {
        //playerCardImages[i].setImage(null);
        //toCenter();
        //remapUserData();

        suitsGrid.setDisable(false);
        suitsGrid.setVisible(true);
        suitsGrid.toFront();
    }

    private void remapUserData() {
        playerCardsHBOX.getChildren().clear();
        for(int i=0; i<playerCardImages.length; i++) {
            try {
                playerCardsHBOX.getChildren().add(buildChild(playerCardImages[i].getImage(), 57, 79, -40*(i+1.5), 0, 0));
                setImageViewUserData(i);
            } catch(NullPointerException ignored) {}
        }
    }



    private void toCenter() {
        int nullCounter = 0;
        ArrayList<Image> notNull = new ArrayList<>();
        int size = playerCardImages.length;

        for(int i=0; i<size; i++) {
            if(playerCardImages[i].getImage() != null)
                notNull.add(playerCardImages[i].getImage());
            else {
                nullCounter++;
            }
        }

        int index = 0;

        for(int i=0; i<size; i++) {
            if(i<nullCounter/2 || i>=20-Math.round((double) nullCounter/2))
                playerCardImages[i].setImage(null);
            else {
                playerCardImages[i].setImage(notNull.get(index));
                index++;
            }
        }
    }

    /**
     * Clears all children from Hbox and Vbox
     */
    private void clearVBox(VBox parent) {
        parent.getChildren().clear();
    }

    private void clearHBox(HBox parent) {
        parent.getChildren().clear();
    }

    /**
     * Takes empty Hbox and Vbox and fills them with cards according to who they are
     */
    private void setCards() {
        for(int i=0; i<20; i++) {
            playerCardsHBOX.getChildren().add(buildChild(players[0].getHand()[i].getImage(), 57, 79, -40*(i+1.5), 0, 0));
            opponent1.getChildren().add(buildChild(BACK, 30, 49, 0, -42*(i), 90));
            teammate.getChildren().add(buildChild(BACK, 30, 49, -23*(i-2), 0, 0));
            opponent2.getChildren().add(buildChild(BACK, 30, 49, 0, -42*(i), 90));
        }
    }

    /**
     * Takes a bunch of parameters and builds a child for a HBox or Vbox
     * @param image image to put in child
     * @param x width
     * @param y height
     * @param dx translation in x direction
     * @param dy translation in y direction
     * @param rot rotation angle in degrees
     * @return result of the child
     */
    private ImageView buildChild(Image image, int x, int y, double dx, double dy, int rot) {
        ImageView imageView = new ImageView(image);
        imageView.setFitWidth(x);
        imageView.setFitHeight(y);
        imageView.setTranslateX(dx);
        imageView.setTranslateY(dy);
        imageView.setRotate(rot);

        return imageView;
    }


    /**
     * Sets the grid used to pick trump suit
     */
    private void setSuitsGrid() {
        for(int i=0; i<suitsGrid.getChildren().size(); i++) {
            ImageView imageView = (ImageView) suitsGrid.getChildren().get(i);
            imageView.setUserData(i);
            imageView.setOnMouseClicked(event -> pickSuit((int) imageView.getUserData()));
        }
        suitsGrid.setVisible(false);
        suitsGrid.setDisable(true);
    }


    /**
     * On click, determine which suit is trump for this hand
     * @param i clicked, which to use as index for suits
     */
    private void pickSuit(int i) {
        String[] suits = new String[] {"S", "C", "H", "D"};
        trumpSuit = suits[i];
        suitsGrid.setVisible(false);

        layMeld();
    }

    private void layMeld() {
        createMeldVBox(playerMeldLayVBox, 0);
        createMeldVBox(opponent1MeldLayVBox, 1);
        createMeldVBox(teammateMeldLayVBox, 2);
        createMeldVBox(opponent2MeldLayVBox, 3);
    }

    private void createMeldVBox(VBox parent, int index) {
        ArrayList<String> meldCards = getMeldCards(players[index].getMeldCardsHashMap(trumpSuit));
        for(int i=0; i<parent.getChildren().size(); i++) {
            HBox subBox = (HBox) parent.getChildren().get(i);
            clearHBox(subBox);
            for(int j=0; j<5; j++) {
                if(5*i+j < meldCards.size()) {
                    Image image = new Image(Card.class.getResourceAsStream("/images/" + meldCards.get(5 * i + j) + ".png"));
                    subBox.getChildren().add(buildChild(image, 45, 70, -35*j, -50*i, 0));
                }
            }
        }
    }

    private ArrayList<String> getMeldCards(HashMap<String, Integer> meldCardsHashMap) {
        ArrayList<String> meldCards = new ArrayList<>();
        for(String card : meldCardsHashMap.keySet()) {
            for(int i=0; i<meldCardsHashMap.get(card); i++) {
                meldCards.add(card);
            }
        }
        return meldCards;
    }

    @FXML
    void playHand(ActionEvent event) {
        playHand();
    }
}

