import java.rmi.Remote;
import java.rmi.RemoteException;
import edu.rit.ds.Lease;
import edu.rit.ds.RemoteEventListener;
import java.util.TreeSet;

public interface BlogRef extends Remote {

    public Message addMessage(String text) throws RemoteException;
    public Message removeMessage(int id) throws RemoteException;
    public Lease addListener(RemoteEventListener<BlogEvent> listener) throws RemoteException;
    public TreeSet<Message> getLatestMessages() throws RemoteException;

}