package com.wood.murakami.query

import scala.collection.mutable

trait Aggregate {
  var value: AnyRef
  def ++=(agg: this.type): this.type
  def +=(add: AnyRef): this.type
}

case class Min(override var value: Double = Integer.MAX_VALUE
                ) extends Aggregate {
  override def +=(add: AnyRef): this.type = {
    value = Math.min(add.asInstanceOf[Double], value)
    this
  }

  override def ++=(agg: this.type): this.type = {
    value = Math.min(value, agg.value)
    this
  }
}

case class Max(override var value: Double = Integer.MIN_VALUE
                ) extends Aggregate {
  override def +=(add: AnyRef): this.type = {
    value = Math.max(add.asInstanceOf[Double], value)
    this
  }

  override def ++=(agg: this.type): this.type = {
    value = Math.max(value, agg.value)
    this
  }
}

case class Sum(override var value: Double = 0.0) extends Aggregate {
  override def +=(add: AnyRef): this.type = {
    value += add.asInstanceOf[Double]
    this
  }

  override def ++=(agg: this.type): this.type = {
    value += agg.value
    this
  }
}

case class Count(override var value: Int = 0) extends Aggregate {
  val seen = mutable.HashSet[AnyRef]()

  override def +=(add: AnyRef): this.type = {
    if (!seen.contains(add)) {
      seen += add
      value += 1
    }
    this
  }

  override def ++=(agg: this.type): this.type = {
    seen ++= agg.seen
    value = seen.size
    this
  }
}

case class Collect(override var value: Int = 0) extends Aggregate {
  val seen = mutable.HashSet[AnyRef]()

  override def +=(add: AnyRef): this.type = {
    if (!seen.contains(add)) {
      seen += add
      value += 1
    }
    this
  }

  override def ++=(agg: this.type): this.type = {
    seen ++= agg.seen
    value = seen.size
    this
  }
}
