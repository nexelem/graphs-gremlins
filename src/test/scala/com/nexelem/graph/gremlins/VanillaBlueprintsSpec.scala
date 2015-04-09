package com.nexelem.graph.gremlins

import com.tinkerpop.blueprints.Direction.OUT

import scala.collection.JavaConverters._

class VanillaBlueprintsSpec extends GraphSpec {

  override protected def setup {
    val mikeTheDev = graph.addVertex(null, Map("name" -> "Mike", "role" -> "developer", "type" -> "person").asJava)
    val paulTheManager = graph.addVertex(null, Map("name" -> "Paul", "role" -> "manager", "type" -> "person").asJava)
    val joanTheDesigner = graph.addVertex(null, Map("name" -> "Joan", "role" -> "designer", "type" -> "person").asJava)
    val willTheTester = graph.addVertex(null, Map("name" -> "Will", "role" -> "tester", "type" -> "person").asJava)

    val mike2JoanEmail = graph.addVertex(null, Map().asJava)
    mike2JoanEmail.setProperties(Map("subject" -> "How are you ?", "body" -> "Just wanted to say hi !", "type" -> "email").asJava)
    mike2JoanEmail.save()
    graph.addEdge(null, mikeTheDev, joanTheDesigner, "to")

    val mailPaul2Everyone = graph.addVertex(null, Map("subject" -> "Deadlines !", "body" -> "Get back to work guys, this software ain't gonna write itself").asJava)
    graph.addEdge(null, paulTheManager, mailPaul2Everyone, "sent")
    graph.addEdge(null, mailPaul2Everyone, mikeTheDev, "to")
    graph.addEdge(null, mailPaul2Everyone, joanTheDesigner, "cc")
    graph.addEdge(null, mailPaul2Everyone, willTheTester, "cc")
  }

  "Pure blueprints API" must {
    "find vertices & edges" in {
      graph.getVertices.asScala.size shouldBe 4 + 2
      graph.getEdges.asScala.size shouldBe 5
    }

    "fetch single vertex and delete" in {
      val vertices = graph.getVertices("name", "Mike").asScala.toList
      vertices.size shouldBe 1

      val mike = vertices(0)
      mike.getProperty[String]("role") shouldBe "developer"

      mike.remove()
      graph.getVertices("name", "Mike").asScala shouldBe empty
    }

    "traverse edges" in {
      val managers = graph.getVertices("role", "manager").asScala

      // this is also suboptimal from performance point-of-view (think N+1 query problem)
      def findMailReceivers(label: String) = for {
        manager <- managers
        sent <- manager.getVertices(OUT, "sent").asScala
        to <- sent.getVertices(OUT, label).asScala
      } yield to.getProperty[String]("name")

      val sentTo = findMailReceivers("to")
      sentTo should contain theSameElementsAs Seq("Mike")

      val sentCc = findMailReceivers("cc")
      sentCc should contain theSameElementsAs Seq("Will", "Joan")
    }
  }
}