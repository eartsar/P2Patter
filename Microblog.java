import edu.rit.ds.registry.RegistryProxy;
import edu.rit.ds.registry.AlreadyBoundException;
import java.rmi.server.UnicastRemoteObject;
import java.rmi.NoSuchObjectException;
import java.rmi.RemoteException;
import java.util.Date;
import java.text.SimpleDateFormat;


public class Microblog implements BlogRef {

    private String registry_host;
    private int registry_port;
    private RegistryProxy registry;
    private String name;

    public Microblog(String[] args) {
        if (args.length < 3) {
            throw new IllegalArgumentException(
                "Error - Too few arguments given.\n" + 
                "Usage: java Start Microblog <host> <port> <name>"
            );
        }
        else if (args.length > 3) {
            throw new IllegalArgumentException(
                "Error - Too few arguments given.\n" + 
                "Usage: java Start Microblog <host> <port> <name>"
            );
        }

        // Set up this microblog's fields
        this.registry_host = args[0];
        this.name = args[2];

        try {
            this.registry_port = Integer.parseInt(args[1]);
        } catch( Exception e ) {
            throw new IllegalArgumentException(
                "Error - Port must be an integer.\n" + 
                "Usage: java Start Microblog <host> <port> <name>"
            ); 
        }

        try {
            // Get a proxy for the Registry Server
            registry = new RegistryProxy(registry_host, registry_port);

            // Export this MicroBlog
            UnicastRemoteObject.exportObject(this, 0);

            // Bind this MicroBlog into the Registry Server
            registry.bind (this.name, this);
        }
        catch (AlreadyBoundException e) {
            try {
                UnicastRemoteObject.unexportObject(this, true);
            }
            catch (NoSuchObjectException e2) {}
            throw new IllegalArgumentException(
                "Error - MicroBlog with name " + this.name + " already exists."
            );
        }
        catch (RemoteException e) {
            // TODO: Handle other remote exceptions on startup
        }

    }


    private class Message implements Comparable<Message> {
        private int id;
        private long time;
        private String author;
        private String content;

        public Message(int id, long time, String author, String content) {
            this.id = id;
            this.time = time;
            this.author = author;
            this.content = content;
        }

        public int compareTo(Message other) {
            if (this.time < other.time) { return -1; }
            else if (this.time > other.time) { return 1; }
            else if (this.author.compareTo(other.author) != 0) {
                return this.author.compareTo(other.author); 
            }
            else if (this.id < other.id) { return -1; }
            else if (this.id > other.id) { return 1; }
            else { return 1; }
        }

        public String toString() {
            Date date = new Date(time);
            SimpleDateFormat formatter = new SimpleDateFormat("yyyy/MM/dd H:mm:ss");
            String timestamp = formatter.format(date);
            String ret;
            ret = "--------------------------------------------------------------------------------";
            ret = ret + "\n" + author + " -- " + "Message " + id + " -- " + timestamp;
            ret = ret + "\n" + content;
            return ret;
        }
    }

}