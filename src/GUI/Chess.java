package GUI;
/*
 * @Author: 零泽
 * @Date: 2022/02/10 21:10:22
 * @LastEditors: 零泽
 * @LastEditTime: 2022/02/16 19:56:22
 * @FilePath: \Gobang\src\GUI\Chess.java
 * @Description:
 */

import javafx.scene.paint.Color;

public class Chess {
	private int x;
	private int y;
	private Color color;

	public Chess(int x, int y, Color color) {
		this.x = x;
		this.y = y;
		this.color = color;
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

	public Color getColor() {
		return this.color;
	}

	public void setColor(Color color) {
		this.color = color;
	}

	@Override
	public String toString() {
		return "{" +
				" x='" + getX() + "'" +
				", y='" + getY() + "'" +
				", color='" + getColor() + "'" +
				"}";
	}
}