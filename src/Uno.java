import java.io.IOException;
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
        playerTurn();
        printusers(playerNumber);
        printcards();

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
        System.out.println("Your cards:");
        for (int i = 0; i < userArrayList.size(); i++) {
            if (userArrayList.get(i).isAdmin())
                userArrayList.get(i).showCards();
        }
        System.out.println("");
    }

    public void playerTurn() {
        for (int i = 0; i < userArrayList.size(); i++) {
            try {
                if (userArrayList.get(i).isAdmin()) continue;
                Socket socket = userArrayList.get(i).getSocket();
                ObjectOutputStream objectOutputStream = new ObjectOutputStream(socket.getOutputStream());
                objectOutputStream.writeObject(userArrayList.get(playerNumber).getName());
                objectOutputStream.flush();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


}