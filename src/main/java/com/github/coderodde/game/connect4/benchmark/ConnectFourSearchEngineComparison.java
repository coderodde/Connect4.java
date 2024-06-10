package com.github.coderodde.game.connect4.benchmark;

import com.github.coderodde.game.connect4.ConnectFourBoard;
import com.github.coderodde.game.connect4.ConnectFourHeuristicFunction;
import com.github.coderodde.game.zerosum.impl.AlphaBetaPruningSearchEngine;
import com.github.coderodde.game.zerosum.impl.ConnectFourAlphaBetaPruningSearchEngine;
import com.github.coderodde.game.zerosum.impl.ParallelConnectFourAlphaBetaPruningSearchEngine;

public class ConnectFourSearchEngineComparison {
    
    private static final int DEPTH = 7;

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
                heuristicFunction)
                .search(b, DEPTH);
        
        endTime = System.currentTimeMillis();
        
        System.out.printf(
            """
            ParallelConnectFourAlphaBetaPruningSearchEngine in %d milliseconds.
            """,
            endTime - startTime);
        
        System.out.println(r);
    }
}
