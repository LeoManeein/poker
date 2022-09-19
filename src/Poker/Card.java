package Poker;

/**
 * this class represents a card object. Each {@code Poker.Card} has a {@code value} value which goes from 2 to A.
 * Each {@code Poker.Card} has a {@code suit} value which is either "clubs", "diamonds", "hearts", or "spades".
 */
public class Card
{
    /**
     * a value of a playing card that can range from 2, 3, 4, ... Q, K, A.
     */
    private String value;
    /**
     * a suit of a playing card that can be either "clubs", "diamonds", "hearts", or "spades".
     */
    private String suit;

    /**
     * constructs a {@code Poker.Card} object with the given {@code value} and {@code suit}
     * @param value the given value of the card
     * @param suit the given suit of the card
     */
    public Card(String value, String suit) {
        this.value = value;
        this.suit = suit;
    }

    /**
     * returns the value of the {@code Poker.Card}
     * @return the value
     */
    public String getValue() {
        return value;
    } // getValue

    /**
     * returns the suit of the {@code Poker.Card}
     * @return the suit
     */
    public String getSuit() {
        return suit;
    } // getSuit

    /**
     * returns the value and suit in a readable way to the user
     * @return in a String the value and suit of the card
     */
    @Override
    public String toString() {
        return value + " of " + suit;
    } // toString
}
