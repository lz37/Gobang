/*
 * @Author: 零泽
 *
 * @Date: 2022/02/08 20:36:23
 *
 * @LastEditors: 零泽
 *
 * @LastEditTime: 2022-02-25 13:13:31
 *
 * @FilePath: /Gobang/src/GUI/Board.java
 *
 * @Description:
 */
package GUI;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Stack;
import java.util.Timer;
import java.util.TimerTask;
import java.util.function.Predicate;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.stage.Window;
import javafx.stage.WindowEvent;
import javafx.util.Pair;
import javafx.scene.paint.Color;

// extends为继承
public abstract class Board extends Stage {
	static int padding = 50;// 线与线之间距离
	static int margin = 30;// 边线距离棋盘的距离
	protected static int BottomMargin = 2 * margin;// 下边线距离棋盘的额外距离
	static int starRadius = 5;// 星的半径
	static int lineCount = 15;// 纵横线条数
	int side = (lineCount - 1) * padding + 2 * margin;
	protected Pane pane = null;// 画板
	protected Chess[][] chesses = new Chess[lineCount][lineCount];
	Stack<Pair<Integer, Integer>> record = new Stack<Pair<Integer, Integer>>();
	boolean isWin = false;
	Stage stage = null;
	boolean isReplay = false;
	private int replayPos = 0;

	public Board() {
		this.stage = this;
		// 创建画板
		this.pane = getPane();
		// 对战功能
		fight();
		// 创建场景对象，将画布导入场景
		Scene scene = new Scene(pane, side, side + BottomMargin);
		// 窗口调用场景
		stage.setScene(scene);
		// 设置退出事件
		setExit();

	}

	protected abstract void setExit();

	protected abstract void fight();

	boolean isHas(int x, int y) {
		return chesses[y][x] != null;
	}

	public boolean getIsWin() {
		return this.isWin;
	}

	public boolean isWin() {
		if (record.empty() || isReplay)
			return false;
		int x = (record.lastElement().getKey() - margin) / padding;
		int y = (record.lastElement().getValue() - margin) / padding;
		// System.out.println(x + " " + y);
		// System.out.println((record.lastElement().getKey() - margin) + " " +
		// (record.lastElement().getValue() - margin));
		// 横向判断
		int left2Right = 1;
		for (int i = 1; i <= 4 && x - i >= 0; i++) {
			if (chesses[y][x - i] == null)
				break;
			else if (!chesses[y][x - i].getColor().equals(chesses[y][x].getColor()))
				break;
			else
				left2Right++;
		}
		for (int i = 1; i <= 4 && x + i < lineCount; i++) {
			if (chesses[y][x + i] == null)
				break;
			else if (!chesses[y][x + i].getColor().equals(chesses[y][x].getColor()))
				break;
			else
				left2Right++;
		}
		if (left2Right >= 5)
			return true;
		// 纵向判断
		int top2Bottom = 1;
		for (int i = 1; i <= 4 && y - i >= 0; i++) {
			if (chesses[y - i][x] == null)
				break;
			else if (!chesses[y - i][x].getColor().equals(chesses[y][x].getColor()))
				break;
			else
				top2Bottom++;
		}
		for (int i = 1; i <= 4 && y + i < lineCount; i++) {
			if (chesses[y + i][x] == null)
				break;
			else if (!chesses[y + i][x].getColor().equals(chesses[y][x].getColor()))
				break;
			else
				top2Bottom++;
		}
		if (top2Bottom >= 5)
			return true;
		// 斜向判断
		int leftTop2RightBottom = 1;
		for (int i = 1; i <= 4 && y - i >= 0 && x - i >= 0; i++) {
			if (chesses[y - i][x - i] == null)
				break;
			else if (!chesses[y - i][x - i].getColor().equals(chesses[y][x].getColor()))
				break;
			else
				leftTop2RightBottom++;
		}
		for (int i = 1; i <= 4 && y + i < lineCount && x + i < lineCount; i++) {
			if (chesses[y + i][x + i] == null)
				break;
			else if (!chesses[y + i][x + i].getColor().equals(chesses[y][x].getColor()))
				break;
			else
				leftTop2RightBottom++;
		}
		if (leftTop2RightBottom >= 5)
			return true;
		// 斜向判断
		int rightTop2LeftBottom = 0;
		for (int i = 1; i <= 4 && y - i >= 0 && x + i < lineCount; i++) {
			if (chesses[y - i][x + i] == null)
				break;
			else if (!chesses[y - i][x + i].getColor().equals(chesses[y][x].getColor()))
				break;
			else
				rightTop2LeftBottom++;
		}
		for (int i = 1; i <= 4 && y + i < lineCount && x - i >= 0; i++) {
			if (chesses[y + i][x - i] == null)
				break;
			else if (!chesses[y + i][x - i].getColor().equals(chesses[y][x].getColor()))
				break;
			else
				rightTop2LeftBottom++;
		}
		if (rightTop2LeftBottom >= 5)
			return true;
		return false;
	}

	// 创建棋盘
	private Pane getPane() {
		Pane pane = new Pane();// 创建画板

		// 获取再来一局按钮
		Button startButton = getRestartButton();
		// 获取悔棋按钮
		Button retractButton = getRetractButton();

		// 获取保存按钮
		Button saveButton = getSaveButton();

		// 获取打谱按钮
		// 打谱前进
		Button next = new Button(">");
		next.setPrefSize(padding, BottomMargin - margin);
		next.setLayoutX(margin + 10 * padding);
		next.setLayoutY(side - margin);
		next.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				if (replayPos > record.size() - 1) {
					System.out.println("没有更多的棋子咯");
					return;
				}
				replayPos = replayPos == -1 ? 0 : replayPos;
				Circle bChess = new Circle(record.get(replayPos).getKey(), record.get(replayPos).getValue(), padding / 2
						- 1, Color.BLACK);
				pane.getChildren().add(bChess);
				if (replayPos % 2 == 1) {
					Circle wChess = new Circle(record.get(replayPos).getKey(), record.get(replayPos).getValue(), padding / 2
							- 2, Color.WHITE);
					pane.getChildren().add(wChess);
				}
				replayPos++;
			}
		});
		// 打谱后退
		Button last = new Button("<");
		last.setPrefSize(padding, BottomMargin - margin);
		last.setLayoutX(margin + 9 * padding);
		last.setLayoutY(side - margin);
		last.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				if (replayPos < 0) {
					System.out.println("没有更少的棋子咯");
					return;
				}
				replayPos = replayPos == record.size() ? record.size() - 1 : replayPos;
				pane.getChildren().removeIf(new Predicate<Object>() {
					@Override
					public boolean test(Object t) {
						if (t instanceof Circle) {
							Circle tmp = (Circle) t;
							if (tmp.getRadius() > starRadius) {
								if (tmp.getCenterX() == record.get(replayPos).getKey()
										&& tmp.getCenterY() == record.get(replayPos).getValue())
									return true;
								else
									return false;
							} else
								return false;
						} else
							return false;
					}
				});
				replayPos--;
			}
		});
		// 打谱
		Button replayButton = getReplayButton(next, last);

		// 获取退出按钮
		Button exitButton = getExitButton();
		pane.getChildren().addAll(retractButton, startButton, saveButton, replayButton, exitButton);
		for (int i = 0; i < lineCount; i++) {
			// 画线条
			Line lineX = new Line(margin, margin + padding * i, side - margin, margin + padding * i);
			Line lineY = new Line(margin + padding * i, margin, margin + padding * i, side - margin);
			// 线条画到画板上
			pane.getChildren().add(lineY);
			pane.getChildren().add(lineX);
			// pane.getChildren()获取一个容器
			// .add(line)线条放入容器中
		}
		// 画星
		for (int i = 0; i < 3; i++) {
			for (int j = 0; j < 3; j++) {
				Circle star = new Circle(margin + padding * (4 * i + 3), margin + padding * (4 * j + 3), starRadius);
				pane.getChildren().add(star);
			}
		}
		// 创建标签文本对象
		Label label = new Label();
		// 创建计时器
		Timer timer = new Timer();
		timer.schedule(new TimerTask() {
			@Override
			public void run() {
				// 获取时间
				LocalDateTime localDateTime = LocalDateTime.now();
				DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
				String time = localDateTime.format(dateTimeFormatter);
				// 涉及到javafx的多线程代码要放到platform之中
				Platform.runLater(new Runnable() {
					@Override
					public void run() {
						// 时间设置到标签文本上
						label.setText(time);

					}
				});

			}
		}, 0, 1000);
		// 设置位置
		label.setLayoutX(side / 2 - "yyyy-MM-dd HH:mm:ss".length() * label.getFont().getSize() / 4);
		label.setLayoutY(0);
		pane.getChildren().add(label);
		return pane;
	}

	protected abstract Button getReplayButton(Button next, Button last);

	private Button getSaveButton() {
		// 添加按钮对象
		Button saveButton = new Button("保存");
		// 设置按钮大小
		saveButton.setPrefSize(2 * padding, BottomMargin - margin);
		// 设置位置
		saveButton.setLayoutX(margin + 6 * padding);
		saveButton.setLayoutY(side);
		saveButton.setOnAction(new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent event) {
				if (isReplay)
					return;
				// 创建保存框对象
				FileChooser fileChooser = new FileChooser();
				// 展示
				File file = fileChooser.showSaveDialog(stage);
				BufferedWriter bw = null;
				if (file != null) {
					try {
						bw = new BufferedWriter(new FileWriter(file));
						for (int i = 0; i < record.size(); i++) {
							bw.write(record.elementAt(i).toString());
							bw.newLine();
							bw.flush();
						}
					} catch (IOException e) {
						System.out.println("保存棋谱失败~");
					} finally {
						if (bw != null) {
							try {
								bw.close();
							} catch (IOException e1) {
								e1.printStackTrace();
							}
						}

					}
				}

			}
		});
		return saveButton;

	}

	// 创建退出按钮
	private Button getExitButton() {
		// 添加按钮对象
		Button exitButton = new Button("退出");
		// 设置按钮大小
		exitButton.setPrefSize(2 * padding, BottomMargin - margin);
		// 设置位置
		exitButton.setLayoutX(margin + 12 * padding);
		exitButton.setLayoutY(side);
		// 给按钮对象绑定click事件
		exitButton.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				// 直接调用window右上角的X
				stage.getOnCloseRequest().handle(new WindowEvent((Window) stage, event.getEventType()));
			}
		});
		return exitButton;
	}

	// 创建悔棋按钮
	protected abstract Button getRetractButton();

	// 创建重启按钮
	protected abstract Button getRestartButton();

	// 落子到棋盘
	public void place(int _x, int _y) {
		Circle bChess = new Circle(_x * padding + margin, _y * padding + margin, padding / 2 - 1,
				Color.BLACK);
		pane.getChildren().add(bChess);
		Chess chess = null;
		if (record.size() % 2 == 0) {
			Circle wChess = new Circle(_x * padding + margin, _y * padding + margin, padding / 2 - 2,
					Color.WHITE);
			pane.getChildren().add(wChess);
			chess = new Chess(_x, _y, Color.WHITE);
		} else {
			chess = new Chess(_x, _y, Color.BLACK);
		}
		chesses[_y][_x] = chess;
	}

	public void pushRecord(int x, int y) {
		record.push(new Pair<Integer, Integer>(x * padding + margin, y * padding + margin));
	}

}