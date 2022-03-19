package de.malik.chessclock.utils;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import de.malik.chessclock.ui.activities.MainActivity;

public class LifecycleManager {

    private AppCompatActivity mActivity;

    public LifecycleManager(AppCompatActivity activity) {
        mActivity = activity;
    }

    public void replace(Fragment newFragment) {
        mActivity.getSupportFragmentManager().beginTransaction()
                .setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out)
                .replace(MainActivity.CONTAINER, newFragment)
                .commit();
    }
}
