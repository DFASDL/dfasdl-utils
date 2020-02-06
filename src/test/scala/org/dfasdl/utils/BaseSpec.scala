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

import javax.xml.parsers.DocumentBuilderFactory

import org.scalatest._
import org.w3c.dom.Document

/**
  * Base spec for tests.
  */
abstract class BaseSpec extends FunSpec with Matchers {

  /**
    * Creates an empty xml document.
    *
    * @return An empty DOM tree document.
    */
  def createNewDocument(): Document = {
    val factory = DocumentBuilderFactory.newInstance()
    val loader  = factory.newDocumentBuilder()
    val doc     = loader.newDocument()
    doc
  }
}
