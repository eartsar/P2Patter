import java.rmi.Remote;
import java.rmi.RemoteException;
import edu.rit.ds.Lease;
import edu.rit.ds.RemoteEventListener;
import java.util.TreeSet;

/**
 * The proxy interface for the blog
 * 
 * @author Eitan Romanoff
 * @date 1/13/2013
 */
public interface BlogRef extends Remote {

    /**
     * Adds a message to the blog.
     *
     * @param content   The content of the message as a string.
     * @return message  The message that was added into the blog
     */
    public Message addMessage(String text) throws RemoteException;


    /**
     * Removes a message from the blog.
     *
     * @param id        The id of the message to remove
     * @return message  The message that was removed from the blog
     */
    public Message removeMessage(int id) throws RemoteException;


    /**
     * Adds a listener to the blog.
     *
     * @param listener   The listener that will be notified on events
     * @return lease     The lease pertaining to this connection
     */
    public Lease addListener(RemoteEventListener<BlogEvent> listener) throws RemoteException;


    /**
     * Gets the latest messages (up to 2)
     *
     * @return messages  The two latest messages added to the blog
     */
    public TreeSet<Message> getLatestMessages() throws RemoteException;

}