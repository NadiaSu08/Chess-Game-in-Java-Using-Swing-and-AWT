package main;

import java.awt.Color;
import java.awt.Graphics2D;

public class Board {
	
	final int MAX_COL = 8;
	final int MAX_ROW = 8;
	
	public static final int SQUARESIZE = 100;
	public static final int HALFSQUARESIZE = SQUARESIZE/2;
	
	public void draw(Graphics2D g2) {
		
		int c = 0;
		
		for(int row = 0; row < MAX_ROW; row++) {
			for(int col = 0; col < MAX_COL; col++) {
				
				if (c==0) {
					g2.setColor(new Color(210,165,125));
					c=1;
				} else {
					g2.setColor(new Color(160, 82, 45));
					c=0;
				}
				
				g2.fillRect(col*SQUARESIZE, row*SQUARESIZE, SQUARESIZE, SQUARESIZE);
			}
			
			if(c==0) {
				c=1;
			}else {
				c=0;
			}
		}
		
	}

}
