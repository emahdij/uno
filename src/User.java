import java.io.Serializable;
import java.net.Socket;
import java.util.ArrayList;

public class User implements Serializable {
    private String name;
    private Socket socket;
    private boolean admin;
    private ArrayList<Card> playercards = new ArrayList<Card>();


    public User(Socket socket, String name) {
        this.socket = socket;
        this.name = name;
    }

    public User(String name, boolean admin) {
        this.name = name;
        this.admin = admin;
    }

    public String getName() {
        return name;
    }

    public Socket getSocket() {
        return socket;
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

    public Card throwCard(int c) {
        return playercards.remove(c);
    }

    public void showCards() {
        String[] card = {" ----- ", "|     |", "|     |", " ----- "};
        String c = "";
        for (int i = 0; i < card.length; i++) {
            for (int j = 0; j < playercards.size(); j++) {
                if (!playercards.get(j).isSpecial()) {
                    if (i == 1) {
                        c = c + "| " + playercards.get(j).getColor() + " |" + " ";
                    } else if (i == 2) {
                        c = c + "|  " + playercards.get(j).getValue() + "  |" + " ";
                    } else {
                        c = c + card[i] + " ";
                    }
                } else {
                    if (i == 1) {
                        c = c + "| " + "+" + playercards.get(j).getValue() + "  |" + " ";
                    } else {
                        c = c + card[i] + " ";
                    }
                }
            }
            c += "\n";
        }
        System.out.print(c);
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


}
