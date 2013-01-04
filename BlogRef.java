import java.rmi.Remote;
import java.rmi.RemoteException;

public interface BlogRef extends Remote {

    public Message addMessage(String text, long timestamp) throws RemoteException;
    public Message removeMessage(int id) throws RemoteException;

}