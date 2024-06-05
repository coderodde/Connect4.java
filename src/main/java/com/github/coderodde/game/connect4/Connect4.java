package com.github.coderodde.game.connect4;

import java.util.Scanner;

/**
 *
 * @version 1.0.0 (Jun 5, 2024)
 * @since 1.0.0 (Jun 5, 2024)
 */
public class Connect4 {

    public static void main(String[] args) {
        Board board = new Board();
        
        final Scanner scanner = new Scanner(System.in);
        
        while (true) {
            final String command = scanner.next().trim();
            
            if (command.equals("quit") || command.equals("q")) {
                return;
            }
            
            final Integer column;
            
            try {
                column = Integer.parseInt(command);
            } catch (final NumberFormatException ex) {
                System.out.printf(">>> Command \"%s\" not recognized.\n");
            }
        }
    }
}
