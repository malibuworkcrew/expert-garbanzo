package com.wood.spenk.query

case class Combiner(selector: Seq[Selector]) {
  var values = Array[Array[String]]()

  def +=(line: String): this.type = {
    val split = line.split('|')
    val fields = selector.map(s => s.select(split)).toArray
    values = values :+ fields
    this
  }

  def ++=(agg: Combiner): this.type = {
    values = values ++ agg.values
    this
  }

  def newInstance(): Combiner = Combiner(selector)

  def outputString: String = {
    values.map(_.mkString(",")).mkString("\n")
  }
}
