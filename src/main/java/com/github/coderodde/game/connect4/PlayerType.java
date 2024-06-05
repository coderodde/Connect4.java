package com.github.coderodde.game.connect4;

/**
 *
 * @version 1.0.0 (Jun 5, 2024)
 * @since 1.0.0 (Jun 5, 2024)
 */
public enum PlayerType {
    
    MINIMIZING_PLAYER,
    MAXIMIZING_PLAYER;
    
    public PlayerType flip() {
        if (this == MINIMIZING_PLAYER) {
            return MAXIMIZING_PLAYER;
        }
        
        return MINIMIZING_PLAYER;
    }
}
