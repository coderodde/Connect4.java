package com.github.coderodde.game.connect4;

import static com.github.coderodde.game.connect4.ConnectFourBoard.COLUMNS;
import static com.github.coderodde.game.connect4.ConnectFourBoard.ROWS;
import com.github.coderodde.game.zerosum.HeuristicFunction;
import com.github.coderodde.game.zerosum.PlayerType;

/**
 * This class implements a heuristic function for the Connect Four game.
 * 
 * @version 1.0.0 (Jun 5, 2024)
 * @since 1.0.0 (Jun 5, 2024)
 */
public final class ConnectFourHeuristicFunction 
        implements HeuristicFunction<ConnectFourBoard> {
    
    private static final int TWO_BLOCKS_SCORE = 1;
    private static final int THREE_BLOCKS_SCORE = 10;
    private static final int MINIMIZING_PLAYER_VICTORY_SCORE = -1_000_000;
    private static final int MAXIMIZING_PLAYER_VICTORY_SCORE = +1_000_000;

    @Override
    public int evaluate(final ConnectFourBoard state, final int depth) {
        if (state.isWinningFor(PlayerType.MINIMIZING_PLAYER)) {
            return MINIMIZING_PLAYER_VICTORY_SCORE - depth;
        }
        
        if (state.isWinningFor(PlayerType.MAXIMIZING_PLAYER)) {
            return MAXIMIZING_PLAYER_VICTORY_SCORE + depth;
        }
        
        return evaluate2(state) + evaluate3(state);
    }
    
    private static int evaluate2(final ConnectFourBoard state) {
        return evaluate2Horizontal(state) +
               evaluate2Vertical(state) + 
               evaluate2Ascending(state) +
               evaluate2Descending(state);
    }
    
    private static int evaluate3(final ConnectFourBoard state) {
        return evaluate3Horizontal(state) +
               evaluate3Vertical(state) + 
               evaluate3Ascending(state) +
               evaluate3Descending(state);
    }
    
    private static int evaluate2Horizontal(final ConnectFourBoard state) {
        int sum = 0;
        
        for (int y = 0; y < ROWS; y++) {
            for (int x = 0; x < COLUMNS - 1; x++) {
                if (state.get(x, y) == PlayerType.MAXIMIZING_PLAYER &&
                    state.get(x + 1, y) == PlayerType.MAXIMIZING_PLAYER) {
                    sum += TWO_BLOCKS_SCORE;
                } else if (state.get(x, y) == PlayerType.MINIMIZING_PLAYER &&
                       state.get(x + 1, y) == PlayerType.MINIMIZING_PLAYER) {
                    
                    sum -= TWO_BLOCKS_SCORE;
                }
            }
        }
        
        return sum;
    }
    
    private static int evaluate2Vertical(final ConnectFourBoard state) {
        int sum = 0;
        
        for (int y = 0; y < ROWS - 1; y++) {
            for (int x = 0; x < COLUMNS; x++) {
                if (state.get(x, y) == PlayerType.MAXIMIZING_PLAYER &&
                    state.get(x, y + 1) == PlayerType.MAXIMIZING_PLAYER) {
                    
                    sum += TWO_BLOCKS_SCORE;
                } else if (state.get(x, y) == PlayerType.MINIMIZING_PLAYER &&
                        state.get(x, y + 1) == PlayerType.MINIMIZING_PLAYER) {
                    
                    sum -= TWO_BLOCKS_SCORE;
                }
            }
        }
        
        return sum;
    }
    
    private static int evaluate2Ascending(final ConnectFourBoard state) {
        int sum = 0;
        
        for (int y = ROWS - 1; y > 0; y--) {
            for (int x = 0; x < COLUMNS - 1; x++) {
                if (state.get(x, y) == PlayerType.MAXIMIZING_PLAYER &&
                    state.get(x + 1, y - 1) == PlayerType.MAXIMIZING_PLAYER) {
                    sum += TWO_BLOCKS_SCORE;
                } else if (state.get(x, y)
                        == PlayerType.MINIMIZING_PLAYER
                        &&
                        state.get(x + 1, y - 1) == PlayerType.MINIMIZING_PLAYER) {
                    
                    sum -= TWO_BLOCKS_SCORE;
                }
            }
        }
        
        return sum;
    }
    
    private static int evaluate2Descending(final ConnectFourBoard state) {
        int sum = 0;
        
        for (int y = ROWS - 1; y > 0; y--) {
            for (int x = 1; x < COLUMNS; x++) {
                if (state.get(x, y) == PlayerType.MAXIMIZING_PLAYER &&
                    state.get(x - 1, y - 1) == PlayerType.MAXIMIZING_PLAYER) {
                    
                    sum += TWO_BLOCKS_SCORE;
                } else if (state.get(x, y) == PlayerType.MINIMIZING_PLAYER &&
                        state.get(x - 1, y - 1) 
                        == PlayerType.MINIMIZING_PLAYER) {
                    
                    sum -= TWO_BLOCKS_SCORE;
                }
            }
        }
        
        return sum;
    }
    
    private static int evaluate3Horizontal(final ConnectFourBoard state) {
        int sum = 0;
        
        for (int y = 0; y < ROWS; y++) {
            for (int x = 0; x < COLUMNS - 2; x++) {
                if (state.get(x, y) == PlayerType.MAXIMIZING_PLAYER &&
                    state.get(x + 1, y) == PlayerType.MAXIMIZING_PLAYER &&
                    state.get(x + 2, y) == PlayerType.MAXIMIZING_PLAYER) {
                    
                    sum += THREE_BLOCKS_SCORE;
                } else if (state.get(x, y) == PlayerType.MINIMIZING_PLAYER &&
                        state.get(x + 1, y) == PlayerType.MINIMIZING_PLAYER &&
                        state.get(x + 2, y) == PlayerType.MINIMIZING_PLAYER) {
                    
                    sum -= THREE_BLOCKS_SCORE;
                }
            }
        }
        
        return sum;
    }
    
    private static int evaluate3Vertical(final ConnectFourBoard state) {
        int sum = 0;
        
        for (int y = 0; y < ROWS - 2; y++) {
            for (int x = 0; x < COLUMNS; x++) {
                if (state.get(x, y) == PlayerType.MAXIMIZING_PLAYER &&
                    state.get(x, y + 1) == PlayerType.MAXIMIZING_PLAYER &&
                    state.get(x, y + 2) == PlayerType.MAXIMIZING_PLAYER) {
                    
                    sum += THREE_BLOCKS_SCORE;
                } else if (state.get(x, y) == PlayerType.MINIMIZING_PLAYER &&
                        state.get(x, y + 1) == PlayerType.MINIMIZING_PLAYER &&
                        state.get(x, y + 2) == PlayerType.MINIMIZING_PLAYER) {
                    
                    sum -= THREE_BLOCKS_SCORE;
                }
            }
        }
        
        return sum;
    }
    
    private static int evaluate3Ascending(final ConnectFourBoard state) {
        int sum = 0;
        
        for (int y = ROWS - 1; y > 1; y--) {
            for (int x = 0; x < COLUMNS - 2; x++) {
                if (state.get(x, y) == PlayerType.MAXIMIZING_PLAYER &&
                    state.get(x + 1, y - 1) == PlayerType.MAXIMIZING_PLAYER &&
                    state.get(x + 2, y - 2) == PlayerType.MAXIMIZING_PLAYER) {
                    
                    sum += THREE_BLOCKS_SCORE;
                } else if (state.get(x, y) == PlayerType.MINIMIZING_PLAYER 
                        &&
                        state.get(x + 1, y - 1) == PlayerType.MINIMIZING_PLAYER 
                        && 
                        state.get(x + 2, y - 2) == 
                        PlayerType.MINIMIZING_PLAYER) {
                    
                    sum -= THREE_BLOCKS_SCORE;
                }
            }
        }
        
        return sum;
    }
    
    private static int evaluate3Descending(final ConnectFourBoard state) {
        int sum = 0;
        
        for (int y = ROWS - 1; y > 1; y--) {
            for (int x = 2; x < COLUMNS; x++) {
                if (state.get(x, y) == PlayerType.MAXIMIZING_PLAYER &&
                    state.get(x - 1, y - 1) == PlayerType.MAXIMIZING_PLAYER &&
                    state.get(x - 2, y - 2) == PlayerType.MAXIMIZING_PLAYER) {
                    
                    sum += THREE_BLOCKS_SCORE;
                } else if (state.get(x, y) == PlayerType.MINIMIZING_PLAYER &&
                        state.get(x - 1, y - 1) == PlayerType.MINIMIZING_PLAYER
                        &&
                        state.get(x - 2, y - 2) 
                        == PlayerType.MINIMIZING_PLAYER) {
                    
                    sum -= THREE_BLOCKS_SCORE;
                }
            }
        }
        
        return sum;
    }
}
