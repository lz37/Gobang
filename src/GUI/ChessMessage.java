
/*
 * @FilePath: /Gobang/src/GUI/ChessMessage.java
 * 
 * @Author: 零泽
 * 
 * @Date: 2022-02-23 21:25:38
 * 
 * @LastEditTime: 2022-02-23 22:24:26
 * 
 * @LastEditors: 零泽
 * 
 * @Description:
 */
package GUI;

import java.io.Serializable;

public class ChessMessage implements Serializable {
  private int x;
  private int y;

  public ChessMessage(Chess chess) {
    this.x = chess.getX();
    this.y = chess.getY();
  }

  public ChessMessage(int x, int y) {
    this.x = x;
    this.y = y;
  }

  public int getX() {
    return this.x;
  }

  public void setX(int x) {
    this.x = x;
  }

  public int getY() {
    return this.y;
  }

  public void setY(int y) {
    this.y = y;
  }

  @Override
  public String toString() {
    return "{" +
        " x='" + getX() + "'" +
        ", y='" + getY() + "'" +
        "}";
  }

}