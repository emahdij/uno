import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectOutputStream;
import java.net.*;
import java.util.ArrayList;

public class Server {
    private String servername;
    private String adminname;
    private int capacity;
    private Reserve reserve = new Reserve();
    private Thread threads;
    private ArrayList<User> userArrayList;
    private InetAddress ip;

    public Server(String servername, String adminname, int capacity, InetAddress ip) {
        this.servername = servername;
        this.capacity = capacity;
        this.adminname = adminname;
        this.ip = ip;
    }

    public Server(String servername, String adminname, int capacity, ArrayList<User> userArrayList) {
        this.servername = servername;
        this.adminname = adminname;
        this.capacity = capacity;
        this.userArrayList = userArrayList;
        reserve.setReserve(1);
    }

    public String getServername() {
        return servername;
    }

    public InetAddress getIp() {
        return ip;
    }


    public void start() {
        userArrayList.add(new User(adminname, true));
        try {
            broadcast();
            listen();
            negotiation();
        } catch (Exception e) {
            System.out.println("Listan problem");
        }
    }


    private void broadcast() {
        threads = new Thread_broadcast(reserve, servername, adminname, capacity);
        threads.start();
    }


    private void listen() throws Exception {
        ServerSocket serverSocket = new ServerSocket(4778);
        while (reserve.getReserve() < capacity) {
            System.out.println("server waiting");
            Socket socket = serverSocket.accept();
            reserve.setReserve(reserve.getReserve() + 1);
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            String clinetname = in.readLine();
            User user = new User(socket, clinetname);
            userArrayList.add(user);
            System.out.println("user " + user.getName() + " connected");
        }
        threads.stop();
        System.out.println("users connected \n the game already started.");
    }


    private void negotiation() {
        client:
        for (int i = 0; i < userArrayList.size(); i++) {
            ArrayList<User> userArray = new ArrayList<User>();
            array:
            for (int j = 0; j < userArrayList.size(); j++) {
                if (i == j) continue array;
                if (userArrayList.get(i).isAdmin()) continue client;
                userArray.add(userArrayList.get(j));
            }

            try {
                Socket socket = userArrayList.get(i).getSocket();
                ObjectOutputStream objectOutputStream = new ObjectOutputStream(socket.getOutputStream());
                objectOutputStream.writeObject(userArray);
                objectOutputStream.flush();
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    }

}