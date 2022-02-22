/*
 * @Author: 零泽
 *
 * @Date: 2022/02/08 20:36:23
 *
 * @LastEditors: 零泽
 *
 * @LastEditTime: 2022-02-22 22:25:05
 *
 * @FilePath: /Gobang/src/GUI/SinglePlay.java
 *
 * @Description:
 */
package GUI;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Optional;
import java.util.Stack;
import java.util.Timer;
import java.util.TimerTask;
import java.util.function.Predicate;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.input.MouseEvent;
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
public class SinglePlay extends Stage {
	private static int padding = 50;// 线与线之间距离
	private static int margin = 30;// 边线距离棋盘的距离
	private static int BottomMargin = 2 * margin;// 下边线距离棋盘的额外距离
	private static int starRadius = 5;// 星的半径
	private static int lineCount = 15;// 纵横线条数
	private int side = (lineCount - 1) * padding + 2 * margin;
	private Pane pane = null;// 画板
	private Chess[][] chesses = new Chess[lineCount][lineCount];
	private Stack<Pair<Integer, Integer>> record = new Stack<Pair<Integer, Integer>>();
	private boolean isWin = false;
	private Stage stage = null;
	private boolean isReplay = false;
	private int replayPos = 0;

	public SinglePlay() {
		this.stage = this;
		// 创建画板
		this.pane = getPane();
		// 落子给画板
		moveInChess();
		Scene scene = new Scene(pane, side, side + BottomMargin);// 创建场景对象，将画布导入场景
		stage.setScene(scene);// 窗口调用场景
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

	private boolean isHas(int x, int y) {
		return chesses[y][x] != null;
	}

	private boolean isWin() {
		if (record.empty() || isReplay)
			return false;
		int x = (record.lastElement().getKey() - margin) / padding;
		int y = (record.lastElement().getValue() - margin) / padding;
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

	private Button getReplayButton(Button next, Button last) {
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
	private Button getRetractButton() {
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

	// 创建重启按钮
	private Button getRestartButton() {
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

	// 落子功能
	private void moveInChess() {
		pane.setOnMouseClicked(new EventHandler<MouseEvent>() {
			// 鼠标点击画板会执行handle函数
			@Override

			public void handle(MouseEvent event) {
				if (isWin || isReplay)
					return;
				// 获取坐标
				double _x = event.getX();
				double _y = event.getY();
				// System.out.println("{ x='" + _x + "',\t y='" + _y + "'\t}");

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

				System.out.println("x='" + _x + "',\t y='" + _y + "'");
				place((int) _x, (int) _y);
				record.push(new Pair<Integer, Integer>(((int) _x) * padding + margin, ((int) _y) * padding + margin));
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

	// 落子到棋盘
	private void place(int _x, int _y) {
		Circle bChess = new Circle(_x * padding + margin, _y * padding + margin, padding / 2 - 1,
				Color.BLACK);
		pane.getChildren().add(bChess);
		Chess chess = null;
		if (record.size() % 2 == 1) {
			Circle wChess = new Circle(_x * padding + margin, _y * padding + margin, padding / 2 - 2,
					Color.WHITE);
			pane.getChildren().add(wChess);
			chess = new Chess((int) _x, (int) _y, Color.WHITE);
		} else {
			chess = new Chess((int) _x, (int) _y, Color.BLACK);
		}
		chesses[(int) _y][(int) _x] = chess;
	}

}