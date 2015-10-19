package cesi.com.tchatapp;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

import cesi.com.tchatapp.fragment.MessagesFragment;
import cesi.com.tchatapp.fragment.UsersFragment;
import cesi.com.tchatapp.fragment.WriteMsgDialog;
import cesi.com.tchatapp.session.Session;

/**
 * Created by sca on 06/06/15.
 */
public class DrawerActivity extends AppCompatActivity {

    private DrawerLayout mDrawerLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_drawer);


/*        final ActionBar ab = getSupportActionBar();
        ab.setHomeAsUpIndicator(R.mipmap.ic_launcher);
        ab.setDisplayHomeAsUpEnabled(true);
*/

        ViewPager pager = (ViewPager) findViewById(R.id.viewpager);
        if(pager != null){
            Adapter adapter = new Adapter(getSupportFragmentManager());
            adapter.addFragment(new MessagesFragment(), "Messages");
            adapter.addFragment(new UsersFragment(), "Users");

            pager.setAdapter(adapter);
        }

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        if(null != tabLayout){
            tabLayout.setupWithViewPager(pager);
        }



        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        if (navigationView != null) {
            setupDrawerContent(navigationView);
        }



        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                WriteMsgDialog.getInstance(Session.token).show(DrawerActivity.this.getFragmentManager(), "write");

            }
        });


    }


    /**
     * setup drawer.
     * @param navigationView
     */
    private void setupDrawerContent(NavigationView navigationView) {
        navigationView.setNavigationItemSelectedListener(
                new NavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(MenuItem menuItem) {
                        if(menuItem.getItemId() == R.id.tchat_disconnect){
                            Session.token = null;
                            DrawerActivity.this.finish();
                        } else {
                            menuItem.setChecked(true);
                            mDrawerLayout.closeDrawers();
                        }
                        return true;
                    }
                });
    }


    static class Adapter extends FragmentPagerAdapter {

        private final List<Fragment> fragments = new ArrayList<>();
        private final List<String> fragmentTitle = new ArrayList<>();

        public Adapter(FragmentManager fm) {
            super(fm);
        }

        public void addFragment(Fragment fragment, String title){
            fragments.add(fragment);
            fragmentTitle.add(title);
        }

        @Override
        public Fragment getItem(int position) {
            return fragments.get(position);
        }

        @Override
        public int getCount() {
            return fragments.size();
        }

        @Override
        public CharSequence getPageTitle(int position){
            return fragmentTitle.get(position);
        }
    }
}
