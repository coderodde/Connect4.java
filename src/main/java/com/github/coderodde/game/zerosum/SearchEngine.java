package com.github.coderodde.game.zerosum;

/**
 *
 * @param <S> the game state type. 
 * 
 * @version 1.0.0 (Jun 5, 2024)
 * @since 1.0.0 (Jun 5, 2024)
 */
public interface SearchEngine<S extends GameState<S>> {
    
    /**
     * Runs the search for the next most move.
     * 
     * @param root the root of the game tree.
     * @param depth the maximal search tree depth.
     * 
     * @return the next move to perform.
     */
    public S search(final S root, final int depth);
}
