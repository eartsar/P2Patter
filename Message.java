import java.util.Date;
import java.text.SimpleDateFormat;
import java.io.Serializable;

public class Message implements Comparable<Message>, Serializable {
    // Necessary for serialization
    //private static final long serialVersionUID = 729386723967029L;

    private int id;
    private long time;
    private String author;
    private String content;

    public Message(int id, long time, String author, String content) {
        this.id = id;
        this.time = time;
        this.author = author;
        this.content = content;
    }

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

    public int getId() { return this.id; }
    public long getTime() { return this.time; }
    public String getAuthor() { return this.author; }
    public String getContent() { return this.content; }
}