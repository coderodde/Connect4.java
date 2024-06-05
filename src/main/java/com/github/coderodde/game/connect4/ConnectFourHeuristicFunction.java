package com.github.coderodde.game.connect4;

import static com.github.coderodde.game.connect4.ConnectFourBoard.COLUMNS;
import static com.github.coderodde.game.connect4.ConnectFourBoard.ROWS;
import com.github.coderodde.game.zerosum.HeuristicFunction;
import com.github.coderodde.game.zerosum.PlayerType;

/**
 *
 * @author rodio
 */
public final class ConnectFourHeuristicFunction 
        implements HeuristicFunction<ConnectFourBoard> {
    
    private static final double TWO_BLOCKS_SCORE = 1.0;
    private static final double THREE_BLOCKS_SCORE = 10.0;
    private static final double MINIMIZING_PLAYER_VICTORY_SCORE = -10E6;
    private static final double MAXIMIZING_PLAYER_VICTORY_SCORE = +10E6;

    @Override
    public double evaluate(ConnectFourBoard state) {
        if (state.isWinningFor(PlayerType.MINIMIZING_PLAYER)) {
            return MINIMIZING_PLAYER_VICTORY_SCORE;
        }
        
        if (state.isWinningFor(PlayerType.MAXIMIZING_PLAYER)) {
            return MAXIMIZING_PLAYER_VICTORY_SCORE;
        }
        
        return evaluate2(state) + evaluate3(state);
    }
    
    private static double evaluate2(final ConnectFourBoard state) {
        return evaluate2Horizontal(state) +
               evaluate2Vertical(state) + 
               evaluate2Ascending(state) +
               evaluate2Descending(state);
    }
    
    private static double evaluate3(final ConnectFourBoard state) {
        return evaluate3Horizontal(state) +
               evaluate3Vertical(state) + 
               evaluate3Ascending(state) +
               evaluate3Descending(state);
    }
    
    private static double evaluate2Horizontal(final ConnectFourBoard state) {
        double sum = 0.0;
        
        for (int y = 0; y < ROWS; y++) {
            for (int x = 0; x < COLUMNS - 1; x++) {
                if (state.boardData[y][x] == PlayerType.MAXIMIZING_PLAYER &&
                    state.boardData[y][x + 1] == PlayerType.MAXIMIZING_PLAYER) {
                    
                    sum += TWO_BLOCKS_SCORE;
                } else if (state.boardData[y][x] 
                        == PlayerType.MINIMIZING_PLAYER
                        &&
                        state.boardData[y][x + 1] 
                        == PlayerType.MINIMIZING_PLAYER) {
                    
                    sum -= TWO_BLOCKS_SCORE;
                }
            }
        }
        
        return sum;
    }
    
    private static double evaluate2Vertical(final ConnectFourBoard state) {
        double sum = 0.0;
        
        for (int y = 0; y < ROWS - 1; y++) {
            for (int x = 0; x < COLUMNS; x++) {
                if (state.boardData[y][x] == PlayerType.MAXIMIZING_PLAYER &&
                    state.boardData[y + 1][x] == PlayerType.MAXIMIZING_PLAYER) {
                    
                    sum += TWO_BLOCKS_SCORE;
                } else if (state.boardData[y][x] 
                        == PlayerType.MINIMIZING_PLAYER
                        &&
                        state.boardData[y + 1][x] 
                        == PlayerType.MINIMIZING_PLAYER) {
                    
                    sum -= TWO_BLOCKS_SCORE;
                }
            }
        }
        
        return sum;
    }
    
    private static double evaluate2Ascending(final ConnectFourBoard state) {
        double sum = 0.0;
        
        for (int y = ROWS - 1; y > 0; y--) {
            for (int x = 0; x < COLUMNS - 1; x++) {
                if (state.boardData[y][x] 
                        == PlayerType.MAXIMIZING_PLAYER 
                        &&
                    state.boardData[y - 1][x + 1] 
                        == PlayerType.MAXIMIZING_PLAYER) {
                    
                    sum += TWO_BLOCKS_SCORE;
                } else if (state.boardData[y][x] 
                        == PlayerType.MINIMIZING_PLAYER
                        &&
                        state.boardData[y - 1][x + 1] 
                        == PlayerType.MINIMIZING_PLAYER) {
                    
                    sum -= TWO_BLOCKS_SCORE;
                }
            }
        }
        
        return sum;
    }
    
    private static double evaluate2Descending(final ConnectFourBoard state) {
        double sum = 0.0;
        
        for (int y = ROWS - 1; y > 0; y--) {
            for (int x = 1; x < COLUMNS; x++) {
                if (state.boardData[y][x] 
                        == PlayerType.MAXIMIZING_PLAYER 
                        &&
                    state.boardData[y - 1][x - 1] 
                        == PlayerType.MAXIMIZING_PLAYER) {
                    
                    sum += TWO_BLOCKS_SCORE;
                } else if (state.boardData[y][x] 
                        == PlayerType.MINIMIZING_PLAYER
                        &&
                        state.boardData[y - 1][x - 1] 
                        == PlayerType.MINIMIZING_PLAYER) {
                    
                    sum -= TWO_BLOCKS_SCORE;
                }
            }
        }
        
        return sum;
    }
    
    private static double evaluate3Horizontal(final ConnectFourBoard state) {
        double sum = 0.0;
        
        for (int y = 0; y < ROWS; y++) {
            for (int x = 0; x < COLUMNS - 2; x++) {
                if (state.boardData[y][x] == PlayerType.MAXIMIZING_PLAYER &&
                    state.boardData[y][x + 1] == PlayerType.MAXIMIZING_PLAYER &&
                    state.boardData[y][x + 2] == PlayerType.MAXIMIZING_PLAYER) {
                    
                    sum += THREE_BLOCKS_SCORE;
                } else if (state.boardData[y][x] 
                        == PlayerType.MINIMIZING_PLAYER
                        &&
                        state.boardData[y][x + 1] 
                        == PlayerType.MINIMIZING_PLAYER
                        &&
                        state.boardData[y][x + 2]
                        == PlayerType.MINIMIZING_PLAYER) {
                    
                    sum -= THREE_BLOCKS_SCORE;
                }
            }
        }
        
        return sum;
    }
    
    private static double evaluate3Vertical(final ConnectFourBoard state) {
        double sum = 0.0;
        
        for (int y = 0; y < ROWS - 2; y++) {
            for (int x = 0; x < COLUMNS; x++) {
                if (state.boardData[y][x] == PlayerType.MAXIMIZING_PLAYER &&
                    state.boardData[y + 1][x] == PlayerType.MAXIMIZING_PLAYER &&
                    state.boardData[y + 2][x] == PlayerType.MAXIMIZING_PLAYER) {
                    
                    sum += THREE_BLOCKS_SCORE;
                } else if (state.boardData[y][x] 
                        == PlayerType.MINIMIZING_PLAYER
                        &&
                        state.boardData[y + 1][x] 
                        == PlayerType.MINIMIZING_PLAYER
                        &&
                        state.boardData[y + 2][x] 
                        == PlayerType.MINIMIZING_PLAYER) {
                    
                    sum -= THREE_BLOCKS_SCORE;
                }
            }
        }
        
        return sum;
    }
    
    private static double evaluate3Ascending(final ConnectFourBoard state) {
        double sum = 0.0;
        
        for (int y = ROWS - 1; y > 1; y--) {
            for (int x = 0; x < COLUMNS - 2; x++) {
                if (state.boardData[y][x] 
                        == PlayerType.MAXIMIZING_PLAYER 
                        &&
                    state.boardData[y - 1][x + 1] 
                        == PlayerType.MAXIMIZING_PLAYER
                        &&
                    state.boardData[y - 2][x + 2] 
                        == PlayerType.MAXIMIZING_PLAYER) {
                    
                    sum += THREE_BLOCKS_SCORE;
                } else if (state.boardData[y][x] 
                        == PlayerType.MINIMIZING_PLAYER
                        &&
                        state.boardData[y - 1][x + 1] 
                        == PlayerType.MINIMIZING_PLAYER
                        && 
                        state.boardData[y - 2][x + 2] 
                        == PlayerType.MINIMIZING_PLAYER) {
                    
                    sum -= THREE_BLOCKS_SCORE;
                }
            }
        }
        
        return sum;
    }
    
    private static double evaluate3Descending(final ConnectFourBoard state) {
        double sum = 0.0;
        
        for (int y = ROWS - 1; y > 1; y--) {
            for (int x = 2; x < COLUMNS; x++) {
                if (state.boardData[y][x] 
                        == PlayerType.MAXIMIZING_PLAYER 
                        &&
                    state.boardData[y - 1][x - 1] 
                        == PlayerType.MAXIMIZING_PLAYER
                        &&
                    state.boardData[y - 2][x - 2] 
                        == PlayerType.MAXIMIZING_PLAYER) {
                    
                    sum += THREE_BLOCKS_SCORE;
                } else if (state.boardData[y][x] 
                        == PlayerType.MINIMIZING_PLAYER
                        &&
                        state.boardData[y - 1][x - 1] 
                        == PlayerType.MINIMIZING_PLAYER
                        &&
                        state.boardData[y - 2][x - 2]
                        == PlayerType.MINIMIZING_PLAYER) {
                    
                    sum -= THREE_BLOCKS_SCORE;
                }
            }
        }
        
        return sum;
    }
}
