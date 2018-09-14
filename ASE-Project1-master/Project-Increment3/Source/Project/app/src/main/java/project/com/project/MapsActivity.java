package project.com.project;

/**
 * Created by gangi on 3/20/2018.
 */

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.view.View;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_maps);

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Checking Permissions.
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[] {
                    android.Manifest.permission.ACCESS_FINE_LOCATION,
                    android.Manifest.permission.ACCESS_COARSE_LOCATION },1);
        }else{
            mMap.setMyLocationEnabled(true);
            mMap.getUiSettings().setMyLocationButtonEnabled(true);
        }

        LocationManager lm = (LocationManager)getSystemService(Context.LOCATION_SERVICE);
        Location myLocation = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER);

        // If Location is Null, Set Accuracy and set the Last Known Location using Criteria.
        if (myLocation == null) {
            System.out.println("myLocation is Null");
            Criteria criteria = new Criteria();
            criteria.setAccuracy(Criteria.ACCURACY_COARSE);
            String provider = lm.getBestProvider(criteria, true);
            myLocation = lm.getLastKnownLocation(provider);
        }

        if(myLocation!=null){
            // Latitude, Longitude setting in LatLng Class.
            LatLng userLocation = new LatLng(myLocation.getLatitude(), myLocation.getLongitude());

            // Creating Marker with userLocation and a Title.
            MarkerOptions currentLocationMarker = new MarkerOptions()
                    .position(userLocation)
                    .title("Your Address");

            // Always show the Marker by using Show Info Window
            mMap.addMarker(currentLocationMarker).showInfoWindow();
            // Zooming the Camera [Input Params: userLocation,width,height,Padding
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(userLocation, 14), 1500, null);

            // Four Square API to fetch Nearby Locations
            String fourSquareURL = "https://api.foursquare.com/v2/venues/search?client_id=XUE5OHFJNUDACMIFWVF5XMCUFUKPSWXTBBTSO1PHBFL0BGDK&" +
                    "client_secret=Z0RUZIJ4W5M3YLFHDQRL1VL2WQWDJHYSPJKXO2SKFALLHU0U&v=20180223" +
                    "&limit=10&query=supermarket&ll="+myLocation.getLatitude()+","+myLocation.getLongitude();
            OkHttpClient client = new OkHttpClient();
            try {
                Request request = new Request.Builder()
                        .url(fourSquareURL)
                        .build();
                client.newCall(request).enqueue(new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {
                        System.out.println(e.getMessage());
                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        final JSONObject jsonResult;
                        final String result = response.body().string();
                        try {
                            jsonResult = new JSONObject(result);
                            JSONObject responseObj = jsonResult.getJSONObject("response");
                            JSONArray venuesArray = responseObj.getJSONArray("venues");

                            for(int itr = 0;itr<venuesArray.length();itr++){
                                JSONObject venueObj = venuesArray.getJSONObject(itr);

                                String venueName = venueObj.getString("name");
                                double venueLat = venueObj.getJSONObject("location").getDouble("lat");
                                double venueLong = venueObj.getJSONObject("location").getDouble("lng");

                                System.out.println(venueName+","+venueLat+","+venueLong);

                                // Latitude, Longitude setting in LatLng Class.
                                LatLng userLocation = new LatLng(venueLat, venueLong);

                                // Creating Marker with userLocation and a Title.
                                final MarkerOptions currentLocationMarker = new MarkerOptions()
                                        .position(userLocation)
                                        .title(venueName);

                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        // Always show the Marker by using Show Info Window
                                        mMap.addMarker(currentLocationMarker);
                                    }
                                });
                            }
                        } catch (JSONException exception) {
                            exception.printStackTrace();
                        }
                    }
                });
            }catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }
}
