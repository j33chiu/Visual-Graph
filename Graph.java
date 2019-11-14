package visual_2;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Point;
import java.util.*;

public class Graph {
	
	HashMap<Integer, Node> idList = new HashMap<Integer, Node>(); //allow retrieval by id number (0 to n-1 nodes)
	HashMap<Node, Integer> nodeList = new HashMap<Node, Integer>(); //allow retrieval by node object
	ArrayList<Edge> edgeList = new ArrayList<Edge>();
	private int V;
	
	private class CostComparatorAStar implements Comparator<Node>{
        public int compare(Node a, Node b) {
            if(a.distance + a.HDist > b.distance + b.HDist) return 1;
            else if(a.distance + a.HDist < b.distance + b.HDist) return -1;
            else return 0;
        }
    } 
	private class CostComparatorDijkstra implements Comparator<Node>{
		public int compare(Node a, Node b) {
			if(a.distance > b.distance) return 1;
            else if(a.distance < b.distance) return -1;
            else return 0;
		}
	}
	private class CostComparatorMST implements Comparator<Edge>{
		public int compare(Edge a, Edge b) {
			if(a.weight > b.weight) return 1;
            else if(a.weight < b.weight) return -1;
            else return 0;
		}
	}
	
	private class Edge{
		int weight;
		Node a;
		Node b;
		boolean hovered = false;
		boolean samex = false;
		public Edge(Node a, Node b, int weight) {
			if(a.getXY().x < b.getXY().x) {
				this.a = a;
				this.b = b;
			}
			else if(a.getXY().x == b.getXY().x) {
				samex = true;
				this.a = a;
				this.b = b;
			}
			else {
				this.a = b;
				this.b = a;
			}
			this.weight = weight;
		}
	}
	
	
	public Graph() {
		V = 0;
	}
	
	public void addNode(Node n, int id) {
		idList.put(id, n);
		nodeList.put(n, id);
	}
	
	public void addEdge(Node src, Node dest, int cost) {
		edgeList.add(new Edge(src, dest, cost));
	}
	
	public void removeNode(Node n, int id) {
		removeEdges(n);
		idList.remove(id);
		nodeList.remove(n);
		
	}
	
	private void removeEdges(Node n) {
		for(int i = edgeList.size() - 1; i >= 0; i--) {
			Edge e = edgeList.get(i);
			if(e.a.equals(n) || e.b.equals(n)) edgeList.remove(e);
		}
	}
	
	public ArrayList<Object[]> dijkstra(Node src, Node dest, boolean visualize) {
		ArrayList<Object[]> visualizeOrder = new ArrayList<Object[]>();
		for(Node n: nodeList.keySet()) {
			n.resetALG();
		}
		src.distance = 0;
		PriorityQueue<Node> pq = new PriorityQueue<Node>(1, new CostComparatorDijkstra());
		pq.add(src);
		while(!pq.isEmpty()) {
			Node cur = pq.poll();
			visualizeOrder.add(new Object[] {cur, Color.ORANGE});
			HashMap<Node, Integer> copy = cur.getCon();
			for(Node n: copy.keySet()) {
				int cost = copy.get(n);
				int totalCost = cur.distance + cost;
				if(n.distance > totalCost) {
					n.distance = totalCost;
					pq.add(n);
					visualizeOrder.add(new Object[] {n, Color.YELLOW});
					n.previous = cur;
				}
			}
		}
		if(dest.distance < (int)(2*1e9)) {
			if(!visualize) {
				List<Node> path = new ArrayList<Node>();
	            Node n = dest;
	            while(!n.equals(src)) {
	                path.add(0, n);
	                n = n.previous;
	            }
	            path.add(0, n);
	            for(Node node: path) {
	            	node.setColour(Color.MAGENTA);
	            }
	            return null;
			}
			else {
				return visualizeOrder;
			}
   		}
		else {
			return null;
		}
		
	}
	
	public ArrayList<Object[]> aStar(Node src, Node dest, boolean visualize) {
		ArrayList<Object[]> visualizeOrder = new ArrayList<Object[]>();
		for(Node n: nodeList.keySet()) {
			n.resetALG();
			n.HDist = (int)Math.sqrt(Math.pow(n.getXY().x - dest.getXY().x, 2) + Math.pow(n.getXY().y - dest.getXY().y, 2));
		}
		src.distance = 0;
		PriorityQueue<Node> pq = new PriorityQueue<Node>(1, new CostComparatorAStar());
		pq.add(src);
		boolean reached = false;
		while(!pq.isEmpty()) {
			Node cur = pq.poll();
			visualizeOrder.add(new Object[] {cur, Color.ORANGE});
			//System.out.println("polled: " + cur.getId());
			HashMap<Node, Integer> copy = cur.getCon();
			for(Node n: copy.keySet()) {
				int cost = copy.get(n);
				int totalCost = cur.distance + cost;
				if(n.distance > totalCost) {
					n.distance = totalCost;
					n.previous = cur;
					pq.add(n);
					//System.out.println("queued: " + n.getId());
					visualizeOrder.add(new Object[] {n, Color.YELLOW});
					if(dest.equals(n)) {
						reached = true;
						break;
					}
				}
			}
			if(reached)break;
		}
		if(dest.distance < (int)(2*1e9)) {
			if(!visualize) {
				List<Node> path = new ArrayList<Node>();
	            Node n = dest;
	            while(!n.equals(src)) {
	                path.add(0, n);
	                n = n.previous;
	            }
	            path.add(0, n);
	            for(Node node: path) {
	            	node.setColour(Color.CYAN);
	            }
	            return null;
			}
			else {
				return visualizeOrder;
			}
   		}
		else {
			return null;
		}
	}
	
	public ArrayList<Node[]> MST(Node src) {
		ArrayList<Edge> edges = new ArrayList<Edge>();
		for(Node n: nodeList.keySet()) 
			n.resetALG();
		src.distance = 0;
		src.visited = true;
		PriorityQueue<Edge> pq = new PriorityQueue<Edge>(1, new CostComparatorMST());
		HashMap<Node, Integer> copySrc = src.getCon();
		for(Node n: copySrc.keySet()) {
			pq.add(new Edge(src, n, copySrc.get(n)));
		}
		while(!pq.isEmpty()) {
			Edge cur = pq.poll();
			if(!cur.a.visited || !cur.b.visited) {
				edges.add(cur);
				cur.a.visited = true;
				cur.b.visited = true;
				HashMap<Node, Integer> copyA = cur.a.getCon();
				for(Node n: copyA.keySet())
					if(!n.visited) pq.add(new Edge(cur.a, n, copyA.get(n)));

				HashMap<Node, Integer> copyB = cur.b.getCon();
				for(Node n: copyB.keySet()) 
					if(!n.visited) pq.add(new Edge(cur.b, n, copyB.get(n)));
			}
			if(edges.size() == nodeList.size() - 1) break;
		}
		for(Edge e: edges) {
			System.out.println(e.a.getId() + "-" + e.b.getId());
		}
		ArrayList<Node[]> out = new ArrayList<Node[]>();
		for(Edge e: edges) {
			out.add(new Node[] {e.a, e.b});
		}
		return out;
	}
	
	public void hasPath(Node src, Node dest) {
		
	}
	
	public void dijkstra(int src, int dest) {
		
	}
	
	public void aStar(int src, int dest) {
		
	}
	
	public void hasPath(int src, int dest) {
		
	}
	
	public int getNumberofNodes() {
		return V;
	}
	
	public void draw(Graphics osg, boolean visualizeMST, ArrayList<Node[]> mst) {
		for(Edge e: edgeList) {
			osg.setColor(Color.BLACK);
			if(visualizeMST) {
				osg.setColor(Color.WHITE);
				for(Node[] pair: mst) {
					if(pair[0].equals(e.a) && pair[1].equals(e.b)) osg.setColor(Color.RED);
					if(pair[0].equals(e.b) && pair[1].equals(e.a)) osg.setColor(Color.RED);
				}
				osg.drawLine(e.a.getXY().x, e.a.getXY().y, e.b.getXY().x, e.b.getXY().y);
				/*if(e.hovered) */osg.drawString(Integer.toString(e.weight), (e.a.getXY().x + e.b.getXY().x)/2, (e.a.getXY().y + e.b.getXY().y)/2);
			}
			else {
				osg.drawLine(e.a.getXY().x, e.a.getXY().y, e.b.getXY().x, e.b.getXY().y);
				osg.setColor(Color.RED);
				/*if(e.hovered) */ osg.drawString(Integer.toString(e.weight), (e.a.getXY().x + e.b.getXY().x)/2, (e.a.getXY().y + e.b.getXY().y)/2);
			}
		}
		for(Node n: nodeList.keySet()) {
			n.draw(osg);
		}
	}

	public void checkEdgeHover(Point mouseXY) {
		//System.out.println(mouseXY.toString());
		/*for(Edge e: edgeList) {
			//check if mouse x is between x values of points
			//check if mouse x and y is on line
			e.hovered = false;
			if(!e.samex) {
				double m = ((double)(e.b.getXY().y - e.a.getXY().y))/((double)(e.b.getXY().x - e.a.getXY().x));
				double b = (double)e.a.getXY().y - (m * (double)e.a.getXY().x);
				//if(e.a.getId() == 1) System.out.println(e.a.getId() + " " + e.b.getId() + ": " + m + " " + b);
				if(mouseXY.x >= e.a.getXY().x && mouseXY.x <= e.b.getXY().x && mouseXY.y > (m*mouseXY.x) + b - 20 && mouseXY.y < (m*mouseXY.x) + b + 20 && mouseXY.x > ((double)(mouseXY.y - b)/m) - 20 && mouseXY.x < ((double)(mouseXY.y - b)/m) + 20) {
					System.out.println(e.weight);
					e.hovered = true;
				}
			}
		}*/
	}
	
}
