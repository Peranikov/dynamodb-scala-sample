import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClient;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;

public class HighLevelApiSampleJava {
    public static void exec() {
        AmazonDynamoDBClient client  = (new AmazonDynamoDBClient())
                .withEndpoint("http://localhost:8000");

        DynamoDBMapper mapper = new DynamoDBMapper(client);
        MusicItemJava musicItems = mapper.load(MusicItemJava.class, "Rainbow", "Kill the King");
        System.out.println(musicItems);
    }
}
