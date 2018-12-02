package com.example.andrew.uscask;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.support.v7.widget.Toolbar;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;

import org.w3c.dom.Text;


public class HomeActivity extends AppCompatActivity
         {


    private TextView mStatusTextView;
    private DrawerLayout mDrawerLayout;
    private GoogleSignInClient mGoogleSignInClient;
    private GoogleSignInAccount mGoogleSignInAccount;
    private Boolean isGuest;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        //-------SETTING UP TOOLBAR-----------------//
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionbar = getSupportActionBar();
        actionbar.setDisplayHomeAsUpEnabled(true);
        actionbar.setHomeAsUpIndicator(R.drawable.ic_action_menu);

       // mStatusTextView = findViewById(R.id.status);
        mDrawerLayout = findViewById(R.id.drawer_layout);
        Intent intent = getIntent();
        int guest = intent.getIntExtra("guest", 1);
        NavigationView navigationView = findViewById(R.id.nav_view);
        if(guest == 1) {
            isGuest = true;
            //----Setting the Nav Header Text to  "Welcome Guest" -----------------------//
            View headerView = navigationView.getHeaderView(0);
            TextView welcomeText = headerView.findViewById(R.id.nav_header_name);
            welcomeText.setText("Guest!");
            //----------disabling menu buttons-------//
            Menu menuNav=navigationView.getMenu();
            MenuItem enterClassroom = menuNav.findItem(R.id.classroom);
            enterClassroom.setEnabled(false);
            MenuItem attendanceButton = menuNav.findItem(R.id.attendance);
            attendanceButton.setEnabled(false);
        } else {
            isGuest = false;
            mGoogleSignInAccount = intent.getParcelableExtra("profile");

            GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                    .requestEmail()
                    .build();
            mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
            //----Setting the Nav Header Text to  "Welcome Back {name}" -----------------------//
            navigationView = findViewById(R.id.nav_view);
            View headerView = navigationView.getHeaderView(0);
            TextView welcomeText = headerView.findViewById(R.id.nav_header_name);
            welcomeText.setText(mGoogleSignInAccount.getGivenName() +"!");
        }

        //----------SETTING FRAGMENT TO HOME---------------------------//
        TextView toolbarTitle = toolbar.findViewById(R.id.toolbar_title);
        toolbarTitle.setText("Profile");
        Fragment fragment = null;
        Class fragmentClass = HomeFragment.class;
        Bundle bundle = new Bundle();
        bundle.putParcelable("profile", mGoogleSignInAccount);
        try {
            fragment = (Fragment) fragmentClass.newInstance();
            fragment.setArguments(bundle);
        } catch (Exception e) {
            e.printStackTrace();
        }
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.flContent, fragment).commit();
        //--------------------Setting up the menu navigation------------------------//
        navigationView.setNavigationItemSelectedListener(
                new NavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(MenuItem menuItem) {
                        int id = menuItem.getItemId();
                        Fragment fragment = null;
                        Class fragmentClass = HomeFragment.class;
                        Bundle bundle = new Bundle();
                        bundle.putParcelable("profile", mGoogleSignInAccount);
                        Toolbar toolbar = findViewById(R.id.toolbar);
                        if(id == R.id.home){
                            TextView toolbarTitle = toolbar.findViewById(R.id.toolbar_title);
                            toolbarTitle.setText("Profile");
                            fragmentClass = HomeFragment.class;
                        } else if(id == R.id.classroom) {
                            fragmentClass = ClassroomFragment.class;
                            TextView toolbarTitle = toolbar.findViewById(R.id.toolbar_title);
                            toolbarTitle.setText("Enter Classroom");
                        } else if (id == R.id.register) {
                            fragmentClass = RegisterFragment.class;
                            TextView toolbarTitle = toolbar.findViewById(R.id.toolbar_title);
                            toolbarTitle.setText("Register");
                            if(isGuest) {
                                bundle.putInt("guest", 1);
                            }
                        } else if (id == R.id.attendance) {
                            fragmentClass = Attendance.class;
                            TextView toolbarTitle = toolbar.findViewById(R.id.toolbar_title);
                            toolbarTitle.setText("Attendance History");
                        } else if(id == R.id.logout) {
                            if(isGuest) {
                                Intent intent = new Intent(HomeActivity.this, MainActivity.class);
                                HomeActivity.this.startActivity(intent);
                            } else {
                                mGoogleSignInClient.signOut()
                                        .addOnCompleteListener(HomeActivity.this, new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                // [START_EXCLUDE]
                                                logout();
                                                // [END_EXCLUDE]
                                            }
                                        });
                            }
                        }
                        try {
                            fragment = (Fragment) fragmentClass.newInstance();

                            fragment.setArguments(bundle);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        FragmentManager fragmentManager = getSupportFragmentManager();
                        fragmentManager.beginTransaction().replace(R.id.flContent, fragment).commit();

                        menuItem.setChecked(true);

                        mDrawerLayout.closeDrawers();
                        return true;
                    }
                });


    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                mDrawerLayout.openDrawer(GravityCompat.START);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void logout() {
        Intent mainIntent = new Intent(this, MainActivity.class);
        HomeActivity.this.startActivity(mainIntent);
    }

}
