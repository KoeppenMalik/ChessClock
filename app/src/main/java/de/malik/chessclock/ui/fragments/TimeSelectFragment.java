package de.malik.chessclock.ui.fragments;

import android.app.Dialog;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import de.malik.chessclock.R;
import de.malik.chessclock.utils.LifecycleManager;

public class TimeSelectFragment extends Fragment {

    private View mV;

    private EditText mEtTime;
    private Button mButtonStart;
    private Spinner mSpinner;

    private LifecycleManager mLcm;
    private AppCompatActivity mActivity;

    public TimeSelectFragment(LifecycleManager lcm, AppCompatActivity activity) {
        mLcm = lcm;
        mActivity = activity;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mV = inflater.inflate(R.layout.time_select_fragment, container, false);
        createComponents();
        setListeners();
        ArrayAdapter<String> adapter = new ArrayAdapter<>(container.getContext(), R.layout.spinner_item, new String[] {"Min", "Sec"});
        mSpinner.setAdapter(adapter);
        return mV;
    }

    private void createComponents() {
        mEtTime = mV.findViewById(R.id.etTime);
        mButtonStart = mV.findViewById(R.id.buttonStart);
        mSpinner = mV.findViewById(R.id.spinner);
    }

    private void setListeners() {
        mButtonStart.setOnClickListener(v -> {
            if (!checkTimeInput()) return;
            int in = Integer.parseInt(mEtTime.getText().toString());
            int millis;
            boolean minutes;
            if (mSpinner.getSelectedItemPosition() == 0) {
                millis = in * 60 * 1000;
                minutes = true;
            }
            else {
                millis = in * 1000;
                minutes = false;
            }
            showDialog(v, millis, minutes);
        });
    }

    private boolean checkTimeInput() {
        if (mEtTime.getText().toString().isEmpty()) {
            mEtTime.setError("Keine Eingabe");
            return false;
        }
        try {
            Integer.parseInt(mEtTime.getText().toString());
            return true;
        } catch (NumberFormatException ex) {
            mEtTime.setError("Nur ganze Zahlen");
            return false;
        }
    }

    private void showDialog(View v, long millis, boolean minutes) {
        Dialog dialog = new Dialog(v.getContext());
        dialog.setContentView(R.layout.time_selection_dialog);
        WindowManager.LayoutParams lp = dialog.getWindow().getAttributes();
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        dialog.getWindow().setAttributes(lp);

        Button buttonYes = dialog.findViewById(R.id.buttonYes),
                buttonNo = dialog.findViewById(R.id.buttonNo);

        buttonYes.setOnClickListener(view -> {
            mActivity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
            mLcm.replace(new ClockFragment(millis, minutes, true, mLcm, mActivity));
            dialog.dismiss();

        });
        buttonNo.setOnClickListener(view -> {
            mActivity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
            mLcm.replace(new ClockFragment(millis, minutes, false, mLcm, mActivity));
            dialog.dismiss();
        });
        dialog.create();
        dialog.show();
    }
}
