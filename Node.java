package visual_2;

import java.awt.*;
import java.util.*;

public class Node {

	//background variables
	private int id = 0;
	private HashMap<Node, Integer> connections = new HashMap<Node, Integer>();
	
	//display variables
	private Point xy = new Point(0,0); //center of the circle
	private Color bgColour = Color.WHITE;
	public final int radius = 12;
	
	private boolean hovered = false;
	private boolean selected = false;
	private boolean isSrc = false;
	private boolean isDest = false;
	
	//algorithm variables
	public int distance = (int)(2*1e9);
	public int HDist = 0;
	public Node previous = null;
	public boolean visited = false;
	
	public Node(int id, Point xy) {
		this.id = id;
		this.xy.x = xy.x;
		this.xy.y = xy.y;
	}
	
	public boolean isHovered(Point xy) {
		int i = (int)Math.pow((xy.x - this.xy.x), 2.0) + (int)Math.pow((xy.y - this.xy.y), 2.0);
		if(i > radius * radius) {
			hovered = false;
			return hovered;
		}
		hovered = true;
		return hovered;
	}
	
	public boolean getHovered() {
		return hovered;
	}
	
	public int getId() {
		return id;
	}
	
	public int getCost(Node n) {
		return connections.get(n);
	}
	
	public void setColour(Color c) {
		bgColour = c;
	}
	
	public void draw(Graphics g) {
		g.setColor(bgColour);
		if(hovered) g.setColor(Color.LIGHT_GRAY);
		if(selected) g.setColor(Color.GRAY);
		if(isSrc) g.setColor(Color.GREEN);
		if(isDest) g.setColor(Color.RED);
		g.fillOval(xy.x - radius, xy.y - radius, radius*2, radius*2);
		g.setColor(Color.black);
		g.drawOval(xy.x - radius, xy.y - radius, radius*2, radius*2);
		
		String str = Integer.toString(id);
		FontMetrics metrics = g.getFontMetrics(g.getFont());
		int x = xy.x - radius + ((radius*2 - metrics.stringWidth(str)) /2);
		int y = xy.y + (radius*2 - metrics.getHeight()) /2;
		g.drawString(str, x, y);
	}
	
	public boolean getSelected() {
		return selected;
	}
	
	public void setSelected(boolean s) {
		selected = s;
	}
	
	public void setSrc(boolean b) {
		isSrc = b;
	}
	
	public void setDest(boolean b) {
		isDest = b;
	}
	
	public Point getXY() {
		return xy;
	}
	
	public void setXY(Point xy) {
		this.xy.x = xy.x;
		this.xy.y = xy.y;
	}
	
	public void addEdge(Node n, int distance) {
		connections.put(n, distance);
	}
	
	public void removeEdge(Node n) {
		connections.remove(n);
	}
	
	public HashMap<Node, Integer> getCon(){
		@SuppressWarnings("unchecked")
		HashMap<Node, Integer> copy = (HashMap<Node, Integer>) connections.clone();
		return copy;
	}
	
	public void updateCost() {
		for(Node dest: connections.keySet()) {
			int cost = (int)Math.sqrt(Math.pow(xy.x - dest.getXY().x, 2) + Math.pow(xy.y - dest.getXY().y, 2));
			connections.put(dest, cost);
			dest.updateValue(this, cost);
		}
	}
	
	public void updateValue(Node source, int cost) {
		connections.put(source, cost);
	}
	
	//for graph algorithms
	public void resetALG() {
		distance = (int)(2*1e9);
		HDist = 0;
		previous = null;
		visited = false;
	}
	
}
