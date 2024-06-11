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
     * Runs the search for the next most move assuming its the turn of the 
     * maximizing player type.
     * 
     * @param root the root of the game tree.
     * @param depth the maximal search tree depth.
     * 
     * @return the next move to perform.
     */
    public default S search(final S root, final int depth) {
        return search(root, depth, PlayerType.MAXIMIZING_PLAYER);
    }
    
    /**
     * Computes the next move state for a particular player type.
     * 
     * @param root       the root node of the game search tree.
     * @param depth      the search depth.
     * @param playerType the type of the player: minimizing vs. maximizing.
     * 
     * @return the next move state.
     */
    public S search(final S root, 
                    final int depth, 
                    final PlayerType playerType);
}
