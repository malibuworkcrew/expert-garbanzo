package com.wood.murakami

import java.io.File

import akka.actor.Props
import akka.routing.RoundRobinPool
import com.webtrends.harness.component.spray.SprayService
import com.webtrends.harness.health.HealthComponent
import com.webtrends.harness.service.messages.Ready
import com.wood.murakami.command.QueryDataCommand

import scala.concurrent.Future
import scala.util.Try

object MurakamiService {
  var baseDir = "../data"
}

class MurakamiService extends SprayService {
  val queryLeaders = context.actorOf(RoundRobinPool(5).props(Props[QueryActor]), "QueryActor")
  MurakamiService.baseDir = Try { context.system.settings.config.getString("base-data-dir") } getOrElse "../data"
  val baseDir = new File(MurakamiService.baseDir)
  baseDir.setWritable(true)

  override def serviceReceive = ({
    case Ready(meta) =>
      log.info("MurakamiService ready to receive")
      addCommandWithProps(QueryDataCommand.commandName, Props(classOf[QueryDataCommand], queryLeaders))
  }: Receive) orElse super.serviceReceive

  override def checkHealth: Future[HealthComponent] =
    Future.successful(HealthComponent("murakami-service", details = "No health monitored by this service"))
}
