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

import org.dfasdl.utils.exceptions.LengthValidationException

/**
  * Tests for the data element processors.
  */
class DataElementProcessorsTest extends BaseSpec with DataElementProcessors {
  describe("processFormattedNumber") {
    val doc = createNewDocument()

    describe("when empty") {
      describe(s"and it has a ${AttributeNames.DEFAULT_NUMBER} attribute") {
        it(s"should set the content to the ${AttributeNames.DEFAULT_NUMBER} attribute value") {
          val element = doc.createElement("formatnum")
          element.setAttribute(AttributeNames.DEFAULT_NUMBER, "314256")

          processFormattedNumber(element).getTextContent should be("314256")
        }
      }
    }

    describe("when not empty") {
      describe(s"and it has a ${AttributeNames.DEFAULT_NUMBER} attribute") {
        it("should not replace the content") {
          val element = doc.createElement("formatnum")
          element.setAttribute(AttributeNames.DEFAULT_NUMBER, "314256")
          element.setTextContent("123,45")

          processFormattedNumber(element).getTextContent should be("123,45")
        }
      }
    }

    describe("when containing illegal characters") {
      describe("using a negative number") {
        it("should clean the number") {
          val element = doc.createElement("formatnum")
          element.setAttribute(AttributeNames.DECIMAL_SEPARATOR, ".")
          element.setTextContent("-12,345.67 €")

          processFormattedNumber(element).getTextContent should be("-12345.67")
        }
      }

      describe("using a positive number") {
        it("should clean the number") {
          val element = doc.createElement("formatnum")
          element.setAttribute(AttributeNames.DECIMAL_SEPARATOR, ",")
          element.setTextContent("12.345,67 $")

          processFormattedNumber(element).getTextContent should be("12345,67")
        }
      }
    }
  }

  describe("processNumber") {
    val doc = createNewDocument()

    describe("that is not a numeric value") {
      it("should not be valid") {
        val element = doc.createElement("num")
        element.setTextContent("12a34")

        intercept[NumberFormatException] {
          processNumber(element)
        }
      }
    }

    describe("that is a numeric value") {
      it("should be valid") {
        val element = doc.createElement("num")
        element.setTextContent("123")

        processNumber(element).getTextContent should be("123")
      }
    }

    describe("that is a negative numeric value") {
      it("should be valid") {
        val element = doc.createElement("num")
        element.setTextContent("-123")

        processNumber(element).getTextContent should be("-123")
      }
    }

    describe(s"that has a ${AttributeNames.DEFAULT_NUMBER} attribute") {
      describe("and is empty") {
        it(s"should set the content to the ${AttributeNames.DEFAULT_NUMBER} attribute value") {
          val element = doc.createElement("num")
          element.setAttribute(AttributeNames.DEFAULT_NUMBER, "314256")

          processNumber(element).getTextContent should be("314256")
        }

        describe(s"and has a ${AttributeNames.LENGTH} attribute") {
          it("should be valid") {
            val element = doc.createElement("num")
            element.setAttribute(AttributeNames.LENGTH, "1")
            element.setAttribute(AttributeNames.DEFAULT_NUMBER, "314256")

            processNumber(element).getTextContent should be("314256")
          }
        }

        describe(s"and has a ${AttributeNames.MAX_DIGITS} attribute") {
          it("should not modify the content set from the default value") {
            val element = doc.createElement("num")
            element.setAttribute(AttributeNames.MAX_DIGITS, "2")
            element.setAttribute(AttributeNames.DEFAULT_NUMBER, "314256")

            processNumber(element).getTextContent should be("314256")
          }
        }
      }

      describe("and is not empty") {
        it("should not replace the content") {
          val element = doc.createElement("num")
          element.setAttribute(AttributeNames.DEFAULT_NUMBER, "314256")
          element.setTextContent("1")

          processNumber(element).getTextContent should be("1")
        }

        describe(s"and has a ${AttributeNames.LENGTH} attribute") {
          it("should not be valid") {
            val element = doc.createElement("num")
            element.setAttribute(AttributeNames.LENGTH, "1")
            element.setAttribute(AttributeNames.DEFAULT_NUMBER, "314256")
            element.setTextContent("12345")

            intercept[LengthValidationException] {
              processNumber(element)
            }
          }
        }

        describe(s"and has a ${AttributeNames.MAX_DIGITS} attribute") {
          it("should modify the content") {
            val element = doc.createElement("num")
            element.setAttribute(AttributeNames.MAX_DIGITS, "2")
            element.setAttribute(AttributeNames.DEFAULT_NUMBER, "314256")
            element.setTextContent("12345")

            processNumber(element).getTextContent should be("12")
          }
        }
      }
    }

    describe(s"that has a ${AttributeNames.LENGTH} attribute") {
      it("should validate if it has the correct length") {
        val element = doc.createElement("num")
        element.setAttribute(AttributeNames.LENGTH, "5")
        element.setTextContent("12345")

        processNumber(element).getTextContent should be("12345")
      }

      it("should not validate if it is too short") {
        val element = doc.createElement("num")
        element.setAttribute(AttributeNames.LENGTH, "5")
        element.setTextContent("123")

        intercept[LengthValidationException] {
          processNumber(element)
        }
      }

      it("should not validate if it is too long") {
        val element = doc.createElement("num")
        element.setAttribute(AttributeNames.LENGTH, "5")
        element.setTextContent("1234567890")

        intercept[LengthValidationException] {
          processNumber(element)
        }
      }
    }

    describe(s"that has a ${AttributeNames.MAX_DIGITS} attribute") {
      describe(s"and it has more digits than ${AttributeNames.MAX_DIGITS}") {
        val element = doc.createElement("num")
        element.setAttribute(AttributeNames.MAX_DIGITS, "5")
        element.setTextContent("1234567890")

        it("should be shortened") {
          processNumber(element).getTextContent should be("12345")
        }
      }

      describe(s"and it has less digits than ${AttributeNames.MAX_DIGITS}") {
        val element = doc.createElement("num")
        element.setAttribute(AttributeNames.MAX_DIGITS, "5")
        element.setTextContent("123")

        it("should not be modified") {
          processNumber(element).getTextContent should be("123")
        }
      }

      describe(s"and it has exactly the same number of digits as ${AttributeNames.MAX_DIGITS}") {
        val element = doc.createElement("num")
        element.setAttribute(AttributeNames.MAX_DIGITS, "5")
        element.setTextContent("12345")

        it("should not be modified") {
          processNumber(element).getTextContent should be("12345")
        }
      }
    }

    describe("and is a negative numeric value") {
      describe(s"that has a ${AttributeNames.LENGTH} attribute") {
        it("should validate if it has the correct length") {
          val element = doc.createElement("num")
          element.setAttribute(AttributeNames.LENGTH, "5")
          element.setTextContent("-12345")

          processNumber(element).getTextContent should be("-12345")
        }

        it("should not validate if it is too short") {
          val element = doc.createElement("num")
          element.setAttribute(AttributeNames.LENGTH, "5")
          element.setTextContent("-123")

          intercept[LengthValidationException] {
            processNumber(element)
          }
        }

        it("should not validate if it is too long") {
          val element = doc.createElement("num")
          element.setAttribute(AttributeNames.LENGTH, "5")
          element.setTextContent("-1234567890")

          intercept[LengthValidationException] {
            processNumber(element)
          }
        }
      }

      describe(s"that has a ${AttributeNames.MAX_DIGITS} attribute") {
        describe(s"and it has more digits than ${AttributeNames.MAX_DIGITS}") {
          val element = doc.createElement("num")
          element.setAttribute(AttributeNames.MAX_DIGITS, "5")
          element.setTextContent("-1234567890")

          it("should be shortened") {
            processNumber(element).getTextContent should be("-12345")
          }
        }

        describe(s"and it has less digits than ${AttributeNames.MAX_DIGITS}") {
          val element = doc.createElement("num")
          element.setAttribute(AttributeNames.MAX_DIGITS, "5")
          element.setTextContent("-123")

          it("should not be modified") {
            processNumber(element).getTextContent should be("-123")
          }
        }

        describe(s"and it has exactly the same number of digits as ${AttributeNames.MAX_DIGITS}") {
          val element = doc.createElement("num")
          element.setAttribute(AttributeNames.MAX_DIGITS, "5")
          element.setTextContent("-12345")

          it("should not be modified") {
            processNumber(element).getTextContent should be("-12345")
          }
        }
      }
    }
  }

  describe("processString") {
    val doc = createNewDocument()

    describe(s"that has a ${AttributeNames.DEFAULT_STRING} attribute") {
      describe("and is empty") {
        it(s"should set the content to the ${AttributeNames.DEFAULT_STRING} attribute value") {
          val element = doc.createElement("str")
          element.setAttribute(
            AttributeNames.DEFAULT_STRING,
            "The Quick Brown Fox Jumps Over The Lazy Dog"
          )

          processString(element).getTextContent should be(
            "The Quick Brown Fox Jumps Over The Lazy Dog"
          )
        }

        describe(s"and has a ${AttributeNames.LENGTH} attribute") {
          it("should be valid") {
            val element = doc.createElement("str")
            element.setAttribute(AttributeNames.LENGTH, "1")
            element.setAttribute(
              AttributeNames.DEFAULT_STRING,
              "The Quick Brown Fox Jumps Over The Lazy Dog"
            )

            processString(element).getTextContent should be(
              "The Quick Brown Fox Jumps Over The Lazy Dog"
            )
          }
        }

        describe(s"and has a ${AttributeNames.MAX_LENGTH} attribute") {
          it("should not modify the content set from the default value") {
            val element = doc.createElement("str")
            element.setAttribute(AttributeNames.MAX_LENGTH, "2")
            element.setAttribute(
              AttributeNames.DEFAULT_STRING,
              "The Quick Brown Fox Jumps Over The Lazy Dog"
            )

            processString(element).getTextContent should be(
              "The Quick Brown Fox Jumps Over The Lazy Dog"
            )
          }
        }
      }

      describe("and is not empty") {
        it("should not replace the content") {
          val element = doc.createElement("str")
          element.setAttribute(
            AttributeNames.DEFAULT_STRING,
            "The Quick Brown Fox Jumps Over The Lazy Dog"
          )
          element.setTextContent("Turrican")

          processString(element).getTextContent should be("Turrican")
        }

        describe(s"and has a ${AttributeNames.LENGTH} attribute") {
          it("should not be valid") {
            val element = doc.createElement("str")
            element.setAttribute(AttributeNames.LENGTH, "1")
            element.setAttribute(
              AttributeNames.DEFAULT_STRING,
              "The Quick Brown Fox Jumps Over The Lazy Dog"
            )
            element.setTextContent("Turrican")

            intercept[LengthValidationException] {
              processString(element)
            }
          }
        }

        describe(s"and has a ${AttributeNames.MAX_LENGTH} attribute") {
          it("should modify the content") {
            val element = doc.createElement("str")
            element.setAttribute(AttributeNames.MAX_LENGTH, "8")
            element.setAttribute(
              AttributeNames.DEFAULT_STRING,
              "The Quick Brown Fox Jumps Over The Lazy Dog"
            )
            element.setTextContent("Turrican II")

            processString(element).getTextContent should be("Turrican")
          }
        }
      }
    }

    describe(s"that has a ${AttributeNames.LENGTH} attribute") {
      it("should validate if it has the correct length") {
        val element = doc.createElement("str")
        element.setAttribute(AttributeNames.LENGTH, "8")
        element.setTextContent("Turrican")

        processString(element).getTextContent should be("Turrican")
      }

      it("should not validate if it is too short") {
        val element = doc.createElement("str")
        element.setAttribute(AttributeNames.LENGTH, "8")
        element.setTextContent("Katakis")

        intercept[LengthValidationException] {
          processString(element)
        }
      }

      it("should not validate if it is too long") {
        val element = doc.createElement("str")
        element.setAttribute(AttributeNames.LENGTH, "8")
        element.setTextContent("Turrican 2")

        intercept[LengthValidationException] {
          processString(element)
        }
      }
    }

    describe(s"that has a ${AttributeNames.MAX_LENGTH} attribute") {
      describe(s"and it has more characters than ${AttributeNames.MAX_LENGTH}") {
        val element = doc.createElement("str")
        element.setAttribute(AttributeNames.MAX_LENGTH, "7")
        element.setTextContent("KatakisTurrican")

        it("should be shortened") {
          processString(element).getTextContent should be("Katakis")
        }
      }

      describe(s"and it has less characters than ${AttributeNames.MAX_LENGTH}") {
        val element = doc.createElement("str")
        element.setAttribute(AttributeNames.MAX_LENGTH, "12")
        element.setTextContent("Turrican")

        it("should not be modified") {
          processString(element).getTextContent should be("Turrican")
        }
      }

      describe(s"and it has exactly the same number of characters as ${AttributeNames.MAX_LENGTH}") {
        val element = doc.createElement("str")
        element.setAttribute(AttributeNames.MAX_LENGTH, "8")
        element.setTextContent("Turrican")

        it("should not be modified") {
          processString(element).getTextContent should be("Turrican")
        }
      }
    }

    describe(s"that is not empty and has attribute ${AttributeNames.TRIM} set to both") {
      val element = doc.createElement("str")
      element.setAttribute(AttributeNames.TRIM, "both")
      element.setTextContent(" Turrican         ")

      it("should be trimmed") {
        processString(element).getTextContent should be("Turrican")
      }
    }
  }

  describe("trimString") {
    val doc = createNewDocument()

    describe("that is empty") {
      val element = doc.createElement("str")
      element.setAttribute(AttributeNames.TRIM, "both")

      it("should do nothing") {
        trimString(element).getTextContent should be("")
      }
    }

    describe("that is not empty") {
      describe(s"and has no attribute ${AttributeNames.TRIM} set") {
        val element = doc.createElement("str")
        element.setTextContent(" foo fancy  ")

        it("should not touch the text content") {
          trimString(element).getTextContent should be(" foo fancy  ")
        }
      }

      describe(s"and has set ${AttributeNames.TRIM} to left") {
        val element = doc.createElement("str")
        element.setAttribute(AttributeNames.TRIM, "left")
        element.setTextContent(" foo fancy  ")

        it("should remove the leading whitespaces") {
          trimString(element).getTextContent should be("foo fancy  ")
        }
      }

      describe(s"and has set ${AttributeNames.TRIM} to right") {
        val element = doc.createElement("str")
        element.setAttribute(AttributeNames.TRIM, "right")
        element.setTextContent(" foo fancy  ")

        it("should remove the trailing whitespaces") {
          trimString(element).getTextContent should be(" foo fancy")
        }
      }

      describe(s"and has set ${AttributeNames.TRIM} to both") {
        val element = doc.createElement("str")
        element.setAttribute(AttributeNames.TRIM, "both")
        element.setTextContent(" foo fancy  ")

        it("should remove the leading and trailing whitespaces") {
          trimString(element).getTextContent should be("foo fancy")
        }
      }
    }
  }
}
