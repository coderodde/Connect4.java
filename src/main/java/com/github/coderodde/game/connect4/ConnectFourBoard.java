package com.github.coderodde.game.connect4;

import com.github.coderodde.game.zerosum.PlayerType;
import com.github.coderodde.game.zerosum.GameState;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 *
 * @version 1.0.0 (Jun 5, 2024)
 * @since 1.0.0 (Jun 5, 2024)
 */
public class ConnectFourBoard implements GameState<ConnectFourBoard> {

    static final int ROWS = 6;
    static final int COLUMNS = 7;
    static final int VICTORY_LENGTH = 4;
    
    final PlayerType[][] boardData = new PlayerType[ROWS][COLUMNS];
    private final List<ConnectFourBoard> childrenTree = new ArrayList<>(COLUMNS);
    
    public ConnectFourBoard(final PlayerType[][] boardData) {
        for (int y = 0; y < ROWS; y++) {
            this.boardData[y] = Arrays.copyOf(boardData[y], COLUMNS);
        }
    }
    
    public ConnectFourBoard() {
        
    }
    
    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        
        for (int y = 0; y < ROWS; y++) {
            // Build the row:
            for (int x = 0; x < COLUMNS; x++) {
                sb.append("|");
                sb.append(getCellChar(boardData[y][x]));
            }
            
            sb.append("|\n");
        }
        
        sb.append("+-+-+-+-+-+-+-+\n");
        sb.append(" 1 2 3 4 5 6 7");
        
        return sb.toString();
    }
    
    @Override
    public List<ConnectFourBoard> expand(final PlayerType playerType) {
        if (!childrenTree.isEmpty()) {
            // Children are already computed, just return them:
            return childrenTree;
        }
        
        for (int x = 0; x < COLUMNS; x++) {
            if (notFullAtX(x)) {
                childrenTree.add(dropAtX(x, playerType));
            }
        }
        
        return childrenTree;
    }
    
    @Override
    public boolean isWinningFor(final PlayerType playerType) {
        if (isTerminalHorizontal(playerType)) {
            return true;
        }
        
        if (isTerminalVertical(playerType)) {
            return true;
        }
        
        if (isTerminalAscendingDiagonal(playerType)) {
            return true;
        }
        
        if (isTerminalDescendingDiagonal(playerType)) {
            return true;
        }
        
        return false;
    }
    
    @Override
    public boolean isTie() {
        for (int x = 0; x < COLUMNS; x++) {
            if (boardData[0][x] == null) {
                return false;
            }
        }
        
        return true;
    }
    
    
    
    public ConnectFourBoard makePly(final int x, final PlayerType playerType) {
        return dropAtX(x, playerType);
    }
    
    private boolean isTerminalHorizontal(final PlayerType playerType) {
        int lastX = COLUMNS - VICTORY_LENGTH;
        
        for (int y = ROWS - 1; y >= 0; y--) {
            horizontalCheck:
            for (int x = 0; x <= lastX; x++) {
                for (int i = x; i < x + VICTORY_LENGTH; i++) {
                    if (boardData[y][i] != playerType) {
                        continue horizontalCheck;
                    }
                }
                
                return true;
            }
        }
        
        return false;
    }
    
    private boolean isTerminalVertical(final PlayerType playerType) {
        int lastY = ROWS - VICTORY_LENGTH;
        
        for (int x = 0; x < COLUMNS; x++) {
            verticalCheck:
            for (int y = 0; y <= lastY; y++) {
                for (int i = y; i < y + VICTORY_LENGTH; i++) {
                    if (boardData[i][x] != playerType) {
                        continue verticalCheck;
                    }
                }
                
                return true;
            }
        }
        
        return false;
    }
    
    private boolean isTerminalAscendingDiagonal(final PlayerType playerType) {
        int lastX = COLUMNS - VICTORY_LENGTH;
        int lastY = ROWS - VICTORY_LENGTH + 1;
        
        for (int y = ROWS - 1; y >= lastY; y--) {
            diagonalCheck:
            for (int x = 0; x <= lastX; x++) {
                for (int i = 0; i < VICTORY_LENGTH; i++) {
                    if (boardData[y - i][x + i] != playerType) {
                        continue diagonalCheck;
                    }
                }
                
                return true;
            }
        }
        
        return false;
    }
    
    private boolean isTerminalDescendingDiagonal(final PlayerType playerType) {
        int lastX = VICTORY_LENGTH - 1;
        int lastY = ROWS - VICTORY_LENGTH + 1;
        
        for (int y = ROWS - 1; y >= lastY; y--) {
            diagonalCheck:
            for (int x = lastX; x < COLUMNS; x++) {
                for (int i = 0; i < VICTORY_LENGTH; i++) {
                    if (boardData[y - i][x - i] != playerType) {
                        continue diagonalCheck;
                    }
                }
                
                return true;
            }
        }
        
        return false;
    }
    
    private boolean notFullAtX(final int x) {
        return boardData[0][x] == null;
    }
    
    private ConnectFourBoard dropAtX(final int x, final PlayerType playerType) {
        final ConnectFourBoard nextBoard = new ConnectFourBoard();
        
        for (int y = 0; y < ROWS; y++) {
            nextBoard.boardData[y] = Arrays.copyOf(this.boardData[y], COLUMNS);
        }
        
        for (int y = ROWS - 1; y >= 0; y--) {
            if (nextBoard.boardData[y][x] == null) {
                nextBoard.boardData[y][x] = playerType;
                return nextBoard;
            }
        }
        
        throw new IllegalStateException("Should not get here.");
    }
    
    private static char getCellChar(final PlayerType playerType) {
        if (playerType == null) {
            return '.';
        }
        
        switch (playerType) {
            case MAXIMIZING_PLAYER:
                return 'O';
                
            case MINIMIZING_PLAYER:
                return 'X';
                
            default:
                throw new IllegalStateException("Should not get here.");
        }
    }
}
