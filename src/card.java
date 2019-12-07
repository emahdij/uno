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
        String color = "";
        if (this.getColor().equals("Red"))
            color = "\u001B[31m";
        else if (this.getColor().equals("Blu"))
            color = "\u001B[34m";
        else if (this.getColor().equals("Grn"))
            color = "\u001B[32m";
        else if (this.getColor().equals("Ylw"))
            color = "\u001B[33m";
        else color = "\u001B[0m";
        for (int i = 0; i < card.length; i++) {
            for (int j = 0; j < 1; j++) {
                if (!this.isSpecial()) {
                    if (i == 1)
                        c += color + "| " + this.getColor() + " |" + " ";
                    else if (i == 2)
                        c += color + "|  " + this.getValue() + "  |" + " ";
                    else c += color + card[i] + " ";
                } else {
                    if (i == 1)
                        c += color + "| " + "+" + this.getValue() + "  |" + " ";
                    else
                        c += color + card[i] + " ";
                }
            }
            c += "\u001B[0m" + "\n";
        }
        return c;
    }

    public int getSpecialValue() {
        return specialValue;
    }
}