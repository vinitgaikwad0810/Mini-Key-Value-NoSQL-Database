import org.restlet.Application;
import org.restlet.Component;
import org.restlet.Restlet;
import org.restlet.data.Protocol;
import org.restlet.routing.Router;

public class ClientRestServer extends Application{

	@Override
    public synchronized Restlet createInboundRoot() {
		Router router = new Router(getContext());
		router.attach("/test", ClientDataHandler.class);
		router.attach("/test/{key}", ClientDataHandler.class);
		return router;
	}
	
	public static void main(String args[]) {                  
        try {
        	if (args.length > 0) {
        		ConfService.getInstance().loadProperties(args[0]);        		
        	}  else {
        		usage();
        	}
        	
            Component component = new Component();    
            component.getServers().add(Protocol.HTTP, ConfService.getInstance().getCurrent_external_port());  
            component.getDefaultHost().attach("/restlet", new ClientRestServer());
			component.start();
		} catch (Exception e) {
			e.printStackTrace();
		}  
	}

	private static void usage() {
		System.out.println("Usage: example <conf file>");
		
	}

}
