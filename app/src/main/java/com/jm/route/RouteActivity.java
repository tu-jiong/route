package com.jm.route;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.jm.annotation.Path;
import com.jm.base.Scheme;
import com.jm.library.Interceptor;
import com.jm.library.Meta;
import com.jm.library.RouteListener;
import com.jm.library.Router;

@Path(Scheme.ROUTE_ACTIVITY)
public class RouteActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_route);
        setTitle("RouteActivity");
        Button button = findViewById(R.id.button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Meta meta = Router.get().build(Scheme.BUSINESS_ACTIVITY);
                meta.addInterceptor(new Interceptor() {
                    @Override
                    public boolean intercept() {
                        return true;
                    }
                }).setRouteListener(new RouteListener() {
                    @Override
                    public void onIntercepted() {
                        toast("被拦截 : " + meta.getPath());
                    }

                    @Override
                    public void onComplete() {
                        toast("跳转成功");
                    }

                    @Override
                    public void onError(Throwable throwable) {
                        toast("出错了");
                    }
                }).navigation(RouteActivity.this);
            }
        });
    }

    private void toast(String toast) {
        Toast.makeText(this, toast, Toast.LENGTH_SHORT).show();
    }
}
