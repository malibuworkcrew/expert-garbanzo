package com.wood.murakami

import com.wood.murakami.directory.Fields
import com.wood.murakami.query.{OrExpr, AndExpr, Equality, Parser}
import org.junit.runner.RunWith
import org.specs2.mutable.Specification
import org.specs2.runner.JUnitRunner

@RunWith(classOf[JUnitRunner])
class FilterParserTest extends Specification {
  import Fields._

  "Filter" should {
    "read basic equality" in {
      val parsed = Parser.parseFilter("STB=\"stb1\"")
      if (!parsed.successful) throw new Exception(parsed.toString)
      parsed.get.expr mustEqual Equality(Fields.STB, "stb1")
    }

    "read basic 'and'" in {
      val parsed = Parser.parseFilter("STB=\"stb1\" and TITLE=\"the goobers\"")
      if (!parsed.successful) throw new Exception(parsed.toString)
      parsed.get.expr mustEqual AndExpr(Equality(Fields.STB, "stb1"), Equality(Fields.TITLE, "the goobers"))
    }

    "read basic 'or'" in {
      val parsed = Parser.parseFilter("STB=\"stb1\" or REV=\"2.0\"")
      if (!parsed.successful) throw new Exception(parsed.toString)
      parsed.get.expr mustEqual OrExpr(Equality(Fields.STB, "stb1"), Equality(Fields.REV, "2.0"))
    }

    "deal with order of operations" in {
      val parsed = Parser.parseFilter("TITLE=\"the matrix\" and (STB=\"stb1\" or REV=\"2.0\") or DATE=\"2014-05-05\"")
      if (!parsed.successful) throw new Exception(parsed.toString)
      parsed.get.expr mustEqual
        OrExpr(AndExpr(Equality(TITLE,"the matrix"),OrExpr(Equality(STB,"stb1"),Equality(REV,"2.0"))),Equality(DATE,"2014-05-05"))
    }
  }
}