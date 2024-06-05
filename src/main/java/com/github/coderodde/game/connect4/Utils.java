package com.github.coderodde.game.connect4;

/**
 *
 * @version 1.0.0 (Jun 5, 2024)
 * @since 1.0.0 (Jun 5, 2024)
 */
public final class Utils {
    
    public static String 
        convertPlayerTypeToString(final PlayerType playerType) {
        switch (playerType) {
            case MAXIMIZING_PLAYER:
                return "O";
                
            case MINIMIZING_PLAYER:
                return "X";
                
            default:
                throw new IllegalStateException("Should not get here.");
        }
    }
}
