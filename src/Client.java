import java.io.DataOutputStream;
import java.io.ObjectInputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.Socket;
import java.util.ArrayList;


public class Client {
    private ArrayList<Server> serverArrayList = new ArrayList<Server>();
    private ArrayList<User> userArrayList;
    private Socket socket;
    private String username;

    public void search(int port, int time) {
        DatagramSocket socket = null;
        try {
            socket = new DatagramSocket(port, InetAddress.getByName("0.0.0.0"));
            socket.setBroadcast(true);
        } catch (Exception e) {
            e.printStackTrace();
//            System.out.println("searching in network problem");
        }
        long now = System.currentTimeMillis();
        long end = now + time;
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
                    if (serverArrayList.get(i).getServername().equals(message[2])) {
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
                e.printStackTrace();
//                System.out.println("Searching in network problem");
            }
        }
        socket.close();
    }

    public Socket connect(String nameserver, String username) throws Exception {
        for (int i = 0; i < serverArrayList.size(); i++) {
            if (serverArrayList.get(i).getServername().equals(nameserver)) {
                socket = new Socket(serverArrayList.get(i).getIp(), 4778);
                OutputStream outstream = socket.getOutputStream();
                PrintWriter out = new PrintWriter(outstream);
//                System.out.println(username);
                out.println(username);
                out.flush();
            }
        }
        return null;
    }

    public ArrayList<User> getusers() {
        try {
            ObjectInputStream objectInput = new ObjectInputStream(socket.getInputStream());
            Object object = objectInput.readObject();
            userArrayList = (ArrayList<User>) object;
            return userArrayList;
        } catch (Exception e) {
            e.printStackTrace();
//            System.out.println("Getusers errot");
        }
        return null;
    }


}
