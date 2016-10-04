package com.wood.spenk.command

import akka.actor.ActorRef
import akka.pattern.ask
import com.webtrends.harness.command._
import com.webtrends.harness.component.spray.route.SprayGet
import com.wood.spenk.Query

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.{Future, Promise}
import scala.util.{Failure, Success}

object QueryDataCommand {
  val commandName = "QueryDataCommand"
}

class QueryDataCommand(queryLeader: ActorRef) extends Command with SprayGet {
  override def commandName: String = QueryDataCommand.commandName
  override def path: String = "/v1/query"

  override def execute[T](bean: Option[CommandBean])(implicit evidence$1: Manifest[T]): Future[BaseCommandResponse[T]] = {
    bean match {
      case Some(b) =>
        b.get("select") match {
          case Some(query) =>
            val p = Promise[BaseCommandResponse[T]]()
            queryLeader ? Query(query.toString, b.getValue[String]("filter"),
              b.getValue[String]("group")) onComplete {
              case Success(resp) => p success CommandResponse[T](Some(resp.asInstanceOf[T]), "text/plain")
              case Failure(f) => p failure CommandException(commandName, f)
            }
            p.future
          case None => Future.failed(CommandException(commandName, "Must set 'select'"))
        }
      case None =>
        Future.failed(CommandException(commandName, "Bean required"))
    }
  }
}
