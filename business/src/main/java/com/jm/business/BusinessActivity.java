package com.jm.business;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.jm.annotation.Path;
import com.jm.library.RouteListener;
import com.jm.library.Router;

@Path("business/route")
public class BusinessActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_business);
        Intent intent = getIntent();
        String value = intent.getStringExtra("key");
        TextView textView = findViewById(R.id.text_view);
        textView.setText(value);
        Button button = findViewById(R.id.button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Router.get().build("app/route")
                        .setRouteListener(new RouteListener() {
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
                        }).navigation(BusinessActivity.this);
            }
        });
    }

    private void toast(String toast) {
        Toast.makeText(this, toast, Toast.LENGTH_SHORT).show();
    }
}
