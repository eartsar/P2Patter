import edu.rit.ds.RemoteEvent;

/**
 * A blog event to be used for the listener publish-subscribe pattern
 * 
 * @author Eitan Romanoff
 * @date 1/13/2013
 */
public class BlogEvent extends RemoteEvent {
    
    // Fields! We love fields!
    public final String blog_name;
    public final Message message;

    /**
     * Constructor for the event object
     *
     * @param blog_name  the name of this blog
     * @param message    the message that the event pertains to
     */
    public BlogEvent(String blog_name, Message message) {
        this.blog_name = blog_name;
        this.message = message;
    }
}