package com.nexelem.graph.gremlins

import com.tinkerpop.blueprints.impls.orient.{OrientBaseGraph, OrientGraphFactory}
import org.junit.runner.RunWith
import org.scalatest.junit.JUnitRunner
import org.scalatest.{BeforeAndAfter, Matchers, WordSpecLike}

@RunWith(classOf[JUnitRunner])
class GraphSpec extends WordSpecLike with Matchers with BeforeAndAfter {

  implicit var graph: OrientBaseGraph = null
  val connector = new BlueprintsDbConnector

  before {
    graph = new OrientGraphFactory("memory:test-db", "admin", "admin").getNoTx
    setup
  }

  after {
    graph.shutdown()
  }

  protected def setup {

  }
}

