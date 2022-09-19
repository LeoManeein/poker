package Poker;

import java.util.*;

/**
 * This class represents a deck of playing cards with 52 unique {@code Poker.Card} objects.
 * This {@code Poker.Deck} can reset the deck, shuffle, and draw a card via {@link #newDeck()}, {@link #shuffleDeck()},
 * and {@link #drawCard()} methods respectively.
 */
public class Deck
{
    /**
     * List of {@code Poker.Card} objects.
     */
    private ArrayList<Card> cards = new ArrayList<>();
    /**
     * The board in a poker game in which players try to match cards to.
     * Array of 5 {@code Poker.Card} objects that gets added throughout the poker game.
     */
    private Card [] board = new Card[5];
    static Random rand = new Random();

    /**
     * This constructor calls the method {@link #newDeck()} and creates a new deck.
     */
    Deck()
    {
        newDeck();
    }

    /**
     * returns the values of the {@code board}
     * @return the {@code board}
     */
    public Card[] getBoard() {
        return board;
    }

    public void setBoard(Card[] board) {
        this.board = board;
    }

    /**
     * Resets the deck to a new deck of 52 {@code cards} un-shuffled
     * also resets the {@code board} of the deck object
     */
    public void newDeck()
    {
        String [] suits = {"Clubs", "Diamonds", "Hearts", "Spades"};
        String [] values = {"2", "3", "4", "5", "6", "7", "8", "9", "T", "J", "Q", "K", "A"};

        if (cards.size() > 0) {
            cards.subList(0, cards.size()).clear();
        }
        for (int i = 0; i < 4; i++)
        {
            for (int j = 0; j < 13; j++)
            {
                cards.add(new Card(values[j], suits[i]));
            }
        }
        Arrays.fill(board, null);
    }

    /**
     * This method will shuffle {@code cards} into a random order
     */
    public void shuffleDeck()
    {
        //putting cards from deck into a tracker array in a random order
        int pos;
        ArrayList<Card> randomDeck = new ArrayList<>();
        int [] tracker = new int[52];
        for (int i = 0; i<52; i++)
        {
            boolean randomCardAdded = false;
            do {
                pos = rand.nextInt(52);
                if (tracker[pos] == 0)
                {
                    randomDeck.add(cards.get(pos));
                    tracker[pos] = 1;
                    randomCardAdded = true;
                }
            } while (!randomCardAdded);
        }
        //saving tracker array into deck array
        for (int j = 0; j<52; j++)
        {
            cards.set(j, randomDeck.get(j));
        }
    }

    /**
     * This method will return a single card from {@code cards} and remove it from {@code cards}.
     * @return the Poker.Card object in the last index of {@code cards}
     */
    public Card drawCard()
    {
        Card topCard = cards.get(cards.size()-1);
        cards.remove(cards.size()-1);
        return topCard;
    }
}
