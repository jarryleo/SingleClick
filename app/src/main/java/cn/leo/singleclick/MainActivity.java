package cn.leo.singleclick;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.leo.click.SingleClick;
import cn.leo.click.SingleClickManager;
import cn.leo.test_library.TestActivity;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    @BindView(R.id.tv1)
    TextView mTv1;
    @BindView(R.id.button)
    Button mButton;
    @BindView(R.id.button2)
    Button mButton2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        SingleClickManager.setClickInterval(1500);
        mButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showLog();
            }
        });
        mTv1.setOnClickListener(new MyClickListener());
    }

    @SingleClick(1000)
    public void testClick(View view) {

        Log.e("startTestActivity======", "showLog: " + System.currentTimeMillis());
    }

    private void showLog() {
        Log.e("MainActivity======", "showLog: " + System.currentTimeMillis());
        startActivity(new Intent(this, TestActivity.class));
    }

    @Override
    public void onClick(View v) {
        showLog();
    }


    @SingleClick(value = 1000, except = {R.id.tv1, R.id.button})
    @OnClick({R.id.tv1, R.id.button, R.id.button2})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.tv1:
                showLog();
                break;
            case R.id.button:
                showLog();
                break;
            case R.id.button2:
                showLog();
                break;
            default:
                break;
        }
    }

    private static class MyClickListener implements View.OnClickListener {
        @SingleClick(2000)
        @Override
        public void onClick(View v) {
            Log.e("2222222", "onClick: ");
        }
    }
}
