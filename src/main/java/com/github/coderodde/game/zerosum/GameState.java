package com.github.coderodde.game.zerosum;

import java.util.List;

/**
 *
 * @param <B> the board type.
 * 
 * @version 1.0.0 (Jun 5, 2024)
 * @since 1.0.0 (Jun 5, 2024)
 */
public interface GameState<B extends GameState<B>> {
 
    /**
     * Generates all the child states of this game state assuming it is 
     * {@code playerType}'s turn.
     * 
     * @param playerType the player type.
     * 
     * @return the list of child game states.
     */
    public List<B> expand(final PlayerType playerType);
    
    /**
     * Returns {@code true} if and only if this game state is terminal: one of 
     * the two players won, or a tie occurred.
     * 
     * @param playerType the target player type to check.
     * 
     * @return a boolean flag indicating whether this game state is terminal.
     */
    public boolean isWinningFor(final PlayerType playerType);
    
    /**
     * Returns {@code true} iff this game state is full, and is thus a tie.
     * 
     * @return {@code true} only if this game states represents a tie.
     */
    public boolean isTie();
}
