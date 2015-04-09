package com.nexelem.graph.gremlins

import scala.collection.JavaConverters._

class PureBlueprintsSpec extends GraphSpec {

  override protected def setup {
    val mikeTheDev = graph.addVertex(null, Map("name" -> "Mike", "role" -> "developer", "type" -> "person").asJava)
    val paulTheManager = graph.addVertex(null, Map("name" -> "Paul", "role" -> "manager", "type" -> "person").asJava)
    val joanTheDesigner = graph.addVertex(null, Map("name" -> "Joan", "role" -> "designer", "type" -> "person").asJava)
    val willTheTester = graph.addVertex(null, Map("name" -> "Will", "role" -> "tester", "type" -> "person").asJava)

    val mike2JoanEmail = graph.addVertex(null, Map().asJava)
    mike2JoanEmail.setProperties(Map("subject" -> "How are you ?", "body" -> "Just wanted to say hi !", "type" -> "email").asJava)
    mike2JoanEmail.save()
    graph.addEdge(null, mikeTheDev, joanTheDesigner, "to")

    val paul2Everyone = graph.addVertex(null, Map("subject" -> "Deadlines !", "body" -> "Get back to work guys, this software ain't gonna write itself").asJava)
    graph.addEdge(null, paulTheManager, mikeTheDev, "to")
    graph.addEdge(null, paulTheManager, joanTheDesigner, "cc")
    graph.addEdge(null, paulTheManager, willTheTester, "cc")
  }

  "Pure blueprints API" must {
    "properly store & read simple classes" in {
      graph.getVertices.asScala.size should be(4 + 2)
      graph.getEdges.asScala.size should be(4)
    }
  }
}