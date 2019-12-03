import java.util.ArrayList;
import java.util.Scanner;

public class Uno {
    private Card current;
    private Deck deck;
    private ArrayList<Card> cardpile;
    private int penalty;
    private Scanner choice;
    private ArrayList<User> userArrayList;
    private int pick;

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
        for (int i = 0; i < userArrayList.size(); i++) {
            userArrayList.get(i).showCards();
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


}