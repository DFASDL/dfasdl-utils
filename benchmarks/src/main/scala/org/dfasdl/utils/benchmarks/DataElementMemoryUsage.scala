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

import java.time._

import org.github.jamm.MemoryMeter

object DataElementMemoryUsage {
  final val SampleSize = 1000000

  sealed trait WrapperStuff

  final case class ByteWrapper(a: Array[Byte]) extends WrapperStuff

  final case class LocalDateWrapper(d: LocalDate) extends WrapperStuff

  final case class OffsetDateTimeWrapper(t: OffsetDateTime) extends WrapperStuff

  def main(args: Array[String]): Unit = {
    val meter = new MemoryMeter()
    val ts    = OffsetDateTime.now()
    println(s"A: Generating $SampleSize direct entries.")
    val va = Vector.fill(SampleSize)(ts.plusSeconds(scala.util.Random.nextInt().toLong))
    println("Measuring...")
    val ba = meter.measureDeep(va)
    val ma = "%.2f".format(ba.toDouble / 1024 / 1024)

    println(s"B: Generating $SampleSize Either entries.")
    val vb =
      Vector.fill(SampleSize)(
        Left(Left(Left(Right(ts.plusSeconds(scala.util.Random.nextInt().toLong)))))
      )
    println("Measuring...")
    val bb = meter.measureDeep(vb)
    val mb = "%.2f".format(bb.toDouble / 1024 / 1024)

    println(s"C: Generating $SampleSize wrapper class entries.")
    val vc = Vector.fill(SampleSize)(
      OffsetDateTimeWrapper(ts.plusSeconds(scala.util.Random.nextInt().toLong))
    )
    println("Measuring...")
    val bc = meter.measureDeep(vc)
    val mc = "%.2f".format(bc.toDouble / 1024 / 1024)

    println(s"D: Generating $SampleSize direct entries.")
    val vd = Vector.fill(SampleSize)(
      ts.plusSeconds(scala.util.Random.nextInt().toLong).toLocalDate
    )
    println("Measuring...")
    val bd = meter.measureDeep(vd)
    val md = "%.2f".format(bd.toDouble / 1024 / 1024)

    println(s"E: Generating $SampleSize Either entries.")
    val ve = Vector.fill(SampleSize)(
      Left(Left(Left(Left(Right(ts.plusSeconds(scala.util.Random.nextInt().toLong).toLocalDate)))))
    )
    println("Measuring...")
    val be = meter.measureDeep(ve)
    val me = "%.2f".format(be.toDouble / 1024 / 1024)

    println(s"F: Generating $SampleSize wrapper class entries.")
    val vf = Vector.fill(SampleSize)(
      LocalDateWrapper(ts.plusSeconds(scala.util.Random.nextInt().toLong).toLocalDate)
    )
    println("Measuring...")
    val bf = meter.measureDeep(vf)
    val mf = "%.2f".format(bf.toDouble / 1024 / 1024)

    println(s"G: Generating $SampleSize direct entries.")
    val vg = Vector.fill(SampleSize)(
      scala.util.Random.alphanumeric.take(40).mkString.getBytes("UTF-8")
    )
    println("Measuring...")
    val bg = meter.measureDeep(vg)
    val mg = "%.2f".format(bg.toDouble / 1024 / 1024)

    println(s"H: Generating $SampleSize Either entries.")
    val vh = Vector.fill(SampleSize)(
      Left(scala.util.Random.alphanumeric.take(40).mkString.getBytes("UTF-8"))
    )
    println("Measuring...")
    val bh = meter.measureDeep(vh)
    val mh = "%.2f".format(bh.toDouble / 1024 / 1024)

    println(s"I: Generating $SampleSize wrapper class entries.")
    val vi = Vector.fill(SampleSize)(
      ByteWrapper(scala.util.Random.alphanumeric.take(40).mkString.getBytes("UTF-8"))
    )
    println("Measuring...")
    val bi = meter.measureDeep(vi)
    val mi = "%.2f".format(bi.toDouble / 1024 / 1024)

    println(s"Object size A:\t$ba bytes ($ma MB)")
    println(s"Object size B:\t$bb bytes ($mb MB)")
    println(s"Object size C:\t$bc bytes ($mc MB)")
    println(s"Object size D:\t$bd bytes ($md MB)")
    println(s"Object size E:\t$be bytes ($me MB)")
    println(s"Object size F:\t$bf bytes ($mf MB)")
    println(s"Object size G:\t$bg bytes ($mg MB)")
    println(s"Object size H:\t$bh bytes ($mh MB)")
    println(s"Object size I:\t$bi bytes ($mi MB)")
  }

}
