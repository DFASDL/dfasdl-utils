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

import org.dfasdl.utils.exceptions.LengthValidationException
import org.w3c.dom.Element

/**
  * Contains several processor functions for data elements. These functions validate the given
  * elements and modify their data according to their attributes e.g. max-digits or max-length.
  */
trait DataElementProcessors {

  /**
    * Process a given formatted number.
    * The value is cleaned regarding the allowed decimal separator. All characters that are not a number or match
    * the allowed decimal separator are removed.
    * An existing minus (`-`) at the beginning of the content is kept because it is a legal start for a number.
    *
    * @param data    The data string holding the number.
    * @param element The element describing the data e.g. holding the attributes.
    * @return The processed data string.
    */
  def processFormattedNumberData(data: String, element: Element): String =
    if (data.isEmpty && element.hasAttribute(AttributeNames.DEFAULT_NUMBER))
      element.getAttribute(AttributeNames.DEFAULT_NUMBER)
    else if (element.hasAttribute(AttributeNames.DECIMAL_SEPARATOR)) {
      val separatorRegex =
        if (element.getAttribute(AttributeNames.DECIMAL_SEPARATOR) == ".")
          "\\."
        else
          element.getAttribute(AttributeNames.DECIMAL_SEPARATOR)
      val allowedCharacters = s"[\\d$separatorRegex]"
      val cleanedData       = data filter (c ⇒ s"$c" matches allowedCharacters)
      if (data.startsWith("-"))
        s"-$cleanedData"
      else
        cleanedData
    } else
      data

  /**
    * Process a given formatted number.
    * The value is cleaned regarding the allowed decimal separator. All characters that are not a number or match
    * the allowed decimal separator are removed.
    * An existing minus (`-`) at the beginning of the content is kept because it is a legal start for a number.
    *
    * @param formattedNumber An xml element describing a formatted number.
    * @return The processed xml element.
    */
  def processFormattedNumber(formattedNumber: Element): Element = {
    formattedNumber.setTextContent(
      processFormattedNumberData(formattedNumber.getTextContent, formattedNumber)
    )
    formattedNumber
  }

  /**
    * Process the given number data.
    *
    * @param data    The data string holding the number.
    * @param element The element describing the data.
    * @return The processed data string.
    */
  @SuppressWarnings(Array("org.wartremover.warts.Throw"))
  @throws(classOf[RuntimeException])
  @throws(classOf[LengthValidationException])
  def processNumberData(data: String, element: Element): String =
    if (data.isEmpty && element.hasAttribute(AttributeNames.DEFAULT_NUMBER))
      element.getAttribute(AttributeNames.DEFAULT_NUMBER)
    else {
      val digits =
        if (data.startsWith("-"))
          data.substring(1)
        else
          data

      if (element.hasAttribute(AttributeNames.LENGTH) && digits.length != element
            .getAttribute(AttributeNames.LENGTH)
            .toInt)
        throw new LengthValidationException(
          s"Length of ${element.getAttribute(AttributeNames.LENGTH)} expected but was ${digits.length}!"
        )

      val shortenedDigits =
        if (element.hasAttribute(AttributeNames.MAX_DIGITS) && digits.length > element
              .getAttribute(AttributeNames.MAX_DIGITS)
              .toInt)
          digits.substring(0, element.getAttribute(AttributeNames.MAX_DIGITS).toInt)
        else
          digits

      if (!shortenedDigits.forall(_.isDigit))
        throw new NumberFormatException(s"Illegal number format: '$data'!")

      if (data.startsWith("-"))
        s"-$shortenedDigits"
      else
        shortenedDigits
    }

  /**
    * Process a given number element.
    *
    * @param number An xml element describing a number.
    * @return The processed xml element.
    */
  def processNumber(number: Element): Element = {
    number.setTextContent(processNumberData(number.getTextContent, number))
    number
  }

  /**
    * Process the given string data.
    *
    * @param data    The data string.
    * @param element The element describing the data.
    * @return The processed data string.
    */
  @SuppressWarnings(Array("org.wartremover.warts.Throw"))
  @throws(classOf[LengthValidationException])
  def processStringData(data: String, element: Element): String = {
    val trimmedData =
      if (element.hasAttribute(AttributeNames.TRIM))
        trimString(data, element.getAttribute(AttributeNames.TRIM))
      else
        data

    if (trimmedData.isEmpty && element.hasAttribute(AttributeNames.DEFAULT_STRING))
      element.getAttribute(AttributeNames.DEFAULT_STRING)
    else {
      if (element.hasAttribute(AttributeNames.LENGTH) && trimmedData.length != element
            .getAttribute(AttributeNames.LENGTH)
            .toInt)
        throw new LengthValidationException(
          s"Length of ${element.getAttribute(AttributeNames.LENGTH)} expected but was ${trimmedData.length}!"
        )

      if (element.hasAttribute(AttributeNames.MAX_LENGTH) && trimmedData.length > element
            .getAttribute(AttributeNames.MAX_LENGTH)
            .toInt)
        trimmedData.substring(0, element.getAttribute(AttributeNames.MAX_LENGTH).toInt)
      else
        trimmedData
    }
  }

  /**
    * Process a given string element.
    *
    * @param string An xml element describing a string.
    * @return The processed xml element.
    */
  def processString(string: Element): Element = {
    string.setTextContent(processStringData(string.getTextContent, string))
    string
  }

  /**
    * Trim the text content of a string element if the "trim" attribute is set.
    *
    * @param string An XML element describing a string.
    * @return The processed XML element.
    */
  def trimString(string: Element): Element = {
    if (string.hasAttribute(AttributeNames.TRIM))
      string.setTextContent(
        trimString(string.getTextContent, string.getAttribute(AttributeNames.TRIM))
      )
    string
  }

  /**
    * Trims the given string using the specified mode.
    *
    * @param data  The string to trim.
    * @param mode  The trim mode which may be `left`, `right` or `both`.
    * @return The trimmed string.
    */
  def trimString(data: String, mode: String): String = {
    val trimCharacters = " \t\n\r"
    mode match {
      case "left"  ⇒ data.dropWhile(c ⇒ trimCharacters.indexOf(c.toLong) >= 0)
      case "right" ⇒ data.reverse.dropWhile(c ⇒ trimCharacters.indexOf(c.toLong) >= 0).reverse
      case "both"  ⇒ data.trim
    }
  }
}
