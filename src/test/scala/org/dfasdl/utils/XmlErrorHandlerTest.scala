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

import java.io.ByteArrayInputStream
import java.nio.charset.Charset

import org.dfasdl.utils.exceptions.XmlValidationErrorException

class XmlErrorHandlerTest extends BaseSpec with DocumentHelpers {
  describe("XmlErrorHandler") {
    describe("parsing invalid DFASDL") {
      it("should produce exceptions") {
        val dfasdl =
          """
            |<dfasdl xmlns="http://www.dfasdl.org/DFASDL" semantic="custom">
            |  <seq id="lines">
            |    <elem id="row">
            |      <str id="Attribut A" stop-sign="," max-length="20"/>
            |      <str id="Attribut B" stop-sign="\r\n" max-length="50"/>
            |    </elem>
            |  </seq>
            |</dfasdl>
          """.stripMargin

        val builder = createDocumentBuilder()
        an[XmlValidationErrorException] should be thrownBy builder.parse(
          new ByteArrayInputStream(dfasdl.getBytes(Charset.defaultCharset()))
        )
      }
    }
  }
}
