/*
 * Copyright (C) 2014 - 2017  Contributors as noted in the AUTHORS.md file
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.dfasdl.utils

import java.nio.charset.StandardCharsets
import java.time.format.DateTimeFormatter
import java.time.{ OffsetDateTime, ZoneOffset, ZonedDateTime }

import com.fortysevendeg.scalacheck.datetime.jdk8.ArbitraryJdk8._
import org.scalatest.prop.PropertyChecks

import scala.util.{ Failure, Success }

class DataElementExtractorsTest extends BaseSpec with PropertyChecks with DataElementExtractors {

  describe("extractBinaryData") {
    val doc = createNewDocument()

    it("should work for standard binary strings") {
      val e = doc.createElement(ElementNames.BINARY)

      forAll { str: String =>
        extractBinaryData(str, e) match {
          case Failure(t) => fail(t.getMessage)
          case Success(b) => b should be(str.getBytes(StandardCharsets.UTF_8))
        }
      }
    }

    it("should work for base 64 encoded strings") {
      val e = doc.createElement(ElementNames.BINARY_64)

      forAll { bs: Array[Byte] =>
        val enc = java.util.Base64.getEncoder.encodeToString(bs)
        extractBinaryData(enc, e) match {
          case Failure(t) => fail(t.getMessage)
          case Success(b) => b should be(bs)
        }
      }
    }

    it("should work for hexadecimal encoded strings") {
      val e = doc.createElement(ElementNames.BINARY_HEX)

      forAll { bs: Array[Byte] =>
        val enc = bs.map("%02x".format(_)).mkString
        extractBinaryData(enc, e) match {
          case Failure(t) => fail(t.getMessage)
          case Success(b) => b should be(bs)
        }
      }
    }
  }

  describe("extractDate") {
    it("should work for correct dates") {
      forAll { zdt: ZonedDateTime =>
        val ld = zdt.toLocalDate
        val s  = ld.toString
        extractDate(s) match {
          case Failure(t) => fail(t.getMessage)
          case Success(t) => t should be(ld)
        }
      }
    }

    it("should work for correct dates with correct format") {
      forAll { zdt: ZonedDateTime =>
        val f  = DateTimeFormatter.ISO_ZONED_DATE_TIME
        val ld = zdt.toLocalDate
        val s  = zdt.toString
        extractDate(f)(s) match {
          case Failure(t) => fail(t.getMessage)
          case Success(t) => t should be(ld)
        }
      }
    }
  }

  describe("extractDateTime") {
    it("should work for correct offset datetimes") {
      forAll { zdt: ZonedDateTime =>
        val s = zdt.toOffsetDateTime.toString
        extractDateTime(s) match {
          case Failure(t) => fail(t.getMessage)
          case Success(t) => t should be(zdt.toOffsetDateTime)
        }
      }
    }

    it("should work for correct zoned datetimes with correct format") {
      forAll { zdt: ZonedDateTime =>
        val f = DateTimeFormatter.ISO_ZONED_DATE_TIME
        val s = zdt.toString
        extractDateTime(f)(s) match {
          case Failure(t) => fail(t.getMessage)
          case Success(t) => t should be(zdt.toOffsetDateTime)
        }
      }
    }

    it("should work for correct local datetimes with correct format") {
      forAll { zdt: ZonedDateTime =>
        val f = DateTimeFormatter.ISO_LOCAL_DATE_TIME
        val s = zdt.toLocalDateTime.toString
        extractDateTime(f)(s) match {
          case Failure(t) => fail(t.getMessage)
          case Success(t) => t should be(OffsetDateTime.of(zdt.toLocalDateTime, ZoneOffset.UTC))
        }
      }
    }

    it("should fail for arbitrary strings") {
      forAll { str: String =>
        extractDateTime(str) match {
          case Failure(_) => // We expect a failure here.
          case Success(_) => fail("Parsing invalid strings should fail!")
        }
      }
    }
  }

  describe("extractDecimal") {
    val doc = createNewDocument()

    it("should work for valid input") {
      forAll { l: Long =>
        val e = doc.createElement(ElementNames.NUMBER)
        val p: Int =
          if (l < 100L)
            0
          else
            3

        e.setAttribute(AttributeNames.PRECISION, p.toString)

        val exp = java.math.BigDecimal.valueOf(l, p)

        extractDecimal(l.toString, e) match {
          case Failure(t) => fail(t.getMessage)
          case Success(d) => d.compareTo(exp) should be(0)
        }
      }
    }
  }

  describe("extractFormattedNumber") {
    describe("without thousands delimiter") {
      describe("without decimal separator") {
        describe("using valid input") {
          it("should work") {
            forAll { d: BigDecimal =>
              val dec   = d.bigDecimal
              val input = dec.toPlainString
              extractFormattedNumber(input) match {
                case Failure(t) => fail(t)
                case Success(n) => n.compareTo(dec) should be(0)
              }
            }
          }
        }
      }

      describe("with decimal separator") {
        describe("using valid input") {
          it("should work") {
            forAll { d: BigDecimal =>
              val dec   = d.bigDecimal
              val input = dec.toPlainString.replace(".", ",,")
              extractFormattedNumber(Option(",,"))(input) match {
                case Failure(t) => fail(t)
                case Success(n) => n.compareTo(dec) should be(0)
              }
            }
          }
        }
      }
    }

    describe("with thousands delimiter") {

      def addThousandsDelimiter(del: String, dec: BigDecimal): String = {
        val n: java.math.BigDecimal = dec.bigDecimal
        val suffix                  = n.toPlainString.dropWhile(_ != '.').drop(1)
        val prefix =
          if (dec < 0)
            n.toPlainString.drop(1).takeWhile(_ != '.')
          else
            n.toPlainString.takeWhile(_ != '.')

        val p = prefix.reverse.zipWithIndex
          .map(
            t =>
              if ((t._2 + 1) % 3 == 0)
                s"${t._1}$del"
              else
                t._1
          )
          .mkString
          .reverse

        val p2 =
          if (p.startsWith(del))
            p.drop(del.length)
          else
            p

        val p3 =
          if (dec < 0)
            s"-${p2}.$suffix"
          else
            s"${p2}.$suffix"

        p3
      }

      describe("without decimal separator") {
        describe("using valid input") {
          it("should work") {
            forAll { d: BigDecimal =>
              val dec   = d.bigDecimal
              val input = addThousandsDelimiter("ABBA", d)
              extractFormattedNumber(Option("ABBA"), None)(input) match {
                case Failure(t) => fail(t)
                case Success(n) => n.compareTo(dec) should be(0)
              }
            }
          }
        }
      }

      describe("with decimal separator") {
        describe("using valid input") {
          it("should work") {
            forAll { d: BigDecimal =>
              val dec   = d.bigDecimal
              val input = addThousandsDelimiter("ABBA", d).replace(".", "FANCY")
              extractFormattedNumber(Option("ABBA"), Option("FANCY"))(input) match {
                case Failure(t) => fail(t)
                case Success(n) => n.compareTo(dec) should be(0)
              }
            }
          }
        }
      }
    }
  }

  describe("extractFormattedTime") {
    describe("using valid input and format") {
      it("should prefer OffsetDateTime first") {
        forAll { zdt: ZonedDateTime =>
          val input  = zdt.toString
          val format = DateTimeFormatter.ISO_ZONED_DATE_TIME
          extractFormattedTime(format)(input) match {
            case Failure(t) => fail(t)
            case Success(t) => t should be(Right(zdt.toOffsetDateTime))
          }
        }
      }

      it("should prefer LocalDate second") {
        forAll { zdt: ZonedDateTime =>
          val input  = zdt.toLocalDate.toString
          val format = DateTimeFormatter.ISO_LOCAL_DATE
          extractFormattedTime(format)(input) match {
            case Failure(t) => fail(t)
            case Success(t) => t should be(Left(Right(zdt.toLocalDate)))
          }
        }
      }

      it("should prefer LocalTime last") {
        forAll { zdt: ZonedDateTime =>
          val input  = zdt.toLocalTime.toString
          val format = DateTimeFormatter.ISO_LOCAL_TIME
          extractFormattedTime(format)(input) match {
            case Failure(t) => fail(t)
            case Success(t) => t should be(Left(Left(zdt.toLocalTime)))
          }
        }
      }
    }

    describe("using invalid input") {
      it("should return an error") {
        val input  = "I am just a lonely string..."
        val format = DateTimeFormatter.ISO_OFFSET_DATE_TIME
        extractFormattedTime(format)(input) match {
          case Failure(t) =>
            t.getMessage should be(
              "Could not parse OffsetDateTime, LocalDate or LocalTime from given input!"
            )
          case Success(_) => fail("Error expected for invalid input!")
        }
      }
    }
  }

  describe("extractInteger") {
    val doc = createNewDocument()

    it("should work for valid input") {
      forAll { l: Long =>
        val e = doc.createElement(ElementNames.NUMBER)

        extractInteger(l.toString, e) match {
          case Failure(t) => fail(t.getMessage)
          case Success(n) => n should be(l)
        }
      }
    }
  }

  describe("extractStringData") {
    val doc = createNewDocument()

    it("should use extractInteger for num fields without precision") {
      forAll { l: Long =>
        val e = doc.createElement(ElementNames.NUMBER)

        extractStringData(l.toString, e) match {
          case Failure(t) => fail(t.getMessage)
          case Success(n) =>
            n shouldBe a[java.lang.Long]
            n should be(l)
        }
      }
    }

    it("should use extractInteger for num fields with precision == 0") {
      forAll { l: Long =>
        val e = doc.createElement(ElementNames.NUMBER)
        e.setAttribute(AttributeNames.PRECISION, "0")

        extractStringData(l.toString, e) match {
          case Failure(t) => fail(t.getMessage)
          case Success(n) =>
            n shouldBe a[java.lang.Long]
            n should be(l)
        }
      }
    }

    it("should use extractDecimal for num fields with precision > 0") {
      forAll { l: Long =>
        whenever(l > 100L) {
          val e = doc.createElement(ElementNames.NUMBER)
          e.setAttribute(AttributeNames.PRECISION, "2")

          extractStringData(l.toString, e) match {
            case Failure(t) => fail(t.getMessage)
            case Success(n: java.math.BigDecimal) =>
              n.compareTo(java.math.BigDecimal.valueOf(l, 2)) should be(0)
            case Success(t) => fail(s"Invalid return type '${t.getClass.getCanonicalName}'!")
          }
        }
      }
    }
  }

  describe("extractTime") {
    it("should work for correct times") {
      forAll { zdt: ZonedDateTime =>
        val lt = zdt.toLocalTime
        val s  = lt.toString
        extractTime(s) match {
          case Failure(t) => fail(t.getMessage)
          case Success(t) => t should be(lt)
        }
      }
    }

    it("should work for correct datetimes with correct format") {
      forAll { zdt: ZonedDateTime =>
        val f = DateTimeFormatter.ISO_OFFSET_DATE_TIME
        val s = zdt.toOffsetDateTime.toString
        extractTime(f)(s) match {
          case Failure(t) => fail(t.getMessage)
          case Success(t) => t should be(zdt.toLocalTime)
        }
      }
    }
  }
}
