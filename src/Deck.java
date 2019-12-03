import java.util.ArrayList;
import java.util.Collections;

public class Deck {

    private ArrayList<Card> deck;

    public Deck() {
        deck = new ArrayList<Card>();
        String[] colors = {"Red", "Blu", "Grn", "Ylw"};
        int[] numbers = {1, 1, 2, 2, 3, 3, 4, 4, 5, 5, 6, 6, 7, 7, 8, 8, 9, 9, 0};
        int[] specialnumbers = {2, 2, 4, 4};

        for (String c : colors)
            for (int i : numbers)
                deck.add(new Card(i, c));
        for (int i : specialnumbers)
            deck.add(new Card(i));
    }

    public Deck(ArrayList<Card> c) {
        deck = c;
    }

    public boolean isEmpty() {
        return (deck.size() > 0) ? false : true;
    }

    public void shuffle() {
        Collections.shuffle(deck);
    }

    public Card getTopCard() {
        return deck.remove(deck.size() - 1);
    }

    public Card peek() {
        return deck.get(deck.size() - 1);
    }

    public String toString() {
        String deck = "";
        for (Card c : this.deck)
            deck += c + " ";
        return deck;
    }

}