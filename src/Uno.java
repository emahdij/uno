import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Random;
import java.util.Scanner;

public class Uno {
    private Card current;
    private Deck deck;
    private ArrayList<Card> cardpile;
    private Scanner choice;
    private ArrayList<User> userArrayList;
    private Random random = new Random();
    private int playerNumber;
    private Scanner scanner = new Scanner(System.in);

    public Uno(ArrayList<User> users) {
        deck = new Deck();
        deck.shuffle();
        current = getStartingCard();
        cardpile = new ArrayList<Card>();
        cardpile.add(current);
        choice = new Scanner(System.in);
        userArrayList = users;
        distributecards();
    }

    public void play() {
        playerNumber = random.nextInt(userArrayList.size());
        boolean penallty;
        wh:
        while (true) {
            playerTurn();
            sendcards(current, 0);
            printusers(playerNumber);
            printcards();
            System.out.println("Current card:");
            System.out.println(current);
            System.out.println("=========================");
            if (userArrayList.get(playerNumber).isAdmin()) {
                if (current.isSpecial()) {
                    for (int i = 0; i < current.getSpecialValue(); i++) {
                        if (deck.isEmpty())
                            deck = new Deck(cardpile);
                        userArrayList.get(playerNumber).pickCards(deck.getTopCard());
                    }
                    Main.clearConsole();
                    printusers(playerNumber);
                    printcards();
                    System.out.println("Current card:");
                    System.out.println(current);
                    System.out.println("=========================");
                    System.out.println("\u001B[31m" + "Your penalty is " + current.getSpecialValue() + " extra card!");
                }
                boolean sw = true;
                boolean pick = false;
                while (sw) {
                    System.out.println("\u001B[0m" + "Pick up the card! (EX: (Red 2) or (4) or (pick) to puck up card from deck)");
                    String cardnumber = scanner.nextLine();
                    String[] splitestring = cardnumber.split(" ");
                    Card card = null;
                    try {
                        if (splitestring.length == 1) {
                            if (splitestring[0].equalsIgnoreCase("pick")) {
                                pick = true;
                                if (deck.isEmpty())
                                    deck = new Deck(cardpile);
                                userArrayList.get(playerNumber).pickCards(deck.getTopCard());
                            } else {
                                for (int i = 0; i < userArrayList.get(playerNumber).getPlayercards().size(); i++) {
                                    if (userArrayList.get(playerNumber).getPlayercards().get(i).isSpecial() &
                                            userArrayList.get(playerNumber).getPlayercards().get(i).getSpecialValue() == Integer.parseInt(splitestring[0])) {
                                        card = userArrayList.get(playerNumber).getPlayercards().remove(i);
                                    }
                                }
                            }
                        } else if (splitestring.length == 2) {
                            for (int i = 0; i < userArrayList.get(playerNumber).getPlayercards().size(); i++) {
                                if (userArrayList.get(playerNumber).getPlayercards().get(i).getValue() == Integer.parseInt(splitestring[1]) &
                                        userArrayList.get(playerNumber).getPlayercards().get(i).getColor().equalsIgnoreCase(splitestring[0]) &
                                        (current.getValue() == Integer.parseInt(splitestring[1]) |
                                                current.getColor().equalsIgnoreCase(splitestring[0]) |
                                                current.isSpecial()
                                        )) {
                                    card = userArrayList.get(playerNumber).getPlayercards().remove(i);
                                }
                            }
                        }
                    } catch (Exception e) {
                    }
                    if (card == null) {
                        Main.clearConsole();
                        printusers(playerNumber);
                        printcards();
                        System.out.println("Current card:");
                        System.out.println(current);
                        if (!pick) {
                            System.out.println("=========================");
                            System.out.println("\u001B[31m" + "Chosen card is incorrect!");
                            System.out.println("\u001B[0m" + "=========================");
                        } else pick = false;
                    } else {
                        sw = !sw;
                        current = card;
                        cardpile.add(card);
                    }
                }
            } else {
                System.out.println("Wair for players!");
                while (getmassage(playerNumber).equalsIgnoreCase("pick")) {
                    if (deck.isEmpty())
                        deck = new Deck(cardpile);
                    Card pick = deck.getTopCard();
                    userArrayList.get(playerNumber).pickCards(pick);
                    sendPickCard(pick);
                }
                current = getcard(playerNumber);
                cardpile.add(current);
            }

            if (isWin()) {
                String winner = getWin();
                sendWinner(winner);
                for (int i = 0; i < userArrayList.size(); i++) {
                    if (userArrayList.get(i).isAdmin())
                        if (userArrayList.get(i).getName().equalsIgnoreCase(winner)) {
                            System.out.println("\u001B[32m" + "Congratulations you won!");
                            System.out.println("\u001B[0m" + "Please press key to back to menu...");
                            scanner.nextLine();
                            break wh;
                        }
                }
                System.out.println("\u001B[32m" + winner + " won!");
                System.out.println("\u001B[0m" + "Please press key to back to menu...");
                scanner.nextLine();
                break wh;
            }
            sendWinner(false);
            playerNumber++;
            playerNumber = playerNumber % userArrayList.size();
        }
    }

    private Card getStartingCard() {
        Card temp = deck.peek();
        while (temp.isSpecial()) {
            deck.shuffle();
            temp = deck.peek();
        }
        temp = deck.getTopCard();
        return temp;
    }

    private void distributecards() {
        for (int i = 0; i < userArrayList.size(); i++)
            for (int j = 0; j < 7; j++)
                userArrayList.get(i).pickCards(deck.getTopCard());
    }


    private void printusers(int playerNumber) {
        Main.clearConsole();
        System.out.println("Players:");
        for (int i = 0; i < userArrayList.size(); i++) {
            System.out.print(userArrayList.get(i) + " ");
            if (i == playerNumber)
                System.out.println("*");
            else System.out.println("");
        }
        System.out.println("=========================");
        System.out.println("");
    }


    private void printcards() {
        for (int i = 0; i < userArrayList.size(); i++) {
            if (userArrayList.get(i).isAdmin())
                userArrayList.get(i).showCards();
        }
    }

    public void playerTurn() {
        for (int i = 0; i < userArrayList.size(); i++) {
            try {
                if (userArrayList.get(i).isAdmin()) continue;
                ObjectOutputStream objectOutputStream = userArrayList.get(i).getObjectOutputStream();
                objectOutputStream.writeObject(userArrayList.get(playerNumber).getName());
                objectOutputStream.flush();
            } catch (IOException e) {
//                e.printStackTrace();
                System.out.println("Player left! Please run game again");
                return;
            }
        }
    }


    public void sendcards(Card current, int sw) {
        if (sw == 1) {
            for (int i = 0; i < userArrayList.size(); i++) {
                if (userArrayList.get(i).isAdmin())
                    continue;
                try {
                    ArrayList<Card> cardArrayList = userArrayList.get(i).getPlayercards();
                    ObjectOutputStream objectOutputStream = userArrayList.get(i).getObjectOutputStream();
                    objectOutputStream.writeObject(cardArrayList);
                    objectOutputStream.flush();
                } catch (Exception e) {
//                    e.printStackTrace();
                    System.out.println("Network Error");
                }
            }
        } else {
            for (int i = 0; i < userArrayList.size(); i++) {
                try {
                    ObjectOutputStream objectOutputStream = userArrayList.get(i).getObjectOutputStream();
                    objectOutputStream.writeObject(current);
                    objectOutputStream.flush();
                } catch (IOException e) {
//                    e.printStackTrace();
                    System.out.println("Network Error");
                }
            }
        }
    }

    private void sendPickCard(Card card) {
        try {
            ObjectOutputStream objectOutputStream = userArrayList.get(playerNumber).getObjectOutputStream();
            objectOutputStream.writeObject(card);
            objectOutputStream.flush();
        } catch (IOException e) {
//            e.printStackTrace();
            System.out.println("Network Error");
        }
    }

    public Card getcard(int playernumber) {
        try {
            ObjectInputStream objectInput = userArrayList.get(playernumber).getObjectInputStream();
            Object object = objectInput.readObject();
            current = (Card) object;
            return current;
        } catch (Exception e) {
//            e.printStackTrace();
            System.out.println("Getcards error");
        }
        return null;
    }

    private String getmassage(int playerNumber) {
        try {
            ObjectInputStream objectInput = userArrayList.get(playerNumber).getObjectInputStream();
            return (String) objectInput.readObject();
        } catch (Exception e) {
//            e.printStackTrace();
            System.out.println("GetAck error");
        }
        return null;
    }

    private boolean isWin() {
        for (int i = 0; i < userArrayList.size(); i++) {
            if (userArrayList.get(i).getPlayercards().size() == 0) {
                return true;
            }
        }
        return false;
    }

    private String getWin() {
        for (int i = 0; i < userArrayList.size(); i++) {
            if (userArrayList.get(i).getPlayercards().size() == 0) {
                return userArrayList.get(i).getName();
            }
        }
        return "";
    }

    private void sendWinner(String name) {
        for (int i = 0; i < userArrayList.size(); i++) {
            if (userArrayList.get(i).isAdmin()) continue;
            try {
                ObjectOutputStream objectOutputStream = userArrayList.get(i).getObjectOutputStream();
                objectOutputStream.writeObject(name);
                objectOutputStream.flush();
            } catch (IOException e) {
//            e.printStackTrace();
                System.out.println("One player Left");
            }
        }
    }

    private void sendWinner(boolean bool) {
        for (int i = 0; i < userArrayList.size(); i++) {
            if (userArrayList.get(i).isAdmin()) continue;
            try {
                ObjectOutputStream objectOutputStream = userArrayList.get(i).getObjectOutputStream();
                objectOutputStream.writeObject(bool);
                objectOutputStream.flush();
            } catch (IOException e) {
//        e.printStackTrace();
                System.out.println("One player Left");
            }
        }
    }

}