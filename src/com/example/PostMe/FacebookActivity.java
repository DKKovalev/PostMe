package com.example.PostMe;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import com.facebook.Session;
import com.facebook.SessionState;
import com.facebook.UiLifecycleHelper;

public class FacebookActivity extends FragmentActivity {
    private UiLifecycleHelper uiLifecycleHelper;
    private Session.StatusCallback statusCallback = new Session.StatusCallback() {
        @Override
        public void call(Session session, SessionState state, Exception exception) {
            onSessionStateChange(session, state, exception);
        }
    };

    private static final int SPLASH = 0;
    private static final int SELECTION = 1;
    private static final int FRAGMENT_COUNT = SELECTION + 1;

    private boolean isResumed = false;

    private Fragment[] fragments = new Fragment[FRAGMENT_COUNT];

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.facebook);

        FragmentManager fragmentManager = getSupportFragmentManager();
        fragments[SPLASH] = fragmentManager.findFragmentById(R.id.splashFragment);
        fragments[SELECTION] = fragmentManager.findFragmentById(R.id.selectionFragment);

        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        for (int i = 0; i < fragments.length; i++) {
            fragmentTransaction.hide(fragments[i]);
        }

        fragmentTransaction.commit();

        uiLifecycleHelper = new UiLifecycleHelper(this, statusCallback);
        uiLifecycleHelper.onCreate(savedInstanceState);

    }

    private void showFragment(int fragmentIndex, boolean addToBackStack) {

        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        for (int i = 0; i < fragments.length; i++) {
            if (i == fragmentIndex) {
                fragmentTransaction.show(fragments[i]);
            } else {
                fragmentTransaction.hide(fragments[i]);
            }
            if (addToBackStack) {
                fragmentTransaction.addToBackStack(null);
            }

            fragmentTransaction.commit();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        isResumed = true;

        /*AppEventsLogger.activateApp(this);
        Session session = Session.getActiveSession();
        if (session != null && (session.isOpened() || session.isClosed())) {
            onSessionStateChange(session, session.getState(), null);
        }
       */

        uiLifecycleHelper.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        isResumed = false;

       /* AppEventsLogger.deactivateApp(this);
        */

        uiLifecycleHelper.onPause();
    }

    private void onSessionStateChange(Session session, SessionState sessionState, Exception e) {
        if (isResumed) {
            FragmentManager fragmentManager = getSupportFragmentManager();
            int backStackSize = fragmentManager.getBackStackEntryCount();
            for (int i = 0; i < backStackSize; i++) {
                fragmentManager.popBackStack();
            }
            if (sessionState.isOpened()) {
                showFragment(SELECTION, false);
            } else if (sessionState.isClosed()) {
                showFragment(SPLASH, false);
            }
        }
    }

    @Override
    protected void onResumeFragments() {
        super.onResumeFragments();
        Session session = Session.getActiveSession();
        if (session != null && session.isOpened()) {
            showFragment(SELECTION, false);
        } else {
            showFragment(SPLASH, false);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        uiLifecycleHelper.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        uiLifecycleHelper.onDestroy();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        uiLifecycleHelper.onSaveInstanceState(outState);
    }
}

