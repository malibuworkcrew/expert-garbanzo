package com.wood.murakami

import com.wood.murakami.directory.Fields.Field
import com.wood.murakami.query.{Aggregate, Filter, Selector}

// Combinable class for gathering data from files in parallel
// and combining all results
trait Combiner {
  val selector: Seq[Selector]
  val filter: Option[Filter]
  val order: Option[Seq[Field]]

  def +=(line: String): this.type
  def ++=(agg: Combiner): this.type
  def newInstance(): Combiner
  def outputString: String
  def sort: this.type
}

// For selection queries with no aggregates or group
case class SelectionCombiner(selector: Seq[Selector],
                             filter: Option[Filter],
                             order: Option[Seq[Field]]) extends Combiner {
  var values = Array[Array[String]]()

  def +=(line: String): this.type = {
    val split = line.split('|')
    if (filter.forall(_.filter(split))) {
      val fields = selector.map(s => s.select(split)).toArray
      values = values :+ fields
    }
    this
  }

  def ++=(agg: Combiner): this.type = {
    values = values ++ agg.asInstanceOf[SelectionCombiner].values
    this
  }

  def newInstance(): Combiner = SelectionCombiner(selector, filter, order)

  def outputString: String = {
    values.map(_.mkString(",")).mkString("\n")
  }

  def sort: this.type = {
    if (order.isDefined) {
      val ord = order.get
      val ordIndexes = ord.map(o => selector.indexWhere(_.field == o))
      // Sort with to use the field specific comparisons
      values = values.sortWith { case (v1, v2) =>
        val larger = ordIndexes.foldLeft(0) { case (res, ind) =>
          // If we've already found an inequality, skip remaining checks
          if (res == 0) selector(ind).field.compare(v1(ind), v2(ind))
          else res
        }
        larger > 0
      }
    }
    this
  }
}

// Aggregate class for a group and aggregates
case class AggregateCombiner(selector: Seq[Selector],
                             filter: Option[Filter],
                             group: Field,
                             order: Option[Seq[Field]]) extends Combiner {
  var values = Map[String, Array[Aggregate[_]]]()

  // Index of the grouping key in the selector
  val groupIndex = selector.indexWhere(_.field == group)
  // Agg array to store each mapping
  val baseAggs: Array[Aggregate[_]] = selector.flatMap(_.agg).toArray
  // Quickly accessible array mapping baseAggs to selector
  val baseToSelectIndexes = selector.zipWithIndex.filter(_._1.agg.isDefined).map(_._2)
  // Quickly access mapping of selector to baseAggs
  val selectToBaseIndexes = ((0 until groupIndex) :+ -1) ++ (groupIndex until (selector.size - 1))

  def +=(line: String): this.type = {
    val split = line.split('|')
    if (filter.forall(_.filter(split))) {
      val fields = selector.map(s => s.select(split)).toArray
      val key = fields(groupIndex)
      val toAdd = values.get(key) match {
        case Some(array) => array
        case None => baseAggs.map(_.newInstance())
      }
      toAdd.view.zipWithIndex.foreach { case (agg, index) =>
        agg += fields(baseToSelectIndexes(index))
      }
      values += key -> toAdd
    }
    this
  }

  def ++=(agg: Combiner): this.type = {
    agg.asInstanceOf[AggregateCombiner].values.foreach { case (key, value) =>
      values.get(key) match {
        case Some(existing) => existing.view.zip(value).foreach(it => it._1 typelessCombine it._2)
        case None => values += key -> value
      }
    }
    this
  }

  def newInstance(): Combiner = AggregateCombiner(selector, filter, group, order)

  def outputString: String = {
    val holderArray = Array.fill(selector.size)("")
    val baseSelectZip = baseToSelectIndexes.zipWithIndex
    values.map { case (groupKey, valueAggs) =>
      // Construct array of aggs and group in right order
      holderArray(groupIndex) = groupKey
      baseSelectZip.foreach { case (bIndex, i) =>
        holderArray(bIndex) = valueAggs(i).toString
      }
      holderArray.mkString(",")
    } mkString "\n"
  }

  def sort: this.type = {
    if (order.isDefined) {
      val ord = order.get
      val ordIndexes = ord.map(o => selector.indexWhere(_.field == o))
      // Sort with to use the field specific comparisons
      values = values.toSeq.sortWith { case (v1, v2) =>
        val larger = ordIndexes.foldLeft(0) { case (res, ind) =>
          // If we've already found an inequality, skip remaining checks
          if (res == 0) {
            val current = selector(ind)
            current.agg match {
              case None => current.field.compare(v1._1, v2._1)
              case Some(agg) =>
                v1._2(selectToBaseIndexes(ind)).typelessCompare(v2._2(selectToBaseIndexes(ind)))
            }
          } else res
        }
        larger > 0
      } toMap
    }
    this
  }
}
