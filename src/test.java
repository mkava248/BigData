import com.mongodb.MongoClient;
 
public class test {
 
	public static void main(String[] args) {
 
		MongoClient client = new MongoClient("localhost", 27017);
		String connectPoint = client.getConnectPoint();
		System.out.println(connectPoint);
		client.close();
	}
 
}