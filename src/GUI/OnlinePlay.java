/*
 * @FilePath: /Gobang/src/GUI/OnlinePlay.java
 * @Author: 零泽
 * @Date: 2022-02-22 21:48:54
 * @LastEditTime: 2022-02-24 23:31:04
 * @LastEditors: 零泽
 * @Description: 
 */
package GUI;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.Socket;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;

public class OnlinePlay extends Board {
  private boolean canPlace = true;

  public void setCanPlace(boolean canPlace) {
    this.canPlace = canPlace;
  }

  public void setIsWin(boolean isWin) {
    this.isWin = isWin;
  }

  public int getRecordSize() {
    return record.size();
  }

  public void onlinePlace(int _x, int _y) {
    Circle bChess = new Circle(_x * padding + margin, _y * padding + margin, padding / 2 - 1,
        Color.BLACK);
    Platform.runLater(new Runnable() {
      @Override
      public void run() {
        pane.getChildren().add(bChess);
      }
    });
    Chess chess = null;
    if (record.size() % 2 == 0) {
      Circle wChess = new Circle(_x * padding + margin, _y * padding + margin, padding / 2 - 2,
          Color.WHITE);
      Platform.runLater(new Runnable() {
        @Override
        public void run() {
          pane.getChildren().add(wChess);
        }
      });
      chess = new Chess(_x, _y, Color.WHITE);
    } else {
      chess = new Chess(_x, _y, Color.BLACK);
    }
    chesses[_y][_x] = chess;
  }

  @Override
  void fight() {

    pane.setOnMouseClicked(new EventHandler<MouseEvent>() {
      // 鼠标点击画板会执行handle函数
      @Override

      public void handle(MouseEvent event) {

        if (isWin || isReplay || !canPlace)
          return;
        // 获取坐标
        double _x = event.getX();
        double _y = event.getY();
        if (!(_x >= margin && _x <= side - margin && _y >= margin && _y <= side - margin)) {
          System.out.println("Overflow");
          return;
        }
        _x = (int) ((_x - margin + padding / 2) / padding);
        _y = (int) ((_y - margin + padding / 2) / padding);
        if (isHas((int) _x, (int) _y)) {
          System.out.println("Occupied");
          return;
        }
        pushRecord((int) _x, (int) _y);
        place((record.lastElement().getKey() - margin) / padding,
            (record.lastElement().getValue() - margin) / padding);
        setCanPlace(false);
        // 发送端发送
        Socket socket = null;
        try {
          socket = new Socket(Global.oppositeIP, Global.oppositePort);
          ObjectOutputStream outPut = new ObjectOutputStream(socket.getOutputStream());
          outPut.writeObject(new ChessMessage((int) _x, (int) _y));
        } catch (Exception e) {
          e.printStackTrace();
        } finally {
          if (socket != null) {
            try {
              socket.close();
            } catch (IOException e) {
              e.printStackTrace();
            }
          }
        }
        if (isWin()) {
          // 弹框
          Alert alert = new Alert(AlertType.INFORMATION);
          // 设置文字说明
          alert.setTitle("胜利");
          alert.setHeaderText("恭喜！");
          alert.setContentText((record.size() % 2 == 1 ? "黑方" : "白方") + "胜利！");
          // 展示弹框
          alert.show();
          isWin = true;
          return;
        }

      }
    });
  }
}
