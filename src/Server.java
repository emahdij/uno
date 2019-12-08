import java.io.*;
import java.net.*;
import java.util.ArrayList;
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
            ObjectInputStream objectInputStream = new ObjectInputStream(socket.getInputStream());
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(socket.getOutputStream());
            boolean sw = true;
            String clinetname = (String) objectInputStream.readObject();
            while (sw) {
                if (checkDuplicate(clinetname)) {
                    objectOutputStream.writeObject(new Boolean(false));
                    objectOutputStream.flush();
                    clinetname = (String) objectInputStream.readObject();
                } else {
                    objectOutputStream.writeObject(new Boolean(true));
                    objectOutputStream.flush();
                    sw = !sw;
                }
            }
            reserve.setReserve(reserve.getReserve() + 1);
            User user = new User(objectInputStream, objectOutputStream, clinetname);
//            System.out.println("user " + user.getName() + " " + socket.getPort());
            userArrayList.add(user);
            System.out.println("User " + user.getName() + " connected!");
        }
        threads.stop();
        System.out.println("Users connected \nThe game is about to start...");
    }

    private boolean checkDuplicate(String name) {
        for (int i = 0; i < userArrayList.size(); i++) {
            if (userArrayList.get(i).getName().equalsIgnoreCase(name))
                return true;
        }
        return false;
    }


    private void negotiation() {
        ArrayList<String> userArray = new ArrayList<String>();
        for (int j = 0; j < userArrayList.size(); j++) {
            userArray.add(userArrayList.get(j).getName());
        }
        for (int i = 0; i < userArrayList.size(); i++) {
            if (userArrayList.get(i).isAdmin()) continue;
            try {
                ObjectOutputStream objectOutputStream = userArrayList.get(i).getObjectOutputStream();
                objectOutputStream.writeObject(userArray);
                objectOutputStream.flush();
            } catch (Exception e) {
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
                userArrayList.get(i).getObjectOutputStream().close();
                userArrayList.get(i).getObjectInputStream().close();
            }
        } catch (Exception e) {

        }
    }

}