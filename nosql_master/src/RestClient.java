import java.io.IOException;
import java.util.Scanner;

import org.restlet.resource.ClientResource;
import org.restlet.resource.ResourceException;

public class RestClient {
	
	public static void main(String args[]) {
		ClientResource client = new ClientResource("http://localhost:8080/restlet/test");
         //helloClientresource.get().write(System.out);
         
         Scanner sc=new Scanner(System.in);
         System.out.println("Data: ");
         String value = sc.nextLine();
		 try {						
			 	String key = client.post(value).getText();
			 	client.get();
			 	
			 	System.out.println("Fetched Data: ");
						
		} catch (ResourceException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
