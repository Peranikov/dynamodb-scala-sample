import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBAttribute;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBHashKey;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBRangeKey;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBTable;

import java.util.List;
import java.util.Set;

@DynamoDBTable(tableName="Music")
public class MusicItemJava {
    public String artist = "";
    public String songTitle = "";
    public Set<String> members;

    // Hash key
    @DynamoDBHashKey(attributeName="Artist")
    public String getArtist() { return this.artist; }
    public void setArtist(String artist) { this.artist = artist; }

    // Range key
    @DynamoDBRangeKey(attributeName="SongTitle")
    public String getSongTitle() { return this.songTitle; }
    public void setSongTitle(String songTitle) { this.songTitle = songTitle; }

    @DynamoDBAttribute(attributeName="Members")
    public Set<String> getMembers() { return this.members; }
    public void setMembers(Set<String> members) { this.members = members; }
}
