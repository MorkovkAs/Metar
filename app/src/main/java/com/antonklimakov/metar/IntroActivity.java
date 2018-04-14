package com.antonklimakov.metar;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;

import com.antonklimakov.metar.fragments.SampleSlide;
import com.github.paolorotolo.appintro.AppIntro;

public class IntroActivity extends AppIntro {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        addSlide(SampleSlide.newInstance(R.layout.slide1));
        addSlide(SampleSlide.newInstance(R.layout.slide2));
        addSlide(SampleSlide.newInstance(R.layout.slide3));
        addSlide(SampleSlide.newInstance(R.layout.slide4));

        // OPTIONAL METHODS
        setBarColor(getResources().getColor(R.color.primary_dark));

        // Hide Skip/Done button.
        showSkipButton(false);
        setProgressButtonEnabled(true);
    }

    @Override
    public void onSkipPressed(Fragment currentFragment) {
        super.onSkipPressed(currentFragment);
        // Do something when users tap on Skip button.
        finish();
    }

    @Override
    public void onDonePressed(Fragment currentFragment) {
        super.onDonePressed(currentFragment);
        // Do something when users tap on Done button.
        finish();
    }

    @Override
    public void onSlideChanged(@Nullable Fragment oldFragment, @Nullable Fragment newFragment) {
        super.onSlideChanged(oldFragment, newFragment);
        // Do something when the slide changes.
    }
}