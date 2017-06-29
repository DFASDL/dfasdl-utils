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

import org.scalatest._

class StructureElementTypeTest extends FunSpec with Matchers {
  describe("isSequence") {
    describe("when given a sequence") {
      it("should return true") {
        StructureElementType.isSequence(StructureElementType.FixedSequence) should be(true)
        StructureElementType.isSequence(StructureElementType.Sequence) should be(true)
      }
    }

    describe("when not given a sequence") {
      it("should return false") {
        StructureElementType.isSequence(StructureElementType.Choice) should be(false)
        StructureElementType.isSequence(StructureElementType.Reference) should be(false)
      }
    }
  }
}
