package com.felixawpw.indoormaps;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.Window;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;
import android.widget.Toast;

import com.felixawpw.indoormaps.font.MaterialDesignIconsTextView;
import com.felixawpw.indoormaps.services.AuthServices;
import com.felixawpw.indoormaps.services.Permissions;
import com.felixawpw.indoormaps.view.kbv.KenBurnsView;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseUser;

import java.security.Permission;

public class SplashScreenActivity extends AppCompatActivity {

    public static final String TAG = SplashScreenActivity.class.getName();
    private static final int RC_SIGN_IN = 9001;

    private KenBurnsView mKenBurns;
    private MaterialDesignIconsTextView mLogo;
    private TextView welcomeText;
    private GoogleSignInClient mGoogleSignInClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().requestFeature(Window.FEATURE_NO_TITLE); //Removing ActionBar
        getSupportActionBar().hide();
        setContentView(R.layout.activity_splash_screen);
        Permissions.checkPermissionsOnLoad(this);

        //Integrate sign in using google
        mGoogleSignInClient = AuthServices.getInstance().
                integrateGoogleSignIn(getString(R.string.default_web_client_id), SplashScreenActivity.this);

        mKenBurns = (KenBurnsView) findViewById(R.id.ken_burns_images);
        mLogo = (MaterialDesignIconsTextView) findViewById(R.id.logo);
        welcomeText = (TextView) findViewById(R.id.welcome_text);
        setAnimation();
    }

    private void setAnimation() {
        mKenBurns.setImageResource(R.drawable.splash_screen_option_three);
        animation2();
        animation3();
    }

    //Copy pasted from the template.
    private void animation2() {
        mLogo.setAlpha(1.0F);
        Animation anim = AnimationUtils.loadAnimation(this, R.anim.translate_top_to_center);
        mLogo.startAnimation(anim);
    }

    //Copy pasted from the template.
    private void animation3() {
        ObjectAnimator alphaAnimation = ObjectAnimator.ofFloat(welcomeText, "alpha", 0.0F, 1.0F);
        alphaAnimation.setStartDelay(1700);
        alphaAnimation.setDuration(500);
        alphaAnimation.start();

        //Add listener to check either ObjectAnimator has finished their tasks or not
        alphaAnimation.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                AuthServices.getInstance().checkUserStatus(SplashScreenActivity.this);
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
    }

    //This function is called from AuthServices.java, on checkUserStatus() function.
    //If the user = null, then apps will force user to sign in using their google account
    //If user already signed in, user will be directed to HomeActivity and close the SplashScreenActivity.
    public void updateUI(FirebaseUser user) {
        Permissions.requestPermissions(this);
        if (user == null)
            signIn();
        else {
            Intent intent = new Intent(this, HomeActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            ActivityCompat.finishAffinity(SplashScreenActivity.this);
        }
    }
//
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        Log.d(TAG, "Request");
        switch (requestCode) {
            case Permissions.REQUEST_ACCESS_FINE_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Permissions.PERMISSION_ACCESS_FINE_LOCATION = true;
                } else {
                    Permissions.PERMISSION_ACCESS_FINE_LOCATION = false;
                }
                return;
            }
            default: return;
        }
    }

    public void signIn() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = task.getResult(ApiException.class);
                AuthServices.getInstance().firebaseAuthWithGoogle(account, SplashScreenActivity.this);
            } catch (ApiException e) {
                // Google Sign In failed, update UI appropriately
                Log.w(TAG, "Google sign in failed", e);
                // ...
            }
        }
    }

}
