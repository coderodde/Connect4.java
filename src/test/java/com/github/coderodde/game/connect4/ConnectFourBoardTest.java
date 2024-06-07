package com.github.coderodde.game.connect4;

import static com.github.coderodde.game.connect4.ConnectFourBoard.COLUMNS;
import static com.github.coderodde.game.connect4.ConnectFourBoard.ROWS;
import static com.github.coderodde.game.connect4.ConnectFourBoard.VICTORY_LENGTH;
import com.github.coderodde.game.zerosum.PlayerType;
import java.awt.Point;
import java.util.List;
import org.junit.Test;
import static org.junit.Assert.*;

public class ConnectFourBoardTest {
    
    @Test
    public void testExpand() {
        ConnectFourBoard b = new ConnectFourBoard();
        
        assertTrue(b.makePly(3, PlayerType.MINIMIZING_PLAYER));
        assertTrue(b.makePly(4, PlayerType.MAXIMIZING_PLAYER));
        
        List<ConnectFourBoard> children = 
                b.expand(PlayerType.MINIMIZING_PLAYER);
        
        assertEquals(PlayerType.MINIMIZING_PLAYER, children.get(0).get(0, 5));
        assertEquals(PlayerType.MINIMIZING_PLAYER, children.get(1).get(1, 5));
        assertEquals(PlayerType.MINIMIZING_PLAYER, children.get(2).get(2, 5));
        assertEquals(PlayerType.MINIMIZING_PLAYER, children.get(3).get(3, 5));
        assertEquals(PlayerType.MAXIMIZING_PLAYER, children.get(4).get(4, 5));
        assertEquals(PlayerType.MINIMIZING_PLAYER, children.get(5).get(5, 5));
        assertEquals(PlayerType.MINIMIZING_PLAYER, children.get(6).get(6, 5));
        
        assertEquals(PlayerType.MINIMIZING_PLAYER, children.get(3).get(3, 4));
        assertEquals(PlayerType.MINIMIZING_PLAYER, children.get(4).get(4, 4));
    }

    @Test
    public void testIsWinningForHorizontal() {
        ConnectFourBoard b = new ConnectFourBoard();
        
        b.makePly(2, PlayerType.MINIMIZING_PLAYER);
        b.makePly(3, PlayerType.MINIMIZING_PLAYER);
        b.makePly(4, PlayerType.MINIMIZING_PLAYER);
        b.makePly(5, PlayerType.MINIMIZING_PLAYER);
        
        assertTrue(b.hasHorizontalStrike(
                PlayerType.MINIMIZING_PLAYER,
                VICTORY_LENGTH));
        
        b.unmakePly(3);
        
        // X = 3 is missing:
        assertFalse(b.hasHorizontalStrike(
                PlayerType.MINIMIZING_PLAYER, 
                VICTORY_LENGTH));
        
        b.makePly(3, PlayerType.MAXIMIZING_PLAYER);
        
        // X = 3 is another player type:
        assertFalse(b.hasHorizontalStrike(
                PlayerType.MINIMIZING_PLAYER, 
                VICTORY_LENGTH));
    }

    @Test
    public void testIsWinningForVertical() {
        ConnectFourBoard b = new ConnectFourBoard();
        
        b.makePly(2, PlayerType.MINIMIZING_PLAYER);
        b.makePly(2, PlayerType.MINIMIZING_PLAYER);
        b.makePly(2, PlayerType.MINIMIZING_PLAYER);
        b.makePly(2, PlayerType.MINIMIZING_PLAYER);
        
        assertTrue(b.hasVerticalStrike(
                PlayerType.MINIMIZING_PLAYER,
                VICTORY_LENGTH));
        
        b.unmakePly(2);
        
        // Y = 3 is missing:
        assertFalse(b.hasVerticalStrike(
                PlayerType.MINIMIZING_PLAYER, 
                VICTORY_LENGTH));
        
        b.makePly(2, PlayerType.MAXIMIZING_PLAYER);
        
        // Y = 3 is another player type:
        assertFalse(b.hasVerticalStrike(
                PlayerType.MINIMIZING_PLAYER, 
                VICTORY_LENGTH));
    }

    @Test
    public void testIsWinningForAscending() {
        ConnectFourBoard b = new ConnectFourBoard();
        //        X
        //       X0
        //      XOX
        //     XOOX
        // Build bottom layer:
        b.makePly(2, PlayerType.MINIMIZING_PLAYER);
        b.makePly(3, PlayerType.MAXIMIZING_PLAYER);
        b.makePly(4, PlayerType.MAXIMIZING_PLAYER);
        b.makePly(5, PlayerType.MINIMIZING_PLAYER);
        
        // Third last layer:
        b.makePly(3, PlayerType.MINIMIZING_PLAYER);
        b.makePly(4, PlayerType.MAXIMIZING_PLAYER);
        b.makePly(5, PlayerType.MINIMIZING_PLAYER);
        
        // Second last layer:
        b.makePly(4, PlayerType.MINIMIZING_PLAYER);
        b.makePly(5, PlayerType.MAXIMIZING_PLAYER);
        
        // Top layer:
        b.makePly(5, PlayerType.MINIMIZING_PLAYER);
        
        assertTrue(b.hasAscendingDiagonalStrike(
                PlayerType.MINIMIZING_PLAYER,
                VICTORY_LENGTH));
        
        b.unmakePly(3);
        
        // X = 3 missing:
        assertFalse(b.hasAscendingDiagonalStrike(
                PlayerType.MINIMIZING_PLAYER, 
                VICTORY_LENGTH));
    }

    @Test
    public void testIsWinningForDescending() {
        ConnectFourBoard b = new ConnectFourBoard();
        //    X
        //    OX
        //    XOX
        //    XOOX
        // Build bottom layer:
        b.makePly(2, PlayerType.MINIMIZING_PLAYER);
        b.makePly(3, PlayerType.MAXIMIZING_PLAYER);
        b.makePly(4, PlayerType.MAXIMIZING_PLAYER);
        b.makePly(5, PlayerType.MINIMIZING_PLAYER);
        
        // Third last layer:
        b.makePly(2, PlayerType.MINIMIZING_PLAYER);
        b.makePly(3, PlayerType.MAXIMIZING_PLAYER);
        b.makePly(4, PlayerType.MINIMIZING_PLAYER);
        
        // Second last layer:
        b.makePly(2, PlayerType.MAXIMIZING_PLAYER);
        b.makePly(3, PlayerType.MINIMIZING_PLAYER);
        
        // Top layer:
        b.makePly(2, PlayerType.MINIMIZING_PLAYER);
        
        assertTrue(b.hasDescendingDiagonalStrike(
                PlayerType.MINIMIZING_PLAYER,
                VICTORY_LENGTH));
        
        b.unmakePly(3);
        
        // X = 3 missing:
        assertFalse(b.hasDescendingDiagonalStrike(
                PlayerType.MINIMIZING_PLAYER, 
                VICTORY_LENGTH));
    }
    
    @Test
    public void testIsTie() {
        ConnectFourBoard b = new ConnectFourBoard();
        
        for (int y = 0; y < ROWS; y++) {
            for (int x = 0; x < COLUMNS; x++) {
                b.makePly(x, PlayerType.MINIMIZING_PLAYER);
            }
        }
        
        assertTrue(b.isTie());
        b.unmakePly(5);
        assertFalse(b.isTie());
    }
    
    @Test
    public void testGetWinningPattern() {
        ConnectFourBoard b = new ConnectFourBoard();
        
        for (int x = 2; x < 2 + 4; x++) {
            b.makePly(x, PlayerType.MINIMIZING_PLAYER);
        }
        
        List<Point> winningPattern = b.getWinningPattern();
        
        assertEquals(new Point(2, ROWS - 1), winningPattern.get(0));
        assertEquals(new Point(3, ROWS - 1), winningPattern.get(1));
        assertEquals(new Point(4, ROWS - 1), winningPattern.get(2));
        assertEquals(new Point(5, ROWS - 1), winningPattern.get(3));
       
        b.unmakePly(5);
        
        winningPattern = b.getWinningPattern();
        
        assertNull(winningPattern);
    }
    
    @Test
    public void testMakeUnmakePly() {
        ConnectFourBoard b = new ConnectFourBoard();
        
        assertTrue(b.makePly(4, PlayerType.MINIMIZING_PLAYER));
        assertTrue(b.makePly(4, PlayerType.MAXIMIZING_PLAYER));
        
        assertEquals(PlayerType.MINIMIZING_PLAYER, b.get(4, ROWS - 1));
        assertEquals(PlayerType.MAXIMIZING_PLAYER, b.get(4, ROWS - 2));
    }
}
