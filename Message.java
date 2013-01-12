import java.util.Date;
import java.text.SimpleDateFormat;
import java.io.Serializable;


/**
 * A message object that is used by the microBlog
 * The message object is fully serializeable
 * 
 * @author Eitan Romanoff
 * @date 1/13/2013
 */
public class Message implements Comparable<Message>, Serializable {
    
    // private fields
    private int id;
    private long time;
    private String author;
    private String content;


    /**
     * The constructor for a message objects
     *
     * @param id       the id of the message
     * @param time     the timestamp in milliseconds of the message
     * @param author   the author of the message
     * @param content  the content of the message
     */
    public Message(int id, long time, String author, String content) {
        this.id = id;
        this.time = time;
        this.author = author;
        this.content = content;
    }


    /**
     * Overridden compareTo to adhere to comparable
     * Comparison is first by time, then author, then id
     *
     * @param other  the other message to compare to
     * @return val   1 if this message is "after", -1 if "before"
     */
    public int compareTo(Message other) {
        if (this.time < other.time) { return -1; }
        else if (this.time > other.time) { return 1; }
        else if (this.author.compareTo(other.author) != 0) {
            return this.author.compareTo(other.author); 
        }
        else if (this.id < other.id) { return -1; }
        else if (this.id > other.id) { return 1; }
        else { return 1; }
    }


    /**
     * toString function to print out messages in a pretty format
     * 80 (NOT ONE MORE NOT ONE LESS) hyphans followed by a STRICT format
     * So strict, we use a java date formatter to do it for us!
     *
     * @return ret  the string representation of this message
     */
    public String toString() {
        Date date = new Date(time);
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy/MM/dd H:mm:ss");
        String timestamp = formatter.format(date);
        String ret;
        ret = "--------------------------------------------------------------------------------";
        ret = ret + "\n" + author + " -- " + "Message " + id + " -- " + timestamp;
        ret = ret + "\n" + content;
        return ret;
    }


    /**
     * Accessor for the id field
     *
     * @return id  the id of this message
     */
    public int getId() { return this.id; }


    /**
     * The accessor for the time field
     *
     * @return time  the timestamp of this message
     */
    public long getTime() { return this.time; }


    /**
     * The accessor of the author field
     *
     * @return author  the author of this message
     */
    public String getAuthor() { return this.author; }


    /**
     * The acessor of the content field
     *
     * @return content  the content of this message
     */
    public String getContent() { return this.content; }
    
}