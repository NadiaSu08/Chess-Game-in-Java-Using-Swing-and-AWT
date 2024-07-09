package main;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.util.ArrayList;

import javax.swing.JPanel;

import piece.Bishop;
import piece.King;
import piece.Knight;
import piece.Pawn;
import piece.Queen;
import piece.Rook;
import piece.piece;

public class GamePanel extends JPanel implements Runnable {
    
    public static final int WIDTH = 1100;
    public static final int HEIGHT = 800;
    final int FPS = 60;
    Thread gameThread;
    
    Board board = new Board();
    Mouse mouse = new Mouse();
    
    // pieces
    
    public static ArrayList<piece> pieces = new ArrayList<>();
    public static ArrayList<piece> simpieces = new ArrayList<>();
    ArrayList<piece> PromoPieces = new ArrayList<>();
    piece activeP, checkingP;
    public static piece castlingP; 
    
    // COLOR
    
    public static final int WHITE = 0;
    public static final int BLACK = 1;
    int currentColor = WHITE;
    
    // booleans
    
    boolean canMove;
    boolean validSquare;
    boolean promotion;
    boolean gameover;
    boolean stalemate;
    
    public GamePanel() {
        setPreferredSize(new Dimension(WIDTH, HEIGHT));
        setBackground(Color.black);
        addMouseMotionListener(mouse);
        addMouseListener(mouse);
        
        setPieces();
        // testPromotion();
        //testIllegal();
        copyPieces(pieces, simpieces);
    }
    
    public void launchGame() {
        gameThread = new Thread(this);
        gameThread.start();
    }
    
    public void setPieces() {
        // white
        pieces.add(new Pawn(WHITE, 0, 6));
        pieces.add(new Pawn(WHITE, 1, 6));
        pieces.add(new Pawn(WHITE, 2, 6));
        pieces.add(new Pawn(WHITE, 3, 6));
        pieces.add(new Pawn(WHITE, 4, 6));
        pieces.add(new Pawn(WHITE, 5, 6));
        pieces.add(new Pawn(WHITE, 6, 6));
        pieces.add(new Pawn(WHITE, 7, 6));
        pieces.add(new Rook(WHITE, 0, 7));
        pieces.add(new Rook(WHITE, 7, 7));
        pieces.add(new Knight(WHITE, 1, 7));
        pieces.add(new Knight(WHITE, 6, 7));
        pieces.add(new Bishop(WHITE, 2, 7));
        pieces.add(new Bishop(WHITE, 5, 7));
        pieces.add(new Queen(WHITE, 3, 7));
        pieces.add(new King(WHITE, 4, 7));
        // pieces.add(new Queen(WHITE, 4, 4));
        
        // black
        pieces.add(new Pawn(BLACK, 0, 1));
        pieces.add(new Pawn(BLACK, 1, 1));
        pieces.add(new Pawn(BLACK, 2, 1));
        pieces.add(new Pawn(BLACK, 3, 1));
        pieces.add(new Pawn(BLACK, 4, 1));
        pieces.add(new Pawn(BLACK, 5, 1));
        pieces.add(new Pawn(BLACK, 6, 1));
        pieces.add(new Pawn(BLACK, 7, 1));
        pieces.add(new Rook(BLACK, 0, 0));
        pieces.add(new Rook(BLACK, 7, 0));
        pieces.add(new Knight(BLACK, 1, 0));
        pieces.add(new Knight(BLACK, 6, 0));
        pieces.add(new Bishop(BLACK, 2, 0));
        pieces.add(new Bishop(BLACK, 5, 0));
        pieces.add(new Queen(BLACK, 3, 0));
        pieces.add(new King(BLACK, 4, 0));
    }
    
    public void testPromotion() {
        pieces.add(new Pawn(WHITE, 0, 3));
        pieces.add(new Pawn(BLACK, 5, 4));
    }
    
    public void testIllegal() {
        pieces.add(new Pawn(WHITE, 7, 6));
        pieces.add(new King(WHITE, 3, 7));
        pieces.add(new King(BLACK, 0, 3));
        pieces.add(new Bishop(BLACK, 1, 4));
        pieces.add(new Queen(BLACK, 4, 5));
    }
    
    private void copyPieces(ArrayList<piece> source, ArrayList<piece> target) {
        target.clear();
        for (int i = 0; i < source.size(); i++) {
            target.add(source.get(i));
        }
    }
    
    @Override
    public void run() {
        // Game loop
        double drawInterval = 1000000000 / FPS;
        double delta = 0;
        long lastTime = System.nanoTime();
        long currentTime;
        
        while (gameThread != null) {
            currentTime = System.nanoTime();
            delta += (currentTime - lastTime) / drawInterval;
            lastTime = currentTime;
            
            if (delta >= 1) {
                update();
                repaint();
                delta--;
            }
        }
    }
    
    private void update() {
        if (promotion) {
            promoting();
        } else if (gameover == false && stalemate == false) {
            if (mouse.pressed) {
                if (activeP == null) {
                    for (piece piece : simpieces) {
                        if (piece.color == currentColor &&
                            piece.col == mouse.x / Board.SQUARESIZE &&
                            piece.row == mouse.y / Board.SQUARESIZE) {
                            activeP = piece;
                            break;
                        }
                    }
                } else {
                    simulate();
                }
            }
            
            if (mouse.pressed == false) {
                if (activeP != null) {
                    if (validSquare) {
                        copyPieces(simpieces, pieces);
                        activeP.updatePosition();
                        
                        if (castlingP != null) {
                            castlingP.updatePosition();
                        }
                        
                        if(isKingInCheck() && isCheckmate()) {
                        	
                        	gameover = true;
                        	
                        }
                        
                        else if (isStalemate() && isKingInCheck()) {
                        	stalemate = true;
                        }
                        else {
                        	if (canPromote()) {
                                promotion = true;
                            } else {
                                changePlayer();
                            }
                        	
                        }
                        
                    } else {
                    	copyPieces(pieces, simpieces);
                        activeP.resetPosition();
                        activeP = null;
                    }
                }
            }
        }
    }
    
    private void simulate() {
        canMove = false;
        validSquare = false;
        
        copyPieces(pieces, simpieces);
        
        // reset castling
        if (castlingP != null) {
            castlingP.col = castlingP.preCol;
            castlingP.x = castlingP.getX(castlingP.col);
            castlingP = null;
        }
        
        activeP.x = mouse.x - Board.HALFSQUARESIZE;
        activeP.y = mouse.y - Board.HALFSQUARESIZE;
        activeP.col = activeP.getcol(activeP.x);
        activeP.row = activeP.getrow(activeP.y);
        
        if (activeP.canMove(activeP.col, activeP.row)) {
            canMove = true;
            
            if (activeP.hittingP != null) {
                simpieces.remove(activeP.hittingP.getIndex());
            }
            
            checkCastling();
            
            if (!isIllegal(activeP) && !opponentCanCaptureKing()) {
                validSquare = true;
            }
        }
    }
    
    private boolean isIllegal(piece king) {
        if (king.type == Type.KING) {
            for (piece piece : simpieces) {
                if (piece != king && piece.color != king.color && piece.canMove(king.col, king.row)) {
                    return true;
                }
            }
        }
        return false;
    }
    
    private boolean opponentCanCaptureKing() {
    	piece king = getKing(false);
    	for(piece piece : simpieces) {
    		if (piece.color != king.color && piece.canMove(king.col, king.row)) {
                return true;
            }
    		
    	}
    	return false;
    }
    private boolean isKingInCheck() {
    	
    	piece king = getKing(true);
    	
    	if(activeP.canMove(king.col, king.row)) {
    		checkingP = activeP;
    		return true;
    	}else {
    		checkingP = null;
    	}
    	return false;
    }
    
    private piece getKing(boolean opponent) {
    	piece king = null;
    	for(piece piece : simpieces) {
    		if(opponent) {
    			if(piece.type == Type.KING && piece.color != currentColor) {
    				king = piece;
    			}
    		} else {
    			if(piece.type == Type.KING && piece.color == currentColor) {
    				king = piece;
    			}
    		}
    	}
    	
    	return king;
    }
    
    private boolean isCheckmate() {
    	piece king = getKing(true);
    	
    	if(kingCanMove(king)) {
    		return false;
    	}
    	
    	else {
    		int colDiff = Math.abs(checkingP.col - king.col);
    		int rowDiff = Math.abs(checkingP.row - king.row);
    		
    		if(colDiff == 0) {
    			
    			if(checkingP.row < king.row) {
    				for(int row = checkingP.row; row < king.row; row++) {
    					for (piece piece : simpieces) {
    						if(piece != king && piece.color != currentColor && piece.canMove(checkingP.col, row)) {
    							return false;
    						}
    					}
    				}
    			}
    			
    			if(checkingP.row > king.row) {
    				for(int row = checkingP.row; row > king.row; row--) {
    					for (piece piece : simpieces) {
    						if(piece != king && piece.color != currentColor && piece.canMove(checkingP.col, row)) {
    							return false;
    						}
    					}
    				}
    			}
    		} 
    		else if ( rowDiff == 0) {
    			
    			if(checkingP.col < king.col) {
    				for(int col = checkingP.col; col < king.col; col++) {
    					for (piece piece : simpieces) {
    						if(piece != king && piece.color != currentColor && piece.canMove(col ,checkingP.col)) {
    							return false;
    						}
    					}
    				}
    			}
    			
    			if(checkingP.col > king.col) {
    				for(int col = checkingP.col; col > king.col; col--) {
    					for (piece piece : simpieces) {
    						if(piece != king && piece.color != currentColor && piece.canMove(col ,checkingP.col)) {
    							return false;
    						}
    					}
    				}
    			}
    			
    		}
    		
    		else if (colDiff == rowDiff) {
    			
    			if(checkingP.row < king.row) {
    				
    				if(checkingP.col < king.col) {
    					
    					for(int col = checkingP.col, row = checkingP.row; col < king.col; col++, row++) {
    						
    						for (piece piece : simpieces) {
        						if(piece != king && piece.color != currentColor && piece.canMove(col ,row)) {
        							return false;
        						}
        					}
    						
    					}
    				}
    				
    				if(checkingP.col > king.col) {
    					
    					for(int col = checkingP.col, row = checkingP.row; col > king.col; col--, row++) {
    						
    						for (piece piece : simpieces) {
        						if(piece != king && piece.color != currentColor && piece.canMove(col ,row)) {
        							return false;
        						}
        					}
    						
    					}
    					
    				}
    				
    			}
    			
        if(checkingP.row > king.row) {
    				
    				if(checkingP.col < king.col) {
    					
    					for(int col = checkingP.col, row = checkingP.row; col < king.col; col++, row--) {
    						
        						for (piece piece : simpieces) {
            						if(piece != king && piece.color != currentColor && piece.canMove(col ,row)) {
            							return false;
            						}
            					}
        						
        					}
    					}
    				
    				
    				if(checkingP.col > king.col) {
    					
for(int col = checkingP.col, row = checkingP.row; col > king.col; col--, row--) {
    						
    						for (piece piece : simpieces) {
        						if(piece != king && piece.color != currentColor && piece.canMove(col ,row)) {
        							return false;
        						}
        					}
    						
    					}
    					
    				}
    				
    			}
    			
    			
    		}
    		else {
    			
    		}
    		
    	}
    	return true;
    }
    
    private boolean kingCanMove(piece king) {
    	
    	if(isValidMove(king, -1, -1)) {return true;}
    	if(isValidMove(king, 0, -1)) {return true;}
    	if(isValidMove(king, 1, -1)) {return true;}
    	if(isValidMove(king, -1, 0)) {return true;}
    	if(isValidMove(king, 0, 0)) {return true;}
    	if(isValidMove(king, -1, 1)) {return true;}
    	if(isValidMove(king, 0, 1)) {return true;}
    	if(isValidMove(king, 1, 1)) {return true;}
    	
    	return false;
    	
    	
    }
    
    private boolean isValidMove(piece king, int colPlus, int rowPlus) {
    	
    	boolean isValidMove = false;
    	king.col+= colPlus;
    	king.row += rowPlus;
    	
    	if(king.canMove(king.col, king.row)) {
    		if(king.hittingP != null) {
    			simpieces.remove(king.hittingP.getIndex());
    		}
    		if(isIllegal(king) == false) {
    			isValidMove = true;
    		}
    	}
    	
    	king.resetPosition();
    	copyPieces(pieces, simpieces);
    	
    	return isValidMove;
    }
    
    private boolean isStalemate() {
    	
    	int count = 0;
    	
    	for( piece piece : simpieces) {
    		if(piece.color != currentColor) {
    			count++;
    		}
    		
    	}
    	
    	if(count == 1) {
    		if(kingCanMove(getKing(true)) == false) {
    			return true;
    		}
    	}
    	return false;
    }
    private void checkCastling() {
        if (castlingP != null) {
            if (castlingP.col == 0) {
                castlingP.col += 3;
            } else if (castlingP.col == 7) {
                castlingP.col -= 2;
            }
            castlingP.x = castlingP.getX(castlingP.col);
        }
    }
    
    private void changePlayer() {
        if (currentColor == WHITE) {
            currentColor = BLACK;
            
            // reset
            for (piece piece : pieces) {
                if (piece.color == BLACK) {
                    piece.twoStepped = false;
                }
            }
        } else {
            currentColor = WHITE;
            
            // reset white
            for (piece piece : pieces) {
                if (piece.color == WHITE) {
                    piece.twoStepped = false;
                }
            }
        }
        
        activeP = null;
    }
    
    private boolean canPromote() {
        if (activeP.type == Type.PAWN) {
            if ((currentColor == WHITE && activeP.row == 0) || (currentColor == BLACK && activeP.row == 7)) {
                PromoPieces.clear();
                PromoPieces.add(new Rook(currentColor, 9, 2));
                PromoPieces.add(new Knight(currentColor, 9, 3));
                PromoPieces.add(new Bishop(currentColor, 9, 4));
                PromoPieces.add(new Queen(currentColor, 9, 5));
                return true;
            }
        }
        return false;
    }
    
    private void promoting() {
        if (mouse.pressed) {
            for (piece piece : PromoPieces) {
                if (piece.col == mouse.x / Board.SQUARESIZE && piece.row == mouse.y / Board.SQUARESIZE) {
                    switch (piece.type) {
                        case ROOK:
                            simpieces.add(new Rook(currentColor, activeP.col, activeP.row));
                            break;
                        case KNIGHT:
                            simpieces.add(new Knight(currentColor, activeP.col, activeP.row));
                            break;
                        case BISHOP:
                            simpieces.add(new Bishop(currentColor, activeP.col, activeP.row));
                            break;
                        case QUEEN:
                            simpieces.add(new Queen(currentColor, activeP.col, activeP.row));
                            break;
                        default:
                            break;
                    }
                    simpieces.remove(activeP.getIndex());
                    copyPieces(simpieces, pieces);
                    activeP = null;
                    promotion = false;
                    changePlayer();
                }
            }
        }
    }
    
    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);

        Graphics2D g2 = (Graphics2D) g;

        // Draw the board
        board.draw(g2);

        // Draw all pieces
        for (piece p : simpieces) {
            p.draw(g2);
        }

        // If there's an active piece, highlight legal moves
        if (activeP != null) {
            // Loop through all board squares to check for valid moves
            for (int row = 0; row < Board.SQUARESIZE; row++) {
                for (int col = 0; col < Board.SQUARESIZE; col++) {
                    if (activeP.canMove(col, row)) { // Check if the move is valid
                        // Highlight the square with a semi-transparent color
                        g2.setColor(new Color(173, 216, 230, 150)); // Green with alpha
                        g2.fillRect(col * Board.SQUARESIZE, row * Board.SQUARESIZE, Board.SQUARESIZE, Board.SQUARESIZE);
                    }
                }
            }

            // Draw the active piece last to ensure it's on top of the highlighted squares
            activeP.draw(g2);
        }

        // Draw status messages
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        g2.setFont(new Font("Poppins", Font.PLAIN, 35));
        g2.setColor(Color.white);

        if (promotion) {
            g2.drawString("Promote to", 840, 150);
            for (piece piece : PromoPieces) {
                g2.drawImage(piece.image, piece.getX(piece.col), piece.getY(piece.row), Board.SQUARESIZE, Board.SQUARESIZE, null);
            }
        } else {
            if (currentColor == WHITE) {
                g2.drawString("White's turn", 840, 550);
                if (checkingP != null && checkingP.color == BLACK) {
                    g2.setColor(Color.red);
                    g2.drawString("The King", 840, 650);
                    g2.drawString("is in check!", 840, 700);
                }
            } else {
                g2.drawString("Black's turn", 840, 250);
                if (checkingP != null && checkingP.color == WHITE) {
                    g2.setColor(Color.red);
                    g2.drawString("The King", 840, 650);
                    g2.drawString("is in check!", 840, 700);
                }
            }
        }

        // Draw game over message if applicable
        if (gameover) {
            String s = (currentColor == WHITE) ? "White wins" : "Black wins";

            // Set font for text
            Font font = new Font("Poppins", Font.PLAIN, 90);
            g2.setFont(font);

            // Calculate text dimensions
            FontMetrics metrics = g2.getFontMetrics(font);
            int textWidth = metrics.stringWidth(s);
            int textHeight = metrics.getHeight();

            // Position of text
            int textX = 200;
            int textY = 420;

            // Draw gradient background for text
            Color startColor = new Color(0, 128, 0); // Dark green
            Color endColor = new Color(34, 139, 34); // Forest green
            GradientPaint gradient = new GradientPaint(textX, textY, startColor, textX + textWidth, textY + textHeight, endColor);
            g2.setPaint(gradient);
            g2.fillRect(textX - 10, textY - textHeight + metrics.getDescent() - 10, textWidth + 20, textHeight + 20);

            // Draw shadow for text
            g2.setColor(Color.darkGray);
            g2.drawString(s, textX + 3, textY + 3); // Shadow offset

            // Draw the text
            g2.setColor(Color.white);
            g2.drawString(s, textX, textY);
        }

        // Draw stalemate message if applicable
        if (stalemate) {
            String stalemateText = "Stalemate";

            // Set font for text
            Font stalemateFont = new Font("Poppins", Font.PLAIN, 90);
            g2.setFont(stalemateFont);

            // Calculate text dimensions
            FontMetrics stalemateMetrics = g2.getFontMetrics(stalemateFont);
            int textWidth = stalemateMetrics.stringWidth(stalemateText);
            int textHeight = stalemateMetrics.getHeight();

            // Position of text
            int textX = 200;
            int textY = 420;

            // Draw gradient background for text
            Color startColor = new Color(224, 255, 255); // Light Cyan
            Color endColor = new Color(175, 238, 238); // Light Cyan
            GradientPaint gradient = new GradientPaint(textX, textY, startColor, textX + textWidth, textY + textHeight, endColor);
            g2.setPaint(gradient);
            g2.fillRect(textX - 10, textY - textHeight + stalemateMetrics.getDescent() - 10, textWidth + 20, textHeight + 20);

            // Draw shadow for text
            g2.setColor(Color.darkGray);
            g2.drawString(stalemateText, textX + 3, textY + 3); // Shadow offset

            // Draw the text
            g2.setColor(Color.black);
            g2.drawString(stalemateText, textX, textY);
        }
    }


}
