package com.example.pinochle;

import java.util.ArrayList;
import java.util.Random;

public class Deck {
    private final Card[] deck;
    private final int LENGTH;

    public Deck() {
        deck = getDeck();
        LENGTH = deck.length;
    }

    private Card[] getDeck() {
        String[] suits = new String[]{"C", "H", "S", "D"};
        String[] values = new String[]{"J", "Q", "K", "T", "A"};
        Card[] deckToReturn = new Card[suits.length * values.length * 4]; // length 80

        for (int i = 0; i < suits.length; i++) {
            for (int j = 0; j < values.length; j++) {
                for (int k = 0; k < 4; k++) {
                    deckToReturn[20 * i + 4 * j + k] = new Card(values[j], suits[i]);
                }
            }
        }
        return deckToReturn;
    }

    private Card[] shuffle() {
        Card[] deckShuffled = new Card[LENGTH];
        Random rand = new Random();
        ArrayList<Integer> indexList = new ArrayList<>();
        for (int i = 0; i < LENGTH; i++) {
            indexList.add(i);
        }

        for (int i = 0; i < LENGTH; i++) {
            int index = indexList.get(rand.nextInt(indexList.size()));
            deckShuffled[i] = deck[index];
            indexList.remove((Integer) index);
        }
        return deckShuffled;
    }

    public Card[][] deal() {
        Card[] deckShuffled = shuffle();
        int PLAYERS = 4;
        Card[][] deal = new Card[PLAYERS][deckShuffled.length / PLAYERS];
        for (int i = 0; i < deckShuffled.length; i++) {
            deal[i % PLAYERS][i / PLAYERS] = deckShuffled[i];
        }
        return deal;
    }
}
