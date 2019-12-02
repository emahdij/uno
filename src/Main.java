import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) throws UnknownHostException, SocketException {
        Scanner scanner = new Scanner(System.in);
        if (scanner.nextLine().equals("server")) {
            ArrayList<User> userArrayList = new ArrayList<User>();
            Server server = new Server("test", "adminname", 3, userArrayList);
            server.start();
        } else {
            Client client = new Client();
            client.search(4778, 3000);
            try {
                client.connect("test", "mehdi");
                ArrayList<User> userArrayList;
                userArrayList = client.getusers();
                for (int i = 0; i < userArrayList.size(); i++) {
                    System.out.println("=========================");
                    System.out.println(userArrayList.get(i).getName());
                }
            } catch (Exception e) {
                System.out.println("Network Error");
            }
        }

    }
}
