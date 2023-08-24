package com.example.pinochle;

import javafx.scene.image.Image;

import java.util.Objects;

/**
 * Card class for each playing card
 */
public class Card {
    /**
     * Value and suit of card
     */
    private final String card;
    /**
     * Image of card
     */
    private final Image image;

    /**
     * Default constructor
     * @param value value of card
     * @param suit suit of card
     */
    public Card(String value, String suit) {
        this.card = value+suit;
        image = new Image(Objects.requireNonNull(Card.class.getResourceAsStream("/images/" + card + ".png")));
    }

    /**
     * Returns the image of the card
     * @return image
     */
    public Image getImage() {return image;}

    /**
     * Returns value of card
     * @return value
     */
    public String getValue() {return String.valueOf(card.charAt(0));}

    /**
     * Returns suit of card
     * @return suit
     */
    public String getSuit() {return String.valueOf(card.charAt(1));}

    /**
     * Provides string representation for Card class
     * @return card
     */
    @Override
    public String toString() {
        return card;
    }
}
