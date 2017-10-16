package com.taskail.mixion;

import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;

import com.taskail.mixion.adapters.SectionsStatePagerAdapter;
import com.taskail.mixion.adapters.TabPagerAdapter;
import com.taskail.mixion.fragments.DTubeFragment;
import com.taskail.mixion.fragments.FeedFragment;
import com.taskail.mixion.fragments.AskSteemFragment;
import com.taskail.mixion.fragments.ProfileFragment;
import com.taskail.mixion.utils.BottomNavigationViewHelper;
import com.taskail.mixion.utils.FragmentLifecycle;
import com.taskail.mixion.utils.LockableViewPager;

public class MainActivity extends AppCompatActivity {

    private LockableViewPager viewPager;
    //private TabPagerAdapter pagerAdapter;
    private SectionsStatePagerAdapter pagerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        viewPager = findViewById(R.id.container);
        pagerAdapter = new SectionsStatePagerAdapter(getSupportFragmentManager());

        setViewPager();
        startBottomNavView();

    }

    private void setViewPager(){

        pagerAdapter.addFragment(new FeedFragment(), "FeedFragment");
        pagerAdapter.addFragment(new AskSteemFragment(), "AskSteem");
        pagerAdapter.addFragment(new DTubeFragment(), "Dtube");
        pagerAdapter.addFragment(new ProfileFragment(), "Profile");

        viewPager.setAdapter(pagerAdapter);
        viewPager.setCurrentItem(0);
        viewPager.setSwipeable(false);


        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {

            int currentPosition = 0;

            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int newPosition) {

                FragmentLifecycle fragmentToHide = (FragmentLifecycle) pagerAdapter.getItem(currentPosition);
                fragmentToHide.onPauseFragment();

                FragmentLifecycle fragmentToShow = (FragmentLifecycle) pagerAdapter.getItem(newPosition);
                fragmentToShow.onResumeFragment();

                currentPosition = newPosition;

            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    private void startBottomNavView(){
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
        BottomNavigationViewHelper.disableShiftMode(bottomNavigationView);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                item.setChecked(true);
                switch (item.getItemId()) {
                    case R.id.feed_icon:
                        viewPager.setCurrentItem(0, true);
                        break;
                    case R.id.asksteem_icon:
                        viewPager.setCurrentItem(1, true);
                        break;
                    case R.id.dtube_icon:
                        viewPager.setCurrentItem(2);
                        break;
                    case R.id.profile_icon:
                        viewPager.setCurrentItem(3, true);
                        break;
                }

                return false;
            }
        });

    }
}
