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
import android.support.v4.app.FragmentManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
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


public class HomeActivity extends AppCompatActivity {

    private static final int MY_PERMISSIONS_REQUEST_LOCATION = 0;
    private TextView mStatusTextView;
    private DrawerLayout mDrawerLayout;
    private GoogleSignInClient mGoogleSignInClient;
    private GoogleSignInAccount mGoogleSignInAccount;
    private Location currentLocation;

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
        mGoogleSignInAccount = intent.getParcelableExtra("profile");

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
        //----Setting the Nav Header Text to  "Welcome Back {name}" -----------------------//
        NavigationView navigationView = findViewById(R.id.nav_view);
        View headerView = navigationView.getHeaderView(0);
        TextView welcomeText = headerView.findViewById(R.id.nav_header_name);
        welcomeText.setText(mGoogleSignInAccount.getGivenName() +"!");

        //----------SETTING FRAGMENT TO HOME---------------------------//
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
                            Toast.makeText(HomeActivity.this, "Home",Toast.LENGTH_SHORT).show();
                            toolbar.setTitle("Home");
                            fragmentClass = HomeFragment.class;
                        } else if(id == R.id.classroom) {
                            Toast.makeText(HomeActivity.this, "Classroom",Toast.LENGTH_SHORT).show();
                            toolbar.setTitle("Enter a Classroom");
                            fragmentClass = ClassroomFragment.class;
                            bundle.putParcelable("location", currentLocation);
                        } else if (id == R.id.register) {
                            Toast.makeText(HomeActivity.this, "Register",Toast.LENGTH_SHORT).show();
                            fragmentClass = RegisterFragment.class;
                            toolbar.setTitle("Register");
                        } else if(id == R.id.logout) {
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
                        try {
                            fragment = (Fragment) fragmentClass.newInstance();

                            fragment.setArguments(bundle);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        FragmentManager fragmentManager = getSupportFragmentManager();
                        fragmentManager.beginTransaction().replace(R.id.flContent, fragment).commit();

                        menuItem.setChecked(true);


                        return true;
                    }
                });
        LocationManager locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        LocationListener locationListener = new LocationListener() {
            public void onLocationChanged(Location location) {
                // Called when a new location is found by the network location provider.
                //makeUseOfNewLocation(location);
                Toast.makeText(HomeActivity.this, "Hello",Toast.LENGTH_SHORT).show();
                currentLocation = location;
            }

            public void onStatusChanged(String provider, int status, Bundle extras) {}

            public void onProviderEnabled(String provider) {}

            public void onProviderDisabled(String provider) {}
        };
        try {
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
            //locationManager.requestSingleUpdate(LocationManager.NETWORK_PROVIDER, locationListener, null);
        } catch(SecurityException e){
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    MY_PERMISSIONS_REQUEST_LOCATION);
        }


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
