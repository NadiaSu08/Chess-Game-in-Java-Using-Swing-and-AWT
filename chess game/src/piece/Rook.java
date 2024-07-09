package piece;

import main.GamePanel;
import main.Type;

public class Rook extends piece {
	
	public Rook(int color, int col, int row) {
		super(color, col, row);
		
		type = Type.ROOK;
		
		if(color == GamePanel.WHITE) {
			image = getImage("/piece/Rook- brown");
		}
		else {
			image = getImage("/piece/Rook- black");
		}
	}
	
	public boolean canMove(int targetCol, int targetRow) {
	    if (isWithinBoard(targetCol, targetRow) && isSameSquare(targetCol, targetRow) == false) {
	        if (targetCol == preCol || targetRow == preRow) {
	            if (isValidSquare(targetCol, targetRow) && pieceIsOnStraightLine(targetCol, targetRow) == false) {
	                return true;
	            }
	        }
	    }
	    return false;
	}
	}
	
	