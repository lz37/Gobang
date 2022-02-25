/*
 * @FilePath: /Gobang/src/GUI/SinglePlay.java
 * 
 * @Author: 零泽
 * 
 * @Date: 2022-02-23 20:43:24
 * 
 * @LastEditTime: 2022-02-25 13:11:17
 * 
 * @LastEditors: 零泽
 * 
 * @Description:
 */
package GUI;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Optional;
import java.util.function.Predicate;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.input.MouseEvent;
import javafx.scene.shape.Circle;
import javafx.stage.FileChooser;
import javafx.stage.WindowEvent;
import javafx.util.Pair;

public class SinglePlay extends Board {
  @Override
  protected Button getRestartButton() {
    // 添加按钮对象
    Button startButton = new Button("重来");
    // 设置按钮大小
    startButton.setPrefSize(2 * padding, BottomMargin - margin);
    // 设置位置
    startButton.setLayoutX(margin);
    startButton.setLayoutY(side);
    // 给按钮对象绑定click事件
    startButton.setOnAction(new EventHandler<ActionEvent>() {
      @Override
      public void handle(ActionEvent event) {
        if (isReplay)
          return;
        if (!isWin) {
          // 弹框
          Alert alert = new Alert(AlertType.CONFIRMATION);
          // 设置文字说明
          alert.setTitle("重来");
          alert.setHeaderText("警告：");
          alert.setContentText("当前对局尚未完成，确定要再来一局吗？");
          // 展示弹框并获得结果
          Optional<ButtonType> result = alert.showAndWait();
          if (result.isPresent() && result.get() == ButtonType.CANCEL) {
            return;
          }
        }
        // 删除所有Circle对象
        pane.getChildren().removeIf(new Predicate<Object>() {
          @Override
          public boolean test(Object t) {
            if (t instanceof Circle) {
              Circle star = (Circle) t;
              if (star.getRadius() == starRadius)
                return false;
              else
                return true;
            } else
              return false;
          }
        });
        record.clear();
        isWin = false;
        // 无需清空，只需要重新新建就好了
        chesses = new Chess[lineCount][lineCount];
        /*
         * for (int i = 0; i < lineCount; i++)
         * for (int j = 0; j < lineCount; j++)
         * chesses[i][j] = null;
         */
      }
    });
    return startButton;
  }

  @Override
  protected Button getRetractButton() {
    // 添加按钮对象
    Button retractButton = new Button("悔棋");
    // 设置按钮大小
    retractButton.setPrefSize(2 * padding, BottomMargin - margin);
    // 设置位置
    retractButton.setLayoutX(margin + 3 * padding);
    retractButton.setLayoutY(side);
    // 给按钮对象绑定click事件
    retractButton.setOnAction(new EventHandler<ActionEvent>() {
      @Override
      public void handle(ActionEvent event) {
        if (isWin || isReplay) {
          return;
        }
        if (record.empty()) {
          Alert alert = new Alert(AlertType.WARNING);
          alert.setTitle("警告！");
          alert.setHeaderText("错误！");
          alert.setContentText("无棋可悔！");
          alert.show();
          return;
        }
        Pair<Integer, Integer> nowChessXY = record.pop();
        // 删除所有当前位置的Circle对象
        pane.getChildren().removeIf(new Predicate<Object>() {
          @Override
          public boolean test(Object t) {
            if (t instanceof Circle) {
              Circle toDelChess = (Circle) t;
              if (nowChessXY.getKey().equals((int) toDelChess.getCenterX())
                  && nowChessXY.getValue().equals((int) toDelChess.getCenterY()))
                return true;
              else
                return false;
            } else
              return false;
          }
        });
        chesses[(nowChessXY.getValue() - margin) / padding][(nowChessXY.getKey() - margin) / padding] = null;
      }
    });
    return retractButton;
  }

  @Override
  protected Button getReplayButton(Button next, Button last) {
    // 添加按钮对象
    Button replayButton = new Button("打谱");
    // 设置按钮大小
    replayButton.setPrefSize(2 * padding, BottomMargin - margin);
    // 设置位置
    replayButton.setLayoutX(margin + 9 * padding);
    replayButton.setLayoutY(side);
    replayButton.setOnAction(new EventHandler<ActionEvent>() {
      @Override
      public void handle(ActionEvent event) {
        isReplay = !isReplay;
        if (isReplay) {
          record.clear();
          isWin = false;
          chesses = new Chess[lineCount][lineCount];
          pane.getChildren().removeIf(new Predicate<Object>() {
            @Override
            public boolean test(Object t) {
              if (t instanceof Circle) {
                Circle tmp = (Circle) t;
                if (tmp.getRadius() > starRadius)
                  return true;
                else
                  return false;
              } else
                return false;
            }
          });
          // 创建保存框对象
          FileChooser fileChooser = new FileChooser();
          // 展示
          File file = fileChooser.showOpenDialog(stage);
          BufferedReader br = null;
          try {
            br = new BufferedReader(new FileReader(file));
            String line = null;
            while ((line = br.readLine()) != null) {
              String[] XY = line.split("=");
              record.push(new Pair<Integer, Integer>(Integer.parseInt(XY[0]), Integer.parseInt(XY[1])));
            }
          } catch (Exception e) {
            e.printStackTrace();
          } finally {
            try {
              br.close();
            } catch (IOException e) {
              e.printStackTrace();
            }
          }
          pane.getChildren().addAll(next, last);
        } else {
          pane.getChildren().removeIf(new Predicate<Object>() {
            @Override
            public boolean test(Object t) {
              return t.equals(next) || t.equals(last);
            }
          });
        }

      }

    });
    return replayButton;
  }

  @Override
  protected void setExit() {
    stage.setOnCloseRequest(new EventHandler<WindowEvent>() {
      @Override
      public void handle(WindowEvent event) {
        Alert alert = new Alert(AlertType.CONFIRMATION);
        // 设置文字说明
        alert.setTitle("退出");
        alert.setHeaderText("确认：");
        alert.setContentText("确定要退出吗？");
        // 展示弹框并获得结果
        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
          stage.close();
          System.exit(0);
        } else {
          event.consume();
        }
      }
    });
  }

  @Override
  protected void fight() {
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