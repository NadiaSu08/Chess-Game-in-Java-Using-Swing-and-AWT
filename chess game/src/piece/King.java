package piece;

import main.GamePanel;
import main.Type;

public class King extends piece {
	
	public King(int color, int col, int row) {
		super(color, col, row);
		
		type = Type.KING;
		
		if(color == GamePanel.WHITE) {
			image = getImage("/piece/King - brown");
		}
		else {
			image = getImage("/piece/King - black");
		}
	}

	public boolean canMove(int targetCol, int targetRow) {
		
		if(isWithinBoard(targetCol,targetRow)) {
			//movement
			if(Math.abs(targetCol- preCol) + Math.abs(targetRow- preRow) == 1 || 
					Math.abs(targetCol- preCol) * Math.abs(targetRow- preRow) == 1) {
				
				if(isValidSquare(targetCol,targetRow)) {
					return true;
				}
				
			}
			
			//castling
			
			if(moved == false) {
				
				//right 
				
				if(targetCol == preCol+2 && targetRow == preRow && pieceIsOnStraightLine(targetCol, targetRow) == false) {
					for(piece piece : GamePanel.simpieces) {
						if(piece.col == preCol+3 && piece.row == preRow && piece.moved == false) {
							GamePanel.castlingP = piece;
							return true;
						}
					}
				}
				
				//right
				
				if(targetCol == preCol-2 && targetRow == preRow && pieceIsOnStraightLine(targetCol, targetRow) == false) {
					piece p[] = new piece[2];
					
					for(piece piece : GamePanel.simpieces) {
						if(piece.col == preCol-3 && piece.row == targetRow) {
							p[0] = piece;
						}
						
						if(piece.col == preCol-4 && piece.row == targetRow) {
							p[1] = piece;
						}
						
						if(p[0] == null && p[1] != null && p[1].moved == false) {
							GamePanel.castlingP = p[1];
							return true;
						}
				}
			}
		}
		
	}
		return false;
	

}
}
