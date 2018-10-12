package cn.leo.test_library;

import android.os.Bundle;
import android.os.SystemClock;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;

import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.leo.click.SingleClick;

/**
 * @author Leo
 */
public class TestActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);
        ButterKnife.bind(this);
    }


    private void showLog() {
        Log.e("TestActivity", "showLog: " + SystemClock.currentThreadTimeMillis());
    }

    @SingleClick(value = 1500, exceptIdName = {"testBtn2"})
    @OnClick({R2.id.testBtn1, R2.id.testBtn2})
    public void onViewClicked(View view) {

        int id = view.getId();
        if (id == R.id.testBtn1) {
            showLog();
        } else if (id == R.id.testBtn2) {
            showLog();
        }
    }
}
