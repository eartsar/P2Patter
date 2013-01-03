import edu.rit.ds.registry.RegistryProxy;
import edu.rit.ds.registry.AlreadyBoundException;
import java.rmi.server.UnicastRemoteObject;


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

        // Get a proxy for the Registry Server
        registry = new RegistryProxy(registry_host, registry_port);

        // Export this MicroBlog
        UnicastRemoteObject.exportObject(this, 0);

        // Bind this MicroBlog into the Registry Server
        try {
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

}