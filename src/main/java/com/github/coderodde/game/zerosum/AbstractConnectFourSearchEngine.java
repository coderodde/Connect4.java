package com.github.coderodde.game.zerosum;

import com.github.coderodde.game.connect4.ConnectFourBoard;

/**
 * This abstract class defines some basic infrastructure for Connect Four AI
 * algorithms.
 * 
 * @version 1.0.0 (Jun 18, 2024)
 * @since 1.0.0 (Jun 18, 2024)
 */
public abstract class AbstractConnectFourSearchEngine 
        implements SearchEngine<ConnectFourBoard> {
   
    protected static final int[] PLIES = { 3, 2, 4, 1, 5, 0, 6 };
    public static final int MAX_INT = +1_000_000_000;
    public static final int MIN_INT = -1_000_000_000;
}
