package com.nexelem.graph.gremlins

import com.ansvia.graph.BlueprintsWrapper._
import com.nexelem.graph.gremlins.BlueprintsDbConnector._
import com.tinkerpop.blueprints.Vertex
import com.tinkerpop.blueprints.impls.orient.OrientGraphFactory
import com.tinkerpop.gremlin.scala.{ScalaGraph, _}
import org.junit.runner.RunWith
import org.scalatest.junit.JUnitRunner
import org.scalatest.{BeforeAndAfterAll, Matchers, WordSpecLike}

import scala.collection.JavaConverters._

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
      graph.countVertices() shouldBe 1150 + 1
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

      val ccEmails = scalaGraph
        .v(dev.getId)
        .out("sent").as("email")
        .outE("cc")
        .back("email")
        .toList()
        .map { case v: Vertex => v }

      ccEmails.asJava.printDump("CC'ed emails:", "subject")
    }

    "all bankers who can access government officials within `knows` distance < 3" in {
      val scalaGraph: ScalaGraph = graph

      val root = scalaGraph
        .V
        .has("_class_", classOf[Root].getName)
        .toList()
        .map { toCC[Root] }
        .head

      val bankers = scalaGraph
        .v(root.getId)
        .in("child")
        .has("occupation", "banker").as("person")
        .out("knows")
        .loop("person", _.getLoops < 3, _.getObject.get("occupation") == Some("official"))
        .back("person")
        .toList()
        .map { case v: Vertex => v }

      bankers.asJava.printDump("Found bankers", "names")
      println(s"In total: ${bankers.size} records")
    }

    "5 people who have the biggest number of government officials or bankers in 1st or 2nd degree" in {

    }

    "find shortest path for two given people" in {
      val scalaGraph: ScalaGraph = graph

      val people = scalaGraph
        .V
        .has("_class_", classOf[Person].getName)
        .toList()

      val firstPerson = people(0)
      val secondPerson = people(35)

      println(s"First person: ${firstPerson}, second person: ${secondPerson}")

      val knowPath = scalaGraph
        .v(firstPerson.getId)
        .startPipe.as("person")
        .out("knows")
        .loop( "person", l => l.getObject.getId != secondPerson.getId && l.getLoops < 5 )
        .path
        .toStream()

      knowPath.take(1).foreach { path =>
        println(path)
      }
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