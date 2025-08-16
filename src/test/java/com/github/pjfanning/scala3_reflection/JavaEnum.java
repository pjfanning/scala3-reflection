package com.github.pjfanning.scala3_reflection;

enum Color 
{ 
    RED, GREEN, BLUE; 
}

public class JavaEnum {
  public JavaEnum() {}

  private Color color;
  public Color getColor() { return color; }
  public void setColor(Color n) { color = n; }
}