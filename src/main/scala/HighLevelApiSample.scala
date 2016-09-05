import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClient
import com.amazonaws.services.dynamodbv2.datamodeling.{DynamoDBQueryExpression, _}
import com.amazonaws.services.dynamodbv2.model.AttributeValue

import collection.JavaConverters._

@DynamoDBTable(tableName="Music")
class MusicItem {
  private var artist: String = _
  private var songTitle: String = _
  private var members: java.util.Set[String] = _
  private var origin: String = _

  // Hash key
  @DynamoDBHashKey(attributeName="Artist")
  def getArtist: String = { this.artist }
  def setArtist(artist: String): Unit = { this.artist = artist }

  // Range key
  @DynamoDBRangeKey(attributeName="SongTitle")
  def getSongTitle: String = { this.songTitle }
  def setSongTitle(songTitle: String): Unit = { this.songTitle = songTitle }

  @DynamoDBIndexRangeKey(attributeName="Origin", localSecondaryIndexName="OriginIndex")
  def getOrigin: String = { this.origin }
  def setOrigin(origin: String): Unit = { this.origin = origin }

  @DynamoDBAttribute(attributeName="Members")
  def getMembers: java.util.Set[String] = { this.members }
  def setMembers(members: java.util.Set[String]): Unit = { this.members = members }

  override def toString: String = {
    s"""{
       |  Artist: ${this.artist}
       |  SongTitle: ${this.songTitle}
       |  Origin: ${this.origin}
       |  Members: ${this.members}
       |}""".stripMargin
  }
}

object MusicItem {

  def apply(artist: String, songTitle: String): MusicItem = {
    val item = new MusicItem()
    item.setArtist(artist)
    item.setSongTitle(songTitle)
    item
  }

  def apply(artist: String, songTitle: String, origin: String, members: Set[String]): MusicItem = {
    val item = new MusicItem()
    item.setArtist(artist)
    item.setSongTitle(songTitle)
    item.setOrigin(origin)
    item.setMembers(members.asJava)
    item
  }
}

object HighLevelApiSample extends App {

  val client: AmazonDynamoDBClient = (new AmazonDynamoDBClient())
    .withEndpoint("http://localhost:8000")
  val mapper: DynamoDBMapper = new DynamoDBMapper(client)

  println("*** Save ***")
  println(
    mapper.save(MusicItem("Guns N' Roses", "Welcome to the Jungle", "USA", Set("Axl Rose", "Slash")))
  )

  println("*** Load ***")
  println(
    mapper.load(classOf[MusicItem], "Guns N' Roses", "Welcome to the Jungle")
  )

  println("*** Delete ***")
  println(
    mapper.delete(MusicItem("Guns N' Roses", "Welcome to the Jungle"))
  )

  println("*** Load (Nothing) ***")
  println(
    mapper.load(classOf[MusicItem], "Guns N' Roses", "Welcome to the Jungle")
  )

  println("*** Batch Save ***")
  println(
    mapper.batchSave(Seq(
      MusicItem("Black Sabbath", "Paranoid", "England", Set("Ozzy Osbourne", "Tony Iommi")),
      MusicItem("Black Sabbath", "Mr. Crowley", "England", Set("Ozzy Osbourne", "Tony Iommi")),
      MusicItem("Black Sabbath", "Heaven or Hell", "England", Set("Ronnie James Dio", "Tony Iommi"))
    ).asJava)
  )

  println("*** Query ***")
  println(
    mapper.query(
      classOf[MusicItem],
      new DynamoDBQueryExpression[MusicItem]()
        .withKeyConditionExpression("Artist = :a")
        .withExpressionAttributeValues(Map(
          ":a" -> new AttributeValue("Black Sabbath")
        ).asJava)
    ).asScala
  )

  println("*** Query Page with Limit ***")
  val limitQueryResult = mapper.queryPage(
    classOf[MusicItem],
    new DynamoDBQueryExpression[MusicItem]()
      .withKeyConditionExpression("Artist = :a")
      .withExpressionAttributeValues(Map(
        ":a" -> new AttributeValue("Black Sabbath")
      ).asJava)
      .withLimit(1)
  )

  println(limitQueryResult.getResults.asScala)

  println("*** Query Page with LastEvaluatedKey ***")
  println(s"LastEvaluatedKey: ${limitQueryResult.getLastEvaluatedKey}")
  println(
    mapper.queryPage(
      classOf[MusicItem],
      new DynamoDBQueryExpression[MusicItem]()
        .withKeyConditionExpression("Artist = :a")
        .withExpressionAttributeValues(Map(
          ":a" -> new AttributeValue("Black Sabbath")
        ).asJava)
        .withExclusiveStartKey(limitQueryResult.getLastEvaluatedKey)
    ).getResults.asScala
  )

  println("*** Query with Local Secondly Index ***")
  println(
    mapper.query(
      classOf[MusicItem],
      new DynamoDBQueryExpression[MusicItem]()
        .withIndexName("OriginIndex")
        .withKeyConditionExpression("Artist = :a and Origin = :o")
        .withExpressionAttributeValues(Map(
          ":a" -> new AttributeValue("Black Sabbath"),
          ":o" -> new AttributeValue("England")
        ).asJava)
    ).asScala
  )

  println("*** Scan ***")
  println(
    mapper.scan(classOf[MusicItem], new DynamoDBScanExpression()).asScala
  )
}
