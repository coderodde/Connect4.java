package com.github.coderodde.game.zerosum.impl;

import com.github.coderodde.game.connect4.ConnectFourBoard;
import static com.github.coderodde.game.connect4.ConnectFourBoard.COLUMNS;
import com.github.coderodde.game.connect4.SearchProgress;
import com.github.coderodde.game.zerosum.PlayerType;
import com.github.coderodde.game.zerosum.HeuristicFunction;
import com.github.coderodde.game.zerosum.SearchEngine;

/**
 * This class implements the 
 * <a href="https://en.wikipedia.org/wiki/Alpha%E2%80%93beta_pruning">
 * Alpha-beta pruning</a> algorithm for making a move.
 * 
 * @version 1.0.0 (Jun 5, 2024)
 * @since 1.0.0 (Jun 5, 2024)
 */
public final class ConnectFourAlphaBetaPruningSearchEngine
        implements SearchEngine<ConnectFourBoard> {

    private ConnectFourBoard bestMoveState;
    private final HeuristicFunction<ConnectFourBoard> heuristicFunction;
    private SearchProgress searchProgress;
    
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
        
        if (searchProgress != null) {
            searchProgress.clear();
        }
        
        bestMoveState = null;
        
        alphaBetaRootImpl(root, 
                          depth,
                          playerType);
        
        return bestMoveState;
    }
    
    public void setSearchProgress(final SearchProgress searchProgress) {
        this.searchProgress = searchProgress;
    }
    
    private void alphaBetaRootImpl(final ConnectFourBoard root, 
                                   final int depth,
                                   final PlayerType playerType) {
        
        if (playerType == PlayerType.MAXIMIZING_PLAYER) {
            
            // Try to maximize the value:
            double alpha = Double.NEGATIVE_INFINITY;
            double value = Double.NEGATIVE_INFINITY;
            double tentativeValue = Double.NEGATIVE_INFINITY;
            
            for (int x = 0; x < COLUMNS; x++) {
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
            
            double beta = Double.POSITIVE_INFINITY;
            double value = Double.POSITIVE_INFINITY;
            double tentativeValue = Double.POSITIVE_INFINITY;
            
            for (int x = 0; x < COLUMNS; x++) {
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
    
    private double alphaBetaImpl(final ConnectFourBoard state,
                                 final int depth, 
                                 double alpha,
                                 double beta,
                                 final PlayerType playerType) {

        if (depth == 0 || state.isTerminal()) {
            if (searchProgress != null) {
                searchProgress.hit();
                searchProgress.setProgressBarValue();
            }
            
            return heuristicFunction.evaluate(state, depth);
        }
        
        if (playerType == PlayerType.MAXIMIZING_PLAYER) {
            double value = Double.NEGATIVE_INFINITY;
            
            for (int x = 0; x < COLUMNS; x++) {
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
            double value = Double.POSITIVE_INFINITY;
            
            for (int x = 0; x < COLUMNS; x++) {
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
