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

import org.dfasdl.utils.{DataElementExtractors, DocumentHelpers}
import org.openjdk.jmh.annotations._

@State(Scope.Benchmark)
class ExtractStringDataBenchmark extends DataElementExtractors with DocumentHelpers {
  val dfasdlXml =
    """
      |<dfasdl xmlns="http://www.dfasdl.org/DFASDL" semantic="custom">
      |  <seq id="products">
      |    <elem id="row">
      |      <str id="str" stop-sign=";"/>
      |      <formatstr id="formatstr" format="\w+" stop-sign=";"/>
      |      <formatnum id="formatnum" decimal-separator="." format="(-?[\d\.,⎖]*)" stop-sign=";"/>
      |      <num id="num" stop-sign=";"/>
      |      <num id="numWithPrec" precision="5" stop-sign=";"/>
      |      <date id="date" stop-sign=";"/>
      |      <datetime id="datetime" stop-sign=";"/>
      |      <time id="time" stop-sign=";"/>
      |      <formattime id="formattimeDateTime" format="dd.MM.yyyy HH:mm:ss" stop-sign=";"/>
      |      <formattime id="formattimeDateTimeZoned" format="dd.MM.yyyy HH:mm:ss Z" stop-sign=";"/>
      |      <formattime id="formattimeDate" format="dd.MM.yyyy" stop-sign=";"/>
      |      <formattime id="formattimeTime" format="HH:mm:ss"/>
      |    </elem>
      |  </seq>
      |</dfasdl>
    """.stripMargin
  val dfasdlTree = createNormalizedDocument(dfasdlXml, useSchema = true)
  val elemString = dfasdlTree.getElementById("str")
  val elemStringData = "Lorem ipsum dolor sit amet, consetetur sadipscing elitr, sed diam nonumy eirmod tempor invidunt ut labore et dolore magna aliquyam erat, sed diam voluptua. At vero eos et accusam et justo duo dolores et ea rebum. Stet clita kasd gubergren, no sea takimata sanctus est Lorem ipsum dolor sit amet. Lorem ipsum dolor sit amet, consetetur sadipscing elitr, sed diam nonumy eirmod tempor invidunt ut labore et dolore magna aliquyam erat, sed diam voluptua. At vero eos et accusam et justo duo dolores et ea rebum. Stet clita kasd gubergren, no sea takimata sanctus est Lorem ipsum dolor sit amet. Lorem ipsum dolor sit amet, consetetur sadipscing elitr, sed diam nonumy eirmod tempor invidunt ut labore et dolore magna aliquyam erat, sed diam voluptua. At vero eos et accusam et justo duo dolores et ea rebum. Stet clita kasd gubergren, no sea takimata sanctus est Lorem ipsum dolor sit amet."
  val elemFormattedString = dfasdlTree.getElementById("formatstr")
  val elemNum = dfasdlTree.getElementById("num")
  val elemNumData = "1234567890"
  val elemNumWithPrec = dfasdlTree.getElementById("numWithPrec")
  val elemNumWithPrecData = "1234567890"
  val elemFormatNum = dfasdlTree.getElementById("formatnum")
  val elemFormatNumData = "123456.7890"
  val elemDate = dfasdlTree.getElementById("date")
  val elemDateData = "1999-12-31"
  val elemDateTime = dfasdlTree.getElementById("datetime")
  val elemDateTimeData = "1999-12-31T23:59:59.9999"
  val elemTime = dfasdlTree.getElementById("time")
  val elemTimeData = "23:59:59"
  val elemFormattedTimeDateTime = dfasdlTree.getElementById("formattimeDateTime")
  val elemFormattedTimeDateTimeData = "31.12.1999 23:59:59"
  val elemFormattedTimeDateTimeZoned = dfasdlTree.getElementById("formattimeDateTimeZoned")
  val elemFormattedTimeDateTimeDataZoned = "31.12.1999 23:59:59 +02"
  val elemFormattedTimeDate = dfasdlTree.getElementById("formattimeDate")
  val elemFormattedTimeDateData = "31.12.1999"
  val elemFormattedTimeTime = dfasdlTree.getElementById("formattimeTime")
  val elemFormattedTimeTimeData = "23:59:59"

  @Benchmark
  def testExtractString = {
    extractStringData(elemStringData, elemString)
  }

  @Benchmark
  def testExtractFormattedString = {
    extractStringData(elemStringData, elemFormattedString)
  }

  @Benchmark
  def testExtractNumber = {
    extractStringData(elemNumData, elemNum)
  }

  @Benchmark
  def testExtractNumberWithPrecision = {
    extractStringData(elemNumWithPrecData, elemNumWithPrec)
  }

  @Benchmark
  def testExtractFormattedNumber = {
    extractStringData(elemFormatNumData, elemFormatNum)
  }

  @Benchmark
  def testExtractDate = {
    extractStringData(elemDateData, elemDate)
  }

  @Benchmark
  def testExtractDateTime = {
    extractStringData(elemDateTimeData, elemDateTime)
  }

  @Benchmark
  def testExtractTime = {
    extractStringData(elemTimeData, elemTime)
  }

  @Benchmark
  def testExtractFormattedTimeDateTime = {
    extractStringData(elemFormattedTimeDateTimeData, elemFormattedTimeDateTime)
  }

  @Benchmark
  def testExtractFormattedTimeDateTimeZoned = {
    extractStringData(elemFormattedTimeDateTimeDataZoned, elemFormattedTimeDateTimeZoned)
  }

  @Benchmark
  def testExtractFormattedTimeDate = {
    extractStringData(elemFormattedTimeDateData, elemFormattedTimeDate)
  }

  @Benchmark
  def testExtractFormattedTimeTime = {
    extractStringData(elemFormattedTimeTimeData, elemFormattedTimeTime)
  }

}
