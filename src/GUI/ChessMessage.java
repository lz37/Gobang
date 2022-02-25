
/*
 * @FilePath: /Gobang/src/GUI/ChessMessage.java
 * 
 * @Author: 零泽
 * 
 * @Date: 2022-02-23 21:25:38
 * 
 * @LastEditTime: 2022-02-25 12:25:39
 * 
 * @LastEditors: 零泽
 * 
 * @Description:
 */
package GUI;

import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.Socket;

public class ChessMessage implements Serializable {
  private int x;
  private int y;

  public static void sent(int _x, int _y) {
    Socket socket = null;
    try {
      socket = new Socket(Global.oppositeIP, Global.oppositePort);
      ObjectOutputStream outPut = new ObjectOutputStream(socket.getOutputStream());
      outPut.writeObject(new ChessMessage(_x, _y));
    } catch (Exception e) {
      e.printStackTrace();
    } finally {
      if (socket != null) {
        try {
          socket.close();
        } catch (Exception e) {
          e.printStackTrace();
        }
      }
    }
  }

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