package com.example.pinochle;

import javafx.animation.*;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.StrokeLineCap;
import javafx.scene.shape.StrokeLineJoin;
import javafx.scene.shape.StrokeType;
import javafx.scene.text.Font;
import javafx.scene.transform.Rotate;
import javafx.util.Duration;

import java.net.URL;
import java.util.*;
import java.util.stream.IntStream;

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
    private Pane blockPane;

    @FXML
    private Group dealerChipsGroup;

    @FXML
    private Group endGameGroup;

    @FXML
    private Label gameResultLabel;

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
    private Group scoresGroup;

    @FXML
    private VBox scoresVBox;

    @FXML
    private GridPane suitsGrid;

    @FXML
    private Pane trickPane;

    @FXML
    private Group tricksPhaseGroup;

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

    private String[] values;

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
    private int leadTrickPlayer;
    private String playerCardPlayed;
    private int trickTurn;
    private ArrayList<String> currentTrick;
    private boolean isFirstTrick;
    private HashMap<String, Integer> cardsLeft;
    private boolean[][] isTrumping;
    private boolean[] hasTrump;
    private String[][] maxCards;
    private ArrayList<String>[] knownCards;


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
        PLAYERCOUNT = 4;
        BACK = new Image(Objects.requireNonNull(Card.class.getResourceAsStream("/images/back.png")));
        deck = new Deck();
        suits = new String[]{"C", "H", "S", "D"};
        values = new String[] {"J", "Q", "K", "T", "A"};
        getPlayers();

        rotateSideCards();
    }

    /**
     * Prepares the graphics for the game to start
     */
    @FXML
    void startGame() {
        dealer = 0;
        activate(welcomeGroup, false);
        activate(endGameGroup, false);
        scores = new int[]{0, 0};
        setScores();
        for(int i=2; i<scoresVBox.getChildren().size(); i++) {
            scoresVBox.getChildren().remove(i);
        }
        startHand();
    }

    /**
     * Sets advanceButton to start a new hand on click
     */

    private void toStartHand() {
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
        activate(inGameGroup, true);
        activate(scoresGridPane, true);
        activate(trumpSuitIndicator, true);
        activate(dealerChipsGroup, true);
        activate(scoresGridPane, true);
        activate(handsGroup, true);

        dealPhase();
    }

    /**
     * Starts a new hand on clicking advanceButton
     * @param event Click
     */
    @FXML
    private void startHand(ActionEvent event) {
        startHand();
    }

    /**
     * Dealing the cards and setting the graphics
     */
    private void dealPhase() {
        resetGroups();
        trumpSuitIndicator.setImage(null);
        activate(bidPhaseGroup, false);
        activate(meldPhaseGroup, false);
        activate(tricksPhaseGroup, false);
        activate(scoresGroup, false);

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
                if (bidAction == 0) choseToBid(0);
                else choseToPass(0);
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
            if (isBidding[0] != -1) time.setOnFinished(e -> editBidActionsGrid());
            else if (biddersLeft > 0) time.setOnFinished(e -> bid());
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
        knownCards = new ArrayList[PLAYERCOUNT];
        for(int i=0; i<knownCards.length; i++) {
            knownCards[i] = new ArrayList<>();
        }
        layMeld();
        if (updateMeldPerTeam()) {
            toTricksPhase();
        } else toScoreBoard();
    }

    private void toTricksPhase() {
        activate(advanceButton, true);
        advanceButton.setOnAction(this::tricksPhase);
        advanceButton.setText("To Tricks");
    }

    private void tricksPhase(ActionEvent event) {
        activate(advanceButton, false);
        activate(meldPhaseGroup, false);
        activate(tricksPhaseGroup, true);

        playerCardPlayed = null;
        isFirstTrick = true;
        currentTrick = new ArrayList<>();

        isTrumping = new boolean[PLAYERCOUNT][suits.length];
        hasTrump = new boolean[PLAYERCOUNT];
        for(int i=0; i<PLAYERCOUNT; i++) {
            hasTrump[i] = true;
        }
        cardsLeft = new HashMap<>();
        for(String suit : suits) {
            for(String value : values) {
                cardsLeft.put(value+suit, 4);
            }
        }
        maxCards = new String[PLAYERCOUNT][suits.length];
        for(int i=0; i<maxCards.length; i++) {
            for(int j=0; j<suits.length; j++) {
                maxCards[i][j] = "A"+suits[j];
            }
        }

        leadTrickPlayer = 0;
        for(int i=0; i<PLAYERCOUNT; i++) {
            if(isBidding[i]==1) leadTrickPlayer = i;
        }
        trickTurn = leadTrickPlayer;

        tricksPhaseGroup.getChildren().remove(0);

        trick();
    }

    private void trick() {
        Timeline time = new Timeline();
        int iterator = 1;

        if(playerCardPlayed == null) {
            while (trickTurn != 0) {
                String cardToPlay = players[trickTurn].getCardToPlay(currentTrick, trumpSuit, cardsLeft, knownCards, isTrumping, hasTrump, trickTurn);
                currentTrick.add(cardToPlay);
                int finalI = trickTurn;
                time.getKeyFrames().add(new KeyFrame(Duration.seconds(iterator), e -> playCardVisual(finalI, cardToPlay)));
                iterator++;
                trickTurn = (trickTurn + 1) % 4;
            }
            time.getKeyFrames().add(new KeyFrame(Duration.seconds(iterator-.9), (KeyValue) null));
            time.play();
            time.setOnFinished(e -> blockCards(false));
        }

        else {
            playCardVisual(playerCardPlayed);
            trickTurn++;

            while (trickTurn != leadTrickPlayer) {
                String cardToPlay = players[trickTurn].getCardToPlay(currentTrick, trumpSuit, cardsLeft, knownCards, isTrumping, hasTrump, trickTurn);
                currentTrick.add(cardToPlay);
                int finalI = trickTurn;
                time.getKeyFrames().add(new KeyFrame(Duration.seconds(iterator), e -> playCardVisual(finalI, cardToPlay)));
                iterator++;
                trickTurn = (trickTurn + 1) % 4;
            }
            time.getKeyFrames().add(new KeyFrame(Duration.seconds(iterator+.5), (KeyValue) null));
            time.play();
            time.setOnFinished(e -> finishedTrick());
        }


    }

    private void playCardVisual(int player, String card) {
        ImageView imageView = buildChild(new Image(Objects.requireNonNull(Card.class.getResourceAsStream("/images/"+card+".png"))), 43, 64, 0, 0);
        imageView.setLayoutX(new int[]{50, -90, 50, 193}[player]);
        imageView.setLayoutY(new int[]{173, 42, -100, 42}[player]);
        imageView.setRotate((player%2)*90);
        double[] delta = new double[] {-100, 0, 100, 0};
        trickPane.getChildren().add(imageView);
        playCardAnimation(imageView, delta[(player+1)%4], delta[player]);
        computerToCenter(player);
        cardsLeft.put(card, cardsLeft.get(card)-1);
        playerTrumping(player, card);
        if(knownCards[player].contains(card)) knownCards[player].remove(card);
    }

    private void playCardVisual(String card) {
        ImageView imageView = buildChild(new Image(Objects.requireNonNull(Card.class.getResourceAsStream("/images/" + card + ".png"))), 43, 64, 0, 0);
        imageView.setLayoutX(50);
        imageView.setLayoutY(173);
        trickPane.getChildren().add(imageView);
        playCardAnimation(imageView, 0, -100);
    }

    private void finishedTrick() {
        int winner = declareWinnerOfTrick(currentTrick);
        leadTrickPlayer = (winner+leadTrickPlayer)%4;
        tricksPerTeam[leadTrickPlayer%2] += countPoints();
        currentTrick.clear();
        trickPane.getChildren().clear();
        if (!isFirstTrick) tricksPhaseGroup.getChildren().remove(0);
        setLeadPlayerIndicator();
        winTrickAnimation(leadTrickPlayer);
        playerCardPlayed = null;
        trickTurn = leadTrickPlayer;
        isFirstTrick = false;

        if(!isTrickPhaseOver()) trick();
        else {
            tricksPerTeam[leadTrickPlayer%2] += 2;
            updateTricksPerTeam();
            updateScores();
            toScoreBoard();
        }
    }

    private void toScoreBoard() {
        activate(tricksPhaseGroup, false);
        activate(advanceButton, true);
        advanceButton.setOnAction(this::scoreBoard);
        advanceButton.setText("Scores");
    }

    private void scoreBoard(ActionEvent event) {
        activate(scoresGroup, true);
        activate(bidPhaseGroup, false);
        activate(meldPhaseGroup, false);
        activate(tricksPhaseGroup, false);
        activate(scoresGridPane, false);
        activate(trumpSuitIndicator, false);
        activate(dealerChipsGroup, false);
        activate(handsGroup, false);
        ArrayList<int[]> allScores = new ArrayList<>();
        allScores.add(bidPerTeam);
        allScores.add(meldPerTeam);
        allScores.add(tricksPerTeam);
        allScores.add(scores);
        HBox scoreRow = addNewScoreRow();
        for(int i=0; i<2; i++) {
            for (int j = 0; j < 4; j++) {
                ((Label) ((StackPane) scoreRow.getChildren().get(4*i+j)).getChildren().get(1)).setText(String.valueOf(allScores.get(j)[i]));
            }
        }
        if (max(scores) < 500) toStartHand();
        else toEndGame();
    }

    private void toEndGame() {
        activate(advanceButton, true);
        advanceButton.setOnAction(this::endGame);
        advanceButton.setText("To End");
    }

    private void endGame(ActionEvent event) {
        activate(scoresGroup, false);
        activate(inGameGroup, false);
        activate(endGameGroup, true);
        if(scores[0]>scores[1]) gameResultLabel.setText("You Win!");
        else if(scores[0]<scores[1]) gameResultLabel.setText("You Lose!");
        else gameResultLabel.setText("It's a tie!");
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
     * Toggles on/off an object
     * @param parent Object to toggle
     * @param on Toggle value
     */
    private void activate(ImageView parent, boolean on) {
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

        if(biddersLeft()==1 && isBidding[bidder]==1) computerWonBid(bidder);
        else if (hasBid) choseToBid(bidder);
        else choseToPass(bidder);
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
    private void increaseBid(boolean increase) {
        int multiplier = 1;
        if(!increase) multiplier = -1;
        if (bid < 60-multiplier) bid += 2*multiplier;
        else bid += 5*multiplier;
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
        increaseBid(true);
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
        if (biddersLeft() == 0) throwInHand(bidder);
    }

    /**
     * What happens after a player throws in a hand (none of the 4 players bid)
     * @param bidder Player that dealt
     */
    private void throwInHand(int bidder) {
        endBiddingPhase = true;
        int team = bidder % 2;
        scores[team] -= bid;
        Label scoreLabel = (Label) scoresGridPane.getChildren().get(team);
        scoreLabel.setText(String.valueOf(scores[team]));
        toScoreBoard();
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
                increaseBid(false);
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
        increaseBid(false);
        bidPerTeam[bidder % 2] = bid;

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
        endBiddingPhase = true;
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
            ArrayList<String> meldCards = getMeldCards(i);
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
     * @param player index of player
     * @return ArrayList of cards to lay
     */
    private ArrayList<String> getMeldCards(int player) {
        HashMap<String, Integer> meldCardsHashMap = players[player].getMeldCardsHashMap(trumpSuit);
        ArrayList<String> meldCards = new ArrayList<>();
        for (String card : meldCardsHashMap.keySet()) {
            for (int i = 0; i < meldCardsHashMap.get(card); i++) {
                meldCards.add(card);
                knownCards[player].add(card);
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
                    return false;
                }
            }
        }
        return true;
    }

    /**
     * Adds the tricks pulled for both teams
     */
    private void updateTricksPerTeam() {
        for (int i = 0; i < tricksPerTeam.length; i++) {
            if (tricksPerTeam[i] < 15) {
                meldPerTeam[i] = 0;
                tricksPerTeam[i] = 0;
            }
        }
    }

    private void setScores() {
        for(int i=0; i<2; i++) {
            ((Label) scoresGridPane.getChildren().get(i)).setText(String.valueOf(scores[i]));
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
        if(players[0].getLegalCards(currentTrick, trumpSuit).contains(i)) {
            blockCards(true);
            playerCardPlayed = players[0].getHand()[i].toString();

            if(knownCards[0].contains(playerCardPlayed)) knownCards[0].remove(playerCardPlayed);

            currentTrick.add(playerCardPlayed);

            String card = players[0].playCard(i);
            cardsLeft.put(card, cardsLeft.get(card)-1);
            playerTrumping(0, card);

            ((ImageView) ((HBox) handsGroup.getChildren().get(0)).getChildren().get(i)).setImage(null);

            toCenter();

            trick();
        }
    }

    private int declareWinnerOfTrick(ArrayList<String> trick) {
        ArrayList<String> orderGeneral = getTrickOrder();

        int winner = 0;
        int highestIndex = orderGeneral.indexOf(trick.get(0));
        for(int i=0; i<trick.size(); i++) {
            if(orderGeneral.indexOf(trick.get(i))>highestIndex) {
                highestIndex = orderGeneral.indexOf(trick.get(i));
                winner = i;
            }
        }
        return winner;
    }

    private void blockCards(boolean block) {
        blockPane.setDisable(!block);
    }

    private boolean isTrickPhaseOver() {
        return players[0].getHand()[9]==null;
    }

    private ArrayList<String> getTrickOrder() {
        String suitOfTrick = String.valueOf(currentTrick.get(0).charAt(1));
        ArrayList<String> orderGeneral = new ArrayList<>();
        ArrayList<String> orderOfTrump = new ArrayList<>();
        for(String value : values) {
            orderGeneral.add(value+suitOfTrick);
            orderOfTrump.add(value+trumpSuit);
        }
        if(!orderGeneral.equals(orderOfTrump)) orderGeneral.addAll(orderOfTrump);
        return orderGeneral;
    }

    private int countPoints() {
        int points = 0;
        char[] pointCards = new char[] {'K', 'T', 'A'};
        for(String card : currentTrick) {
            for(char point : pointCards) {
                if(card.charAt(0) == point) points++;
            }
        }
        return points;
    }

    @FXML
    private void exitGame(ActionEvent event) {
        ((Node)(event.getSource())).getScene().getWindow().hide();
    }

    private HBox addNewScoreRow() {
        HBox scoreRow = new HBox();
        for(int i=0; i<2; i++) {
            for (int j = 0; j < 4; j++) {
                StackPane box = new StackPane();
                Rectangle background = new Rectangle();
                if (j == 3) background.setWidth(70);
                else background.setWidth(60);
                background.setHeight(15);
                background.setFill(Paint.valueOf("#FFFFFF"));
                background.setStroke(Paint.valueOf("#000000"));
                background.setStrokeType(StrokeType.INSIDE);
                background.setStrokeLineCap(StrokeLineCap.SQUARE);
                background.setStrokeLineJoin(StrokeLineJoin.MITER);
                background.setArcWidth(5);
                background.setArcHeight(5);
                box.getChildren().add(background);

                Label number = new Label();
                number.setAlignment(Pos.BASELINE_CENTER);
                number.setFont(Font.font("Bodoni MT Black"));
                box.getChildren().add(number);
                scoreRow.getChildren().add(box);
            }
        }
        scoresVBox.getChildren().add(scoreRow);
        return scoreRow;

    }

    private void computerToCenter(int player) {
        int nullCounter = 0;
        Card[] hand = players[player].getHand();
        for (Card card : hand) {
            if (card == null) nullCounter++;
        }
        if(nullCounter%2==0) ((ImageView) ((HBox) handsGroup.getChildren().get(player)).getChildren().get(nullCounter/2)).setImage(null);
        else ((ImageView) ((HBox) handsGroup.getChildren().get(player)).getChildren().get(20-(nullCounter+1)/2)).setImage(null);
    }

    private void playCardAnimation(ImageView imageView, double x, double y) {
        TranslateTransition translate = new TranslateTransition(Duration.seconds(.75), imageView);
        translate.setByX(x);
        translate.setByY(y);
        translate.play();
    }

    private void setLeadPlayerIndicator() {
        ImageView imageView = new ImageView();
        imageView.setImage(BACK);
        imageView.setLayoutX(237);
        imageView.setLayoutY(130);
        imageView.setFitWidth(20);
        imageView.setFitHeight(30);
        tricksPhaseGroup.getChildren().add(imageView);
        imageView.toBack();
    }

    private void winTrickAnimation(int winner) {
        double x, y, rot = 180;
        switch(winner) {    //  237, 130
            case 0 -> {     //  211, 202
                x = -26;
                y = 72;
            } case 1 -> {   // 5, 117
                x = -232;
                y = -13;
                rot = 90;
            } case 2 -> {   // 211, 0
                x = -26;
                y = -130;
            } case 3 -> {   // 424, 121
                x = 187;
                y = -9;
                rot = 90;
            } default -> x = y = 0;
        }
        TranslateTransition translate = new TranslateTransition(Duration.seconds(.5), tricksPhaseGroup.getChildren().get(0));
        translate.setByX(x);
        translate.setByY(y);
        RotateTransition rotate = new RotateTransition(Duration.seconds(.2*rot/90), tricksPhaseGroup.getChildren().get(0));
        rotate.setByAngle(rot);
        translate.play();
        rotate.play();
    }

    private void playerTrumping(int player, String card) {
        int suitOfTrickIndex = IntStream.range(0, suits.length).filter(i -> suits[i].equals(String.valueOf(currentTrick.get(0).charAt(1)))).findFirst().orElse(-1);
        if(!isTrumping[player][suitOfTrickIndex] && currentTrick.get(0).charAt(1)!=card.charAt(1) && trumpSuit.equals(String.valueOf(card.charAt(1)))) { // Trumped
            isTrumping[player][suitOfTrickIndex] = true;
            System.out.println("Player " + player + " is out of " + suits[suitOfTrickIndex]);
        } else if(hasTrump[player] && !getTrickOrder().contains(card)) { // No trump left
            hasTrump[player] = false;
            isTrumping[player] = new boolean[suits.length];
            System.out.println("Player " + player + " is out of trump");
        }

        ArrayList<String> trick = (ArrayList<String>) currentTrick.clone();
        for(int i=trick.size()-1; i>=0; i--) {
            if(i>(4+player-leadTrickPlayer)%4)
                trick.remove(i);
        }

        if(declareWinnerOfTrick(trick)!=trick.size()-1) {
            int winner = declareWinnerOfTrick(trick);
            ArrayList<String> order = getTrickOrder();
            int trumpSuitIndex = IntStream.range(0, suits.length).filter(i -> suits[i].equals(trumpSuit)).findFirst().orElse(-1);
            if(order.contains(card)) {
                if (suits[suitOfTrickIndex].equals(String.valueOf(card.charAt(1))) && order.indexOf(trick.get(winner)) < order.indexOf(maxCards[player][suitOfTrickIndex]) && order.indexOf(trick.get(winner)) < 5) maxCards[player][suitOfTrickIndex] = trick.get(winner);
                else if (order.indexOf(trick.get(winner)) < order.indexOf(maxCards[player][trumpSuitIndex])) maxCards[player][trumpSuitIndex] = trick.get(winner);
            }
        }
    }
}
