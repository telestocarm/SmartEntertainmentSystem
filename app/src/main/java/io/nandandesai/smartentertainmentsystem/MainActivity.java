package io.nandandesai.smartentertainmentsystem;

import android.annotation.SuppressLint;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.tabs.TabLayout;

import java.util.List;

import io.nandandesai.smartentertainmentsystem.fragments.MovieFragment;
import io.nandandesai.smartentertainmentsystem.utils.ErrorDialog;
import io.nandandesai.smartentertainmentsystem.viewadapters.SectionsPageAdapter;

public class MainActivity extends AppCompatActivity {


    private ViewPager mViewPager;
    private SearchView searchView;
    private boolean backButtonPressedOnce=false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);



        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.container);
        setupViewPager(mViewPager);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(mViewPager);
        tabLayout.getTabAt(0).setIcon(R.drawable.ic_movie_white_24dp);
        handleIntent(getIntent());
    }

    private void setupViewPager(ViewPager viewPager) {
        SectionsPageAdapter adapter = new SectionsPageAdapter(getSupportFragmentManager());
        adapter.addFragment(new MovieFragment(), "");
        viewPager.setAdapter(adapter);
    }

    @SuppressLint("RestrictedApi")
    private Fragment getCurrentFragment(){
        List<Fragment> fragments=getSupportFragmentManager().getFragments();
        for(Fragment fragment:fragments){
            if(fragment.isMenuVisible()){
                return fragment;
            }
        }
        return null;
    }

    /*
     * The section below is for 'searching' and 'settings' action
     * */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        SearchManager searchManager =
                (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        final SearchView searchView =
                (SearchView) menu.findItem(R.id.menu_search).getActionView();

        this.searchView=searchView;

        searchView.setOnCloseListener(new SearchView.OnCloseListener() {
            @Override
            public boolean onClose() {
                //Toast.makeText(getApplicationContext(),"Loading",Toast.LENGTH_SHORT).show();
                Fragment fragment=getCurrentFragment();
                ((MovieFragment) fragment).new FetchImagesTask(null).execute();
                return false;
            }
        });


        searchView.setOnSearchClickListener(new View.OnClickListener() {
            @SuppressLint("RestrictedApi")
            @Override
            public void onClick(View v) {
                Fragment fragment=getCurrentFragment();
                searchView.setQueryHint("Search movies...");
            }
        });
        searchView.setSearchableInfo(
                searchManager.getSearchableInfo(getComponentName()));


//        /*Configure settings MenuItem*/
//        MenuItem settingsItem=menu.findItem(R.id.action_settings);
//        settingsItem.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
//            @Override
//            public boolean onMenuItemClick(MenuItem item) {
//                Intent intent=new Intent(MainActivity.this, SettingsActivity.class);
//                startActivity(intent);
//                return false;
//            }
//        });

        /*Configure about MenuItem*/
        MenuItem aboutItem=menu.findItem(R.id.action_about);
        aboutItem.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                Intent intent=new Intent(MainActivity.this, AboutActivity.class);
                startActivity(intent);
                return false;
            }
        });

        return true;
    }


    @Override
    protected void onNewIntent(Intent intent) {
        handleIntent(intent);
    }

    @SuppressLint("RestrictedApi")
    private void handleIntent(Intent intent) {
        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            //Toast.makeText(getApplicationContext(),"Searching",Toast.LENGTH_SHORT).show();
            String searchQuery = intent.getStringExtra(SearchManager.QUERY);
            System.out.println("Search query: "+searchQuery);
            Fragment fragment=getCurrentFragment();
//            ((MovieFragment) fragment).new FetchImagesTask(searchQuery).execute();
            ErrorDialog.showDialog(this, "Search option is not supported yet.");
        }
    }

    @Override
    public void onBackPressed() {
        if(!searchView.isIconified()){
            searchView.onActionViewCollapsed();
            Fragment fragment=getCurrentFragment();
            ((MovieFragment) fragment).new FetchImagesTask(null).execute();
        }else{
            if (backButtonPressedOnce) {
                super.onBackPressed();
                return;
            }

            backButtonPressedOnce = true;
            Toast.makeText(this, "Press back again to quit", Toast.LENGTH_SHORT).show();

            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    backButtonPressedOnce=false;
                }
            }, 2000);
        }
    }
}