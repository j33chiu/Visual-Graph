package visual_2;

import java.awt.*;

public class Button {

	private Point xy = new Point(0,0);
	private int width = 0;
	private int height = 0;
	
	private Color bgCol = Color.WHITE;
	private int toggle = 0;
	
	private boolean hovered = false;
	
	private String text = "";
	public Button(String text, Point xy, int w, int h) {
		this.text = text;
		this.xy.x = xy.x;
		this.xy.y = xy.y;
		width = w;
		height = h;
	}
	
	public boolean isHovered(Point xy) {
		if(xy.x > this.xy.x && xy.x < this.xy.x + width && xy.y > this.xy.y && xy.y < this.xy.y + height) hovered = true;
		else hovered = false;
		return hovered;
	}
	
	public boolean getHovered() {
		return hovered;
	}
	
	public void draw(Graphics g) {
		g.setColor(bgCol);
		if(hovered)g.setColor(Color.LIGHT_GRAY);
		g.fillRoundRect(xy.x, xy.y, width, height, 20, 20);
		g.setColor(Color.BLACK);
		g.drawRoundRect(xy.x, xy.y, width, height, 20, 20);

		FontMetrics metrics = g.getFontMetrics(g.getFont());
		int x = (xy.x + width/2) - (width/2) + ((width - metrics.stringWidth(text)) /2);
		int y = (xy.y + height/2) + (height - metrics.getHeight()) /2;
		g.drawString(text, x, y);
	}
	
	public String getName() {
		return text;
	}
	
	public void toggleBtn() {
		toggle = (toggle + 1)%2;
		if(toggle == 0) bgCol = Color.WHITE;
		else bgCol = Color.DARK_GRAY;
	}
}
