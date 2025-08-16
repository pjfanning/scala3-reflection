package com.github.pjfanning.scala3_reflection
package info

import impl.*
import java.nio.ByteBuffer


/** Java Set dirivative */
object JavaSetInfo:
  def fromBytes( bbuf: ByteBuffer ): JavaSetInfo =
    JavaSetInfo(
      StringByteEngine.read(bbuf),
      RTypeByteEngine.read(bbuf)
      )

case class JavaSetInfo protected[scala3_reflection](
  name: String,
  _elementType: RType
) extends RType with CollectionRType:

  val fullName = name + "[" + _elementType.fullName + "]"
  lazy val infoClass: Class[_] = Class.forName(name)
      
  override def resolveTypeParams( paramMap: Map[TypeSymbol, RType] ): RType = 
    _elementType match {
      case ts: TypeSymbolInfo if paramMap.contains(ts.name.asInstanceOf[TypeSymbol]) => 
        JavaSetInfo(name, paramMap(ts.name.asInstanceOf[TypeSymbol]))
      case art: AppliedRType if art.isAppliedType => 
        JavaSetInfo(name, art.resolveTypeParams(paramMap))
      case _ => this
    }

  lazy val elementType: RType = _elementType match {
    case e: SelfRefRType => e.resolve
    case e => e
  }
    
  def toBytes( bbuf: ByteBuffer ): Unit = 
    bbuf.put( JAVA_SET_INFO )
    StringByteEngine.write(bbuf, name)
    RTypeByteEngine.write(bbuf, _elementType)
