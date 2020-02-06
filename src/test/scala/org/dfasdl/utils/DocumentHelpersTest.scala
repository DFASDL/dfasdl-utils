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

import java.io.{ InputStream, StringReader }

import org.w3c.dom.Element
import org.xml.sax.InputSource

class DocumentHelpersTest extends BaseSpec with DocumentHelpers {
  describe("DocumentHelpers") {
    describe("analyzeChoice") {
      it("should return the data elements mapped to their branches") {
        val xml = scala.io.Source
          .fromInputStream(
            getClass.getResourceAsStream("/org/dfasdl/utils/helpers/analyzeChoice-01.xml")
          )
          .mkString
        val doc            = createNormalizedDocument(xml)
        val choice         = doc.getElementById("my-choice")
        val branchElements = analyzeChoice(choice)
        branchElements.isEmpty should be(false)
        branchElements.size should be(3)
        val branch1 = doc.getElementById("branch-01")
        branchElements.contains(branch1) should be(true)
        branchElements(branch1).size should be(2)
        branchElements(branch1).head should be(doc.getElementById("branch-01-field-01"))
        branchElements(branch1)(1) should be(doc.getElementById("branch-01-field-02"))

        val branch2 = doc.getElementById("branch-02")
        branchElements.contains(branch2) should be(true)
        branchElements(branch2).size should be(3)
        branchElements(branch2).head should be(doc.getElementById("branch-02-field-01"))
        branchElements(branch2)(1) should be(doc.getElementById("branch-02-field-02"))
        branchElements(branch2)(2) should be(doc.getElementById("branch-02-field-03"))

        val branch3 = doc.getElementById("branch-03")
        branchElements.contains(branch3) should be(true)
        branchElements(branch3).size should be(1)
        branchElements(branch3).head should be(doc.getElementById("branch-03-field-01"))
      }
    }

    describe("analyzeChoiceBranch") {
      describe("if branch is null") {
        it("should return an empty list") {
          analyzeChoiceBranch(null) should be(List.empty[Element])
        }
      }

      describe("if branch is no choice element") {
        it("should return an empty list") {
          val doc    = createNewDocument()
          val branch = doc.createElement(ElementNames.ELEMENT)
          analyzeChoiceBranch(branch) should be(List.empty[Element])
        }
      }

      describe("if branch is a choice element") {
        describe("without data elements") {
          it("should return an empty list") {
            val doc    = createNewDocument()
            val branch = doc.createElement(ElementNames.CHOICE_ELEMENT)
            analyzeChoiceBranch(branch) should be(List.empty[Element])
          }
        }

        describe("with data elements") {
          it("should return a list of data elements") {
            val xml = scala.io.Source
              .fromInputStream(
                getClass
                  .getResourceAsStream("/org/dfasdl/utils/helpers/analyzeChoiceBranch-01.xml")
              )
              .mkString
            val doc      = createNormalizedDocument(xml)
            val branch   = doc.getElementById("branch-02")
            val elements = analyzeChoiceBranch(branch)
            elements.size should be(3)
            for (count <- 1 to 3) {
              val e = elements(count - 1)
              e.getAttribute("id") should be(s"branch-02-field-0$count")
            }
          }
        }
      }
    }

    describe("getChildDataElementsFromElement") {
      describe("when given an empty element") {
        it("should return an empty list") {
          val doc          = createNewDocument()
          val emptyElement = doc.createElement(ElementNames.ELEMENT)
          val response     = getChildDataElementsFromElement(emptyElement)
          response.isEmpty should be(true)
        }
      }

      describe("when given a simple sequence") {
        it("should return a list of DataElements") {
          val in: InputStream = getClass.getResourceAsStream(
            "/org/dfasdl/utils/helpers/getChildDataElementsFromElement-01.xml"
          )
          val xml = scala.io.Source.fromInputStream(in).mkString

          val builder  = createDocumentBuilder()
          val document = builder.parse(new InputSource(new StringReader(xml)))
          document.getDocumentElement.normalize()

          val element      = document.getElementById("account_list")
          val dataElements = getChildDataElementsFromElement(element)
          dataElements.size should be(6)

          val expectedMetaData = List(
            ("str", "firstname"),
            ("str", "lastname"),
            ("str", "email"),
            ("str", "birthday"),
            ("str", "phone"),
            ("str", "division")
          )

          expectedMetaData zip dataElements foreach { e =>
            e._2.getNodeName shouldEqual e._1._1
            e._2.getAttribute("id") shouldEqual e._1._2
          }
        }
      }

      describe("when given a more complex sequence") {
        it("should return a list of DataElements") {
          val in: InputStream = getClass.getResourceAsStream(
            "/org/dfasdl/utils/helpers/getChildDataElementsFromElement-02.xml"
          )
          val xml = scala.io.Source.fromInputStream(in).mkString

          val builder  = createDocumentBuilder()
          val document = builder.parse(new InputSource(new StringReader(xml)))
          document.getDocumentElement.normalize()

          val element      = document.getElementById("account_list")
          val dataElements = getChildDataElementsFromElement(element)
          dataElements.size should be(9)

          val expectedMetaData = List(
            ("str", "firstname"),
            ("str", "lastname"),
            ("str", "email"),
            ("str", "birthday"),
            ("str", "phone"),
            ("str", "division"),
            ("str", "alias"),
            ("str", "photo-url"),
            ("str", "level")
          )

          expectedMetaData zip dataElements foreach { e =>
            e._2.getNodeName shouldEqual e._1._1
            e._2.getAttribute("id") shouldEqual e._1._2
          }
        }
      }
    }

    describe("getSortedIdList") {
      describe("when given an empty dfasdl string") {
        it("should return an empty list") {
          getSortedIdList("") should be(List())
        }
      }

      describe("when given a simple dfasdl") {
        it("should return the ids in the correct order") {
          val xmlIn: InputStream =
            getClass.getResourceAsStream("/org/dfasdl/utils/helpers/getSortedIds-simple.xml")
          val xml = scala.io.Source.fromInputStream(xmlIn).mkString
          val expected =
            List("running-number", "firstname", "lastname", "full-name", "salary", "email")
          getSortedIdList(xml) should be(expected)
        }
      }

      describe("when given a complex dfasdl") {
        it("should return the ids in the correct order") {
          val xmlIn: InputStream =
            getClass.getResourceAsStream("/org/dfasdl/utils/helpers/getSortedIds-complex.xml")
          val xml = scala.io.Source.fromInputStream(xmlIn).mkString
          val expected = List(
            "columnStr",
            "column",
            "column2Str",
            "column2",
            "columns",
            "entries",
            "vcards",
            "choiceNum",
            "choiceElem",
            "choiceStr",
            "choiceElem2",
            "choice",
            "secondPart"
          )
          getSortedIdList(xml) should be(expected)
        }
      }
    }

    describe("getUniqueDataElements") {
      it("should work") {
        val xmlIn: InputStream =
          getClass.getResourceAsStream("/org/dfasdl/utils/helpers/unique-elements.xml")
        val xml                    = scala.io.Source.fromInputStream(xmlIn).mkString
        val doc                    = createNormalizedDocument(xml)
        val elements: Set[Element] = getUniqueDataElements(doc)
        elements.map(_.getAttribute("id")) should be(
          Set("centuries_row_name", "employees_row_lastname")
        )
      }
    }
  }
}
