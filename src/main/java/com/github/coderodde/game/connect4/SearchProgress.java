package com.github.coderodde.game.connect4;

import static com.github.coderodde.game.connect4.ConnectFourBoard.COLUMNS;
import javafx.application.Platform;
import javafx.scene.control.ProgressBar;

/**
 *
 * @version 1.0.0
 * @since 1.0.0
 */
public final class SearchProgress {
    
    private volatile long currentNumberOfStatesDiscovered;
    private final long maximumNumberOfStates;
    private final ProgressBar progressBar;
    
    public SearchProgress(final int depth,
                          final ProgressBar progressBar) {
        
        this.maximumNumberOfStates = (long) Math.pow(COLUMNS, depth);
        this.progressBar = progressBar;
    }
    
    public void hit() {
        currentNumberOfStatesDiscovered++;
    }
    
    public void clear() {
        currentNumberOfStatesDiscovered = 0;
    }
    
    public long getCurrentNumberOfStatesDiscovered() {
        return currentNumberOfStatesDiscovered;
    }
    
    public void setProgressBarValue() {
        Platform.runLater(() -> {
            progressBar.setProgress(getProgressValue());
        });
    }
    
    public double getProgressValue() {
        final double completionRatio = 20.0 *
               ((double) currentNumberOfStatesDiscovered) /
               ((double) maximumNumberOfStates);
        
        return completionRatio;
    }
}
