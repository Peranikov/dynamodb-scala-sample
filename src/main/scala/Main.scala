import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClient
import com.amazonaws.services.dynamodbv2.model._

import collection.JavaConversions._

object Main extends App {
  val client: AmazonDynamoDBClient = (new AmazonDynamoDBClient())
    .withEndpoint("http://localhost:8000")

  println("*** List Tables ***")
  println(client.listTables)

  println("*** Describe Table ***")
  println(client.describeTable("Music"))

  println("*** Batch Write ***")

  val batchWriteItems: java.util.List[WriteRequest] = Seq(
    new WriteRequest(new PutRequest(
      Map(
        "Artist"    -> new AttributeValue("Rainbow"),
        "SongTitle" -> new AttributeValue("Kill the King"),
        "Member"    -> new AttributeValue(Seq(
          "Ronnie James Dio",
          "Ritchie Blackmore"
        ))
      )
    )),
    new WriteRequest(new PutRequest(
      Map(
        "Artist"    -> new AttributeValue("Rainbow"),
        "SongTitle" -> new AttributeValue("All Night Long"),
        "Member"    -> new AttributeValue(Seq(
          "Graham Bonnet",
          "Ritchie Blackmore"
        ))
      )
    )),
    new WriteRequest(new PutRequest(
      Map(
        "Artist"    -> new AttributeValue("Metallica"),
        "SongTitle" -> new AttributeValue("Enter Sandman"),
        "Member"    -> new AttributeValue(Seq(
          "James Hetfield",
          "Kirk Hammett"
        ))
      )
    )),
    new WriteRequest(new PutRequest(
      Map(
        "Artist"    -> new AttributeValue("Deep Purple"),
        "SongTitle" -> new AttributeValue("Highway Star"),
        "Member"    -> new AttributeValue(Seq(
          "Ian Gillan",
          "Ritchie Blackmore"
        ))
      )
    ))
  )

  println(
    client.batchWriteItem(
      (new BatchWriteItemRequest())
        .withRequestItems(Map("Music" -> batchWriteItems))
    )
  )

  println("*** Query ***")

  val hashCondition = new Condition()
    .withComparisonOperator(ComparisonOperator.EQ)
    .withAttributeValueList(new AttributeValue("Rainbow")
  )

  println(
    client.query(
      (new QueryRequest("Music"))
        .withKeyConditions(Map("Artist" -> hashCondition))
    ).getItems.map(_.toMap.prettyPrint)
  )

  println("*** Query with Filter ***")

  val filterCondition = new Condition()
    .withComparisonOperator(ComparisonOperator.EQ)
    .withAttributeValueList(new AttributeValue("Ronnie James Dio")
  )

  println(
    client.query(
      (new QueryRequest("Music"))
        .withKeyConditions(Map("Artist" -> hashCondition))
        .withQueryFilter(Map("Vocal" -> filterCondition))
    ).getItems.map(_.toMap.prettyPrint)
  )

  println("*** Query with Limit ***")
  val limitQueryResult = client.query(
    (new QueryRequest("Music"))
      .withKeyConditions(Map("Artist" -> hashCondition))
      .withLimit(1)
  )
  println(
    limitQueryResult.getItems.map(_.toMap.prettyPrint)
  )

  println("*** Query with LastEvaluatedKey ***")
  println(s"LastEvaluatedKey: ${limitQueryResult.getLastEvaluatedKey}" )
  println(
    client.query(
      (new QueryRequest("Music"))
        .withKeyConditions(Map("Artist" -> hashCondition))
        .withExclusiveStartKey(limitQueryResult.getLastEvaluatedKey)
    ).getItems.map(_.toMap.prettyPrint)
  )

  println("*** Query with Nested Item ***")

  println("*** Scan ***")
  println(
    client.scan(
      new ScanRequest("Music")
    ).getItems.map(_.toMap.prettyPrint)
  )

  // http://stackoverflow.com/questions/32004050/pretty-print-a-nested-map-in-scala
  implicit class PrettyPrintMap[K, V](val map: Map[K, V]) {
    def prettyPrint: PrettyPrintMap[K, V] = this

    override def toString: String = {
      val valuesString = toStringLines.mkString("\n")

      "Map (\n" + valuesString + "\n)"
    }

    def toStringLines = {
      map
        .flatMap{ case (k, v) => keyValueToString(k, v)}
        .map(indentLine(_))
    }

    def keyValueToString(key: K, value: V): Iterable[String] = {
      value match {
        case v: Map[_, _] => Iterable(key + " -> Map (") ++ v.prettyPrint.toStringLines ++ Iterable(")")
        case x => Iterable(key + " -> " + x.toString)
      }
    }

    def indentLine(line: String): String = {
      "\t" + line
    }
  }
}
