package com.wood.murakami.query

import com.wood.murakami.directory.Fields
import com.wood.murakami.directory.Fields.Field

import scala.util.parsing.combinator._

object Parser extends JavaTokenParsers {
  // Parse a select statement
  def parseSelect(s: String) = parseAll(SelectParser.parserSelect, s)
  // Parse a filter statement
  def parseFilter(s: String) = parseAll(FilterParser.innerExpr, s)
  // Parse a order statement
  def parseOrder(s: String) = parseAll(OrderParser.parserOrder, s)

  // PARSING CLASSES //
  private object SelectParser {
    protected[Parser] def parserSelect: Parser[Seq[Selector]] =
      rep1sep(selectExpr, ',') ^^ {
        case selects => selects.toSeq
      }

    private def selectExpr: Parser[Selector] =
      toUpper ~ opt(":" ~> toUpper) ^^ {
        case field ~ agg =>
          val matchedAgg: Option[Aggregate[_]] = agg match {
            case Some("MIN") => Some(Min())
            case Some("MAX") => Some(Max())
            case Some("SUM") => Some(Sum())
            case Some("COUNT") => Some(Count())
            case Some("COLLECT") => Some(Collect())
            case None => None
            case _ => throw new IllegalArgumentException("Nonexistent agg")
          }
          Selector(Fields.fields.find(_.stringValue == field).get, matchedAgg)
      }
  }

  private object FilterParser {
    protected[Parser] def innerExpr: Parser[Filter] = combinedPredicate ^^ { ex => Filter(ex) }

    private def combinedPredicate: Parser[Expr] = predicate ~ rep(("and" | "or") ~ predicate) ^^ {
      case left ~ right =>
        var pointer = left
        right.foreach {
          case "and" ~ rightOp => pointer = AndExpr(pointer, rightOp)
          case "or" ~ rightOp => pointer = OrExpr(pointer, rightOp)
        }
        pointer
    }

    private def predicate: Parser[Expr] = "(" ~> combinedPredicate <~ ")" | equality

    private def equality: Parser[Expr] =
      toUpper ~ ("=" ~> doubleQuotedString) ^^ {
        case field ~ value => Equality(Fields.fields.find(_.stringValue == field).get, value)
    }
  }

  private object OrderParser {
    protected[Parser] def parserOrder: Parser[Seq[Field]] =
      rep1sep(toUpper, ',') ^^ {
        case fields => fields.map(field => Fields.fields.find(_.stringValue == field).get).toSeq
      }
  }

  private def toUpper: Parser[String] =
    ident ^^ {
      _.toUpperCase
    }

  private def doubleQuotedString: Parser[String] = stringLiteral ^^ { _.replaceAll("^\"|\"$", "") }
}
