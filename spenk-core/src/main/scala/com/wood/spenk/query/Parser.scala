package com.wood.spenk.query

import com.wood.spenk.directory.Fields

import scala.util.parsing.combinator._

object Parser extends JavaTokenParsers {
  private def parserSelect: Parser[Seq[Selector]] =
    rep1sep(selectExpr, ',') ^^ {
    case selects => selects.toSeq
  }

  private def selectExpr: Parser[Selector] =
    toUpper ~ opt(":" ~> toUpper) ^^ {
    case field ~ agg => Selector(Fields.fields.find(_.stringValue == field).get,
      None)
  }

  private def toUpper: Parser[String] =
    ident ^^ { _.toUpperCase }

  def parseSelect(s: String) = parseAll(parserSelect, s)
}
