package visual_2;

import java.awt.Color;
import java.awt.Frame;

public class Window extends Frame{

	private static final long serialVersionUID = 1L;
	
	Display display;

	public Window() {
		display = new Display(); 
		setTitle("Visual Graphing"); 
		setSize(710, 737);
		setLocation(0,0);
		setVisible(true);
		setResizable(false);
		add(display);
		display.setBackground(Color.WHITE);
	}
	
	public void save() {
		try {
			display.save();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
