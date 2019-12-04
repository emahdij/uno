import java.io.Serializable;

class Card implements Serializable {

    private String color;
    private int value;
    private int specialValue;
    private boolean special;

    public Card(int value, String color) {
        this.color = color;
        this.value = value;
        this.specialValue = 0;
        this.special = false;
    }

    public Card(int specialValue) {
        this.color = "";
        this.specialValue = specialValue;
        this.value = 0;
        this.special = true;
    }

    public String getColor() {
        return this.color;
    }

    public int getValue() {
        return (this.special) ? this.specialValue : this.value;
    }

    public boolean isSpecial() {
        return (special) ? true : false;
    }

    public String toString() {
        String[] card = {" ----- ", "|     |", "|     |", " ----- "};
        String c = "";
        for (int i = 0; i < card.length; i++) {
            for (int j = 0; j < 1; j++) {
                if (!this.isSpecial()) {
                    if (i == 1) {
                        c = c + "| " + this.getColor() + " |" + " ";
                    } else if (i == 2) {
                        c = c + "|  " + this.getValue() + "  |" + " ";
                    } else {
                        c = c + card[i] + " ";
                    }
                } else {
                    if (i == 1) {
                        c = c + "| " + "+" + this.getValue() + "  |" + " ";
                    } else {
                        c = c + card[i] + " ";
                    }
                }
            }
            c += "\n";
        }
        return c;
    }

}