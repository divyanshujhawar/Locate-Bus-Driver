package com.example.dell.buslocator;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.IOException;
import java.util.List;

import static android.R.attr.id;
import static android.content.Context.MODE_PRIVATE;

public class MapsActivity extends AppCompatActivity implements OnMapReadyCallback, LocationListener {

    private GoogleMap mMap;
    LocationManager locationManager;
    List<Address> addressList;
    Marker marker;
    String email;
    DatabaseReference database;
    String name;
    String contact;
    DatabaseReference databaseLocation;
    private FirebaseAuth mAuth;
    int flag;
    FirebaseUser currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        mAuth = FirebaseAuth.getInstance();

        SharedPreferences sh = getSharedPreferences("user",MODE_PRIVATE);
        name = sh.getString("name",null);
        contact = sh.getString("contact",null);

        currentUser = mAuth.getCurrentUser();


        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }

        flag = 0;

        //Check whether the network provider is enabled
        if(locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, new LocationListener() {
                @Override
                public void onLocationChanged(Location location) {
                    final double latitude = location.getLatitude();
                    final double longitude =  location.getLongitude();

                    LatLng latLng = new LatLng(latitude,longitude);

                    Geocoder geocoder = new Geocoder(getApplicationContext());
                    try {
                        List<Address> addressList = geocoder.getFromLocation(latitude,longitude,1);
                        String str = addressList.get(0).getLocality() + ", ";
                        str += addressList.get(0).getCountryName();
                        Log.i("Coordinates:", latitude + " " + longitude);
                        if(marker == null) {
                            marker = mMap.addMarker(new MarkerOptions().position(latLng).title(str));
                            mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
                        }
                        else{
                            marker.setPosition(latLng);
                        }
                        databaseLocation = FirebaseDatabase.getInstance().getReference("Location");
                        databaseLocation.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                for(DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                    Locate loc = snapshot.getValue(Locate.class);
                                    if(loc.getEmail().equals(currentUser.getEmail())){
                                        database = databaseLocation.child(loc.getId());
                                        loc.latitude = latitude;
                                        loc.longitude = longitude;
                                        database.setValue(loc);
                                        flag = 1;
                                    }
                                }

                                if(flag == 0) {
                                    String id = databaseLocation.push().getKey();
                                    Locate locat = new Locate(id, latitude, longitude, name, contact, currentUser.getEmail());
                                    databaseLocation.child(id).setValue(locat);
                                }
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });


                    } catch (IOException e) {
                        e.printStackTrace();
                    }


                }

                @Override
                public void onStatusChanged(String provider, int status, Bundle extras) {

                }

                @Override
                public void onProviderEnabled(String provider) {

                }

                @Override
                public void onProviderDisabled(String provider) {

                }
            });
        }
        else if(locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)){
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, new LocationListener() {
                @Override
                public void onLocationChanged(Location location) {
                    double latitude = location.getLatitude();
                    double longitude =  location.getLongitude();

                    LatLng latLng = new LatLng(latitude,longitude);

                    Geocoder geocoder = new Geocoder(getApplicationContext());
                    try {
                        addressList.clear();
                        addressList = geocoder.getFromLocation(latitude,longitude,1);
                        String str = addressList.get(0).getLocality() + ", ";
                        str += addressList.get(0).getCountryName();
                        mMap.addMarker(new MarkerOptions().position(latLng).title(str));
                        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 10.2f));
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onStatusChanged(String provider, int status, Bundle extras) {

                }

                @Override
                public void onProviderEnabled(String provider) {

                }

                @Override
                public void onProviderDisabled(String provider) {

                }
            });
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.toolbar_menu,menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        if (id == R.id.signOut) {
            mAuth.signOut();
            finish();
            // Login.login_status=0;
            startActivity(new Intent(this,LogInActivity.class));
        }
        return super.onOptionsItemSelected(item);
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Add a marker in Sydney and move the camera
        //LatLng sydney = new LatLng(-34, 151);
        //mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
        //mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
    }

    @Override
    public void onLocationChanged(Location location) {

    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }
}
