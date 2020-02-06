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

package org.dfasdl.utils

import org.dfasdl.utils.exceptions.{
  XmlValidationErrorException,
  XmlValidationFatalException,
  XmlValidationWarningException
}
import org.xml.sax.{ ErrorHandler, SAXParseException }

@SuppressWarnings(Array("org.wartremover.warts.Null", "org.wartremover.warts.Throw"))
class XmlErrorHandler extends ErrorHandler {
  def warning(e: SAXParseException): Unit =
    if (e.getCause != null)
      throw XmlValidationWarningException.create(e.getMessage, e.getCause)
    else
      throw new XmlValidationWarningException(e.getMessage)

  def error(e: SAXParseException): Unit =
    if (e.getCause != null)
      throw XmlValidationErrorException.create(e.getMessage, e.getCause)
    else
      throw new XmlValidationErrorException(e.getMessage)

  def fatalError(e: SAXParseException): Unit =
    if (e.getCause != null)
      throw XmlValidationFatalException.create(e.getMessage, e.getCause)
    else
      throw new XmlValidationFatalException(e.getMessage)
}
