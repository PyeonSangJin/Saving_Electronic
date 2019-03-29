package com.example.saving_electricity;

import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;

import com.example.saving_electricity.botpager.BottomNavPagerAdapter;
import com.example.saving_electricity.botpager.Controller;
import com.example.saving_electricity.botpager.CustomViewPager;
import com.example.saving_electricity.botpager.DashBoard;
import com.example.saving_electricity.botpager.Home;

public class MainActivity extends AppCompatActivity {
    private BottomNavigationView bottomNavigationView;
    private CustomViewPager viewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bottom);

        viewPager = (CustomViewPager) findViewById(R.id.viewpager);
        viewPager.setPagingEnabled(false);
        setupViewPager(viewPager);
        bottomNavigationView = (BottomNavigationView) findViewById(R.id.navigation);


        bottomNavigationView.setOnNavigationItemSelectedListener(
                new BottomNavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                        switch (item.getItemId()) {
                            case R.id.menuitem_home:
                                viewPager.setCurrentItem(0);
                                return true;
                            case R.id.menuitem_dashboard:
                                viewPager.setCurrentItem(1);
                                return true;
                            case R.id.menuitem_controller:
                                viewPager.setCurrentItem(2);
                                return true;
                        }
                        return false;
                    }
                });
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                switch (position) {
                    case 0:
                        bottomNavigationView.setSelectedItemId(R.id.menuitem_home);
                        break;
                    case 1:
                        bottomNavigationView.setSelectedItemId(R.id.menuitem_dashboard);
                        break;
                    case 2:
                        bottomNavigationView.setSelectedItemId(R.id.menuitem_controller);
                        break;
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }
    private void setupViewPager (ViewPager viewPager){
        BottomNavPagerAdapter adapter = new BottomNavPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(new Home());
        adapter.addFragment(new DashBoard());
        adapter.addFragment(new Controller());
        viewPager.setAdapter(adapter);
    }
}