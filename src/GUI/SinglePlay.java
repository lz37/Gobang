/*
 * @FilePath: /Gobang/src/GUI/SinglePlay.java
 * 
 * @Author: 零泽
 * 
 * @Date: 2022-02-23 20:43:24
 * 
 * @LastEditTime: 2022-02-23 21:42:01
 * 
 * @LastEditors: 零泽
 * 
 * @Description:
 */
package GUI;

import javafx.event.EventHandler;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.input.MouseEvent;

public class SinglePlay extends Board {

  @Override
  void fight() {
    pane.setOnMouseClicked(new EventHandler<MouseEvent>() {
      // 鼠标点击画板会执行handle函数
      @Override

      public void handle(MouseEvent event) {
        if (isWin || isReplay)
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
        place((int) _x, (int) _y);
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