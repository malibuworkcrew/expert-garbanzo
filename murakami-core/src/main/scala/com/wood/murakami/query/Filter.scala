package com.wood.murakami.query

import com.wood.murakami.directory.Fields.Field

trait Expr {
  def check(line: Array[String]): Boolean
}

case class Equality(field: Field, value: String) extends Expr {
  override def check(line: Array[String]): Boolean = line(field.index) == value
}

case class AndExpr(left: Expr, right: Expr) extends Expr {
  override def check(line: Array[String]): Boolean = left.check(line) && right.check(line)
}

case class OrExpr(left: Expr, right: Expr) extends Expr {
  override def check(line: Array[String]): Boolean = left.check(line) || right.check(line)
}

case class Filter(expr: Expr) {
  def filter(line: Array[String]): Boolean = {
    expr.check(line)
  }
}
