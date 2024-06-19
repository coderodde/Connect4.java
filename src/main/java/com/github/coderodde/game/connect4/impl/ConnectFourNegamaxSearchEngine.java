package com.github.coderodde.game.connect4.impl;

import com.github.coderodde.game.connect4.ConnectFourBoard;
import com.github.coderodde.game.zerosum.AbstractConnectFourSearchEngine;
import com.github.coderodde.game.zerosum.HeuristicFunction;
import com.github.coderodde.game.zerosum.PlayerType;

/**
 * This class implements a Negamax algorithm with alpha-beta pruning for playing
 * Connect Four.
 * 
 * @version 1.0.0 (Jun 16, 2024)
 * @since 1.0.0 (Jun 16, 2024)
 */
public final class ConnectFourNegamaxSearchEngine
        extends AbstractConnectFourSearchEngine {

    private final HeuristicFunction<ConnectFourBoard> heuristicFunction;
    
    public ConnectFourNegamaxSearchEngine(
            final HeuristicFunction<ConnectFourBoard> heuristicFunction) {
        
        this.heuristicFunction = heuristicFunction;
    }
    
    @Override
    public ConnectFourBoard search(final ConnectFourBoard root, 
                                   final int depth, 
                                   final PlayerType playerType) {
        
        if (playerType == PlayerType.MINIMIZING_PLAYER) {
            return negamaxRoot(root, 
                               depth,
                               Double.NEGATIVE_INFINITY,
                               Double.POSITIVE_INFINITY,
                               -1);
        } else {
            return negamaxRoot(root,
                               depth,
                               Double.NEGATIVE_INFINITY,
                               Double.POSITIVE_INFINITY,
                               +1);
        }
    }
    
    private ConnectFourBoard negamaxRoot(final ConnectFourBoard root,
                                         final int depth,
                                         double alpha,
                                         double beta,
                                         final int color) {
        
        double value = Double.NEGATIVE_INFINITY;
        ConnectFourBoard bestMoveState = null;
        
        for (int x : PLIES) {
            if (!root.makePly(
                    x, 
                    color == 1 ? 
                            PlayerType.MAXIMIZING_PLAYER : 
                            PlayerType.MINIMIZING_PLAYER)) {
                
                continue;
            }
            
            final double score = 
                    -negamax(root,
                             depth - 1,
                             -beta,
                             -alpha,
                             -color);
            
            if (color == +1) {
                if (value < score) {
                    value = score;
                    bestMoveState = new ConnectFourBoard(root);
                }
            } else {
                if (value > score) {
                    value = score;
                    bestMoveState = new ConnectFourBoard(root);
                }
            }
            
            root.unmakePly(x);
            
            alpha = Math.max(alpha, value);
            
            if (alpha >= beta) {
                break;
            }
        }
        
        return bestMoveState;
    }
    
    private double negamax(final ConnectFourBoard root, 
                           final int depth,
                           double alpha,
                           double beta,
                           final int color) {
        
        if (depth == 0 || root.isTerminal()) {
            return color * heuristicFunction.evaluate(root, depth);
        }
        
        double value = Double.NEGATIVE_INFINITY;
        
        for (int x : PLIES) {
            if (!root.makePly(
                    x, 
                    color == 1 ? 
                            PlayerType.MAXIMIZING_PLAYER : 
                            PlayerType.MINIMIZING_PLAYER)) {
                
                continue;
            }
            
            value = Math.max(
                        value,
                        -negamax(
                                root, 
                                depth - 1, 
                                -beta, 
                                -alpha, 
                                -color));
            
            root.unmakePly(x);
            
            alpha = Math.max(alpha, value);
            
            if (alpha >= beta) {
                break;
            }
        }
        
        return value;
    }
}