package com.github.coderodde.game.connect4;

import com.github.coderodde.game.zerosum.HeuristicFunction;
import com.github.coderodde.game.zerosum.PlayerType;
import java.util.Scanner;
import com.github.coderodde.game.zerosum.SearchEngine;
import com.github.coderodde.game.zerosum.impl.ConnectFourNegamaxSearchEngine;
import com.github.coderodde.game.zerosum.impl.ParallelConnectFourAlphaBetaPruningSearchEngine;

/**
 * This class implements the REPL for playring Connect Four against an AI bot.
 * 
 * @version 1.0.0 (Jun 5, 2024)
 * @since 1.0.0 (Jun 5, 2024)
 */
public class ConnectFour {
   
    private static final int DEFAULT_DEPTH = 8;
    private static final int MINIMUM_DEPTH = 1;

    public static void main(String[] args) {
        final int depth = parseDepth(args);
        
        System.out.printf(">>> Using search depth: %d.\n", depth);
        
        final Scanner scanner = new Scanner(System.in);
        final HeuristicFunction<ConnectFourBoard> heuristicFunction = 
                new ConnectFourHeuristicFunction();
        
//        final SearchEngine<ConnectFourBoard> bot = 
//                new ParallelConnectFourAlphaBetaPruningSearchEngine(
//                        heuristicFunction, 
//                        2);
        
        final SearchEngine<ConnectFourBoard> bot = 
                new ConnectFourNegamaxSearchEngine(heuristicFunction);

        ConnectFourBoard currentBoard = new ConnectFourBoard();
        
        while (true) {
            System.out.println(currentBoard);
            
            final String command = scanner.next().trim();
            
            if (command.equals("quit") || command.equals("q")) {
                return;
            }
            
            int column;
            
            try {
                column = Integer.parseInt(command);
            } catch (final NumberFormatException ex) {
                System.out.printf(">>> Command \"%s\" not recognized.\n",
                                  command);
                continue;
            }
            
            if (0 < column && column <= ConnectFourBoard.COLUMNS) {
                column--; // 1-based indexing to 0-based.
                
                currentBoard.makePly(
                        column,
                        PlayerType.MINIMIZING_PLAYER);
                
                long startTime = System.currentTimeMillis();
                
                final ConnectFourBoard nextConnectFourBoard = 
                        bot.search(currentBoard, depth);
                
                long endTime = System.currentTimeMillis();
                
                System.out.printf(">>> AI took %d milliseconds.\n",
                                  endTime - startTime);
                
                if (nextConnectFourBoard != null) {
                    currentBoard = nextConnectFourBoard;
                }
                
                if (currentBoard.isWinningFor(PlayerType.MINIMIZING_PLAYER)) {
                    System.out.println(">>> You won!");
                    System.out.println(currentBoard);
                    return;
                }
                
                if (currentBoard.isWinningFor(PlayerType.MAXIMIZING_PLAYER)) {
                    System.out.println(">>> AI won!");
                    System.out.println(currentBoard);
                    return;
                }
                
                if (currentBoard.isTie()) {
                    System.out.println(">>> It's a tie!");
                    System.out.println(currentBoard);
                    return;
                }
                
                System.out.println(">>> Board after AI's move:");
            }
        }
    }
    
    /**
     * Attempts to read the search depth from the {@code args}. If not present,
     * returns the default depth.
     * 
     * @param args the array of command line arguments.
     * 
     * @return the search depth to use.
     */
    private static int parseDepth(final String[] args) {
        if (args.length == 0) {
            return DEFAULT_DEPTH;
        }
        
        int depth;
        
        try {
            depth = Integer.parseInt(args[0]);
        } catch (final NumberFormatException ex) {
            return DEFAULT_DEPTH;
        }
        
        return Math.max(depth, MINIMUM_DEPTH);
    }
}
