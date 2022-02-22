
/*
 * @FilePath: /Gobang/src/IPInput.java
 * @Author: 零泽
 * @Date: 2022-02-22 22:04:20
 * @LastEditTime: 2022-02-22 23:32:50
 * @LastEditors: 零泽
 * @Description: 
 */

import java.util.Optional;
import javafx.scene.control.ButtonType;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import javafx.stage.Window;
import javafx.stage.WindowEvent;

public class IPInput extends Stage {
  private int labelX = 100;
  // 横坐标与标签之间的间隔
  private int margin = 100;
  private int textFieldX = labelX + margin;
  private int textFieldLength = 2 * margin;

  private Label myIPLabel = null;
  private Label myPortLabel = null;
  private Label oppositeIPLabel = null;
  private Label oppositePortLabel = null;
  private TextField myIPInput = null;
  private TextField myPortInput = null;
  private TextField oppositeIPInput = null;
  private TextField oppositePortInput = null;
  private Button OK = null;
  private Button quit = null;

  public IPInput() {
    this.setOnCloseRequest(new EventHandler<WindowEvent>() {
      @Override
      public void handle(WindowEvent event) {
        Alert alert = new Alert(AlertType.CONFIRMATION);
        // 设置文字说明
        alert.setTitle("退出");
        alert.setHeaderText("请确认:");
        alert.setContentText("确定要退出吗?");
        // 展示弹框并获得结果
        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
          System.exit(0);
        } else {
          event.consume();
        }
      }
    });
    // 贴画布
    Pane pane = new Pane();
    // 设标签
    myIPLabel = new Label("我的IP:");
    myIPLabel.setLayoutX(labelX);
    myIPLabel.setLayoutY(margin / 2);
    myPortLabel = new Label("我的IP:");
    myPortLabel.setLayoutX(labelX);
    myPortLabel.setLayoutY(myIPLabel.getLayoutY() + margin);
    oppositeIPLabel = new Label("对方的IP:");
    oppositeIPLabel.setLayoutX(labelX);
    oppositeIPLabel.setLayoutY(myPortLabel.getLayoutY() + margin);
    oppositePortLabel = new Label("对方的IP:");
    oppositePortLabel.setLayoutX(labelX);
    oppositePortLabel.setLayoutY(oppositeIPLabel.getLayoutY() + margin);
    pane.getChildren().addAll(myIPLabel, myPortLabel, oppositeIPLabel, oppositePortLabel);
    // 设文本框
    myIPInput = new TextField();
    myIPInput.setLayoutX(textFieldX);
    myIPInput.setLayoutY(myIPLabel.getLayoutY());
    myIPInput.setPrefWidth(textFieldLength);
    myPortInput = new TextField();
    myPortInput.setLayoutX(textFieldX);
    myPortInput.setLayoutY(myPortLabel.getLayoutY());
    myPortInput.setPrefWidth(textFieldLength);
    oppositeIPInput = new TextField();
    oppositeIPInput.setLayoutX(textFieldX);
    oppositeIPInput.setLayoutY(oppositeIPLabel.getLayoutY());
    oppositeIPInput.setPrefWidth(textFieldLength);
    oppositePortInput = new TextField();
    oppositePortInput.setLayoutX(textFieldX);
    oppositePortInput.setLayoutY(oppositePortLabel.getLayoutY());
    oppositePortInput.setPrefWidth(textFieldLength);
    pane.getChildren().addAll(myIPInput, myPortInput, oppositeIPInput, oppositePortInput);
    OK = new Button("确认");
    OK.setPrefWidth(textFieldLength / 2);
    OK.setLayoutX(labelX);
    OK.setLayoutY(oppositePortLabel.getLayoutY() + margin);
    OK.setOnAction(new EventHandler<ActionEvent>() {
      @Override
      public void handle(ActionEvent event) {
        // TODO:写完网络对战，计划OnlinePlay的构造函数参数设置为文本框的四个
        IPInput.this.close();
      }
    });
    quit = new Button("退出");
    quit.setPrefWidth(textFieldLength / 2);
    quit.setLayoutX(labelX + OK.getPrefWidth() + margin);
    quit.setLayoutY(oppositePortLabel.getLayoutY() + margin);
    quit.setOnAction(new EventHandler<ActionEvent>() {
      @Override
      public void handle(ActionEvent event) {
        IPInput.this.getOnCloseRequest().handle(new WindowEvent((Window) IPInput.this, event.getEventType()));
      }
    });
    pane.getChildren().addAll(OK, quit);
    this.setScene(new Scene(pane, textFieldX + textFieldLength + margin, oppositePortLabel.getLayoutY() + 2 * margin));
  }

}
