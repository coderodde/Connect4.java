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
                               MIN_INT,
                               MAX_INT,
                               -1);
        } else {
            return negamaxRoot(root,
                               depth,
                               MIN_INT,
                               MAX_INT,
                               +1);
        }
    }
    
    private ConnectFourBoard negamaxRoot(final ConnectFourBoard root,
                                         final int depth,
                                         int alpha,
                                         int beta,
                                         final int color) {
        
        int value = MIN_INT;
        ConnectFourBoard bestMoveState = null;
        
        for (int x : PLIES) {
            if (!root.makePly(
                    x, 
                    color == 1 ? 
                            PlayerType.MAXIMIZING_PLAYER : 
                            PlayerType.MINIMIZING_PLAYER)) {
                
                continue;
            }
            
            final int score = -negamax(root,
                                       depth - 1,
                                       -beta,
                                       -alpha,
                                       -color);
            
            if (value < score) {
                value = score;
                bestMoveState = new ConnectFourBoard(root);
            }
            
            root.unmakePly(x);
            
            alpha = Math.max(alpha, value);
            
            if (alpha >= beta) {
                break;
            }
        }
        
        return bestMoveState;
    }
    
    private int negamax(final ConnectFourBoard root, 
                        final int depth,
                        int alpha,
                        int beta,
                        final int color) {
        
        if (depth == 0 || root.isTerminal()) {
            return color * heuristicFunction.evaluate(root, depth);
        }
        
        int value = MIN_INT;
        
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