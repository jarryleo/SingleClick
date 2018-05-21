package cn.leo.singleclick;

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

public class MainActivity extends AppCompatActivity {

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
        /*mButton.setOnClickListener(new View.OnClickListener() {
            @Override
            @SingleClick(300)
            public void onClick(View v) {
                showLog();
            }
        });*/
    }

    @SingleClick(1000)
    public void testClick(View view) {
        showLog();
    }

    private void showLog() {
        Log.e("======", "showLog: " + System.currentTimeMillis());
    }

    @SingleClick(except = R.id.tv1)
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
        }
    }
}
