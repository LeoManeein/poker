package Poker;

import java.io.IOException;
import java.util.*;

/**
 * This Poker.PlayPoker program simulates a poker card game following the rules of Texas Hold'em Poker.
 * This program does not use any GUIs in this version.
 * This program is in the
 * @author Leo Maneein
 * @version 1.0
 * @since 2022-02-28
 */
public class PlayPoker {
    static Random rand = new Random();
    static Scanner scan = new Scanner(System.in);

    public static void main(String[] args) {
        ArrayList<Player> players = new ArrayList<>();
        Deck deck = new Deck();
        boolean valid = false;

        //Starting Screen
        System.out.println("<<Texas Hold'em Poker Tournament>>\n");

        //Adding players
        String NOP;
        int numberOfPlayers = 0;
        while (!valid) {
            System.out.print("Enter number of players to join the table (including yourself) (4-10):\040");
            try {
                NOP = scan.nextLine();
                numberOfPlayers = Integer.parseInt(NOP);
                if (numberOfPlayers < 4 || numberOfPlayers > 10) {
                    throw new InvalidFormatException("Invalid input. Must be an integer between 4 and 10");
                }
                valid = true;
            } catch (NumberFormatException e) {
                System.out.println("Invalid input, Must be an integer between 4 and 10");
                System.out.print("Re-");
            } catch (InvalidFormatException e) {
                System.out.println(e.getMessage());
                System.out.print("Re-");
            }
        }

        for (int i = 0; i < numberOfPlayers; i++) {
            players.add(new Player());
        }

        //setting blinds
        System.out.println("\nEach player will start with $5000");
        System.out.println("Blinds will start at $25/50 and increase every 2 rotations");
        System.out.println("You will start in the dealer/button position");

        //setting first positions
        int button = 0;
        int littleBlindPosition = setBlind(players, button);
        int bigBlindPosition = setBlind(players, littleBlindPosition);
        int firstBetPosition = setBlind(players, bigBlindPosition);
        if (firstBetPosition == 0) {
            System.out.println("You will be the first to bet");
        } else {
            System.out.println("Player " + firstBetPosition + " is the first to bet");
        }

        int LB = 25;
        int BB = LB * 2;
        int roundCount = 0;
        int pot = 0;

        do {
            roundCount++;
            if (roundCount % 5 == 0)
            {
                LB *= 2;
                BB = LB * 2;
            }
            //reset player stats
            for (Player p: players) {
                p.roundReset();
            }

            // Starting round
            System.out.println("""
                                    
                    <<Starting new round>>""");
            pause();
            deck.newDeck();
            deck.shuffleDeck();

            // print blinds
            System.out.println("The blinds are $" + LB + "/" + BB);

            // Dealing cards
            dealCards(players, deck, littleBlindPosition);
            // Make this a method

            System.out.println("You drew " + players.get(0).getHand()[0] + " & " + players.get(0).getHand()[1]);

            // Pre-flop betting
            int currentBet = BB;
            int minBet = 0;
            int playerBet = 0;
            String userChoice;

            // First loop of pre-flop betting
            // First part of loop
            System.out.println("\n<<Pre-Flop betting>>");
            pause();
            for (int i = firstBetPosition; i < players.size(); i++) {
                if (players.get(i).getPlayerID() == 0 && players.get(i).getPlayerID() == firstBetPosition) {
                    players.get(i).setChipsInBetRound(0);
                    boolean validChar = false;
                    do {
                        System.out.println("You have $" + players.get(i).getChipAmount());
                        System.out.println("The minimum you can bet is $" + currentBet);
                        System.out.println("If you have less chips than the minimum bet, calling will automatically put you All-In.");
                        System.out.print("Would you like to Bet, Call, or Fold (B/C/F): ");
                        userChoice = scan.nextLine().toUpperCase();
                        switch (userChoice) {
                            case "B" -> {
                                if (players.get(i).getChipAmount() < currentBet) {
                                    break;
                                }
                                valid = false;
                                System.out.println("You have $" + players.get(i).getChipAmount());
                                System.out.println("The minimum you can bet is $" + currentBet);
                                while (!valid)
                                {
                                    try
                                    {
                                        System.out.print("How much would you like to bet: ");
                                        playerBet = scan.nextInt();
                                        scan.nextLine();
                                        if (playerBet < currentBet || playerBet > players.get(i).getChipAmount())
                                        {
                                            throw new InvalidFormatException("Poker: Invalid Input. Enter an integer between " + currentBet + " and " + players.get(i).getChipAmount());
                                        }
                                        valid = true;
                                    } catch (InvalidFormatException e) {
                                        System.out.println(e.getMessage());
                                    }
                                    catch (InputMismatchException e ) {
                                        System.out.println("Poker: invalid Input. " + e.getMessage());
                                    }
                                }
                                players.get(i).setChipsInBetRound(playerBet);
                                players.get(i).setChipsInRound(players.get(i).getChipsInBetRound());
                                currentBet = playerBet;
                                pot = pot + currentBet;
                                players.get(i).setChipAmount(players.get(i).getChipAmount() - currentBet);
                                if (players.get(i).getChipAmount() == 0) {
                                    System.out.println("You are All-In!");
                                    players.get(i).setAllIn(true);
                                } else {
                                    System.out.println("You have $" + players.get(i).getChipAmount());
                                }
                                System.out.println("Current bet has been raised to $" + currentBet);
                                System.out.println();
                                validChar = true;
                            }
                            case "C" -> {
                                if (players.get(i).getChipAmount() <= currentBet) {
                                    System.out.println("You are going All-In");
                                    players.get(i).setChipsInBetRound(players.get(i).getChipAmount());
                                    players.get(i).setChipsInRound(players.get(i).getChipsInBetRound());
                                    pot = pot + players.get(i).getChipAmount();
                                    players.get(i).setChipAmount(0);
                                    players.get(i).setAllIn(true);
                                } else {
                                    System.out.println("You have called $" + currentBet);
                                    players.get(i).setChipsInBetRound(currentBet);
                                    players.get(i).setChipsInRound(players.get(i).getChipsInBetRound());
                                    pot = pot + currentBet;
                                    players.get(i).setChipAmount(players.get(i).getChipAmount() - currentBet);
                                    System.out.println("You have $" + players.get(i).getChipAmount());
                                }
                                System.out.println();
                                validChar = true;
                            }
                            case "F" -> {
                                System.out.println("You have folded.");
                                System.out.println("You have $" + players.get(i).getChipAmount());
                                System.out.println();
                                players.get(i).setStillInRound(false);
                                validChar = true;
                            }
                            default -> System.out.println("Please enter a valid character.");
                        }
                    } while (!validChar);
                } else if (players.get(i).getPlayerID() == 0 && players.get(i).getPlayerID() == littleBlindPosition) {
                    players.get(i).setChipsInBetRound(0);
                    boolean validChar = false;
                    do {
                        System.out.println("You are little blind. $" + LB + " is the current little blind");
                        System.out.println("You have $" + players.get(i).getChipAmount());
                        System.out.println("The previous bet is $" + currentBet);
                        System.out.println("If you have less chips than the current bet or the blind, Calling will automatically put you All-In.");
                        System.out.print("Would you like to Bet, Call, or Fold (B/C/F): ");
                        userChoice = scan.nextLine().toUpperCase();
                        switch (userChoice) {
                            case "B" -> {
                                if (players.get(i).getChipAmount() <= currentBet) {
                                    break;
                                }
                                if (players.get(i).getChipAmount() < 2 * currentBet) {
                                    System.out.println("You are All-In!");
                                    players.get(i).setAllIn(true);
                                    currentBet = players.get(i).getChipAmount();
                                    pot = pot + players.get(i).getChipAmount();
                                    players.get(i).setChipAmount(0);
                                    players.get(i).setChipsInBetRound(currentBet);
                                    players.get(i).setChipsInRound(players.get(i).getChipsInBetRound());
                                } else {
                                    valid = false;
                                    while (!valid)
                                    {
                                        minBet = 2 * currentBet;
                                        System.out.println("You have $" + players.get(i).getChipAmount());
                                        System.out.println("The minimum you can bet is $" + minBet);
                                        try
                                        {
                                            System.out.print("How much would you like to bet: ");
                                            playerBet = scan.nextInt();
                                            scan.nextLine();
                                            if (playerBet < minBet || playerBet > players.get(i).getChipAmount())
                                            {
                                                throw new InvalidFormatException("Invalid input. Must be an integer between " + minBet + " and " + players.get(i).getChipAmount());
                                            }
                                            valid = true;
                                        } catch (InvalidFormatException e) {
                                            System.out.println(e.getMessage());
                                        }
                                    }
                                    players.get(i).setChipsInBetRound(playerBet);
                                    players.get(i).setChipsInRound(players.get(i).getChipsInBetRound());
                                    currentBet = playerBet;
                                    pot = pot + currentBet;
                                    players.get(i).setChipAmount(players.get(i).getChipAmount() - currentBet);
                                    if (players.get(i).getChipAmount() == 0) {
                                        System.out.println("You are All-In!");
                                        players.get(i).setAllIn(true);
                                    } else {
                                        System.out.println("You have $" + players.get(i).getChipAmount());
                                    }
                                    System.out.println("Current bet has been raised to $" + currentBet);
                                    System.out.println();
                                    validChar = true;
                                }
                            }
                            case "C" -> {
                                if (players.get(i).getChipAmount() <= currentBet) {
                                    System.out.println("You are going All-In");
                                    players.get(i).setChipsInBetRound(players.get(i).getChipAmount());
                                    players.get(i).setChipsInRound(players.get(i).getChipsInBetRound());
                                    pot = pot + players.get(i).getChipAmount();
                                    players.get(i).setChipAmount(0);
                                    players.get(i).setAllIn(true);
                                } else {
                                    System.out.println("You have called $" + currentBet);
                                    players.get(i).setChipsInBetRound(currentBet);
                                    players.get(i).setChipsInRound(players.get(i).getChipsInBetRound());
                                    pot = pot + currentBet;
                                    players.get(i).setChipAmount(players.get(i).getChipAmount() - currentBet);
                                    System.out.println("You have $" + players.get(i).getChipAmount());
                                }
                                System.out.println();
                                validChar = true;
                            }
                            case "F" -> {
                                if (players.get(i).getChipAmount() <= LB) {
                                    break;
                                }
                                System.out.println("You have folded.");
                                System.out.println("You are little blind. $" + LB + " has been taken to be added to the pot");
                                players.get(i).setChipAmount(players.get(i).getChipAmount() - LB);
                                pot = pot + LB;
                                System.out.println("You have $" + players.get(i).getChipAmount());
                                System.out.println();
                                players.get(i).setStillInRound(false);
                                validChar = true;
                            }

                            default -> System.out.println("Please enter a valid character.");
                        }
                    } while (!validChar);
                } else if (players.get(i).getPlayerID() == 0 && players.get(i).getPlayerID() == bigBlindPosition) {
                    players.get(i).setChipsInBetRound(0);
                    boolean validChar = false;
                    do {
                        System.out.println("You are big blind. $" + BB + " is the current big blind");
                        System.out.println("You have $" + players.get(i).getChipAmount());
                        System.out.println("The previous bet is $" + currentBet);
                        System.out.println("If you have less chips than the current bet or the blind, Calling will automatically put you All-In.");
                        System.out.print("Would you like to Bet, Call, or Fold (B/C/F): ");
                        userChoice = scan.nextLine().toUpperCase();
                        switch (userChoice) {
                            case "B" -> {
                                if (players.get(i).getChipAmount() <= currentBet) {
                                    break;
                                }
                                if (players.get(i).getChipAmount() < 2 * currentBet) {
                                    System.out.println("You are All-In!");
                                    players.get(i).setAllIn(true);
                                    currentBet = players.get(i).getChipAmount();
                                    pot = pot + players.get(i).getChipAmount();
                                    players.get(i).setChipAmount(0);
                                    players.get(i).setChipsInBetRound(currentBet);
                                    players.get(i).setChipsInRound(players.get(i).getChipsInBetRound());
                                } else {
                                    valid = false;
                                    while (!valid)
                                    {
                                        minBet = 2 * currentBet;
                                        System.out.println("You have $" + players.get(i).getChipAmount());
                                        System.out.println("The minimum you can bet is $" + minBet);
                                        try
                                        {
                                            System.out.print("How much would you like to bet: ");
                                            playerBet = scan.nextInt();
                                            scan.nextLine();
                                            if (playerBet < minBet || playerBet > players.get(i).getChipAmount())
                                            {
                                                throw new InvalidFormatException("Invalid input. Must be an integer between " + minBet + " and " + players.get(i).getChipAmount());
                                            }
                                            valid = true;
                                        } catch (InvalidFormatException e) {
                                            System.out.println(e.getMessage());
                                        }
                                    }
                                    players.get(i).setChipsInBetRound(playerBet);
                                    players.get(i).setChipsInRound(players.get(i).getChipsInBetRound());
                                    currentBet = playerBet;
                                    pot = pot + currentBet;
                                    players.get(i).setChipAmount(players.get(i).getChipAmount() - currentBet);
                                    if (players.get(i).getChipAmount() == 0) {
                                        System.out.println("You are All-In!");
                                        players.get(i).setAllIn(true);
                                    } else {
                                        System.out.println("You have $" + players.get(i).getChipAmount());
                                    }
                                    System.out.println("Current bet has been raised to $" + currentBet);
                                    System.out.println();
                                    validChar = true;
                                }
                            }
                            case "C" -> {
                                if (players.get(i).getChipAmount() <= currentBet) {
                                    System.out.println("You are going All-In");
                                    players.get(i).setChipsInBetRound(players.get(i).getChipAmount());
                                    players.get(i).setChipsInRound(players.get(i).getChipsInBetRound());
                                    pot = pot + players.get(i).getChipAmount();
                                    players.get(i).setChipAmount(0);
                                    players.get(i).setAllIn(true);
                                } else {
                                    System.out.println("You have called $" + currentBet);
                                    players.get(i).setChipsInBetRound(currentBet);
                                    players.get(i).setChipsInRound(players.get(i).getChipsInBetRound());
                                    pot = pot + currentBet;
                                    players.get(i).setChipAmount(players.get(i).getChipAmount() - currentBet);
                                    System.out.println("You have $" + players.get(i).getChipAmount());
                                }
                                System.out.println();
                                validChar = true;
                            }
                            case "F" -> {
                                if (players.get(i).getChipAmount() <= BB) {
                                    break;
                                }
                                System.out.println("You have folded.");
                                System.out.println("You are big blind. $" + BB + " has been taken to be added to the pot");
                                players.get(i).setChipAmount(players.get(i).getChipAmount() - BB);
                                pot = pot + BB;
                                System.out.println("You have $" + players.get(i).getChipAmount());
                                System.out.println();
                                players.get(i).setStillInRound(false);
                                validChar = true;
                            }
                            default -> System.out.println("Please enter a valid character.");
                        }
                    } while (!validChar);
                } else if (players.get(i).getPlayerID() == 0) {
                    players.get(i).setChipsInBetRound(0);
                    boolean validChar = false;
                    do {
                        System.out.println("You have $" + players.get(i).getChipAmount());
                        System.out.println("The previous bet is $" + currentBet);
                        System.out.println("If you have less chips than the current bet, Calling will automatically put you All-In.");
                        System.out.print("Would you like to Bet, Call, or Fold (B/C/F): ");
                        userChoice = scan.nextLine().toUpperCase();
                        switch (userChoice) {
                            case "B" -> {
                                if (players.get(i).getChipAmount() <= currentBet) {
                                    break;
                                }
                                if (players.get(i).getChipAmount() < 2 * currentBet) {
                                    System.out.println("You are All-In!");
                                    players.get(i).setAllIn(true);
                                    currentBet = players.get(i).getChipAmount();
                                    pot = pot + players.get(i).getChipAmount();
                                    players.get(i).setChipAmount(0);
                                    players.get(i).setChipsInBetRound(currentBet);
                                    players.get(i).setChipsInRound(players.get(i).getChipsInBetRound());
                                } else {
                                    valid = false;
                                    while (!valid)
                                    {
                                        minBet = 2 * currentBet;
                                        System.out.println("You have $" + players.get(i).getChipAmount());
                                        System.out.println("The minimum you can bet is $" + minBet);
                                        try
                                        {
                                            System.out.print("How much would you like to bet: ");
                                            playerBet = scan.nextInt();
                                            scan.nextLine();
                                            if (playerBet < minBet || playerBet > players.get(i).getChipAmount())
                                            {
                                                throw new InvalidFormatException("Invalid input. Must be an integer between " + minBet + " and " + players.get(i).getChipAmount());
                                            }
                                            valid = true;
                                        } catch (InvalidFormatException e) {
                                            System.out.println(e.getMessage());
                                        }
                                    }
                                    players.get(i).setChipsInBetRound(playerBet);
                                    players.get(i).setChipsInRound(players.get(i).getChipsInBetRound());
                                    currentBet = playerBet;
                                    pot = pot + currentBet;
                                    players.get(i).setChipAmount(players.get(i).getChipAmount() - currentBet);
                                    if (players.get(i).getChipAmount() == 0) {
                                        System.out.println("You are All-In!");
                                        players.get(i).setAllIn(true);
                                    } else {
                                        System.out.println("You have $" + players.get(i).getChipAmount());
                                    }
                                    System.out.println("Current bet has been raised to $" + currentBet);
                                    System.out.println();
                                    validChar = true;
                                }
                            }
                            case "C" -> {
                                if (players.get(i).getChipAmount() <= currentBet) {
                                    System.out.println("You are going All-In");
                                    players.get(i).setChipsInBetRound(players.get(i).getChipAmount());
                                    players.get(i).setChipsInRound(players.get(i).getChipsInBetRound());
                                    pot = pot + players.get(i).getChipAmount();
                                    players.get(i).setChipAmount(0);
                                    players.get(i).setAllIn(true);
                                } else {
                                    System.out.println("You have called $" + currentBet);
                                    players.get(i).setChipsInBetRound(currentBet);
                                    players.get(i).setChipsInRound(players.get(i).getChipsInBetRound());
                                    pot = pot + currentBet;
                                    players.get(i).setChipAmount(players.get(i).getChipAmount() - currentBet);
                                    System.out.println("You have $" + players.get(i).getChipAmount());
                                }
                                System.out.println();
                                validChar = true;
                            }
                            case "F" -> {
                                System.out.println("You have folded.");
                                System.out.println("You have $" + players.get(i).getChipAmount());
                                System.out.println();
                                players.get(i).setStillInRound(false);
                                validChar = true;
                            }
                            default -> System.out.println("Please enter a valid character.");
                        }
                    } while (!validChar);
                } else {
                    int[] Pot_CurrentBet = botPreFlopFirstBettingRandom(players, i, LB, BB, pot, currentBet, firstBetPosition, littleBlindPosition, bigBlindPosition);
                    pot = Pot_CurrentBet[0];
                    currentBet = Pot_CurrentBet[1];
                    pause();
                }
            }

            // Second part of loop
            for (int i = 0; i < firstBetPosition; i++) {
                if (players.get(i).getPlayerID() == 0 && players.get(i).getPlayerID() == littleBlindPosition) {
                    players.get(i).setChipsInBetRound(0);
                    boolean validChar = false;
                    do {
                        System.out.println("You are little blind. $" + LB + " is the current little blind");
                        System.out.println("You have $" + players.get(i).getChipAmount());
                        System.out.println("The previous bet is $" + currentBet);
                        System.out.println("If you have less chips than the current bet or the blind, Calling will automatically put you All-In.");
                        System.out.print("Would you like to Bet, Call, or Fold (B/C/F): ");
                        userChoice = scan.nextLine().toUpperCase();
                        switch (userChoice) {
                            case "B" -> {
                                if (players.get(i).getChipAmount() <= currentBet) {
                                    break;
                                }
                                if (players.get(i).getChipAmount() < 2 * currentBet) {
                                    System.out.println("You are All-In!");
                                    players.get(i).setAllIn(true);
                                    currentBet = players.get(i).getChipAmount();
                                    pot = pot + players.get(i).getChipAmount();
                                    players.get(i).setChipAmount(0);
                                    players.get(i).setChipsInBetRound(currentBet);
                                    players.get(i).setChipsInRound(players.get(i).getChipsInBetRound());
                                } else {
                                    valid = false;
                                    while (!valid)
                                    {
                                        minBet = 2 * currentBet;
                                        System.out.println("You have $" + players.get(i).getChipAmount());
                                        System.out.println("The minimum you can bet is $" + minBet);
                                        try
                                        {
                                            System.out.print("How much would you like to bet: ");
                                            playerBet = scan.nextInt();
                                            scan.nextLine();
                                            if (playerBet < minBet || playerBet > players.get(i).getChipAmount())
                                            {
                                                throw new InvalidFormatException("Invalid input. Must be an integer between " + minBet + " and " + players.get(i).getChipAmount());
                                            }
                                            valid = true;
                                        } catch (InvalidFormatException e) {
                                            System.out.println(e.getMessage());
                                        }
                                    }
                                    players.get(i).setChipsInBetRound(playerBet);
                                    players.get(i).setChipsInRound(players.get(i).getChipsInBetRound());
                                    currentBet = playerBet;
                                    pot = pot + currentBet;
                                    players.get(i).setChipAmount(players.get(i).getChipAmount() - currentBet);
                                    if (players.get(i).getChipAmount() == 0) {
                                        System.out.println("You are All-In!");
                                        players.get(i).setAllIn(true);
                                    } else {
                                        System.out.println("You have $" + players.get(i).getChipAmount());
                                    }
                                    System.out.println("Current bet has been raised to $" + currentBet);
                                    System.out.println();
                                    validChar = true;
                                }
                            }
                            case "C" -> {
                                if (players.get(i).getChipAmount() <= currentBet) {
                                    System.out.println("You are going All-In");
                                    players.get(i).setChipsInBetRound(players.get(i).getChipAmount());
                                    players.get(i).setChipsInRound(players.get(i).getChipsInBetRound());
                                    pot = pot + players.get(i).getChipAmount();
                                    players.get(i).setChipAmount(0);
                                    players.get(i).setAllIn(true);
                                } else {
                                    System.out.println("You have called $" + currentBet);
                                    players.get(i).setChipsInBetRound(currentBet);
                                    players.get(i).setChipsInRound(players.get(i).getChipsInBetRound());
                                    pot = pot + currentBet;
                                    players.get(i).setChipAmount(players.get(i).getChipAmount() - currentBet);
                                    System.out.println("You have $" + players.get(i).getChipAmount());
                                }
                                System.out.println();
                                validChar = true;
                            }
                            case "F" -> {
                                if (players.get(i).getChipAmount() <= LB) {
                                    break;
                                }
                                System.out.println("You have folded.");
                                System.out.println("You are little blind. $" + LB + " has been taken to be added to the pot");
                                players.get(i).setChipAmount(players.get(i).getChipAmount() - LB);
                                pot = pot + LB;
                                System.out.println("You have $" + players.get(i).getChipAmount());
                                System.out.println();
                                players.get(i).setStillInRound(false);
                                validChar = true;
                            }

                            default -> System.out.println("Please enter a valid character.");
                        }
                    } while (!validChar);
                } else if (players.get(i).getPlayerID() == 0 && players.get(i).getPlayerID() == bigBlindPosition) {
                    players.get(i).setChipsInBetRound(0);
                    boolean validChar = false;
                    do {
                        System.out.println("You are big blind. $" + BB + " is the current big blind");
                        System.out.println("You have $" + players.get(i).getChipAmount());
                        System.out.println("The previous bet is $" + currentBet);
                        System.out.println("If you have less chips than the current bet or the blind, Calling will automatically put you All-In.");
                        System.out.print("Would you like to Bet, Call, or Fold (B/C/F): ");
                        userChoice = scan.nextLine().toUpperCase();
                        switch (userChoice) {
                            case "B" -> {
                                if (players.get(i).getChipAmount() <= currentBet) {
                                    break;
                                }
                                if (players.get(i).getChipAmount() < 2 * currentBet) {
                                    System.out.println("You are All-In!");
                                    players.get(i).setAllIn(true);
                                    currentBet = players.get(i).getChipAmount();
                                    pot = pot + players.get(i).getChipAmount();
                                    players.get(i).setChipAmount(0);
                                    players.get(i).setChipsInBetRound(currentBet);
                                    players.get(i).setChipsInRound(players.get(i).getChipsInBetRound());
                                } else {
                                    valid = false;
                                    while (!valid)
                                    {
                                        minBet = 2 * currentBet;
                                        System.out.println("You have $" + players.get(i).getChipAmount());
                                        System.out.println("The minimum you can bet is $" + minBet);
                                        try
                                        {
                                            System.out.print("How much would you like to bet: ");
                                            playerBet = scan.nextInt();
                                            scan.nextLine();
                                            if (playerBet < minBet || playerBet > players.get(i).getChipAmount())
                                            {
                                                throw new InvalidFormatException("Invalid input. Must be an integer between " + minBet + " and " + players.get(i).getChipAmount());
                                            }
                                            valid = true;
                                        } catch (InvalidFormatException e) {
                                            System.out.println(e.getMessage());
                                        }
                                    }
                                    players.get(i).setChipsInBetRound(playerBet);
                                    players.get(i).setChipsInRound(players.get(i).getChipsInBetRound());
                                    currentBet = playerBet;
                                    pot = pot + currentBet;
                                    players.get(i).setChipAmount(players.get(i).getChipAmount() - currentBet);
                                    if (players.get(i).getChipAmount() == 0) {
                                        System.out.println("You are All-In!");
                                        players.get(i).setAllIn(true);
                                    } else {
                                        System.out.println("You have $" + players.get(i).getChipAmount());
                                    }
                                    System.out.println("Current bet has been raised to $" + currentBet);
                                    System.out.println();
                                    validChar = true;
                                }
                            }
                            case "C" -> {
                                if (players.get(i).getChipAmount() <= currentBet) {
                                    System.out.println("You are going All-In");
                                    players.get(i).setChipsInBetRound(players.get(i).getChipAmount());
                                    players.get(i).setChipsInRound(players.get(i).getChipsInBetRound());
                                    pot = pot + players.get(i).getChipAmount();
                                    players.get(i).setChipAmount(0);
                                    players.get(i).setAllIn(true);
                                } else {
                                    System.out.println("You have called $" + currentBet);
                                    players.get(i).setChipsInBetRound(currentBet);
                                    players.get(i).setChipsInRound(players.get(i).getChipsInBetRound());
                                    pot = pot + currentBet;
                                    players.get(i).setChipAmount(players.get(i).getChipAmount() - currentBet);
                                    System.out.println("You have $" + players.get(i).getChipAmount());
                                }
                                System.out.println();
                                validChar = true;
                            }
                            case "F" -> {
                                if (players.get(i).getChipAmount() <= BB) {
                                    break;
                                }
                                System.out.println("You have folded.");
                                System.out.println("You are big blind. $" + BB + " has been taken to be added to the pot");
                                players.get(i).setChipAmount(players.get(i).getChipAmount() - BB);
                                pot = pot + BB;
                                System.out.println("You have $" + players.get(i).getChipAmount());
                                System.out.println();
                                players.get(i).setStillInRound(false);
                                validChar = true;
                            }
                            default -> System.out.println("Please enter a valid character.");
                        }
                    } while (!validChar);
                } else if (players.get(i).getPlayerID() == 0) {
                    players.get(i).setChipsInBetRound(0);
                    boolean validChar = false;
                    do {
                        System.out.println("You have $" + players.get(i).getChipAmount());
                        System.out.println("The previous bet is $" + currentBet);
                        System.out.println("If you have less chips than the current bet, Calling will automatically put you All-In.");
                        System.out.print("Would you like to Bet, Call, or Fold (B/C/F): ");
                        userChoice = scan.nextLine().toUpperCase();
                        switch (userChoice) {
                            case "B" -> {
                                if (players.get(i).getChipAmount() <= currentBet) {
                                    break;
                                }
                                if (players.get(i).getChipAmount() < 2 * currentBet) {
                                    System.out.println("You are All-In!");
                                    players.get(i).setAllIn(true);
                                    currentBet = players.get(i).getChipAmount();
                                    pot = pot + players.get(i).getChipAmount();
                                    players.get(i).setChipAmount(0);
                                    players.get(i).setChipsInBetRound(currentBet);
                                    players.get(i).setChipsInRound(players.get(i).getChipsInBetRound());
                                } else {
                                    valid = false;
                                    while (!valid)
                                    {
                                        minBet = 2 * currentBet;
                                        System.out.println("You have $" + players.get(i).getChipAmount());
                                        System.out.println("The minimum you can bet is $" + minBet);
                                        try
                                        {
                                            System.out.print("How much would you like to bet: ");
                                            playerBet = scan.nextInt();
                                            scan.nextLine();
                                            if (playerBet < minBet || playerBet > players.get(i).getChipAmount())
                                            {
                                                throw new InvalidFormatException("Invalid input. Must be an integer between " + minBet + " and " + players.get(i).getChipAmount());
                                            }
                                            valid = true;
                                        } catch (InvalidFormatException e) {
                                            System.out.println(e.getMessage());
                                        }
                                    }
                                    players.get(i).setChipsInBetRound(playerBet);
                                    players.get(i).setChipsInRound(players.get(i).getChipsInBetRound());
                                    currentBet = playerBet;
                                    pot = pot + currentBet;
                                    players.get(i).setChipAmount(players.get(i).getChipAmount() - currentBet);
                                    if (players.get(i).getChipAmount() == 0) {
                                        System.out.println("You are All-In!");
                                        players.get(i).setAllIn(true);
                                    } else {
                                        System.out.println("You have $" + players.get(i).getChipAmount());
                                    }
                                    System.out.println("Current bet has been raised to $" + currentBet);
                                    System.out.println();
                                    validChar = true;
                                }
                            }
                            case "C" -> {
                                if (players.get(i).getChipAmount() <= currentBet) {
                                    System.out.println("You are going All-In");
                                    players.get(i).setChipsInBetRound(players.get(i).getChipAmount());
                                    players.get(i).setChipsInRound(players.get(i).getChipsInBetRound());
                                    pot = pot + players.get(i).getChipAmount();
                                    players.get(i).setChipAmount(0);
                                    players.get(i).setAllIn(true);
                                } else {
                                    System.out.println("You have called $" + currentBet);
                                    players.get(i).setChipsInBetRound(currentBet);
                                    players.get(i).setChipsInRound(players.get(i).getChipsInBetRound());
                                    pot = pot + currentBet;
                                    players.get(i).setChipAmount(players.get(i).getChipAmount() - currentBet);
                                    System.out.println("You have $" + players.get(i).getChipAmount());
                                }
                                System.out.println();
                                validChar = true;
                            }
                            case "F" -> {
                                System.out.println("You have folded.");
                                System.out.println("You have $" + players.get(i).getChipAmount());
                                System.out.println();
                                players.get(i).setStillInRound(false);
                                validChar = true;
                            }
                            default -> System.out.println("Please enter a valid character.");
                        }
                    } while (!validChar);
                } else {
                    int[] Pot_CurrentBet = botPreFlopFirstBettingRandom(players, i, LB, BB, pot, currentBet, firstBetPosition, littleBlindPosition, bigBlindPosition);
                    pot = Pot_CurrentBet[0];
                    currentBet = Pot_CurrentBet[1];
                    pause();
                }
            }

            // First loop of pre-flop betting finished
            // Making sure all players bet the same value

            while (!equalChipsInBetRound(players)) {
                for (int i = firstBetPosition; i < players.size(); i++) {
                    if (players.get(i).getChipsInBetRound() < currentBet && players.get(i).getPlayerID() == 0 && players.get(i).isStillInRound() && !players.get(i).isAllIn()) {
                        boolean validChar = false;
                        do {
                            System.out.println("You have $" + players.get(i).getChipAmount());
                            System.out.println("The previous bet is $" + currentBet);
                            System.out.println("You currently have $" + players.get(i).getChipsInBetRound() + " chips in the round");
                            System.out.println("If you don't have enough chips to match the current bet, Calling will automatically put you All-In.");
                            System.out.print("Would you like to Bet, Call, or Fold (B/C/F): ");
                            userChoice = scan.nextLine().toUpperCase();
                            switch (userChoice) {
                                case "B" -> {
                                    if (players.get(i).getChipAmount() + players.get(i).getChipsInBetRound() <= currentBet) {
                                        break;
                                    }
                                    if (players.get(i).getChipAmount() + players.get(i).getChipsInBetRound() < 2 * currentBet) {
                                        System.out.println("You are All-In!");
                                        players.get(i).setAllIn(true);
                                        currentBet = players.get(i).getChipAmount() + players.get(i).getChipsInBetRound();
                                        pot = pot + players.get(i).getChipAmount();
                                        players.get(i).setChipAmount(0);
                                        players.get(i).setChipsInBetRound(currentBet);
                                        players.get(i).setChipsInRound(players.get(i).getChipsInBetRound());
                                    } else {
                                        do {
                                            System.out.println("You have $" + players.get(i).getChipAmount());
                                            System.out.println("The minimum you can bet is $" + 2 * currentBet);
                                            System.out.print("How much would you like to bet: ");
                                            playerBet = scan.nextInt();
                                            scan.nextLine();
                                        } while (playerBet < 2 * currentBet && playerBet > players.get(i).getChipAmount() + players.get(i).getChipsInBetRound());
                                        currentBet = playerBet;
                                        players.get(i).setChipAmount(players.get(i).getChipAmount() - currentBet + players.get(i).getChipsInBetRound());
                                        pot = pot + currentBet - players.get(i).getChipsInBetRound();
                                        players.get(i).setChipsInBetRound(playerBet);
                                        players.get(i).setChipsInRound(players.get(i).getChipsInBetRound());
                                        if (players.get(i).getChipAmount() == 0) {
                                            System.out.println("You are All-In!");
                                            players.get(i).setAllIn(true);
                                        } else {
                                            System.out.println("You have $" + players.get(i).getChipAmount());
                                        }
                                    }
                                    System.out.println("Current bet has been raised to $" + currentBet);
                                    System.out.println();
                                    validChar = true;
                                }
                                case "C" -> {
                                    if (players.get(i).getChipAmount() + players.get(i).getChipsInBetRound() <= currentBet) {
                                        System.out.println("You are going All-In");
                                        players.get(i).setChipsInBetRound(players.get(i).getChipAmount() + players.get(i).getChipsInBetRound());
                                        players.get(i).setChipsInRound(players.get(i).getChipsInBetRound());
                                        pot = pot + players.get(i).getChipAmount();
                                        players.get(i).setChipAmount(0);
                                        players.get(i).setAllIn(true);
                                    } else {
                                        System.out.println("You have called $" + currentBet);
                                        players.get(i).setChipAmount(players.get(i).getChipAmount() - currentBet + players.get(i).getChipsInBetRound());
                                        pot = pot + currentBet - players.get(i).getChipsInBetRound();
                                        players.get(i).setChipsInBetRound(currentBet);
                                        players.get(i).setChipsInRound(players.get(i).getChipsInBetRound());
                                        System.out.println("You have $" + players.get(i).getChipAmount());
                                    }
                                    System.out.println();
                                    validChar = true;
                                }
                                case "F" -> {
                                    System.out.println("You have folded.");
                                    System.out.println("You have $" + players.get(i).getChipAmount());
                                    System.out.println();
                                    players.get(i).setStillInRound(false);
                                    validChar = true;
                                }
                                default -> System.out.println("Please enter a valid character.");
                            }
                        } while (!validChar);
                    } else if (players.get(i).getChipsInBetRound() < currentBet && players.get(i).isStillInRound() && !players.get(i).isAllIn()) {
                        int[] Pot_CurrentBet = botEqualBettingRandom(players, i, pot, currentBet);
                        pot = Pot_CurrentBet[0];
                        currentBet = Pot_CurrentBet[1];
                        pause();
                    }
                }
                for (int i = 0; i < firstBetPosition; i++) {
                    if (players.get(i).getChipsInBetRound() < currentBet && players.get(i).getPlayerID() == 0 && players.get(i).isStillInRound() && !players.get(i).isAllIn()) {
                        boolean validChar = false;
                        do {
                            System.out.println("You have $" + players.get(i).getChipAmount());
                            System.out.println("The previous bet is $" + currentBet);
                            System.out.println("You currently have $" + players.get(i).getChipsInBetRound() + " chips in the round");
                            System.out.println("If you don't have enough chips to match the current bet, Calling will automatically put you All-In.");
                            System.out.print("Would you like to Bet, Call, or Fold (B/C/F): ");
                            userChoice = scan.nextLine().toUpperCase();
                            switch (userChoice) {
                                case "B" -> {
                                    if (players.get(i).getChipAmount() + players.get(i).getChipsInBetRound() <= currentBet) {
                                        break;
                                    }
                                    if (players.get(i).getChipAmount() + players.get(i).getChipsInBetRound() < 2 * currentBet) {
                                        System.out.println("You are All-In!");
                                        players.get(i).setAllIn(true);
                                        currentBet = players.get(i).getChipAmount() + players.get(i).getChipsInBetRound();
                                        pot = pot + players.get(i).getChipAmount();
                                        players.get(i).setChipAmount(0);
                                        players.get(i).setChipsInBetRound(currentBet);
                                        players.get(i).setChipsInRound(players.get(i).getChipsInBetRound());
                                    } else {
                                        do {
                                            System.out.println("You have $" + players.get(i).getChipAmount());
                                            System.out.println("The minimum you can bet is $" + 2 * currentBet);
                                            System.out.print("How much would you like to bet: ");
                                            playerBet = scan.nextInt();
                                            scan.nextLine();
                                        } while (playerBet < 2 * currentBet && playerBet > players.get(i).getChipAmount() + players.get(i).getChipsInBetRound());
                                        currentBet = playerBet;
                                        players.get(i).setChipAmount(players.get(i).getChipAmount() - currentBet + players.get(i).getChipsInBetRound());
                                        pot = pot + currentBet - players.get(i).getChipsInBetRound();
                                        players.get(i).setChipsInBetRound(playerBet);
                                        players.get(i).setChipsInRound(players.get(i).getChipsInBetRound());
                                        if (players.get(i).getChipAmount() == 0) {
                                            System.out.println("You are All-In!");
                                            players.get(i).setAllIn(true);
                                        } else {
                                            System.out.println("You have $" + players.get(i).getChipAmount());
                                        }
                                    }
                                    System.out.println("Current bet has been raised to $" + currentBet);
                                    System.out.println();
                                    validChar = true;
                                }
                                case "C" -> {
                                    if (players.get(i).getChipAmount() + players.get(i).getChipsInBetRound() <= currentBet) {
                                        System.out.println("You are going All-In");
                                        players.get(i).setChipsInBetRound(players.get(i).getChipAmount() + players.get(i).getChipsInBetRound());
                                        players.get(i).setChipsInRound(players.get(i).getChipsInBetRound());
                                        pot = pot + players.get(i).getChipAmount();
                                        players.get(i).setChipAmount(0);
                                        players.get(i).setAllIn(true);
                                    } else {
                                        System.out.println("You have called $" + currentBet);
                                        players.get(i).setChipAmount(players.get(i).getChipAmount() - currentBet + players.get(i).getChipsInBetRound());
                                        pot = pot + currentBet - players.get(i).getChipsInBetRound();
                                        players.get(i).setChipsInBetRound(currentBet);
                                        players.get(i).setChipsInRound(players.get(i).getChipsInBetRound());
                                        System.out.println("You have $" + players.get(i).getChipAmount());
                                    }
                                    System.out.println();
                                    validChar = true;
                                }
                                case "F" -> {
                                    System.out.println("You have folded.");
                                    System.out.println("You have $" + players.get(i).getChipAmount());
                                    System.out.println();
                                    players.get(i).setStillInRound(false);
                                    validChar = true;
                                }
                                default -> System.out.println("Please enter a valid character.");
                            }
                        } while (!validChar);
                    } else if (players.get(i).getChipsInBetRound() < currentBet && players.get(i).isStillInRound() && !players.get(i).isAllIn()) {
                        int[] Pot_CurrentBet = botEqualBettingRandom(players, i, pot, currentBet);
                        pot = Pot_CurrentBet[0];
                        currentBet = Pot_CurrentBet[1];
                        pause();
                    }
                }
            }
            // Making side pots if a player is all-in
            // Counting number of players that are still in the round
            for (Player p : players) {
                if (p.isAllIn() && p.getAllInPot() == 0) {
                    int chipsNotInPot = 0;
                    for (Player p1 : players) {
                        if (p1.isStillInRound() && p1.getChipsInRound() > p.getChipsInRound()) {
                            chipsNotInPot = chipsNotInPot + p1.getChipsInRound() - p.getChipsInRound();
                        }
                    }
                    p.setAllInPot(pot - chipsNotInPot);
                }
            }
            System.out.println("<<Pre-Flop betting has ended>>");


            // Flop
            System.out.println("\nDrawing the flop... ");
            System.out.println("The Flop is: " + drawBoard(deck) + ", " + drawBoard(deck) + ", " + drawBoard(deck));
            System.out.println("Your hand is: " + players.get(0).getHand()[0] + " & " + players.get(0).getHand()[1]);
            pause();

            // Flop betting
            System.out.println("<<Flop Betting>>");
            currentBet = 0;
            for (int i = littleBlindPosition; i < players.size(); i++) {
                if (players.get(i).getPlayerID() == 0 && players.get(i).isStillInRound() && !players.get(i).isAllIn()) {
                    players.get(i).setChipsInBetRound(0);
                    boolean validChar = false;
                    do {
                        System.out.println("You have $" + players.get(i).getChipAmount());
                        System.out.println("The previous bet is $" + currentBet);
                        System.out.println("If you have less chips than the current bet, Calling will automatically put you All-In.");
                        System.out.print("Would you like to Bet, Call, or Fold (B/C/F): ");
                        userChoice = scan.nextLine().toUpperCase();
                        switch (userChoice) {
                            case "B" -> {
                                if (players.get(i).getChipAmount() <= currentBet) {
                                    break;
                                }
                                if (players.get(i).getChipAmount() < 2 * currentBet) {
                                    System.out.println("You are All-In!");
                                    players.get(i).setAllIn(true);
                                    currentBet = players.get(i).getChipAmount();
                                    pot = pot + players.get(i).getChipAmount();
                                    players.get(i).setChipAmount(0);
                                    players.get(i).setChipsInBetRound(currentBet);
                                    players.get(i).setChipsInRound(players.get(i).getChipsInRound() + players.get(i).getChipsInBetRound());
                                } else {
                                    do {
                                        System.out.println("You have $" + players.get(i).getChipAmount());
                                        System.out.println("The minimum you can bet is $" + 2 * currentBet);
                                        System.out.print("How much would you like to bet: ");
                                        playerBet = scan.nextInt();
                                        scan.nextLine();
                                    } while (playerBet < 2 * currentBet && playerBet > players.get(i).getChipAmount());
                                    players.get(i).setChipsInBetRound(playerBet);
                                    players.get(i).setChipsInRound(players.get(i).getChipsInRound() + players.get(i).getChipsInBetRound());
                                    currentBet = playerBet;
                                    pot = pot + currentBet;
                                    players.get(i).setChipAmount(players.get(i).getChipAmount() - currentBet);
                                    if (players.get(i).getChipAmount() == 0) {
                                        System.out.println("You are All-In!");
                                        players.get(i).setAllIn(true);
                                    } else {
                                        System.out.println("You have $" + players.get(i).getChipAmount());
                                    }
                                    System.out.println("Current bet has been raised to $" + currentBet);
                                    System.out.println();
                                    validChar = true;
                                }
                            }
                            case "C" -> {
                                if (players.get(i).getChipAmount() <= currentBet) {
                                    System.out.println("You are going All-In");
                                    players.get(i).setChipsInBetRound(players.get(i).getChipAmount());
                                    players.get(i).setChipsInRound(players.get(i).getChipsInRound() + players.get(i).getChipsInBetRound());
                                    pot = pot + players.get(i).getChipAmount();
                                    players.get(i).setChipAmount(0);
                                    players.get(i).setAllIn(true);
                                } else {
                                    System.out.println("You have called $" + currentBet);
                                    players.get(i).setChipsInBetRound(currentBet);
                                    players.get(i).setChipsInRound(players.get(i).getChipsInRound() + players.get(i).getChipsInBetRound());
                                    pot = pot + currentBet;
                                    players.get(i).setChipAmount(players.get(i).getChipAmount() - currentBet);
                                    System.out.println("You have $" + players.get(i).getChipAmount());
                                }
                                System.out.println();
                                validChar = true;
                            }
                            case "F" -> {
                                System.out.println("You have folded.");
                                System.out.println("You have $" + players.get(i).getChipAmount());
                                System.out.println();
                                players.get(i).setStillInRound(false);
                                validChar = true;
                            }
                            default -> System.out.println("Please enter a valid character.");
                        }
                    } while (!validChar);
                } else if (players.get(i).isStillInRound() && !players.get(i).isAllIn()) {
                    int[] Pot_CurrentBet = botPostFlopFirstBettingRandom(players, i, pot, currentBet, LB);
                    pot = Pot_CurrentBet[0];
                    currentBet = Pot_CurrentBet[1];
                    pause();
                }
            }
            // Second part of Flop betting
            for (int i = 0; i < littleBlindPosition; i++) {
                if (players.get(i).getPlayerID() == 0 && players.get(i).isStillInRound() && !players.get(i).isAllIn()) {
                    players.get(i).setChipsInBetRound(0);
                    boolean validChar = false;
                    do {
                        System.out.println("You have $" + players.get(i).getChipAmount());
                        System.out.println("The previous bet is $" + currentBet);
                        System.out.println("If you have less chips than the current bet, Calling will automatically put you All-In.");
                        System.out.print("Would you like to Bet, Call, or Fold (B/C/F): ");
                        userChoice = scan.nextLine().toUpperCase();
                        switch (userChoice) {
                            case "B" -> {
                                if (players.get(i).getChipAmount() <= currentBet) {
                                    break;
                                }
                                if (players.get(i).getChipAmount() < 2 * currentBet) {
                                    System.out.println("You are All-In!");
                                    players.get(i).setAllIn(true);
                                    currentBet = players.get(i).getChipAmount();
                                    pot = pot + players.get(i).getChipAmount();
                                    players.get(i).setChipAmount(0);
                                    players.get(i).setChipsInBetRound(currentBet);
                                    players.get(i).setChipsInRound(players.get(i).getChipsInRound() + players.get(i).getChipsInBetRound());
                                } else {
                                    do {
                                        System.out.println("You have $" + players.get(i).getChipAmount());
                                        System.out.println("The minimum you can bet is $" + 2 * currentBet);
                                        System.out.print("How much would you like to bet: ");
                                        playerBet = scan.nextInt();
                                        scan.nextLine();
                                    } while (playerBet < 2 * currentBet && playerBet > players.get(i).getChipAmount());
                                    players.get(i).setChipsInBetRound(playerBet);
                                    players.get(i).setChipsInRound(players.get(i).getChipsInRound() + players.get(i).getChipsInBetRound());
                                    currentBet = playerBet;
                                    pot = pot + currentBet;
                                    players.get(i).setChipAmount(players.get(i).getChipAmount() - currentBet);
                                    if (players.get(i).getChipAmount() == 0) {
                                        System.out.println("You are All-In!");
                                        players.get(i).setAllIn(true);
                                    } else {
                                        System.out.println("You have $" + players.get(i).getChipAmount());
                                    }
                                    System.out.println("Current bet has been raised to $" + currentBet);
                                    System.out.println();
                                    validChar = true;
                                }
                            }
                            case "C" -> {
                                if (players.get(i).getChipAmount() <= currentBet) {
                                    System.out.println("You are going All-In");
                                    players.get(i).setChipsInBetRound(players.get(i).getChipAmount());
                                    players.get(i).setChipsInRound(players.get(i).getChipsInRound() + players.get(i).getChipsInBetRound());
                                    pot = pot + players.get(i).getChipAmount();
                                    players.get(i).setChipAmount(0);
                                    players.get(i).setAllIn(true);
                                } else {
                                    System.out.println("You have called $" + currentBet);
                                    players.get(i).setChipsInBetRound(currentBet);
                                    players.get(i).setChipsInRound(players.get(i).getChipsInRound() + players.get(i).getChipsInBetRound());
                                    pot = pot + currentBet;
                                    players.get(i).setChipAmount(players.get(i).getChipAmount() - currentBet);
                                    System.out.println("You have $" + players.get(i).getChipAmount());
                                }
                                System.out.println();
                                validChar = true;
                            }
                            case "F" -> {
                                System.out.println("You have folded.");
                                System.out.println("You have $" + players.get(i).getChipAmount());
                                System.out.println();
                                players.get(i).setStillInRound(false);
                                validChar = true;
                            }
                            default -> System.out.println("Please enter a valid character.");
                        }
                    } while (!validChar);
                } else if (players.get(i).isStillInRound() && !players.get(i).isAllIn()) {
                    int[] Pot_CurrentBet = botPostFlopFirstBettingRandom(players, i, pot, currentBet, LB);
                    pot = Pot_CurrentBet[0];
                    currentBet = Pot_CurrentBet[1];
                    pause();
                }
            }

            // First loop of flop betting finished
            // Making sure all players bet the same value

            while (!equalChipsInBetRound(players)) {
                for (int i = littleBlindPosition; i < players.size(); i++) {
                    if (players.get(i).getChipsInBetRound() < currentBet && players.get(i).getPlayerID() == 0 && players.get(i).isStillInRound() && !players.get(i).isAllIn()) {
                        boolean validChar = false;
                        do {
                            System.out.println("You have $" + players.get(i).getChipAmount());
                            System.out.println("The previous bet is $" + currentBet);
                            System.out.println("You currently have $" + players.get(i).getChipsInBetRound() + " chips in the round");
                            System.out.println("If you don't have enough chips to match the current bet, Calling will automatically put you All-In.");
                            System.out.print("Would you like to Bet, Call, or Fold (B/C/F): ");
                            userChoice = scan.nextLine().toUpperCase();
                            switch (userChoice) {
                                case "B" -> {
                                    if (players.get(i).getChipAmount() + players.get(i).getChipsInBetRound() <= currentBet) {
                                        break;
                                    }
                                    if (players.get(i).getChipAmount() + players.get(i).getChipsInBetRound() < 2 * currentBet) {
                                        System.out.println("You are All-In!");
                                        players.get(i).setAllIn(true);
                                        currentBet = players.get(i).getChipAmount() + players.get(i).getChipsInBetRound();
                                        pot = pot + players.get(i).getChipAmount();
                                        players.get(i).setChipsInRound(players.get(i).getChipsInRound() + players.get(i).getChipAmount());
                                        players.get(i).setChipAmount(0);
                                        players.get(i).setChipsInBetRound(currentBet);
                                    } else {
                                        do {
                                            System.out.println("You have $" + players.get(i).getChipAmount());
                                            System.out.println("The minimum you can bet is $" + 2 * currentBet);
                                            System.out.print("How much would you like to bet: ");
                                            playerBet = scan.nextInt();
                                            scan.nextLine();
                                        } while (playerBet < 2 * currentBet && playerBet > players.get(i).getChipAmount() + players.get(i).getChipsInBetRound());
                                        currentBet = playerBet;
                                        players.get(i).setChipAmount(players.get(i).getChipAmount() - currentBet + players.get(i).getChipsInBetRound());
                                        pot = pot + currentBet - players.get(i).getChipsInBetRound();
                                        players.get(i).setChipsInRound(players.get(i).getChipsInRound() + currentBet - players.get(i).getChipsInBetRound());
                                        players.get(i).setChipsInBetRound(playerBet);
                                        if (players.get(i).getChipAmount() == 0) {
                                            System.out.println("You are All-In!");
                                            players.get(i).setAllIn(true);
                                        } else {
                                            System.out.println("You have $" + players.get(i).getChipAmount());
                                        }
                                    }
                                    System.out.println("Current bet has been raised to $" + currentBet);
                                    System.out.println();
                                    validChar = true;
                                }
                                case "C" -> {
                                    if (players.get(i).getChipAmount() + players.get(i).getChipsInBetRound() <= currentBet) {
                                        System.out.println("You are going All-In");
                                        players.get(i).setChipsInBetRound(players.get(i).getChipAmount() + players.get(i).getChipsInBetRound());
                                        players.get(i).setChipsInRound(players.get(i).getChipsInRound() + players.get(i).getChipAmount());
                                        pot = pot + players.get(i).getChipAmount();
                                        players.get(i).setChipAmount(0);
                                        players.get(i).setAllIn(true);
                                    } else {
                                        System.out.println("You have called $" + currentBet);
                                        players.get(i).setChipAmount(players.get(i).getChipAmount() - currentBet + players.get(i).getChipsInBetRound());
                                        pot = pot + currentBet - players.get(i).getChipsInBetRound();
                                        players.get(i).setChipsInRound(players.get(i).getChipsInRound() + currentBet - players.get(i).getChipsInBetRound());
                                        players.get(i).setChipsInBetRound(currentBet);
                                        System.out.println("You have $" + players.get(i).getChipAmount());
                                    }
                                    System.out.println();
                                    validChar = true;
                                }
                                case "F" -> {
                                    System.out.println("You have folded.");
                                    System.out.println("You have $" + players.get(i).getChipAmount());
                                    System.out.println();
                                    players.get(i).setStillInRound(false);
                                    validChar = true;
                                }
                                default -> System.out.println("Please enter a valid character.");
                            }
                        } while (!validChar);
                    } else if (players.get(i).getChipsInBetRound() < currentBet && players.get(i).isStillInRound() && !players.get(i).isAllIn()) {
                        int[] Pot_CurrentBet = botEqualBettingRandom(players, i, pot, currentBet);
                        pot = Pot_CurrentBet[0];
                        currentBet = Pot_CurrentBet[1];
                        pause();
                    }
                }
                for (int i = 0; i < littleBlindPosition; i++) {
                    if (players.get(i).getChipsInBetRound() < currentBet && players.get(i).getPlayerID() == 0 && players.get(i).isStillInRound() && !players.get(i).isAllIn()) {
                        boolean validChar = false;
                        do {
                            System.out.println("You have $" + players.get(i).getChipAmount());
                            System.out.println("The previous bet is $" + currentBet);
                            System.out.println("You currently have $" + players.get(i).getChipsInBetRound() + " chips in the round");
                            System.out.println("If you don't have enough chips to match the current bet, Calling will automatically put you All-In.");
                            System.out.print("Would you like to Bet, Call, or Fold (B/C/F): ");
                            userChoice = scan.nextLine().toUpperCase();
                            switch (userChoice) {
                                case "B" -> {
                                    if (players.get(i).getChipAmount() + players.get(i).getChipsInBetRound() <= currentBet) {
                                        break;
                                    }
                                    if (players.get(i).getChipAmount() + players.get(i).getChipsInBetRound() < 2 * currentBet) {
                                        System.out.println("You are All-In!");
                                        players.get(i).setAllIn(true);
                                        currentBet = players.get(i).getChipAmount() + players.get(i).getChipsInBetRound();
                                        pot = pot + players.get(i).getChipAmount();
                                        players.get(i).setChipsInRound(players.get(i).getChipsInRound() + players.get(i).getChipAmount());
                                        players.get(i).setChipAmount(0);
                                        players.get(i).setChipsInBetRound(currentBet);
                                    } else {
                                        do {
                                            System.out.println("You have $" + players.get(i).getChipAmount());
                                            System.out.println("The minimum you can bet is $" + 2 * currentBet);
                                            System.out.print("How much would you like to bet: ");
                                            playerBet = scan.nextInt();
                                            scan.nextLine();
                                        } while (playerBet < 2 * currentBet && playerBet > players.get(i).getChipAmount() + players.get(i).getChipsInBetRound());
                                        currentBet = playerBet;
                                        players.get(i).setChipAmount(players.get(i).getChipAmount() - currentBet + players.get(i).getChipsInBetRound());
                                        pot = pot + currentBet - players.get(i).getChipsInBetRound();
                                        players.get(i).setChipsInRound(players.get(i).getChipsInRound() + currentBet - players.get(i).getChipsInBetRound());
                                        players.get(i).setChipsInBetRound(playerBet);
                                        if (players.get(i).getChipAmount() == 0) {
                                            System.out.println("You are All-In!");
                                            players.get(i).setAllIn(true);
                                        } else {
                                            System.out.println("You have $" + players.get(i).getChipAmount());
                                        }
                                    }
                                    System.out.println("Current bet has been raised to $" + currentBet);
                                    System.out.println();
                                    validChar = true;
                                }
                                case "C" -> {
                                    if (players.get(i).getChipAmount() + players.get(i).getChipsInBetRound() <= currentBet) {
                                        System.out.println("You are going All-In");
                                        players.get(i).setChipsInBetRound(players.get(i).getChipAmount() + players.get(i).getChipsInBetRound());
                                        players.get(i).setChipsInRound(players.get(i).getChipsInRound() + players.get(i).getChipAmount());
                                        pot = pot + players.get(i).getChipAmount();
                                        players.get(i).setChipAmount(0);
                                        players.get(i).setAllIn(true);
                                    } else {
                                        System.out.println("You have called $" + currentBet);
                                        players.get(i).setChipAmount(players.get(i).getChipAmount() - currentBet + players.get(i).getChipsInBetRound());
                                        pot = pot + currentBet - players.get(i).getChipsInBetRound();
                                        players.get(i).setChipsInRound(players.get(i).getChipsInRound() + currentBet - players.get(i).getChipsInBetRound());
                                        players.get(i).setChipsInBetRound(currentBet);
                                        System.out.println("You have $" + players.get(i).getChipAmount());
                                    }
                                    System.out.println();
                                    validChar = true;
                                }
                                case "F" -> {
                                    System.out.println("You have folded.");
                                    System.out.println("You have $" + players.get(i).getChipAmount());
                                    System.out.println();
                                    players.get(i).setStillInRound(false);
                                    validChar = true;
                                }
                                default -> System.out.println("Please enter a valid character.");
                            }
                        } while (!validChar);
                    } else if (players.get(i).getChipsInBetRound() < currentBet && players.get(i).isStillInRound() && !players.get(i).isAllIn()) {
                        int[] Pot_CurrentBet = botEqualBettingRandom(players, i, pot, currentBet);
                        pot = Pot_CurrentBet[0];
                        currentBet = Pot_CurrentBet[1];
                        pause();
                    }
                }
            }
            for (Player p : players) {
                if (p.isAllIn() && p.getAllInPot() == 0) {
                    int chipsNotInPot = 0;
                    for (Player p1 : players) {
                        if (p1.isStillInRound() && p1.getChipsInRound() > p.getChipsInRound()) {
                            chipsNotInPot = chipsNotInPot + p1.getChipsInRound() - p.getChipsInRound();
                        }
                    }
                    p.setAllInPot(pot - chipsNotInPot);
                }
            }
            System.out.println("<<Flop betting has ended>>");


            // Turn
            System.out.println("\nDrawing the Turn... ");
            System.out.println("The Turn is " + drawBoard(deck));
            System.out.println("The Board is: " + deck.getBoard()[0] + ", " + deck.getBoard()[1] + ", " + deck.getBoard()[2] + ", " + deck.getBoard()[3]);
            System.out.println("Your hand is: " + players.get(0).getHand()[0] + " & " + players.get(0).getHand()[1]);
            pause();

            // Turn betting
            System.out.println("<<Turn Betting>>");
            currentBet = 0;
            for (int i = littleBlindPosition; i < players.size(); i++) {
                if (players.get(i).getPlayerID() == 0 && players.get(i).isStillInRound() && !players.get(i).isAllIn()) {
                    players.get(i).setChipsInBetRound(0);
                    boolean validChar = false;
                    do {
                        System.out.println("You have $" + players.get(i).getChipAmount());
                        System.out.println("The previous bet is $" + currentBet);
                        System.out.println("If you have less chips than the current bet, Calling will automatically put you All-In.");
                        System.out.print("Would you like to Bet, Call, or Fold (B/C/F): ");
                        userChoice = scan.nextLine().toUpperCase();
                        switch (userChoice) {
                            case "B" -> {
                                if (players.get(i).getChipAmount() <= currentBet) {
                                    break;
                                }
                                if (players.get(i).getChipAmount() < 2 * currentBet) {
                                    System.out.println("You are All-In!");
                                    players.get(i).setAllIn(true);
                                    currentBet = players.get(i).getChipAmount();
                                    pot = pot + players.get(i).getChipAmount();
                                    players.get(i).setChipAmount(0);
                                    players.get(i).setChipsInBetRound(currentBet);
                                    players.get(i).setChipsInRound(players.get(i).getChipsInRound() + players.get(i).getChipsInBetRound());
                                } else {
                                    do {
                                        System.out.println("You have $" + players.get(i).getChipAmount());
                                        System.out.println("The minimum you can bet is $" + 2 * currentBet);
                                        System.out.print("How much would you like to bet: ");
                                        playerBet = scan.nextInt();
                                        scan.nextLine();
                                    } while (playerBet < 2 * currentBet && playerBet > players.get(i).getChipAmount());
                                    players.get(i).setChipsInBetRound(playerBet);
                                    players.get(i).setChipsInRound(players.get(i).getChipsInRound() + players.get(i).getChipsInBetRound());
                                    currentBet = playerBet;
                                    pot = pot + currentBet;
                                    players.get(i).setChipAmount(players.get(i).getChipAmount() - currentBet);
                                    if (players.get(i).getChipAmount() == 0) {
                                        System.out.println("You are All-In!");
                                        players.get(i).setAllIn(true);
                                    } else {
                                        System.out.println("You have $" + players.get(i).getChipAmount());
                                    }
                                    System.out.println("Current bet has been raised to $" + currentBet);
                                    System.out.println();
                                    validChar = true;
                                }
                            }
                            case "C" -> {
                                if (players.get(i).getChipAmount() <= currentBet) {
                                    System.out.println("You are going All-In");
                                    players.get(i).setChipsInBetRound(players.get(i).getChipAmount());
                                    players.get(i).setChipsInRound(players.get(i).getChipsInRound() + players.get(i).getChipsInBetRound());
                                    pot = pot + players.get(i).getChipAmount();
                                    players.get(i).setChipAmount(0);
                                    players.get(i).setAllIn(true);
                                } else {
                                    System.out.println("You have called $" + currentBet);
                                    players.get(i).setChipsInBetRound(currentBet);
                                    players.get(i).setChipsInRound(players.get(i).getChipsInRound() + players.get(i).getChipsInBetRound());
                                    pot = pot + currentBet;
                                    players.get(i).setChipAmount(players.get(i).getChipAmount() - currentBet);
                                    System.out.println("You have $" + players.get(i).getChipAmount());
                                }
                                System.out.println();
                                validChar = true;
                            }
                            case "F" -> {
                                System.out.println("You have folded.");
                                System.out.println("You have $" + players.get(i).getChipAmount());
                                System.out.println();
                                players.get(i).setStillInRound(false);
                                validChar = true;
                            }
                            default -> System.out.println("Please enter a valid character.");
                        }
                    } while (!validChar);
                } else if (players.get(i).isStillInRound() && !players.get(i).isAllIn()) {
                    int[] Pot_CurrentBet = botPostFlopFirstBettingRandom(players, i, pot, currentBet, LB);
                    pot = Pot_CurrentBet[0];
                    currentBet = Pot_CurrentBet[1];
                    pause();
                }
            }
            // Second part of Flop betting
            for (int i = 0; i < littleBlindPosition; i++) {
                if (players.get(i).getPlayerID() == 0 && players.get(i).isStillInRound() && !players.get(i).isAllIn()) {
                    players.get(i).setChipsInBetRound(0);
                    boolean validChar = false;
                    do {
                        System.out.println("You have $" + players.get(i).getChipAmount());
                        System.out.println("The previous bet is $" + currentBet);
                        System.out.println("If you have less chips than the current bet, Calling will automatically put you All-In.");
                        System.out.print("Would you like to Bet, Call, or Fold (B/C/F): ");
                        userChoice = scan.nextLine().toUpperCase();
                        switch (userChoice) {
                            case "B" -> {
                                if (players.get(i).getChipAmount() <= currentBet) {
                                    break;
                                }
                                if (players.get(i).getChipAmount() < 2 * currentBet) {
                                    System.out.println("You are All-In!");
                                    players.get(i).setAllIn(true);
                                    currentBet = players.get(i).getChipAmount();
                                    pot = pot + players.get(i).getChipAmount();
                                    players.get(i).setChipAmount(0);
                                    players.get(i).setChipsInBetRound(currentBet);
                                    players.get(i).setChipsInRound(players.get(i).getChipsInRound() + players.get(i).getChipsInBetRound());
                                } else {
                                    do {
                                        System.out.println("You have $" + players.get(i).getChipAmount());
                                        System.out.println("The minimum you can bet is $" + 2 * currentBet);
                                        System.out.print("How much would you like to bet: ");
                                        playerBet = scan.nextInt();
                                        scan.nextLine();
                                    } while (playerBet < 2 * currentBet && playerBet > players.get(i).getChipAmount());
                                    players.get(i).setChipsInBetRound(playerBet);
                                    players.get(i).setChipsInRound(players.get(i).getChipsInRound() + players.get(i).getChipsInBetRound());
                                    currentBet = playerBet;
                                    pot = pot + currentBet;
                                    players.get(i).setChipAmount(players.get(i).getChipAmount() - currentBet);
                                    if (players.get(i).getChipAmount() == 0) {
                                        System.out.println("You are All-In!");
                                        players.get(i).setAllIn(true);
                                    } else {
                                        System.out.println("You have $" + players.get(i).getChipAmount());
                                    }
                                    System.out.println("Current bet has been raised to $" + currentBet);
                                    System.out.println();
                                    validChar = true;
                                }
                            }
                            case "C" -> {
                                if (players.get(i).getChipAmount() <= currentBet) {
                                    System.out.println("You are going All-In");
                                    players.get(i).setChipsInBetRound(players.get(i).getChipAmount());
                                    players.get(i).setChipsInRound(players.get(i).getChipsInRound() + players.get(i).getChipsInBetRound());
                                    pot = pot + players.get(i).getChipAmount();
                                    players.get(i).setChipAmount(0);
                                    players.get(i).setAllIn(true);
                                } else {
                                    System.out.println("You have called $" + currentBet);
                                    players.get(i).setChipsInBetRound(currentBet);
                                    players.get(i).setChipsInRound(players.get(i).getChipsInRound() + players.get(i).getChipsInBetRound());
                                    pot = pot + currentBet;
                                    players.get(i).setChipAmount(players.get(i).getChipAmount() - currentBet);
                                    System.out.println("You have $" + players.get(i).getChipAmount());
                                }
                                System.out.println();
                                validChar = true;
                            }
                            case "F" -> {
                                System.out.println("You have folded.");
                                System.out.println("You have $" + players.get(i).getChipAmount());
                                System.out.println();
                                players.get(i).setStillInRound(false);
                                validChar = true;
                            }
                            default -> System.out.println("Please enter a valid character.");
                        }
                    } while (!validChar);
                } else if (players.get(i).isStillInRound() && !players.get(i).isAllIn()) {
                    int[] Pot_CurrentBet = botPostFlopFirstBettingRandom(players, i, pot, currentBet, LB);
                    pot = Pot_CurrentBet[0];
                    currentBet = Pot_CurrentBet[1];
                    pause();
                }
            }

            // First loop of flop betting finished
            // Making sure all players bet the same value

            while (!equalChipsInBetRound(players)) {
                for (int i = littleBlindPosition; i < players.size(); i++) {
                    if (players.get(i).getChipsInBetRound() < currentBet && players.get(i).getPlayerID() == 0 && players.get(i).isStillInRound() && !players.get(i).isAllIn()) {
                        boolean validChar = false;
                        do {
                            System.out.println("You have $" + players.get(i).getChipAmount());
                            System.out.println("The previous bet is $" + currentBet);
                            System.out.println("You currently have $" + players.get(i).getChipsInBetRound() + " chips in the round");
                            System.out.println("If you don't have enough chips to match the current bet, Calling will automatically put you All-In.");
                            System.out.print("Would you like to Bet, Call, or Fold (B/C/F): ");
                            userChoice = scan.nextLine().toUpperCase();
                            switch (userChoice) {
                                case "B" -> {
                                    if (players.get(i).getChipAmount() + players.get(i).getChipsInBetRound() <= currentBet) {
                                        break;
                                    }
                                    if (players.get(i).getChipAmount() + players.get(i).getChipsInBetRound() < 2 * currentBet) {
                                        System.out.println("You are All-In!");
                                        players.get(i).setAllIn(true);
                                        currentBet = players.get(i).getChipAmount() + players.get(i).getChipsInBetRound();
                                        pot = pot + players.get(i).getChipAmount();
                                        players.get(i).setChipsInRound(players.get(i).getChipsInRound() + players.get(i).getChipAmount());
                                        players.get(i).setChipAmount(0);
                                        players.get(i).setChipsInBetRound(currentBet);
                                    } else {
                                        do {
                                            System.out.println("You have $" + players.get(i).getChipAmount());
                                            System.out.println("The minimum you can bet is $" + 2 * currentBet);
                                            System.out.print("How much would you like to bet: ");
                                            playerBet = scan.nextInt();
                                            scan.nextLine();
                                        } while (playerBet < 2 * currentBet && playerBet > players.get(i).getChipAmount() + players.get(i).getChipsInBetRound());
                                        currentBet = playerBet;
                                        players.get(i).setChipAmount(players.get(i).getChipAmount() - currentBet + players.get(i).getChipsInBetRound());
                                        pot = pot + currentBet - players.get(i).getChipsInBetRound();
                                        players.get(i).setChipsInRound(players.get(i).getChipsInRound() + currentBet - players.get(i).getChipsInBetRound());
                                        players.get(i).setChipsInBetRound(playerBet);
                                        if (players.get(i).getChipAmount() == 0) {
                                            System.out.println("You are All-In!");
                                            players.get(i).setAllIn(true);
                                        } else {
                                            System.out.println("You have $" + players.get(i).getChipAmount());
                                        }
                                    }
                                    System.out.println("Current bet has been raised to $" + currentBet);
                                    System.out.println();
                                    validChar = true;
                                }
                                case "C" -> {
                                    if (players.get(i).getChipAmount() + players.get(i).getChipsInBetRound() <= currentBet) {
                                        System.out.println("You are going All-In");
                                        players.get(i).setChipsInBetRound(players.get(i).getChipAmount() + players.get(i).getChipsInBetRound());
                                        players.get(i).setChipsInRound(players.get(i).getChipsInRound() + players.get(i).getChipAmount());
                                        pot = pot + players.get(i).getChipAmount();
                                        players.get(i).setChipAmount(0);
                                        players.get(i).setAllIn(true);
                                    } else {
                                        System.out.println("You have called $" + currentBet);
                                        players.get(i).setChipAmount(players.get(i).getChipAmount() - currentBet + players.get(i).getChipsInBetRound());
                                        pot = pot + currentBet - players.get(i).getChipsInBetRound();
                                        players.get(i).setChipsInRound(players.get(i).getChipsInRound() + currentBet - players.get(i).getChipsInBetRound());
                                        players.get(i).setChipsInBetRound(currentBet);
                                        System.out.println("You have $" + players.get(i).getChipAmount());
                                    }
                                    System.out.println();
                                    validChar = true;
                                }
                                case "F" -> {
                                    System.out.println("You have folded.");
                                    System.out.println("You have $" + players.get(i).getChipAmount());
                                    System.out.println();
                                    players.get(i).setStillInRound(false);
                                    validChar = true;
                                }
                                default -> System.out.println("Please enter a valid character.");
                            }
                        } while (!validChar);
                    } else if (players.get(i).getChipsInBetRound() < currentBet && players.get(i).isStillInRound() && !players.get(i).isAllIn()) {
                        int[] Pot_CurrentBet = botEqualBettingRandom(players, i, pot, currentBet);
                        pot = Pot_CurrentBet[0];
                        currentBet = Pot_CurrentBet[1];
                        pause();
                    }
                }
                for (int i = 0; i < littleBlindPosition; i++) {
                    if (players.get(i).getChipsInBetRound() < currentBet && players.get(i).getPlayerID() == 0 && players.get(i).isStillInRound() && !players.get(i).isAllIn()) {
                        boolean validChar = false;
                        do {
                            System.out.println("You have $" + players.get(i).getChipAmount());
                            System.out.println("The previous bet is $" + currentBet);
                            System.out.println("You currently have $" + players.get(i).getChipsInBetRound() + " chips in the round");
                            System.out.println("If you don't have enough chips to match the current bet, Calling will automatically put you All-In.");
                            System.out.print("Would you like to Bet, Call, or Fold (B/C/F): ");
                            userChoice = scan.nextLine().toUpperCase();
                            switch (userChoice) {
                                case "B" -> {
                                    if (players.get(i).getChipAmount() + players.get(i).getChipsInBetRound() <= currentBet) {
                                        break;
                                    }
                                    if (players.get(i).getChipAmount() + players.get(i).getChipsInBetRound() < 2 * currentBet) {
                                        System.out.println("You are All-In!");
                                        players.get(i).setAllIn(true);
                                        currentBet = players.get(i).getChipAmount() + players.get(i).getChipsInBetRound();
                                        pot = pot + players.get(i).getChipAmount();
                                        players.get(i).setChipsInRound(players.get(i).getChipsInRound() + players.get(i).getChipAmount());
                                        players.get(i).setChipAmount(0);
                                        players.get(i).setChipsInBetRound(currentBet);
                                    } else {
                                        do {
                                            System.out.println("You have $" + players.get(i).getChipAmount());
                                            System.out.println("The minimum you can bet is $" + 2 * currentBet);
                                            System.out.print("How much would you like to bet: ");
                                            playerBet = scan.nextInt();
                                            scan.nextLine();
                                        } while (playerBet < 2 * currentBet && playerBet > players.get(i).getChipAmount() + players.get(i).getChipsInBetRound());
                                        currentBet = playerBet;
                                        players.get(i).setChipAmount(players.get(i).getChipAmount() - currentBet + players.get(i).getChipsInBetRound());
                                        pot = pot + currentBet - players.get(i).getChipsInBetRound();
                                        players.get(i).setChipsInRound(players.get(i).getChipsInRound() + currentBet - players.get(i).getChipsInBetRound());
                                        players.get(i).setChipsInBetRound(playerBet);
                                        if (players.get(i).getChipAmount() == 0) {
                                            System.out.println("You are All-In!");
                                            players.get(i).setAllIn(true);
                                        } else {
                                            System.out.println("You have $" + players.get(i).getChipAmount());
                                        }
                                    }
                                    System.out.println("Current bet has been raised to $" + currentBet);
                                    System.out.println();
                                    validChar = true;
                                }
                                case "C" -> {
                                    if (players.get(i).getChipAmount() + players.get(i).getChipsInBetRound() <= currentBet) {
                                        System.out.println("You are going All-In");
                                        players.get(i).setChipsInBetRound(players.get(i).getChipAmount() + players.get(i).getChipsInBetRound());
                                        players.get(i).setChipsInRound(players.get(i).getChipsInRound() + players.get(i).getChipAmount());
                                        pot = pot + players.get(i).getChipAmount();
                                        players.get(i).setChipAmount(0);
                                        players.get(i).setAllIn(true);
                                    } else {
                                        System.out.println("You have called $" + currentBet);
                                        players.get(i).setChipAmount(players.get(i).getChipAmount() - currentBet + players.get(i).getChipsInBetRound());
                                        pot = pot + currentBet - players.get(i).getChipsInBetRound();
                                        players.get(i).setChipsInRound(players.get(i).getChipsInRound() + currentBet - players.get(i).getChipsInBetRound());
                                        players.get(i).setChipsInBetRound(currentBet);
                                        System.out.println("You have $" + players.get(i).getChipAmount());
                                    }
                                    System.out.println();
                                    validChar = true;
                                }
                                case "F" -> {
                                    System.out.println("You have folded.");
                                    System.out.println("You have $" + players.get(i).getChipAmount());
                                    System.out.println();
                                    players.get(i).setStillInRound(false);
                                    validChar = true;
                                }
                                default -> System.out.println("Please enter a valid character.");
                            }
                        } while (!validChar);
                    } else if (players.get(i).getChipsInBetRound() < currentBet && players.get(i).isStillInRound() && !players.get(i).isAllIn()) {
                        int[] Pot_CurrentBet = botEqualBettingRandom(players, i, pot, currentBet);
                        pot = Pot_CurrentBet[0];
                        currentBet = Pot_CurrentBet[1];
                        pause();
                    }
                }
            }
            for (Player p : players) {
                if (p.isAllIn() && p.getAllInPot() == 0) {
                    int chipsNotInPot = 0;
                    for (Player p1 : players) {
                        if (p1.isStillInRound() && p1.getChipsInRound() > p.getChipsInRound()) {
                            chipsNotInPot = chipsNotInPot + p1.getChipsInRound() - p.getChipsInRound();
                        }
                    }
                    p.setAllInPot(pot - chipsNotInPot);
                }
            }
            System.out.println("<<Turn betting has ended>>");


            // River
            System.out.println("\nDrawing the River... ");
            System.out.println("The River is " + drawBoard(deck));
            System.out.println("The Board is: " + deck.getBoard()[0] + ", " + deck.getBoard()[1] + ", "
                    + deck.getBoard()[2] + ", " + deck.getBoard()[3] + ", " + deck.getBoard()[4]);
            System.out.println("Your hand is: " + players.get(0).getHand()[0] + " & " + players.get(0).getHand()[1]);
            pause();

            // River betting
            System.out.println("<<River Betting>>");
            currentBet = 0;
            for (int i = littleBlindPosition; i < players.size(); i++) {
                if (players.get(i).getPlayerID() == 0 && players.get(i).isStillInRound() && !players.get(i).isAllIn()) {
                    players.get(i).setChipsInBetRound(0);
                    boolean validChar = false;
                    do {
                        System.out.println("You have $" + players.get(i).getChipAmount());
                        System.out.println("The previous bet is $" + currentBet);
                        System.out.println("If you have less chips than the current bet, Calling will automatically put you All-In.");
                        System.out.print("Would you like to Bet, Call, or Fold (B/C/F): ");
                        userChoice = scan.nextLine().toUpperCase();
                        switch (userChoice) {
                            case "B" -> {
                                if (players.get(i).getChipAmount() <= currentBet) {
                                    break;
                                }
                                if (players.get(i).getChipAmount() < 2 * currentBet) {
                                    System.out.println("You are All-In!");
                                    players.get(i).setAllIn(true);
                                    currentBet = players.get(i).getChipAmount();
                                    pot = pot + players.get(i).getChipAmount();
                                    players.get(i).setChipAmount(0);
                                    players.get(i).setChipsInBetRound(currentBet);
                                    players.get(i).setChipsInRound(players.get(i).getChipsInRound() + players.get(i).getChipsInBetRound());
                                } else {
                                    do {
                                        System.out.println("You have $" + players.get(i).getChipAmount());
                                        System.out.println("The minimum you can bet is $" + 2 * currentBet);
                                        System.out.print("How much would you like to bet: ");
                                        playerBet = scan.nextInt();
                                        scan.nextLine();
                                    } while (playerBet < 2 * currentBet && playerBet > players.get(i).getChipAmount());
                                    players.get(i).setChipsInBetRound(playerBet);
                                    players.get(i).setChipsInRound(players.get(i).getChipsInRound() + players.get(i).getChipsInBetRound());
                                    currentBet = playerBet;
                                    pot = pot + currentBet;
                                    players.get(i).setChipAmount(players.get(i).getChipAmount() - currentBet);
                                    if (players.get(i).getChipAmount() == 0) {
                                        System.out.println("You are All-In!");
                                        players.get(i).setAllIn(true);
                                    } else {
                                        System.out.println("You have $" + players.get(i).getChipAmount());
                                    }
                                    System.out.println("Current bet has been raised to $" + currentBet);
                                    System.out.println();
                                    validChar = true;
                                }
                            }
                            case "C" -> {
                                if (players.get(i).getChipAmount() <= currentBet) {
                                    System.out.println("You are going All-In");
                                    players.get(i).setChipsInBetRound(players.get(i).getChipAmount());
                                    players.get(i).setChipsInRound(players.get(i).getChipsInRound() + players.get(i).getChipsInBetRound());
                                    pot = pot + players.get(i).getChipAmount();
                                    players.get(i).setChipAmount(0);
                                    players.get(i).setAllIn(true);
                                } else {
                                    System.out.println("You have called $" + currentBet);
                                    players.get(i).setChipsInBetRound(currentBet);
                                    players.get(i).setChipsInRound(players.get(i).getChipsInRound() + players.get(i).getChipsInBetRound());
                                    pot = pot + currentBet;
                                    players.get(i).setChipAmount(players.get(i).getChipAmount() - currentBet);
                                    System.out.println("You have $" + players.get(i).getChipAmount());
                                }
                                System.out.println();
                                validChar = true;
                            }
                            case "F" -> {
                                System.out.println("You have folded.");
                                System.out.println("You have $" + players.get(i).getChipAmount());
                                System.out.println();
                                players.get(i).setStillInRound(false);
                                validChar = true;
                            }
                            default -> System.out.println("Please enter a valid character.");
                        }
                    } while (!validChar);
                } else if (players.get(i).isStillInRound() && !players.get(i).isAllIn()) {
                    int[] Pot_CurrentBet = botPostFlopFirstBettingRandom(players, i, pot, currentBet, LB);
                    pot = Pot_CurrentBet[0];
                    currentBet = Pot_CurrentBet[1];
                    pause();
                }
            }
            // Second part of Flop betting
            for (int i = 0; i < littleBlindPosition; i++) {
                if (players.get(i).getPlayerID() == 0 && players.get(i).isStillInRound() && !players.get(i).isAllIn()) {
                    players.get(i).setChipsInBetRound(0);
                    boolean validChar = false;
                    do {
                        System.out.println("You have $" + players.get(i).getChipAmount());
                        System.out.println("The previous bet is $" + currentBet);
                        System.out.println("If you have less chips than the current bet, Calling will automatically put you All-In.");
                        System.out.print("Would you like to Bet, Call, or Fold (B/C/F): ");
                        userChoice = scan.nextLine().toUpperCase();
                        switch (userChoice) {
                            case "B" -> {
                                if (players.get(i).getChipAmount() <= currentBet) {
                                    break;
                                }
                                if (players.get(i).getChipAmount() < 2 * currentBet) {
                                    System.out.println("You are All-In!");
                                    players.get(i).setAllIn(true);
                                    currentBet = players.get(i).getChipAmount();
                                    pot = pot + players.get(i).getChipAmount();
                                    players.get(i).setChipAmount(0);
                                    players.get(i).setChipsInBetRound(currentBet);
                                    players.get(i).setChipsInRound(players.get(i).getChipsInRound() + players.get(i).getChipsInBetRound());
                                } else {
                                    do {
                                        System.out.println("You have $" + players.get(i).getChipAmount());
                                        System.out.println("The minimum you can bet is $" + 2 * currentBet);
                                        System.out.print("How much would you like to bet: ");
                                        playerBet = scan.nextInt();
                                        scan.nextLine();
                                    } while (playerBet < 2 * currentBet && playerBet > players.get(i).getChipAmount());
                                    players.get(i).setChipsInBetRound(playerBet);
                                    players.get(i).setChipsInRound(players.get(i).getChipsInRound() + players.get(i).getChipsInBetRound());
                                    currentBet = playerBet;
                                    pot = pot + currentBet;
                                    players.get(i).setChipAmount(players.get(i).getChipAmount() - currentBet);
                                    if (players.get(i).getChipAmount() == 0) {
                                        System.out.println("You are All-In!");
                                        players.get(i).setAllIn(true);
                                    } else {
                                        System.out.println("You have $" + players.get(i).getChipAmount());
                                    }
                                    System.out.println("Current bet has been raised to $" + currentBet);
                                    System.out.println();
                                    validChar = true;
                                }
                            }
                            case "C" -> {
                                if (players.get(i).getChipAmount() <= currentBet) {
                                    System.out.println("You are going All-In");
                                    players.get(i).setChipsInBetRound(players.get(i).getChipAmount());
                                    players.get(i).setChipsInRound(players.get(i).getChipsInRound() + players.get(i).getChipsInBetRound());
                                    pot = pot + players.get(i).getChipAmount();
                                    players.get(i).setChipAmount(0);
                                    players.get(i).setAllIn(true);
                                } else {
                                    System.out.println("You have called $" + currentBet);
                                    players.get(i).setChipsInBetRound(currentBet);
                                    players.get(i).setChipsInRound(players.get(i).getChipsInRound() + players.get(i).getChipsInBetRound());
                                    pot = pot + currentBet;
                                    players.get(i).setChipAmount(players.get(i).getChipAmount() - currentBet);
                                    System.out.println("You have $" + players.get(i).getChipAmount());
                                }
                                System.out.println();
                                validChar = true;
                            }
                            case "F" -> {
                                System.out.println("You have folded.");
                                System.out.println("You have $" + players.get(i).getChipAmount());
                                System.out.println();
                                players.get(i).setStillInRound(false);
                                validChar = true;
                            }
                            default -> System.out.println("Please enter a valid character.");
                        }
                    } while (!validChar);
                } else if (players.get(i).isStillInRound() && !players.get(i).isAllIn()) {
                    int[] Pot_CurrentBet = botPostFlopFirstBettingRandom(players, i, pot, currentBet, LB);
                    pot = Pot_CurrentBet[0];
                    currentBet = Pot_CurrentBet[1];
                    pause();
                }
            }

            // First loop of flop betting finished
            // Making sure all players bet the same value

            while (!equalChipsInBetRound(players)) {
                for (int i = littleBlindPosition; i < players.size(); i++) {
                    if (players.get(i).getChipsInBetRound() < currentBet && players.get(i).getPlayerID() == 0 && players.get(i).isStillInRound() && !players.get(i).isAllIn()) {
                        boolean validChar = false;
                        do {
                            System.out.println("You have $" + players.get(i).getChipAmount());
                            System.out.println("The previous bet is $" + currentBet);
                            System.out.println("You currently have $" + players.get(i).getChipsInBetRound() + " chips in the round");
                            System.out.println("If you don't have enough chips to match the current bet, Calling will automatically put you All-In.");
                            System.out.print("Would you like to Bet, Call, or Fold (B/C/F): ");
                            userChoice = scan.nextLine().toUpperCase();
                            switch (userChoice) {
                                case "B" -> {
                                    if (players.get(i).getChipAmount() + players.get(i).getChipsInBetRound() <= currentBet) {
                                        break;
                                    }
                                    if (players.get(i).getChipAmount() + players.get(i).getChipsInBetRound() < 2 * currentBet) {
                                        System.out.println("You are All-In!");
                                        players.get(i).setAllIn(true);
                                        currentBet = players.get(i).getChipAmount() + players.get(i).getChipsInBetRound();
                                        pot = pot + players.get(i).getChipAmount();
                                        players.get(i).setChipsInRound(players.get(i).getChipsInRound() + players.get(i).getChipAmount());
                                        players.get(i).setChipAmount(0);
                                        players.get(i).setChipsInBetRound(currentBet);
                                    } else {
                                        do {
                                            System.out.println("You have $" + players.get(i).getChipAmount());
                                            System.out.println("The minimum you can bet is $" + 2 * currentBet);
                                            System.out.print("How much would you like to bet: ");
                                            playerBet = scan.nextInt();
                                            scan.nextLine();
                                        } while (playerBet < 2 * currentBet && playerBet > players.get(i).getChipAmount() + players.get(i).getChipsInBetRound());
                                        currentBet = playerBet;
                                        players.get(i).setChipAmount(players.get(i).getChipAmount() - currentBet + players.get(i).getChipsInBetRound());
                                        pot = pot + currentBet - players.get(i).getChipsInBetRound();
                                        players.get(i).setChipsInRound(players.get(i).getChipsInRound() + currentBet - players.get(i).getChipsInBetRound());
                                        players.get(i).setChipsInBetRound(playerBet);
                                        if (players.get(i).getChipAmount() == 0) {
                                            System.out.println("You are All-In!");
                                            players.get(i).setAllIn(true);
                                        } else {
                                            System.out.println("You have $" + players.get(i).getChipAmount());
                                        }
                                    }
                                    System.out.println("Current bet has been raised to $" + currentBet);
                                    System.out.println();
                                    validChar = true;
                                }
                                case "C" -> {
                                    if (players.get(i).getChipAmount() + players.get(i).getChipsInBetRound() <= currentBet) {
                                        System.out.println("You are going All-In");
                                        players.get(i).setChipsInBetRound(players.get(i).getChipAmount() + players.get(i).getChipsInBetRound());
                                        players.get(i).setChipsInRound(players.get(i).getChipsInRound() + players.get(i).getChipAmount());
                                        pot = pot + players.get(i).getChipAmount();
                                        players.get(i).setChipAmount(0);
                                        players.get(i).setAllIn(true);
                                    } else {
                                        System.out.println("You have called $" + currentBet);
                                        players.get(i).setChipAmount(players.get(i).getChipAmount() - currentBet + players.get(i).getChipsInBetRound());
                                        pot = pot + currentBet - players.get(i).getChipsInBetRound();
                                        players.get(i).setChipsInRound(players.get(i).getChipsInRound() + currentBet - players.get(i).getChipsInBetRound());
                                        players.get(i).setChipsInBetRound(currentBet);
                                        System.out.println("You have $" + players.get(i).getChipAmount());
                                    }
                                    System.out.println();
                                    validChar = true;
                                }
                                case "F" -> {
                                    System.out.println("You have folded.");
                                    System.out.println("You have $" + players.get(i).getChipAmount());
                                    System.out.println();
                                    players.get(i).setStillInRound(false);
                                    validChar = true;
                                }
                                default -> System.out.println("Please enter a valid character.");
                            }
                        } while (!validChar);
                    } else if (players.get(i).getChipsInBetRound() < currentBet && players.get(i).isStillInRound() && !players.get(i).isAllIn()) {
                        int[] Pot_CurrentBet = botEqualBettingRandom(players, i, pot, currentBet);
                        pot = Pot_CurrentBet[0];
                        currentBet = Pot_CurrentBet[1];
                        pause();
                    }
                }
                for (int i = 0; i < littleBlindPosition; i++) {
                    if (players.get(i).getChipsInBetRound() < currentBet && players.get(i).getPlayerID() == 0 && players.get(i).isStillInRound() && !players.get(i).isAllIn()) {
                        boolean validChar = false;
                        do {
                            System.out.println("You have $" + players.get(i).getChipAmount());
                            System.out.println("The previous bet is $" + currentBet);
                            System.out.println("You currently have $" + players.get(i).getChipsInBetRound() + " chips in the round");
                            System.out.println("If you don't have enough chips to match the current bet, Calling will automatically put you All-In.");
                            System.out.print("Would you like to Bet, Call, or Fold (B/C/F): ");
                            userChoice = scan.nextLine().toUpperCase();
                            switch (userChoice) {
                                case "B" -> {
                                    if (players.get(i).getChipAmount() + players.get(i).getChipsInBetRound() <= currentBet) {
                                        break;
                                    }
                                    if (players.get(i).getChipAmount() + players.get(i).getChipsInBetRound() < 2 * currentBet) {
                                        System.out.println("You are All-In!");
                                        players.get(i).setAllIn(true);
                                        currentBet = players.get(i).getChipAmount() + players.get(i).getChipsInBetRound();
                                        pot = pot + players.get(i).getChipAmount();
                                        players.get(i).setChipsInRound(players.get(i).getChipsInRound() + players.get(i).getChipAmount());
                                        players.get(i).setChipAmount(0);
                                        players.get(i).setChipsInBetRound(currentBet);
                                    } else {
                                        do {
                                            System.out.println("You have $" + players.get(i).getChipAmount());
                                            System.out.println("The minimum you can bet is $" + 2 * currentBet);
                                            System.out.print("How much would you like to bet: ");
                                            playerBet = scan.nextInt();
                                            scan.nextLine();
                                        } while (playerBet < 2 * currentBet && playerBet > players.get(i).getChipAmount() + players.get(i).getChipsInBetRound());
                                        currentBet = playerBet;
                                        players.get(i).setChipAmount(players.get(i).getChipAmount() - currentBet + players.get(i).getChipsInBetRound());
                                        pot = pot + currentBet - players.get(i).getChipsInBetRound();
                                        players.get(i).setChipsInRound(players.get(i).getChipsInRound() + currentBet - players.get(i).getChipsInBetRound());
                                        players.get(i).setChipsInBetRound(playerBet);
                                        if (players.get(i).getChipAmount() == 0) {
                                            System.out.println("You are All-In!");
                                            players.get(i).setAllIn(true);
                                        } else {
                                            System.out.println("You have $" + players.get(i).getChipAmount());
                                        }
                                    }
                                    System.out.println("Current bet has been raised to $" + currentBet);
                                    System.out.println();
                                    validChar = true;
                                }
                                case "C" -> {
                                    if (players.get(i).getChipAmount() + players.get(i).getChipsInBetRound() <= currentBet) {
                                        System.out.println("You are going All-In");
                                        players.get(i).setChipsInBetRound(players.get(i).getChipAmount() + players.get(i).getChipsInBetRound());
                                        players.get(i).setChipsInRound(players.get(i).getChipsInRound() + players.get(i).getChipAmount());
                                        pot = pot + players.get(i).getChipAmount();
                                        players.get(i).setChipAmount(0);
                                        players.get(i).setAllIn(true);
                                    } else {
                                        System.out.println("You have called $" + currentBet);
                                        players.get(i).setChipAmount(players.get(i).getChipAmount() - currentBet + players.get(i).getChipsInBetRound());
                                        pot = pot + currentBet - players.get(i).getChipsInBetRound();
                                        players.get(i).setChipsInRound(players.get(i).getChipsInRound() + currentBet - players.get(i).getChipsInBetRound());
                                        players.get(i).setChipsInBetRound(currentBet);
                                        System.out.println("You have $" + players.get(i).getChipAmount());
                                    }
                                    System.out.println();
                                    validChar = true;
                                }
                                case "F" -> {
                                    System.out.println("You have folded.");
                                    System.out.println("You have $" + players.get(i).getChipAmount());
                                    System.out.println();
                                    players.get(i).setStillInRound(false);
                                    validChar = true;
                                }
                                default -> System.out.println("Please enter a valid character.");
                            }
                        } while (!validChar);
                    } else if (players.get(i).getChipsInBetRound() < currentBet && players.get(i).isStillInRound() && !players.get(i).isAllIn()) {
                        int[] Pot_CurrentBet = botEqualBettingRandom(players, i, pot, currentBet);
                        pot = Pot_CurrentBet[0];
                        currentBet = Pot_CurrentBet[1];
                        pause();
                    }
                }
            }
            for (Player p : players) {
                if (p.isAllIn() && p.getAllInPot() == 0) {
                    int chipsNotInPot = 0;
                    for (Player p1 : players) {
                        if (p1.isStillInRound() && p1.getChipsInRound() > p.getChipsInRound()) {
                            chipsNotInPot = chipsNotInPot + p1.getChipsInRound() - p.getChipsInRound();
                        }
                    }
                    p.setAllInPot(pot - chipsNotInPot);
                }
            }
            System.out.println("<<River betting has ended>>");

            System.out.println("\n<<Showdown>>");

            System.out.println("The Board is: " + deck.getBoard()[0] + ", " + deck.getBoard()[1] + ", "
                    + deck.getBoard()[2] + ", " + deck.getBoard()[3] + ", " + deck.getBoard()[4]);
            pause();
            // showing everyone's hand and rankings
            assignHandRank(players, deck, littleBlindPosition);
            // showdown winner
            int[][] winner = showdownWinner(players);
            printShowdownWinner(winner);
            // pot returns
            potReturns(players, winner, pot);

            if (!containsPlayerID(players, 0)) {
                System.out.println("You have been eliminated from the game");
                break;
            }

            System.out.println("There are " + players.size() + " left in the tournament.");
            pause();
            // printing player chips
            printPlayerChips(players);
            pause();

        } while (players.size()>1);
        if (players.size() == 1) {
            System.out.println("Congratulations you won!");
        }
        System.out.println("The Poker Game has ended");
    } //main

    public static boolean containsPlayerID(final ArrayList<Player> players, int ID)
    {
        for (Player p: players) {
            if (p.getPlayerID() == ID) {
                return true;
            }
        }
        return false;
    }

    public static void pause() {
        System.out.print("--> Press \"Enter\" to continue");
        try {
            int read = System.in.read(new byte[2]);
            System.out.println();
        } catch (IOException e) {
            e.printStackTrace();
        }
    } //pause

    public static int firstButton(ArrayList<Player> pla, Deck d) {
        d.shuffleDeck();
        int b = -1;
        boolean aceFound = false;
        do {
            for (Player p : pla) {
                p.setCard1(d.drawCard());
                if (p.getPlayerID() == 0) {
                    System.out.println("You drew " + p.getCard1());
                } else {
                    System.out.println("Player " + p.getPlayerID() + " drew a " + p.getCard1());
                }
                pause();
                if (p.getCard1().getValue().equals("A")) {

                    b = p.getPlayerID();
                    aceFound = true;
                    break;
                }
            }
        } while (!aceFound);
        if (b == 0) {
            System.out.println("You are assigned to the dealer/button position");
        } else {
            System.out.println("Player " + b + " is assigned to the dealer/button position");
        }
        return b;
    } //firstButton

    public static int setBlind(ArrayList<Player> players, int position1) {
        int position2;
        if (position1 == players.size() - 1) {
            position2 = 0;
        } else {
            position2 = position1 + 1;
        }
        return position2;
    } //setBlind

    public static void dealCards(ArrayList<Player> players, Deck deck, int littleBlind) {
        //dealing round 1
        for (Player p :players)
        {
            p.setCard1(deck.drawCard());
            p.setCard2(deck.drawCard());
            p.setStillInRound(true);
            p.setAllIn(false);
        }
    } //dealCards

    public static Card drawBoard(Deck deck) {
        Card cardToBoard = deck.drawCard();
        Card[] board = deck.getBoard();
        for (int i = 0; i < board.length; i++) {
            if (board[i] == null) {
                board[i] = cardToBoard;
                break;
            }
        }
        deck.setBoard(board);
        return cardToBoard;
    } //drawBoard

    public static boolean equalChipsInBetRound(ArrayList<Player> players) {
        int checkValue = 0;
        for (Player i : players) {
            if (i.isStillInRound() && !i.isAllIn()) {
                checkValue = i.getChipsInBetRound();
                break;
            }
        }
        for (Player i : players) {
            if (i.isStillInRound() && !i.isAllIn()) {
                if (i.getChipsInBetRound() != checkValue) {
                    return false;
                }
            }
        }
        return true;
    } //equalChipsInBetRound

    public static int[] botPreFlopFirstBettingRandom(ArrayList<Player> players, int i, int LB, int BB, int pot, int currentBet, int firstBetPosition, int littleBlindPosition, int bigBlindPosition) {
        if (players.get(i).getPlayerID() == firstBetPosition) {
            int choice = rand.nextInt(2);
            if (players.get(i).getChipAmount() <= currentBet) {
                switch (choice) {
                    case 0 -> {
                        System.out.println("Player " + i + " is All-In.");
                        players.get(i).setChipsInBetRound(players.get(i).getChipAmount());
                        players.get(i).setChipsInRound(players.get(i).getChipsInBetRound());
                        pot = pot + players.get(i).getChipAmount();
                        players.get(i).setChipAmount(0);
                        players.get(i).setAllIn(true);
                    }
                    case 1 -> {
                        System.out.println("Player " + i + " folds.");
                        System.out.println("Player " + i + " has $" + players.get(i).getChipAmount());
                        players.get(i).setStillInRound(false);
                    }
                }
            } else {
                switch (choice) {
                    case 0 -> {
                        System.out.println("Player " + i + " bets $" + currentBet);
                        System.out.println("Current bet has been raised to $" + currentBet);
                        players.get(i).setChipsInBetRound(currentBet);
                        players.get(i).setChipsInRound(players.get(i).getChipsInBetRound());
                        pot = pot + currentBet;
                        players.get(i).setChipAmount(players.get(i).getChipAmount() - currentBet);
                        System.out.println("Player " + i + " has $" + players.get(i).getChipAmount());
                    }
                    case 1 -> {
                        System.out.println("Player " + i + " folds");
                        System.out.println("Player " + i + " has $" + players.get(i).getChipAmount());
                        players.get(i).setStillInRound(false);
                    }
                }
            }
        } else if (players.get(i).getPlayerID() == littleBlindPosition) {
            System.out.println("Player " + i + " is little blind");
            if (players.get(i).getChipAmount() <= LB) {
                System.out.println("Player " + i + " is All-In.");
                players.get(i).setChipsInBetRound(players.get(i).getChipAmount());
                players.get(i).setChipsInRound(players.get(i).getChipsInBetRound());
                pot = pot + players.get(i).getChipAmount();
                players.get(i).setChipAmount(0);
                players.get(i).setAllIn(true);
            } else if (players.get(i).getChipAmount() <= currentBet) {
                int choice = rand.nextInt(2);
                switch (choice) {
                    case 0 -> {
                        System.out.println("Player " + i + " is All-In.");
                        players.get(i).setChipsInBetRound(players.get(i).getChipAmount());
                        players.get(i).setChipsInRound(players.get(i).getChipsInBetRound());
                        pot = pot + players.get(i).getChipAmount();
                        players.get(i).setChipAmount(0);
                        players.get(i).setAllIn(true);
                    }
                    case 1 -> {
                        System.out.println("Player " + i + " folds");
                        players.get(i).setChipAmount(players.get(i).getChipAmount() - LB);
                        pot = pot + LB;
                        System.out.println("$" + LB + " has been added to the pot");
                        System.out.println("Player " + i + " has $" + players.get(i).getChipAmount());
                        players.get(i).setStillInRound(false);
                    }
                }
            } else {
                int choice = rand.nextInt(3);
                switch (choice) {
                    case 0 -> {
                        if (players.get(i).getChipAmount() <= 2 * currentBet) {
                            System.out.println("Player " + i + " is All-In.");
                            System.out.println("Player " + i + " bets $" + players.get(i).getChipAmount());
                            currentBet = players.get(i).getChipAmount();
                            System.out.println("Current bet has been raised to $" + currentBet);
                            players.get(i).setChipsInBetRound(players.get(i).getChipAmount());
                            players.get(i).setChipsInRound(players.get(i).getChipsInBetRound());
                            pot = pot + players.get(i).getChipAmount();
                            players.get(i).setChipAmount(0);
                            players.get(i).setAllIn(true);
                        } else {
                            System.out.println("Player " + i + " bets $" + 2 * currentBet);
                            currentBet = 2 * currentBet;
                            System.out.println("Current bet has been raised to $" + currentBet);
                            players.get(i).setChipsInBetRound(currentBet);
                            players.get(i).setChipsInRound(players.get(i).getChipsInBetRound());
                            pot = pot + currentBet;
                            players.get(i).setChipAmount(players.get(i).getChipAmount() - currentBet);
                            System.out.println("Player " + i + " has $" + players.get(i).getChipAmount());
                        }
                    }
                    case 1 -> {
                        System.out.println("Player " + i + " calls $" + currentBet);
                        players.get(i).setChipsInBetRound(currentBet);
                        players.get(i).setChipsInRound(players.get(i).getChipsInBetRound());
                        pot = pot + currentBet;
                        players.get(i).setChipAmount(players.get(i).getChipAmount() - currentBet);
                        System.out.println("Player " + i + " has $" + players.get(i).getChipAmount());
                    }
                    case 2 -> {
                        System.out.println("Player " + i + " folds");
                        players.get(i).setChipAmount(players.get(i).getChipAmount() - LB);
                        pot = pot + LB;
                        System.out.println("$" + LB + " has been added to the pot");
                        System.out.println("Player " + i + " has $" + players.get(i).getChipAmount());
                        players.get(i).setStillInRound(false);
                    }
                }
            }
        } else if (players.get(i).getPlayerID() == bigBlindPosition) {
            System.out.println("Player " + i + " is big blind");
            if (players.get(i).getChipAmount() <= BB) {
                System.out.println("Player " + i + " is All-In.");
                players.get(i).setChipsInBetRound(players.get(i).getChipAmount());
                players.get(i).setChipsInRound(players.get(i).getChipsInBetRound());
                pot = pot + players.get(i).getChipAmount();
                players.get(i).setChipAmount(0);
                players.get(i).setAllIn(true);
            } else if (players.get(i).getChipAmount() <= currentBet) {
                int choice = rand.nextInt(2);
                switch (choice) {
                    case 0 -> {
                        System.out.println("Player " + i + " is All-In.");
                        players.get(i).setChipsInBetRound(players.get(i).getChipAmount());
                        players.get(i).setChipsInRound(players.get(i).getChipsInBetRound());
                        pot = pot + players.get(i).getChipAmount();
                        players.get(i).setChipAmount(0);
                        players.get(i).setAllIn(true);
                    }
                    case 1 -> {
                        System.out.println("Player " + i + " folds");
                        players.get(i).setChipAmount(players.get(i).getChipAmount() - BB);
                        pot = pot + BB;
                        System.out.println("$" + BB + " has been added to the pot");
                        System.out.println("Player " + i + " has $" + players.get(i).getChipAmount());
                        players.get(i).setStillInRound(false);
                    }
                }
            } else {
                int choice = rand.nextInt(3);
                switch (choice) {
                    case 0 -> {
                        if (players.get(i).getChipAmount() <= 2 * currentBet) {
                            System.out.println("Player " + i + " is All-In.");
                            System.out.println("Player " + i + " bets $" + players.get(i).getChipAmount());
                            currentBet = players.get(i).getChipAmount();
                            System.out.println("Current bet has been raised to $" + currentBet);
                            players.get(i).setChipsInBetRound(players.get(i).getChipAmount());
                            players.get(i).setChipsInRound(players.get(i).getChipsInBetRound());
                            pot = pot + players.get(i).getChipAmount();
                            players.get(i).setChipAmount(0);
                            players.get(i).setAllIn(true);
                        } else {
                            System.out.println("Player " + i + " bets $" + 2 * currentBet);
                            currentBet = 2 * currentBet;
                            System.out.println("Current bet has been raised to $" + currentBet);
                            players.get(i).setChipsInBetRound(currentBet);
                            players.get(i).setChipsInRound(players.get(i).getChipsInBetRound());
                            pot = pot + currentBet;
                            players.get(i).setChipAmount(players.get(i).getChipAmount() - currentBet);
                            System.out.println("Player " + i + " has $" + players.get(i).getChipAmount());
                        }
                    }
                    case 1 -> {
                        System.out.println("Player " + i + " calls $" + currentBet);
                        players.get(i).setChipsInBetRound(currentBet);
                        players.get(i).setChipsInRound(players.get(i).getChipsInBetRound());
                        pot = pot + currentBet;
                        players.get(i).setChipAmount(players.get(i).getChipAmount() - currentBet);
                        System.out.println("Player " + i + " has $" + players.get(i).getChipAmount());
                    }
                    case 2 -> {
                        System.out.println("Player " + i + " folds");
                        players.get(i).setChipAmount(players.get(i).getChipAmount() - BB);
                        pot = pot + BB;
                        System.out.println("$" + BB + " has been added to the pot");
                        System.out.println("Player " + i + " has $" + players.get(i).getChipAmount());
                        players.get(i).setStillInRound(false);
                    }
                }
            }
        } else {
            if (players.get(i).getChipAmount() <= currentBet) {
                int choice = rand.nextInt(2);
                switch (choice) {
                    case 0 -> {
                        System.out.println("Player " + i + " is All-In.");
                        players.get(i).setChipsInBetRound(players.get(i).getChipAmount());
                        players.get(i).setChipsInRound(players.get(i).getChipsInBetRound());
                        pot = pot + players.get(i).getChipAmount();
                        players.get(i).setChipAmount(0);
                        players.get(i).setAllIn(true);
                    }
                    case 1 -> {
                        System.out.println("Player " + i + " folds.");
                        System.out.println("Player " + i + " has $" + players.get(i).getChipAmount());
                        players.get(i).setStillInRound(false);
                    }
                }
            } else {
                int choice = rand.nextInt(3);
                switch (choice) {
                    case 0 -> {
                        if (players.get(i).getChipAmount() <= 2 * currentBet) {
                            System.out.println("Player " + i + " is All-In.");
                            System.out.println("Player " + i + " bets $" + players.get(i).getChipAmount());
                            currentBet = players.get(i).getChipAmount();
                            System.out.println("Current bet has been raised to $" + currentBet);
                            players.get(i).setChipsInBetRound(players.get(i).getChipAmount());
                            players.get(i).setChipsInRound(players.get(i).getChipsInBetRound());
                            pot = pot + players.get(i).getChipAmount();
                            players.get(i).setChipAmount(0);
                            players.get(i).setAllIn(true);
                        } else {
                            System.out.println("Player " + i + " bets $" + 2 * currentBet);
                            currentBet = 2 * currentBet;
                            System.out.println("Current bet has been raised to $" + currentBet);
                            players.get(i).setChipsInBetRound(currentBet);
                            players.get(i).setChipsInRound(players.get(i).getChipsInBetRound());
                            pot = pot + currentBet;
                            players.get(i).setChipAmount(players.get(i).getChipAmount() - currentBet);
                            System.out.println("Player " + i + " has $" + players.get(i).getChipAmount());
                        }
                    }
                    case 1 -> {
                        System.out.println("Player " + i + " calls $" + currentBet);
                        players.get(i).setChipsInBetRound(currentBet);
                        players.get(i).setChipsInRound(players.get(i).getChipsInBetRound());
                        pot = pot + currentBet;
                        players.get(i).setChipAmount(players.get(i).getChipAmount() - currentBet);
                        System.out.println("Player " + i + " has $" + players.get(i).getChipAmount());
                    }
                    case 2 -> {
                        System.out.println("Player " + i + " folds");
                        System.out.println("Player " + i + " has $" + players.get(i).getChipAmount());
                        players.get(i).setStillInRound(false);
                    }
                }
            }
        }
        return new int[]{pot, currentBet};
    } //botPreFlopFirstBettingRandom

    public static int[] botPostFlopFirstBettingRandom(ArrayList<Player> players, int i, int pot, int currentBet, int LB) {
        if (players.get(i).getChipAmount() <= currentBet) {
            int choice = rand.nextInt(2);
            switch (choice) {
                case 0 -> {
                    System.out.println("Player " + i + " is All-In.");
                    players.get(i).setChipsInBetRound(players.get(i).getChipAmount());
                    players.get(i).setChipsInRound(players.get(i).getChipsInRound() + players.get(i).getChipsInBetRound());
                    pot = pot + players.get(i).getChipAmount();
                    players.get(i).setChipAmount(0);
                    players.get(i).setAllIn(true);
                }
                case 1 -> {
                    System.out.println("Player " + i + " folds.");
                    System.out.println("Player " + i + " has $" + players.get(i).getChipAmount());
                    players.get(i).setStillInRound(false);
                }
            }
        } else if (currentBet == 0) {
            int choice = rand.nextInt(2);
            switch (choice) {
                case 0 -> {
                    if (players.get(i).getChipAmount() <= 3 * LB) {
                        System.out.println("Player " + i + " is All-In.");
                        System.out.println("Player " + i + " bets $" + players.get(i).getChipAmount());
                        currentBet = players.get(i).getChipAmount();
                        System.out.println("Current bet has been raised to $" + currentBet);
                        players.get(i).setChipsInBetRound(players.get(i).getChipAmount());
                        players.get(i).setChipsInRound(players.get(i).getChipsInRound() + players.get(i).getChipsInBetRound());
                        pot = pot + players.get(i).getChipAmount();
                        players.get(i).setChipAmount(0);
                        players.get(i).setAllIn(true);
                    } else {
                        System.out.println("Player " + i + " bets $" + 3 * LB);
                        currentBet = 3 * LB;
                        System.out.println("Current bet has been raised to $" + currentBet);
                        players.get(i).setChipsInBetRound(currentBet);
                        players.get(i).setChipsInRound(players.get(i).getChipsInRound() + players.get(i).getChipsInBetRound());
                        pot = pot + currentBet;
                        players.get(i).setChipAmount(players.get(i).getChipAmount() - currentBet);
                        System.out.println("Player " + i + " has $" + players.get(i).getChipAmount());
                    }
                }
                case 1 -> {
                    System.out.println("Player " + i + " calls $" + currentBet);
                    players.get(i).setChipsInBetRound(currentBet);
                    players.get(i).setChipsInRound(players.get(i).getChipsInRound() + players.get(i).getChipsInBetRound());
                    pot = pot + currentBet;
                    players.get(i).setChipAmount(players.get(i).getChipAmount() - currentBet);
                    System.out.println("Player " + i + " has $" + players.get(i).getChipAmount());
                }
            }
        } else {
            int choice = rand.nextInt(3);
            switch (choice) {
                case 0 -> {
                    if (players.get(i).getChipAmount() <= 2 * currentBet) {
                        System.out.println("Player " + i + " is All-In.");
                        System.out.println("Player " + i + " bets $" + players.get(i).getChipAmount());
                        currentBet = players.get(i).getChipAmount();
                        System.out.println("Current bet has been raised to $" + currentBet);
                        players.get(i).setChipsInBetRound(players.get(i).getChipAmount());
                        players.get(i).setChipsInRound(players.get(i).getChipsInRound() + players.get(i).getChipsInBetRound());
                        pot = pot + players.get(i).getChipAmount();
                        players.get(i).setChipAmount(0);
                        players.get(i).setAllIn(true);
                    } else {
                        System.out.println("Player " + i + " bets $" + 2 * currentBet);
                        currentBet = 2 * currentBet;
                        System.out.println("Current bet has been raised to $" + currentBet);
                        players.get(i).setChipsInBetRound(currentBet);
                        players.get(i).setChipsInRound(players.get(i).getChipsInRound() + players.get(i).getChipsInBetRound());
                        pot = pot + currentBet;
                        players.get(i).setChipAmount(players.get(i).getChipAmount() - currentBet);
                        System.out.println("Player " + i + " has $" + players.get(i).getChipAmount());
                    }
                }
                case 1 -> {
                    System.out.println("Player " + i + " calls $" + currentBet);
                    players.get(i).setChipsInBetRound(currentBet);
                    players.get(i).setChipsInRound(players.get(i).getChipsInRound() + players.get(i).getChipsInBetRound());
                    pot = pot + currentBet;
                    players.get(i).setChipAmount(players.get(i).getChipAmount() - currentBet);
                    System.out.println("Player " + i + " has $" + players.get(i).getChipAmount());
                }
                case 2 -> {
                    System.out.println("Player " + i + " folds");
                    System.out.println("Player " + i + " has $" + players.get(i).getChipAmount());
                    players.get(i).setStillInRound(false);
                }
            }
        }
        return new int[]{pot, currentBet};
    } //botPostFlopFirstBettingRandom

    public static int[] botEqualBettingRandom(ArrayList<Player> players, int i, int pot, int currentBet) {
        if (players.get(i).getChipAmount() + players.get(i).getChipsInBetRound() <= currentBet) {
            int choice = rand.nextInt(2);
            switch (choice) {
                case 0 -> {
                    System.out.println("Player " + i + " is All-In.");
                    players.get(i).setChipsInBetRound(players.get(i).getChipsInBetRound() + players.get(i).getChipAmount());
                    pot = pot + players.get(i).getChipAmount();
                    players.get(i).setChipsInRound(players.get(i).getChipsInRound() + players.get(i).getChipAmount());
                    players.get(i).setChipAmount(0);
                    players.get(i).setAllIn(true);
                }
                case 1 -> {
                    System.out.println("Player " + i + " folds.");
                    System.out.println("Player " + i + " has $" + players.get(i).getChipAmount());
                    players.get(i).setStillInRound(false);
                }
            }
        } else {
            int choice = rand.nextInt(3);
            switch (choice) {
                case 0 -> {
                    if (players.get(i).getChipAmount() <= 2 * currentBet) {
                        System.out.println("Player " + i + " is All-In.");
                        System.out.println("Player " + i + " bets $" + players.get(i).getChipsInBetRound() + players.get(i).getChipAmount());
                        currentBet = players.get(i).getChipsInBetRound() + players.get(i).getChipAmount();
                        System.out.println("Current bet has been raised to $" + currentBet);
                        pot = pot + players.get(i).getChipAmount();
                        players.get(i).setChipsInRound(players.get(i).getChipsInRound() + players.get(i).getChipAmount());
                        players.get(i).setChipsInBetRound(players.get(i).getChipsInBetRound() + players.get(i).getChipAmount());
                        players.get(i).setChipAmount(0);
                        players.get(i).setAllIn(true);
                    } else {
                        System.out.println("Player " + i + " bets $" + 2 * currentBet);
                        currentBet = 2 * currentBet;
                        System.out.println("Current bet has been raised to $" + currentBet);
                        pot = pot + currentBet - players.get(i).getChipsInBetRound();
                        players.get(i).setChipsInRound(players.get(i).getChipsInRound() + currentBet - players.get(i).getChipsInBetRound());
                        players.get(i).setChipAmount(players.get(i).getChipAmount() - currentBet + players.get(i).getChipsInBetRound());
                        players.get(i).setChipsInBetRound(currentBet);
                        System.out.println("Player " + i + " has $" + players.get(i).getChipAmount());
                    }
                }
                case 1 -> {
                    System.out.println("Player " + i + " calls $" + currentBet);
                    pot = pot + currentBet - players.get(i).getChipsInBetRound();
                    players.get(i).setChipsInRound(players.get(i).getChipsInRound() + currentBet - players.get(i).getChipsInBetRound());
                    players.get(i).setChipAmount(players.get(i).getChipAmount() - currentBet + players.get(i).getChipsInBetRound());
                    players.get(i).setChipsInBetRound(currentBet);
                    System.out.println("Player " + i + " has $" + players.get(i).getChipAmount());
                }
                case 2 -> {
                    System.out.println("Player " + i + " folds");
                    System.out.println("Player " + i + " has $" + players.get(i).getChipAmount());
                    players.get(i).setStillInRound(false);
                }
            }
        }
        return new int[]{pot, currentBet};
    } //botEqualBettingRandom

    public static void assignHandRank(ArrayList<Player> players, Deck deck, int littleBlindPosition) {
        for (int i = littleBlindPosition; i < players.size(); i++) {
            if (players.get(i).isStillInRound() && players.get(i).getPlayerID() == 0) {
                System.out.println("Your hand is: " + players.get(i).getHand()[0] + " & " + players.get(i).getHand()[1]);
                players.get(i).setHandRanking(rankHand(deck, players.get(i).getHand()));
                System.out.println("You have " + numberToHandRank(players.get(i).getHandRanking()[0]));
                pause();
            } else if (players.get(i).isStillInRound()) {
                System.out.println("Player " + players.get(i).getPlayerID() + "'s hand is: " + players.get(i).getHand()[0]
                        + " & " + players.get(i).getHand()[1]);
                players.get(i).setHandRanking(rankHand(deck, players.get(i).getHand()));
                System.out.println("Player " + players.get(i).getPlayerID() + " has "
                        + numberToHandRank(players.get(i).getHandRanking()[0]));
                pause();
            }
        }
        for (int i = 0; i < littleBlindPosition; i++) {
            if (players.get(i).isStillInRound() && players.get(i).getPlayerID() == 0) {
                System.out.println("Your hand is: " + players.get(i).getHand()[0] + " & " + players.get(i).getHand()[1]);
                players.get(i).setHandRanking(rankHand(deck, players.get(i).getHand()));
                System.out.println("You have " + numberToHandRank(players.get(i).getHandRanking()[0]));
                pause();
            } else if (players.get(i).isStillInRound()) {
                System.out.println("Player " + players.get(i).getPlayerID() + "'s hand is: " + players.get(i).getHand()[0]
                        + " & " + players.get(i).getHand()[1]);
                players.get(i).setHandRanking(rankHand(deck, players.get(i).getHand()));
                System.out.println("Player " + players.get(i).getPlayerID() + " has "
                        + numberToHandRank(players.get(i).getHandRanking()[0]));
                pause();
            }
        }
    } //assignHandRank

    public static String numberToHandRank(int number) {
        return switch (number) {
            case 9 -> "a Royal FLush";
            case 8 -> "a Straight FLush";
            case 7 -> "Four of a Kind";
            case 6 -> "a Full House";
            case 5 -> "a FLush";
            case 4 -> "a Straight";
            case 3 -> "Three of a Kind";
            case 2 -> "Two Pair";
            case 1 -> "a Pair";
            case 0 -> "a High Poker.Card";
            default -> "";
        };
    } //numberToHandRank

    public static int[] rankHand(Deck deck, Card[] hand) {
        if (isRoyalFlush(deck, hand)) {
            return new int[]{9};
        }
        int[] isStraightFlush_value = isStraightFlush(deck, hand);
        if (isStraightFlush_value[0] == 1) {
            isStraightFlush_value[0] = 8;
            return isStraightFlush_value;
        }
        int[] isQuad_values = isFourOfAKind(deck, hand);
        if (isQuad_values[0] == 1) {
            isQuad_values[0] = 7;
            return isQuad_values;
        }
        int[] isFullHouse_values = isFullHouse(deck, hand);
        if (isFullHouse_values[0] == 1) {
            isFullHouse_values[0] = 6;
            return isFullHouse_values;
        }
        int[] isFlush_values = isFlush(deck, hand);
        if (isFlush_values[0] == 1) {
            isFlush_values[0] = 5;
            return isFlush_values;
        }
        int[] isStraight_value = isStraight(deck, hand);
        if (isStraight_value[0] == 1) {
            isStraight_value[0] = 4;
            return isStraight_value;
        }
        int[] isThreeOfAKind_values = isThreeOfAKind(deck, hand);
        if (isThreeOfAKind_values[0] == 1) {
            isThreeOfAKind_values[0] = 3;
            return isThreeOfAKind_values;
        }
        int[] isTwoPair_values = isTwoPair(deck, hand);
        if (isTwoPair(deck, hand)[0] == 1) {
            isTwoPair_values[0] = 2;
            return isTwoPair_values;
        }
        int[] isPair_values = isPair(deck, hand);
        if (isPair_values[0] == 1) {
            // isPair_values[0] is already 1
            return isPair_values;
        }
        return highCard(deck, hand);
    } //rankHand

    public static boolean isRoyalFlush(Deck deck, Card[] hand) {
        int[] flush = isFlush(deck, hand);
        return (flush[0] == 1 && flush[1] == 14 && flush[2] == 13 && flush[3] == 12 && flush[4] == 11 && flush[5] == 10);
    } //isRoyalFlush

    public static int[] isStraightFlush(Deck deck, Card[] hand) {
        ArrayList<Card> allCards = new ArrayList<>(Arrays.asList(deck.getBoard()[0], deck.getBoard()[1], deck.getBoard()[2], deck.getBoard()[3],
                deck.getBoard()[4], hand[0], hand[1]));

        // Sorting the array by value
        for (int i = 0; i < allCards.size() - 1; i++)
        {
            for (int j = 0; j < allCards.size() - i - 1; j++)
            {
                if (cardToValue(allCards.get(j)) > cardToValue(allCards.get(j + 1))) {
                    // swap arr[j+1] and arr[j]
                    Collections.swap(allCards, j, j+1);
                }
            }
        }

        int numOfCardsInRow = 1;
        int pos = 0;
        boolean first4InRow = true;
        String suit = null;

        int[] isStraightFlush_highValue = new int[2];
        // 0 index ->  0 means not a straight flush
        // 0 index -> 1 means is a straight flush
        // 1 index -> the highest card of the straight flush

        while (pos < allCards.size() - 1) {
            // if the card in front is a duplicate, continue the loop
            if (cardToValue(allCards.get(pos + 1)) - cardToValue(allCards.get(pos)) == 0) {
                pos++;
                continue;
            }
            if (cardToValue(allCards.get(pos + 1)) - cardToValue(allCards.get(pos)) == 1 && allCards.get(pos).getSuit().equals(allCards.get(pos + 1).getSuit())) {
                numOfCardsInRow++;
                suit = allCards.get(pos).getSuit();
                if (numOfCardsInRow >= 5) {
                    isStraightFlush_highValue[0] = 1;
                    isStraightFlush_highValue[1] = cardToValue(allCards.get(pos + 1));
                    pos++;
                } else if (numOfCardsInRow == 4 && first4InRow) {
                    isStraightFlush_highValue[1] = cardToValue(allCards.get(pos + 1));
                    pos++;
                    first4InRow = false;
                } else {
                    pos++;
                }
            } else if (!first4InRow) {
                break;
            } else {
                numOfCardsInRow = 1;
                pos++;
            }
        }

        if (numOfCardsInRow == 4 && isStraightFlush_highValue[1] == 5 && allCards.contains(new Card("A", suit))) {
            isStraightFlush_highValue[0] = 1;
        }

        return isStraightFlush_highValue;
    } //isStraightFlush

    public static int[] isFourOfAKind(Deck deck, Card[] hand) {
        int[] allCards = {cardToValue(deck.getBoard()[0]), cardToValue(deck.getBoard()[1]), cardToValue(deck.getBoard()[2]),
                cardToValue(deck.getBoard()[3]), cardToValue(deck.getBoard()[4]), cardToValue(hand[0]), cardToValue(hand[1])};
        Arrays.sort(allCards);

        int numOfRepeat;
        int i = 0;
        int j;

        int[] isFourOfAKind_value = new int[3];
        // 0 index -> 0 means not a Four of a Kind
        // 0 index -> 1 means is a Four of a Kind
        // 1 index -> the value of the Four of a Kind
        // 2 index -> value of kicker

        while (i < allCards.length && isFourOfAKind_value[0] == 0) {
            j = i + 1;
            numOfRepeat = 1;
            while (j < allCards.length && isFourOfAKind_value[0] == 0) {
                if (allCards[i] == allCards[j]) {
                    numOfRepeat++;
                    if (numOfRepeat == 4) {
                        isFourOfAKind_value[0] = 1;
                        isFourOfAKind_value[1] = allCards[i];
                    }
                }
                j++;
            }
            i++;
        }
        // if there is no four of a kind, return false
        if (isFourOfAKind_value[0] == 0) {
            return isFourOfAKind_value;
        }

        // determining the kicker
        if (isFourOfAKind_value[1] == allCards[6]) {
            isFourOfAKind_value[2] = allCards[2];
            return isFourOfAKind_value;
        }
        isFourOfAKind_value[2] = allCards[6];
        return isFourOfAKind_value;
    } //isFourOfAKind

    public static int[] isFullHouse(Deck deck, Card[] hand) {
        Integer[] allCards = {cardToValue(deck.getBoard()[0]), cardToValue(deck.getBoard()[1]), cardToValue(deck.getBoard()[2]),
                cardToValue(deck.getBoard()[3]), cardToValue(deck.getBoard()[4]), cardToValue(hand[0]), cardToValue(hand[1])};
        Arrays.sort(allCards, Collections.reverseOrder());
        int numOfCardsInARow = 1;
        int pos = 0;
        int skip = 8;

        boolean isThreeOfAKind = false;
        boolean isTwoOfAKind = false;
        int[] isFullHouse_values = new int[3];
        // 0 index -> 0 means not a Full House
        // 0 index -> 1 means is a Full House
        // 1 index -> the value of the Trip
        // 2 index -> the value of the Pair

        while (pos < allCards.length - 1 && !isThreeOfAKind) {
            if (Objects.equals(allCards[pos], allCards[pos + 1])) {
                numOfCardsInARow++;
                if (numOfCardsInARow == 3) {
                    isThreeOfAKind = true;
                    isFullHouse_values[1] = allCards[pos];
                    skip = pos - 2;
                } else pos++;
            } else {
                numOfCardsInARow = 1;
                pos++;
            }
        }
        pos = 0;
        while (pos < allCards.length - 1 && !isTwoOfAKind) {
            if (pos != skip && pos != skip + 1 && pos != skip + 2) {
                if (Objects.equals(allCards[pos], allCards[pos + 1])) {
                    isTwoOfAKind = true;
                    isFullHouse_values[2] = allCards[pos];
                } else {
                    pos++;
                }
            } else {
                pos++;
            }
        }
        if (isThreeOfAKind && isTwoOfAKind) {
            isFullHouse_values[0] = 1;
        }
        return isFullHouse_values;
    } //isFullHouse

    public static int[] isFlush(Deck deck, Card[] hand) {
        Card[] allCards = {deck.getBoard()[0], deck.getBoard()[1], deck.getBoard()[2], deck.getBoard()[3],
                deck.getBoard()[4], hand[0], hand[1]};

        ArrayList<Integer> clubCards = new ArrayList<>();
        ArrayList<Integer> diamondCards = new ArrayList<>();
        ArrayList<Integer> heartCards = new ArrayList<>();
        ArrayList<Integer> spadeCards = new ArrayList<>();

        int[] isFlush_values = new int[6];
        // 0 index -> 0 means not a straight
        // 0 index -> 1 means is a straight
        // five following indexes -> the highest card values of the straight from highest to lowest

        for (Card c : allCards) {
            switch (c.getSuit()) {
                case "Clubs" -> clubCards.add(cardToValue(c));
                case "Diamonds" -> diamondCards.add(cardToValue(c));
                case "Hearts" -> heartCards.add(cardToValue(c));
                case "Spades" -> spadeCards.add(cardToValue(c));
            }
        }
        if (clubCards.size() >= 5) {
            isFlush_values[0] = 1;
            Collections.sort(clubCards);
            isFlush_values[1] = clubCards.get(clubCards.size() - 1);
            isFlush_values[2] = clubCards.get(clubCards.size() - 2);
            isFlush_values[3] = clubCards.get(clubCards.size() - 3);
            isFlush_values[4] = clubCards.get(clubCards.size() - 4);
            isFlush_values[5] = clubCards.get(clubCards.size() - 5);
        } else if (diamondCards.size() >= 5) {
            isFlush_values[0] = 1;
            Collections.sort(diamondCards);
            isFlush_values[1] = diamondCards.get(diamondCards.size() - 1);
            isFlush_values[2] = diamondCards.get(diamondCards.size() - 2);
            isFlush_values[3] = diamondCards.get(diamondCards.size() - 3);
            isFlush_values[4] = diamondCards.get(diamondCards.size() - 4);
            isFlush_values[5] = diamondCards.get(diamondCards.size() - 5);
        } else if (heartCards.size() >= 5) {
            isFlush_values[0] = 1;
            Collections.sort(heartCards);
            isFlush_values[1] = heartCards.get(heartCards.size() - 1);
            isFlush_values[2] = heartCards.get(heartCards.size() - 2);
            isFlush_values[3] = heartCards.get(heartCards.size() - 3);
            isFlush_values[4] = heartCards.get(heartCards.size() - 4);
            isFlush_values[5] = heartCards.get(heartCards.size() - 5);
        } else if (spadeCards.size() >= 5) {
            isFlush_values[0] = 1;
            Collections.sort(spadeCards);
            isFlush_values[1] = spadeCards.get(spadeCards.size() - 1);
            isFlush_values[2] = spadeCards.get(spadeCards.size() - 2);
            isFlush_values[3] = spadeCards.get(spadeCards.size() - 3);
            isFlush_values[4] = spadeCards.get(spadeCards.size() - 4);
            isFlush_values[5] = spadeCards.get(spadeCards.size() - 5);
        }
        return isFlush_values;
    } //isFlush

    public static int[] isStraight(Deck deck, Card[] hand) {
        int[] allCards = {cardToValue(deck.getBoard()[0]), cardToValue(deck.getBoard()[1]), cardToValue(deck.getBoard()[2]),
                cardToValue(deck.getBoard()[3]), cardToValue(deck.getBoard()[4]), cardToValue(hand[0]), cardToValue(hand[1])};
        Arrays.sort(allCards);
        int numOfCardsInRow = 1;
        int pos = 0;
        boolean first4InRow = true;

        int[] isStraight_highValue = new int[2];
        // 0 index -> 0 means not a straight
        // 0 index -> 1 means is a straight
        // 1 index -> the highest card of the straight

        while (pos < allCards.length - 1) {
            // if the card in front is a duplicate, continue the loop
            if (allCards[pos + 1] - allCards[pos] == 0) {
                pos++;
                continue;
            }
            if (allCards[pos + 1] - allCards[pos] == 1) {
                numOfCardsInRow++;
                if (numOfCardsInRow >= 5) {
                    isStraight_highValue[0] = 1;
                    isStraight_highValue[1] = allCards[pos + 1];
                    pos++;
                } else if (numOfCardsInRow == 4 && first4InRow) {
                    isStraight_highValue[1] = allCards[pos + 1];
                    pos++;
                    first4InRow = false;
                } else {
                    pos++;
                }
            } else if (!first4InRow) {
                break;
            } else {
                numOfCardsInRow = 1;
                pos++;
            }
        }

        if (numOfCardsInRow == 4 && isStraight_highValue[1] == 5 && allCards[6] == 14) {
            isStraight_highValue[0] = 1;
        }

        return isStraight_highValue;
    } //isStraight

    public static int[] isThreeOfAKind(Deck deck, Card[] hand) {
        int[] allCards = {cardToValue(deck.getBoard()[0]), cardToValue(deck.getBoard()[1]), cardToValue(deck.getBoard()[2]),
                cardToValue(deck.getBoard()[3]), cardToValue(deck.getBoard()[4]), cardToValue(hand[0]), cardToValue(hand[1])};
        Arrays.sort(allCards);

        int numOfRepeat;
        int i = 0;
        int j;

        int[] isThreeOfAKind_value = new int[4];
        // 0 index -> 0 means not a Three of a Kind
        // 0 index -> 1 means is a Three of a Kind
        // 1 index -> the value of the Three of a Kind
        // 2 index -> the value of the higher kicker
        // 3 index -> the value of the lower kicker

        while (i < allCards.length && isThreeOfAKind_value[0] == 0) {
            j = i + 1;
            numOfRepeat = 1;
            while (j < allCards.length && isThreeOfAKind_value[0] == 0) {
                if (allCards[i] == allCards[j]) {
                    numOfRepeat++;
                    if (numOfRepeat == 3) {
                        isThreeOfAKind_value[0] = 1;
                        isThreeOfAKind_value[1] = allCards[i];
                    }
                }
                j++;
            }
            i++;
        }
        // if there is no three of a kind, return false
        if (isThreeOfAKind_value[0] == 0) {
            return isThreeOfAKind_value;
        }

        // determining the kicker cards
        int highest = 0;
        int secondHighest = 0;

        for (int allCard : allCards) {
            if (allCard >= highest && allCard != isThreeOfAKind_value[1]) {
                highest = allCard;
                secondHighest = highest;
            }
        }
        isThreeOfAKind_value[2] = highest;
        isThreeOfAKind_value[3] = secondHighest;
        return isThreeOfAKind_value;
    } //isThreeOfAKind

    public static int[] isTwoPair(Deck deck, Card[] hand) {
        Integer[] allCards = {cardToValue(deck.getBoard()[0]), cardToValue(deck.getBoard()[1]), cardToValue(deck.getBoard()[2]),
                cardToValue(deck.getBoard()[3]), cardToValue(deck.getBoard()[4]), cardToValue(hand[0]), cardToValue(hand[1])};
        Arrays.sort(allCards, Collections.reverseOrder());
        int pos = 0;

        boolean isPair1 = false;
        boolean isPair2 = false;
        int[] isTwoPair_values = new int[4];
        // 0 index -> 0 means not a Two Pair
        // 0 index -> 1 means is a Two Pair
        // 1 index -> the value of the higher Pair
        // 2 index -> the value of the lower Pair
        // 3 index -> the value of the kicker

        while (pos < allCards.length - 1) {
            if (Objects.equals(allCards[pos], allCards[pos + 1])) {
                if (isPair1) {
                    isPair2 = true;
                    isTwoPair_values[2] = allCards[pos];
                    break;
                } else {
                    isPair1 = true;
                    isTwoPair_values[1] = allCards[pos];
                }
            }
            pos++;
        }
        // if there is no two pair, return false
        if (!(isPair1 && isPair2)) {
            return isTwoPair_values;
        }

        // determining the kicker
        if (isTwoPair_values[1] == allCards[0] && isTwoPair_values[2] == allCards[2]) {
            isTwoPair_values[0] = 1;
            isTwoPair_values[3] = allCards[4];
            return isTwoPair_values;
        }
        if (isTwoPair_values[1] == allCards[0]) {
            isTwoPair_values[0] = 1;
            isTwoPair_values[3] = allCards[2];
            return isTwoPair_values;
        }
        isTwoPair_values[0] = 1;
        isTwoPair_values[3] = allCards[0];
        return isTwoPair_values;
    } //isTwoPair

    public static int[] isPair(Deck deck, Card[] hand) {
        int[] allCards = {cardToValue(deck.getBoard()[0]), cardToValue(deck.getBoard()[1]), cardToValue(deck.getBoard()[2]),
                cardToValue(deck.getBoard()[3]), cardToValue(deck.getBoard()[4]), cardToValue(hand[0]), cardToValue(hand[1])};
        Arrays.sort(allCards);

        int i = 0;
        int j;

        int[] isPair_value = new int[5];
        // 0 index -> 0 means not a Pair
        // 0 index -> 1 means is a Pair
        // 1 index -> the value of the Pair
        // 2 index -> the highest kicker
        // 3 index -> the second-highest kicker
        // 4 index -> the third-highest kicker

        while (i < allCards.length - 1 && isPair_value[0] == 0) {
            j = i + 1;
            while (j < allCards.length && isPair_value[0] == 0) {
                if (allCards[i] == allCards[j]) {
                    isPair_value[0] = 1;
                    isPair_value[1] = allCards[i];
                }
                j++;
            }
            i++;
        }
        // if there is no pair, return false
        if (isPair_value[0] == 0) {
            return isPair_value;
        }
        // determining the kickers
        int highest = 0;
        int secondHighest = 0;
        int thirdHighest = 0;

        for (int allCard : allCards) {
            if (allCard >= highest && allCard != isPair_value[1]) {
                highest = allCard;
                secondHighest = highest;
                thirdHighest = highest;
            }
        }
        isPair_value[2] = highest;
        isPair_value[3] = secondHighest;
        isPair_value[4] = thirdHighest;
        return isPair_value;
    } //isPair

    public static int[] highCard(Deck deck, Card[] hand) {
        int[] allCards = {cardToValue(deck.getBoard()[0]), cardToValue(deck.getBoard()[1]), cardToValue(deck.getBoard()[2]),
                cardToValue(deck.getBoard()[3]), cardToValue(deck.getBoard()[4]), cardToValue(hand[0]), cardToValue(hand[1])};
        Arrays.sort(allCards);

        int[] highCard_values = new int[6];
        // 0 index -> 0 for method rankHand
        // 1 index -> value of high card
        // 2 index -> value of second-highest card
        // 3 index -> value of third-highest card
        // 4 index -> value of fourth-highest card
        // 5 index -> value of fifth-highest card

        for (int i = 0; i < 5; i++) {
            highCard_values[i + 1] = allCards[6 - i];
        }
        return highCard_values;
    } //highCard

    public static int cardToValue(Card card) {
        int value = 0;
        switch (card.getValue()) {
            case "2" -> value = 2;
            case "3" -> value = 3;
            case "4" -> value = 4;
            case "5" -> value = 5;
            case "6" -> value = 6;
            case "7" -> value = 7;
            case "8" -> value = 8;
            case "9" -> value = 9;
            case "T" -> value = 10;
            case "J" -> value = 11;
            case "Q" -> value = 12;
            case "K" -> value = 13;
            case "A" -> value = 14;
        }
        return value;
    } //cardToValue

    public static int[][] showdownWinner(ArrayList<Player> players) {

        ArrayList<Player> showdownPlayers = new ArrayList<>(players);
        showdownPlayers.removeIf(p -> (p.getHandRanking() == null));
        // sorts the players by hand-rankings property starting with the best hand
        for (int i = 0; i < showdownPlayers.size() - 1; i++) {
            for (int j = 0; j < showdownPlayers.size() - i - 1; j++) {
                if (showdownPlayers.get(j).getHandRanking()[0] < showdownPlayers.get(j + 1).getHandRanking()[0])
                {
                    Collections.swap(showdownPlayers, j, j+1);
                }
            }
        }

        // 2d array of winners by playerID
        // 1st dimension -> order of winners from first to last
        // 2nd dimension -> if there is a tie the player IDs will be in the same column;
        int [][] winner = new int[showdownPlayers.size()][showdownPlayers.size()];
        // initializing winner 2d array with -1
        for (int i = 0; i < winner.length; i++)
        {
            for (int j = 0; j < winner[0].length; j++)
            {
                winner[i][j] = -1;
            }
        }
        //number of winners in the winner array
        int k = 0;
        boolean leave = false;
        while (!leave) {
            // if the first index of getHandRanking is greater, the winner is found
            if (showdownPlayers.size() == 1)
            {
                winner[k][0] = showdownPlayers.get(0).getPlayerID();
                leave = true;
                break;
            }
            else if (showdownPlayers.get(0).getHandRanking()[0] > showdownPlayers.get(1).getHandRanking()[0]) {
                winner[k][0] = showdownPlayers.get(0).getPlayerID();
                showdownPlayers.remove(0);
                k++;
            }
            // else, the first index of getHandRanking is the same as the next player and there is a tie and the kickers must be looked at
            else
            {
                int handRankingLength = showdownPlayers.get(0).getHandRanking().length;
                ArrayList<Player> playersInTie = new ArrayList<>(showdownPlayers);
                for (int i = 0; i < handRankingLength; i++) {
                    // counting the number of players in a tie
                    int finalI = i;
                    playersInTie.removeIf(p -> p.getHandRanking()[finalI] != playersInTie.get(0).getHandRanking()[finalI]);
                    int numOfPlayersInTie = playersInTie.size();
                    // if there is only one player in the tie, they are the winner
                    if (numOfPlayersInTie == 1) {
                        winner[k][0] = playersInTie.get(0).getPlayerID();
                        showdownPlayers.remove(playersInTie.get(0));
                        k++;
                        break;
                    }
                    // if this is the last iteration of kickers
                    else if (i == handRankingLength - 1) {
                        for (int i1 = 0; i1 < numOfPlayersInTie; i1++) {
                            winner[k][i1] = playersInTie.get(i1).getPlayerID();
                            showdownPlayers.remove(playersInTie.get(i1));
                        }
                        k++;
                        break;
                    }
                    //sorting by the kicker of the hand ranking in each index of the array
                    for (int i1 = 0; i1 < playersInTie.size() - 1; i1++) {
                        for (int j = 0; j < playersInTie.size() - i1 - 1; j++) {
                            if (playersInTie.get(j).getHandRanking()[i + 1] < playersInTie.get(j + 1).getHandRanking()[i + 1]) {
                                Collections.swap(playersInTie, j, j + 1);
                            }
                        }
                    }
                }
            }
        }
        return winner;
    } //showdownWinner

    public static void printShowdownWinner(int[][] winner) {
        ArrayList<Integer> winnerList = new ArrayList<>();
        for (int w: winner[0]) {
            if (w!=-1)
            {
                winnerList.add(w);
            }
        }
        if (winnerList.size() > 1) {
            System.out.println("The winners of the round are:");
            for (int w : winnerList) {
                System.out.println("Player " + w);
            }
            System.out.println();
            return;
        }
        if (winner[0][0] == 0) {
            System.out.println("You won the round\n");
            return;
        }
        System.out.println("Player " + winner[0][0] + " won the round\n");
    } //printShowdownWinner

    public static void potReturns(ArrayList<Player> players, int[][] winner, int pot) {
        // New arrayList of people still in the round sorted by chips in round
        ArrayList<Player> playersInRound = new ArrayList<>(players);
        playersInRound.removeIf(p -> !p.isStillInRound());
        //sorting players in round by chips in round from least to greatest
        for (int i = 0; i < playersInRound.size() - 1; i++) {
            for (int j = 0; j < playersInRound.size() - i - 1; j++) {
                if (playersInRound.get(j).getChipsInRound() > playersInRound.get(j + 1).getChipsInRound()) {
                    Collections.swap(playersInRound, j, j+1);
                }
            }
        }

        ArrayList<Integer> eliminatedPlayers = new ArrayList<>();
        do
        {
            int i = 0;
            ArrayList<Player> winnersInTie = new ArrayList<>();
            // creating a list with the row of winners in the iteration: the winner(s) in a tie
            for (int w: winner[i])
            {
                for (Player p: playersInRound)
                {
                    if (p.getPlayerID() == w)
                    {
                        winnersInTie.add(p);
                    }
                }
            }
            // sorting the winners in a tie by amount of chips in round
            for (int i1 = 0; i1 < winnersInTie.size() - 1; i1++) {
                for (int j = 0; j < winnersInTie.size() - i - 1; j++) {
                    if (winnersInTie.get(j).getChipsInRound() > winnersInTie.get(j + 1).getChipsInRound()) {
                        Collections.swap(winnersInTie, j, j+1);
                    }
                }
            }
            // getting the highest chip in round from winners in round
            int winnerChipsInRound = winnersInTie.get(winnersInTie.size()-1).getChipsInRound();
            //removing player(s) that are all in and did not win.
            for (Player p : playersInRound) {
                boolean skip = false;
                for(int i1 = 0; i1 < winner[i].length; i1++) {
                    if (p.getPlayerID() == winner[i][i1]) {
                        skip = true;
                        break;
                    }
                }
                if (skip)
                {
                    continue;
                }
                if (p.getChipsInRound() <= winnerChipsInRound && p.isAllIn())
                {
                    System.out.println("Player " + p.getPlayerID() + " is out of chips & is eliminated from the tournament.");
                    eliminatedPlayers.add(p.getPlayerID());
                }
            }
            players.removeIf(p -> eliminatedPlayers.contains(p.getPlayerID()));
            eliminatedPlayers.clear();
            pause();
            // giving pot to winner(s)
            int numOfWinnersInTie = winnersInTie.size();
            int previousWinnerSidePot = 0;
            ArrayList<Player> playerAlreadyWonChips = new ArrayList<>();
            // iterating through every winner starting with the lowest all in pot
            for (Player w : winnersInTie) {
                //starting to divide the winner with the lowest all in pot
                int winnerSidePot = w.getAllInPot() - previousWinnerSidePot;
                System.out.println("The pot of " + winnerSidePot + " chips is divided between " + numOfWinnersInTie + " winner(s).");
                for (Player w1 : winnersInTie) {
                    if (playerAlreadyWonChips.contains(w1))
                    {
                        // if player already won maximum chips then skip
                        continue;
                    }
                    for (Player p : playersInRound) {
                        if (p.equals(w1)) {
                            // matching winner to player
                            //giving winning to each winner
                            int chipsWon = winnerSidePot/numOfWinnersInTie;
                            p.setChipAmount(p.getChipAmount() + winnerSidePot / numOfWinnersInTie);
                            System.out.println("Player " + p.getPlayerID() + " won " + chipsWon + " chips.");
                            pot -= chipsWon;
                        }
                    }
                    if (w1.equals(w))
                    {
                        playerAlreadyWonChips.add(w);
                    }
                }
                numOfWinnersInTie--;
                previousWinnerSidePot = winnerSidePot;
            }
        }
        while (pot > 0);
        System.out.println("Pot: " + pot + " should be 0");
    } //potReturns

    public static void printPlayerChips(ArrayList<Player> players)
    {
        System.out.println("Player Chip Amounts:\n");

        for (Player p:players)
        {
            System.out.println("Player " + p.getPlayerID() + " has " + p.getChipAmount() + " chips.");
        }
    } //printPlayerChips
} //Poker.PlayPoker