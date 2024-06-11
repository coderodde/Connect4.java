package com.github.coderodde.game.zerosum.impl;

import com.github.coderodde.game.zerosum.PlayerType;
import com.github.coderodde.game.zerosum.GameState;
import com.github.coderodde.game.zerosum.HeuristicFunction;
import com.github.coderodde.game.zerosum.SearchEngine;

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
        implements SearchEngine<S> {

    private S bestMoveState;
    private final HeuristicFunction<S> heuristicFunction;
    
    public AlphaBetaPruningSearchEngine(
            final HeuristicFunction<S> heuristicFunction) {
        this.heuristicFunction = heuristicFunction;
    }

    @Override
    public S search(S root, int depth, PlayerType playerType) {
        bestMoveState = null;
        
        alphaBetaRootImpl(root, 
                          depth,
                          playerType);
        
        return bestMoveState;
    }
    
    @Override
    public S search(final S root, final int depth) {
        bestMoveState = null;
        
        alphaBetaRootImpl(root, 
                          depth,
                          PlayerType.MAXIMIZING_PLAYER);
        
        return bestMoveState;
    }
    
    private void alphaBetaRootImpl(final S root, 
                                   final int depth,
                                   final PlayerType playerType) {
        if (playerType == PlayerType.MAXIMIZING_PLAYER) {
            
            // Try to maximize the value:
            double tentativeValue = Double.NEGATIVE_INFINITY;
            double alpha = Double.NEGATIVE_INFINITY;

            for (final S child : root.expand(PlayerType.MAXIMIZING_PLAYER)) {
                double value = alphaBetaImpl(child,
                                             depth - 1,
                                             alpha,
                                             Double.POSITIVE_INFINITY,
                                             PlayerType.MINIMIZING_PLAYER);

                if (tentativeValue < value) {
                    tentativeValue = value;
                    bestMoveState = child;
                }

                alpha = Math.max(alpha, value);
            }
        } else {
            
            double tentativeValue = Double.POSITIVE_INFINITY;
            double beta = Double.POSITIVE_INFINITY;
            
            for (final S child : root.expand(PlayerType.MAXIMIZING_PLAYER)) {
                double value = alphaBetaImpl(child,
                                             depth - 1,
                                             Double.NEGATIVE_INFINITY,
                                             beta,
                                             PlayerType.MAXIMIZING_PLAYER);

                if (tentativeValue > value) {
                    tentativeValue = value;
                    bestMoveState = child;
                }

                beta = Math.min(beta, value);
            }
        }
    }
    
    private double alphaBetaImpl(final S state,
                                 final int depth, 
                                 double alpha,
                                 double beta,
                                 final PlayerType playerType) {
        
        if (depth == 0 || state.isTerminal()) {
            return heuristicFunction.evaluate(state, depth);
        }
        
        if (playerType == PlayerType.MAXIMIZING_PLAYER) {
            double value = Double.NEGATIVE_INFINITY;
            
            for (final S child : state.expand(playerType)) {
                value = Math.max(
                        value,
                        alphaBetaImpl(
                                child, 
                                depth - 1,
                                alpha,
                                beta,
                                PlayerType.MINIMIZING_PLAYER));
                
                if (value > beta) {
                    break;
                }
                
                alpha = Math.max(alpha, value);
            }
                
            return value;
        } else {
            double value = Double.POSITIVE_INFINITY;
            
            for (final S child : state.expand(playerType)) {
                value = Math.min(
                        value, 
                        alphaBetaImpl(
                                child, 
                                depth - 1, 
                                alpha, 
                                beta, 
                                PlayerType.MAXIMIZING_PLAYER));
                
                if (value < alpha) {
                    break;
                }
                
                beta = Math.min(beta, value);
            }
            
            return value;
        }
    }   
}
