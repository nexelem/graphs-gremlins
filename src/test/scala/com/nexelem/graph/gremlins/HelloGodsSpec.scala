package com.nexelem.graph.gremlins

import com.ansvia.graph.BlueprintsWrapper._
import com.ansvia.graph.annotation.Persistent
import com.tinkerpop.blueprints.Direction.{IN, OUT}

class HelloGodsSpec extends GraphSpec {
  "blueprints-scala simple access" should {
    "properly store & read simple classes" in {
      val greekRealm = connector.save(Realm("Greek"))

      val hercules = connector.save(God("Hercules", "Demigod"))
      val ares = connector.save(God("Ares", "God"))

      val realms = connector.findAll[Realm]
      realms should have size 1

      val gods = connector.findAll[God]
      gods should have size 2
    }

    "properly delete classes" in {
      val mesopotamianRealm = connector.save(Realm("Mesopotamian"))
      val cthulhuRealm = connector.save(Realm("Cthulhu"))
      val shintoRealm = connector.save(Realm("Shinto"))

      connector.findAll[Realm] should contain theSameElementsAs Seq(mesopotamianRealm, cthulhuRealm, shintoRealm)

      connector.delete(mesopotamianRealm)
      connector.findAll[Realm] should contain theSameElementsAs Seq(cthulhuRealm, shintoRealm)

      connector.delete(shintoRealm)
      connector.findAll[Realm] should contain theSameElementsAs Seq(cthulhuRealm)
    }

    "properly associate edges with vertices" in {
      val hinduRealm = connector.save(Realm("Hindu"))

      val vishnu = connector.save(God("Vishnu", "Deity"))
      connector.getLinked(hinduRealm, classOf[God], IN, "livesIn") should have size 0

      vishnu.getVertex --> "livesIn" --> hinduRealm.getVertex
      connector.getLinked(hinduRealm, classOf[God], IN, "livesIn") should have size 1

      val shakti = connector.save(God("Shakti", "Deity"))
      connector.getLinked(hinduRealm, classOf[God], IN, "livesIn") should have size 1
      connector.findAll[God] should have size 2
    }

    "properly handle linking vertices" in {
      val chineseRealm = connector.save(Realm("Chinese"))

      val hundun = connector.save(God("Hundun", "Abstract"))
      hundun.getVertex --> "livesIn" --> chineseRealm.getVertex

      val taotie = connector.save(God("Taotie", "Abstract"))
      hundun.getVertex --> "livesIn" --> chineseRealm.getVertex

      connector.getLinked(chineseRealm, classOf[God], IN, "livesIn") should have size 2

      val jiaolong = connector.save(God("Jiaolong", "Dragon"))
      val mizuchi = connector.save(God("Mizuchi", "Dragon"))

      jiaolong.getVertex --> "livesIn" --> chineseRealm.getVertex <-- "livesIn" <-- mizuchi.getVertex
      connector.getLinked(chineseRealm, classOf[God], IN, "livesIn") should have size 4

      jiaolong.getVertex <--> "aliasOf" <--> mizuchi.getVertex
      connector.getLinked(jiaolong, classOf[God], OUT, "aliasOf") should contain(mizuchi)
      connector.getLinked(mizuchi, classOf[God], OUT, "aliasOf") should contain(jiaolong)

      jiaolong.getVertex.mutual("aliasOf").printDump("Mutual aliases:", "name")
    }
  }
}

case class God (name: String, godType: String) extends BaseEntity

case class Realm (name: String) extends BaseEntity

case class Hero(name: String) extends BaseEntity {
  @Persistent
  var born = ""
}

//val achilles = new Hero("Achilles")
//achilles.born = "1250 BC"
//achilles.save()