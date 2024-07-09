# Chess-Game-in-Java-Using-Swing-and-AWT

This repository contains a Java implementation of a chess game. The game is implemented using Java Swing for the GUI and supports various features such as piece movement, check, checkmate, stalemate, and piece promotion.

# Features
- Graphical User Interface: The game is built using Java Swing for a responsive and interactive chess board.
- Piece Movement: All standard chess pieces (Pawn, Rook, Knight, Bishop, Queen, King) and their movements are implemented.
- Check and Checkmate Detection: The game detects check and checkmate conditions, ending the game when a checkmate occurs.
- Stalemate Detection: The game also detects stalemate conditions.
- Piece Promotion: Pawns can be promoted to Rook, Knight, Bishop, or Queen when they reach the opposite side of the board.
- Castling: Both kingside and queenside castling are supported.
- Turn-Based Gameplay: The game alternates turns between the two players (white and black).

# Technologies Used
- Java: Programming language used to build the application.
- Swing: GUI widget toolkit for Java, used for creating the user interface.
- AWT (Abstract Window Toolkit): Used indirectly through Swing for rendering graphics and handling events.

# Classes Overview
# Main Classes
- GamePanel: This is the main class that sets up the game board and handles the game loop, piece movement, and rendering.
- Board: This class represents the chess board and is responsible for drawing the board.
- Mouse: This class handles mouse input for selecting and moving pieces.
  
# Piece Classes
- Piece: The base class for all chess pieces.
- Pawn, Rook, Knight, Bishop, Queen, King: These classes extend the Piece class and implement specific movement rules for each type of piece.
  
# How to Play
- Launch the game.
- The game starts with Brown's turn. Click on a piece to select it.
- Move the piece to a valid square. The game will highlight possible moves.
- Alternate turns between black & brown.
- The game will automatically detect check, checkmate, and stalemate conditions.
- For pawn promotion, click on the desired piece (Rook, Knight, Bishop, Queen) when prompted.

# Screenshots
