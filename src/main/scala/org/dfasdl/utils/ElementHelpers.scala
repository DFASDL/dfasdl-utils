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

import org.dfasdl.utils.DataElementType.DataElementType
import org.dfasdl.utils.ElementNames._
import org.dfasdl.utils.ElementType.ElementType
import org.dfasdl.utils.StructureElementType.StructureElementType
import org.w3c.dom.traversal.NodeFilter
import org.w3c.dom.{ Element, Node }

import scala.annotation.tailrec

/**
  * Contains several useful functions for handling elements and their types.
  */
trait ElementHelpers {
  val binaryDataElements = List(
    BINARY,
    BINARY_64,
    BINARY_HEX
  )

  val stringDataElements = List(
    DATE,
    DATETIME,
    FORMATTED_NUMBER,
    FORMATTED_STRING,
    FORMATTED_TIME,
    NUMBER,
    STRING,
    TIME
  )

  val dataElements: List[String] = binaryDataElements ::: stringDataElements

  val expressionElements = List(
    CONSTANT,
    SCALA_EXPRESSION
  )

  val structElements = List(
    CHOICE,
    CUSTOM_ID,
    CHOICE_ELEMENT,
    ELEMENT,
    FIXED_SEQUENCE,
    REFERENCE,
    SEQUENCE
  )

  /**
    * This is a simple implementation of a `NodeFilter` that can be used to traverse only
    * data elements in a dfasdl xml tree.
    */
  class DataElementFilter extends NodeFilter {
    override def acceptNode(n: Node): Short =
      if (n.getNodeType == Node.ELEMENT_NODE && getElementType(n.getNodeName) == ElementType.DataElement)
        NodeFilter.FILTER_ACCEPT
      else
        NodeFilter.FILTER_REJECT
  }

  /**
    * Analyze the given tag name and return the DFASDL element type.
    * If the type is not known an `UnknownElement` type is returned.
    *
    * @param tagName The tag name of the element.
    * @return The element type or `UnknownElement`.
    */
  def getElementType(tagName: String): ElementType =
    if (isDataElement(tagName))
      ElementType.DataElement
    else if (isExpressionElement(tagName))
      ElementType.ExpressionElement
    else if (isStructuralElement(tagName))
      ElementType.StructuralElement
    else if (tagName == ROOT)
      ElementType.RootElement
    else
      ElementType.UnknownElement

  /**
    * Analyze the given DataElement and return the type.
    * If the type is not known an `UnknownElement` type is returned.
    *
    * @param tagName The tag name of the element.
    * @return The data element type or `UnknownElement`
    */
  def getDataElementType(tagName: String): DataElementType =
    if (isBinaryDataElement(tagName))
      DataElementType.BinaryDataElement
    else if (isStringDataElement(tagName))
      DataElementType.StringDataElement
    else
      DataElementType.UnknownElement

  /**
    * Analyze the given structural element name and return it's type.
    * If the type is not known an `Unknownelement` type is returned.
    *
    * @param tagName The tag name of the element.
    * @return The structural element type or `UnknownElement`.
    */
  def getStructureElementType(tagName: String): StructureElementType =
    if (isStructuralElement(tagName)) {
      tagName match {
        case ElementNames.CHOICE         => StructureElementType.Choice
        case ElementNames.CHOICE_ELEMENT => StructureElementType.ChoiceElement
        case ElementNames.CUSTOM_ID      => StructureElementType.CustomId
        case ElementNames.ELEMENT        => StructureElementType.Element
        case ElementNames.FIXED_SEQUENCE => StructureElementType.FixedSequence
        case ElementNames.REFERENCE      => StructureElementType.Reference
        case ElementNames.SEQUENCE       => StructureElementType.Sequence
      }
    } else
      StructureElementType.Unknown

  def isBinaryDataElement(tagName: String): Boolean = binaryDataElements.contains(tagName)

  def isDataElement(tagName: String): Boolean = dataElements.contains(tagName)

  def isStringDataElement(tagName: String): Boolean = stringDataElements.contains(tagName)

  def isExpressionElement(tagName: String): Boolean = expressionElements.contains(tagName)

  def isStructuralElement(tagName: String): Boolean = structElements.contains(tagName)

  def isUniqueDataElement(e: Element): Boolean =
    isDataElement(e.getNodeName) && e.hasAttribute(AttributeNames.UNIQUE) && e.getAttribute(
      AttributeNames.UNIQUE
    ) == "true"

  /**
    * Walk up the tree until we find the parent choice of the given node.
    *
    * @param n The start node.
    * @return An option to the parent choice element if it exists.
    */
  @SuppressWarnings(Array("org.wartremover.warts.AsInstanceOf", "org.wartremover.warts.Null"))
  @tailrec
  final def getParentChoice(n: Node): Option[Element] =
    if (n == null)
      None
    else {
      val parent = n.getParentNode
      if (parent == null)
        None
      else {
        if (getStructureElementType(parent.getNodeName) == StructureElementType.Choice)
          Option(parent.asInstanceOf[Element])
        else
          getParentChoice(parent)
      }
    }

  /**
    * Walk up the tree until we find the parent sequence of the given node.
    *
    * @param n The start node.
    * @return An option to the parent sequence element if it exists.
    */
  @SuppressWarnings(Array("org.wartremover.warts.AsInstanceOf", "org.wartremover.warts.Null"))
  @tailrec
  final def getParentSequence(n: Node): Option[Element] =
    if (n == null)
      None
    else {
      val parent = n.getParentNode
      if (parent == null)
        None
      else {
        if (StructureElementType.isSequence(getStructureElementType(parent.getNodeName)))
          Option(parent.asInstanceOf[Element])
        else
          getParentSequence(parent)
      }
    }
}
