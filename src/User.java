import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;

public class User {
    private String name;
    private boolean admin = false;
    private ArrayList<Card> playercards = new ArrayList<Card>();
    private static ObjectOutputStream objectOutputStream;
    private static ObjectInputStream objectInputStream;


    public User(ObjectInputStream objectInputStream, ObjectOutputStream objectOutputStream, String name) {
        this.name = name;
        try {
            this.objectInputStream = objectInputStream;
            this.objectOutputStream = objectOutputStream;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public User(String name, boolean admin) {
        this.name = name;
        this.admin = admin;
    }

    public String getName() {
        return name;
    }


    public boolean isAdmin() {
        return admin;
    }

    public int numberOfCards() {
        return playercards.size();
    }

    public ArrayList<Card> PlayerCards() {
        return playercards;
    }

    public void pickCards(Card c) {
        playercards.add(c);
    }


    public void showCards() {
        System.out.println("Your cards:");
        String[] card = {" -----   ", "|     |", "|     |", " -----   "};
        String c = "";
        for (int i = 0; i < card.length; i++) {
            for (int j = 0; j < playercards.size(); j++) {
                String color = "";
                if (playercards.get(j).getColor().equals("Red"))
                    color = "\u001B[31m";
                else if (playercards.get(j).getColor().equals("Blu"))
                    color = "\u001B[34m";
                else if (playercards.get(j).getColor().equals("Grn"))
                    color = "\u001B[32m";
                else if (playercards.get(j).getColor().equals("Ylw"))
                    color = "\u001B[33m";
                else color = "\u001B[0m";
                if (!playercards.get(j).isSpecial()) {
                    if (i == 1) {
                        c += color + "| " + playercards.get(j).getColor() + " |" + "   ";
                    } else if (i == 2) {
                        c += color + "|  " + playercards.get(j).getValue() + "  |" + "   ";
                    } else {
                        c += color + card[i] + " ";
                    }
                } else {
                    if (i == 0 | i == 3)
                        c += color + card[i] + " ";
                    else if (i == 1)
                        c += color + "| " + "+" + playercards.get(j).getValue() + "  |" + "   ";
                    else
                        c += color + card[i] + "   ";
                }
            }
            c += "\u001B[0m" + "\n";
        }
        System.out.print(c);
        System.out.println("");
    }

    public void setPlayercards(ArrayList<Card> playercards) {
        this.playercards = playercards;
    }

    public ArrayList<Card> getPlayercards() {
        return playercards;
    }

    public boolean hasWon() {
        return (playercards.size() == 0) ? true : false;
    }

    public String toString() {
        return this.name;
    }

//    public void sayUno() {
//        Scanner s = new Scanner(System.in);
//        if (playercards.size() == 1) {
//            System.out.println("Uno");
//            System.out.println("Press Enter...");
//            s.nextLine();
//        }
//    }


    public ObjectOutputStream getObjectOutputStream() {
        return objectOutputStream;
    }

    public ObjectInputStream getObjectInputStream() {
        return objectInputStream;
    }
}
