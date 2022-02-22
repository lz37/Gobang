/*
 * @FilePath: /Gobang/src/Select.java
 * @Author: 零泽
 * @Date: 2022-02-22 21:32:14
 * @LastEditTime: 2022-02-22 22:11:07
 * @LastEditors: 零泽
 * @Description: 
 */

import GUI.SinglePlay;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

public class Select extends Application {
  // 按钮长宽与各条边到边界以及彼此之间的距离
  private int width = 100;
  private int length = 100;
  private int margin = 100;

  Button singlePlayButton = null;
  Button onlinePlayButton = null;

  @Override
  public void start(Stage stage) throws Exception {
    Pane pane = new Pane();
    // 单机
    singlePlayButton = new Button("单机版");
    singlePlayButton.setPrefSize(width, length);
    singlePlayButton.setLayoutX(margin);
    singlePlayButton.setLayoutY(margin);
    singlePlayButton.setOnAction(new EventHandler<ActionEvent>() {
      @Override
      public void handle(ActionEvent event) {
        SinglePlay singlePlay = new SinglePlay();
        // 打开单人模式
        singlePlay.show();
        // 选择框关闭
        stage.close();
      }

    });
    onlinePlayButton = new Button("联机版");
    onlinePlayButton.setPrefSize(width, length);
    onlinePlayButton.setLayoutX(2 * margin + width);
    onlinePlayButton.setLayoutY(margin);
    onlinePlayButton.setOnAction(new EventHandler<ActionEvent>() {
      @Override
      public void handle(ActionEvent arg0) {
        IPInput ipInput = new IPInput();
        // 打开IP与端口输入窗口
        ipInput.show();
        // 选择框关闭
        stage.close();
      }

    });
    pane.getChildren().addAll(singlePlayButton, onlinePlayButton);
    stage.setScene(new Scene(pane, 3 * margin + 2 * width, 2 * margin + length));
    stage.show();
  }

  public static void main(String[] args) {
    launch(args);
  }

}
