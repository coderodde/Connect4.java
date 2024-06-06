package com.github.coderodde.game.zerosum;

public interface HeuristicFunction<S extends GameState<S>> {
    
    /**
     * Evaluates the input state.
     * 
     * @param state the input state of which score to compute.
     * @param depth the depth of {@code state} in the search tree.
     * 
     * @return the score of the input game state.
     */
    public double evaluate(final S state, final int depth);
}
