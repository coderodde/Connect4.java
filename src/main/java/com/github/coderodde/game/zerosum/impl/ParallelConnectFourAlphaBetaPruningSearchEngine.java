package com.github.coderodde.game.zerosum.impl;

import com.github.coderodde.game.connect4.ConnectFourBoard;
import static com.github.coderodde.game.connect4.ConnectFourBoard.COLUMNS;
import com.github.coderodde.game.connect4.ConnectFourHeuristicFunction;
import com.github.coderodde.game.zerosum.HeuristicFunction;
import com.github.coderodde.game.zerosum.PlayerType;
import com.github.coderodde.game.zerosum.SearchEngine;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * @version 1.0.0 (Jun 7, 2024) 
 * @since 1.0.0 (Jun 7, 2024)
 */
public final class ParallelConnectFourAlphaBetaPruningSearchEngine 
implements SearchEngine<ConnectFourBoard> {

    private static final int MINIMUM_SEED_DEPTH = 2;
    private static final int DEFAULT_SEED_DEPTH = 3;
    
    private final HeuristicFunction<ConnectFourBoard> heuristicFunction;
    private final int seedDepth;
    
    public ParallelConnectFourAlphaBetaPruningSearchEngine(
            final HeuristicFunction<ConnectFourBoard> heuristicFunction,
            final int seedDepth) {
        
        this.heuristicFunction = heuristicFunction;
        this.seedDepth = seedDepth;
    }
    
    public ParallelConnectFourAlphaBetaPruningSearchEngine(
            final ConnectFourHeuristicFunction heuristicFunction) {
        
        this(heuristicFunction, DEFAULT_SEED_DEPTH);
    }
    
    @Override
    public ConnectFourBoard search(ConnectFourBoard root, int depth) {
        if (depth < MINIMUM_SEED_DEPTH) {
            // If too shallow, delegate to single-threaded AI:
            return new ConnectFourAlphaBetaPruningSearchEngine(
                    heuristicFunction).search(root,
                                              depth);
        }
        
        final List<ConnectFourBoard> seedStates = getSeedStates(root, 
                                                                seedDepth);
        
        Collections.shuffle(seedStates);
        
        final List<List<ConnectFourBoard>> threadLoads = 
                bucketizeSeedStates(seedStates, 
                                    Runtime.getRuntime().availableProcessors());
        
        final List<SearchThread> searchThreadList = 
                new ArrayList<>(threadLoads.size());
        
        for (final List<ConnectFourBoard> threadLoad : threadLoads) {
            final SearchThread searchThread =
                    new SearchThread(
                            threadLoad,
                            heuristicFunction,
                            seedDepth % 2 == 0 ? PlayerType.MAXIMIZING_PLAYER :
                                                 PlayerType.MINIMIZING_PLAYER,
                            depth - seedDepth);
            
            searchThread.start();
            
            searchThreadList.add(searchThread);
        }
        
        for (final SearchThread searchThread : searchThreadList) {
            try {
                searchThread.join();
            } catch (final InterruptedException ex) {
                
            }
        }
        
        final Map<ConnectFourBoard, Double> globalScoreMap = 
                getGlobalScoreMap(searchThreadList);
        
        final SeedHeuristicFunction seedHeuristicFunction = 
                new SeedHeuristicFunction(globalScoreMap);
        
        return alphaBetaImplRoot(root, 
                                 seedHeuristicFunction,
                                 seedDepth);
    }
    
    private static ConnectFourBoard 
        alphaBetaImplRoot(
                final ConnectFourBoard root,
                final HeuristicFunction<ConnectFourBoard> heuristicFunction,
                final int seedDepth) {
        
        double tentativeValue = Double.NEGATIVE_INFINITY;
        double alpha = Double.NEGATIVE_INFINITY;
        
        ConnectFourBoard bestMoveState = null;
        
        for (int x = 0; x < COLUMNS; x++) {
            if (!root.makePly(x, PlayerType.MAXIMIZING_PLAYER)) {
                continue;
            }
            
            double value = alphaBetaImpl(root,
                                         seedDepth - 1,
                                         Double.NEGATIVE_INFINITY,
                                         Double.POSITIVE_INFINITY,
                                         PlayerType.MINIMIZING_PLAYER,
                                         heuristicFunction);
            
            if (tentativeValue < value) {
                tentativeValue = value;
                bestMoveState = new ConnectFourBoard(root);
            }
            
            root.unmakePly(x);
            alpha = Math.max(alpha, value);
        }
        
        return bestMoveState;
    }
    
    private static Map<ConnectFourBoard, Double>
         getGlobalScoreMap(final List<SearchThread> searchThreadList) {
        
        final Map<ConnectFourBoard, Double> globalScoreMap = new HashMap<>();
        
        for (final SearchThread searchThread : searchThreadList) {
            globalScoreMap.putAll(searchThread.getScoreMap());
        }
        
        return globalScoreMap;
    }
    
    private static List<List<ConnectFourBoard>> 
        bucketizeSeedStates(final List<ConnectFourBoard> seedStates,
                            final int threadCount) {
            
        final List<List<ConnectFourBoard>> threadBuckets = 
                new ArrayList<>(threadCount);
        
        final int basicNumberOfSeedsPerBucket = seedStates.size() / threadCount;
        
        int index = 0;
        
        for (int i = 0; i < threadCount; i++) {
            
            final List<ConnectFourBoard> bucket = 
                    new ArrayList<>(basicNumberOfSeedsPerBucket + 1);
            
            for (int j = 0; j < basicNumberOfSeedsPerBucket; j++, index++) {
                bucket.add(seedStates.get(index));
            }
            
            threadBuckets.add(bucket);
        }
        
        final int remainingStates = seedStates.size() % threadCount;
        
        for (int i = 0; i < remainingStates; i++, index++) {
            threadBuckets.get(i).add(seedStates.get(index));
        }
        
        return threadBuckets;
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
    
    private static final class SeedHeuristicFunction
            implements HeuristicFunction<ConnectFourBoard> {

        private final Map<ConnectFourBoard, Double> scoreMap;
        
        SeedHeuristicFunction(final Map<ConnectFourBoard, Double> scoreMap) {
            this.scoreMap = scoreMap;
        }
        
        @Override
        public double evaluate(ConnectFourBoard state, int depth) {
            return scoreMap.get(state);
        }
    }
    
    private static final class SearchThread extends Thread {
        
        private final List<ConnectFourBoard> workload;
        private final Map<ConnectFourBoard, Double> scoreMap;
        private final HeuristicFunction<ConnectFourBoard> heuristicFunction;
        private final PlayerType rootPlayerType;
        private final int depth;
        
        SearchThread(final List<ConnectFourBoard> workload,
                     final HeuristicFunction<ConnectFourBoard> 
                           heuristicFunction,
                     final PlayerType rootPlayerType,
                     final int depth) {
            
            this.workload = workload;
            this.scoreMap = new HashMap<>(workload.size());
            this.heuristicFunction = heuristicFunction;
            this.rootPlayerType = rootPlayerType;
            this.depth = depth;
        }
        
        Map<ConnectFourBoard, Double> getScoreMap() {
            return scoreMap;
        }
        
        @Override
        public void run() {
            for (final ConnectFourBoard root : workload) {
                final double score = 
                        alphaBetaImpl(
                                root,
                                depth, 
                                Double.NEGATIVE_INFINITY,
                                Double.POSITIVE_INFINITY,
                                rootPlayerType, 
                                heuristicFunction);
                
                scoreMap.put(root, score);
            }
        }
    }
    
    /**
     * Implements the actual game search via Alpha-beta pruning.
     * 
     * @param root              the root node of the search subtree.
     * @param depth             the depth to search the game tree.
     * @param alpha             the alpha value.
     * @param beta              the beta value.
     * @param playerType        the player type for the state {@code root}.
     * @param heuristicFunction the heuristic function.
     * 
     * @return the value of the state {@code root}.
     */
    private static double
         alphaBetaImpl(
                 final ConnectFourBoard root,
                 final int depth, 
                 double alpha,
                 double beta,
                 final PlayerType playerType,
                 final HeuristicFunction<ConnectFourBoard> heuristicFunction) {

        if (depth == 0 || root.isTerminal()) {
            return heuristicFunction.evaluate(root, depth);
        }
        
        if (playerType == PlayerType.MAXIMIZING_PLAYER) {
            double value = Double.NEGATIVE_INFINITY;
            
            for (int x = 0; x < COLUMNS; x++) {
                if (!root.makePly(x, PlayerType.MAXIMIZING_PLAYER)) {
                    continue;
                }
                
                value = Math.max(value, 
                                 alphaBetaImpl(root,
                                               depth - 1,
                                               alpha,
                                               beta,
                                               PlayerType.MINIMIZING_PLAYER,
                                               heuristicFunction));
                
                root.unmakePly(x);
                
                if (value > beta) {
                    break;
                }
                
                alpha = Math.max(alpha, value);
            }   
            
            return value;
        } else {
            double value = Double.POSITIVE_INFINITY;
            
            for (int x = 0; x < COLUMNS; x++) {
                if (!root.makePly(x, PlayerType.MINIMIZING_PLAYER)) {
                    continue;
                }
                
                value = Math.min(value,
                                 alphaBetaImpl(root,
                                               depth - 1,
                                               alpha,
                                               beta,
                                               PlayerType.MAXIMIZING_PLAYER,
                                               heuristicFunction));
                
                root.unmakePly(x);
                
                if (value < alpha) {
                    break;
                }
                
                beta = Math.min(beta, value);
            }
            
            return value;
        }          
    }
}
