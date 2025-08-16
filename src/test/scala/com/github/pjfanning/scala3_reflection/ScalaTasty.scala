package com.github.pjfanning.scala3_reflection

import munit.*
import com.github.pjfanning.scala3_reflection.{ClassAnno,FieldAnno}
import info.*
import impl.PrimitiveType.*


inline def describe(message: String, color: String = Console.MAGENTA): Unit = println(s"$color$message${Console.RESET}")
inline def pending = describe("   << Test Pending (below) >>", Console.YELLOW)

class ScalaTasty extends munit.FunSuite:

  test("reflect basic Tasty class with union") {
    val result = RType.of[Person] 
    assertEquals( result.show(), """ScalaCaseClassInfo(com.github.pjfanning.scala3_reflection.Person):
    |   fields:
    |      (0) name: java.lang.String
    |      (1) age: scala.Int
    |      (2) other: Union:
    |         left--scala.Int
    |         right--scala.Boolean
    |""".stripMargin)
  }

  test("create basic Tasty class") {
    val p = RType.of[Person]
    val person = p.asInstanceOf[ScalaCaseClassInfo].constructWith[Person](List("Frank", Integer.valueOf(35), Integer.valueOf(5)))
    assertEquals(person, Person("Frank",35,5))
  }

  test("handle match types") {
    val result = RType.of[Definitely] 
    assertEquals( result.show(), """ScalaCaseClassInfo(com.github.pjfanning.scala3_reflection.Definitely):
    |   fields:
    |      (0) id: scala.Int
    |      (1) stuff: scala.Char
    |""".stripMargin)
  }

  test("Skip_Reflection annotation works") {
    val result = RType.of[SkipMe]
    assertEquals( result.show().stripLineEnd, """UnknownInfo(com.github.pjfanning.scala3_reflection.SkipMe)""")
  }
  
  test("process mixins") {
    val m = RType.of[WithMix]
    assertEquals(m.asInstanceOf[ScalaCaseClassInfo].hasMixin("com.github.pjfanning.scala3_reflection.SJCapture"),true)
  }

  test("capture field and class annotations") {
    val result = RType.of[WithAnnotation] 
    assertEquals( result.show(), """ScalaCaseClassInfo(com.github.pjfanning.scala3_reflection.WithAnnotation):
    |   fields:
    |      (0) id: java.lang.String
    |         annotations: Map(com.github.pjfanning.scala3_reflection.FieldAnno -> Map(idx -> 5))
    |   annotations: Map(com.github.pjfanning.scala3_reflection.ClassAnno -> Map(name -> Foom))
    |""".stripMargin)
  }

  // PROBLEM: Too Slow!! 2.x seconds, vs < 0.5 sec before.
  // The processing in Reflection is too slow... it's called @ runtime for inspection
  test("handle parameterized class - inspection") {
    val wp = WithParam(1,true)
    val result = RType.of(wp.getClass) 
    assertEquals( result.show(), """ScalaCaseClassInfo(com.github.pjfanning.scala3_reflection.WithParam):
    |   fields:
    |      (0)[T] one: T
    |      (1)[U] two: U
    |""".stripMargin)
  }

  test("handle parameterized class - reflection") {
    val result = RType.of[WithParam[Int,Boolean]] 
    assertEquals( result.show(), """ScalaCaseClassInfo(com.github.pjfanning.scala3_reflection.WithParam):
    |   fields:
    |      (0)[T] one: scala.Int
    |      (1)[U] two: scala.Boolean
    |""".stripMargin)
  }

  test("handle opaque type alias") {
    val result = RType.of[Employee]
    assertEquals( result.show(), """ScalaCaseClassInfo(com.github.pjfanning.scala3_reflection.Employee):
    |   fields:
    |      (0) eId: alias EMP_ID defined as scala.Int
    |      (1) age: scala.Int
    |""".stripMargin)
  }

  test("opaque type alias is a union type") {
    val result = RType.of[OpaqueUnion] 
    assertEquals( result.show(), """ScalaCaseClassInfo(com.github.pjfanning.scala3_reflection.OpaqueUnion):
    |   fields:
    |      (0) id: alias GEN_ID defined as Union:
    |         left--scala.Int
    |         right--java.lang.String
    |""".stripMargin)
  }

  test("support value classes") {
    val result = RType.of[Employee2]
    assertEquals( result.show(), """ScalaCaseClassInfo(com.github.pjfanning.scala3_reflection.Employee2):
    |   fields:
    |      (0) eId: ScalaCaseClassInfo--Value Class--(com.github.pjfanning.scala3_reflection.IdUser):
    |         fields:
    |            (0) id: scala.Int
    |      (1) age: scala.Int
    |""".stripMargin)
  }

  test("detect default values in case class constructor fields") {
    val result = RType.of[WithDefault]
    assertEquals( result.show(), """ScalaCaseClassInfo(com.github.pjfanning.scala3_reflection.WithDefault):
    |   fields:
    |      (0) a: scala.Int
    |      (1) b: java.lang.String
    |""".stripMargin)
    val wd = result.asInstanceOf[ScalaCaseClassInfo]
    val newWd = wd.constructWith[WithDefault](List(Integer.valueOf(5),wd.fields(1).defaultValue.get))
    assertEquals(newWd, WithDefault(5))
  }

  test("plain class support") {
    val result = RType.of[PlainGood]
    assertEquals( result.show(), """ScalaClassInfo(com.github.pjfanning.scala3_reflection.PlainGood):
    |   fields:
    |      (0) a: scala.Int
    |      (1) b: java.lang.String
    |   non-constructor fields:
    |""".stripMargin)

    val nonVal = RType.of[PlainNonVal]
    assertEquals(nonVal.asInstanceOf[ScalaClassInfo].fields.map(_.asInstanceOf[ScalaFieldInfo].isNonValConstructorField).toList, List(false,true))
  }

  test("all Scala primitive types") {
    val result = RType.of[ScalaPrimitives]
    assertEquals( result.show(), """ScalaCaseClassInfo(com.github.pjfanning.scala3_reflection.ScalaPrimitives):
    |   fields:
    |      (0) a: scala.Boolean
    |      (1) b: scala.Byte
    |      (2) c: scala.Char
    |      (3) d: scala.Double
    |      (4) e: scala.Float
    |      (5) f: scala.Int
    |      (6) g: scala.Long
    |      (7) h: scala.Short
    |      (8) i: java.lang.String
    |      (9) j: scala.Any
    |""".stripMargin)
  }

  test("Scala 2.x class") {
    val result = RType.of[scala.math.BigDecimal]
    assertEquals( result.show(), "Scala2Info(scala.math.BigDecimal)\n")
  }

  test("Try type") {
    val result = RType.of[TryMe]
    assertEquals( result.show(), """ScalaCaseClassInfo(com.github.pjfanning.scala3_reflection.TryMe):
    |   fields:
    |      (0) maybe: Try of scala.Boolean
    |""".stripMargin)
  }

  test("sealed trait with case classes") {
    val result = RType.of[VehicleHolder]
    assertEquals( result.show(), """ScalaCaseClassInfo(com.github.pjfanning.scala3_reflection.VehicleHolder):
    |   fields:
    |      (0) v: SealedTraitInfo(com.github.pjfanning.scala3_reflection.Vehicle):
    |         children:
    |            ScalaCaseClassInfo(com.github.pjfanning.scala3_reflection.Truck):
    |               fields:
    |                  (0) numberOfWheels: scala.Int
    |            ScalaCaseClassInfo(com.github.pjfanning.scala3_reflection.Car):
    |               fields:
    |                  (0) numberOfWheels: scala.Int
    |                  (1) color: java.lang.String
    |            ScalaCaseClassInfo(com.github.pjfanning.scala3_reflection.Plane):
    |               fields:
    |                  (0) numberOfEngines: scala.Int
    |""".stripMargin)
  }

  test("sealed trait with case objects") {
    val result = RType.of[FlavorHolder]
    assertEquals( result.show(), """ScalaCaseClassInfo(com.github.pjfanning.scala3_reflection.FlavorHolder):
    |   fields:
    |      (0) f: SealedTraitInfo(com.github.pjfanning.scala3_reflection.Flavor):
    |         children:
    |            ObjectInfo(com.github.pjfanning.scala3_reflection.Vanilla)
    |            ObjectInfo(com.github.pjfanning.scala3_reflection.Chocolate)
    |            ObjectInfo(com.github.pjfanning.scala3_reflection.Bourbon)
    |""".stripMargin)
  }

  test("sealed abstract class with case class") {
    val result = RType.of[PetOwner]
    assertEquals(result.show(),
      """ScalaCaseClassInfo(com.github.pjfanning.scala3_reflection.PetOwner):
        |   fields:
        |      (0) owner: java.lang.String
        |      (1) pet: ScalaClassInfo(com.github.pjfanning.scala3_reflection.Animal):
        |         fields:
        |            (0) animalType: java.lang.String
        |         non-constructor fields:
        |         children:
        |            ScalaClassInfo(com.github.pjfanning.scala3_reflection.Dog):
        |               fields:
        |                  (0) name: java.lang.String
        |               non-constructor fields:
        |            ScalaClassInfo(com.github.pjfanning.scala3_reflection.Cat):
        |               fields:
        |                  (0) name: java.lang.String
        |               non-constructor fields:
        |""".stripMargin)
  }

  test("handle intersection types") {
    val result = RType.of[IntersectionHolder]
    assertEquals( result.show(), """ScalaCaseClassInfo(com.github.pjfanning.scala3_reflection.IntersectionHolder):
    |   fields:
    |      (0) a: Intersection:
    |         left--Intersection:
    |            left--TraitInfo(com.github.pjfanning.scala3_reflection.InterA) with fields:
    |            right--TraitInfo(com.github.pjfanning.scala3_reflection.InterB) with fields:
    |         right--TraitInfo(com.github.pjfanning.scala3_reflection.InterC) with fields:
    |""".stripMargin)
  }

  test("handle Scala non-case classes") {
    val result = RType.of[FoomNC]
    val target = result.show()
    assertEquals( result.show(0,Nil,false,true), """ScalaClassInfo(com.github.pjfanning.scala3_reflection.FoomNC):
    |   fields:
    |      (0) a: scala.Int
    |      (1) b: java.lang.String
    |   non-constructor fields:
    |      (_) age: scala.Int
    |         annotations: Map(com.github.pjfanning.scala3_reflection.FieldAnno -> Map(idx -> 2))
    |      (_) blah: scala.Boolean
    |         annotations: Map(com.github.pjfanning.scala3_reflection.FieldAnno -> Map(idx -> 5))
    |      (_) hey: scala.Int
    |         annotations: Map(com.github.pjfanning.scala3_reflection.Ignore -> Map())
    |""".stripMargin)
  }

  test("Inheritance and Annotations") {
    val result = RType.of[InheritSimpleChild]
    assertEquals( result.show(0,Nil,false,true), """ScalaClassInfo(com.github.pjfanning.scala3_reflection.InheritSimpleChild):
    |   fields:
    |      (0) extra: java.lang.String
    |      (1) one: java.lang.String
    |         annotations: Map(com.github.pjfanning.scala3_reflection.Change -> Map(name -> uno), com.github.pjfanning.scala3_reflection.DBKey -> Map(index -> 50))
    |   non-constructor fields:
    |      (_) bogus: java.lang.String
    |         annotations: Map(com.github.pjfanning.scala3_reflection.Ignore -> Map())
    |      (_) dontForget: scala.Int
    |      (_) dontseeme: scala.Int
    |         annotations: Map(com.github.pjfanning.scala3_reflection.Ignore -> Map())
    |      (_) foo: scala.Int
    |         annotations: Map(com.github.pjfanning.scala3_reflection.DBKey -> Map(index -> 99))
    |      (_) four: scala.Double
    |         annotations: Map(com.github.pjfanning.scala3_reflection.DBKey -> Map(index -> 2), com.github.pjfanning.scala3_reflection.Change -> Map(name -> quatro))
    |      (_) nada: scala.Double
    |         annotations: Map(com.github.pjfanning.scala3_reflection.Ignore -> Map())
    |      (_) three: scala.Boolean
    |      (_) two: scala.Int
    |         annotations: Map(com.github.pjfanning.scala3_reflection.Change -> Map(name -> foobar), com.github.pjfanning.scala3_reflection.DBKey -> Map(index -> 1))
    |      (_) unused: scala.Double
    |         annotations: Map(com.github.pjfanning.scala3_reflection.Ignore -> Map())
    |""".stripMargin)
  }

  test("Inheritance and Parameterized Classes") {
    val result = RType.of[ParamChild[Boolean]]
    assertEquals( result.show(0,Nil,false,true), """ScalaClassInfo(com.github.pjfanning.scala3_reflection.ParamChild):
    |   fields:
    |      (0)[T] thing: scala.Boolean
    |   non-constructor fields:
    |      (_)[T] cosa: scala.Boolean
    |      (_)[T] item: scala.Boolean
    |""".stripMargin)
  }

  test("Self-referencing types (non-parameterized") {
    val result = RType.of[Shape]
    assertEquals( result.show(), """ScalaCaseClassInfo(com.github.pjfanning.scala3_reflection.Shape):
    |   fields:
    |      (0) id: scala.Int
    |      (1) parent: Option of ScalaCaseClassInfo(com.github.pjfanning.scala3_reflection.Shape) (self-ref recursion)
    |""".stripMargin)
    val result2 = RType.of[Person2]
    assertEquals( result2.show(), """ScalaCaseClassInfo(com.github.pjfanning.scala3_reflection.Person2):
    |   fields:
    |      (0) name: java.lang.String
    |      (1) age: scala.Int
    |      (2) boss: ScalaCaseClassInfo(com.github.pjfanning.scala3_reflection.Person2) (self-ref recursion)
    |""".stripMargin)
  }

  test("Self-referencing types (parameterized") {
    val result = RType.of[Drawer[Shape]]
    assertEquals( result.show(), """ScalaCaseClassInfo(com.github.pjfanning.scala3_reflection.Drawer):
    |   fields:
    |      (0) id: scala.Int
    |      (1) nextInChain: Option of ScalaCaseClassInfo(com.github.pjfanning.scala3_reflection.Drawer) (self-ref recursion)
    |      (2)[T] thing: ScalaCaseClassInfo(com.github.pjfanning.scala3_reflection.Shape):
    |         fields:
    |            (0) id: scala.Int
    |            (1) parent: Option of ScalaCaseClassInfo(com.github.pjfanning.scala3_reflection.Shape) (self-ref recursion)
    |""".stripMargin)
  }

  test("Ensure caching (equals) works") {
    val r0 = RType.of[Int]
    val r1 = RType.of[Int]
    assert(r0 == r1)
    assert(r0.equals(r1))
  }

  test("Classes defined inside objects should work") {
    val result = RType.of[BigObject.LittleThing]
    assertEquals(result.infoClass.getName(), "com.github.pjfanning.scala3_reflection.BigObject$LittleThing")
    assertEquals( result.show(), """ScalaCaseClassInfo(com.github.pjfanning.scala3_reflection.BigObject$LittleThing):
     |   fields:
     |      (0) a: scala.Int
     |""".stripMargin)
  }

  test("reflect enumeratum enum") {
    val result = RType.of[SizeEnum]
    assertEquals(result.show(),
      """ScalaClassInfo(com.github.pjfanning.scala3_reflection.SizeEnum):
        |   fields:
        |   non-constructor fields:
        |   children:
        |      ObjectInfo(com.github.pjfanning.scala3_reflection.SizeEnum$.TALL)
        |      ObjectInfo(com.github.pjfanning.scala3_reflection.SizeEnum$.GRANDE)
        |      ObjectInfo(com.github.pjfanning.scala3_reflection.SizeEnum$.VENTI)
        |""".stripMargin)
  }

  test("reflect case class in object (enumeratum field)") {
    val result = RType.of[EnumeratumWrapper.EchoEnumeratum]
    assertEquals(result.show(),
      """ScalaCaseClassInfo(com.github.pjfanning.scala3_reflection.EnumeratumWrapper$EchoEnumeratum):
        |   fields:
        |      (0) enumValue: ScalaClassInfo(com.github.pjfanning.scala3_reflection.SizeEnum):
        |         fields:
        |         non-constructor fields:
        |         children:
        |            ObjectInfo(com.github.pjfanning.scala3_reflection.SizeEnum$.TALL)
        |            ObjectInfo(com.github.pjfanning.scala3_reflection.SizeEnum$.GRANDE)
        |            ObjectInfo(com.github.pjfanning.scala3_reflection.SizeEnum$.VENTI)
        |""".stripMargin)
}  