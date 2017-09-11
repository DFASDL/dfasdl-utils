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
import org.dfasdl.utils.types.extractors._
import org.scalatest.prop.PropertyChecks

import scala.util.{ Failure, Success }

class DataElementExtractorsTest extends BaseSpec with PropertyChecks with DataElementExtractors {

  describe("extractData") {
    val doc = createNewDocument()

    describe("given an unknown element") {
      it("should return a failure") {
        forAll("StringData") { d: String =>
          val e = doc.createElement("a" + scala.util.Random.alphanumeric.take(15).mkString) // XML node names must start with a letter.
          extractData(d, e) match {
            case Failure(f) => f shouldBe a[IllegalArgumentException]
            case Success(_) => fail("Data extraction on unknown element must fail!")
          }
        }
      }
    }

    describe("given a binary element") {
      it("should extract the correct data") {
        val e = doc.createElement(ElementNames.BINARY)

        forAll { str: String =>
          extractData(str, e) match {
            case Failure(f) => fail(f.getMessage)
            case Success(d) => getBinary(d) should contain(str.getBytes(StandardCharsets.UTF_8))
          }
        }
      }
    }

    describe("given a string element") {
      describe("containing a string") {
        it("should extract the correct data") {
          val e = doc.createElement(ElementNames.STRING)

          forAll { str: String =>
            extractData(str, e) match {
              case Failure(f) => fail(f.getMessage)
              case Success(d) => getString(d) should contain(str)
            }
          }
        }
      }

      describe("containing an integer number") {
        it("should extract the correct data") {
          val e = doc.createElement(ElementNames.NUMBER)

          forAll { n: Long =>
            extractData(n.toString, e) match {
              case Failure(f) => fail(f.getMessage)
              case Success(d) => getInteger(d) should contain(n)
            }
          }
        }
      }

      describe("containing a decimal number") {
        it("should extract the correct data") {
          val e = doc.createElement(ElementNames.NUMBER)

          forAll { l: Long =>
            whenever(l > 100L) {
              val p = 3

              e.setAttribute(AttributeNames.PRECISION, p.toString)

              val exp = java.math.BigDecimal.valueOf(l, p)

              extractData(l.toString, e) match {
                case Failure(t) => fail(t.getMessage)
                case Success(d) =>
                  withClue(s"Wrong number returned (got $d, expected $exp)!")(
                    getDecimal(d).exists(_.compareTo(exp) == 0) should be(true)
                  )
              }
            }
          }
        }
      }

      describe("containing a formatted number") {
        describe("with decimal separator") {
          it("should extract the correct data") {
            val e = doc.createElement(ElementNames.FORMATTED_NUMBER)

            forAll { bd: BigDecimal =>
              val dec   = bd.bigDecimal
              val input = dec.toPlainString.replace(".", ",,")
              e.setAttribute(AttributeNames.DECIMAL_SEPARATOR, ",,")
              extractData(input, e) match {
                case Failure(t) => fail(t)
                case Success(d) =>
                  withClue(s"Wrong number returned (got $d, expected $dec)!")(
                    getDecimal(d).exists(_.compareTo(dec) == 0) should be(true)
                  )
              }
            }
          }
        }

        describe("without decimal separator") {
          it("should extract the correct data") {
            val e = doc.createElement(ElementNames.FORMATTED_NUMBER)

            forAll { bd: BigDecimal =>
              val dec   = bd.bigDecimal
              val input = dec.toPlainString
              extractData(input, e) match {
                case Failure(t) => fail(t)
                case Success(d) =>
                  withClue(s"Wrong number returned (got $d, expected $dec)!")(
                    getDecimal(d).exists(_.compareTo(dec) == 0) should be(true)
                  )
              }
            }
          }
        }
      }

      describe("containing a date") {
        it("should extract the correct data") {
          val e = doc.createElement(ElementNames.DATE)

          forAll { zdt: ZonedDateTime =>
            val ld = zdt.toLocalDate
            val s  = ld.toString
            extractData(s, e) match {
              case Failure(t) => fail(t.getMessage)
              case Success(d) => getLocalDate(d) should contain(ld)
            }
          }
        }
      }

      describe("containing a datetime") {
        it("should extract the correct data") {
          val e = doc.createElement(ElementNames.DATETIME)

          forAll { zdt: ZonedDateTime =>
            val s = zdt.toOffsetDateTime.toString
            extractData(s, e) match {
              case Failure(t) => fail(t.getMessage)
              case Success(d) => getOffsetDateTime(d) should contain(zdt.toOffsetDateTime)
            }
          }
        }
      }

      describe("containing a time") {
        it("should extract the correct data") {
          val e = doc.createElement(ElementNames.TIME)

          forAll { zdt: ZonedDateTime =>
            val lt = zdt.toLocalTime
            val s  = lt.toString
            extractData(s, e) match {
              case Failure(t) => fail(t.getMessage)
              case Success(d) => getLocalTime(d) should contain(lt)
            }
          }
        }
      }

      describe("containing a formatted time") {
        describe("which is a date") {
          it("should extract the correct data") {
            val e = doc.createElement(ElementNames.FORMATTED_TIME)

            forAll { zdt: ZonedDateTime =>
              val ld = zdt.toLocalDate
              val s  = ld.toString

              val fm = "uuuu-MM-dd"
              e.setAttribute(AttributeNames.FORMAT, fm)

              withClue(s"Could not parse '$s' using format '$fm'!") {
                extractData(s, e) match {
                  case Failure(t) => fail(t.getMessage)
                  case Success(d) => getLocalDate(d) should contain(ld)
                }
              }
            }
          }
        }

        describe("which is a datetime") {
          it("should extract the correct data") {
            val e = doc.createElement(ElementNames.FORMATTED_TIME)

            forAll { zdt: ZonedDateTime =>
              val od = zdt.toLocalDateTime
              val s  = od.toString

              val fm = "uuuu-MM-dd'T'HH:mm:ss.SSSSSSSSS"
              e.setAttribute(AttributeNames.FORMAT, fm)

              withClue(s"Could not parse '$s' using format '$fm'!") {
                extractData(s, e) match {
                  case Failure(t) => fail(t.getMessage)
                  case Success(d) =>
                    getOffsetDateTime(d) should contain(od.atOffset(ZoneOffset.UTC))
                }
              }
            }
          }
        }

        describe("which is a time") {
          it("should extract the correct data") {
            val e = doc.createElement(ElementNames.FORMATTED_TIME)

            forAll { zdt: ZonedDateTime =>
              val lt = zdt.toLocalTime
              val s  = lt.toString

              val fm = "HH:mm:ss.SSSSSSSSS"
              e.setAttribute(AttributeNames.FORMAT, fm)

              withClue(s"Could not parse '$s' using format '$fm'!") {
                extractData(s, e) match {
                  case Failure(t) => fail(t.getMessage)
                  case Success(d) => getLocalTime(d) should contain(lt)
                }
              }
            }
          }
        }
      }
    }
  }

  describe("extractBinaryData") {
    val doc = createNewDocument()

    it("should work for standard binary strings") {
      val e = doc.createElement(ElementNames.BINARY)

      forAll { str: String =>
        extractBinaryData(str, e) should be(str.getBytes(StandardCharsets.UTF_8))
      }
    }

    it("should work for base 64 encoded strings") {
      val e = doc.createElement(ElementNames.BINARY_64)

      forAll { bs: Array[Byte] =>
        val enc = java.util.Base64.getEncoder.encodeToString(bs)
        extractBinaryData(enc, e) should be(bs)
      }
    }

    it("should work for hexadecimal encoded strings") {
      val e = doc.createElement(ElementNames.BINARY_HEX)

      forAll { bs: Array[Byte] =>
        val enc = bs.map("%02x".format(_)).mkString
        extractBinaryData(enc, e) should be(bs)
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
            s"-$p2.$suffix"
          else
            s"$p2.$suffix"

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
          case Success(n) => getInteger(n) should contain(l)
        }
      }
    }

    it("should use extractInteger for num fields with precision == 0") {
      forAll { l: Long =>
        val e = doc.createElement(ElementNames.NUMBER)
        e.setAttribute(AttributeNames.PRECISION, "0")

        extractStringData(l.toString, e) match {
          case Failure(t) => fail(t.getMessage)
          case Success(n) => getInteger(n) should contain(l)
        }
      }
    }

    it("should use extractDecimal for num fields with precision > 0") {
      forAll { l: Long =>
        whenever(l > 100L) {
          val e = doc.createElement(ElementNames.NUMBER)
          e.setAttribute(AttributeNames.PRECISION, "2")

          val expected = java.math.BigDecimal.valueOf(l, 2)

          extractStringData(l.toString, e) match {
            case Failure(t) => fail(t.getMessage)
            case Success(s) =>
              getDecimal(s).exists(_.compareTo(expected) == 0) should be(true)
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
