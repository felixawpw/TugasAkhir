package com.felixawpw.indoormaps.services;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.util.Log;

import com.felixawpw.indoormaps.SplashScreenActivity;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;

public class AuthServices {
    private static final AuthServices ourInstance = new AuthServices();
    public static final String TAG = AuthServices.class.getName();

    public static AuthServices getInstance() {
        return ourInstance;
    }

    private FirebaseAuth mAuth;
    private FirebaseUser mUser;
    private AuthServices() {
        mAuth = FirebaseAuth.getInstance();
    }

    public GoogleSignInClient integrateGoogleSignIn(String requestIdToken, Activity baseActivity) {
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(requestIdToken)
                .requestEmail()
                .build();
        return GoogleSignIn.getClient(baseActivity, gso);
    }

    public void checkUserStatus(Activity baseActivity) {
        mUser = mAuth.getCurrentUser();
        Log.d(TAG, mUser == null ? "User is not logged in" : String.format("Logged in : %s - %s", mUser.getDisplayName(), mUser.getUid()));

        if (baseActivity instanceof SplashScreenActivity)
            ((SplashScreenActivity)baseActivity).updateUI(mUser);
    }

    public void firebaseAuthWithGoogle(GoogleSignInAccount acct, final Activity baseActivity) {
        Log.d(TAG, "firebaseAuthWithGoogle:" + acct.getId());

        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        mAuth.signInWithCredential(credential)
            .addOnCompleteListener(baseActivity, new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()) {
                        // Sign in success, update UI with the signed-in user's information
                        Log.d(TAG, "signInWithCredential:success");
                        checkUserStatus(baseActivity);
                    } else {
                        // If sign in fails, display a message to the user.
                        Log.w(TAG, "signInWithCredential:failure", task.getException());
                    }
                }
            });
    }
}
