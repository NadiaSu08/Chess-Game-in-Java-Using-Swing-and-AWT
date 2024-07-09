package piece;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.IOException;

import javax.imageio.ImageIO;

import main.Board;
import main.GamePanel;
import main.Type;

public class piece {

    public Type type;
	public BufferedImage image;
    public int x, y;
    public int col, row, preCol, preRow;
    public int color;
    public piece hittingP;
    public boolean moved, twoStepped;

    public piece(int color, int col, int row) {
        this.color = color;
        this.col = col;
        this.row = row;
        x = getX(col);
        y = getY(row);
        preCol = col;
        preRow = row;
    }

    public BufferedImage getImage(String imagePath) {
        BufferedImage image = null;
        try {
            image = ImageIO.read(getClass().getResourceAsStream(imagePath + ".png"));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return image;
    }

    public int getX(int col) {
        return col * Board.SQUARESIZE;
    }

    public int getY(int row) {
        return row * Board.SQUARESIZE;
    }

    public int getcol(int x) {
        return (x + Board.HALFSQUARESIZE) / Board.SQUARESIZE;
    }

    public int getrow(int y) {
        return (y + Board.HALFSQUARESIZE) / Board.SQUARESIZE;
    }
    
    public int getIndex() {
    	for(int index=0; index<GamePanel.simpieces.size(); index++) {
    		if(GamePanel.simpieces.get(index) == this) {
    			return index;
    		}
    	}
    	return 0;
    }

    public void updatePosition() {
    	
    	// CHECK EN PASSANT
    	
    	if(type == Type.PAWN) {
    		
    		if(Math.abs(row - preRow) == 2) {
    			twoStepped = true;
    		}
    	}
        x = getX(col);
        y = getY(row);
        preCol = col;
        preRow = row;
        moved = true;
    }

    public boolean canMove(int targetCol, int targetRow) {
        return false;  // To be overridden in subclasses
    }

    public boolean isWithinBoard(int targetCol, int targetRow) {
       if( targetCol >= 0 && targetCol <= 7 && targetRow >= 0 && targetRow <= 7) {
        	return true;
        }
        return false;
    }
    
    public boolean isSameSquare(int targetCol, int targetRow) {
    	if(targetCol == preCol && targetRow == preRow) {
    		return true;
    	}
    	return false;
    }
    
    public piece getHittingP(int targetCol, int targetRow) {
    	for(piece piece : GamePanel.simpieces){
    		if(piece.col == targetCol && piece.row == targetRow && piece!= this) {
    			return piece;
    		}
    	}
    	return null;
    }
    
    public boolean isValidSquare(int targetCol, int targetRow) {
    	hittingP= getHittingP(targetCol, targetRow);
    	if(hittingP == null) {
    		return true; 		
    	} else {
    		if(hittingP.color != this.color) {
    			return true;
    		}
    		else {
    			hittingP = null;
    		}
    	}
    	return false;
    }
    
    public boolean pieceIsOnStraightLine(int targetCol, int targetRow) {
       

        // Check horizontally to the left
        for (int c = preCol - 1; c > targetCol; c--) {
            for (piece piece : GamePanel.simpieces) {
                if (piece.col == c && piece.row == targetRow) {
                    hittingP = piece;
                    return true;
                }
            }
        }

        // Check horizontally to the right
        for (int c = preCol + 1; c < targetCol; c++) {
            for (piece piece : GamePanel.simpieces) {
                if (piece.col == c && piece.row == targetRow) {
                    hittingP = piece;
                    return true;
                }
            }
        }

        // Check vertically upwards
        for (int r = preRow - 1; r > targetRow; r--) {
            for (piece piece : GamePanel.simpieces) {
                if (piece.col == targetCol && piece.row == r) {
                    hittingP = piece;
                    return true;
                }
            }
        }

        // Check vertically downwards
        for (int r = preRow + 1; r < targetRow; r++) {
            for (piece piece : GamePanel.simpieces) {
                if (piece.col == targetCol && piece.row == r) {
                    hittingP = piece;
                    return true;
                }
            }
        }

        return false;
    }
    
    public boolean pieceIsOnDiagonalLine(int targetCol, int targetRow) {
        

        // up left
        if(targetRow < preRow) {
        	for (int c = preCol - 1; c > targetCol; c--) {
        		int diff = Math.abs(c-preCol);
        		for (piece piece : GamePanel.simpieces) {
                    if (piece.col == c && piece.row == preRow - diff) {
                        hittingP = piece;
                        return true;
                    }
                }
            }
        	}
        
        for (int c = preCol + 1; c < targetCol; c++) {
    		int diff = Math.abs(c-preCol);
    		for (piece piece : GamePanel.simpieces) {
                if (piece.col == c && piece.row == preRow - diff) {
                    hittingP = piece;
                    return true;
                }
            }
        }
        
        if(targetRow > preRow) {
        	for (int c = preCol + 1; c < targetCol; c++) {
        		int diff = Math.abs(c-preCol);
        		for (piece piece : GamePanel.simpieces) {
                    if (piece.col == c && piece.row == preRow - diff) {
                        hittingP = piece;
                        return true;
                    }
                }
            }
        	}
    	
        return false;
    }


    public void draw(Graphics2D g2) {
        g2.drawImage(image, x, y, Board.SQUARESIZE, Board.SQUARESIZE, null);
    }

    public void resetPosition() {
        col = preCol;
        row = preRow;
        x = getX(col);
        y = getY(row);
    }
}
