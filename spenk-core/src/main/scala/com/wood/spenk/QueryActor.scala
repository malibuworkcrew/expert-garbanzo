package com.wood.spenk

import akka.actor.Actor
import com.wood.spenk.directory.PathFinder

import scala.util.Success

case class Query(select: String, filter: Option[String], group: Option[String])

class QueryActor extends Actor {
  val fileName = "formatted.psv"

  override def receive: Receive = {
    case Query(select, filter, group) =>
      val paths = PathFinder.getPaths(filter)

      sender() ! Success("Read Complete")
  }
}
