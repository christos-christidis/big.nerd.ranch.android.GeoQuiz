package com.bignerdranch.geoquiz;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Locale;

public class MainActivity extends Activity {

    private static final String SAVED_CURRENT_INDEX = "current_index";
    private static final int REQUEST_CODE_CHEAT = 0;

    private static final String SAVED_USER_CHEATED = "user_cheated";
    private boolean mUserCheated;

    private TextView mStatementView;
    private Button mTrueButton;
    private Button mFalseButton;
    private Button mNextButton;
    private Button mCheatButton;

    private final Statement[] mStatements = new Statement[]{
            new Statement(R.string.statement_australia, true),
            new Statement(R.string.statement_oceans, true),
            new Statement(R.string.statement_mideast, false),
            new Statement(R.string.statement_africa, false),
            new Statement(R.string.statement_americas, true),
            new Statement(R.string.statement_asia, true),
    };

    private int mCurrentIndex = 0;
    private int mNumCorrectAnswers = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (savedInstanceState != null) {
            mCurrentIndex = savedInstanceState.getInt(SAVED_CURRENT_INDEX, 0);
            mUserCheated = savedInstanceState.getBoolean(SAVED_USER_CHEATED);
        }

        mStatementView = findViewById(R.id.statement_view);
        updateStatement();

        setUpTrueButton();
        setUpFalseButton();
        setUpNextButton();
        setUpCheatButton();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(SAVED_CURRENT_INDEX, mCurrentIndex);
        outState.putBoolean(SAVED_USER_CHEATED, mUserCheated);
    }

    private void setUpTrueButton() {
        mTrueButton = findViewById(R.id.true_button);
        mTrueButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                disableAllButtonsExceptNext();
                checkAnswer(true);
                mNumCorrectAnswers++;
            }
        });
    }

    private void setUpFalseButton() {
        mFalseButton = findViewById(R.id.false_button);
        mFalseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                disableAllButtonsExceptNext();
                checkAnswer(false);
            }
        });
    }

    private void setUpNextButton() {
        mNextButton = findViewById(R.id.next_button);
        mNextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCurrentIndex = (mCurrentIndex + 1) % mStatements.length;

                if (mCurrentIndex == 0) {
                    displayPercentage();
                }

                updateStatement();
                enableAllButtonsExceptNext();
            }
        });

        // Next button is initially disabled until user answers question
        mNextButton.setClickable(false);
    }

    private void setUpCheatButton() {
        mCheatButton = findViewById(R.id.cheat_button);
        mCheatButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean statementIsTrue = mStatements[mCurrentIndex].isTrue();
                // Read comment in CheatActivity
                Intent intent = CheatActivity.newIntent(MainActivity.this, statementIsTrue);
                startActivityForResult(intent, REQUEST_CODE_CHEAT);
            }
        });
    }

    private void updateStatement() {
        int statementResId = mStatements[mCurrentIndex].getTextResId();
        mStatementView.setText(statementResId);
    }

    private void checkAnswer(boolean userPressedTrue) {
        boolean statementIsTrue = mStatements[mCurrentIndex].isTrue();

        int messageResId;
        if (mUserCheated) {
            messageResId = R.string.judgment_toast;
            mUserCheated = false;
        } else {
            if (userPressedTrue == statementIsTrue) {
                messageResId = R.string.correct_toast;
            } else {
                messageResId = R.string.incorrect_toast;
            }
        }

        Toast.makeText(this, messageResId, Toast.LENGTH_SHORT).show();
    }

    private void disableAllButtonsExceptNext() {
        mTrueButton.setClickable(false);
        mFalseButton.setClickable(false);
        mCheatButton.setClickable(false);
        mNextButton.setClickable(true);
    }

    private void enableAllButtonsExceptNext() {
        mTrueButton.setClickable(true);
        mFalseButton.setClickable(true);
        mCheatButton.setClickable(true);
        mNextButton.setClickable(false);
    }

    private void displayPercentage() {
        Toast.makeText(this, String.format(Locale.getDefault(),
                "Percentage of correct answers: %.1f%%",
                100f * mNumCorrectAnswers / mStatements.length),
                Toast.LENGTH_SHORT).show();
    }

    // if CheatActivity returns ANY result w RESULT_OK, user cheated!
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_CODE_CHEAT && resultCode == RESULT_OK) {
            mUserCheated = true;
        }
    }
}
