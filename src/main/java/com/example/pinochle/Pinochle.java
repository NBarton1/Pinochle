package com.example.pinochle;

public class Pinochle {
    public static void main(String[] args) {
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
    }
}