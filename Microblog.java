import edu.rit.ds.registry.RegistryProxy;
import edu.rit.ds.registry.AlreadyBoundException;
import edu.rit.ds.Lease;
import edu.rit.ds.RemoteEventListener;
import edu.rit.ds.RemoteEventGenerator;
import java.rmi.server.UnicastRemoteObject;
import java.rmi.NoSuchObjectException;
import java.rmi.RemoteException;
import java.util.Date;
import java.text.SimpleDateFormat;
import java.util.TreeSet;
import java.util.Iterator;


/**
 * A distributed microblog object for the P2Patter project.
 * Usage: java Start Microblog <host> <port> <name>
 * 
 * @author Eitan Romanoff
 * @date   1/13/2013
 */
public class Microblog implements BlogRef {

    // registry variables
    private String registry_host;
    private int registry_port;
    private RegistryProxy registry;

    // blog fields
    private String name;
    private int last_id;
    private TreeSet<Message> messages;

    // event objects
    private RemoteEventGenerator<BlogEvent> eventGenerator;


    /**
     * Constructor for the microblog object. Must be run with the Start program
     *
     * @param args  A list of arguments as a string. See above for usage.
     */
    public Microblog(String[] args) {
        if (args.length < 3) {
            throw new IllegalArgumentException(
                "Microblog(): Too few arguments given.\n" + 
                "Usage: java Start Microblog <host> <port> <name>"
            );
        }
        else if (args.length > 3) {
            throw new IllegalArgumentException(
                "Microblog(): Too many arguments given.\n" + 
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
                "Microblog(): Port must be an integer.\n" + 
                "Usage: java Start Microblog <host> <port> <name>"
            ); 
        }

        try {
            eventGenerator = new RemoteEventGenerator<BlogEvent>();

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
            catch (NoSuchObjectException e2) {
            }
            throw new IllegalArgumentException(
                "Microblog(): Microblog with name " + this.name + " already exists."
            );
        }
        catch (RemoteException e) {
            throw new IllegalArgumentException(
                "Microblog(): No Registry Server is running at specified host and port."
            );
        }
        catch (Exception e) {
            System.err.println("Microblog(): An error has occurred.");
            e.printStackTrace();
        }
    }


    /**
     * Adds a message to the blog.
     *
     * @param content   The content of the message as a string.
     * @return message  The message that was added into the blog
     */
    public Message addMessage(String content) {
        long now = System.currentTimeMillis();
        int id = last_id + 1;
        last_id ++;
        Message message = new Message(id, now, this.name, content);
        this.messages.add(message);

        eventGenerator.reportEvent(new BlogEvent(this.name, message));

        return message;
    }


    /**
     * Removes a message from the blog by id
     *
     * @param id        The id value of the message to remove from the blog.
     * @return message  The message that was removed from the blog
     */
    public Message removeMessage(int id) {
        Iterator<Message> iter = messages.iterator();

        while( iter.hasNext() ) {
            Message message = iter.next();
            if (message.getId() == id) {
                iter.remove();
            }
            return message;
        }
        
        return null;
    }


    /**
     * Gets the latest (up to 2) messages from the blog.
     *
     * @return latest  A treeset containing up to the latest two messages in the blog
     */
    public TreeSet<Message> getLatestMessages() {
        TreeSet<Message> latest = new TreeSet<Message>();
        TreeSet<Message> temp = new TreeSet<Message>();
        temp.addAll(messages);

        if (messages.size() == 0) {
        }
        else if(messages.size() == 1) {
            latest.add( temp.pollLast() );
        }
        else {
            latest.add( temp.pollLast() );
            latest.add( temp.pollLast() );
        }
        
        return latest;
    }


    /**
     * Adds a lease listener to this blog. Leases expire after 20 seconds.
     *
     * @param listener          The lease listener
     * @return lease            The initial lease
     * @throws RemoteException  thrown if the event generator throws an exception
     */
    public Lease addListener(RemoteEventListener<BlogEvent> listener) throws RemoteException {
        return eventGenerator.addListener(listener, 20000);
    }

}