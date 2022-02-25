/*
 * @FilePath: /Gobang/src/GUI/OnlinePlay.java
 * @Author: 零泽
 * @Date: 2022-02-22 21:48:54
 * @LastEditTime: 2022-02-25 13:16:19
 * @LastEditors: 零泽
 * @Description: 
 */
package GUI;

import java.util.Optional;
import java.util.function.Predicate;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.stage.WindowEvent;
import javafx.util.Pair;

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

  @Override
  protected Button getRestartButton() {
    Button startButton = new Button("重来");
    startButton.setPrefSize(2 * padding, BottomMargin - margin);
    startButton.setLayoutX(margin);
    startButton.setLayoutY(side);
    startButton.setOnAction(new EventHandler<ActionEvent>() {
      @Override
      public void handle(ActionEvent event) {
        if (isReplay)
          return;
        if (!isWin) {
          // 弹框
          Alert alert = new Alert(AlertType.CONFIRMATION);
          // 设置文字说明
          alert.setTitle(Global.myIP + ":" + Global.myPort);
          alert.setHeaderText("警告：");
          alert.setContentText("当前对局尚未完成，确定要再来一局吗？");
          // 展示弹框并获得结果
          Optional<ButtonType> result = alert.showAndWait();
          if (result.isPresent() && result.get() == ButtonType.CANCEL) {
            return;
          }
        }
        ElseMessage.sent(ElseMessage.Restart);
      }
    });
    return startButton;
  }

  public void clearBoard() {
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
    chesses = new Chess[lineCount][lineCount];
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
          alert.setTitle(Global.myIP + ":" + Global.myPort);
          alert.setHeaderText("警告！");
          alert.setContentText("无棋可悔！");
          alert.show();
          return;
        }
        ElseMessage.sent(ElseMessage.Retract);
      }
    });
    return retractButton;
  }

  public void deleteNowChess() {
    Pair<Integer, Integer> nowChessXY = record.pop();
    // 删除所有当前位置的Circle对象
    pane.getChildren().removeIf(new Predicate<Object>() {
      @Override
      public boolean test(Object t) {
        if (t instanceof Circle) {
          Circle toDelChess = (Circle) t;
          if (nowChessXY.getKey().equals((int) toDelChess.getCenterX())
              && nowChessXY.getValue().equals((int) toDelChess.getCenterY()) && toDelChess.getRadius() > starRadius)
            return true;
          else
            return false;
        } else
          return false;
      }
    });
    chesses[(nowChessXY.getValue() - margin) / padding][(nowChessXY.getKey() - margin) / padding] = null;
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
        Alert alert = new Alert(AlertType.WARNING);
        alert.setTitle(Global.myIP + ":" + Global.myPort);
        alert.setHeaderText("不可执行的操作");
        alert.setContentText("联网模式下无法打谱");
        alert.show();
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
        alert.setTitle(Global.myIP + ":" + Global.myPort);
        alert.setHeaderText("退出：");
        alert.setContentText("确定要退出吗？");
        // 展示弹框并获得结果
        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
          ElseMessage.sent(ElseMessage.Exit);
          stage.close();
          System.exit(0);
        } else {
          event.consume();
        }
      }
    });
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
  protected void fight() {
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
        // 发送端发送棋子信息
        ChessMessage.sent((int) _x, (int) _y);
        if (isWin()) {
          // 弹框
          Alert alert = new Alert(AlertType.INFORMATION);
          // 设置文字说明
          alert.setTitle(Global.myIP + ":" + Global.myPort);
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
