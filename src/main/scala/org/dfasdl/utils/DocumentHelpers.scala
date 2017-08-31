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

import java.io.{ InputStream, StringReader }
import javax.xml.XMLConstants
import javax.xml.parsers.{ DocumentBuilder, DocumentBuilderFactory }
import javax.xml.transform.stream.StreamSource
import javax.xml.validation.SchemaFactory

import org.w3c.dom.{ Document, Element, Node }
import org.w3c.dom.traversal.{ DocumentTraversal, NodeFilter, TreeWalker }
import org.xml.sax.InputSource

import scala.collection.mutable.ListBuffer

/**
  * Useful functions for handling DFASDL documents.
  */
@SuppressWarnings(Array("org.wartremover.warts.AsInstanceOf", "org.wartremover.warts.Null"))
trait DocumentHelpers extends ElementHelpers {

  /**
    * Analyze the given choice and return it's branches and their data elements.
    * Only the first level(!) of the choice is used to determine the branches.
    * If there is only one `celem` beneath the choice then it will only count as one branch!
    *
    * @param choice The choice to be analyzed.
    * @return A map containing the data elements mapped to their top branch element.
    */
  def analyzeChoice(choice: Element): Map[Element, List[Element]] = {
    val child = choice.getFirstChild
    if (child == null)
      Map.empty[Element, List[Element]]
    else {
      val candidates =
        for (count <- 0 until choice.getChildNodes.getLength;
             c = choice.getChildNodes.item(count)
             if c != null && c.getNodeName == ElementNames.CHOICE_ELEMENT)
          yield
            Map(c.asInstanceOf[Element] -> getChildDataElementsFromElement(c.asInstanceOf[Element]))
      candidates.flatten.toMap
    }
  }

  /**
    * Return the list of child data elements if the given node is a choice element.
    *
    * @param branch The node to be analyzed.
    * @return A list of child data elements.
    */
  final def analyzeChoiceBranch(branch: Node): List[Element] =
    if (branch == null || branch.getNodeName != ElementNames.CHOICE_ELEMENT)
      List.empty[Element]
    else
      getChildDataElementsFromElement(branch.asInstanceOf[Element])

  /**
    * Creates a DOM document builder specific for our DFASDL schema.
    *
    * @param useSchema        Determines if we want to to create a document builder using the DFASDL schema.
    * @param schemaDefinition The schema definition, e.g. the XSD file to load.
    * @return A document builder using the DFASDL schema.
    */
  @SuppressWarnings(Array("org.wartremover.warts.DefaultArguments", "org.wartremover.warts.Null"))
  def createDocumentBuilder(useSchema: Boolean = true,
                            schemaDefinition: String = "/org/dfasdl/dfasdl.xsd"): DocumentBuilder =
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

  /**
    * Create an xml document that is normalized and uses the DFASDL schema per default.
    *
    * @param xml       A string containing the xml.
    * @param useSchema Indicate if the DFASDL schema has to be used (defaults to `true`).
    * @return A DOM xml document tree.
    */
  @SuppressWarnings(Array("org.wartremover.warts.DefaultArguments"))
  def createNormalizedDocument(xml: String, useSchema: Boolean = true): Document = {
    val builder = createDocumentBuilder(useSchema)
    val doc     = builder.parse(new InputSource(new StringReader(xml)))
    doc.getDocumentElement.normalize()
    doc
  }

  /**
    * Get the data elements that are child elements of the given element.
    *
    * @param e The parent element of the data elements.
    * @return A list of data elements that are child elements.
    */
  def getChildDataElementsFromElement(e: Element): List[Element] = {
    val traversal  = e.getOwnerDocument.asInstanceOf[DocumentTraversal]
    val treeWalker = traversal.createTreeWalker(e, NodeFilter.SHOW_ELEMENT, null, true)
    getChildDataElements(treeWalker)
  }

  /**
    * Get the data elements that are child elements of the given treewalker.
    *
    * @param treeWalker  The starting point of the recursion.
    * @return A list of elements that are DataElements of the treewalker.
    */
  @SuppressWarnings(Array("org.wartremover.warts.NonUnitStatements", "org.wartremover.warts.Var"))
  def getChildDataElements(treeWalker: TreeWalker): List[Element] = {
    val elements: ListBuffer[Element] = ListBuffer[Element]()

    val currentNode = treeWalker.getCurrentNode
    var nextNode    = treeWalker.firstChild()

    if (currentNode != null && isDataElement(currentNode.getNodeName)) {
      elements += currentNode.asInstanceOf[Element]
    }

    while (nextNode != null) {
      elements ++= getChildDataElements(treeWalker)
      nextNode = treeWalker.nextSibling()
    }
    treeWalker.setCurrentNode(currentNode)

    elements.toList
  }

  /**
    * Return all data elements from the given dfasdl document that have the `unique`
    * attribute set to `true`.
    *
    * @param doc A dfasdl xml document.
    * @return A set of elements.
    */
  def getUniqueDataElements(doc: Document): Set[Element] = {
    val es = getChildDataElementsFromElement(doc.getDocumentElement)
    es.filter(e => isUniqueDataElement(e)).toSet
  }

  /**
    * Convert the given dfasdl into an xml document, traverse it and return the list of ids in the
    * apropriate order.
    *
    * @param dfasdl A string containing a dfasdl.
    * @return A list of ids that may be empty.
    */
  def getSortedIdList(dfasdl: String): Vector[String] =
    if (dfasdl.isEmpty)
      Vector.empty[String]
    else {
      val doc = createNormalizedDocument(dfasdl)
      getSortedIdList(doc)
    }

  /**
    * Traverse the given xml document and return the list of ids in the apropriate order.
    *
    * @param doc A xml document.
    * @return A list of ids that may be empty.
    */
  @SuppressWarnings(Array("org.wartremover.warts.NonUnitStatements", "org.wartremover.warts.Var"))
  def getSortedIdList(doc: Document): Vector[String] = {
    val traversal = doc.asInstanceOf[DocumentTraversal]
    val treeWalker =
      traversal.createTreeWalker(doc.getDocumentElement, NodeFilter.SHOW_ELEMENT, null, true)

    val ids = Vector.newBuilder[String]

    def traverseLevel(treeWalker: TreeWalker): Unit = {
      val currentNode = treeWalker.getCurrentNode
      var nextNode    = treeWalker.firstChild()

      while (nextNode != null) {
        traverseLevel(treeWalker)
        nextNode = treeWalker.nextSibling()
      }

      val e = currentNode.asInstanceOf[Element]

      if (e.getNodeName != ElementNames.ROOT) {
        ids += currentNode.asInstanceOf[Element].getAttribute("id")
      }

      treeWalker.setCurrentNode(currentNode)
    }

    traverseLevel(treeWalker)

    ids.result()
  }
}
