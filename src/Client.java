import java.io.*;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Scanner;


public class Client {
    private Scanner scanner = new Scanner(System.in);
    private ArrayList<Server> serverArrayList = new ArrayList<Server>();
    private ArrayList<String> userArrayList;
    private Socket socket;
    private User user;
    private String playerturn;
    private Card current;
    private static ObjectOutputStream objectOutputStream;
    private static ObjectInputStream objectInputStream;


    public void start(int port, int time) throws Exception {
        search(port, time);
        connect();
        try {
            objectOutputStream = new ObjectOutputStream(socket.getOutputStream());
            objectInputStream = new ObjectInputStream(socket.getInputStream());
        } catch (Exception e) {
            e.printStackTrace();
        }
        getusers();
        getcards(false);
        getturnuser();
        wh:
        while (true) {
            Main.clearConsole();
            current = getcards(true);
            printusers(playerturn);
            user.showCards();
            System.out.println("Current card:");
            System.out.println(current.toString());
            System.out.println("=========================");
            if (playerturn.equals(user.getName())) {
                boolean sw = true;
                while (sw) {
                    System.out.println("Pick up the card! (EX: (2 ReD) or (4) or (Pick) to puck up card from deck)");
                    String cardnumber = scanner.nextLine();
                    String[] splitestring = cardnumber.split(" ");
                    Card card = null;
                    try {
                        if (splitestring.length == 1) {
                            if (splitestring[1].equalsIgnoreCase("Pick")) {
                                sendAcc("pick");
                                user.getPlayercards().add(getcards(true));
                            } else {
                                for (int i = 0; i < user.getPlayercards().size(); i++) {
                                    if (user.getPlayercards().get(i).isSpecial() & current.isSpecial() &
                                            user.getPlayercards().get(i).getValue() == Integer.parseInt(splitestring[0])) {
                                        card = user.getPlayercards().remove(i);
                                        break;
                                    }
                                }
                            }
                        } else if (splitestring.length == 2) {
                            for (int i = 0; i < user.getPlayercards().size(); i++) {
                                if (!user.getPlayercards().get(i).isSpecial() & !current.isSpecial() &
                                        user.getPlayercards().get(i).getValue() == Integer.parseInt(splitestring[1]) &
                                        user.getPlayercards().get(i).getColor().equalsIgnoreCase(splitestring[0]) &
                                        (current.getValue() == user.getPlayercards().get(i).getValue() |
                                                current.getColor().equalsIgnoreCase(splitestring[0]))) {
                                    card = user.getPlayercards().remove(i);
                                    break;
                                }
                            }
                        }
                    } catch (Exception e) {
                    }
                    if (card == null) {
                        Main.clearConsole();
                        printusers(playerturn);
                        user.showCards();
                        System.out.println("Current card:");
                        System.out.println(current.toString());
                        System.out.println("=========================");
                        System.out.println("\u001B[31m" + "Chosen card is incorrect!");
                        System.out.println("\u001B[0m" + "=========================");
                    } else {
                        sw = !sw;
                        current = card;
                        sendAcc("ok");
                        sendcard(card);
                    }
                }
            }
            System.out.println("Waite for players!");
            getturnuser();
        }
    }

    private void search(int port, int time) {
        DatagramSocket socket = null;
        try {
            socket = new DatagramSocket(port, InetAddress.getByName("0.0.0.0"));
            socket.setBroadcast(true);
        } catch (Exception e) {
//            e.printStackTrace();
            System.out.println("searching in network problem");
        }
        long now = System.currentTimeMillis();
        long end = now + time;
        System.out.println("Searching...");
        while (System.currentTimeMillis() < end) {
            try {
                byte[] recvBuf = new byte[1500];
                DatagramPacket packet = new DatagramPacket(recvBuf, recvBuf.length);
                socket.receive(packet);
                String tmp = new String(packet.getData()).trim();
                String[] message = tmp.split(" ");
                if (message.length == 6 && !message[1].equals("BROADCAST"))
                    continue;
                boolean duplicated = false;
                for (int i = 0; i < serverArrayList.size(); i++) {
                    if (serverArrayList.get(i).getServername().equals(message[2]) &&
                            serverArrayList.get(i).getIp().equals(packet.getAddress())) {
                        duplicated = true;
                        break;
                    }
                }
                if (!duplicated) {
                    System.out.println("New server found: " + message[2] + " " + "capacity:" + message[4] + "/" + message[5]);
                    serverArrayList.add(new Server(message[2], message[3], Integer.parseInt(message[4]), packet.getAddress()));
                }
            } catch (ArrayIndexOutOfBoundsException e) {
                continue;
            } catch (Exception e) {
//                e.printStackTrace();
                System.out.println("Searching in network problem");
            }
        }
    }

    private void connect() throws Exception {
        String nameserver;
        System.out.print("Plese enter server name:");
        nameserver = scanner.nextLine();
        boolean sw = true;
        while (sw) {
            for (int i = 0; i < serverArrayList.size(); i++) {
                if (serverArrayList.get(i).getServername().equalsIgnoreCase(nameserver)) {
                    socket = new Socket(serverArrayList.get(i).getIp(), 4778);
                    OutputStream outstream = socket.getOutputStream();
                    PrintWriter out = new PrintWriter(outstream);
                    System.out.print("Plese enter your username:");
                    String username = scanner.nextLine();
                    user = new User(username, false);
                    out.println(username);
                    out.flush();
                    sw = false;
                }
            }
            if (socket == null) {
                System.out.println("\u001B[31m" + "Wrong server name!");
                System.out.print("\u001B[0m" + "Plese enter server name:");
                nameserver = scanner.nextLine();
            }
        }
        System.out.println("\u001B[0m" + "Waiting for other players...");
    }

    private void getusers() {
        try {
            ObjectInputStream objectInput = objectInputStream;
            Object object = objectInput.readObject();
            userArrayList = (ArrayList<String>) object;
        } catch (Exception e) {
            e.printStackTrace();
//            System.out.println("Getusers errot");
        }
    }

    private void printusers(String player) {
        Main.clearConsole();
        System.out.println("Players:");
        for (int i = 0; i < userArrayList.size(); i++) {
            System.out.print(userArrayList.get(i) + " ");
            if (userArrayList.get(i).equals(player))
                System.out.println("*");
            else System.out.println("");
        }
        System.out.println("=========================");
        System.out.println("");
    }

    private Card getcards(boolean current) {
        if (!current) {
            try {
                ObjectInputStream objectInput = objectInputStream;
                Object object = objectInput.readObject();
                user.setPlayercards((ArrayList<Card>) object);
                return null;
            } catch (Exception e) {
                e.printStackTrace();
//                System.out.println("Getcards error");
            }
        } else {
            try {
                ObjectInputStream objectInput = objectInputStream;
                Object object = objectInput.readObject();
                return (Card) object;
            } catch (Exception e) {
                e.printStackTrace();
//                System.out.println("Getcards error");
            }
        }
        return null;
    }


    private void sendcard(Card card) {
        try {
            ObjectOutputStream objectOutputStream = this.objectOutputStream;
            objectOutputStream.writeObject(card);
            objectOutputStream.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void getturnuser() {
        try {
            ObjectInputStream objectInput = objectInputStream;
            Object object = objectInput.readObject();
            playerturn = (String) object;
        } catch (Exception e) {
            e.printStackTrace();
//            System.out.println("Getturn user error ");
        }
    }

    private void sendAcc(String ack) {
        try {
            ObjectOutputStream objectOutputStream = this.objectOutputStream;
            objectOutputStream.writeObject(ack);
            objectOutputStream.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


}
