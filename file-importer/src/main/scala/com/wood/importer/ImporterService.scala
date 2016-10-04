package com.wood.importer

import java.io.File

import akka.actor.Props
import akka.routing.RoundRobinPool
import akka.util.Timeout
import com.webtrends.harness.component.spray.SprayService
import com.webtrends.harness.health.HealthComponent
import com.webtrends.harness.service.messages.Ready
import com.wood.importer.command.ImportDataCommand
import com.wood.importer.workers.ImportActorPool

import scala.concurrent.Future
import scala.concurrent.duration._
import scala.util.Try

object ImporterService {
  var baseDir = "../data"
}

class ImporterService extends SprayService {
  implicit val timeout = Timeout(60 seconds)
  val importLeaders = context.actorOf(RoundRobinPool(5).props(Props[ImportActorLeader]), "ImportActorLeader")
  ImporterService.baseDir = Try { context.system.settings.config.getString("base-data-dir") } getOrElse "../data"
  val baseDir = new File(ImporterService.baseDir)
  baseDir.setWritable(true)
  ImportActorPool(10, context)

  override def serviceReceive = ({
    case Ready(meta) =>
      log.info("Import service ready to receive")
      addCommandWithProps(ImportDataCommand.commandName, Props(classOf[ImportDataCommand], importLeaders))
  }: Receive) orElse super.serviceReceive

  override def checkHealth: Future[HealthComponent] =
    Future.successful(HealthComponent("import-service", details = "No health monitored by this service"))
}
