package com.example.muzafarimran.lastingsales.activities;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.example.muzafarimran.lastingsales.R;

public class TutorialScreenActivity extends AppCompatActivity {



    ViewPager viewPager;

    int []layout;


    Button skip,next;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_tutorial_screen );

        viewPager=findViewById( R.id.tutorial_view_pager);

        layout=new int[]{
            R.layout.tutorial_screen1,
            R.layout.tutorial_screen2,
            R.layout.tutorial_screen3

        };

        TutorialAdapter adapter=new TutorialAdapter();

        viewPager.setAdapter( adapter );

        skip=findViewById(R.id.btn_skip );
        next=findViewById(R.id.btn_next );


        skip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                launchHomeScreen();
            }
        });

        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // checking for last page
                // if last page home screen will be launched
                int current = getItem(+1);
                if (current < layout.length) {
                    // move to next screen
                    viewPager.setCurrentItem(current);
                } else {
                    launchHomeScreen();
                }
            }
        });





    }

    private int getItem(int i) {

    return viewPager.getCurrentItem()+i;
    }

    private void launchHomeScreen() {
        startActivity(new Intent( this,NavigationBottomMainActivity.class ));
    }


    public class TutorialAdapter extends PagerAdapter {


        @NonNull
        @Override
        public Object instantiateItem(@NonNull ViewGroup container, int position) {


            LayoutInflater layoutInflater=LayoutInflater.from( getApplicationContext() );
            ViewGroup viewGroup=(ViewGroup) layoutInflater.inflate( layout[position],container,false );


            

            container.addView( viewGroup );
            return viewGroup;
        }

        @Override
        public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
            // super.destroyItem( container, position, object );

            container.removeView( (View)object );

        }

        @Override
        public int getCount() {
            return layout.length;
        }

        @Override
        public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
            return view==object;
        }
    }






}
