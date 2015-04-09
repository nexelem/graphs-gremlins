package com.nexelem.graph.gremlins

import com.ansvia.graph.BlueprintsWrapper._
import com.nexelem.graph.gremlins.BlueprintsDbConnector._
import com.tinkerpop.blueprints.Direction.IN
import com.tinkerpop.gremlin.scala.ScalaGraph

class HelloGodsSpec extends GraphSpec {
  "blueprints-scala simple access" should {
    "properly store & read simple classes" in {
      val greekRealm = connector.save(Realm("Greek"))

      val hercules = connector.save(God("Hercules", "Demigod"))
      val ares = connector.save(God("Ares", "God"))

      val realms = connector.findAll[Realm]
      realms.size should be(1)

      val gods = connector.findAll[God]
      gods.size should be(2)
    }

    "properly delete classes" in {
      val mesopotamianRealm = connector.save(Realm("Mesopotamian"))
      val cthulhuRealm = connector.save(Realm("Cthulhu"))
      val shintoRealm = connector.save(Realm("Shinto"))

      connector.findAll[Realm].filter(god => Seq(mesopotamianRealm.name, cthulhuRealm.name, shintoRealm.name).contains(god.name)).size should be(3)

      connector.delete(mesopotamianRealm)
      connector.findAll[Realm].filter(god => Seq(cthulhuRealm.name, shintoRealm.name).contains(god.name)).size should be(2)

      connector.delete(shintoRealm)
      connector.findAll[Realm].filter(god => Seq(cthulhuRealm.name).contains(god.name)).size should be(1)
    }

    "properly associate edges with vertices" in {
      val hinduRealm = connector.save(Realm("Hindu"))

      val vishnu = connector.save(God("Vishnu", "Deity"))

      connector.getLinked(hinduRealm, classOf[God], IN, "livesIn").size should be(0)

      vishnu.getVertex --> "livesIn" --> hinduRealm.getVertex
      connector.getLinked(hinduRealm, classOf[God], IN, "livesIn").size should be(1)

      val shakti = connector.save(God("Shakti", "Deity"))
      connector.getLinked(hinduRealm, classOf[God], IN, "livesIn").size should be(1)
      connector.findAll[God].filter(god => Seq(shakti.name, vishnu.name).contains(god.name)).size should be(2)
    }

    "properly handle gremlin queries" in {
      val chineseRealm = connector.save(Realm("Chinese"))

      val hundun = connector.save(God("Hundun", "Abstract"))
      hundun.getVertex --> "livesIn" --> chineseRealm.getVertex

      val taotie = connector.save(God("Taotie", "Abstract"))
      hundun.getVertex --> "livesIn" --> chineseRealm.getVertex

      val jiaolong = connector.save(God("Jiaolong", "Dragon"))
      jiaolong.getVertex --> "livesIn" --> chineseRealm.getVertex

      val scalaGraph: ScalaGraph = graph
      val abstractGods = scalaGraph.V
        .has("_class_", classOf[God].getName)
        .has("godType", "Abstract")
        .toList()
        .map {
        toCC[God]
      }

      abstractGods.size should be(2)
      abstractGods.find {
        _.name == "Jialong"
      } should be(None)
    }
  }
}

case class God (val name: String, val godType: String) extends BaseEntity

case class Realm (val name: String) extends BaseEntity