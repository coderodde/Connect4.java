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
    private static final int DEFAULT_SEED_DEPTH = 2;
    
    private final HeuristicFunction<ConnectFourBoard> heuristicFunction;
    private int seedDepth;
    
    /**
     * Constructs this search engine.
     * 
     * @param heuristicFunction the heuristic function used to score the states.
     * @param seedDepth         the depth of the seed states.
     */
    public ParallelConnectFourAlphaBetaPruningSearchEngine(
            final HeuristicFunction<ConnectFourBoard> heuristicFunction,
            final int seedDepth) {
        
        this.heuristicFunction = heuristicFunction;
        this.seedDepth = seedDepth;
    }
    
    /**
     * Constructs this search engine.
     * 
     * @param heuristicFunction the heuristic function used to score the states.
     */
    public ParallelConnectFourAlphaBetaPruningSearchEngine(
            final ConnectFourHeuristicFunction heuristicFunction) {
        
        this(heuristicFunction, DEFAULT_SEED_DEPTH);
    }
    
    /**
     * Performs the actual search for the next move state.
     * 
     * @param root  the root state of the search.
     * @param depth the depth of the search.
     * 
     * @return next move state.
     */
    @Override
    public ConnectFourBoard search(ConnectFourBoard root, int depth) {
        if (depth < MINIMUM_SEED_DEPTH) {
            // If too shallow, delegate to single-threaded AI:
            return new ConnectFourAlphaBetaPruningSearchEngine(
                    heuristicFunction).search(root,
                                              depth);
        }
        
        // Obtains the list of seed states. May lower the 'seedDepth':
        final List<ConnectFourBoard> seedStates = getSeedStates(root);
        
        if (seedDepth <= MINIMUM_SEED_DEPTH) {
            // Once here, the search is effectively shallow. This means that 
            // reaching a full board requires less plies than 'depth':
            return new ConnectFourAlphaBetaPruningSearchEngine(
                    heuristicFunction).search(root, 
                                              seedDepth);
        }
        
        // Randomly shuffle the seed states. This is a trivial load balancing:
        Collections.shuffle(seedStates);
        
        // Get the list of thread workloads:
        final List<List<ConnectFourBoard>> threadLoads = 
                bucketizeSeedStates(seedStates, 
                                    Runtime.getRuntime().availableProcessors());
        
        // Create the list of search threads:
        final List<SearchThread> searchThreadList = 
                new ArrayList<>(threadLoads.size());
        
        // Populate the search threads:
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
        
        // Wait for all the threads to complete:
        for (final SearchThread searchThread : searchThreadList) {
            try {
                searchThread.join();
            } catch (final InterruptedException ex) {
                
            }
        }
        
        // Compute the global seed state score map:
        final Map<ConnectFourBoard, Double> globalScoreMap = 
                getGlobalScoreMap(searchThreadList);
        
        // Construct the seed state heuristic function:
        final SeedStateHeuristicFunction seedHeuristicFunction = 
                new SeedStateHeuristicFunction(globalScoreMap);
        
        // Just compute above the seed states:
        return alphaBetaImplRoot(root, 
                                 seedHeuristicFunction,
                                 seedDepth);
    }
    
    /**
     * The 
     * @param root
     * @param heuristicFunction
     * @param seedDepth
     * @return 
     */
    private static ConnectFourBoard 
        alphaBetaImplRoot(
                final ConnectFourBoard root,
                final HeuristicFunction<ConnectFourBoard> heuristicFunction,
                final int seedDepth) {
        
        double tentativeValue = Double.NEGATIVE_INFINITY;
        
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
        }
        
        return bestMoveState;
    }
    
    /**
     * Combines all the score maps into one global map for searching above the
     * seed states.
     * 
     * @param searchThreadList the list of search threads.
     * 
     * @return the combined global score map.
     */
    private static Map<ConnectFourBoard, Double>
         getGlobalScoreMap(final List<SearchThread> searchThreadList) {
        
        final Map<ConnectFourBoard, Double> globalScoreMap = new HashMap<>();
        
        for (final SearchThread searchThread : searchThreadList) {
            globalScoreMap.putAll(searchThread.getScoreMap());
        }
        
        return globalScoreMap;
    }
    
    /**
     * Splits the list of seed states into list of lists of seed states.
     * 
     * @param seedStates  the list of all the seed states.
     * @param threadCount the number of threads to assume.
     * 
     * @return list of seed state buckets. One for each thread.
     */
    private static List<List<ConnectFourBoard>> 
        bucketizeSeedStates(final List<ConnectFourBoard> seedStates,
                            final int threadCount) {
            
        // Construct a list with capacity sufficient to accommodate all the
        // buckets:
        final List<List<ConnectFourBoard>> threadBuckets = 
                new ArrayList<>(threadCount);
        
        // The basic number of seed states per thread bucket:
        final int basicNumberOfSeedsPerBucket = seedStates.size() / threadCount;
        
        // The seed state index:
        int index = 0;
        
        for (int i = 0; i < threadCount; i++) {
            // Construct the new bucket. +1 in order to add additional possible
            // seed state in case 'threadCount' does not divide
            // 'seedStates.size()':
            final List<ConnectFourBoard> bucket = 
                    new ArrayList<>(basicNumberOfSeedsPerBucket + 1);
            
            // Load the current bucket:
            for (int j = 0; j < basicNumberOfSeedsPerBucket; j++, index++) {
                bucket.add(seedStates.get(index));
            }
            
            // Add the bucket to the bucket list:
            threadBuckets.add(bucket);
        }
        
        // How many threads should receive one more additional seed state?
        final int remainingStates = seedStates.size() % threadCount;
        
        for (int i = 0; i < remainingStates; i++, index++) {
            threadBuckets.get(i).add(seedStates.get(index));
        }
        
        return threadBuckets;
    }
    
    /**
     * Computes the list of seed states. The idea is that each search thread 
     * starts its search from a seed state.
     * 
     * @param root the actual root state of the search.
     * 
     * @return the list of seed states.
     */
    private List<ConnectFourBoard> getSeedStates(final ConnectFourBoard root) {
        
        List<ConnectFourBoard> levelA = new ArrayList<>();
        List<ConnectFourBoard> levelB = new ArrayList<>();
        PlayerType playerType = PlayerType.MAXIMIZING_PLAYER;
        
        levelA.add(root);
        
        int effectiveSeedDepth = 0;
        
        for (int i = 0; i < seedDepth; i++) {
            // Load next state layer:
            for (final ConnectFourBoard cfb : levelA) {
                levelB.addAll(cfb.expand(playerType));
            }
            
            if (!levelB.isEmpty()) {
                effectiveSeedDepth++;
            } else {
                // Once here, the root state is missing very few plies:
                seedDepth = effectiveSeedDepth;
                return levelA;
            }
            
            levelA.clear();
            levelA.addAll(levelB);
            levelB.clear();
            
            // Assume the opposite player:
            playerType = playerType.flip();
        }
        
        return levelA;
    }
    
    /**
     * This static inner class implements the heuristic function for the seed 
     * states.
     */
    private static final class SeedStateHeuristicFunction
            implements HeuristicFunction<ConnectFourBoard> {

        private final Map<ConnectFourBoard, Double> scoreMap;
        
        SeedStateHeuristicFunction(
                final Map<ConnectFourBoard, Double> scoreMap) {
            
            this.scoreMap = scoreMap;
        }
        
        @Override
        public double evaluate(ConnectFourBoard state, int depth) {
            return scoreMap.get(state);
        }
    }
    
    /**
     * This static inner class implements the actual search routine starting 
     * from seed states.
     */
    private static final class SearchThread extends Thread {
        
        /**
         * The list of seed states to process.
         */
        private final List<ConnectFourBoard> workload;
        
        /**
         * This map maps each seed states to its score after computation.
         */
        private final Map<ConnectFourBoard, Double> scoreMap;
        
        /**
         * The heuristic function for evaluating intermediate states.
         */
        private final HeuristicFunction<ConnectFourBoard> heuristicFunction;
        
        /**
         * The beginning player type.
         */
        private final PlayerType rootPlayerType;
        
        /**
         * The (maximal) search depth.
         */
        private final int depth;
        
        /**
         * Constructs this search thread.
         * 
         * @param workload          the workload list of seed states.
         * @param heuristicFunction the heuristic function.
         * @param rootPlayerType    the beginning player type.
         * @param depth             the maximal search depth.
         */
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
        
        /**
         * Returns computed score map mapping each seed state to its score.
         * 
         * @return the score map.
         */
        Map<ConnectFourBoard, Double> getScoreMap() {
            return scoreMap;
        }
        
        /**
         * Runs the search in this thread.
         */
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
