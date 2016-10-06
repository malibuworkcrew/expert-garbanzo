package com.wood.murakami.query

import com.wood.murakami.directory.Fields.Field

import scala.collection.mutable

trait Aggregate[T] {
  var value: T
  // Combine two Aggregate objects
  def ++=(agg: Aggregate[T]): this.type
  // Add another single entry to this Aggregate
  def +=(add: AnyRef): this.type
  def newInstance(): Aggregate[T]
  // -1 if this smaller, 0 if equal, 1 if this larger
  def compare(other: Aggregate[T]): Int

  // use to avoid erasure
  def typelessCompare(other: Any): Int = this.compare(other.asInstanceOf[this.type])
  def typelessCombine(a: Any): this.type = this ++= a.asInstanceOf[this.type]
}

case class Min(override var value: Double = Integer.MAX_VALUE
                ) extends Aggregate[Double] {
  override def +=(add: AnyRef): this.type = {
    value = Math.min(add.toString.toDouble, value)
    this
  }

  override def ++=(agg: Aggregate[Double]): this.type = {
    value = Math.min(value, agg.asInstanceOf[Min].value)
    this
  }

  def compare(other: Aggregate[Double]): Int = {
    value.compareTo(other.value)
  }

  override def toString: String = value.toString
  override def newInstance(): Min = Min()
}

case class Max(override var value: Double = Integer.MIN_VALUE
                ) extends Aggregate[Double] {
  override def +=(add: AnyRef): this.type = {
    value = Math.max(add.toString.toDouble, value)
    this
  }

  override def ++=(agg: Aggregate[Double]): this.type = {
    value = Math.max(value, agg.asInstanceOf[Max].value)
    this
  }

  def compare(other: Aggregate[Double]): Int = {
    value.compareTo(other.value)
  }

  override def toString: String = value.toString
  override def newInstance(): Max = Max()
}

case class Sum(override var value: Double = 0.0) extends Aggregate[Double] {
  override def +=(add: AnyRef): this.type = {
    value += add.toString.toDouble
    this
  }

  override def ++=(agg: Aggregate[Double]): this.type = {
    value += agg.asInstanceOf[Sum].value
    this
  }

  def compare(other: Aggregate[Double]): Int = {
    value.compareTo(other.value)
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

  def compare(other: Aggregate[mutable.HashSet[AnyRef]]): Int = {
    value.size.compareTo(other.value.size)
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

  def compare(other: Aggregate[mutable.HashSet[AnyRef]]): Int = {
    value.size.compareTo(other.value.size)
  }

  override def toString: String = value.mkString("[", ",", "]")
  override def newInstance(): Collect = Collect()
}
