package Poker;

/**
 * This class represents a player object for the poker game.
 */
public class Player
{
    private int chipAmount;
    private static int numberOfPlayers = 0;
    private int playerID;
    private Card [] hand;
    private int chipsInBetRound;
    private boolean stillInRound;
    private int chipsInRound;
    private boolean allIn;
    private int allInPot;
    private int[] handRanking;

    Player()
    {
        chipAmount = 5000;
        playerID = numberOfPlayers;
        numberOfPlayers++;
        chipsInBetRound = 0;
        chipsInRound = 0;
        stillInRound = true;
        allIn = false;
        allInPot = 0;
        hand = new Card[2];
        handRanking = null;
    }

    public int getChipAmount() {
        return chipAmount;
    }

    public void setChipAmount(int chipAmount) {
        this.chipAmount = chipAmount;
    }

    public int getPlayerID() {
        return playerID;
    }
    public void setPlayerID(int playerID) {
        this.playerID = playerID;
    }

    public Card[] getHand() {
        return hand;
    }

    public void setHand(Card[] hand) {
        this.hand = hand;
    }

    public Card getCard1()
    {
        return hand[0];
    }

    public void setCard1 (Card card1)
    {
        hand[0] = card1;
    }

    public Card getCard2()
    {
        return hand[1];
    }

    public void setCard2(Card card2)
    {
        hand[1] = card2;
    }

    public int getChipsInBetRound()
    {
        return chipsInBetRound;
    }

    public void setChipsInBetRound(int chipsInBetRound) {
        this.chipsInBetRound = chipsInBetRound;
    }

    public boolean isStillInRound() {
        return stillInRound;
    }

    public void setStillInRound(boolean stillInRound) {
        this.stillInRound = stillInRound;
    }

    public boolean isAllIn() {
        return allIn;
    }

    public void setAllIn(boolean allIn) {
        this.allIn = allIn;
    }

    public int getAllInPot() {
        return allInPot;
    }

    public void setAllInPot(int allInPot) {
        this.allInPot = allInPot;
    }

    public int getChipsInRound() {
        return chipsInRound;
    }

    public void setChipsInRound(int chipsInRound) {
        this.chipsInRound = chipsInRound;
    }

    public int[] getHandRanking() {
        return handRanking;
    }

    public void setHandRanking(int[] handRanking) {
        this.handRanking = handRanking;
    }

    public void roundReset() {
        hand = new Card[2];
        chipsInBetRound = 0;
        chipsInRound = 0;
        allInPot = 0;
        handRanking = null;
    }
}
