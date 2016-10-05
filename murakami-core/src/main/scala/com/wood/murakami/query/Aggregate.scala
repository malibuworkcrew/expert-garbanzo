package com.wood.murakami.query

import scala.collection.mutable

trait Aggregate[T] {
  var value: T
  def ++=(agg: Aggregate[T]): this.type
  def +=(add: AnyRef): this.type
  def newInstance(): Aggregate[T]

  // use to avoid erasure
  def typelessAdd(a: Any): this.type = this += a.asInstanceOf[AnyRef]
  def typelessCombine(a: Any): this.type = this ++= a.asInstanceOf[this.type]
}

case class Min(override var value: Double = Integer.MAX_VALUE
                ) extends Aggregate[Double] {
  override def +=(add: AnyRef): this.type = {
    value = Math.min(add.asInstanceOf[Double], value)
    this
  }

  override def ++=(agg: Aggregate[Double]): this.type = {
    value = Math.min(value, agg.asInstanceOf[Min].value)
    this
  }

  override def toString: String = value.toString
  override def newInstance(): Min = Min()
}

case class Max(override var value: Double = Integer.MIN_VALUE
                ) extends Aggregate[Double] {
  override def +=(add: AnyRef): this.type = {
    value = Math.max(add.asInstanceOf[Double], value)
    this
  }

  override def ++=(agg: Aggregate[Double]): this.type = {
    value = Math.max(value, agg.asInstanceOf[Max].value)
    this
  }

  override def toString: String = value.toString
  override def newInstance(): Max = Max()
}

case class Sum(override var value: Double = 0.0) extends Aggregate[Double] {
  override def +=(add: AnyRef): this.type = {
    value += add.asInstanceOf[Double]
    this
  }

  override def ++=(agg: Aggregate[Double]): this.type = {
    value += agg.asInstanceOf[Sum].value
    this
  }

  override def toString: String = value.toString
  override def newInstance(): Sum = Sum()
}

case class Count(override var value: mutable.HashSet[AnyRef] = mutable.HashSet[AnyRef]())
  extends Aggregate[mutable.HashSet[AnyRef]] {
  override def +=(add: AnyRef): this.type = {
    value += add
    this
  }

  override def ++=(agg: Aggregate[mutable.HashSet[AnyRef]]): this.type = {
    value ++= agg.asInstanceOf[Count].value
    this
  }

  override def toString: String = value.size.toString
  override def newInstance(): Count = Count()
}

case class Collect(override var value: mutable.HashSet[AnyRef] = mutable.HashSet[AnyRef]()) extends Aggregate[mutable.HashSet[AnyRef]] {
  override def +=(add: AnyRef): this.type = {
    value += add
    this
  }

  override def ++=(agg: Aggregate[mutable.HashSet[AnyRef]]): this.type = {
    value ++= agg.asInstanceOf[Collect].value
    this
  }

  override def toString: String = value.mkString("[", ",", "]")
  override def newInstance(): Collect = Collect()
}
