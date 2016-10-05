package com.wood.murakami.query

import com.wood.murakami.directory.Fields.Field

case class Combiner(selector: Seq[Selector], filter: Option[Filter], group: Option[Field]) {
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
    values = values ++ agg.values
    this
  }

  def newInstance(): Combiner = Combiner(selector, filter, group)

  def outputString: String = {
    values.map(_.mkString(",")).mkString("\n")
  }
}
