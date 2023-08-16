package com.example.pinochle;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;

import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;

public class GUIController implements Initializable {

    @FXML
    private Label otherTeamScore;

    @FXML
    private HBox playerCardsHBOX;

    @FXML
    private Label playerTeamScore;

    private ImageView testCard;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        ArrayList<ImageView> playerCards = getPlayerCards();

        int PLAYERCOUNT = 4;
        Deck deck = new Deck();
        Card[][] deal = deck.deal();
        Player[] players = new Player[PLAYERCOUNT];
        for (int i = 0; i < PLAYERCOUNT; i++) {
            players[i] = new Player();
        }
        for (int i = 0; i < PLAYERCOUNT; i++) {
            players[i].setHand(deal[i]);
            System.out.println(players[i]);
        }

        for(int i=0; i<playerCardsHBOX.getChildren().size(); i++) {
            ImageView imageView = (ImageView) playerCardsHBOX.getChildren().get(i);
            imageView.setImage(players[0].getHand()[i].getImage());
            
        }
    }
    private ArrayList<ImageView> getPlayerCards() {
        ArrayList<ImageView> playerCards = new ArrayList<>();
        for(int i = 0; i< playerCardsHBOX.getChildren().size(); i++) {
            playerCards.add((ImageView) playerCardsHBOX.getChildren().get(i));
        }
        return playerCards;
    }
}
