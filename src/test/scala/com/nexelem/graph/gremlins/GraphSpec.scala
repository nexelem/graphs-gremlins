package com.nexelem.graph.gremlins

import com.tinkerpop.blueprints.impls.orient.OrientGraphFactory
import org.junit.runner.RunWith
import org.scalatest.junit.JUnitRunner
import org.scalatest.{BeforeAndAfter, FlatSpec, Matchers}

@RunWith(classOf[JUnitRunner])
class GraphSpec extends FlatSpec with Matchers with BeforeAndAfter {

  implicit val graph = new OrientGraphFactory("memory:test-db", "admin", "admin").getTx
  val connector = new BlueprintsDbConnector

  after {
    graph.rollback()
  }
}

