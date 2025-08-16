package com.github.pjfanning.scala3_reflection

class PackageObject extends munit.FunSuite:

  test("reflect basic Generic class declared inside Package Object") {
    val result = RType.of[PackageGenericTestClass[Long]]
    assertEquals( result.show(), """ScalaCaseClassInfo(com.github.pjfanning.scala3_reflection.package$PackageGenericTestClass):
                                   |   fields:
                                   |      (0)[T] t: scala.Long
                                   |""".stripMargin)
    assertEquals(result.infoClass.getName, "com.github.pjfanning.scala3_reflection.package$PackageGenericTestClass")
  }
