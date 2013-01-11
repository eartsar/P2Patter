import edu.rit.ds.registry.RegistryProxy;


public class AddMessage {

    public static void main(String[] args) {
        // TODO: Check the arguments

        String host = args[0];
        int port = Integer.parseInt(args[1]);
        String name = args[2];
        String content = args[3];

        // Get a proxy for the matching microblog
        try {
            RegistryProxy registry = new RegistryProxy(host, port);

            BlogRef blog = (BlogRef)registry.lookup(name);
            
            Message message = blog.addMessage(content);
            System.out.println(message);
            
        }
        catch (Exception e) { 
            e.printStackTrace();
        }

    }
}