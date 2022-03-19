package de.malik.chessclock.utils;

import android.app.Dialog;
import android.content.pm.ActivityInfo;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.Date;

import de.malik.chessclock.R;
import de.malik.chessclock.ui.fragments.TimeSelectFragment;
import de.malik.mylibrary.managers.TimeManager;

public class TimeThread implements Runnable {

    private Thread mThread;
    private TextView mTv, mOtherTv;
    private LifecycleManager mLcm;
    private AppCompatActivity mActivity;

    private long mMillis;
    private int mNum;
    private boolean mIsRunning, mTimeExt;

    public TimeThread(TextView tv, TextView otherTv, LifecycleManager lcm, AppCompatActivity activity, long totalMillis, int num, boolean timeExt) {
        mTimeExt = timeExt;
        mTv = tv;
        mOtherTv = otherTv;
        mActivity = activity;
        mLcm = lcm;
        mNum = num;
        mMillis = totalMillis;
    }

    public void start() {
        if (!mIsRunning) {
            mThread = new Thread(this);
            mThread.start();
        }
        mIsRunning = true;
    }

    public void stop() {
        if (mIsRunning && mTimeExt && mMillis < 10000) {
            mMillis += 3000;
            String timeString = TimeManager.toTimeString(new Date(mMillis), true);
            long[] values = TimeManager.timeParts(timeString);
            mTv.setText(values[1] + " : " + values[2]);
        }
        if (mIsRunning)
            mThread.interrupt();
        mIsRunning = false;
    }

    public void join() {
        try {
            mThread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        for (;;) {
            if (!mIsRunning || mMillis <= 0) {
                break;
            }
            mMillis -= 250;
            String timeString = TimeManager.toTimeString(new Date(mMillis), true);
            long[] values = TimeManager.timeParts(timeString);
            mTv.setText(values[1] + " : " + values[2]);
            try {
                Thread.sleep(250);
            } catch (InterruptedException ex) {
                ex.printStackTrace();
            }
        }
        if (mMillis <= 0) {
            showDialog();
        }
        stop();
    }

    private void showDialog() {
        mActivity.runOnUiThread(() -> {
            Dialog dialog = new Dialog(mOtherTv.getContext());
            dialog.setCancelable(false);
            dialog.setContentView(R.layout.dialog_layout);
            WindowManager.LayoutParams lp = dialog.getWindow().getAttributes();
            lp.width = WindowManager.LayoutParams.MATCH_PARENT;
            lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
            dialog.getWindow().setAttributes(lp);

            TextView tvInfo = dialog.findViewById(R.id.tvInfo);
            Button buttonOkay = dialog.findViewById(R.id.buttonOkay);

            tvInfo.setText("Die Spielzeit von Spieler " + mNum + " ist vorbei\n" +
                    "Spieler " + getOtherPlayer(mNum) + " hatte noch " + mOtherTv.getText());
            buttonOkay.setOnClickListener(v -> {
                dialog.dismiss();
                stop();
                try {
                    mThread.join();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                mActivity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
                mLcm.replace(new TimeSelectFragment(mLcm, mActivity));
            });
            dialog.create();
            dialog.show();
        });
    }

    private int getOtherPlayer(int player) {
        return 3 - player;
    }
}
