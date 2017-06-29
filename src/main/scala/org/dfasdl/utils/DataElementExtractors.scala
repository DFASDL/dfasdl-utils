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

import java.math.BigDecimal
import java.nio.charset.{ Charset, StandardCharsets }
import java.time._
import java.time.format.DateTimeFormatter

import org.dfasdl.utils.ElementNames._
import org.w3c.dom.Element

import scala.util.{ Failure, Success, Try }

/**
  * Contains helper functions to extract the actual data from a data element and
  * return it as a correct data type.
  */
trait DataElementExtractors extends ElementHelpers {

  /**
    * Use the given data element description to convert the given data
    * string into a useful data type.
    *
    * @param d The actual data.
    * @param e The element that describes the data.
    * @return Either an error or the extracted data type.
    */
  def extractData(d: String, e: Element): Try[Any] =
    getDataElementType(e.getNodeName) match {
      case DataElementType.BinaryDataElement ⇒
        extractBinaryData(d, e)
      case DataElementType.StringDataElement ⇒
        extractStringData(d, e)
      case DataElementType.UnknownElement ⇒
        Failure(
          new IllegalArgumentException(s"Illegal data element type! ${e.getNodeName}")
        )
    }

  /**
    * Extract the binary data represented by the given string.
    *
    * @param d        A string holding a representation of the binary data.
    * @param e The element that describes the data.
    * @return Either an error or the extracted binary data.
    */
  final protected def extractBinaryData(d: String, e: Element): Try[Array[Byte]] =
    Try {
      val charset = Try(Charset.forName(e.getAttribute(AttributeNames.ENCODING)))
        .getOrElse(StandardCharsets.UTF_8)
      e.getNodeName match {
        case BINARY     ⇒ d.getBytes(charset)
        case BINARY_64  ⇒ java.util.Base64.getDecoder.decode(d)
        case BINARY_HEX ⇒ javax.xml.bind.DatatypeConverter.parseHexBinary(d)
      }
    }

  /**
    * Extract a decimal number from the given string data.
    * The string should contain a valid integer number which will be parsed
    * according to the precision attribute of the dfasdl element.
    *
    * @param d The string holding the data.
    * @param e The element that describes the data.
    * @return Either an error or a decimal number.
    */
  final protected def extractDecimal(d: String, e: Element): Try[java.math.BigDecimal] = Try {
    val base = d.toLong
    val prec = e.getAttribute(AttributeNames.PRECISION).toInt
    java.math.BigDecimal.valueOf(base, prec)
  }

  /**
    * Extract an integer number from the given string data.
    *
    * @param d The string holding the data.
    * @param e The element that describes the data.
    * @return Either an error or an integer number.
    */
  final protected def extractInteger(d: String, e: Element): Try[Long] = Try(d.toLong)

  /**
    * Extract the data from a string element.
    *
    * @param d        The string holding the data.
    * @param e The element that describes the data.
    * @return Either an error or the extracted data.
    */
  final protected def extractStringData(d: String, e: Element): Try[Any] =
    e.getNodeName match {
      case FORMATTED_STRING | STRING ⇒ Try(d)
      case NUMBER ⇒
        if (e.hasAttribute(AttributeNames.PRECISION))
          extractDecimal(d, e)
        else
          extractInteger(d, e)
      case FORMATTED_NUMBER ⇒
        val dec: Option[String] =
          if (e.hasAttribute(AttributeNames.DECIMAL_SEPARATOR))
            Option(e.getAttribute(AttributeNames.DECIMAL_SEPARATOR))
          else
            None
        val del: Option[String] = None // FIXME Add an attribute for a thousands delimiter.
        extractFormattedNumber(del, dec)(d)
      case DATE     ⇒ extractDate(d)
      case DATETIME ⇒ extractDateTime(d)
      case TIME     ⇒ extractTime(d)
      case FORMATTED_TIME ⇒
        extractFormattedTime(d, e) match {
          case Failure(t)              ⇒ Failure(t)
          case Success(Left(Left(t)))  ⇒ Success(t)
          case Success(Left(Right(t))) ⇒ Success(t)
          case Success(Right(t))       ⇒ Success(t)
        }
      case _ ⇒
        Failure(
          new IllegalArgumentException(s"Illegal string data element type! ${e.getNodeName}")
        )
    }

  /**
    * Extract a `java.time.LocalDate` from the given data string using
    * the provided format.
    *
    * @param f The format of the date string.
    * @param d   A string containing a parseable date.
    * @return Either an error or a date.
    */
  final protected def extractDate(
      f: DateTimeFormatter
  )(d: String): Try[java.time.LocalDate] =
    Try(java.time.LocalDate.parse(d, f))

  /**
    * Extract a `java.time.LocalDate` from the given data string using
    * the default ISO format.
    *
    * @param d A string containing a parseable date.
    * @return Either an error or a date.
    */
  final protected def extractDate(d: String): Try[java.time.LocalDate] =
    extractDate(DateTimeFormatter.ISO_DATE)(d)

  /**
    * Extract a `java.time.OffsetDateTime` from the given data string using
    * the provided format.
    *
    * <p>If the offset datetime could not be parsed directly the `java.time.LocalDateTime`
    * parser is tried and upon success converted into an offset datetime using the
    * `UTC` timezone.</p>
    *
    * @param f The format of the datetime string.
    * @param d   A string containing a parseable datetime.
    * @return Either an error or a datetime with an offset.
    */
  final protected def extractDateTime(
      f: DateTimeFormatter
  )(d: String): Try[java.time.OffsetDateTime] =
    Try(java.time.OffsetDateTime.parse(d, f)) match {
      case Failure(_) ⇒
        Try(
          java.time.OffsetDateTime.of(
            java.time.LocalDateTime.parse(d, f),
            ZoneOffset.UTC
          )
        )
      case ot @ Success(_) ⇒ ot
    }

  /**
    * Extract a `java.time.OffsetDateTime` from the given data string using
    * the default ISO format.
    *
    * @param d A string containing a parseable datetime.
    * @return Either an error or a datetime with an offset.
    */
  final protected def extractDateTime(d: String): Try[java.time.OffsetDateTime] =
    extractDateTime(DateTimeFormatter.ISO_OFFSET_DATE_TIME)(d)

  /**
    * Extract a `java.math.BigDecimal` from the given data string using
    * the provided thousands delimiter and decimal separator.
    *
    * @param del  The character sequence that is used as a thousands delimiter.
    * @param dec  The character sequence that is used as a decimal separator.
    * @param d A string containing a parseable decimal number.
    * @return Either an error or a decimal number.
    */
  final protected def extractFormattedNumber(del: Option[String], dec: Option[String])(
      d: String
  ): Try[BigDecimal] = Try {
    val clean = del.map(s ⇒ d.replace(s, "")).getOrElse(d)
    val input = dec.map(s ⇒ clean.replace(s, ".")).getOrElse(clean)
    new BigDecimal(input)
  }

  /**
    * Extract a `java.math.BigDecimal` from the given data string using
    * the provided decimal separator.
    *
    * @param dec  The character sequence that is used as a decimal separator.
    * @param d A string containing a parseable decimal number.
    * @return Either an error or a decimal number.
    */
  final protected def extractFormattedNumber(dec: Option[String])(d: String): Try[BigDecimal] =
    extractFormattedNumber(None, dec)(d)

  /**
    * Extract a `java.math.BigDecimal` from the given data string.
    *
    * @param d   A string containing a parseable decimal number.
    * @return Either an error or a decimal number.
    */
  final protected def extractFormattedNumber(d: String): Try[BigDecimal] =
    extractFormattedNumber(None, None)(d)

  /**
    * Parse the given input string using the provided format and try to return
    * a matching temporal type. The following priority regarding possibly matching
    * temporals is used:
    *
    * <ol>
    *   <li>`java.time.OffsetDateTime`</li>
    *   <li>`java.time.LocalDate`</li>
    *   <li>`java.time.LocalTime`</li>
    * </ol>
    *
    * @param f The format of the input string.
    * @param d   A string containing a parseable input.
    * @return Either an error or a matching temporal type in respect to the described ordering.
    */
  final protected def extractFormattedTime(
      f: DateTimeFormatter
  )(d: String): Try[Either[Either[LocalTime, LocalDate], OffsetDateTime]] = {
    val dt: Try[OffsetDateTime] = extractDateTime(f)(d)
    val ld: Try[LocalDate]      = extractDate(f)(d)
    val lt: Try[LocalTime]      = extractTime(f)(d)
    (dt, ld, lt) match {
      case (Success(t), _, _)          ⇒ Success(Right(t))
      case (Failure(_), Success(t), _) ⇒ Success(Left(Right(t)))
      case (Failure(_), _, Success(t)) ⇒ Success(Left(Left(t)))
      case (Failure(x), Failure(_), Failure(_)) ⇒
        Failure(
          new Error("Could not parse OffsetDateTime, LocalDate or LocalTime from given input!", x)
        )
    }
  }

  /**
    * Parse the given input string using the format provided by the element and try to return
    * a matching temporal type. The following priority regarding possibly matching
    * temporals is used:
    *
    * <ol>
    *   <li>`java.time.OffsetDateTime`</li>
    *   <li>`java.time.LocalDate`</li>
    *   <li>`java.time.LocalTime`</li>
    * </ol>
    *
    * @param d A string containing a parseable input.
    * @param e The element that describes the data.
    * @return Either an error or a matching temporal type in respect to the described ordering.
    */
  final protected def extractFormattedTime(
      d: String,
      e: Element
  ): Try[Either[Either[LocalTime, LocalDate], OffsetDateTime]] =
    for {
      f ← Try(DateTimeFormatter.ofPattern(e.getAttribute(AttributeNames.FORMAT)))
      t ← extractFormattedTime(f)(d)
    } yield t

  /**
    * Extract a `java.time.LocalTime` from the given data string using
    * the provided format.
    *
    * @param f The format of the time string.
    * @param d   A string containing a parseable time.
    * @return Either an error or a date.
    */
  final protected def extractTime(
      f: DateTimeFormatter
  )(d: String): Try[java.time.LocalTime] =
    Try(java.time.LocalTime.parse(d, f))

  /**
    * Extract a `java.time.LocalTime` from the given data string using
    * the default ISO format.
    *
    * @param d A string containing a parseable time.
    * @return Either an error or a date.
    */
  final protected def extractTime(d: String): Try[java.time.LocalTime] =
    extractTime(DateTimeFormatter.ISO_LOCAL_TIME)(d)

}
