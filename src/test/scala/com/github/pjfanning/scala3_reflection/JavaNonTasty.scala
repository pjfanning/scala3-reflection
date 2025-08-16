package com.github.pjfanning.scala3_reflection

import munit.*
import com.github.pjfanning.scala3_reflection.{ClassAnno,FieldAnno}
import info.*
import impl.PrimitiveType.*


class JavaNonTasty extends munit.FunSuite:

  test("basic Java collections") {
    val result = RType.of[JColl]
    assertEquals( result.show(), """ScalaCaseClassInfo(com.github.pjfanning.scala3_reflection.JColl):
    |   fields:
    |      (0) a: JavaListInfo(java.util.List): scala.Int
    |      (1) b: Optional of JavaListInfo(java.util.ArrayList): scala.Int
    |      (2) c: JavaStackInfo(java.util.Stack): java.lang.String
    |      (3) d: JavaQueueInfo(java.util.Queue): MapLikeInfo(scala.collection.immutable.Map):
    |         scala.Int
    |         java.lang.String
    |      (4) e: JavaSetInfo(java.util.Set): scala.Boolean
    |      (5) f: JavaMapInfo(java.util.Map):
    |         scala.Int
    |         java.lang.String
    |""".stripMargin)
  }

  test("reflect basic with capture") {
    val result = RType.of[com.github.pjfanning.scala3_reflection.javabean.Person]
    assertEquals( result.show(), """JavaClassInfo(com.github.pjfanning.scala3_reflection.javabean.Person):
    |   fields:
    |      (0) age: scala.Int
    |      (1) name: java.lang.String
    |      (2) other: scala.Int
    |""".stripMargin)
    assert(result.asInstanceOf[JavaClassInfo].hasMixin("com.github.pjfanning.scala3_reflection.SJCaptureJava"))
  }

  test("create Java object") {
    val p = RType.of[com.github.pjfanning.scala3_reflection.javabean.Person].asInstanceOf[JavaClassInfo]
    val person = p.constructWith[com.github.pjfanning.scala3_reflection.javabean.Person](
      List(Integer.valueOf(35), "Frank", Integer.valueOf(5)))
    assertEquals(person.getName,"Frank")
    assertEquals(person.getAge,35)
    assertEquals(person.getOther,5)
    assertEquals(p.fields(1).valueOf(person).toString,"Frank")
    assertEquals(p.fields(0).valueOf(person).asInstanceOf[Int],35)
    assertEquals(p.fields(2).valueOf(person).asInstanceOf[Int],5)
  }

  test("Verify Java primitives") {
    val jx = RType.of[com.github.pjfanning.scala3_reflection.JavaTypes].asInstanceOf[JavaClassInfo]
    val number: java.lang.Number = java.lang.Integer.valueOf(123).asInstanceOf[java.lang.Number]
    val inst = jx.constructWith[com.github.pjfanning.scala3_reflection.JavaTypes](List(
      java.lang.Boolean.TRUE, java.lang.Boolean.FALSE, java.lang.Byte.valueOf(5.toByte),
      java.lang.Byte.valueOf(3.toByte), java.lang.Character.valueOf('x'), java.lang.Character.valueOf('y'),
      java.lang.Double.valueOf(1.2D), java.lang.Double.valueOf(2.3D), java.lang.Float.valueOf(4.5F),
      java.lang.Float.valueOf(5.6F), Integer.valueOf(1), Integer.valueOf(2), java.lang.Long.valueOf(3L),
      java.lang.Long.valueOf(4L), number, "something", java.lang.Short.valueOf(5.toShort),
      java.lang.Short.valueOf(6.toShort), "foom"
    ))

    val _a = jx.field("jBoolean").get
    _a.setValue(inst, java.lang.Boolean.valueOf(false))
    val a = _a.valueOf(inst)

    val _b = jx.field("jBoolean2").get 
    _b.setValue(inst, java.lang.Boolean.valueOf(true))
    val b = _b.valueOf(inst)

    val _c = jx.field("jByte").get 
    _c.setValue(inst, java.lang.Byte.valueOf(3.toByte))
    val c = _c.valueOf(inst)

    val _d = jx.field("jByte2").get 
    _d.setValue(inst, java.lang.Byte.valueOf(5.toByte))
    val d = _d.valueOf(inst)

    val _e = jx.field("jChar").get 
    _e.setValue(inst, java.lang.Character.valueOf('y'))
    val e = _e.valueOf(inst)

    val _f = jx.field("jCharacter").get 
    _f.setValue(inst, java.lang.Character.valueOf('z'))
    val f = _f.valueOf(inst)

    val _g = jx.field("jDouble").get 
    _g.setValue(inst, java.lang.Double.valueOf(2.3D))
    val g = _g.valueOf(inst)

    val _h = jx.field("jDouble2").get 
    _h.setValue(inst, java.lang.Double.valueOf(1.2D))
    val h = _h.valueOf(inst)

    val _i = jx.field("jFloat").get 
    _i.setValue(inst, java.lang.Float.valueOf(5.6F))
    val i = _i.valueOf(inst)

    val _j = jx.field("jFloat2").get 
    _j.setValue(inst, java.lang.Float.valueOf(4.5F))
    val j = _j.valueOf(inst)

    val _k = jx.field("jInt").get 
    _k.setValue(inst, java.lang.Integer.valueOf(2))
    val k = _k.valueOf(inst)

    val _l = jx.field("jInteger").get 
    _l.setValue(inst, java.lang.Integer.valueOf(1))
    val l = _l.valueOf(inst)

    val _m = jx.field("jLong").get 
    _m.setValue(inst, java.lang.Long.valueOf(4L))
    val m = _m.valueOf(inst)

    val _n = jx.field("jLong2").get 
    _n.setValue(inst, java.lang.Long.valueOf(3L))
    val n = _n.valueOf(inst)

    val _o = jx.field("jShort").get 
    _o.setValue(inst, java.lang.Short.valueOf(6.toShort))
    val o = _o.valueOf(inst)

    val _p = jx.field("jShort2").get 
    _p.setValue(inst, java.lang.Short.valueOf(5.toShort))
    val p = _p.valueOf(inst)

    val _q = jx.field("jString").get 
    _q.setValue(inst, "blather")
    val q = _q.valueOf(inst)

    val _r = jx.field("jObj").get 
    _r.setValue(inst, "empty")
    val r = _r.valueOf(inst)

    val _s = jx.field("jNumber").get 
    _s.setValue(inst, java.lang.Integer.valueOf(456).asInstanceOf[java.lang.Number])
    val s = _s.valueOf(inst)

    assert( a.asInstanceOf[java.lang.Boolean].booleanValue == false && a.getClass.getName == "java.lang.Boolean" )
    assert( b.asInstanceOf[java.lang.Boolean].booleanValue == true && b.getClass.getName == "java.lang.Boolean" )
    assert( c.asInstanceOf[java.lang.Byte].byteValue == 3.toByte && c.getClass.getName == "java.lang.Byte" )
    assert( d.asInstanceOf[java.lang.Byte].byteValue == 5.toByte && d.getClass.getName == "java.lang.Byte" )
    assert( e.asInstanceOf[java.lang.Character].charValue == 'y' && e.getClass.getName == "java.lang.Character" )
    assert( f.asInstanceOf[java.lang.Character].charValue == 'z' && f.getClass.getName == "java.lang.Character" )
    assert( g.asInstanceOf[java.lang.Double].doubleValue == 2.3D && g.getClass.getName == "java.lang.Double" )
    assert( h.asInstanceOf[java.lang.Double].doubleValue == 1.2D && h.getClass.getName == "java.lang.Double" )
    assert( i.asInstanceOf[java.lang.Float].floatValue == 5.6F && i.getClass.getName == "java.lang.Float" )
    assert( j.asInstanceOf[java.lang.Float].floatValue == 4.5F && j.getClass.getName == "java.lang.Float" )
    assert( k.asInstanceOf[java.lang.Integer].doubleValue == 2 && k.getClass.getName == "java.lang.Integer" )
    assert( l.asInstanceOf[java.lang.Integer].doubleValue == 1 && l.getClass.getName == "java.lang.Integer" )
    assert( m.asInstanceOf[java.lang.Long].floatValue == 4L && m.getClass.getName == "java.lang.Long" )
    assert( n.asInstanceOf[java.lang.Long].floatValue == 3L && n.getClass.getName == "java.lang.Long" )
    assert( o.asInstanceOf[java.lang.Short].doubleValue == 6.toShort && o.getClass.getName == "java.lang.Short" )
    assert( p.asInstanceOf[java.lang.Short].floatValue == 5.toShort && p.getClass.getName == "java.lang.Short" )
    assert( q.asInstanceOf[java.lang.String] == "blather" && q.getClass.getName == "java.lang.String" )
    assert( r.asInstanceOf[java.lang.Object] == "empty" && r.getClass.getName == "java.lang.String" )
    assert( s.asInstanceOf[java.lang.Number].intValue == 456 && s.getClass.getName == "java.lang.Integer" )
  }

  test("Detect parameterized Java class") {
    val wp = Class.forName("com.github.pjfanning.scala3_reflection.ParamAnno")
    val result = RType.of(wp) 
    assertEquals( result.show(), """JavaClassInfo(com.github.pjfanning.scala3_reflection.ParamAnno):
    |   fields:
    |      (0) age: T
    |         annotations: Map(com.github.pjfanning.scala3_reflection.FieldAnno -> Map(idx -> 2))
    |      (1) bogus: java.lang.Boolean
    |         annotations: Map(com.github.pjfanning.scala3_reflection.Ignore -> Map())
    |      (2) name: java.lang.String
    |         annotations: Map(com.github.pjfanning.scala3_reflection.FieldAnno -> Map(idx -> 1))
    |   annotations: Map(com.github.pjfanning.scala3_reflection.ClassAnno -> Map(name -> Foom))
    |""".stripMargin)
  }

  test("Java collection types") {
    val result = RType.of[com.github.pjfanning.scala3_reflection.JavaCollections]
    assertEquals( result.show(), """JavaClassInfo(com.github.pjfanning.scala3_reflection.JavaCollections):
    |   fields:
    |      (0) hMap: JavaMapInfo(java.util.HashMap):
    |         java.lang.String
    |         java.lang.Integer
    |      (1) myArr: array of java.lang.String
    |      (2) myList: JavaListInfo(java.util.ArrayList): java.lang.String
    |      (3) myQ: JavaQueueInfo(java.util.concurrent.BlockingQueue): java.lang.String
    |      (4) myTree: JavaSetInfo(java.util.TreeSet): java.lang.String
    |      (5) nested: array of JavaListInfo(java.util.List): java.lang.Integer
    |""".stripMargin)
  }

  test("Nested Java classes") {
    val result = RType.of[com.github.pjfanning.scala3_reflection.You]
    assertEquals( result.show(), """JavaClassInfo(com.github.pjfanning.scala3_reflection.You):
    |   fields:
    |      (0) sayHey: JavaClassInfo(com.github.pjfanning.scala3_reflection.Hey):
    |         fields:
    |            (0) jString: java.lang.String
    |""".stripMargin)
  }

  test("Java parameterized class top level") {
    val result = RType.of[com.github.pjfanning.scala3_reflection.JavaParam[Integer]]
    assertEquals( result.show(), """JavaClassInfo(com.github.pjfanning.scala3_reflection.JavaParam):
    |   fields:
    |      (0) jThing: java.lang.Integer
    |""".stripMargin)
  }

  test("Java parameterized class field member") {
    val result = RType.of[com.github.pjfanning.scala3_reflection.JavaParamHolder]
    assertEquals( result.show(), """JavaClassInfo(com.github.pjfanning.scala3_reflection.JavaParamHolder):
    |   fields:
    |      (0) jFoo: JavaClassInfo(com.github.pjfanning.scala3_reflection.JavaParam):
    |         fields:
    |            (0) jThing: java.lang.Integer
    |""".stripMargin)
  }