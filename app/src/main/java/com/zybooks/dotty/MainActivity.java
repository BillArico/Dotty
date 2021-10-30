package com.zybooks.dotty;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import java.util.Locale;
import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;

public class MainActivity extends AppCompatActivity {

    private DotsGame mGame;
    private DotsGrid mDotsGrid;
    private TextView mMovesRemaining;
    private TextView mScore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mMovesRemaining = findViewById(R.id.movesRemaining);
        mScore = findViewById(R.id.score);
        mDotsGrid = findViewById(R.id.gameGrid);
        mDotsGrid.setGridListener(mGridListener);

        mGame = DotsGame.getInstance();
        startNewGame();
    }

    private final DotsGrid.DotsGridListener mGridListener = new DotsGrid.DotsGridListener() {

        @Override
        public void onDotSelected(Dot dot, DotsGrid.DotSelectionStatus selectionStatus) {

            // Ignore selections when game is over
            if (mGame.isGameOver()) return;

            // Add/remove dot to/from selected dots
            DotsGame.DotStatus addStatus = mGame.processDot(dot);

            // If done selecting dots then replace selected dots and display new moves and score
            if (selectionStatus == DotsGrid.DotSelectionStatus.Last) {
                if (mGame.getSelectedDots().size() > 1) {

                    mDotsGrid.animateDots();

                    // These methods must be called AFTER the animation completes
                    //mGame.finishMove();
                    //updateMovesAndScore();
                    updateMovesAndScore();
                }
                else {
                    mGame.clearSelectedDots();
                }
            }

            // Display changes to the game
            mDotsGrid.invalidate();
        }
        @Override
        public void onAnimationFinished() {
            mGame.finishMove();
            mDotsGrid.invalidate();
            updateMovesAndScore();
        }
    };

    public void newGameClick(View view) {
        // Animate down off screen
        int screenHeight = this.getWindow().getDecorView().getHeight();
        ObjectAnimator moveBoardOff = ObjectAnimator.ofFloat(mDotsGrid,
                "translationY", screenHeight);
        moveBoardOff.setDuration(700);
        moveBoardOff.start();
        moveBoardOff.addListener(new AnimatorListenerAdapter() {
            public void onAnimationEnd(Animator animation) {
                startNewGame();
                // Animate from above the screen down to default location
                ObjectAnimator moveBoardOn = ObjectAnimator.ofFloat(mDotsGrid,
                        "translationY", -screenHeight, 0);
                moveBoardOn.setDuration(700);
                moveBoardOn.start();
            }
        });
        startNewGame();
    }

    private void startNewGame() {
        mGame.newGame();
        mDotsGrid.invalidate();
        updateMovesAndScore();
    }

    private void updateMovesAndScore() {
        mMovesRemaining.setText(String.format(Locale.getDefault(), "%d", mGame.getMovesLeft()));
        mScore.setText(String.format(Locale.getDefault(), "%d", mGame.getScore()));
    }
}