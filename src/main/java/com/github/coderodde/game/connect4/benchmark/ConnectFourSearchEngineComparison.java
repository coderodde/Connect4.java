package com.github.coderodde.game.connect4.benchmark;

import com.github.coderodde.game.connect4.ConnectFourBoard;
import com.github.coderodde.game.connect4.ConnectFourHeuristicFunction;
import com.github.coderodde.game.zerosum.PlayerType;
import com.github.coderodde.game.zerosum.SearchEngine;
import com.github.coderodde.game.zerosum.impl.AlphaBetaPruningSearchEngine;
import com.github.coderodde.game.zerosum.impl.ConnectFourAlphaBetaPruningSearchEngine;
import com.github.coderodde.game.zerosum.impl.ConnectFourNegamaxSearchEngine;
import com.github.coderodde.game.zerosum.impl.ConnectFourPrincipalVariationSearchEngine;
import com.github.coderodde.game.zerosum.impl.ParallelConnectFourAlphaBetaPruningSearchEngine;

public class ConnectFourSearchEngineComparison {
    
    private static final int DEPTH = 10;
    private static final int SEED_DEPTH = 2;
    private static final int ALPHA_BETA_PRUNING_ENGINE_DEPTH = 9;
    private static final int NEGAMAX_ENGINE_DEPTH = 9;

    public static void main(String[] args) {
        final ConnectFourBoard b = new ConnectFourBoard();
        ConnectFourBoard r;
        ConnectFourHeuristicFunction heuristicFunction = 
                new ConnectFourHeuristicFunction();
        
        long startTime = System.currentTimeMillis();
        
        r = new AlphaBetaPruningSearchEngine<>(heuristicFunction)
                .search(b, DEPTH);
        
        long endTime = System.currentTimeMillis();
        
        System.out.printf("AlphaBetaPruningSearchEngine in %d milliseconds.\n",
                          endTime - startTime);
        
        System.out.println(r);
        
        startTime = System.currentTimeMillis();
        
        r = new ConnectFourAlphaBetaPruningSearchEngine(heuristicFunction)
                .search(b, DEPTH);
        
        endTime = System.currentTimeMillis();
        
        System.out.printf(
                "ConnectFourAlphaBetaPruningSearchEngine in %d milliseconds.\n",
                endTime - startTime);
        
        System.out.println(r);
        
        startTime = System.currentTimeMillis();
        
        r = new ParallelConnectFourAlphaBetaPruningSearchEngine(
                heuristicFunction, SEED_DEPTH)
                .search(b, DEPTH);
        
        endTime = System.currentTimeMillis();
        
        System.out.printf(
            """
            ParallelConnectFourAlphaBetaPruningSearchEngine in %d milliseconds.
            """,
            endTime - startTime);
        
        System.out.println(r);
        
        startTime = System.currentTimeMillis();
        
        r = new ConnectFourPrincipalVariationSearchEngine(heuristicFunction)
                .search(b, DEPTH);
        
        endTime = System.currentTimeMillis();
        
        System.out.printf(
            """
            ConnectFourPrincipalVariationSearchEngine in %d milliseconds.
            """,
            endTime - startTime);
        
        System.out.println(r);
        
        startTime = System.currentTimeMillis();
        
        r = new ConnectFourNegamaxSearchEngine(heuristicFunction)
                .search(b, DEPTH);
        
        endTime = System.currentTimeMillis();
        
        System.out.printf(
            """
            ConnectFourNegamaxSearchEngine in %d milliseconds.
            """,
            endTime - startTime);
        
        System.out.println(r);
        
        System.out.println();
        System.out.println("<<< ConnnectFourAlphaBetaPruningSearchEngine vs. " + 
                           "ConnectFourNegamaxSearchEngine >>>");
        
        long duration1 = 0L;
        long duration2 = 0L;
        long duration = 0L;
        
        final SearchEngine<ConnectFourBoard> engine1 = 
                new ConnectFourAlphaBetaPruningSearchEngine(heuristicFunction);
        
        final SearchEngine<ConnectFourBoard> engine2 = 
                new ConnectFourNegamaxSearchEngine(heuristicFunction);
        
        ConnectFourBoard board = new ConnectFourBoard();
        System.out.println(board);
        
        System.out.printf(
                "ConnectFourAlphaBetaPruningSearchEngine depth:   %d\n",
                ALPHA_BETA_PRUNING_ENGINE_DEPTH);
        
        System.out.printf(
                "ConnectFourNegamaxSearchEngine depth:            %d\n", 
                NEGAMAX_ENGINE_DEPTH);
        
        while (true) {
            
            // Alpha-beta pruning search engine makes a ply first per round:
            startTime = System.currentTimeMillis();
            
            board = engine1.search(board,
                                   ALPHA_BETA_PRUNING_ENGINE_DEPTH,
                                   PlayerType.MINIMIZING_PLAYER);
            
            endTime = System.currentTimeMillis();
            
            duration = endTime - startTime;
            
            duration1 += duration;
            
            System.out.println(board);
            
            System.out.printf(
                    "Alpha-beta pruning engine (X) in %d milliseconds.\n",
                    duration);
            
            if (board.isTerminal()) {
                report(board);
                break;
            }
            
            startTime = System.currentTimeMillis();
            
            board = engine2.search(board, 
                                   NEGAMAX_ENGINE_DEPTH,
                                   PlayerType.MAXIMIZING_PLAYER);
            
            endTime = System.currentTimeMillis();
            
            duration = endTime - startTime;
            
            duration2 += duration;
            
            System.out.println(board);
            
            System.out.printf(
                    "Negamax engine (O) in %d milliseconds.\n", 
                    duration);
            
            if (board.isTerminal()) {
                report(board);
                break;
            }
        }
        
        System.out.printf(
                "Alpha-beta pruning engine in total %d milliseconds.\n", 
                duration1);
        
        System.out.printf(
                "Negamax engine in total %d milliseconds.\n", 
                duration2);
    }
    
    private static void report(final ConnectFourBoard connectFourBoard) {
        if (connectFourBoard.isTie()) {
            System.out.println("RESULT: It's a tie.");
        } else if (connectFourBoard
                .isWinningFor(PlayerType.MINIMIZING_PLAYER)) {
            
            System.out.println("RESULT: Alpha-beta engine wins.");
        } else if (connectFourBoard
                .isWinningFor(PlayerType.MAXIMIZING_PLAYER)) {
            
            System.out.println("RESULT: Negamax engine wins.");
        } else {
            
            throw new IllegalStateException("Should not get here.");
        }
    }
}
