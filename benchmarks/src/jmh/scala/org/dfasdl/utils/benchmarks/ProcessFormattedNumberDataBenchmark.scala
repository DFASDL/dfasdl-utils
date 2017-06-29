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
class ProcessFormattedNumberDataBenchmark extends DataElementProcessors with DocumentHelpers {
  val dfasdlXml =
    """
      |<dfasdl xmlns="http://www.dfasdl.org/DFASDL" semantic="custom">
      |  <seq id="products">
      |    <elem id="row">
      |      <formatnum id="num1" decimal-separator="." format="(-?[\d\.,⎖]*)" stop-sign=";">12345.67890</formatnum>
      |      <formatnum id="num2" decimal-separator="." format="(-?[\d\.,⎖]*)" stop-sign=";">12345,67890</formatnum>
      |      <formatnum id="num3" decimal-separator="." format="(-?[\d\.,⎖]*)" stop-sign=";">-12345.67890</formatnum>
      |      <formatnum id="num4" decimal-separator="." format="(-?[\d\.,⎖]*)" stop-sign=";">-12345,67890</formatnum>
      |      <formatnum id="num5" decimal-separator="." format="(-?[\d\.,⎖]*)" stop-sign=";">1,234,567,890.12345</formatnum>
      |      <formatnum id="num6" decimal-separator="." format="(-?[\d\.,⎖]*)" stop-sign=";">-10,234,567,890.123456789012345</formatnum>
      |    </elem>
      |  </seq>
      |</dfasdl>
    """.stripMargin
  val dfasdlTree = createNormalizedDocument(dfasdlXml, useSchema = true)

  @Benchmark
  def testAverageNum1: String = {
    val e = dfasdlTree.getElementById("num1")
    processFormattedNumberData(e.getTextContent, e)
  }

  @Benchmark
  def testAverageNum2: String = {
    val e = dfasdlTree.getElementById("num2")
    processFormattedNumberData(e.getTextContent, e)
  }

  @Benchmark
  def testAverageNum3: String = {
    val e = dfasdlTree.getElementById("num3")
    processFormattedNumberData(e.getTextContent, e)
  }

  @Benchmark
  def testAverageNum4: String = {
    val e = dfasdlTree.getElementById("num4")
    processFormattedNumberData(e.getTextContent, e)
  }

  @Benchmark
  def testBigNum1: String = {
    val e = dfasdlTree.getElementById("num5")
    processFormattedNumberData(e.getTextContent, e)
  }

  @Benchmark
  def testBigNum2: String = {
    val e = dfasdlTree.getElementById("num6")
    processFormattedNumberData(e.getTextContent, e)
  }
}
