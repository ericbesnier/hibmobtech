package com.hibernatus.hibmobtech.contact;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.OnStreetViewPanoramaReadyCallback;
import com.google.android.gms.maps.StreetViewPanorama;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.hibernatus.hibmobtech.HibmobtechApplication;
import com.hibernatus.hibmobtech.HibmobtechOptionsMenuActivity;
import com.hibernatus.hibmobtech.R;
import com.hibernatus.hibmobtech.model.LatLng;
import com.hibernatus.hibmobtech.model.Site;
import com.hibernatus.hibmobtech.model.contact.Contact;
import com.hibernatus.hibmobtech.model.contact.Email;
import com.hibernatus.hibmobtech.model.contact.Phone;
import com.hibernatus.hibmobtech.site.SiteCurrent;
import com.hibernatus.hibmobtech.view.WrapListView;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

// C o n t a c t   A c t i v i t y
//
public class ContactActivity extends HibmobtechOptionsMenuActivity implements OnMapReadyCallback, OnStreetViewPanoramaReadyCallback, GoogleMap.OnMarkerClickListener {
    public static final String TAG = "ContactActivity";
    public static final int ZOOM_LEVEL = 18;

    protected Site site;
    protected Contact contact;
    protected MapFragment mapFragment;
    protected CollapsingToolbarLayout collapsingToolbar;
    protected TextView textViewAdress1;
    protected TextView textViewAdress2;
    protected WrapListView contactPhoneWrapListView;
    protected WrapListView contactEmailWrapListView;
    protected TextView contactRoleTextView;

    // @ O v e r r i d e   m e t h o d s
    // ---------------------------------
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        site = SiteCurrent.getInstance().getSiteCurrent();
        contact = ContactCurrent.getInstance().getContactCurrent();
        Log.d(TAG, "onCreate: currentSite=" + site + " contact.getName()=" + contact.getName());

        setContentView(R.layout.contact_activity);

        // MapFragment
        mapFragment = (MapFragment) getFragmentManager().findFragmentById(R.id.contactActivityMap);
        mapFragment.getMapAsync(this);

        findViews();
        setViews();

        initToolBar();
        actionBar.setDefaultDisplayHomeAsUpEnabled(true);
        initDrawer();

        // phone
        final List<Phone> phones = ContactCurrent.getInstance().getContactCurrent().getPhones();
        ArrayList<Phone> phoneArrayList = new ArrayList<>(phones);
        ArrayAdapterPhone adapterPhone = new ArrayAdapterPhone(this, phoneArrayList);
        contactPhoneWrapListView.setAdapter(adapterPhone);

        // mail
        final List<Email> emails = ContactCurrent.getInstance().getContactCurrent().getEmails();
        ArrayList<Email> emailArrayList = new ArrayList<>(emails);
        ArrayAdapterEmail adapterEmail = new ArrayAdapterEmail(this, emailArrayList);

        contactEmailWrapListView.setAdapter(adapterEmail);
        contactPhoneWrapListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                TextView listText = (TextView) view.findViewById(R.id.contactPhoneListRowNumberTextView);
                String phoneNumber = listText.getText().toString();
                Intent callIntent = new Intent(Intent.ACTION_DIAL);
                Log.v(TAG, "buttonPhone.setOnClickListener: phoneNumber=" + phoneNumber);
                callIntent.setData(Uri.parse("tel:" + Uri.encode(phoneNumber.trim())));
                callIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(callIntent);
            }
        });

        contactEmailWrapListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                TextView listText = (TextView) view.findViewById(R.id.contactEmailListRowAdressTextView);
                String emailAdress = listText.getText().toString();
                Intent intent = new Intent(Intent.ACTION_SENDTO);
                Log.v(TAG, "buttonEmail.setOnClickListener: emailAdress=" + emailAdress);
                intent.setType("text/plain");
                intent.setData(Uri.parse("mailto:" + emailAdress));
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }
        });
    }

    @Override
    public void onStreetViewPanoramaReady(StreetViewPanorama panorama) {
        LatLng geoloc = site.getGeoloc();
        if (site.getGeoloc() == null || site.getGeoloc().getLat() == 0 || site.getGeoloc().getLng() == 0){
            Log.d(TAG, "onMapReady: siteCurrent.getGeoloc() == null, let's call geocoder !");
            try {
                site.setGeoloc(getApplicationContext());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        com.google.android.gms.maps.model.LatLng latLng
                = new com.google.android.gms.maps.model.LatLng(geoloc.getLat(), geoloc.getLng());
        panorama.setPosition(latLng);
    }

    @Override
    public void onMapReady(GoogleMap map) {
        LatLng geoloc = site.getGeoloc();
        if (geoloc == null){
            Log.d(TAG, "onMapReady: siteCurrent.getGeoloc() == null, let's call geocoder !");
            try {
                site.setGeoloc(getApplicationContext());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        if (geoloc != null) {
            if (geoloc.getLat() == 0 || geoloc.getLng() == 0) {
                Log.d(TAG, "onMapReady: siteCurrent.getGeoloc() == null, let's call geocoder !");
                try {
                    site.setGeoloc(getApplicationContext());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            com.google.android.gms.maps.model.LatLng latLng
                    = new com.google.android.gms.maps.model.LatLng(geoloc.getLat(), geoloc.getLng());
            Log.d(TAG, "onMapReady: latLng.latitude=" + latLng.latitude + " latLng.longitude=" + latLng.longitude);

            Marker marker = map.addMarker(new MarkerOptions()
                    .position(latLng)
                    .title(site.getName()));
            marker.showInfoWindow();
            map.setOnMarkerClickListener(this);
            map.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
            map.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, ZOOM_LEVEL));
        }
        else {
            Log.e(TAG, "onMapReady: geoloc == null !");
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        trackScreen(TAG);
    }


    @Override
    public boolean onCreateOptionsMenu ( Menu menu ) {
        Log.i(TAG, "onCreateOptionsMenu");
        super.onCreateOptionsMenu(menu);
        MenuItem mainMenuItemCheck = menu.findItem(R.id.mainMenuCheckItem);
        mainMenuItemCheck.setVisible(false);
        return true;
    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        return false;
    }


    // O t h e r s   m e t h o d s
    // ---------------------------
    public void findViews() {
        collapsingToolbar = (CollapsingToolbarLayout)findViewById(R.id.contactActivityCollapsingToolbar);
        textViewAdress1 = (TextView)findViewById(R.id.siteDetailItemAdress1TextView);
        textViewAdress2 = (TextView)findViewById(R.id.siteDetailItemAdress2TextView);
        contactPhoneWrapListView = (WrapListView)findViewById(R.id.contactPhoneWrapListView);
        contactEmailWrapListView = (WrapListView)findViewById(R.id.contactEmailWrapListView);
        //contactRoleTextView = (TextView)findViewById(R.id.contactRoleTextView);
    }

    public void setViews() {
        if(contact.getFullname() == null){
            collapsingToolbar.setTitle(contact.getName());
        } else {
            collapsingToolbar.setTitle(contact.getFullname());
        }
        textViewAdress1.setText(site.getAddress().getAdressPart1());
        textViewAdress2.setText(site.getAddress().getAdressPart2());
/*        if(contact.getRole() == null){
            contactRoleTextView.setVisibility(View.GONE);
        }
        else{
            contactRoleTextView.setText(contact.getRole());
        }*/
    }

    // c l a s s   A r r a y A d a p t e r P h o n e
    // ---------------------------------------------
    public static class ArrayAdapterPhone extends ArrayAdapter<Phone> {

        private static class ViewHolder {
            private TextView contactPhoneListRowTagTextView;
            private TextView contactPhoneListRowNumberTextView;
            private TextView icon_fa_phone_contact;
        }

        public ArrayAdapterPhone(Context context, ArrayList<Phone> phoneArrayList) {
            super(context, 0, phoneArrayList);
        }

        public View getView(int position, View convertView, ViewGroup parent) {
            Phone phone = getItem(position);
            ViewHolder viewHolder;
            if (convertView == null) {
                convertView = LayoutInflater.from(this.getContext())
                        .inflate(R.layout.contact_phone_list_row, parent, false);

                viewHolder = new ViewHolder();
                viewHolder.contactPhoneListRowTagTextView = (TextView) convertView.findViewById(R.id.contactPhoneListRowTagTextView);
                viewHolder.contactPhoneListRowNumberTextView = (TextView) convertView.findViewById(R.id.contactPhoneListRowNumberTextView);
                viewHolder.icon_fa_phone_contact = (TextView) convertView.findViewById(R.id.icon_fa_phone_contact);

                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }

            Phone item = getItem(position);
            if (item!= null) {
                viewHolder.contactPhoneListRowTagTextView.setText(phone.getTag());
                viewHolder.contactPhoneListRowNumberTextView.setText(phone.getNumber());
                viewHolder.icon_fa_phone_contact.setTypeface(HibmobtechApplication.getHibmobtechResources().getHibmobtechFont());
            }

            return convertView;
        }
    }

    // c l a s s   A r r a y A d a p t e r E m a i l
    // ---------------------------------------------
    public static class ArrayAdapterEmail extends ArrayAdapter<Email> {

        private static class ViewHolder {
            private TextView icon_fa_envelope;
            private TextView contactEmailListRowTagTextView;
            private TextView contactEmailListRowAdressTextView;
        }

        public ArrayAdapterEmail(Context context, ArrayList<Email> emailArrayList) {
            super(context, 0, emailArrayList);
        }

        public View getView(int position, View convertView, ViewGroup parent) {
            // Typeface font = Typeface.createFromAsset(getContext().getAssets(), "fontawesome-webfont.ttf");
            Email email = getItem(position);
            ViewHolder viewHolder;
            if (convertView == null) {
                convertView = LayoutInflater.from(this.getContext())
                        .inflate(R.layout.contact_email_list_row, parent, false);

                viewHolder = new ViewHolder();
                viewHolder.contactEmailListRowTagTextView = (TextView) convertView.findViewById(R.id.contactEmailListRowTagTextView);
                viewHolder.contactEmailListRowAdressTextView = (TextView) convertView.findViewById(R.id.contactEmailListRowAdressTextView);
                viewHolder.icon_fa_envelope = (TextView) convertView.findViewById(R.id.icon_fa_envelope);

                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }

            Email item = getItem(position);
            if (item!= null) {
                viewHolder.icon_fa_envelope.setTypeface(HibmobtechApplication.getHibmobtechResources().getHibmobtechFont());
                viewHolder.contactEmailListRowTagTextView.setText(email.getTag());
                viewHolder.contactEmailListRowAdressTextView.setText(email.getAddress());
            }
            return convertView;
        }
    }
}
