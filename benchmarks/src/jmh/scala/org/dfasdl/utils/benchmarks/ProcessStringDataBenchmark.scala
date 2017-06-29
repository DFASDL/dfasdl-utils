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

package org.dfasdl.utils.benchmarks

import org.dfasdl.utils.{DataElementProcessors, DocumentHelpers}
import org.openjdk.jmh.annotations._

@State(Scope.Benchmark)
class ProcessStringDataBenchmark extends DataElementProcessors with DocumentHelpers {
  val dfasdlXml =
    """
      |<dfasdl xmlns="http://www.dfasdl.org/DFASDL" semantic="custom">
      |  <seq id="products">
      |    <elem id="row">
      |      <str id="str1" stop-sign=";">Lorem ipsum dolor sit amet, consetetur sadipscing elitr, sed diam nonumy eirmod tempor invidunt ut labore et dolore magna aliquyam erat, sed diam voluptua. At vero eos et accusam et justo duo dolores et ea rebum. Stet clita kasd gubergren, no sea takimata sanctus est Lorem ipsum dolor sit amet. Lorem ipsum dolor sit amet, consetetur sadipscing elitr, sed diam nonumy eirmod tempor invidunt ut labore et dolore magna aliquyam erat, sed diam voluptua. At vero eos et accusam et justo duo dolores et ea rebum. Stet clita kasd gubergren, no sea takimata sanctus est Lorem ipsum dolor sit amet. Lorem ipsum dolor sit amet, consetetur sadipscing elitr, sed diam nonumy eirmod tempor invidunt ut labore et dolore magna aliquyam erat, sed diam voluptua. At vero eos et accusam et justo duo dolores et ea rebum. Stet clita kasd gubergren, no sea takimata sanctus est Lorem ipsum dolor sit amet.</str>
      |      <str id="str2" trim="left" stop-sign=";">          Lorem ipsum dolor sit amet, consetetur sadipscing elitr, sed diam nonumy eirmod tempor invidunt ut labore et dolore magna aliquyam erat, sed diam voluptua. At vero eos et accusam et justo duo dolores et ea rebum. Stet clita kasd gubergren, no sea takimata sanctus est Lorem ipsum dolor sit amet. Lorem ipsum dolor sit amet, consetetur sadipscing elitr, sed diam nonumy eirmod tempor invidunt ut labore et dolore magna aliquyam erat, sed diam voluptua. At vero eos et accusam et justo duo dolores et ea rebum. Stet clita kasd gubergren, no sea takimata sanctus est Lorem ipsum dolor sit amet. Lorem ipsum dolor sit amet, consetetur sadipscing elitr, sed diam nonumy eirmod tempor invidunt ut labore et dolore magna aliquyam erat, sed diam voluptua. At vero eos et accusam et justo duo dolores et ea rebum. Stet clita kasd gubergren, no sea takimata sanctus est Lorem ipsum dolor sit amet.</str>
      |      <str id="str3" trim="right" stop-sign=";">Lorem ipsum dolor sit amet, consetetur sadipscing elitr, sed diam nonumy eirmod tempor invidunt ut labore et dolore magna aliquyam erat, sed diam voluptua. At vero eos et accusam et justo duo dolores et ea rebum. Stet clita kasd gubergren, no sea takimata sanctus est Lorem ipsum dolor sit amet. Lorem ipsum dolor sit amet, consetetur sadipscing elitr, sed diam nonumy eirmod tempor invidunt ut labore et dolore magna aliquyam erat, sed diam voluptua. At vero eos et accusam et justo duo dolores et ea rebum. Stet clita kasd gubergren, no sea takimata sanctus est Lorem ipsum dolor sit amet. Lorem ipsum dolor sit amet, consetetur sadipscing elitr, sed diam nonumy eirmod tempor invidunt ut labore et dolore magna aliquyam erat, sed diam voluptua. At vero eos et accusam et justo duo dolores et ea rebum. Stet clita kasd gubergren, no sea takimata sanctus est Lorem ipsum dolor sit amet.          </str>
      |      <str id="str4" trim="both" stop-sign=";">          Lorem ipsum dolor sit amet, consetetur sadipscing elitr, sed diam nonumy eirmod tempor invidunt ut labore et dolore magna aliquyam erat, sed diam voluptua. At vero eos et accusam et justo duo dolores et ea rebum. Stet clita kasd gubergren, no sea takimata sanctus est Lorem ipsum dolor sit amet. Lorem ipsum dolor sit amet, consetetur sadipscing elitr, sed diam nonumy eirmod tempor invidunt ut labore et dolore magna aliquyam erat, sed diam voluptua. At vero eos et accusam et justo duo dolores et ea rebum. Stet clita kasd gubergren, no sea takimata sanctus est Lorem ipsum dolor sit amet. Lorem ipsum dolor sit amet, consetetur sadipscing elitr, sed diam nonumy eirmod tempor invidunt ut labore et dolore magna aliquyam erat, sed diam voluptua. At vero eos et accusam et justo duo dolores et ea rebum. Stet clita kasd gubergren, no sea takimata sanctus est Lorem ipsum dolor sit amet.          </str>
      |      <str id="str5" defaultstr="DEFAULT" stop-sign=";"/>
      |      <str id="str6" defaultstr="DEFAULT" trim="left" stop-sign=";">          </str>
      |      <str id="str7" defaultstr="DEFAULT" trim="right" stop-sign=";">          </str>
      |      <str id="str8" defaultstr="DEFAULT" trim="both" stop-sign=";">          </str>
      |      <str id="str9" defaultstr="DEFAULT" max-length="64" stop-sign=";">Lorem ipsum dolor sit amet, consetetur sadipscing elitr, sed diam nonumy eirmod tempor invidunt ut labore et dolore magna aliquyam erat, sed diam voluptua. At vero eos et accusam et justo duo dolores et ea rebum. Stet clita kasd gubergren, no sea takimata sanctus est Lorem ipsum dolor sit amet. Lorem ipsum dolor sit amet, consetetur sadipscing elitr, sed diam nonumy eirmod tempor invidunt ut labore et dolore magna aliquyam erat, sed diam voluptua. At vero eos et accusam et justo duo dolores et ea rebum. Stet clita kasd gubergren, no sea takimata sanctus est Lorem ipsum dolor sit amet. Lorem ipsum dolor sit amet, consetetur sadipscing elitr, sed diam nonumy eirmod tempor invidunt ut labore et dolore magna aliquyam erat, sed diam voluptua. At vero eos et accusam et justo duo dolores et ea rebum. Stet clita kasd gubergren, no sea takimata sanctus est Lorem ipsum dolor sit amet.</str>
      |      <str id="str10" defaultstr="DEFAULT" max-length="64" trim="left" stop-sign=";">          Lorem ipsum dolor sit amet, consetetur sadipscing elitr, sed diam nonumy eirmod tempor invidunt ut labore et dolore magna aliquyam erat, sed diam voluptua. At vero eos et accusam et justo duo dolores et ea rebum. Stet clita kasd gubergren, no sea takimata sanctus est Lorem ipsum dolor sit amet. Lorem ipsum dolor sit amet, consetetur sadipscing elitr, sed diam nonumy eirmod tempor invidunt ut labore et dolore magna aliquyam erat, sed diam voluptua. At vero eos et accusam et justo duo dolores et ea rebum. Stet clita kasd gubergren, no sea takimata sanctus est Lorem ipsum dolor sit amet. Lorem ipsum dolor sit amet, consetetur sadipscing elitr, sed diam nonumy eirmod tempor invidunt ut labore et dolore magna aliquyam erat, sed diam voluptua. At vero eos et accusam et justo duo dolores et ea rebum. Stet clita kasd gubergren, no sea takimata sanctus est Lorem ipsum dolor sit amet.</str>
      |      <str id="str11" defaultstr="DEFAULT" max-length="64" trim="right" stop-sign=";">Lorem ipsum dolor sit amet, consetetur sadipscing elitr, sed diam nonumy eirmod tempor invidunt ut labore et dolore magna aliquyam erat, sed diam voluptua. At vero eos et accusam et justo duo dolores et ea rebum. Stet clita kasd gubergren, no sea takimata sanctus est Lorem ipsum dolor sit amet. Lorem ipsum dolor sit amet, consetetur sadipscing elitr, sed diam nonumy eirmod tempor invidunt ut labore et dolore magna aliquyam erat, sed diam voluptua. At vero eos et accusam et justo duo dolores et ea rebum. Stet clita kasd gubergren, no sea takimata sanctus est Lorem ipsum dolor sit amet. Lorem ipsum dolor sit amet, consetetur sadipscing elitr, sed diam nonumy eirmod tempor invidunt ut labore et dolore magna aliquyam erat, sed diam voluptua. At vero eos et accusam et justo duo dolores et ea rebum. Stet clita kasd gubergren, no sea takimata sanctus est Lorem ipsum dolor sit amet.          </str>
      |      <str id="str12" defaultstr="DEFAULT" max-length="64" trim="both" stop-sign=";">          Lorem ipsum dolor sit amet, consetetur sadipscing elitr, sed diam nonumy eirmod tempor invidunt ut labore et dolore magna aliquyam erat, sed diam voluptua. At vero eos et accusam et justo duo dolores et ea rebum. Stet clita kasd gubergren, no sea takimata sanctus est Lorem ipsum dolor sit amet. Lorem ipsum dolor sit amet, consetetur sadipscing elitr, sed diam nonumy eirmod tempor invidunt ut labore et dolore magna aliquyam erat, sed diam voluptua. At vero eos et accusam et justo duo dolores et ea rebum. Stet clita kasd gubergren, no sea takimata sanctus est Lorem ipsum dolor sit amet. Lorem ipsum dolor sit amet, consetetur sadipscing elitr, sed diam nonumy eirmod tempor invidunt ut labore et dolore magna aliquyam erat, sed diam voluptua. At vero eos et accusam et justo duo dolores et ea rebum. Stet clita kasd gubergren, no sea takimata sanctus est Lorem ipsum dolor sit amet.          </str>
      |      <str id="str13" defaultstr="DEFAULT" max-length="4" trim="both" stop-sign=";">      </str>
      |    </elem>
      |  </seq>
      |</dfasdl>
    """.stripMargin
  val dfasdlTree = createNormalizedDocument(dfasdlXml, useSchema = true)

  @Benchmark
  def testNoOp: String = {
    val e = dfasdlTree.getElementById("str1")
    processStringData(e.getTextContent, e)
  }

  @Benchmark
  def testTrimLeft: String = {
    val e = dfasdlTree.getElementById("str2")
    processStringData(e.getTextContent, e)
  }

  @Benchmark
  def testTrimRight: String = {
    val e = dfasdlTree.getElementById("str3")
    processStringData(e.getTextContent, e)
  }

  @Benchmark
  def testTrimBoth: String = {
    val e = dfasdlTree.getElementById("str4")
    processStringData(e.getTextContent, e)
  }

  @Benchmark
  def testDefault: String = {
    val e = dfasdlTree.getElementById("str5")
    processStringData(e.getTextContent, e)
  }

  @Benchmark
  def testDefaultTrimLeft: String = {
    val e = dfasdlTree.getElementById("str6")
    processStringData(e.getTextContent, e)
  }

  @Benchmark
  def testDefaultTrimRight: String = {
    val e = dfasdlTree.getElementById("str7")
    processStringData(e.getTextContent, e)
  }

  @Benchmark
  def testDefaultTrimBoth: String = {
    val e = dfasdlTree.getElementById("str8")
    processStringData(e.getTextContent, e)
  }

  @Benchmark
  def testMaxLength: String = {
    val e = dfasdlTree.getElementById("str9")
    processStringData(e.getTextContent, e)
  }

  @Benchmark
  def testMaxLengthTrimLeft: String = {
    val e = dfasdlTree.getElementById("str10")
    processStringData(e.getTextContent, e)
  }

  @Benchmark
  def testMaxLengthTrimRight: String = {
    val e = dfasdlTree.getElementById("str11")
    processStringData(e.getTextContent, e)
  }

  @Benchmark
  def testMaxLengthTrimBoth: String = {
    val e = dfasdlTree.getElementById("str12")
    processStringData(e.getTextContent, e)
  }

  @Benchmark
  def testDefaultMaxLengthTrimBoth: String = {
    val e = dfasdlTree.getElementById("str13")
    processStringData(e.getTextContent, e)
  }
}
