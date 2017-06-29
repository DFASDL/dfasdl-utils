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
class ProcessNumberDataBenchmark extends DataElementProcessors with DocumentHelpers {
  val dfasdlXml =
    """
      |<dfasdl xmlns="http://www.dfasdl.org/DFASDL" semantic="custom">
      |  <seq id="products">
      |    <elem id="row">
      |      <num id="num1" stop-sign=";">12345</num>
      |      <num id="num2" defaultnum="12345" stop-sign=";"/>
      |      <num id="num3" max-digits="5" stop-sign=";">9223372036854775807</num>
      |      <num id="num4" max-digits="5" stop-sign=";">-9223372036854775808</num>
      |    </elem>
      |  </seq>
      |</dfasdl>
    """.stripMargin
  val dfasdlTree = createNormalizedDocument(dfasdlXml, useSchema = true)

  @Benchmark
  def testNoOp: String = {
    val e = dfasdlTree.getElementById("num1")
    processNumberData(e.getTextContent, e)
  }

  @Benchmark
  def testDefault: String = {
    val e = dfasdlTree.getElementById("num2")
    processNumberData(e.getTextContent, e)
  }

  @Benchmark
  def testMaxDigits: String = {
    val e = dfasdlTree.getElementById("num3")
    processNumberData(e.getTextContent, e)
  }

  @Benchmark
  def testMaxDigitsNeg: String = {
    val e = dfasdlTree.getElementById("num4")
    processNumberData(e.getTextContent, e)
  }
}
