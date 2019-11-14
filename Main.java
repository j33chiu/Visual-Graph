package visual_2;

import java.awt.event.*;

public class Main {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		Window window = new Window(); //initialize the window which the user sees
		window.addWindowListener(new WindowAdapter(){ //allows the user to exit the game
			public void windowClosing(WindowEvent e) {
				window.save();
        		System.exit(0);
        	}
		});
	}
	
}
