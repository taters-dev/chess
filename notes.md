# MY NOTES

## Work Flow
### Phase 0: Chess Moves
#### 1. ChessPosition <span style="color: green;">[DONE]</span>
#### 2. ChessBoard
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



