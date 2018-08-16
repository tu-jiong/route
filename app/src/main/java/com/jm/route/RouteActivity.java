package com.jm.route;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.jm.annotation.Path;

@Path(RoutePath.PATH)
public class RouteActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_route);
    }
}
