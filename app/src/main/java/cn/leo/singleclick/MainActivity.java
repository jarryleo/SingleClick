package cn.leo.singleclick;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
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
    @BindView(R.id.listView)
    ListView mListView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        SingleClickManager.setClickInterval(1500);
        mButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showLog("button");
            }
        });
        mTv1.setOnClickListener(new MyClickListener());
        initListView();
    }

    private void initListView() {
        String[] strings = {"测试1", "测试2", "测试3", "测试4", "测试5",};
        mListView.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, strings));
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                showLog("onItemClick");
            }
        });
    }

    @SingleClick(1000)
    public void testClick(View view) {
        startActivity(new Intent(this, TestActivity.class));
        showLog("startActivity");
    }

    private void showLog(String msg) {
        Log.e("MainActivity[" + System.currentTimeMillis() + "]", "showLog: " + msg);

    }

    @Override
    public void onClick(View v) {
        showLog("tv");
    }


   /* @SingleClick(value = 1000, except = {R.id.tv1, R.id.button})
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
    }*/

    private static class MyClickListener implements View.OnClickListener {
        @SingleClick(2000)
        @Override
        public void onClick(View v) {
            Log.e("2222222", "onClick: ");
        }
    }
}
