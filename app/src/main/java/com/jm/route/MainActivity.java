package com.jm.route;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.jm.base.Scheme;
import com.jm.library.RouteListener;
import com.jm.library.Router;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setTitle("MainActivity");
        Button button = findViewById(R.id.button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle bundle = new Bundle();
                bundle.putString("gender", "男");
                Router.get().build(Scheme.BUSINESS_ACTIVITY + "?name=张三&age=23").setBundle(bundle).setRouteListener(new RouteListener() {
                    @Override
                    public void onIntercepted() {
                        toast("被拦截");
                    }

                    @Override
                    public void onComplete() {
                        toast("跳转成功");
                    }

                    @Override
                    public void onError(Throwable throwable) {
                        toast("出错了");
                    }
                }).navigation(MainActivity.this);
            }
        });
    }

    private void toast(String toast) {
        Toast.makeText(this, toast, Toast.LENGTH_SHORT).show();
    }
}
