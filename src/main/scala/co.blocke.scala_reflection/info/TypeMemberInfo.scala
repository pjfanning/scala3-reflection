package co.blocke.scala_reflection
package info

import java.nio.ByteBuffer
import impl.*

object TypeMemberInfo:
  def fromBytes( bbuf: ByteBuffer ): TypeMemberInfo = 
    TypeMemberInfo(
      StringByteEngine.read(bbuf),
      StringByteEngine.read(bbuf).asInstanceOf[TypeSymbol],
      RTypeByteEngine.read(bbuf)
      )

case class TypeMemberInfo(
    name: String, 
    typeSymbol: TypeSymbol, 
    memberType: RType
  ) extends RType:

  val fullName: String = name

  lazy val infoClass: Class[_] = impl.Clazzes.ObjectClazz

  def show(tab: Int = 0, seenBefore: List[String] = Nil, suppressIndent: Boolean = false, modified: Boolean = false): String =
    {if(!suppressIndent) tabs(tab) else ""} + name + s"[$typeSymbol]: "+ memberType.show(tab+1,name :: seenBefore, true)
    
  def toBytes( bbuf: ByteBuffer ): Unit = 
    bbuf.put( TYPE_MEMBER_INFO )
    StringByteEngine.write(bbuf, name)
    StringByteEngine.write(bbuf, typeSymbol.toString)
    RTypeByteEngine.write(bbuf, memberType)
