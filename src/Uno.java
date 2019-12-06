import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Random;
import java.util.Scanner;

public class Uno {
    private Card current;
    private Deck deck;
    private ArrayList<Card> cardpile;
    private int penalty;
    private Scanner choice;
    private ArrayList<User> userArrayList;
    private int pick;
    private Random random = new Random();
    private int playerNumber;
    private Scanner scanner = new Scanner(System.in);

    public Uno(ArrayList<User> users) {
        deck = new Deck();
        deck.shuffle();
        penalty = 0;
        current = getStartingCard();
        cardpile = new ArrayList<Card>();
        cardpile.add(current);
        choice = new Scanner(System.in);
        userArrayList = users;
        distributecards();
    }

    public void play() {
        playerNumber = random.nextInt(userArrayList.size());
        wh:
        while (true) {
            playerTurn();
            sendcards(true);
            printusers(playerNumber);
            printcards();
            System.out.println("Current card:");
            System.out.println(current);
            System.out.println("=========================");

            if (userArrayList.get(playerNumber).isAdmin()) {
                boolean sw = true;
                while (sw) {
                    System.out.println("Pick up the card! (EX: 2 ReD or 4)");
                    String cardnumber = scanner.nextLine();
                    String[] splitestring = cardnumber.split(" ");
                    Card card = null;
                    try {
                        int indx = 0;
                        for (int i = 0; i < userArrayList.size(); i++) {
                            if (userArrayList.get(i).isAdmin()) {
                                indx = i;
                                break;
                            }
                        }
                        if (splitestring.length == 1) {
                            for (int i = 0; i < userArrayList.get(indx).getPlayercards().size(); i++) {
                                if (userArrayList.get(indx).getPlayercards().get(i).isSpecial() & current.isSpecial() &
                                        userArrayList.get(indx).getPlayercards().get(i).getValue() == Integer.parseInt(splitestring[0])) {
                                    card = userArrayList.get(indx).getPlayercards().remove(i);
                                }
                            }
                        } else if (splitestring.length == 2) {
                            for (int i = 0; i < userArrayList.get(indx).getPlayercards().size(); i++) {
                                if (!userArrayList.get(indx).getPlayercards().get(i).isSpecial() & !current.isSpecial() &
                                        userArrayList.get(indx).getPlayercards().get(i).getValue() == Integer.parseInt(splitestring[1]) &
                                        userArrayList.get(indx).getPlayercards().get(i).getColor().equalsIgnoreCase(splitestring[0]) &
                                        (current.getValue() == Integer.parseInt(splitestring[1]) |
                                                current.getColor().equalsIgnoreCase(splitestring[0]))) {
                                    card = userArrayList.get(indx).getPlayercards().remove(i);
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
                        System.out.println("=========================");
                        System.out.println("\u001B[31m" + "Chosen card is incorrect!");
                        System.out.println("\u001B[0m" + "=========================");
                    } else {
                        sw = !sw;
                        current = card;
                    }
                }
            } else {
                System.out.println("Wair for player!");
                getcard(playerNumber);
            }
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


    public void sendcards(boolean current) {
        if (!current) {
            for (int i = 0; i < userArrayList.size(); i++) {
                if (userArrayList.get(i).isAdmin())
                    continue;
                try {
                    ArrayList<Card> cardArrayList = userArrayList.get(i).getPlayercards();
                    ObjectOutputStream objectOutputStream = userArrayList.get(i).getObjectOutputStream();
                    objectOutputStream.writeObject(cardArrayList);
                    objectOutputStream.flush();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        } else {
            for (int i = 0; i < userArrayList.size(); i++) {
                if (userArrayList.get(i).isAdmin())
                    continue;
                try {
                    ObjectOutputStream objectOutputStream = userArrayList.get(i).getObjectOutputStream();
                    objectOutputStream.writeObject(this.current);
                    objectOutputStream.flush();
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
        }
    }

    public void getcard(int playernumber) {
        try {
            ObjectInputStream objectInput = userArrayList.get(playernumber).getObjectInputStream();
            Object object = objectInput.readObject();
            current = (Card) object;
        } catch (Exception e) {
            e.printStackTrace();
//            System.out.println("Getcards error");
        }

    }


}