package com.github.coderodde.game.connect4;

import com.github.coderodde.game.zerosum.PlayerType;
import com.github.coderodde.game.zerosum.GameState;
import java.awt.Point;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * This class implements a board that corresponds to a game state in the game
 * search tree.
 * 
 * @version 1.0.0 (Jun 5, 2024)
 * @since 1.0.0 (Jun 5, 2024)
 */
public class ConnectFourBoard implements GameState<ConnectFourBoard> {

    public static final int ROWS = 6;
    public static final int COLUMNS = 7;
    public static final int VICTORY_LENGTH = 4;
    
    final PlayerType[] boardData = new PlayerType[ROWS * COLUMNS];
    
    public ConnectFourBoard(final ConnectFourBoard other) {
        System.arraycopy(other.boardData, 0, boardData, 0, ROWS * COLUMNS);
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
                sb.append(getCellChar(get(x, y)));
            }
            
            sb.append("|\n");
        }
        
        sb.append("+-+-+-+-+-+-+-+\n");
        sb.append(" 1 2 3 4 5 6 7");
        
        return sb.toString();
    }
    
    @Override
    public List<ConnectFourBoard> expand(final PlayerType playerType) {
        final List<ConnectFourBoard> children = new ArrayList<>(COLUMNS);
        
        for (int x = 0; x < COLUMNS; x++) {
            if (notFullAtX(x)) {
                children.add(dropAtX(x, playerType));
            }
        }
        
        return children;
    }
    
    @Override
    public boolean isWinningFor(final PlayerType playerType) {
        return hasAscendingDiagonalStrike(playerType, VICTORY_LENGTH) ||
              hasDescendingDiagonalStrike(playerType, VICTORY_LENGTH) ||
                      hasHorizontalStrike(playerType, VICTORY_LENGTH) ||
                        hasVerticalStrike(playerType, VICTORY_LENGTH);
    }
    
    @Override
    public boolean isTie() {
        for (int x = 0; x < COLUMNS; x++) {
            if (get(x, 0) == null) {
                return false;
            }
        }
        
        return true;
    }
    
    @Override
    public boolean equals(Object o) {
        if (o == null) {
            return false;
        }
            
        if (o == this) {
            return true;
        }
        
        if (!this.getClass().equals(o.getClass())) {
            return false;
        }
        
        final ConnectFourBoard other = (ConnectFourBoard) o;
        
        return Arrays.equals(boardData, other.boardData);
    }
    
    @Override
    public int hashCode() {
        return Arrays.hashCode(boardData);
    }
    
    public List<Point> getWinningPattern() {
        if (!isWinningFor(PlayerType.MINIMIZING_PLAYER) &&
            !isWinningFor(PlayerType.MAXIMIZING_PLAYER)) {
            return null;
        }
        
        List<Point> winningPattern = null;
        
        for (int length = ROWS; length >= VICTORY_LENGTH; length--) {
        
            // Try load the vertical winning pattern:
            winningPattern = 
                    tryLoadVerticalWinningPattern(PlayerType.MINIMIZING_PLAYER,
                                                  length);

            if (winningPattern != null) {
                return winningPattern;
            }
        
            winningPattern = 
                    tryLoadVerticalWinningPattern(PlayerType.MAXIMIZING_PLAYER, 
                                                  length);

            if (winningPattern != null) {
                return winningPattern;
            }
            
            // Try to load the ascending winning pattern:
            winningPattern =
                tryLoadAscendingWinningPattern(PlayerType.MINIMIZING_PLAYER,
                                               length);
        
            if (winningPattern != null) {
                return winningPattern;
            }
            
            winningPattern =
                tryLoadAscendingWinningPattern(PlayerType.MAXIMIZING_PLAYER,
                                               length);
        
            if (winningPattern != null) {
                return winningPattern;
            }

            // Try to load the descending winning pattern:
            winningPattern = 
                    tryLoadDescendingWinningPattern(
                            PlayerType.MINIMIZING_PLAYER,
                            length);

            if (winningPattern != null) {
                return winningPattern;
            }

            winningPattern = 
                    tryLoadDescendingWinningPattern(
                            PlayerType.MAXIMIZING_PLAYER,
                            length);
            
            if (winningPattern != null) {
                return winningPattern;
            }
        }

        for (int length = COLUMNS; length >= VICTORY_LENGTH; length--) {
            
            winningPattern = 
                    tryLoadHorizontalWinningPattern(
                            PlayerType.MINIMIZING_PLAYER,
                            length);
            
            if (winningPattern != null) {
                return winningPattern;
            }
            
            winningPattern = 
                    tryLoadHorizontalWinningPattern(
                            PlayerType.MAXIMIZING_PLAYER,
                            length);
            
            if (winningPattern != null) {
                return winningPattern;
            }
        }
        
        throw new IllegalStateException("Should not get here.");
    }
    
    public PlayerType get(final int x, final int y) {
        return boardData[y * COLUMNS + x];
    }
    
    public void set(final int x,
                    final int y,
                    final PlayerType playerType) {
        boardData[y * COLUMNS + x] = playerType;
    }
    
    public boolean makePly(final int x, final PlayerType playerType) {
        for (int y = ROWS - 1; y >= 0; y--) {
            if (get(x, y) == null) {
                set(x, y, playerType);
                return true;
            }
        }
        
        return false;
    }
    
    public void unmakePly(final int x) {
        for (int y = 0; y < ROWS; y++) {
            if (get(x, y) != null) {
                set(x, y, null);
                return;
            }
        }
    }
    
    boolean hasHorizontalStrike(final PlayerType playerType, final int length) {
        
        final int lastX = COLUMNS - length;
        
        for (int y = ROWS - 1; y >= 0; y--) {
            horizontalCheck:
            for (int x = 0; x <= lastX; x++) {
                for (int i = 0; i < length; i++) {
                    if (get(x + i, y) != playerType) {
                        continue horizontalCheck;
                    }
                }
                
                return true;
            }
        }
        
        return false;
    }
    
    boolean hasVerticalStrike(final PlayerType playerType, final int length) {
        
        int lastY = ROWS - length;
        
        for (int x = 0; x < COLUMNS; x++) {
            verticalCheck:
            for (int y = 0; y <= lastY; y++) {
                for (int i = 0; i < length; i++) {
                    if (get(x, y + i) != playerType) {
                        continue verticalCheck;
                    }
                }
                
                return true;
            }
        }
        
        return false;
    }
    
    boolean hasAscendingDiagonalStrike(final PlayerType playerType, 
                                       final int length) {
        
        final int lastX = COLUMNS - length;
        final int lastY = ROWS - length;
        
        for (int y = ROWS - 1; y > lastY; y--) {
            diagonalCheck:
            for (int x = 0; x <= lastX; x++) {
                for (int i = 0; i < length; i++) {
                    if (get(x + i, y - i) != playerType) {
                        continue diagonalCheck;
                    }
                }
                
                return true;
            }
        }
        
        return false;
    }
    
    boolean hasDescendingDiagonalStrike(final PlayerType playerType, 
                                        final int length) {
        
        final int firstX = COLUMNS - length;
        final int lastY = ROWS - length;
        
        for (int y = ROWS - 1; y > lastY; y--) {
            diagonalCheck:
            for (int x = firstX; x < COLUMNS; x++) {
                for (int i = 0; i < length; i++) {
                    if (get(x - i, y - i) != playerType) {
                        continue diagonalCheck;
                    }
                }
                
                return true;
            }
        }
        
        return false;
    }
    
    private List<Point> tryLoadAscendingWinningPattern(
            final PlayerType playerType,
            final int length) {
        
        final int lastX = COLUMNS - length;
        final int lastY = ROWS - length;
        final List<Point> winningPattern = new ArrayList<>(length);
        
        for (int y = ROWS - 1; y > lastY; y--) {
            diagonalCheck:
            for (int x = 0; x <= lastX; x++) {
                for (int i = 0; i < length; i++) {
                    if (get(x + i, y - i) == playerType) {
                        winningPattern.add(new Point(x + i, y - i));
                        
                        if (winningPattern.size() == length) {
                            return winningPattern;
                        }
                    } else {
                        winningPattern.clear();
                        continue diagonalCheck;
                    }
                }
            }
        }
        
        return null;
    }
    
    private List<Point> tryLoadDescendingWinningPattern(
            final PlayerType playerType,
            final int length) {
        
        final int firstX = length - 1;
        final int lastY = ROWS - length;
        final List<Point> winningPattern = new ArrayList<>(length);
        
        for (int y = ROWS - 1; y > lastY; y--) {
            diagonalCheck:
            for (int x = firstX; x < COLUMNS; x++) {
                for (int i = 0; i < length; i++) {
                    if (get(x - i, y - i) == playerType) {
                        winningPattern.add(new Point(x - i, y - i));
                        
                        if (winningPattern.size() == length) {
                            return winningPattern;
                        }
                    } else {
                        winningPattern.clear();
                        continue diagonalCheck;
                    }
                }
            }
        }
        
        return null;
    }
    
    private List<Point> tryLoadHorizontalWinningPattern(
            final PlayerType playerType,
            final int length) {
        
        final int lastX = COLUMNS - length;
        final List<Point> winningPattern = new ArrayList<>(length);
        
        for (int y = ROWS - 1; y >= 0; y--) {
            horizontalCheck:
            for (int x = 0; x <= lastX; x++) {
                for (int i = 0; i < length; i++) {
                    if (get(x + i, y) == playerType) {
                        winningPattern.add(new Point(x + i, y));
                        
                        if (winningPattern.size() == length) {
                            return winningPattern;
                        }
                    } else {
                        winningPattern.clear();
                        continue horizontalCheck;
                    }
                }
            }
        }
        
        return null;
    }
    
    private List<Point> tryLoadVerticalWinningPattern(
            final PlayerType playerType,
            final int length) {
        
        final int lastY = ROWS - length;
        final List<Point> winningPattern = new ArrayList<>(length);
        
        for (int x = 0; x < COLUMNS; x++) {
            verticalCheck:
            for (int y = 0; y <= lastY; y++) {
                for (int i = 0; i < length; i++) {
                    if (get(x, y + i) == playerType) {
                        winningPattern.add(new Point(x, y + i));
                        
                        if (winningPattern.size() == length) {
                            return winningPattern;
                        }
                    } else {
                        winningPattern.clear();
                        continue verticalCheck;
                    }
                }
            }
        }
        
        return null;
    }
    
    private boolean notFullAtX(final int x) {
        return get(x, 0) == null;
    }
    
    private ConnectFourBoard dropAtX(final int x, final PlayerType playerType) {
        final ConnectFourBoard nextBoard = new ConnectFourBoard(this);
        
        for (int y = ROWS - 1; y >= 0; y--) {
            if (nextBoard.get(x, y) == null) {
                nextBoard.set(x, y, playerType);
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
    
    private int nextXIndex(final int x, final int y) {
        return y * COLUMNS + x + 1;
    }
    
    private int nextYIndex(final int x, final int y) {
        return y * (COLUMNS + 1) + x;
    }
    
    private int nextAscendingDiagonalIndex(final int x, final int y) {
        return y * (COLUMNS - 1) + x - 1;
    }
    
    private int nextDescendingDiagonalIndex(final int x, final int y) {
        return y * (COLUMNS - 1) - x + 1;
    }
}
