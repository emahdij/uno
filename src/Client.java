import java.io.ObjectInputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
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
    private String username;

    public void start(int port, int time) throws Exception {
        search(port, time);
        connect();
        getusers();
        printusers();
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
                    System.out.println("New server found:" + message[2] + " " + "capacity:" + message[4] + "/" + message[5]);
                    serverArrayList.add(new Server(message[2], message[3], Integer.parseInt(message[4]), packet.getAddress()));
                }
            } catch (ArrayIndexOutOfBoundsException e) {
                continue;
            } catch (Exception e) {
//                e.printStackTrace();
                System.out.println("Searching in network problem");
            }

        }
        socket.close();
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
                    System.out.print("Plese enter your username name:");
                    this.username = scanner.nextLine();
                    out.println(this.username);
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
            ObjectInputStream objectInput = new ObjectInputStream(socket.getInputStream());
            Object object = objectInput.readObject();
            userArrayList = (ArrayList<String>) object;
        } catch (Exception e) {
            e.printStackTrace();
//            System.out.println("Getusers errot");
        }
    }

    private void printusers() {
        Main.clearConsole();
        System.out.println("Player names:");
        for (int i = 0; i < userArrayList.size(); i++)
            System.out.println("| " + userArrayList.get(i) + " |");
    }
}
