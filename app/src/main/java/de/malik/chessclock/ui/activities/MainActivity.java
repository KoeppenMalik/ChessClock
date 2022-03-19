package de.malik.chessclock.ui.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.WindowManager;

import de.malik.chessclock.R;
import de.malik.chessclock.ui.fragments.TimeSelectFragment;
import de.malik.chessclock.utils.LifecycleManager;

public class MainActivity extends AppCompatActivity {

    public static final int CONTAINER = R.id.containerLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        LifecycleManager lcm = new LifecycleManager(this);
        lcm.replace(new TimeSelectFragment(lcm, this));
    }

    @Override
    public void onBackPressed() {
    }
}