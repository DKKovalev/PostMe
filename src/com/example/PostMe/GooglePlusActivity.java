package com.example.PostMe;

import android.app.Activity;
import android.content.Intent;
import android.content.IntentSender;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.plus.Plus;
import com.google.android.gms.plus.PlusShare;

public class GooglePlusActivity extends Activity implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    private static final int SIGN_IN = 0;

    private GoogleApiClient googleApiClient;
    private boolean intentInProgress;
    private ConnectionResult connectionResult;

    Button signInButton;
    Button postButton;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.google_plus);

        signInButton = (Button)findViewById(R.id.sign_in_button);
        signInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signIn();
            }
        });

        postButton = (Button)findViewById(R.id.post_on_wall_button);
        postButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent postIntent = new PlusShare.Builder(GooglePlusActivity.this)
                        .setType("text/plain")
                        .setText("Hello")
                        .getIntent();

                startActivityForResult(postIntent, 0);
            }
        });

        googleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this).addApi(Plus.API)
                .addScope(Plus.SCOPE_PLUS_LOGIN)
                .build();
    }

    protected void onStart() {
        super.onStart();
        googleApiClient.connect();
    }

    protected void onStop() {
        super.onStop();
        if (googleApiClient.isConnected()) {
            googleApiClient.disconnect();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_take_photo) {
            takePhoto();
        }
        return false;
    }

    private void takePhoto() {

    }


    @Override
    public void onConnected(Bundle bundle) {

    }

    @Override
    public void onConnectionSuspended(int i) {
        googleApiClient.connect();

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        if (!connectionResult.hasResolution()) {
            GooglePlayServicesUtil.getErrorDialog(connectionResult.getErrorCode(), this, 0).show();
            return;
        }

        if (!intentInProgress) {
            this.connectionResult = connectionResult;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == SIGN_IN) {
            if (resultCode != RESULT_OK) {

            }

            intentInProgress = false;
            if (!googleApiClient.isConnected()) {
                googleApiClient.connect();
            }
        }
    }

    private void signIn() {
        if (!googleApiClient.isConnecting()) {
            if (connectionResult.hasResolution()) {
                try {
                    intentInProgress = true;
                    connectionResult.startResolutionForResult(this, SIGN_IN);
                } catch (IntentSender.SendIntentException e) {
                    intentInProgress = false;
                    googleApiClient.connect();
                }
            }
        }
    }


}