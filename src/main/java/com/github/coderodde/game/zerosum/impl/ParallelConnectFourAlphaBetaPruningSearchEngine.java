package com.github.coderodde.game.zerosum.impl;

import com.github.coderodde.game.connect4.ConnectFourBoard;
import com.github.coderodde.game.connect4.ConnectFourHeuristicFunction;
import com.github.coderodde.game.zerosum.HeuristicFunction;
import com.github.coderodde.game.zerosum.PlayerType;
import com.github.coderodde.game.zerosum.SearchEngine;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @version 1.0.0 (Jun 7, 2024) 
 * @since 1.0.0 (Jun 7, 2024)
 */
public final class ParallelConnectFourAlphaBetaPruningSearchEngine 
implements SearchEngine<ConnectFourBoard> {

    private static final int MINIMUM_SEED_DEPTH = 2;
    
    private final HeuristicFunction<ConnectFourBoard> heuristicFunction;
    private final int seedDepth;
    
    public ParallelConnectFourAlphaBetaPruningSearchEngine(
            final ConnectFourHeuristicFunction heuristicFunction,
            final int seedDepth) {
        
        this.heuristicFunction = heuristicFunction;
        this.seedDepth = seedDepth;
    }
    
    @Override
    public ConnectFourBoard search(ConnectFourBoard root, int depth) {
        if (depth < MINIMUM_SEED_DEPTH) {
            // If too shallow, delegate to single-threaded AI:
            return new ConnectFourAlphaBetaPruningSearchEngine(
                    heuristicFunction).search(root, depth);
        }
        
        final List<ConnectFourBoard> seedStates = getSeedStates(root, 
                                                                seedDepth);
        
        throw new UnsupportedOperationException();
    }
    
    private static List<ConnectFourBoard> getSeedStates(
            final ConnectFourBoard root,
            final int seedDepth) {
        
        List<ConnectFourBoard> levelA = new ArrayList<>();
        List<ConnectFourBoard> levelB = new ArrayList<>();
        PlayerType playerType = PlayerType.MAXIMIZING_PLAYER;
        
        levelA.add(root);
        
        for (int i = 0; i < seedDepth; i++) {
            for (final ConnectFourBoard cfb : levelA) {
                levelB.addAll(cfb.expand(playerType));
            }
            
            levelA.clear();
            levelA.addAll(levelB);
            levelB.clear();
            playerType = playerType.flip();
        }
        
        return levelA;
    }
}
