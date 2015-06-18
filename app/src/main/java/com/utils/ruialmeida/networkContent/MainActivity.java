package com.utils.ruialmeida.networkContent;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.telephony.CellLocation;
import android.telephony.TelephonyManager;
import android.telephony.gsm.GsmCellLocation;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.util.List;


public class MainActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        TextView lac = (TextView) findViewById(R.id.lac);
        TextView cellid = (TextView) findViewById(R.id.cellid);
        TextView mcc = (TextView) findViewById(R.id.mcc);
        TextView mnc = (TextView) findViewById(R.id.mnc);
        TextView operatorName = (TextView) findViewById(R.id.operatorName);
        TextView simOperatorInfo = (TextView) findViewById(R.id.simOperatorInfo);
        TextView networkOperatorName = (TextView) findViewById(R.id.networkOperatorInfo);
        TextView home = (TextView) findViewById(R.id.home);
        TextView net = (TextView) findViewById(R.id.net);
        TextView operatorIso = (TextView) findViewById(R.id.operatorIso);
        TextView coordinates = (TextView) findViewById(R.id.coordinates);
        TextView loc = (TextView) findViewById(R.id.location);
        TextView city = (TextView) findViewById(R.id.city);
        TextView country = (TextView) findViewById(R.id.country);

        TelephonyManager telephonyManager = (TelephonyManager) this.getSystemService(Context.TELEPHONY_SERVICE);

        CellLocation location = telephonyManager.getCellLocation();
        GsmCellLocation gsmLocation = (GsmCellLocation) location;
        int cellIdValue = gsmLocation.getCid();
        int lacValue = gsmLocation.getLac();



        String networkOperator = telephonyManager.getNetworkOperator();
        int mccValue = 0;
        int mncValue = 0;

        if(networkOperator != null && networkOperator.length() >= 3) {
             mccValue = Integer.parseInt(networkOperator.substring(0, 3));
             mncValue = Integer.parseInt(networkOperator.substring(3));
        }

        String operatorNameValue = telephonyManager.getSimOperatorName();
        String simOperatorInfoValue = telephonyManager.getSimOperator() + " (" + telephonyManager.getSimCountryIso() + ")";
        String networkOperatorNameValue = telephonyManager.getNetworkOperatorName();

        String homeValue = new String();
        if (telephonyManager.isNetworkRoaming() == true) {
            homeValue = "Roaming";
        } else {
            homeValue = "Home";
        }

        String netValue = network(telephonyManager);
        String networkCountryOperatorIso = telephonyManager.getNetworkCountryIso();

        GpsCoordinates gpsCoordinates = getCoordinates(this);
        LocationModel locationModel = getLocationByCoordinates(this, gpsCoordinates.Latitude, gpsCoordinates.Longitude);

        cellid.setText(cellIdValue + "");
        lac.setText(lacValue + "");
        mcc.setText(mccValue + "");
        mnc.setText(mncValue + "");
        operatorName.setText(operatorNameValue);
        simOperatorInfo.setText(simOperatorInfoValue);
        networkOperatorName.setText(networkOperatorNameValue);
        home.setText(homeValue);
        net.setText(telephonyManager.getNetworkType() + "- (" + netValue + ")");
        operatorIso.setText(networkCountryOperatorIso);
        coordinates.setText(gpsCoordinates.Latitude + " - " + gpsCoordinates.Longitude);
        loc.setText(locationModel.address);
        city.setText(locationModel.city);
        country.setText(locationModel.country);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    @Override
    public void onDestroy() {
        Toast.makeText(this, "End of The World", Toast.LENGTH_SHORT).show();
        super.onDestroy();

    }

    public String network(TelephonyManager tm) {
        String net = new String();

        switch (tm.getNetworkType()) {
            case TelephonyManager.NETWORK_TYPE_1xRTT:
                net = "CDMA - 2000";
                break;
            case TelephonyManager.NETWORK_TYPE_CDMA:
                net = "CDMA";
                break;
            case TelephonyManager.NETWORK_TYPE_EDGE:
                net = "GSM - EDGE";
                break;
            case TelephonyManager.NETWORK_TYPE_EVDO_0:
                net = "CDMA - EVDO A";
                break;
            case TelephonyManager.NETWORK_TYPE_EVDO_A:
                net = "CDMA - EVDO A";
                break;
            case TelephonyManager.NETWORK_TYPE_GPRS:
                net = "GSM - GPRS";
                break;
            case TelephonyManager.NETWORK_TYPE_UMTS:
                net = "UMTS";
                break;
            case TelephonyManager.NETWORK_TYPE_LTE:
                net = "LTE";
                break;
            case 8:
                net = "UMTS - HSDPA ";
                break;
            case 9:
                net = "UMTS - HSUPA ";
                break;
            case 10:
                net = "UMTS - HSPA ";
                break;
            case TelephonyManager.NETWORK_TYPE_UNKNOWN:
            default:
                net = "Unknown";
        }

        return net;
    }

    public static LocationModel getLocationByCoordinates(Context context, Double latitude, Double longitude) {
        try {
            Geocoder geocoder;
            List<Address> addresses;
            geocoder = new Geocoder(context);
            addresses = geocoder.getFromLocation(latitude, longitude, 1);

            LocationModel locationModel = new LocationModel();
            locationModel.latitude = latitude;
            locationModel.longitude = longitude;
            try {
                locationModel.address = addresses.get(0).getAddressLine(0);
            } catch (Exception ex) {

            }
            try {
                locationModel.city = addresses.get(0).getAddressLine(1);
            } catch (Exception ex) {
            }
            try {
                locationModel.country = addresses.get(0).getAddressLine(2);
            } catch (Exception ex) {
            }

            return locationModel;
        } catch (IOException e) {
            return new LocationModel();
        }
    }

    public GpsCoordinates getCoordinates(Context context) {
        GpsCoordinates gpsCoordinates = new GpsCoordinates();
        CurrentLocationListener currentLocation = new CurrentLocationListener(context);
        if (currentLocation.canGetLocation()) {
            Location location = currentLocation.getLocation();
            if (location != null) {
                gpsCoordinates.Longitude = location.getLongitude();
                gpsCoordinates.Latitude = location.getLatitude();
            }
        }
        return gpsCoordinates;
    }
}
