package com.bignerdranch.geoquiz;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.widget.Button;
import android.widget.TextView;

public class CheatActivity extends Activity {

    private static final String EXTRA_STATEMENT_IS_TRUE = "statement_is_true";
    private static final String KEY_USER_CHEATED = "user_cheated";

    private boolean mStatementIsTrue;
    private boolean mUserCheated;

    private TextView mAnswerTextView;
    private Button mShowAnswerButton;

    static Intent newIntent(Context context, boolean statementIsTrue) {
        Intent intent = new Intent(context, CheatActivity.class);
        intent.putExtra(EXTRA_STATEMENT_IS_TRUE, statementIsTrue);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cheat);

        mAnswerTextView = findViewById(R.id.answer_text_view);
        mShowAnswerButton = findViewById(R.id.show_answer_button);

        mStatementIsTrue = getIntent().getBooleanExtra(EXTRA_STATEMENT_IS_TRUE, false);

        if (savedInstanceState != null) {
            mUserCheated = savedInstanceState.getBoolean(KEY_USER_CHEATED);
        }

        if (mUserCheated) {
            showAnswer();
            // we can't use animations in onCreate! Kind of obvious
            hideAnswerButton(false);
        }

        mShowAnswerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mUserCheated = true;
                showAnswer();
                hideAnswerButton(true);
            }
        });
    }

    private void showAnswer() {
        mAnswerTextView.setText(mStatementIsTrue ? R.string.true_button : R.string.false_button);
    }

    private void hideAnswerButton(boolean hideGradually) {
        if (hideGradually && Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            int cx = mShowAnswerButton.getWidth() / 2;
            int cy = mShowAnswerButton.getHeight() / 2;
            float radius = mShowAnswerButton.getWidth();

            Animator anim = ViewAnimationUtils.createCircularReveal(mShowAnswerButton, cx, cy, radius, 0);
            anim.addListener(new AnimatorListenerAdapter() {
                // must set to invisible at end, otherwise button reappears after anim is done
                @Override
                public void onAnimationEnd(Animator animation) {
                    super.onAnimationEnd(animation);
                    mShowAnswerButton.setVisibility(View.INVISIBLE);
                }
            });

            anim.start();
        } else {
            mShowAnswerButton.setVisibility(View.INVISIBLE);
        }
    }

    // This closes the loop-hole where the user rotates the device after cheating
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean(KEY_USER_CHEATED, mUserCheated);
    }

    @Override
    public void onBackPressed() {
        // If user has cheated, I set to RESULT_OK. Otherwise, RESULT_CANCELLED is returned
        if (mUserCheated) {
            setResult(RESULT_OK);
        }

        super.onBackPressed();
    }
}
