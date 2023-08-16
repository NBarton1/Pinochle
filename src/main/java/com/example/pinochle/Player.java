package com.example.pinochle;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;

public class Player {
    private Card[] hand;
    private final String[] suits;
    private final String[] values;

    public Player() {
        hand = new Card[20];
        suits = new String[]{"C", "H", "S", "D"};
        values = new String[]{"J", "Q", "K", "T", "A"};
    }

    public void setHand(Card[] hand) {
        this.hand = hand;
        sortHand();
    }

    public Card[] getHand() {
        return hand.clone();
    }

    private void sortHand() {
        String[] order = getOrder();
        Card[] handCopy = hand.clone();
        int i = 0;
        for (String nextCard : order) {
            for (Card card : handCopy) {
                if (card.getCard().equals(nextCard)) {
                    hand[i] = card;
                    i++;
                }
            }
        }
    }

    private String[] getOrder() {
        String[] order = new String[suits.length * values.length];
        for (int i = 0; i < suits.length; i++) {
            for (int j = 0; j < values.length; j++) {
                order[5 * i + j] = values[j] + suits[i];
            }
        }
        return order;
    }

    private ArrayList<String> getMeldPinochle() {
        return new ArrayList<>(Arrays.asList("JD", "QS"));
    }

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

    private ArrayList<ArrayList<String>> getMeldCombos() {
        ArrayList<ArrayList<String>> meldCombos = new ArrayList<>();

        meldCombos.add(getMeldPinochle());
        meldCombos.addAll(getMeldAround());
        meldCombos.addAll(getMeldRun());
        meldCombos.addAll(getMeldMarriage());

        return meldCombos;
    }

    private int[] getValuePinochle() {
        return new int[]{0, 4, 30, 60, 90};
    }

    private ArrayList<int[]> getValueAround() {
        ArrayList<int[]> valueAround = new ArrayList<>();
        for (int i = 4; i <= 10; i += 2) {
            valueAround.add(new int[]{0, i, 10 * i, 15 * i, 20 * i});
        }
        return valueAround;
    }

    private ArrayList<int[]> getValueRun() {
        ArrayList<int[]> valueRun = new ArrayList<>();
        for (int i = 0; i < 4; i++) {
            valueRun.add(new int[]{0, 15, 150, 225, 300});
        }
        return valueRun;
    }

    private ArrayList<int[]> getValueMarriage() {
        ArrayList<int[]> valueMarriage = new ArrayList<>();
        for (int i = 0; i < 4; i++) {
            valueMarriage.add(new int[]{0, 2, 4, 6, 8});
        }
        return valueMarriage;
    }

    private ArrayList<int[]> getMeldValues() {
        ArrayList<int[]> meldValues = new ArrayList<>();

        meldValues.add(getValuePinochle());
        meldValues.addAll(getValueAround());
        meldValues.addAll(getValueRun());
        meldValues.addAll(getValueMarriage());

        return meldValues;
    }

    private int getComboCount(ArrayList<String> combo) {
        int comboCount = 0;
        int prevComboCount;
        ArrayList<String> handCopy = new ArrayList<>();
        for(Card card : hand) {
            handCopy.add(card.getCard());
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

    public LinkedHashMap<String, Integer> getMeldCardsHashMap(String suit) {
        LinkedHashMap<String, Integer> meldCards = new LinkedHashMap<>();
        ArrayList<ArrayList<String>> meldCombos = getMeldCombos();
        int di=0;
        switch(suit) {
            case "H" -> di = 1;
            case "S" -> di = 2;
            case "D" -> di = 3;
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

    @Override
    public String toString() {
        StringBuilder ret = new StringBuilder();
        for (Card card : hand) {
            ret.append(card.getCard()).append("  ");
        }
        ret.append("\n");
        for (int meld : calcMeld()) {
            ret.append(meld).append("  ");
        }
        return ret + "\n";
    }

    public int j(int[] i) {
        int a = i[0];
        int b = i[1];
        int c = i[2];
        int d = i[3];

        return Math.max(Math.max(a, b), Math.max(c, d));
    }
}
