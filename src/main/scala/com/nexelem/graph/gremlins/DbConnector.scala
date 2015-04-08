package com.nexelem.graph.gremlins
import com.ansvia.graph.BlueprintsWrapper._
import com.nexelem.graph.gremlins.BlueprintsDbConnector._
import com.tinkerpop.blueprints.{Direction, Graph, Vertex}

import scala.collection.JavaConverters._
import scala.reflect.ClassTag


class BlueprintsDbConnector {
  val ClassProperty = "_class_"

  def save[T <: BaseEntity : ClassTag](entity: T)(implicit graph: Graph, m: Manifest[T]): T = {
    toCC(graph.save(entity))
  }

  def read[T <: BaseEntity : ClassTag](id: String)(implicit graph: Graph): T = {
    toCC(graph.getVertex(id))
  }

  def delete(entity: BaseEntity)(implicit graph: Graph) {
    graph.removeVertex(entity.getVertex)
  }

  def findAll[T <: BaseEntity : ClassTag]()(implicit graph: Graph): Iterable[T] = {
    val vertices = graph.getVertices(ClassProperty, implicitly[ClassTag[T]].runtimeClass.getName)
    vertices.asScala.map { vertex => toCC[T](vertex) }
  }

  def getLinked[T <: BaseEntity : ClassTag, E <: BaseEntity : ClassTag](entity: T, otherClass: Class[E], direction: Direction, labels: String*)(implicit graph: Graph): Iterable[E] = {
    val vertex = graph.getVertex(entity.getId)
    val vertices = vertex.getVertices(direction, labels.toArray : _*)
    vertices.asScala.map { v => toCC[E](v) }
  }
}

object BlueprintsDbConnector {
  def toCC[T <: BaseEntity : ClassTag](vertex: Vertex) = vertex.toCC[T].get
}
