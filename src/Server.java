import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectOutputStream;
import java.net.*;
import java.util.ArrayList;
import java.util.Random;
import java.util.Scanner;

public class Server {
    private String servername;
    private String adminname;
    private int capacity;
    private Reserve reserve = new Reserve();
    private Thread threads;
    private ArrayList<User> userArrayList = new ArrayList<User>();
    private InetAddress ip;
    private Uno uno;


    public Server(String servername, String adminname, int capacity, InetAddress ip) {
        this.servername = servername;
        this.capacity = capacity;
        this.adminname = adminname;
        this.ip = ip;
    }

    public Server() {
        reserve.setReserve(1);
        Scanner scanner = new Scanner(System.in);
        System.out.print("Please enter server name:");
        this.servername = scanner.nextLine();
        System.out.print("Please enter Admin name: ");
        this.adminname = scanner.nextLine();
        System.out.print("Please enter capacity of server(2/4): ");
        String in = scanner.nextLine();
        boolean sw = true;
        while (sw) {
            if (in.equals("2") | in.equals("4")) {
                this.capacity = Integer.parseInt(in);
                sw = false;
            } else {
                System.out.println("\u001B[31m" + "Wrong!");
                System.out.print("\u001B[0m" + "Please enter capacity of server(2/4): ");
                in = scanner.nextLine();
            }
        }
    }

    public void start() {
        userArrayList.add(new User(adminname, true));
        try {
            broadcast();
            listen();
            negotiation();
            uno = new Uno(userArrayList);
            uno.sendcards(null, 1);
            uno.play();
        } catch (Exception e) {
            e.printStackTrace();
//            System.out.println("Listan problem");
        }
    }

    private void broadcast() {
        threads = new Thread_broadcast(reserve, servername, adminname, capacity);
        threads.start();
    }

    private void listen() throws Exception {
        ServerSocket serverSocket = new ServerSocket(4778);
        while (reserve.getReserve() < capacity) {
            System.out.println("Server waiting...");
            Socket socket = serverSocket.accept();
            reserve.setReserve(reserve.getReserve() + 1);
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            String clinetname = in.readLine();
            User user = new User(socket, clinetname);
            userArrayList.add(user);
            System.out.println("User " + user.getName() + " connected!");
        }
        threads.stop();
        System.out.println("Users connected \nThe game already started...");
    }

    private void negotiation() {
        client:
        for (int i = 0; i < userArrayList.size(); i++) {
            ArrayList<String> userArray = new ArrayList<String>();
            array:
            for (int j = 0; j < userArrayList.size(); j++) {
                if (userArrayList.get(i).isAdmin()) continue client;
                userArray.add(userArrayList.get(j).getName());
            }
            try {
//                Socket socket = userArrayList.get(i).getSocket();
//                ObjectOutputStream objectOutputStream = new ObjectOutputStream(socket.getOutputStream());
                ObjectOutputStream objectOutputStream = userArrayList.get(i).getObjectOutputStream();
                objectOutputStream.writeObject(userArray);
                objectOutputStream.flush();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


    public String getServername() {
        return servername;
    }

    public InetAddress getIp() {
        return ip;
    }

    public void destroy() {
        try {
            for (int i = 0; i < userArrayList.size(); i++) {
                userArrayList.get(i).getSocket().close();
            }
        } catch (Exception e) {

        }
    }

}