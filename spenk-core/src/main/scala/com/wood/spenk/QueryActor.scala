package com.wood.spenk

import akka.actor.Actor
import com.webtrends.harness.app.HActor
import com.wood.spenk.directory.PathFinder
import com.wood.spenk.executors.QueryExecutors
import com.wood.spenk.query.{Combiner, Parser}
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.util.{Failure, Success}

case class Query(select: String, filter: Option[String], group: Option[String])

class QueryActor extends HActor {
  val fileName = "formatted.psv"

  override def receive: Receive = {
    case Query(select, filter, group) =>
      try {
        val paths = PathFinder.getPaths(filter, fileName)
        val selects = Parser.parseSelect(select)
        if (!selects.successful) throw new IllegalArgumentException(selects.toString)
        val combiner = Combiner(selects.get)
        val callback = sender()
        val futures = Future.sequence(paths.map(path => Future {
          val exec = new QueryExecutors(path, combiner.newInstance())
          retry[Combiner](3)(exec.execute)
        }) toList)
        futures.onComplete {
          case Success(s) =>
            if (s.size <= 1) callback ! Success(s.headOption)
            else {
              val combined = s.tail.foldLeft(s.head) { case (a, b) =>
                a ++= b
              }
              callback ! combined.outputString
            }
          case Failure(f) => callback ! Failure(f)
        }
      } catch {
        case ex: Throwable =>
          log.error(ex, "Query Failure")
          sender() ! Failure(ex)
      }
  }

  def retry[T](n: Int)(fn: => T): T = {
    try fn catch {
      case ex: Throwable =>
        if (n > 1) retry(n - 1)(fn)
        else throw ex
    }
  }
}
