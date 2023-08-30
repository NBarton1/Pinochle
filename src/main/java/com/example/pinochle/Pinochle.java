package com.example.pinochle;

import javafx.animation.*;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Group;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.transform.Rotate;
import javafx.util.Duration;

import java.net.URL;
import java.util.*;

public class Pinochle implements Initializable {

    @FXML
    private Button advanceButton;

    @FXML
    private GridPane bidActionsGrid;

    @FXML
    private Group bidPhaseGroup;

    @FXML
    private Group bidStackPanesGroup;

    @FXML
    private Group dealerChipsGroup;

    @FXML
    private Group handsGroup;

    @FXML
    private Group inGameGroup;

    @FXML
    private Group meldPhaseGroup;

    @FXML
    private Group playersMeldsGroup;

    @FXML
    private GridPane scoresGridPane;

    @FXML
    private GridPane suitsGrid;

    @FXML
    private ImageView trumpSuitIndicator;

    @FXML
    private Group welcomeGroup;


    /**
     * Constant player count (4)
     */
    private int PLAYERCOUNT;
    /**
     * Constant Image ("/images/back.png")
     */
    private Image BACK;
    /**
     * Array of players (Size 4)
     */
    private Player[] players;
    /**
     * Deck of cards object
     */
    private Deck deck;
    /**
     * Constant suits {"C", "H", "S", "D"}
     */
    private String[] suits;
    /**
     * Dealer of the hand
     */
    private int dealer;
    /**
     * Scores per team
     */
    private int[] scores;
    /**
     * Bid per team (either bid or 0 because only one team can win the bid)
     */
    private int[] bidPerTeam;
    /**
     * Meld per team
     */
    private int[] meldPerTeam;
    /**
     * Tricks pulled per team
     */
    private int[] tricksPerTeam;
    /**
     * Trump suit of the hand
     */
    private String trumpSuit;
    /**
     * Bid amount of the hand
     */
    private int bid;
    /**
     * An array of which players remain in the bid
     * -1 - passed
     *  0 - has not bid nor passed
     *  1 - bidding
     */
    private int[] isBidding;
    /**
     * An array of which players have bid at least once
     *  true - bid at least once
     * false - passed on first turn
     */
    private boolean[] bidAtLeastOnce;
    /**
     * What the user decides to do on their turn to bid
     */
    private int bidAction;
    /**
     * Condition for when to end bidding phase
     */
    private boolean endBiddingPhase;


    /**
     * Initializes the graphics
     * @param url URL object
     * @param resourceBundle ResourceBundle object
     */
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        getVariables();
    }

    /**
     * Gets the variables needed to start the game
     */
    private void getVariables() {
        dealer = 0;
        PLAYERCOUNT = 4;
        scores = new int[]{0, 0};
        BACK = new Image(Objects.requireNonNull(Card.class.getResourceAsStream("/images/back.png")));
        deck = new Deck();
        suits = new String[]{"C", "H", "S", "D"};
        getPlayers();

        rotateSideCards();
    }

    /**
     * Prepares the graphics for the game to start
     */
    @FXML
    void startGame() {
        activate(welcomeGroup, false);
        activate(inGameGroup, true);
        startHand();
    }

    /**
     * Sets advanceButton to start a new hand on click
     */
    void toStartHand() {
        activate(advanceButton, true);
        advanceButton.setText("New Deal");
        advanceButton.setOnAction(this::startHand);
    }

    /**
     * Starts a new hand
     */
    private void startHand() {
        bidPerTeam = new int[]{0, 0};
        meldPerTeam = new int[]{0, 0};
        tricksPerTeam = new int[]{0, 0};

        toDealPhase();
    }

    /**
     * Starts a new hand on clicking advanceButton
     * @param event Click
     */
    private void startHand(ActionEvent event) {
        startHand();
    }

    /**
     * Sets up the deal phase
     */
    private void toDealPhase() {
        resetGroups();
        trumpSuitIndicator.setImage(null);
        activate(bidPhaseGroup, false);
        activate(meldPhaseGroup, false);

        dealPhase();
    }

    /**
     * Dealing the cards and setting the graphics
     */
    private void dealPhase() {
        showDealerChip();

        dealCards();
        setCards();

        System.out.println(players[0]);

        toBidPhase();
    }

    /**
     * Sets up advanceButton to advance to the bid phase on click
     */
    private void toBidPhase() {
        activate(advanceButton, true);
        advanceButton.setOnAction(this::bidPhase);
        advanceButton.setText("To Bid");
    }

    /**
     * Starts the bid phase on clicking advanceButton
     * @param event Click
     */
    void bidPhase(ActionEvent event) {
        activate(bidPhaseGroup, true);
        activate(advanceButton, false);
        setBidActionsGrid();
        setBidLabels();
        bid = 52;
        bidAction = -1;
        isBidding = new int[]{0, 0, 0, 0};
        bidAtLeastOnce = new boolean[]{false, false, false, false};
        endBiddingPhase = false;

        bid();

        dealer = (dealer + 1) % 4;
    }

    /**
     * The bidding process
     */
    private void bid() {
        if (!endBiddingPhase) {
            int bidder = 1;
            if (isBidding[0] == 0)
                bidder += dealer;

            // Human player
            if (bidAction != -1) {
                if (bidAction == 0) {
                    choseToBid(0);
                    if (bid != 50) {
                        increaseBid();
                    }
                } else {
                    choseToPass(0);
                    if (biddersLeft() == 0) {
                        throwInHand(0);
                        toStartHand();
                    }
                }
                bidAction = -1;
            }

            int biddersLeft = 0;
            Timeline time = new Timeline();
            for (int i = bidder; i < PLAYERCOUNT; i++) {
                int finalI = i;
                if (isBidding[i] != -1) {
                    biddersLeft++;
                    time.getKeyFrames().add(new KeyFrame(Duration.seconds(biddersLeft), e -> computerBid(finalI)));
                }
            }
            time.getKeyFrames().add(new KeyFrame(Duration.seconds(1), (KeyValue) null));
            time.playFromStart();
            if (isBidding[0] != -1 || biddersLeft <= 1) time.setOnFinished(e -> editBidActionsGrid());
            else time.setOnFinished(e -> bid());
        }
    }

    /**
     * Sets up advanceButton to advance to meld phase on click
     */
    private void toMeldPhase() {
        activate(advanceButton, true);
        advanceButton.setOnAction(this::meldPhase);
        advanceButton.setText("To Meld");
    }

    /**
     * Starts the meld phase on clicking advanceButton
     * @param event Click
     */
    private void meldPhase(ActionEvent event) {
        activate(advanceButton, true);
        activate(bidPhaseGroup, false);
        activate(meldPhaseGroup, true);
        trumpSuitIndicator.setImage(new Image(Objects.requireNonNull(Card.class.getResourceAsStream("/images/" + trumpSuit + ".png"))));
        layMeld();
        if (updateMeldPerTeam()) {
            // toTricksPhase();   <-- goes here
            updateTricksPerTeam();
            updateScores();
        }
        toStartHand();
    }

    /**
     * Gets the array of players
     */
    private void getPlayers() {
        players = new Player[PLAYERCOUNT];
        for (int i = 0; i < PLAYERCOUNT; i++) {
            players[i] = new Player();
        }
    }

    /**
     * Rotates the cards on the left and right 90 degrees
     * [A rotated HBox is used rather than a VBox to keep the children in handsGroup a consistent type]
     */
    private void rotateSideCards() {
        for (int i = 0; i < PLAYERCOUNT / 2; i++) {
            handsGroup.getChildren().get(2 * i + 1).getTransforms().add(new Rotate(90, 50, 0));
        }
    }

    /**
     * Updates the graphics for the dealer chip
     */
    private void showDealerChip() {
        dealerChipsGroup.getChildren().get((dealer + 3) % 4).setVisible(false);
        dealerChipsGroup.getChildren().get(dealer).setVisible(true);
    }

    /**
     * Toggles on/off an object
     * @param parent Object to toggle
     * @param on Toggle value
     */
    private void activate(Button parent, boolean on) {
        parent.setDisable(!on);
        parent.setVisible(on);
    }

    /**
     * Toggles on/off an object
     * @param parent Object to toggle
     * @param on Toggle value
     */
    private void activate(Group parent, boolean on) {
        parent.setDisable(!on);
        parent.setVisible(on);
    }

    /**
     * Toggles on/off an object
     * @param parent Object to toggle
     * @param on Toggle value
     */
    private void activate(GridPane parent, boolean on) {
        parent.setDisable(!on);
        parent.setVisible(on);
    }

    /**
     * Resets handsGroup and playerMeldsGroup
     */
    private void resetGroups() {
        resetHandsGroup();
        resetPlayerMeldsGroup();
    }

    /**
     * Resets handsGroup
     */
    private void resetHandsGroup() {
        for (int i = 0; i < handsGroup.getChildren().size(); i++) {
            ((HBox) handsGroup.getChildren().get(i)).getChildren().clear();
        }
    }

    /**
     * Resets playerMeldsGroup
     */
    private void resetPlayerMeldsGroup() {
        for (int i = 0; i < playersMeldsGroup.getChildren().size(); i++) {
            VBox parent = (VBox) playersMeldsGroup.getChildren().get(i);
            for (int j = 0; j < Objects.requireNonNull(parent).getChildren().size(); j++) {
                HBox subbox = (HBox) parent.getChildren().get(j);
                for (int k = 0; k < subbox.getChildren().size(); k++) {
                    ((ImageView) subbox.getChildren().get(k)).setImage(null);
                }
            }
        }
    }

    /**
     * Deals the cards to the players
     */
    private void dealCards() {
        Card[][] deal = deck.deal();
        for (int i = 0; i < PLAYERCOUNT; i++) {
            players[i].setHand(deal[i]);
        }
    }

    /**
     * Sets an index to each card in hand to retrieve when clicking
     * @param i Index to pass as value
     */
    private void setImageViewUserData(int i) {
        ImageView imageView = (ImageView) ((HBox) handsGroup.getChildren().get(0)).getChildren().get(i);
        imageView.setUserData(i);
        imageView.setOnMouseClicked(event -> playCard((int) imageView.getUserData()));
    }

    /**
     * Centers the rest of the cards after playing one
     */
    private void toCenter() {
        int nullCounter = 0;
        ArrayList<Image> notNull = new ArrayList<>();
        HBox playerCardImagesHBox = ((HBox) handsGroup.getChildren().get(0));
        int size = playerCardImagesHBox.getChildren().size();
        for (int i = 0; i < size; i++) {
            Image image = ((ImageView) playerCardImagesHBox.getChildren().get(i)).getImage();
            if (image != null)
                notNull.add(image);
            else nullCounter++;
        }
        int index = 0;
        for (int i = 0; i < size; i++) {
            ImageView imageView = (ImageView) playerCardImagesHBox.getChildren().get(i);
            if (i < nullCounter / 2 || i >= 20 - Math.round((double) nullCounter / 2))
                imageView.setImage(null);
            else {
                imageView.setImage(notNull.get(index));
                setImageViewUserData(index);
                index++;
            }
        }
    }

    /**
     * Sets the graphics for all cards
     */
    private void setCards() {
        for (int i = 0; i < 20; i++) {
            ((HBox) handsGroup.getChildren().get(0)).getChildren().add(buildChild(players[0].getHand()[i].getImage(), 57, 79, -40 * (i + 1.5), 0));
            setImageViewUserData(i);

            for (int j = 1; j < handsGroup.getChildren().size(); j++) {
                ((HBox) handsGroup.getChildren().get(j)).getChildren().add(buildChild(BACK, 30, 49, -23 * (i - 2), 0));
            }
        }
    }

    /**
     * Builds an ImageView child for each player's hand
     * @param image Image to pass (card for player, back for computer)
     * @param x Relative x position to (0, 0) in HBox
     * @param y Relative y position to (0, 0) in HBox
     * @param dx Translation in x direction
     * @param dy Translation in y direction
     * @return Resulting ImageView
     */
    private ImageView buildChild(Image image, double x, double y, double dx, double dy) {
        ImageView imageView = new ImageView(image);
        imageView.setFitWidth(x);
        imageView.setFitHeight(y);
        imageView.setTranslateX(dx);
        imageView.setTranslateY(dy);

        return imageView;
    }

    /**
     * Sets the bid labels ("Player", "Opponent 1", "Teammate", "Opponent 2"}
     */
    private void setBidLabels() {
        String[] names = new String[]{"Player", "Opponent 1", "Teammate", "Opponent 2"};
        for (int i = 0; i < bidStackPanesGroup.getChildren().size(); i++) {
            ((Label) ((StackPane) bidStackPanesGroup.getChildren().get(i)).getChildren().get(1)).setText(names[i]);
        }
    }

    /**
     * Sets the bid actions grid (bid or pass)
     */
    private void setBidActionsGrid() {
        for (int i = 0; i < bidActionsGrid.getChildren().size(); i++) {
            Button button = (Button) bidActionsGrid.getChildren().get(i);
            button.setUserData(i);
            button.setOnAction(event -> bidAction((int) button.getUserData()));
        }
        ((Button) bidActionsGrid.getChildren().get(1)).setText("Pass");
    }

    /**
     * What the computer does when it is its turn to bid
     * @param bidder Which computer is bidding
     */
    private void computerBid(int bidder) {

        if (isBidding[bidder] == 0 && biddersLeft() == 1) bid = 50; // Bid dropped on computer

        int max = max(players[bidder].getExpectedPoints(bidAtLeastOnce[(bidder + 2) % 4]));
        boolean hasBid = bid <= max;

        if(biddersLeft()==1 && isBidding[bidder]==1) { // Computer won bid
            computerWonBid(bidder);
        } else if (hasBid) { // Computer bidding
            choseToBid(bidder);
            increaseBid();
        } else { // Passes
            choseToPass(bidder);
            if (biddersLeft() == 0) { // Computer throws in hand
                throwInHand(bidder);
                toStartHand();
            }
        }
    }

    /**
     * Amount of bidders that remain in the bid (have not passed)
     * @return Count
     */
    private int biddersLeft() {
        int bidders = 0;
        for (int bit : isBidding) {
            if (bit != -1) bidders++;
        }
        return bidders;
    }

    /**
     * What the user does on their turn to bid
     * @param i Index of button clicked (0 for bid, 1 for pass)
     */
    private void bidAction(int i) {
        activate(bidActionsGrid, false);
        bidAction = i;
        isBidding[0] = (int) ((i - .5) * -2);
        bid();
    }

    /**
     * Increases the bid after a player bids
     */
    private void increaseBid() {
        if (bid < 60) bid += 2;
        else bid += 5;
    }

    /**
     * What happens after a player bids
     * @param bidder Player that bid
     */
    private void choseToBid(int bidder) {
        Label label = (Label) ((StackPane) bidStackPanesGroup.getChildren().get(bidder)).getChildren().get(1);
        assert label != null;

        isBidding[bidder] = 1;
        bidAtLeastOnce[bidder] = true;
        label.setText("Bid: " + bid);
    }

    /**
     * What happens after a player passes the bid
     * @param bidder Player that passed
     */
    private void choseToPass(int bidder) {
        Label label = (Label) ((StackPane) bidStackPanesGroup.getChildren().get(bidder)).getChildren().get(1);
        assert label != null;

        isBidding[bidder] = -1;
        label.setText("Pass!");
    }

    /**
     * What happens after a player throws in a hand (none of the 4 players bid)
     * @param bidder Player that dealt
     */
    private void throwInHand(int bidder) {
        int team = bidder % 2;
        scores[team] -= bid;
        Label scoreLabel = (Label) scoresGridPane.getChildren().get(team);
        scoreLabel.setText(String.valueOf(scores[team]));
    }

    /**
     * Edits the bid actions grid to show the options that the user has on their turn to bid
     */
    private void editBidActionsGrid() {
        if (isBidding[0] != -1 && biddersLeft() >= 1) { // Player is in bid
            activate(bidActionsGrid, true);
            if(isBidding[0]==0 && biddersLeft()==1) { // Bid is dropped on player
                bid = 50;
                ((Button) bidActionsGrid.getChildren().get(1)).setText("Throw");
            } else if(isBidding[0] == 1 && biddersLeft() == 1) { // Player has won bid
                activate(bidActionsGrid, false);
                bidPerTeam[0] += bid;
                setSuitsGrid();
            }
            ((Button) bidActionsGrid.getChildren().get(0)).setText("Bid " + bid);
        }
    }

    /**
     * What happens when a computer wins the bid
     * @param bidder Computer that won the bid
     */
    private void computerWonBid(int bidder) {
        bidPerTeam[bidder % 2] = bid;
        endBiddingPhase = true;

        int[] expectedPoints = players[bidder].getExpectedPoints(bidAtLeastOnce[(bidder + 2) % 4]);
        int max = max(expectedPoints);
        for (int i = 0; i < PLAYERCOUNT; i++) {
            if (expectedPoints[i] == max) {
                pickSuit(i);
                break;
            }
        }
    }

    /**
     * Gets the max number of an array
     * @param nums Array of numbers
     * @return Max number in the array
     */
    private int max(int[] nums) {
        int max = nums[0];
        for (int i : nums) {
            max = Math.max(max, i);
        }
        return max;
    }

    /**
     * Sets the grid that allows the user to pick trump suit (if the user won the bid)
     */
    private void setSuitsGrid() {
        for (int i = 0; i < suitsGrid.getChildren().size(); i++) {
            ImageView imageView = (ImageView) suitsGrid.getChildren().get(i);
            imageView.setUserData(i);
            imageView.setOnMouseClicked(event -> pickSuit((int) imageView.getUserData()));
        }
        activate(suitsGrid, true);
    }

    /**
     * What happens when it's time to pick trump suit
     * @param i index of suit to select
     */
    private void pickSuit(int i) {
        trumpSuit = suits[i];
        activate(suitsGrid, false);
        toMeldPhase();
    }

    /**
     * Laying the meld of all players on the table
     */
    private void layMeld() {
        for (int i = 0; i < playersMeldsGroup.getChildren().size(); i++) {
            VBox parent = (VBox) playersMeldsGroup.getChildren().get(i);
            ArrayList<String> meldCards = getMeldCards(players[i].getMeldCardsHashMap(trumpSuit));
            for (int j = 0; j < Objects.requireNonNull(parent).getChildren().size(); j++) {
                HBox subBox = (HBox) parent.getChildren().get(j);
                subBox.getChildren().clear();
                for (int k = 0; k < 5; k++) {
                    if (5 * j + k < meldCards.size()) {
                        Image image = new Image(Objects.requireNonNull(Card.class.getResourceAsStream("/images/" + meldCards.get(5 * j + k) + ".png")));
                        subBox.getChildren().add(buildChild(image, 45, 70, -35 * k, -50 * j));
                    }
                }
            }
        }
    }

    /**
     * Gets the cards for each player to lay
     * @param meldCardsHashMap Return from getMeldCardsHashMap(trumpSuit) in Player class
     * @return ArrayList of cards to lay
     */
    private ArrayList<String> getMeldCards(HashMap<String, Integer> meldCardsHashMap) {
        ArrayList<String> meldCards = new ArrayList<>();
        for (String card : meldCardsHashMap.keySet()) {
            for (int i = 0; i < meldCardsHashMap.get(card); i++) {
                meldCards.add(card);
            }
        }
        return meldCards;
    }

    /**
     * Adds the meld for both teams
     * @return true - bidding team's meld is eligible to play trick phase
     *        false - bidding team's meld is not eligible to play trick phase
     */
    private boolean updateMeldPerTeam() {
        int trumpSuitIndex = -1;
        for (int i = 0; i < suits.length; i++) {
            if (trumpSuit.equals(suits[i])) trumpSuitIndex = i;
        }
        for (int i = 0; i < PLAYERCOUNT; i++) {
            meldPerTeam[i % 2] += players[i].calcMeld()[trumpSuitIndex];
        }
        for (int i = 0; i < meldPerTeam.length; i++) {
            if (meldPerTeam[i] < 15) {
                meldPerTeam[i] = 0;
                if (bidPerTeam[i] == bid) {
                    scores[i] -= bid;
                    scores[(i + 1) % 2] += meldPerTeam[(i + 1) % 2];
                    for (int j = 0; j < PLAYERCOUNT / 2; j++) {
                        ((Label) scoresGridPane.getChildren().get(j)).setText(String.valueOf(scores[j]));
                    }
                    toStartHand();
                    return false;
                }
            }
        }
        return true;
    }

    /**
     * Adds the tricks pulled for both teams
     * TODO: Add trick phase and change the way this method works
     */
    private void updateTricksPerTeam() {
        int trumpSuitIndex = -1;
        for (int i = 0; i < suits.length; i++) {
            if (trumpSuit.equals(suits[i])) trumpSuitIndex = i;
        }
        for (int i = 0; i < PLAYERCOUNT; i++) {
            tricksPerTeam[i % 2] += players[i].getExpectedPoints(bidAtLeastOnce[(i + 2) % 4])[trumpSuitIndex] - players[i].calcMeld()[trumpSuitIndex] - 8;
        }
        for (int i = 0; i < tricksPerTeam.length; i++) {
            if (tricksPerTeam[i] < 15) {
                meldPerTeam[i] = 0;
                tricksPerTeam[i] = 0;
            }
        }
    }

    /**
     * Adds the meld + tricks for both teams and compares to bid to update scores
     */
    private void updateScores() {
        for (int i = 0; i < PLAYERCOUNT / 2; i++) {
            if (meldPerTeam[i] + tricksPerTeam[i] > bidPerTeam[i]) scores[i] += meldPerTeam[i] + tricksPerTeam[i];
            else scores[i] -= bidPerTeam[i];
            ((Label) scoresGridPane.getChildren().get(i)).setText(String.valueOf(scores[i]));
        }
    }

    /**
     * Plays a card on click
     * @param i Index of card to play
     */
    private void playCard(int i) {
        //players[0].playCard(i);
        ((ImageView) ((HBox) handsGroup.getChildren().get(0)).getChildren().get(i)).setImage(null);

        toCenter();
    }
}