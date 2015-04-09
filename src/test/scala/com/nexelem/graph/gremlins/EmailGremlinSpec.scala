package com.nexelem.graph.gremlins

class EmailGremlinSpec extends GraphSpec {
  "Gremlins queries" should {

    "should find all emails correctly" in {
//      val chineseRealm = connector.save(Realm("Chinese"))
//
//      val hundun = connector.save(God("Hundun", "Abstract"))
//      hundun.getVertex --> "livesIn" --> chineseRealm.getVertex
//
//      val taotie = connector.save(God("Taotie", "Abstract"))
//      hundun.getVertex --> "livesIn" --> chineseRealm.getVertex
//
//      connector.getLinked(chineseRealm, classOf[God], IN, "livesIn") should have size 2
//
//      val jiaolong = connector.save(God("Jiaolong", "Dragon"))
//      val mizuchi = connector.save(God("Mizuchi", "Dragon"))
//
//      jiaolong.getVertex --> "livesIn" --> chineseRealm.getVertex <-- "livesIn" <-- mizuchi.getVertex
//      connector.getLinked(chineseRealm, classOf[God], IN, "livesIn") should have size 4
//
//      jiaolong.getVertex <--> "aliasOf" <--> mizuchi.getVertex
//      connector.getLinked(jiaolong, classOf[God], OUT, "aliasOf") should contain(mizuchi)
//      connector.getLinked(mizuchi, classOf[God], OUT, "aliasOf") should contain(jiaolong)
//
//      jiaolong.getVertex.mutual("aliasOf").printDump("Mutual aliases:", "name")

//      val scalaGraph: ScalaGraph = graph
//      val abstractGods = scalaGraph.V
//        .has("_class_", classOf[God].getName)
//        .has("godType", "Abstract")
//        .toList()
//        .map {
//        toCC[God]
//      }
//
//      abstractGods.size should be(2)
//      abstractGods.find {
//        _.name == "Jialong"
//      } should be(None)
    }
  }
}