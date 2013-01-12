import edu.rit.ds.registry.RegistryProxy;
import edu.rit.ds.registry.RegistryEventListener;
import edu.rit.ds.registry.RegistryEvent;
import edu.rit.ds.registry.RegistryEventFilter;
import edu.rit.ds.registry.NotBoundException;
import edu.rit.ds.RemoteEventListener;
import edu.rit.ds.Lease;
import edu.rit.ds.LeaseListener;
import java.rmi.server.UnicastRemoteObject;
import java.rmi.RemoteException;
import java.rmi.ConnectException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.HashSet;
import java.util.Collections;


/**
 * A client program that follows blogs
 * Usage: java AddMessage <host> <port> <blog1> [blog2 blog3...]
 * 
 * @author Eitan Romanoff
 * @date   1/13/2013
 */
public class Follow {

    private static RemoteEventListener<BlogEvent> blogListener;
    private static RegistryEventFilter registryFilter;
    private static RegistryProxy registry;
    private static RegistryEventListener registryListener;
    private static LinkedList<String> followNames;
    private static HashSet<String> connectedNames;

    /**
     * The main method. Usage: java AddMessage <host> <port> <blog1> [blog2 blog3...]
     *   host -    the hostname of the registry server
     *   port -    the port that the registry server is listening in on
     *   blog* -    the name of the blog owner to follow, may be more than one
     */
    public static void main(String[] args) {
        // TODO: Check the arguments

        if (args.length < 3) {
            throw new IllegalArgumentException(
                "Follow(): Too few arguments.\n" + 
                "  Usage: java Follow <host> <post> <name1> [name2 name3...]"
            );
        }

        String host = args[0];
        int port = 0;

        try {
            port = Integer.parseInt(args[1]);
        }
        catch (NumberFormatException e) {
            throw new IllegalArgumentException(
                "Follow(): port must be an integer"
            );
        }

        followNames = new LinkedList<String>();
        connectedNames = new HashSet<String>();

        for (int i = 2; i < args.length; i++) {
            followNames.add(args[i]);
        }


        // Connect to the registry
        try {
            registry = new RegistryProxy(host, port);
        }
        catch (ConnectException e) {
            System.err.println("An error occurred: Chances are the microblog went down, and the lease hasn't yet expired."); 
            e.printStackTrace();
            System.exit(1);
        }
        catch (RemoteException e) {
            throw new IllegalArgumentException(
                "Microblog(): No Registry Server is running at specified host and port."
            );
        }
        

        // Export a remote event listener object for receiving notifications
        // from the Registry Server.
        // When a new blog comes into existence, call listenToBlog
        try {
            registryListener = new RegistryEventListener() {
                public void report (long seqnum, RegistryEvent event) {
                    listenToBlog (event.objectName());
                }
            };
            UnicastRemoteObject.exportObject (registryListener, 0);
        }
        catch (Exception e) {
            System.err.println("Follow(): An error occured while exporting the registry listener.");
            e.printStackTrace();
        }



        // Export listener for getting notifications from blogs
        // Any time an add or remote happens, this gets called.
        try {
            blogListener = new RemoteEventListener<BlogEvent>() {
                public void report (long seqnum, BlogEvent event) {
                    System.out.println(event.message);
                }
            };
            UnicastRemoteObject.exportObject(blogListener, 0);
        }
        catch (Exception e) {
            System.err.println("Follow(): An error occured while exporting the blog listener.");
            e.printStackTrace();
        }
        

        // Get notified when a new blog is bound
        try {
            registryFilter = new RegistryEventFilter().reportType("Microblog").reportBound();
            registry.addEventListener(registryListener, registryFilter);
        }
        catch (Exception e) {
            System.err.println("Follow(): An error occured while adding the registry listener.");
            e.printStackTrace();
        }

        // Get a proxy for the matching microblog
        LinkedList<Message> initialMessages = new LinkedList<Message>();
        for (String name : followNames) {
            try {
                BlogRef blog = (BlogRef)registry.lookup(name);

                for (Message m : blog.getLatestMessages()) {
                    initialMessages.add(m);
                }

                Lease lease = blog.addListener(blogListener);
                lease.setListener(new LeaseWatcher(name));
                connectedNames.add(name);
            }
            catch (NotBoundException e) {
            }
            catch (RemoteException e) {
                System.err.println("Follow(): An error occured while adding adding a blog listener.");
                e.printStackTrace();
            }
        }

        Collections.sort(initialMessages);
        for (Message m : initialMessages) {
            System.out.println(m);
        }
    }


    /**
     * Callback for when a blog connects to the registry
     *
     * @param blogName  the name of the blog that has connected
     */
    private static void listenToBlog( String blogName ) {
        // Check to see if we're supposed to follow, and haven't already seen the connection
        //   If so, print the latest messages and establish the listener
        // If we are following, but the connection was already established...
        //   Just re-establish the listener silently
        try {
            if (followNames.contains(blogName)) {
                BlogRef blog = (BlogRef)registry.lookup(blogName);
                Lease lease = blog.addListener(blogListener);
                lease.setListener(new LeaseWatcher(blogName));
                
                if(!connectedNames.contains(blogName)) {
                    for (Message m : blog.getLatestMessages()) {
                        System.out.println(m);
                    }
                    connectedNames.add(blogName);
                }
            }
        }
        catch (Exception e) {
            System.err.println("Follow(): An error occurred in the registryListener callback.");
            e.printStackTrace();
            System.exit(1);
        }
    }
}