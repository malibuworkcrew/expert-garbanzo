package com.wood.importer.workers

import akka.actor.{ActorContext, ActorRef, Props}

import scala.collection.mutable.ArrayBuffer

// Be sure to call ImportActorPool(workerLimit, context) to instantiate this class once
object ImportActorPool {
  private var context: Option[ActorContext] = None
  private val pool = collection.mutable.HashMap[String, ActorRef]()
  private var workerLimit = 10
  private val workerArray = ArrayBuffer[ActorRef]()
  private var currentWorker = 0

  def apply(workers: Int, actorContext: ActorContext): Unit = {
    workerLimit = workers
    context = Some(actorContext)
  }

  // Returns a worker right away if we've seen this date, otherwise assigns/creates a new one
  def getWorker(date: String): ActorRef = {
    pool.get(date) match {
      case Some(actor) => actor
      case None =>
        // Lock the pool and spin up a new worker for this day
        this.synchronized[ActorRef] {
          pool.get(date) match {
            case Some(actor) => actor
            case None =>
              val actor = if (workerArray.length < workerLimit) {
                val actor = context.get.actorOf(Props[ImportActorWorkers], s"ImportWorker-${workerArray.length}")
                workerArray += actor
                actor
              } else workerArray(currentWorker)
              pool(date) = actor
              currentWorker = (currentWorker + 1) % workerLimit
              actor
          }
        }
    }
  }
}
