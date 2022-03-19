package de.malik.chessclock.ui.fragments;

import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import de.malik.chessclock.R;
import de.malik.chessclock.utils.LifecycleManager;
import de.malik.chessclock.utils.TimeThread;

public class ClockFragment extends Fragment {

    private View mV;

    private long mTime;
    private int mMin;
    private boolean isStarted = false, mTimeExtension, mMinutes;

    private TextView mTvTime, mTvTime1, mTvTime2;
    private ImageButton mButtonBack;
    private TimeThread mTime1Thread, mTime2Thread;
    private LifecycleManager mLcm;
    private AppCompatActivity mActivity;

    public ClockFragment(long millis, boolean minutes, boolean timeExtension, LifecycleManager lcm, AppCompatActivity activity) {
        mLcm = lcm;
        mMinutes = minutes;
        mTimeExtension = timeExtension;
        mActivity = activity;
        mTime = millis;
        mMin = (int) millis / 60000;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mV = inflater.inflate(R.layout.clock_fragment, container, false);
        createComponents();
        setListeners();
        int[] timeValues = getTimeValues();
        String suffix;
        int totalTime;
        if (mMinutes) {
            suffix = "Min";
            totalTime = timeValues[0];
        }
        else {
            suffix = "Sec";
            totalTime = timeValues[1];
        }
        mTime1Thread = new TimeThread(mTvTime1, mTvTime2, mLcm, mActivity, mTime, 1, mTimeExtension);
        mTime2Thread = new TimeThread(mTvTime2, mTvTime1, mLcm, mActivity, mTime, 2, mTimeExtension);
        mTvTime.setText(totalTime + " " + suffix);
        mTvTime1.setText(timeValues[0] + " : " + timeValues[1]);
        mTvTime2.setText(timeValues[0] + " : " + timeValues[1]);
        return mV;
    }

    private int[] getTimeValues() {
        if (mMinutes) {
            return new int[] {(int) mTime / 60000, 0};
        }
        return new int[] {0, (int) mTime / 1000};
    }

    private void createComponents() {
        mTvTime = mV.findViewById(R.id.tvTime);
        mTvTime1 = mV.findViewById(R.id.tvTime1);
        mTvTime2 = mV.findViewById(R.id.tvTime2);
        mButtonBack = mV.findViewById(R.id.buttonBack);
    }

    private void setListeners() {
        mButtonBack.setOnClickListener(v -> {
            mTime1Thread.stop();
            mTime2Thread.stop();
            mActivity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
            mLcm.replace(new TimeSelectFragment(mLcm, mActivity));
        });
        mTvTime1.setOnClickListener(v -> {
            if (!isStarted) {
                mTime1Thread.start();
                isStarted = true;
                return;
            }
            mTime1Thread.stop();
            mTime2Thread.start();
        });
        mTvTime2.setOnClickListener(v -> {
            if (!isStarted) {
                mTime2Thread.start();
                isStarted = true;
                return;
            }
            mTime2Thread.stop();
            mTime1Thread.start();
        });
    }
}
