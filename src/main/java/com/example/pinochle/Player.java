package com.example.pinochle;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;

/**
 * Player class for each player in the game
 */
public class Player {
    /**
     * Hand of the player
     */
    private Card[] hand;
    /**
     * All suits in order C, H, S, D
     */
    private final String[] suits;
    /**
     * All values in order J, Q, K, T, A
     */
    private final String[] values;

    /**
     * Default constructor
     */
    public Player() {
        hand = new Card[20];
        suits = new String[]{"C", "H", "S", "D"};
        values = new String[]{"J", "Q", "K", "T", "A"};
    }

    /**
     * Sets the hand
     * @param hand hand to set as
     */
    public void setHand(Card[] hand) {
        this.hand = hand;

        //String[] handAsString = new String[] {"QC",  "QC",  "KC",  "AC",  "JH",  "KH",  "TH",  "QS",  "KS",  "AS",  "JD",  "JD",  "QD",  "QD",  "KD",  "KD",  "TD",  "TD",  "AD",  "AD"};
        //for(int i=0; i<20; i++) {
        //    this.hand[i] = new Card(String.valueOf(handAsString[i].charAt(0)), String.valueOf(handAsString[i].charAt(1)));
        //}

        sortHand();
    }

    /**
     * Gets the hand
     * @return hand
     */
    public Card[] getHand() {
        return hand.clone();
    }

    /**
     * Sorts the hand
     */
    private void sortHand() {
        String[] order = getOrder();
        Card[] handCopy = hand.clone();
        int i = 0;
        for (String nextCard : order) {
            for (Card card : handCopy) {
                if (card.toString().equals(nextCard)) {
                    hand[i] = card;
                    i++;
                }
            }
        }
    }

    /**
     * Gets the order of cards for sorting
     * @return order
     */
    private String[] getOrder() {
        String[] order = new String[suits.length * values.length];
        for (int i = 0; i < suits.length; i++) {
            for (int j = 0; j < values.length; j++) {
                order[5 * i + j] = values[j] + suits[i];
            }
        }
        return order;
    }

    /**
     * Gets cards necessary for pinochles
     * @return cards
     */
    private ArrayList<String> getMeldPinochle() {
        return new ArrayList<>(Arrays.asList("JD", "QS"));
    }

    /**
     * Gets cards necessary for arounds
     * @return cards
     */
    private ArrayList<ArrayList<String>> getMeldAround() {
        String[] importantValues = new String[]{"J", "Q", "K", "A"};
        ArrayList<ArrayList<String>> meldAround = new ArrayList<>();
        for (String value : importantValues) {
            ArrayList<String> add = new ArrayList<>();
            for (String suit : suits) {
                add.add(value + suit);
            }
            meldAround.add(add);
        }
        return meldAround;
    }

    /**
     * Gets cards necessary for runs
     * @return cards
     */
    private ArrayList<ArrayList<String>> getMeldRun() {
        ArrayList<ArrayList<String>> meldRun = new ArrayList<>();
        for (String suit : suits) {
            ArrayList<String> add = new ArrayList<>();
            for (String value : values) {
                add.add(value + suit);
            }
            meldRun.add(add);
        }
        return meldRun;
    }

    /**
     * Gets cards necessary for marriages
     * @return cards
     */
    private ArrayList<ArrayList<String>> getMeldMarriage() {
        ArrayList<ArrayList<String>> meldMarriage = new ArrayList<>();
        String[] importantValues = new String[]{"Q", "K"};
        for (String suit : suits) {
            ArrayList<String> add = new ArrayList<>();
            for (String value : importantValues) {
                add.add(value + suit);
            }
            meldMarriage.add(add);
        }
        return meldMarriage;
    }

    /**
     * Puts all types of meld in one ArrayList
     * @return list of types of meld
     */
    private ArrayList<ArrayList<String>> getMeldCombos() {
        ArrayList<ArrayList<String>> meldCombos = new ArrayList<>();

        meldCombos.add(getMeldPinochle());
        meldCombos.addAll(getMeldAround());
        meldCombos.addAll(getMeldRun());
        meldCombos.addAll(getMeldMarriage());

        return meldCombos;
    }

    /**
     * Gets the meld for pinochles
     * @return meld
     */
    private int[] getValuePinochle() {
        return new int[]{0, 4, 30, 60, 90};
    }

    /**
     * Gets the meld for arounds
     * @return meld
     */
    private ArrayList<int[]> getValueAround() {
        ArrayList<int[]> valueAround = new ArrayList<>();
        for (int i = 4; i <= 10; i += 2) {
            valueAround.add(new int[]{0, i, 10 * i, 15 * i, 20 * i});
        }
        return valueAround;
    }

    /**
     * Gets the meld for runs
     * @return meld
     */
    private ArrayList<int[]> getValueRun() {
        ArrayList<int[]> valueRun = new ArrayList<>();
        for (int i = 0; i < 4; i++) {
            valueRun.add(new int[]{0, 15, 150, 225, 300});
        }
        return valueRun;
    }

    /**
     * Gets the meld for marriages
     * @return meld
     */
    private ArrayList<int[]> getValueMarriage() {
        ArrayList<int[]> valueMarriage = new ArrayList<>();
        for (int i = 0; i < 4; i++) {
            valueMarriage.add(new int[]{0, 2, 4, 6, 8});
        }
        return valueMarriage;
    }

    /**
     * Puts all meld values in one ArrayList
     * @return meld values
     */
    private ArrayList<int[]> getMeldValues() {
        ArrayList<int[]> meldValues = new ArrayList<>();

        meldValues.add(getValuePinochle());
        meldValues.addAll(getValueAround());
        meldValues.addAll(getValueRun());
        meldValues.addAll(getValueMarriage());

        return meldValues;
    }

    /**
     * Gets the amount of times that each meld appears in a hand
     * @param combo type of meld
     * @return how many times it occurs
     */
    private int getComboCount(ArrayList<String> combo) {
        int comboCount = 0;
        int prevComboCount;
        ArrayList<String> handCopy = new ArrayList<>();
        for(Card card : hand) {
            handCopy.add(card.toString());
        }
        do {
            prevComboCount = comboCount;
            int inHand = 0;
            for (String card : combo) {
                if (handCopy.contains(card)) {
                    handCopy.remove(card);
                    inHand++;
                }
            }
            if (inHand == combo.size())
                comboCount++;
        } while (comboCount != prevComboCount);
        return comboCount;
    }

    /**
     * Calculates the hand's meld per each suit
     * @return meld per suit
     */
    public int[] calcMeld() {
        int[] meldPerSuit = new int[suits.length];
        int meldGeneral = 0;
        int[] runsPerSuit = new int[suits.length];

        ArrayList<ArrayList<String>> meldCombos = getMeldCombos();
        ArrayList<int[]> meldValues = getMeldValues();

        for (int i = 0; i < meldCombos.size(); i++) {
            int comboCount = getComboCount(meldCombos.get(i));
            int[] value = meldValues.get(i);

            if (i >= 9) {
                for (int j = 0; j < suits.length; j++) {
                    if (j == i - 9)
                        meldPerSuit[j] += 2 * value[comboCount - runsPerSuit[j]];
                    else meldPerSuit[j] += value[comboCount];
                }
            } else if (i >= 5) {
                meldPerSuit[i - 5] += value[comboCount];
                runsPerSuit[i - 5] = comboCount;
            } else meldGeneral += value[comboCount];
        }
        for (int i = 0; i < suits.length; i++) {
            meldPerSuit[i] += meldGeneral;
        }
        return meldPerSuit;
    }

    /**
     * Gets the cards that would be part of meld
     * @param suit trump suit
     * @return LinkedHashMap containing each card and how many are needed
     */
    public LinkedHashMap<String, Integer> getMeldCardsHashMap(String suit) {
        LinkedHashMap<String, Integer> meldCards = new LinkedHashMap<>();
        ArrayList<ArrayList<String>> meldCombos = getMeldCombos();
        int di;
        switch(suit) {
            case "H" -> di = 1;
            case "S" -> di = 2;
            case "D" -> di = 3;
            default -> di = 0;
        }

        // Runs
        int comboCount = getComboCount(meldCombos.get(5+di));
        for(int i=0; i<comboCount; i++) {
            for(String card : meldCombos.get(5+di)) {
                try {
                    meldCards.put(card, Math.max(meldCards.get(card), comboCount));
                } catch(NullPointerException err) {
                    meldCards.put(card, comboCount);
                }
            }
        }

        // Marriages
        for(int i=0; i<4; i++) {
            comboCount = getComboCount(meldCombos.get(9 + i));
            for (int j = 0; j < comboCount; j++) {
                for(String card : meldCombos.get(9+i)) {
                    try {
                        meldCards.put(card, Math.max(meldCards.get(card), comboCount));
                    } catch(NullPointerException err) {
                        meldCards.put(card, comboCount);
                    }
                }
            }
        }

        // Arounds
        for(int i=0; i<4; i++) {
            comboCount = getComboCount(meldCombos.get(1 + i));
            for (int j = 0; j < comboCount; j++) {
                for(String card : meldCombos.get(1+i)) {
                    try {
                        meldCards.put(card, Math.max(meldCards.get(card), comboCount));
                    } catch(NullPointerException err) {
                        meldCards.put(card, comboCount);
                    }
                }
            }
        }

        // Pinochle
        comboCount = getComboCount(meldCombos.get(0));
        for (int j = 0; j < comboCount; j++) {
            for(String card : meldCombos.get(0)) {
                try {
                    meldCards.put(card, Math.max(meldCards.get(card), comboCount));
                } catch(NullPointerException err) {
                    meldCards.put(card, comboCount);
                }
            }
        }
        return meldCards;
    }

    /**
     * Plays a card by removing it from the hand
     * @param i index of card to remove
     */
    public void playCard(int i) {
        hand[i] = null;
        toCenter();
    }

    /**
     * Centers the remaining cards after playing one
     */
    private void toCenter() {
        int nullCounter = 0;
        ArrayList<Card> notNull = new ArrayList<>();
        int size = hand.length;
        for (Card card : hand) {
            if (card != null)
                notNull.add(card);
            else nullCounter++;
        }
        int index = 0;
        for(int i=0; i<size; i++) {
            if(i<nullCounter/2 || i>=20-Math.round((double) nullCounter/2))
                hand[i] = null;
            else {
                hand[i] = notNull.get(index);
                index++;
            }
        }
    }

    /**
     * Provides string representation of Player class
     * @return string
     */
    @Override
    public String toString() {
        StringBuilder ret = new StringBuilder();
        for (Card card : hand) {
            ret.append(card.toString()).append("  ");
        }
        ret.append("\n");
        for (int meld : calcMeld()) {
            ret.append(meld).append("  ");
        }
        return ret + "\n";
    }
}
