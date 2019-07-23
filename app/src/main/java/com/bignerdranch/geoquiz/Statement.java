package com.bignerdranch.geoquiz;

class Statement {

    private final int mTextResId;
    private final boolean mTrue;

    Statement(int textResId, boolean isTrue) {
        mTextResId = textResId;
        mTrue = isTrue;
    }

    int getTextResId() {
        return mTextResId;
    }

    boolean isTrue() {
        return mTrue;
    }
}
