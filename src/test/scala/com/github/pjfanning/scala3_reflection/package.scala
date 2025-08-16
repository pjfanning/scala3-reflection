package com.github.pjfanning

package object scala3_reflection {
  //it's usually recommended not to declare case classes in a package object - but worth testing them anyway
  case class PackageGenericTestClass[T](t: T)
}
