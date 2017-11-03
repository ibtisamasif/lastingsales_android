package com.example.muzafarimran.lastingsales.home;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.example.muzafarimran.lastingsales.R;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;


public class MainActivityR extends AppCompatActivity {
    private static final String TAG = "RecyclerViewExample";

    private List<Object> list = new ArrayList<Object>();
    private RecyclerView mRecyclerView;
    private MyRecyclerViewAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_recycler);

        mRecyclerView = findViewById(R.id.recycler_view);

        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        parseResult();

        adapter = new MyRecyclerViewAdapter(MainActivityR.this, list);
        mRecyclerView.setAdapter(adapter);
    }

    private void parseResult() {

        for (int i = 0; i < 16; i++) {

            int randomNum = ThreadLocalRandom.current().nextInt(1, 2 + 1);

            if (randomNum == 1) {
                Article a = new Article();
                a.a = "xyz";
                a.b = "yy";
                list.add(a);
            } else {
                Contact c = new Contact();
                c.a = 1;
                c.b = 2;
                list.add(c);
            }
        }
    }

    public class Article {
        public String a;
        public String b;
    }

    public class Contact {
        public int a;
        public int b;
    }

}