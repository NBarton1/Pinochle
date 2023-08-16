package com.example.pinochle;

import javafx.scene.image.Image;

public class Card {
    private String card;
    private Image image;

    public Card(String value, String suit) {
        this.card = value+suit;
        image = new Image(Card.class.getResourceAsStream("/images/"+card+".png"));
    }

    public String getCard() {
        return card;
    }

    public Image getImage() {
        return image;
    }
}
