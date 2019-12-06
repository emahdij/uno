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
        int i = 0;
        while (i < 10) {
            playerTurn();
            sendcards(true);
            printusers(playerNumber);
            printcards();
            System.out.println("Current card:");
            System.out.println(current);
            getcard(playerNumber);
            playerNumber++;
            playerNumber = playerNumber % userArrayList.size();
            i++;
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
                Socket socket = userArrayList.get(i).getSocket();
                ObjectOutputStream objectOutputStream = new ObjectOutputStream(socket.getOutputStream());
                objectOutputStream.writeObject(userArrayList.get(playerNumber).getName());
                objectOutputStream.flush();
            } catch (IOException e) {
                e.printStackTrace();
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
                    Socket socket = userArrayList.get(i).getSocket();
                    ObjectOutputStream objectOutputStream = new ObjectOutputStream(socket.getOutputStream());
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
                    Socket socket = userArrayList.get(i).getSocket();
                    ObjectOutputStream objectOutputStream = new ObjectOutputStream(socket.getOutputStream());
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
            ObjectInputStream objectInput = new ObjectInputStream(userArrayList.get(playernumber).getSocket().getInputStream());
            Object object = objectInput.readObject();
            current = (Card) object;
        } catch (Exception e) {
//            e.printStackTrace();
            System.out.println("Getcards error");
        }

    }


}