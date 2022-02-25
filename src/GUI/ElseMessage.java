/*
 * @FilePath: /Gobang/src/GUI/ElseMessage.java
 * @Author: 零泽
 * @Date: 2022-02-25 10:27:27
 * @LastEditTime: 2022-02-25 13:02:11
 * @LastEditors: 零泽
 * @Description: 
 */
package GUI;

import java.io.ObjectOutputStream;
import java.net.Socket;

public enum ElseMessage {
  Exit, Retract, RetractAgree, RetractDisagree, Restart, RestartAgree, RestartDisagree;

  public static void sent(ElseMessage elseMessage) {
    Socket socket = null;
    try {
      socket = new Socket(Global.oppositeIP, Global.oppositePort);
      ObjectOutputStream outPut = new ObjectOutputStream(socket.getOutputStream());
      outPut.writeObject(elseMessage);
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
}
