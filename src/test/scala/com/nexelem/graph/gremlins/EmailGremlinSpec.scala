package com.nexelem.graph.gremlins

import com.tinkerpop.blueprints.Vertex
import com.tinkerpop.blueprints.impls.orient.OrientGraphFactory
import com.tinkerpop.gremlin.scala.ScalaGraph
import org.junit.runner.RunWith
import org.scalatest.junit.JUnitRunner
import org.scalatest.{BeforeAndAfterAll, Matchers, WordSpecLike}
import scala.collection.JavaConverters._
import BlueprintsDbConnector._
import com.ansvia.graph.BlueprintsWrapper._
import com.tinkerpop.gremlin.scala._

@RunWith(classOf[JUnitRunner])
class EmailGremlinSpec  extends WordSpecLike with Matchers with BeforeAndAfterAll {

  val factory = new OrientGraphFactory("memory:test-db", "admin", "admin")
//  val factory = new OrientGraphFactory("remote:localhost/graphs-gremlins", "root", "root")
  val connector = new BlueprintsDbConnector
  implicit val graph = factory.getNoTx

  override def beforeAll(): Unit = {
//    dropDatabase

    if(graph.countVertices() == 0) {
      println("Empty db - populating with test data")
      EmailPopulator.populateDb(connector)
    } else {
      println("Db already populated")
    }
  }



  override def afterAll() {
    graph.shutdown()
  }

  "Gremlins queries" should {

    "sanity check" in {
      graph.countVertices() shouldBe 1\\150 + 1
      graph.countEdges() should be > 400L
    }

    "find all emails CC for given user" in {
      val scalaGraph: ScalaGraph = graph

      val dev = scalaGraph
        .V
        .has("_class_", classOf[Person].getName)
        .has("occupation", "developer")
        .toStream()
        .headOption
        .map { toCC[Person] }
        .getOrElse { throw new IllegalStateException("Duh... No person fulfilling given criteria in db") }

      val ccEmails: Iterable[Vertex] = scalaGraph
        .v(dev.getId)
        .out("sent").as("email")
        .outE("cc")
        .back("email")
        .toList()
        .map { case v: Vertex => v }

      ccEmails.asJava.printDump("CC'ed emails:", "subject")
    }

    "find all emails from bankers to government officials with 2nd degree familiarity" in {

    }

    "all enterpreneurs who can access CEO withing less than 3 degrees of distance" in {

    }

    "5 people who have the biggest number of government officials or bankers in 1st or 2nd degree" in {

    }

    "find shortest path for two given people" in {

    }

//    "finds person that sent the biggest amount of e-mails" in {
//      val scalaGraph: ScalaGraph = graph
//
//      val abstractGods = scalaGraph.V
//        .has("_class_", classOf[Person].getName)
//        .outE("sent")
//        .as()
//        .toList()
//        .map {
//        toCC[God]
//      }
//    }
  }

  private def dropDatabase {
    graph.getVertices.asScala.foreach {
      _.remove()
    }
    graph.getEdges.asScala.foreach {
      _.remove()
    }
  }
}