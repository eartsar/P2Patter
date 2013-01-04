import java.rmi.Remote;
import java.rmi.RemoteException;
import edu.rit.ds.Lease;
import edu.rit.ds.RemoteEventListener;

public interface BlogRef extends Remote {

    public Message addMessage(String text, long timestamp) throws RemoteException;
    public Message removeMessage(int id) throws RemoteException;
    public Lease addListener(RemoteEventListener<BlogEvent> listener) throws RemoteException;

}