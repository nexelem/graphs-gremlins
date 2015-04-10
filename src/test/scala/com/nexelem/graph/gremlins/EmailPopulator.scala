package com.nexelem.graph.gremlins

import com.tinkerpop.blueprints.Vertex
import com.tinkerpop.blueprints.impls.orient.OrientBaseGraph
import com.tinkerpop.furnace.generators.{CommunityGenerator, NormalDistribution, PowerLawDistribution}
import org.apache.commons.lang3.RandomStringUtils.randomAlphabetic
import org.apache.commons.lang3.RandomUtils.nextInt
import com.ansvia.graph.BlueprintsWrapper._

import scala.io.Source

object EmailPopulator {
  def populateDb(connector: BlueprintsDbConnector)(implicit graph: OrientBaseGraph): List[Vertex] = {
    val root = Root("graph root node").save
    val people = populatePeople(connector, root)
    val emails = populateEmails(connector, people)

    people
  }

  def populatePeople(connector: BlueprintsDbConnector, root: Vertex)(implicit graph: OrientBaseGraph) = {
    val generateName = createRandomNamesGenerator
    val generateOccupation = createRandomOccupationGenerator

    val people = Stream.continually {
      val name = generateName()
      val occupation = generateOccupation()
      val person = Person(name.name, name.lastName, occupation).save
      root <-- "child" <-- person
      person
    }.take(150).toList

    val commGenerator = new CommunityGenerator("knows")
    commGenerator.setCommunityDistribution(new NormalDistribution(10))
    commGenerator.setDegreeDistribution(new PowerLawDistribution(2.3))
    commGenerator.setCrossCommunityPercentage(0.1)
    val numEdges = commGenerator.generate(graph,5,1000)
    println(s"Generated ${numEdges} `knows` edges")

    people
  }

  def populateEmails(connector: BlueprintsDbConnector, people: List[Vertex])(implicit graph: OrientBaseGraph) = {
    val emails = Stream.continually {
      Email(s"Subject: ${randomAlphabetic(20)}", randomAlphabetic(100)).save
    }.take(1000).toList

    generateEmailEdges(people, emails)

    emails
  }


  def generateEmailEdges(people: List[Vertex], emails: List[Vertex])(implicit graph: OrientBaseGraph) {
    people.foreach { person =>
      0 until nextInt(0, 20) foreach { _ =>
        val email = emails(nextInt(0, emails.size))
        person --> "sent" --> email

        generateGenericEdges(email, people, 3, "to")
        generateGenericEdges(email, people, 7, "cc")
      }
    }
  }

  def generateGenericEdges(outVertex: Vertex, inVertices: List[Vertex], maxNumber: Int, label: String)(implicit graph: OrientBaseGraph) {
    0 until nextInt(0, maxNumber) foreach { _ =>
      outVertex --> label --> inVertices(nextInt(0, inVertices.size))
    }
  }

  def createRandomNamesGenerator = {
    createGenerator("names.esv") { name =>
      val splitName = name.trim.split("\\s")
      Name(splitName(0), splitName(1))
    }
  }

  def createRandomOccupationGenerator = {
    createGenerator("roles.esv") { occ =>
      occ
    }
  }

  private def createGenerator[T](filename: String)(entryToEntity: String => T) = {
    resource.managed(getClass.getClassLoader.getResourceAsStream(filename)).acquireAndGet { stream =>
      val names = Source.fromInputStream(stream)
        .getLines()
        .map { _.trim }
        .map { entryToEntity }
        .toList

      () => names(nextInt(0, names.size))
    }
  }
}

case class Name(name: String, lastName: String)

case class Email(subject: String, body: String) extends BaseEntity
case class Person(name: String, lastName: String, occupation: String) extends BaseEntity
case class Root(name: String) extends BaseEntity