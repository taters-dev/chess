# MY NOTES

## Work Flow
### Phase 0: Chess Moves
#### 1. ChessPosition <span style="color: green;">[DONE]</span>
#### 2. ChessBoard <span style="color: green;">[DONE]</span>
#### 3. ChessPiece
#### 4. ChessMove

## Development Notes

### Monday, Jan 12

#### First Commit:
    Read through the project files and I feel the most logical place to start is
    with ChessPosition.java because it is needed for all the rest of the classes.

<span style="color: green;">[Passed test cases]</span> ChessPosition.java - 1/12 7:35 pm<br>

    - Simple Constructor and Getter methods
    - Struggled at first with overriding equals() and hashCode() however after reading the tips from the course github it was fine.

#### Second Commit:
    Realized that I need to include a piece object defaulted to null for every instance of ChessPositon. So I have made that change. Also added a getter method for the piece within ChessPosition class.
    
    - Added piece to ChessPosition constructor method
    - Successfully built ChessBoard constructor method
    - Code now passes Construct Empty ChessBoard testcase

#### Third Commit:
    Realized that I need to implement part of the ChessPiece class in order to fully implement the ChessBoard class as well.

    - Successfully implemented Constructor and getter method for ChessPieces aside from pieceMoves

#### Fourth Commit:
    - Sucessfully implemented methods getPiece and addPiece in ChessBoard class. Having issues with HashCode and Equals Testing.

#### Fifth Commit:
    - Found my issue for ChessBoard I was not resetting the board to have all the pieces because I miss understood that it was to put all the pieces on the board. I fixed this and Now all test cases pass for ChessBoard class.

<span style="color: green;">[Passed test cases]</span> ChessBoard.java - 1/12 10:51 pm<br>

#### Sixth Commit
    - Fixed typos

#### Seventh Commit
    - Got each piece class set up now just need to implement move logic for each separate piece. 

#### Eighth Commit
    - Started with PawnPiece class realized it would be the most complicated so I implemented all others besides Knight and Pawn. 
    - Have a better understanding of how to make these classes work now so I hope that pawn will be easier than before. 
    - Finished King, Rook, Bishop, Queen

#### Ninth Commit
    - Finished moves for the KnightPiece class
    - Passing all test Cases otuside of PawnMoveTests
