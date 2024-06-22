package com.github.coderodde.game.connect4.impl;

import com.github.coderodde.game.connect4.ConnectFourBoard;
import com.github.coderodde.game.zerosum.AbstractConnectFourSearchEngine;
import com.github.coderodde.game.zerosum.PlayerType;
import com.github.coderodde.game.zerosum.HeuristicFunction;

/**
 * This class implements the 
 * <a href="https://en.wikipedia.org/wiki/Alpha%E2%80%93beta_pruning">
 * Alpha-beta pruning</a> algorithm for making a move.
 * 
 * @version 1.0.0 (Jun 5, 2024)
 * @since 1.0.0 (Jun 5, 2024)
 */
public final class ConnectFourAlphaBetaPruningSearchEngine
        extends AbstractConnectFourSearchEngine {
    
    private ConnectFourBoard bestMoveState;
    private final HeuristicFunction<ConnectFourBoard> heuristicFunction;
    
    public ConnectFourAlphaBetaPruningSearchEngine(
            final HeuristicFunction<ConnectFourBoard> heuristicFunction) {
        this.heuristicFunction = heuristicFunction;
    }
    
    @Override
    public ConnectFourBoard search(final ConnectFourBoard root, 
                                   final int depth) {
        return search(root,
                      depth,
                      PlayerType.MAXIMIZING_PLAYER);
    }   

    @Override
    public ConnectFourBoard search(final ConnectFourBoard root,
                                   int depth, 
                                   final PlayerType playerType) {
        bestMoveState = null;
        
        alphaBetaRootImpl(root, 
                          depth,
                          playerType);
        
        return bestMoveState;
    }
    
    private void alphaBetaRootImpl(final ConnectFourBoard root, 
                                   final int depth,
                                   final PlayerType playerType) {
        
        if (playerType == PlayerType.MAXIMIZING_PLAYER) {
            
            // Try to maximize the value:
            int alpha = Integer.MIN_VALUE;
            int value = Integer.MIN_VALUE;
            int tentativeValue = Integer.MIN_VALUE;
            
            for (final int x : PLIES) {
                if (!root.makePly(x, PlayerType.MAXIMIZING_PLAYER)) {
                    continue;
                }

                value = Math.max(value,
                                 alphaBetaImpl(root,
                                               depth - 1,
                                               alpha,
                                               Double.POSITIVE_INFINITY,
                                               PlayerType.MINIMIZING_PLAYER));
                
                if (tentativeValue < value) {
                    tentativeValue = value;
                    bestMoveState = new ConnectFourBoard(root);
                }

                root.unmakePly(x);  
                
                alpha = Math.max(alpha, value);
            }
        } else {
            
            int beta = Integer.MAX_VALUE;
            int value = Integer.MAX_VALUE;
            int tentativeValue = Integer.MAX_VALUE;
            
            for (final int x : PLIES) {
                if (!root.makePly(x, PlayerType.MINIMIZING_PLAYER)) {
                    continue;
                }

                value = Math.min(value,
                                 alphaBetaImpl(root,
                                               depth - 1,
                                               Double.NEGATIVE_INFINITY,
                                               beta,
                                               PlayerType.MAXIMIZING_PLAYER));

                if (tentativeValue > value) {
                    tentativeValue = value;
                    bestMoveState = new ConnectFourBoard(root);
                }

                root.unmakePly(x);
                
                beta = Math.min(beta, value);
            }
        }
    }
    
    private int alphaBetaImpl(final ConnectFourBoard state,
                              final int depth, 
                              double alpha,
                              double beta,
                              final PlayerType playerType) {
        
        if (depth == 0 || state.isTerminal()) {
            return heuristicFunction.evaluate(state, depth);
        }
        
        if (playerType == PlayerType.MAXIMIZING_PLAYER) {
            int value = Integer.MIN_VALUE;
            
            for (int x : PLIES) {
                if (!state.makePly(x, PlayerType.MAXIMIZING_PLAYER)) {
                    continue;
                }
                
                value = Math.max(value, 
                                 alphaBetaImpl(state,
                                               depth - 1,
                                               alpha,
                                               beta,
                                               PlayerType.MINIMIZING_PLAYER));
                
                state.unmakePly(x);
                
                if (value > beta) {
                    break;
                }
                
                alpha = Math.max(alpha, value);
            }   
            
            return value;
        } else {
            int value = Integer.MAX_VALUE;
            
            for (int x : PLIES) {
                if (!state.makePly(x, PlayerType.MINIMIZING_PLAYER)) {
                    continue;
                }
                
                value = Math.min(value,
                                 alphaBetaImpl(state,
                                               depth - 1,
                                               alpha,
                                               beta,
                                               PlayerType.MAXIMIZING_PLAYER));
                
                state.unmakePly(x);
                
                if (value < alpha) {
                    break;
                }
                
                beta = Math.min(beta, value);
            }
            
            return value;
        }          
    }
}
