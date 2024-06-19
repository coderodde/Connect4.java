package com.github.coderodde.game.zerosum.impl;

import com.github.coderodde.game.connect4.ConnectFourBoard;
import com.github.coderodde.game.zerosum.AbstractConnectFourSearchEngine;
import com.github.coderodde.game.zerosum.HeuristicFunction;
import com.github.coderodde.game.zerosum.PlayerType;

/**
 *
 * @version 1.0.0 (Jun 18, 2024)
 * @since 1.0.0 (Jun 18, 2024)
 */
public final class ConnectFourPrincipalVariationSearchEngine 
        extends AbstractConnectFourSearchEngine {

    private final HeuristicFunction<ConnectFourBoard> heuristicFunction;
    
    public ConnectFourPrincipalVariationSearchEngine(
            final HeuristicFunction<ConnectFourBoard> heuristicFunction) {
        
        this.heuristicFunction = heuristicFunction;
    }
    
    @Override
    public ConnectFourBoard search(final ConnectFourBoard root, 
                                   final int depth, 
                                   final PlayerType playerType) {
        return pvsRoot(root,
                       depth,
                       Double.NEGATIVE_INFINITY,
                       Double.POSITIVE_INFINITY,
                       playerType == PlayerType.MINIMIZING_PLAYER ?
                                  -1 :
                                  +1);
    }
    
    private ConnectFourBoard pvsRoot(final ConnectFourBoard root,
                                     final int depth,
                                     double alpha,
                                     double beta,
                                     final int color) {
        
        double value = Double.NEGATIVE_INFINITY;   
        ConnectFourBoard bestMoveState = null;
        
        for (final int x : PLIES) {
            if (!root.makePly(
                    x,
                    color == 1 ?
                            PlayerType.MAXIMIZING_PLAYER : 
                            PlayerType.MINIMIZING_PLAYER)) {
                
                continue;
            }
            
            final double score = -pvs(root, 
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
    
    private double pvs(final ConnectFourBoard root,
                       final int depth,
                       double alpha,
                       double beta,
                       final int color) {
        
        if (depth == 0 || root.isTerminal()) {
            return color * heuristicFunction.evaluate(root, depth);
        }
        
        boolean isFirstState = true;
        
        for (final int x : PLIES) {
            if (!root.makePly(
                    x, 
                    color == 1 ? 
                            PlayerType.MAXIMIZING_PLAYER : 
                            PlayerType.MINIMIZING_PLAYER)) {
                continue;
            }
            
            double score;
            
            if (isFirstState) {
                isFirstState = false;
                    
                score = -pvs(root,
                             depth - 1,
                             -beta,
                             -alpha,
                             -color);
            } else {
                score = -pvs(root,
                             depth -1,
                             -alpha - 1, 
                             -alpha,
                             -color);
                
                if (alpha < score && score < beta) {
                    score = -pvs(root, 
                                 depth - 1, 
                                 -beta, 
                                 -alpha, 
                                 -color);
                }
            }
            
            root.unmakePly(x);
            
            alpha = Math.max(alpha, score);
            
            if (alpha >= beta) {
                break;
            }
        }
        
        return alpha;
    }
}
