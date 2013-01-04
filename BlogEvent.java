import edu.rit.ds.RemoteEvent;

public class BlogEvent extends RemoteEvent {
    public final String blog_name;
    public final Message message;

    public BlogEvent(String blog_name, Message message) {
        this.blog_name = blog_name;
        this.message = message;
    }
}