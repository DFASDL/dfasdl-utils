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
import javax.xml.XMLConstants
import javax.xml.parsers.{ DocumentBuilder, DocumentBuilderFactory }
import javax.xml.transform.stream.StreamSource
import javax.xml.validation.SchemaFactory

import org.scalatest._
import org.w3c.dom.Element
import org.w3c.dom.traversal.{ DocumentTraversal, NodeFilter }
import org.xml.sax.InputSource

class ElementHelpersTest extends FunSpec with Matchers with ElementHelpers {
  def createDocumentBuilder(
      useSchema: Boolean = true,
      schemaDefinition: String = "/org/dfasdl/dfasdl.xsd"
  ): DocumentBuilder =
    if (useSchema) {
      val xsdMain: InputStream = getClass.getResourceAsStream(schemaDefinition)

      require(xsdMain != null, "Could not load DFASDL library (resource stream was 'null')!")

      val factory = DocumentBuilderFactory.newInstance()
      factory.setValidating(false)
      factory.setNamespaceAware(true)

      val schemaFactory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI)
      factory.setSchema(schemaFactory.newSchema(new StreamSource(xsdMain)))

      val builder = factory.newDocumentBuilder()
      builder.setErrorHandler(new XmlErrorHandler())
      builder
    } else
      DocumentBuilderFactory.newInstance().newDocumentBuilder()

  describe("DataElementFilter") {
    describe("when traversing a dfasdl") {
      it("should only return data elements") {
        val xml = scala.io.Source
          .fromInputStream(
            getClass.getResourceAsStream("/org/dfasdl/utils/helpers/data-element-filter.xml")
          )
          .mkString
        val builder = createDocumentBuilder()
        val doc     = builder.parse(new InputSource(new StringReader(xml)))
        doc.getDocumentElement.normalize()
        val traversal = doc.asInstanceOf[DocumentTraversal]
        val nodeIterator = traversal.createNodeIterator(
          doc.getDocumentElement,
          NodeFilter.SHOW_ELEMENT,
          new DataElementFilter(),
          true
        )
        var nextNode    = nodeIterator.nextNode()
        var cnt         = 0
        val expectedIds = List("columnStr", "column2Str", "choiceNum", "choiceStr")
        while (nextNode != null) {
          getElementType(nextNode.getNodeName) should be(ElementType.DataElement)
          nextNode.asInstanceOf[Element].getAttribute("id") should be(expectedIds(cnt))
          cnt += 1
          nextNode = nodeIterator.nextNode()
        }
      }
    }
  }

  describe("getElementType") {
    describe("when given a root element") {
      it("should return a RootElement") {
        getElementType("dfasdl") should be(ElementType.RootElement)
      }
    }

    describe("when given an unknown tag name") {
      it("should return an UnknownElement") {
        getElementType("totally unknown tag name!") should be(ElementType.UnknownElement)
      }
    }

    describe("when given a data element") {
      describe("which is a binary data element") {
        it("should return a DataElement") {
          getElementType(ElementNames.BINARY) should be(ElementType.DataElement)
        }
      }

      describe("which is a string data element") {
        it("should return a StringDataElement") {
          getElementType(ElementNames.STRING) should be(ElementType.DataElement)
        }
      }
    }

    describe("when given an expression element") {
      it("should return an ExpressionElement") {
        getElementType(ElementNames.SCALA_EXPRESSION) should be(ElementType.ExpressionElement)
      }
    }

    describe("when given a structural element") {
      it("should return a StructuralElement") {
        getElementType(ElementNames.CHOICE) should be(ElementType.StructuralElement)
      }
    }
  }

  describe("getDataElementType") {
    describe("when given a specific data element") {
      describe("which is a binary data element") {
        it("should return a DataElement") {
          getDataElementType(ElementNames.BINARY) should be(DataElementType.BinaryDataElement)
        }
      }

      describe("which is a string data element") {
        it("should return a StringDataElement") {
          getDataElementType(ElementNames.STRING) should be(DataElementType.StringDataElement)
        }
      }
    }

    describe("when given an unknown tag name") {
      it("should return an UnknownElement") {
        getDataElementType("totally unknown tag name!") should be(DataElementType.UnknownElement)
      }
    }
  }
}
