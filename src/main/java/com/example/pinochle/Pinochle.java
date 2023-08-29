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
import javafx.util.Duration;

import java.net.URL;
import java.util.*;

/**
 * Main place where things happen
 */
public class Pinochle implements Initializable {

    /**
     * Pass/Bid
     */
    @FXML
    private GridPane bidActionsGrid;
    /**
     * Advances to next stage on click
     */
    @FXML
    private Button advanceButton;
    /**
     * Cards of opponent 1
     */
    @FXML
    private VBox opponent1VBox;
    /**
     * Cards of opponent 2
     */
    @FXML
    private VBox opponent2VBox;
    /**
     * Other team's score label
     */
    @FXML
    private Label otherTeamScoreLabel;
    /**
     * Player bid/pass label
     */
    @FXML
    private Label playerBidLabel;
    /**
     * Player cards container
     */
    @FXML
    private HBox playerCardsHBOX;
    /**
     * Player team's score label
     */
    @FXML
    private Label playerTeamScoreLabel;
    /**
     * Where scores are shown
     */
    @FXML
    private GridPane scoresGridPane;
    /**
     * Suits for picking trump
     */
    @FXML
    private GridPane suitsGrid;
    /**
     * Teammate cards
     */
    @FXML
    private HBox teammateHBox;
    /**
     * Starts game on click
     */
    @FXML
    private Button welcomeButton;
    /**
     * Just to look nice
     */
    @FXML
    private Group welcomeImageGroup;
    /**
     * Welcome!
     */
    @FXML
    private Label welcomeLabel;
    /**
     * Where meld is laid
     */
    @FXML
    private Group playersMeldsGroup;
    /**
     * Where bids are shown
     */
    @FXML
    private Group bidStackPanesGroup;
    /**
     * Shows trump suit
     */
    @FXML
    private StackPane trumpSuitIndicatorStackPane;
    /**
     * Shows dealer
     */
    @FXML
    private Group dealerChipsGroup;
    /**
     * Blocks user clicks
     */
    @FXML
    private Pane blockPane;


    /**
     * Amount of players
     */
    private int PLAYERCOUNT;
    /**
     * Image of back of card
     */
    private Image BACK;
    /**
     * Array of players
     */
    private Player[] players;
    /**
     * Images for each card in player's hand
     */
    private ImageView[] playerCardImages;
    /**
     * Deck of cards object
     */
    private Deck deck;
    /**
     * Array of scores
     */
    private int[] scores;
    /**
     * Trump suit of the hand
     */
    private String trumpSuit;
    /**
     * Dealer of the hand
     */
    private int dealer;
    /**
     * An array of actions players took in bidding phase
     */
    private int[] isBidding;
    /**
     * An array to store which players were competing in bidding phase at some point
     */
    private boolean[] bidAtLeastOnce;
    /**
     * Current bid amount
     */
    private int bid;
    /**
     * Indicator of when bidding phase ends
     */
    private boolean endBiddingPhase;
    /**
     * What each team bid per hand
     */
    private int[] bidPerTeam;
    /**
     * How much meld each team has per hand
     */
    private int[] meldPerTeam;
    /**
     * How many tricks each team pulled per hand
     */
    private int[] tricksPerTeam;

    /**
     * Starts the program
     * @param url given
     * @param resourceBundle given
     */
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        getVariables();
    }

    /**
     * Starts the game!
     */
    @FXML
    void startGame() {
        welcomeButton.setDisable(true);
        welcomeButton.setVisible(false);

        welcomeImageGroup.setDisable(true);
        welcomeImageGroup.setVisible(false);

        welcomeLabel.setDisable(true);
        welcomeLabel.setVisible(false);

        advanceButton.setDisable(false);
        advanceButton.setVisible(true);

        scoresGridPane.setDisable(false);
        scoresGridPane.setVisible(true);

        startHand();
    }

    /**
     * Initializes variables
     */
    private void getVariables() {
        dealer = 0;
        PLAYERCOUNT = 4;
        scores = new int[] {0, 0};
        BACK = new Image(Objects.requireNonNull(Card.class.getResourceAsStream("/images/back.png")));
        deck = new Deck();
        players = new Player[PLAYERCOUNT];
        for (int i = 0; i < PLAYERCOUNT; i++) {
            players[i] = new Player();
        }
    }

    /**
     * Deals the cards and sets images of them
     */
    private void deal() {
        clearHBox(playerCardsHBOX);
        clearVBox(opponent1VBox);
        clearHBox(teammateHBox);
        clearVBox(opponent2VBox);

        dealerChipsGroup.getChildren().get(dealer).setVisible(true);

        Card[][] deal = deck.deal();

        for (int i = 0; i < PLAYERCOUNT; i++) {
            players[i].setHand(deal[i]);
        }

        System.out.println(players[0]);
        setCards();
        for(int i=0; i<playerCardsHBOX.getChildren().size(); i++) {
            setImageViewUserData(i);
        }
        getPlayerCardImages();
        setTrumpSuitIndicatorStackPane();
    }

    /**
     * Starts the hand by dealing and appropriately setting images
     */
    private void startHand() {
        bidPerTeam = new int[] {0, 0};
        meldPerTeam = new int[] {0, 0};
        tricksPerTeam = new int[] {0, 0};

        for(int i=0; i<playersMeldsGroup.getChildren().size(); i++) {
            VBox parent = (VBox) playersMeldsGroup.getChildren().get(i);
            for (int j = 0; j< Objects.requireNonNull(parent).getChildren().size(); j++) {
                HBox subbox = (HBox) parent.getChildren().get(j);
                for(int k=0; k<subbox.getChildren().size(); k++) {
                    ((ImageView) subbox.getChildren().get(k)).setImage(null);
                }
            }
        }
        deal();
        setAdvanceButton(true);
        setBidStackPanes(false);
        ((ImageView) trumpSuitIndicatorStackPane.getChildren().get(0)).setImage(null);
        advanceButton.setOnAction(this::startBid);
        advanceButton.setText("To Bid");
        bid = 52;
    }

    /**
     * Starts a new hand
     * @param event button click
     */
    @FXML
    void startHand(ActionEvent event) {
        dealer = (dealer+1)%4;
        dealerChipsGroup.getChildren().get((dealer+3)%4).setVisible(false);
        dealerChipsGroup.getChildren().get(dealer).setVisible(true);
        startHand();
    }

    /**
     * Starts bid phase
     * @param event button click
     */
    @FXML
    void startBid(ActionEvent event) {
        setBidActionsGrid();
        setBidStackPanes(true);
        advanceButton.setOnAction(this::meldPhase);
        advanceButton.setText("To Meld");
        setAdvanceButton(false);
        setBidLabels();
        isBidding = new int[] {0, 0, 0, 0};
        bidAtLeastOnce = new boolean[] {false, false, false, false};
        endBiddingPhase = false;
        for(int i=0; i<PLAYERCOUNT; i++) {
            for(int j : players[i].getExpectedPoints(bidAtLeastOnce[(i+2)%4])) {
                System.out.print(j + "  ");
            }
            System.out.println();
        }
        bid(false, false, false);
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
        //players[0].playCard(i);

        playerCardImages[i].setImage(null);

        //TranslateTransition transition = new TranslateTransition(Duration.seconds(1), toMove);
        //transition.setByX(-17*(i-10));
        //transition.setByY(-100);
        //transition.play();
        //transition.setOnFinished(e -> );

        toCenter();
    }

    /**
     * Centers cards after clicking one
     */
    private void toCenter() {
        int nullCounter = 0;
        ArrayList<Image> notNull = new ArrayList<>();
        int size = playerCardImages.length;
        for (ImageView playerCardImage : playerCardImages) {
            if (playerCardImage.getImage() != null)
                notNull.add(playerCardImage.getImage());
            else nullCounter++;
        }
        int index = 0;
        for(int i=0; i<size; i++) {
            if(i<nullCounter/2 || i>=20-Math.round((double) nullCounter/2))
                playerCardImages[i].setImage(null);
            else {
                playerCardImages[i].setImage(notNull.get(index));
                setImageViewUserData(index);
                index++;
            }
        }
    }

    /**
     * Clears all children from Vbox
     * @param parent VBox to clear
     */
    private void clearVBox(VBox parent) {
        parent.getChildren().clear();
    }

    /**
     * Clears all children from HBox
     * @param parent HBox to clear
     */
    private void clearHBox(HBox parent) {
        parent.getChildren().clear();
    }

    /**
     * Takes empty Hbox and Vbox and fills them with cards according to who they are
     */
    private void setCards() {
        for(int i=0; i<20; i++) {
            playerCardsHBOX.getChildren().add(buildChild(players[0].getHand()[i].getImage(), 57, 79, -40*(i+1.5), 0, 0));
            opponent1VBox.getChildren().add(buildChild(BACK, 30, 49, 0, -42*(i), 90));
            teammateHBox.getChildren().add(buildChild(BACK, 30, 49, -23*(i-2), 0, 0));
            opponent2VBox.getChildren().add(buildChild(BACK, 30, 49, 0, -42*(i), 90));
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
    private ImageView buildChild(Image image, double x, double y, double dx, double dy, double rot) {
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
     * @param on show/hide
     */
    private void setSuitsGrid(boolean on) {
        for(int i=0; i<suitsGrid.getChildren().size(); i++) {
            ImageView imageView = (ImageView) suitsGrid.getChildren().get(i);
            imageView.setUserData(i);
            imageView.setOnMouseClicked(event -> pickSuit((int) imageView.getUserData()));
        }
        suitsGrid.setVisible(on);
        suitsGrid.setDisable(!on);
        if(on) suitsGrid.toFront();
        else suitsGrid.toBack();
    }

    /**
     * On click, determine which suit is trump for this hand
     * @param i clicked, which to use as index for suits
     */
    private void pickSuit(int i) {
        String[] suits = new String[] {"S", "C", "H", "D"};
        trumpSuit = suits[i];
        setSuitsGrid(false);
        endBiddingPhase();
    }

    /**
     * Lays all players' meld on the table
     */
    private void layMeld() {
        for(int i=0; i<playersMeldsGroup.getChildren().size(); i++) {
            VBox parent = (VBox) playersMeldsGroup.getChildren().get(i);
            ArrayList<String> meldCards = getMeldCards(players[i].getMeldCardsHashMap(trumpSuit));
            for (int j = 0; j < Objects.requireNonNull(parent).getChildren().size(); j++) {
                HBox subBox = (HBox) parent.getChildren().get(j);
                clearHBox(subBox);
                for (int k = 0; k < 5; k++) {
                    if (5 * j + k < meldCards.size()) {
                        Image image = new Image(Objects.requireNonNull(Card.class.getResourceAsStream("/images/" + meldCards.get(5 * j + k) + ".png")));
                        subBox.getChildren().add(buildChild(image, 45, 70, -35 * k, -50 * j, 0));
                    }
                }
            }
        }
    }

    /**
     * Bidding phase
     * @param startComputersAtStart whether to start the cpu bids from the player's left
     * @param playerTurn whether it is the player's turn
     * @param playerHasBid whether the player bid
     */
    private void bid(boolean startComputersAtStart, boolean playerTurn, boolean playerHasBid) {
        if (!endBiddingPhase) {
            int bidder = 1;
            if (!startComputersAtStart)
                bidder += dealer;

            // Human player
            if (playerTurn) {
                if (playerHasBid) {
                    playerBidLabel.setText("Bid: " + bid);
                    isBidding[0] = 1;
                    bidAtLeastOnce[0] = true;
                    if (bid != 50) {
                        bid += 2;
                        if (bid > 60)
                            bid += 3;
                    }
                } else {
                    isBidding[0] = -1;
                    playerBidLabel.setText("Pass!");
                    if (biddersLeft() == 0) {
                        advanceButton.setText("New Deal");
                        advanceButton.setOnAction(this::startHand);
                        setAdvanceButton(true);
                        scores[0] -= 50;
                        playerTeamScoreLabel.setText(String.valueOf(scores[0]));
                    }
                }
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
            else time.setOnFinished(e -> bid(true, false, false));
        }
    }

    /**
     * Counts the amount of bidders still in the bid
     * @return amount left
     */
    private int biddersLeft() {
        int bidders = 0;
        for(int bit : isBidding) {
            if(bit!=-1) bidders++;
        }
        return bidders;
    }

    /**
     * Gets the cards needed to lay meld
     * @param meldCardsHashMap Cards and amounts needed
     * @return Cards in order to lay
     */
    private ArrayList<String> getMeldCards(HashMap<String, Integer> meldCardsHashMap) {
        ArrayList<String> meldCards = new ArrayList<>();
        for(String card : meldCardsHashMap.keySet()) {
            for(int i=0; i<meldCardsHashMap.get(card); i++) {
                meldCards.add(card);
            }
        }
        return meldCards;
    }

    /**
     * Sets the bid labels for bid phase
     */
    private void setBidLabels() {
        String[] names = new String[] {"Player", "Opponent 1", "Teammate", "Opponent 2"};
        for(int i=0; i<bidStackPanesGroup.getChildren().size(); i++) {
            ((Label) ((StackPane) bidStackPanesGroup.getChildren().get(i)).getChildren().get(1)).setText(names[i]);
        }
    }

    /**
     * Sets bid actions grid for when it is player's turn to bid
     */
    private void setBidActionsGrid() {
        for(int i=0; i<bidActionsGrid.getChildren().size(); i++) {
            Button button = (Button) bidActionsGrid.getChildren().get(i);
            button.setUserData(i);
            button.setOnAction(event -> bidAction((int) button.getUserData()));
        }
        ((Button) bidActionsGrid.getChildren().get(0)).setText("Pass");
    }

    /**
     * Reads the user's decision to bid or pass
     * @param i decision
     */
    private void bidAction(int i) {
        bidActionsGrid.setVisible(false);
        bidActionsGrid.setDisable(true);
        bid(true, true, i==1);
    }

    /**
     * Sets the display of bids
     * @param on show/hide
     */
    private void setBidStackPanes(boolean on) {
        for(int i=0; i<bidStackPanesGroup.getChildren().size(); i++) {
            StackPane stackPane = (StackPane) bidStackPanesGroup.getChildren().get(i);
            stackPane.setDisable(!on);
            stackPane.setVisible(on);
        }
    }

    /**
     * What the computer will do on their bid
     * @param bidder computer whose turn it is
     */
    private void computerBid(int bidder) {
        Label label = (Label) ((StackPane) bidStackPanesGroup.getChildren().get(bidder)).getChildren().get(1);

        if(isBidding[bidder]==0 && biddersLeft()==1) bid = 50; // Bid dropped on computer

        int max = max(players[bidder].getExpectedPoints(bidAtLeastOnce[(bidder+2)%4]));
        boolean hasBid = bid<=max;
        if(isBidding[bidder]!=-1 && hasBid && biddersLeft()>1) { // Computer bidding
            isBidding[bidder] = 1;
            bidAtLeastOnce[bidder] = true;
            assert label != null;
            label.setText("Bid: " + bid);
            bid += 2;
            if (bid > 60) bid += 3;
        } else if(((isBidding[bidder]==1 && biddersLeft()==1) || (isBidding[bidder] == 0 && biddersLeft()==1 && hasBid)) && !endBiddingPhase) { // Computer wins bid
            if(isBidding[bidder] == 1) {
                if(bid<=60) bid-=2;
                else bid-=5;
            }
            assert label != null;
            label.setText("Bid: " + bid);
            bidPerTeam[bidder%2] = bid;
            endBiddingPhase = true;
            computerWonBid(bidder);
        } else if(isBidding[bidder]==0 && !hasBid && biddersLeft()==1) { // Computer gets dropped on but passes
            int team = bidder%2;
            Label scoreLabel;
            scores[team] -= bid;
            if(team==1) scoreLabel = otherTeamScoreLabel;
            else scoreLabel = playerTeamScoreLabel;
            setAdvanceButton(true);
            scoreLabel.setText(String.valueOf(scores[team]));
            isBidding[bidder] = -1;
            assert label != null;
            label.setText("Pass!");
            advanceButton.setOnAction(this::startHand);
            advanceButton.setText("New Deal");
        } else { // Computer passes
            isBidding[bidder] = -1;
            assert label != null;
            label.setText("Pass!");
        }
    }

    /**
     * Edits bid actions grid for player's turn
     */
    private void editBidActionsGrid() {
        if(isBidding[0]!=-1 && biddersLeft()>1) { // Player is in bid, but so are others
            bidActionsGrid.setDisable(false);
            ((Button) bidActionsGrid.getChildren().get(1)).setText("Bid "+bid);
            bidActionsGrid.setVisible(true);
        } else if(isBidding[0]==0 && biddersLeft()==1) { // Bid is dropped on player
            bid = 50;
            bidActionsGrid.setDisable(false);
            ((Button) bidActionsGrid.getChildren().get(0)).setText("Throw");
            ((Button) bidActionsGrid.getChildren().get(1)).setText("Bid "+bid);
            bidActionsGrid.setVisible(true);
        } else if(isBidding[0]==1 && biddersLeft()==1) { // Player has won bid
            bidPerTeam[0] += bid;
            setSuitsGrid(true);
            setBidStackPanes(false);
        }
    }

    /**
     * What the computer does when they win the bid
     * @param bidder computer that won bid
     */
    private void computerWonBid(int bidder) {
        String[] suits = new String[]{"C", "H", "S", "D"};
        int[] expectedPoints = players[bidder].getExpectedPoints(bidAtLeastOnce[(bidder+2)%4]);
        int max = max(expectedPoints);
        for (int i = 0; i < PLAYERCOUNT; i++) {
            if (expectedPoints[i] == max) trumpSuit = suits[i];
        }
        endBiddingPhase();
    }
    /**
     * Sets the trump suit indicator
     */
    private void setTrumpSuitIndicatorStackPane() {
        ImageView imageView = new ImageView();
        imageView.setFitWidth(48);
        imageView.setFitHeight(48);
        trumpSuitIndicatorStackPane.getChildren().add(imageView);
    }

    /**
     * Sets the advance button
     * @param on show/hide
     */
    private void setAdvanceButton(boolean on) {
        advanceButton.setDisable(!on);
        advanceButton.setVisible(on);
    }

    /**
     * Gets the max of an array
     * @param nums array to get max
     * @return max number
     */
    private int max(int[] nums) {
        int max = nums[0];
        for(int i : nums) {
            max = Math.max(max, i);
        }
        return max;
    }

    /**
     * Updates meld per team to add to score
     * @return true - team that bid has >=15 meld (play hand)
     *        false - team that bid has <15 meld (throw in)
     */
    private boolean updateMeldPerTeam() {
        int trumpSuitIndex = -1;
        String[] suits = new String[] {"C", "H", "S", "D"};
        for(int i=0; i<suits.length; i++) {
            if(trumpSuit.equals(suits[i])) trumpSuitIndex = i;
        }
        for(int i=0; i<PLAYERCOUNT; i++) {
            meldPerTeam[i%2] += players[i].calcMeld()[trumpSuitIndex];
        }
        advanceButton.setText("Sim Hand");
        for(int i=0; i<meldPerTeam.length; i++) {
            if(meldPerTeam[i]<15) {
                meldPerTeam[i] = 0;
                if(bidPerTeam[i]==bid) {
                    scores[i] -= bid;
                    scores[(i+1)%2] += meldPerTeam[(i+1)%2];
                    updateScoreLabels();
                    advanceButton.setText("New Deal");
                    advanceButton.setOnAction(this::startHand);
                    return false;
                }
            }
        }
        return true;
    }

    /**
     * Deactivates all things needed during bidding phase and sets up trick phase
     */
    private void endBiddingPhase() {
        setAdvanceButton(true);
        ((ImageView) trumpSuitIndicatorStackPane.getChildren().get(0)).setImage(new Image(Objects.requireNonNull(Card.class.getResourceAsStream("/images/" + trumpSuit + ".png"))));
    }

    /**
     * Updates tricksPerTeam by counting how many points each team pulled
     * TODO: add tricks phase and update this method
     */
    private void updateTricksPerTeam() {
        int trumpSuitIndex = -1;
        String[] suits = new String[] {"C", "H", "S", "D"};
        for(int i=0; i<suits.length; i++) {
            if(trumpSuit.equals(suits[i])) trumpSuitIndex = i;
        }
        for(int i=0; i<PLAYERCOUNT; i++) {
            tricksPerTeam[i%2] += players[i].getExpectedPoints(bidAtLeastOnce[(i+2)%4])[trumpSuitIndex]-players[i].calcMeld()[trumpSuitIndex]-8;
        }
        for(int i=0; i<tricksPerTeam.length; i++) {
            if(tricksPerTeam[i] < 15) {
                meldPerTeam[i] = 0;
                tricksPerTeam[i] = 0;
            }
        }
    }

    /**
     * Updates scores after each hand
     */
    private void updateScores() {
        for(int i=0; i<PLAYERCOUNT/2; i++) {
            if(meldPerTeam[i]+tricksPerTeam[i]>bidPerTeam[i]) scores[i] += meldPerTeam[i]+tricksPerTeam[i];
            else scores[i]-=bidPerTeam[i];
        }
        updateScoreLabels();
    }

    /**
     * Updates score labels to show new scores
     */
    private void updateScoreLabels() {
        playerTeamScoreLabel.setText(String.valueOf(scores[0]));
        otherTeamScoreLabel.setText(String.valueOf(scores[1]));
    }

    private void meldPhase(ActionEvent event) {
        System.out.println(bid);
        setBidStackPanes(false);
        layMeld();
        if (updateMeldPerTeam()) {
            updateTricksPerTeam();
            updateScores();
        }
        advanceButton.setOnAction(this::startHand);
    }
}