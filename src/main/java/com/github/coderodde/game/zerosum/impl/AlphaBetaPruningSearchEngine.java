package com.github.coderodde.game.zerosum.impl;

import com.github.coderodde.game.zerosum.PlayerType;
import com.github.coderodde.game.zerosum.GameState;
import com.github.coderodde.game.zerosum.HeuristicFunction;
import com.github.coderodde.game.zerosum.SearchAlgorithm;
import java.util.ArrayDeque;
import java.util.Deque;

/**
 * This class implements the 
 * <a href="https://en.wikipedia.org/wiki/Alpha%E2%80%93beta_pruning">
 * Alpha-beta pruning</a> algorithm for making a move.
 * 
 * @param <S> the game state type.
 * 
 * @version 1.0.0 (Jun 5, 2024)
 * @since 1.0.0 (Jun 5, 2024)
 */
public final class AlphaBetaPruningSearchEngine<S extends GameState<S>>
        implements SearchAlgorithm<S> {

    private double bestValue;
    private S bestMoveState;
    private final Deque<S> bestStatePath = new ArrayDeque<>();
    private final HeuristicFunction<S> heuristicFunction;
    private final double minimizingPlayerVictoryScore;
    private final double maximizingPlayerVictoryScore;
    
    public AlphaBetaPruningSearchEngine(
            final HeuristicFunction<S> heuristicFunction,
            final double minimizingPlayerVictoryScore,
            final double maximizingPlayerVictoryScore) {
        
        this.heuristicFunction = heuristicFunction;
        this.minimizingPlayerVictoryScore = minimizingPlayerVictoryScore;
        this.maximizingPlayerVictoryScore = maximizingPlayerVictoryScore;
    }
    
    @Override
    public S search(final S root, final int depth) {
        bestValue = Double.NEGATIVE_INFINITY;
        bestMoveState = null;
        bestStatePath.clear();
        
        alphaBetaImpl(root, 
                      depth,
                      Double.NEGATIVE_INFINITY, 
                      Double.POSITIVE_INFINITY,
                      PlayerType.MAXIMIZING_PLAYER);
        
        return bestMoveState;
    }
    
    private double alphaBetaImpl(final S state,
                                 final int depth, 
                                 double alpha,
                                 double beta,
                                 final PlayerType playerType) {
        
        if (state.isWinningFor(PlayerType.MINIMIZING_PLAYER) &&
            state.isWinningFor(PlayerType.MAXIMIZING_PLAYER) &&
            state.isTie()) {
            
            bestMoveState = null;
            return Double.NaN;
        } 
        
        if (depth == 0) {
            
            final double score = heuristicFunction.evaluate(state);
            
            if (bestValue < score) {
                bestValue = score;
                bestMoveState = bestStatePath.getFirst();
            }
            
            return score;
        }
        
        if (playerType == PlayerType.MAXIMIZING_PLAYER) {
            double value = Double.NEGATIVE_INFINITY;
            
            for (final S child : state.expand(playerType)) {
                bestStatePath.addLast(child);

                value = Math.max(
                        value,
                        alphaBetaImpl(
                                child, 
                                depth - 1,
                                alpha,
                                beta,
                                PlayerType.MINIMIZING_PLAYER));
                
                bestStatePath.removeLast();
                
                if (value > beta) {
                    break;
                }
                
                alpha = Math.max(alpha, value);
            }
                
            return value;
        } else {
            double value = Double.POSITIVE_INFINITY;
            
            for (final S child : state.expand(playerType)) {
                bestStatePath.addLast(child);
                
                value = Math.min(
                        value, 
                        alphaBetaImpl(
                                child, 
                                depth - 1, 
                                alpha, 
                                beta, 
                                PlayerType.MAXIMIZING_PLAYER));
                
                bestStatePath.removeLast();
                
                if (value < alpha) {
                    break;
                }
                
                beta = Math.min(beta, value);
            }
            
            return value;
        }
    }   
}
