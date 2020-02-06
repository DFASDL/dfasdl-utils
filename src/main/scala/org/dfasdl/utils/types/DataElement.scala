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
  * A sealed trait to wrap the data element types.
  * The general idea is to gain type safety and move away from using the
  * dreaded `Any` as base type.
  */
sealed trait DataElement extends Product with Serializable

/**
  * A data element wrapper for binary data.
  *
  * @param v An array of bytes.
  */
final case class BinaryE(v: Array[Byte]) extends DataElement

/**
  * A data element wrapper for a decimal number.
  *
  * @param v A decimal number.
  */
final case class DecimalE(v: BigDecimal) extends DataElement

/**
  * A data element wrapper for an integer number.
  *
  * @param v An integer number.
  */
final case class IntegerE(v: Long) extends DataElement

/**
  * A data element wrapper for a local date.
  *
  * @param v A local date.
  */
final case class LocalDateE(v: LocalDate) extends DataElement

/**
  * A data element wrapper for a local time.
  *
  * @param v A local time.
  */
final case class LocalTimeE(v: LocalTime) extends DataElement

/**
  * A data element wrapper for an offset datetime.
  *
  * @param v An offset datetime.
  */
final case class OffsetDateTimeE(v: OffsetDateTime) extends DataElement

/**
  * A data element wrapper for a string.
  *
  * @param v A string.
  */
final case class StringE(v: String) extends DataElement
