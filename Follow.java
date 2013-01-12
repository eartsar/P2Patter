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
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.HashSet;


public class Follow {

    private static RemoteEventListener<BlogEvent> blogListener;
    private static RegistryEventFilter registryFilter;
    private static RegistryProxy registry;
    private static RegistryEventListener registryListener;
    private static LinkedList<String> followNames;
    private static HashSet<String> connectedNames;

    public static void main(String[] args) {
        // TODO: Check the arguments

        if (args.length < 3) {
            usage();
        }

        String host = args[0];
        int port = Integer.parseInt(args[1]);
        followNames = new LinkedList<String>();
        connectedNames = new HashSet<String>();

        for (int i = 2; i < args.length; i++) {
            followNames.add(args[i]);
        }


        // Connect to the registry
        try {
            registry = new RegistryProxy(host, port);
        }
        catch (Exception e) {
            e.printStackTrace();
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
            e.printStackTrace();
        }
        

        // Get notified when a new blog is bound
        try {
            registryFilter = new RegistryEventFilter().reportType("Microblog").reportBound();
            registry.addEventListener(registryListener, registryFilter);
        }
        catch (Exception e) {
            e.printStackTrace();
        }

        // Get a proxy for the matching microblog   
        for (String name : followNames) {
            try {
                BlogRef blog = (BlogRef)registry.lookup(name);

                for (Message m : blog.getLatestMessages()) {
                    System.out.println(m);
                }

                Lease lease = blog.addListener(blogListener);
                lease.setListener(new LeaseWatcher(name));
                connectedNames.add(name);
            }
            catch (NotBoundException e) {
            }
            catch (RemoteException e) {
                e.printStackTrace();
            }
        }
    }


    private static void listenToBlog( String blogName ) {
        System.out.println("NEW CONNETION: " + blogName);
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
            e.printStackTrace();
        }
    }

    public static void usage() {
        System.out.println("Usage: java Follow <host> <post> <name>");
    }

}