import edu.rit.ds.registry.RegistryProxy;
import edu.rit.ds.registry.AlreadyBoundException;
import java.rmi.ConnectException;
import java.rmi.RemoteException;
import java.rmi.NotBoundException;


/**
 * A client program that adds a message into a specified microblog
 * Usage: java AddMessage <host> <port> <name> <content>
 * 
 * @author Eitan Romanoff
 * @date   1/13/2013
 */
public class AddMessage {

    /**
     * The main method. Usage: java AddMessage <host> <port> <name> <content>
     *   host -    the hostname of the registry server
     *   port -    the port that the registry server is listening in on
     *   name -    the name of the blog owner
     *   content - the content of the message to add
     */
    public static void main(String[] args) {
        
        if( args.length < 4 ) {
            throw new IllegalArgumentException(
                "AddMessage(): Too few arguments.\n" + 
                "  Usage: java AddMessage <host> <port> <name> <content>"
            );
        }
        else if( args.length > 4 ) {
            throw new IllegalArgumentException(
                "AddMessage(): Too many arguments.\n" + 
                "  Usage: java AddMessage <host> <port> <name> <content>"
            );
        }
        else {
            // Get a proxy for the matching microblog

            String host = args[0];
            String name = args[2];
            String content = args[3];
            int port = 0;

            RegistryProxy registry = null;
            BlogRef blog = null;
            
            try {
                port = Integer.parseInt(args[1]);
            }
            catch (NumberFormatException e) {
                throw new IllegalArgumentException(
                    "Microblog(): port must be an integer"
                );
            }

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
            

            try {
                blog = (BlogRef)registry.lookup(name);
            }
            catch (Exception e) {
                throw new IllegalArgumentException(
                    "AddMessage(): No microblog for user " + name + " exists."
                );
            }
            
            try {
                Message message = blog.addMessage(content);
                System.out.println(message);
            }
            catch (ConnectException e) {
                System.err.println("An error occurred: Chances are the microblog went down, and the lease hasn't yet expired."); 
                e.printStackTrace();
            }
            catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}