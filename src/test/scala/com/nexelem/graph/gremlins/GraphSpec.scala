package com.nexelem.graph.gremlins

import com.tinkerpop.blueprints.impls.orient.{OrientBaseGraph, OrientGraphFactory}
import org.junit.runner.RunWith
import org.scalatest.junit.JUnitRunner
import org.scalatest.{BeforeAndAfter, Matchers, WordSpecLike}

@RunWith(classOf[JUnitRunner])
abstract class GraphSpec extends WordSpecLike with Matchers with BeforeAndAfter {

  val connector = new BlueprintsDbConnector
  implicit var graph: OrientBaseGraph = null

  before {
    val factory = new OrientGraphFactory("memory:test-db", "admin", "admin")
    graph = factory.getNoTx
    setup
  }

  after {
    graph.drop()
    graph.shutdown()
  }

  protected def setup {

  }
}

