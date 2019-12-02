import java.io.Serializable;
import java.net.Socket;

public class User implements Serializable {
    private Socket socket;
    private String name;
    private boolean admin;

    public User(Socket socket, String name) {
        this.socket = socket;
        this.name = name;
    }

    public User(String name, boolean admin) {
        this.name = name;
        this.admin = admin;
    }

    public String getName() {
        return name;
    }

    public Socket getSocket() {
        return socket;
    }

    public boolean isAdmin() {
        return admin;
    }
}
