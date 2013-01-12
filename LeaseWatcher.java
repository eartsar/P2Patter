import edu.rit.ds.Lease;
import edu.rit.ds.LeaseListener;


/**
 * An implementation of a LeaseListener that handles Microblog leases
 * 
 * @author Eitan Romanoff
 * @date 1/13/2013
 */
public class LeaseWatcher implements LeaseListener {

    private String blogName;

    /**
     * Constructor.
     *
     * @param blogName  The name of the blog being listened on
     */
    public LeaseWatcher(String blogName) {
        this.blogName = blogName;
    }


    /**
     * Callback when the lease is canceled. From LeaseListener interface.
     *
     * @param theLease  The lease that was canceled.
     */
    public void leaseCanceled(Lease theLease) {
    }


    /**
     * Callback for when the lease has expired. From LeaseListener interface.
     *
     * @param theLease  The lease that was canceled
     */
    public void leaseExpired(Lease theLease) {
        System.out.println("--------------------------------------------------------------------------------");
        System.out.println(blogName + " -- Failed");
    }


    /**
     * Callback for when the lease has been renewed. From LeaseListener interface.
     *
     * @param theLease  The lease that was renewed. From LeaseListener interface.
     */
    public void leaseRenewed(Lease theLease) {
    }
}