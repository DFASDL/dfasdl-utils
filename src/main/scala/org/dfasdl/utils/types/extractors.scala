/*
 * Copyright (C) 2014 - 2020  Contributors as noted in the AUTHORS.md file
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

package org.dfasdl.utils.types

import java.math.BigDecimal
import java.time.{ LocalDate, LocalTime, OffsetDateTime }

/**
  * This object contains helper functions to extract the concrete data element
  * type from the wrapper type.
  */
object extractors {

  /**
    * Extract the byte array from the given `DataElement`.
    *
    * @param d A data element.
    * @return An option to the contained byte array which may be empty.
    */
  final def getBinary(d: DataElement): Option[Array[Byte]] = d match {
    case BinaryE(v) => Option(v)
    case _          => None
  }

  /**
    * Extract the decimal number from the given `DataElement`.
    *
    * @param d A data element.
    * @return An option to the contained decimal number which may be empty.
    */
  final def getDecimal(d: DataElement): Option[BigDecimal] = d match {
    case DecimalE(v) => Option(v)
    case _           => None
  }

  /**
    * Extract the long number from the given `DataElement`.
    *
    * @param d A data element.
    * @return An option to the contained long number which may be empty.
    */
  final def getInteger(d: DataElement): Option[Long] = d match {
    case IntegerE(v) => Option(v)
    case _           => None
  }

  /**
    * Extract the local date from the given `DataElement`.
    *
    * @param d A data element.
    * @return An option to the contained local date which may be empty.
    */
  final def getLocalDate(d: DataElement): Option[LocalDate] = d match {
    case LocalDateE(v) => Option(v)
    case _             => None
  }

  /**
    * Extract the local time from the given `DataElement`.
    *
    * @param d A data element.
    * @return An option to the contained local time which may be empty.
    */
  final def getLocalTime(d: DataElement): Option[LocalTime] = d match {
    case LocalTimeE(v) => Option(v)
    case _             => None
  }

  /**
    * Extract the offset datetime from the given `DataElement`.
    *
    * @param d A data element.
    * @return An option to the contained offset  which may be empty.
    */
  final def getOffsetDateTime(d: DataElement): Option[OffsetDateTime] = d match {
    case OffsetDateTimeE(v) => Option(v)
    case _                  => None
  }

  /**
    * Extract the string from the given `DataElement`.
    *
    * @param d A data element.
    * @return An option to the contained string which may be empty.
    */
  final def getString(d: DataElement): Option[String] = d match {
    case StringE(v) => Option(v)
    case _          => None
  }

}
