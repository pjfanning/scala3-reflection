package com.github.pjfanning.scala3_reflection

import munit.*
import info.*
import impl.PrimitiveType.*

class Enums extends munit.FunSuite:

  test("Java Enums") {
    val result = RType.of[com.github.pjfanning.scala3_reflection.JavaEnum]
    assertEquals( result.show(), """JavaClassInfo(com.github.pjfanning.scala3_reflection.JavaEnum):
    |   fields:
    |      (0) color: JavaEnumInfo(com.github.pjfanning.scala3_reflection.Color)
    |""".stripMargin)
  }

  test("Scala Enums (old and new)") {
    val result = RType.of[Birthday]
    assertEquals( result.show(), """ScalaCaseClassInfo(com.github.pjfanning.scala3_reflection.Birthday):
    |   fields:
    |      (0) m: ScalaEnumInfo(com.github.pjfanning.scala3_reflection.Month) with values [Jan,Feb,Mar]
    |      (1) d: ScalaEnumerationInfo(com.github.pjfanning.scala3_reflection.WeekDay) with values [Monday,Tuesday,Wednesday,Thursday,Friday,Saturday,Sunday]
    |""".stripMargin)
  }

  test("Scala Enum methods") {
    val result = RType.of[Birthday]
    result match {
      case sc: ScalaCaseClassInfo => 
        val e = sc.fields(0).asInstanceOf[ScalaFieldInfo].fieldType.asInstanceOf[ScalaEnumInfo]
        assertEquals( e.valueOf("Jan"), Month.Jan )
        assertEquals( e.ordinal("Feb"), 1 )
        assertEquals( e.valueOf(2), Month.Mar )
      case _ => false
    }
  }

  test("Scala2 Enumeration methods") {
    val result = RType.of[Birthday]
    result match {
      case sc: ScalaCaseClassInfo => 
        val e = sc.fields(1).asInstanceOf[ScalaFieldInfo].fieldType.asInstanceOf[ScalaEnumerationInfo]
        assertEquals( e.valueOf("Monday"), WeekDay.Monday )
        assertEquals( e.ordinal("Wednesday"), 99 )
        assertEquals( e.valueOf(99), WeekDay.Wednesday )
      case _ => false
    }
  }