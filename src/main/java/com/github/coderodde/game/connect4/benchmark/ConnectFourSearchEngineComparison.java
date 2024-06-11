package com.github.coderodde.game.connect4.benchmark;

import com.github.coderodde.game.connect4.ConnectFourBoard;
import com.github.coderodde.game.connect4.ConnectFourHeuristicFunction;
import com.github.coderodde.game.zerosum.PlayerType;
import com.github.coderodde.game.zerosum.SearchEngine;
import com.github.coderodde.game.zerosum.impl.AlphaBetaPruningSearchEngine;
import com.github.coderodde.game.zerosum.impl.ConnectFourAlphaBetaPruningSearchEngine;
import com.github.coderodde.game.zerosum.impl.ParallelConnectFourAlphaBetaPruningSearchEngine;

public class ConnectFourSearchEngineComparison {
    
    private static final int DEPTH = 9;
    private static final int SEED_DEPTH = 2;

    public static void main(String[] args) {
        ConnectFourBoard b = new ConnectFourBoard();
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
        
        b = new ConnectFourBoard();
        
        startTime = System.currentTimeMillis();
        
        r = new ConnectFourAlphaBetaPruningSearchEngine(heuristicFunction)
                .search(b, DEPTH);
        
        endTime = System.currentTimeMillis();
        
        System.out.printf(
                "ConnectFourAlphaBetaPruningSearchEngine in %d milliseconds.\n",
                endTime - startTime);
        
        System.out.println(r);
        
        b = new ConnectFourBoard();
        
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
        
        System.out.println("<<< AI vs. AI >>>");
        
        long duration1 = 0L;
        long duration2 = 0L;
        long duration = 0L;
        
        final SearchEngine<ConnectFourBoard> engine1 = 
                new ConnectFourAlphaBetaPruningSearchEngine(heuristicFunction);
        
        final SearchEngine<ConnectFourBoard> engine2 = 
                new ParallelConnectFourAlphaBetaPruningSearchEngine(
                        heuristicFunction, 
                        SEED_DEPTH);
        
        ConnectFourBoard board = new ConnectFourBoard();
        System.out.println(board);
        
        final int ENGINE1_DEPTH = 9;
        final int ENGINE2_DEPTH = 10;
        
        System.out.printf("Serial AI depth: %d\n", ENGINE1_DEPTH);
        System.out.printf("Parallel AI depth: %d\n", ENGINE2_DEPTH);
        
        while (true) {
            
            // Parallel search engine is the second in a turn:
            startTime = System.currentTimeMillis();
            
            board = engine2.search(board, 
                                   ENGINE2_DEPTH,
                                   PlayerType.MINIMIZING_PLAYER);
            
            endTime = System.currentTimeMillis();
            
            duration = endTime - startTime;
            
            duration2 += duration;
            
            System.out.println(board);
            
            System.out.printf(
                    "Parallel engine in %d milliseconds.\n", 
                    duration);
            
            if (board.isTerminal()) {
                report(board);
                break;
            }
            
            // Serial search engine makes a ply first per round:
            startTime = System.currentTimeMillis();
            
            board = engine1.search(board,
                                   ENGINE1_DEPTH,
                                   PlayerType.MAXIMIZING_PLAYER);
            
            endTime = System.currentTimeMillis();
            
            duration = endTime - startTime;
            
            duration1 += duration;
            
            System.out.println(board);
            
            System.out.printf("Serial engine in %d milliseconds.\n", duration);
            
            if (board.isTerminal()) {
                report(board);
                break;
            }
        }
        
        System.out.printf(
                "Serial engine in total %d milliseconds.\n", 
                duration1);
        
        System.out.printf(
                "Parallel engine in total %d milliseconds.\n", 
                duration2);
    }
    
    private static void report(final ConnectFourBoard connectFourBoard) {
        if (connectFourBoard.isTie()) {
            System.out.println("RESULT: It's a tie.");
        } else if (connectFourBoard
                .isWinningFor(PlayerType.MINIMIZING_PLAYER)) {
            
            System.out.println("RESULT: Parallel engine wins.");
        } else if (connectFourBoard
                .isWinningFor(PlayerType.MAXIMIZING_PLAYER)) {
            
            System.out.println("RESULT: Serial engine wins.");
        } else {
            
            throw new IllegalStateException("Should not get here.");
        }
    }
}
