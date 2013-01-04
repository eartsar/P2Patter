import edu.rit.ds.registry.RegistryProxy;
import edu.rit.ds.registry.AlreadyBoundException;
import java.rmi.server.UnicastRemoteObject;
import java.rmi.NoSuchObjectException;
import java.rmi.RemoteException;
import java.util.Date;
import java.text.SimpleDateFormat;
import java.util.TreeSet;


public class Microblog implements BlogRef {

    private String registry_host;
    private int registry_port;
    private RegistryProxy registry;
    private String name;
    private int last_id;

    private TreeSet<Message> messages;

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

        this.messages = new TreeSet<Message>();
        this.last_id = 0;

        // Distributed setup!
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
            e.printStackTrace();
        }
    }


    public Message addMessage(String content, long timestamp) {
        int id = last_id + 1;
        last_id ++;
        Message message = new Message(id, timestamp, this.name, content);
        this.messages.add(message);
        return message;
    }

    public Message removeMessage(int id) {
        Message to_remove = null;
        for (Message message : messages) {
            if (message.getId() == id) {
                to_remove = message;
                break;
            }
        }
        messages.remove(to_remove);
        return to_remove;
    }

}