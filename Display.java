package visual_2;

import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.*;

public class Display extends Panel implements KeyListener, MouseMotionListener, MouseListener{

	private static final long serialVersionUID = 1L;
	private Dimension dim = new Dimension(698, 702);
	private Point mouseXY = new Point(0,0);
	
	//offscreen graphics
	private BufferedImage osm;
	private Graphics osg;
	
	//object lists
	private int nodesCounter = 0;
	private ArrayList<Button> buttonsList = new ArrayList<Button>();
	private ArrayList<Node> nodesList = new ArrayList<Node>();
	
	//function variables
	private Node source = null;
	private Node destination = null;
	private Graph graph;
	private boolean visualizePath = false;
	
	//functionality
	private int mouseButton = 0;
	private boolean isDrawingEdge = false;
	private Node drawingSrc = null;
	private boolean isMovingNode = false;
	private Node movingNode = null;
	private Dimension moveDisp = null;
	private Timer pauseTimer = new Timer();
	private Pause pauseTask;
	private ArrayList<Object[]> visualizeTimerTask;
	private int iter = 0;
	private boolean buttonsClickable = true;
	
	private ArrayList<Node[]> mst;
	private boolean visualizeMST = false;
	
	//files
	private String saveFile = "files/save.txt";
	
	private class Pause extends TimerTask{
		public void run() {
			Object[] o = visualizeTimerTask.get(iter);
			iter++;
			Node n = (Node)o[0];
			n.setColour((Color) o[1]);
			//System.out.println(n.getId());
			if(iter == visualizeTimerTask.size() - 1) {
				graph.aStar(source, destination, false);
				buttonsClickable = true;
				pauseTimer.cancel();
			}
		}
	}
	
	public Display() {
		addKeyListener(this);
		addMouseListener(this);
		addMouseMotionListener(this);
		setFocusable(true);
		graph = new Graph();
		initialize();
	}
	
	private void initialize() {
		osm = new BufferedImage(dim.width, dim.height, BufferedImage.TYPE_INT_RGB);
		osg = osm.getGraphics();
		buttonsList.add(new Button("Start", new Point(5, 670), 60, 30));
		buttonsList.add(new Button("End", new Point(70, 670), 60, 30));
		buttonsList.add(new Button("Save", new Point(135, 670), 60, 30));
		buttonsList.add(new Button("Load", new Point(200, 670), 60, 30));
		buttonsList.add(new Button("Reset", new Point(265, 670), 60, 30));
		buttonsList.add(new Button("Path: A*", new Point(330, 670), 60, 30));
		buttonsList.add(new Button("Path: Dijkstra", new Point(395, 670), 90, 30));
		buttonsList.add(new Button("Visualize Path", new Point(490, 670), 90, 30)); 
		buttonsList.add(new Button("MST", new Point(585, 670), 60, 30));
		try {
			load();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void save() throws Exception {
		PrintWriter out = new PrintWriter(saveFile);
		Node[] nodeArray = new Node[nodesList.size()];
		for(Node n: nodesList) {
			nodeArray[n.getId()] = n;
		}
		for(Node n: nodeArray) {
			out.println(n.getXY().x + "," + n.getXY().y);
		}
		out.println("###");
		for(Node n: nodeArray) {
			HashMap<Node, Integer> copy = n.getCon();
			for(Node connected: copy.keySet()) {
				out.println(n.getId() + "-" + connected.getId() + "-" + copy.get(connected));
			}
		}
		out.close();
	}
	
	public void load() throws Exception {
		Scanner in = new Scanner(new File(saveFile));
		nodesList = new ArrayList<Node>();
		nodesCounter = 0;
		graph = new Graph();
		String line = in.nextLine();
		while(!line.equals("###")) {
			String[] coords = line.split(",");
			Node n = new Node(nodesCounter, new Point(Integer.parseInt(coords[0]), Integer.parseInt(coords[1])));
			nodesList.add(n);
			graph.addNode(n, nodesCounter);
			nodesCounter++;
			line = in.nextLine();
		}
		while(in.hasNextLine()) {
			line = in.nextLine();
			String[] conn = line.split("-");
			Node src = nodesList.get(Integer.parseInt(conn[0]));
			Node dest = nodesList.get(Integer.parseInt(conn[1]));
			int cost = Integer.parseInt(conn[2]);//(int)Math.sqrt(Math.pow(src.getXY().x - dest.getXY().x, 2) + Math.pow(src.getXY().y - dest.getXY().y, 2));
			dest.addEdge(src, cost);
			src.addEdge(dest, cost);
			graph.addEdge(src, dest, cost);
		}
		in.close();
	}
	
	public void paint(Graphics g) {
		update(g);
	}
	
	public void update(Graphics g) {
		osg.setColor(Color.WHITE);
		osg.fillRect(0, 0, dim.width, dim.height);
		
		if(isDrawingEdge) {
			osg.setColor(Color.BLACK);
			osg.drawLine(drawingSrc.getXY().x, drawingSrc.getXY().y, mouseXY.x, mouseXY.y);
		}
		
		for(Button btn: buttonsList) btn.draw(osg);
		/*for(Node n: nodesList) {
			HashMap<Node, Integer> copy = n.getCon();
			for(Node dest: copy.keySet()) {
				osg.setColor(Color.BLACK);
				if(visualizeMST) {
					osg.setColor(Color.WHITE);
					for(Node[] pair: mst) {
						if(pair[0].equals(n) && pair[1].equals(dest)) osg.setColor(Color.RED);
						if(pair[0].equals(dest) && pair[1].equals(n)) osg.setColor(Color.RED);
					}
					osg.drawLine(n.getXY().x, n.getXY().y, dest.getXY().x, dest.getXY().y);
					osg.drawString(Integer.toString(copy.get(dest)), (n.getXY().x + dest.getXY().x)/2, (n.getXY().y + dest.getXY().y)/2);
				}
				else {
					osg.drawLine(n.getXY().x, n.getXY().y, dest.getXY().x, dest.getXY().y);
					osg.setColor(Color.RED);
					osg.drawString(Integer.toString(copy.get(dest)), (n.getXY().x + dest.getXY().x)/2, (n.getXY().y + dest.getXY().y)/2);
				}
			}
		}
		for(Node n: nodesList) {
			n.draw(osg);
		}
		*/
		graph.draw(osg, visualizeMST, mst);
		g.drawImage(osm, 0, 0, this);
		repaint();
	}

	@Override
	public void mouseClicked(MouseEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseEntered(MouseEvent arg0) {}
	@Override
	public void mouseExited(MouseEvent arg0) {}

	@Override
	public void mousePressed(MouseEvent me) {
		// TODO Auto-generated method stub
		mouseButton = me.getButton();
		mouseXY.x = me.getX();
		mouseXY.y = me.getY();
		boolean isHovering = false;
		//check if any buttons are hovered
		for(Button btn: buttonsList) {
			if(btn.getHovered()) {
				isHovering = true;
				break;
			}
		}
		for(Node n: nodesList) {
			if(n.getHovered()) {
				isHovering = true;
				break;
			}
		}
		
		if(!isHovering && mouseXY.y < 665) {
			//add new node at mouseXY
			Node n = new Node(nodesCounter, mouseXY);
			nodesList.add(n);
			n.isHovered(mouseXY);
			graph.addNode(n, nodesCounter);
			nodesCounter++;
		}
		else {
			//get hovered object, do action for click
			//buttons
			if(buttonsClickable) {
				for(Button btn: buttonsList) {
					if(btn.getHovered()) {
						if(btn.getName().equals("Start")) {
							for(Node n: nodesList) {
								n.setSrc(false);
								if(n.getSelected()) {
									source = n;
									n.setSrc(true);
								}
							}
						}
						else if(btn.getName().equals("End")) {
							for(Node n: nodesList) {
								n.setDest(false);
								if(n.getSelected()) {
									destination = n;
									n.setDest(true);
								}
							}
						}
						else if(btn.getName().equals("Save")) {
							try {
								save();
							} catch (Exception e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
						}
						else if(btn.getName().equals("Load")) {
							try {
								load();
							} catch (Exception e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
						}
						else if(btn.getName().equals("Reset")) {
							nodesList = new ArrayList<Node>();
							nodesCounter = 0;
							graph = new Graph();
						}
						else if(btn.getName().equals("Path: A*") && source != null && destination != null) {
							for(Node n: nodesList) n.setColour(Color.WHITE);
							ArrayList<Object[]> visualizeAStar = graph.aStar(source, destination, visualizePath);
							if(visualizeAStar != null) {
								buttonsClickable = false;
								visualizeTimerTask = visualizeAStar;
								iter = 0;
								pauseTimer = new Timer();
								pauseTask = new Pause();
								pauseTimer.scheduleAtFixedRate(pauseTask, 500, 200);
							}
							visualizeMST = false;
							//graph.dijkstra(source, destination);
						}
						else if(btn.getName().equals("Path: Dijkstra") && source != null && destination != null) {
							for(Node n: nodesList) n.setColour(Color.WHITE);
							//graph.aStar(source, destination);
							ArrayList<Object[]> visualizeDijkstra = graph.dijkstra(source, destination, visualizePath);
							if(visualizeDijkstra != null) {
								buttonsClickable = false;
								visualizeTimerTask = visualizeDijkstra;
								iter = 0;
								pauseTimer = new Timer();
								pauseTask = new Pause();
								pauseTimer.scheduleAtFixedRate(pauseTask, 500, 75);
							}
							visualizeMST = false;
						}
						else if(btn.getName().equals("Visualize Path")) {
							btn.toggleBtn();
							visualizePath = !visualizePath;
						}
						else if(btn.getName().equals("MST") && source != null) {
							btn.toggleBtn();
							visualizeMST = !visualizeMST;
							if(visualizeMST) mst = graph.MST(source);
						}
						break;
					}
				}
			}
			//nodes
			for(Node n: nodesList) {
				n.setSelected(false);
				if(n.getHovered()) {
					n.setSelected(true);
				}
			}
		}
	}

	@Override
	public void mouseReleased(MouseEvent arg0) {
		// TODO Auto-generated method stub
		if(isMovingNode) {
			isMovingNode = false;
			movingNode.updateCost();
		}
		else if(isDrawingEdge) {
			isDrawingEdge = false;
			for(Node n: nodesList) {
				if(n.getHovered() && !n.equals(drawingSrc)) {
					int cost = (int)Math.sqrt(Math.pow(n.getXY().x - drawingSrc.getXY().x, 2) + Math.pow(n.getXY().y - drawingSrc.getXY().y, 2));
					drawingSrc.addEdge(n, cost);
					n.addEdge(drawingSrc, cost);
					graph.addEdge(drawingSrc, n, cost);
					break;
				}
			}
		}
	}

	@Override
	public void mouseDragged(MouseEvent me) {//does not call mouseMoved
		// useful for moving nodes
		mouseXY.x = me.getX();
		mouseXY.y = me.getY();
		if(mouseButton == 1 && !isDrawingEdge) { //left button: draw edges
			drawingSrc = null;
			for(Node n: nodesList) {
				if(n.getHovered()) {
					drawingSrc = n;
					isDrawingEdge = true;
					break;
				}
			}
			
		}
		else if(mouseButton == 3 && !isMovingNode) { //right button: move nodes
			movingNode = null;
			moveDisp = null;
			for(Node n: nodesList) {
				if(n.getHovered()) {
					movingNode = n;
					isMovingNode = true;
					moveDisp = new Dimension(mouseXY.x - n.getXY().x, mouseXY.y - n.getXY().y);
					break;
				}
			}
		}
		else if(mouseButton == 3 && isMovingNode) { //right button: move nodes
			movingNode.setXY(new Point(mouseXY.x - moveDisp.width, mouseXY.y - moveDisp.height));
		}
		mouseMoved(me);
	}

	@Override
	public void mouseMoved(MouseEvent me) {
		mouseXY.x = me.getX();
		mouseXY.y = me.getY();
		boolean foundHovered = false;
		//check buttons for hover
		for(Button btn: buttonsList) {
			if(foundHovered) break;
			if(btn.isHovered(mouseXY)) {
				foundHovered = true;
				break;
			}
		}
		//check nodes for hover
		for(Node n: nodesList) {
			if(foundHovered) break;
			if(n.isHovered(mouseXY)) {
				foundHovered = true;
				break;
			}
		}
		//check edges for hover
		graph.checkEdgeHover(mouseXY);
	}

	@Override
	public void keyPressed(KeyEvent ke) {
		// TODO Auto-generated method stub
		//System.out.println(ke.getKeyCode());
		int code = ke.getKeyCode();
		if(code == 127) {//delete
			for(Node n: nodesList) {
				if(n.getSelected()) {
					if(n.equals(source)) source = null;
					else if(n.equals(destination)) destination = null;
					HashMap<Node, Integer> copy = n.getCon();
					for(Node conn: copy.keySet())
						conn.removeEdge(n);
					System.out.println(n.getId());
					nodesList.remove(n);
					graph.removeNode(n, n.getId());
					break;
				}
			}
		}
	}

	@Override
	public void keyReleased(KeyEvent arg0) {}
	@Override
	public void keyTyped(KeyEvent arg0) {}
	
}
