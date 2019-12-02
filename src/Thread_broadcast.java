import java.net.*;

public class Thread_broadcast extends Thread {
    private Reserve reserve;
    private String name;
    private int capacity;
    private DatagramSocket socket;
    private String adminname;

    public Thread_broadcast(Reserve reserve, String name, String adminname, int capacity) {
        this.reserve = reserve;
        this.name = name;
        this.adminname = adminname;
        this.capacity = capacity;
    }

    @Override
    public void run() {
        try {
            socket = new DatagramSocket();
            socket.setBroadcast(true);
            while (true) {
                byte[] sendData = ("SERVER BROADCAST " + name + " " + adminname + " " + reserve.getReserve() + " " + capacity).getBytes();
                DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, InetAddress.getByName("255.255.255.255"), 4778);
                socket.send(sendPacket);
                sleep(1000);
            }
        } catch (Exception e) {
            System.out.println("Network error");
        }
    }
}