package co.edu.javeriana.ciclipointer;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.net.Uri;
import android.os.health.SystemHealthManager;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.AccessTokenTracker;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.Profile;
import com.facebook.ProfileTracker;
import com.facebook.appevents.AppEventsLogger;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.security.MessageDigest;

public class MainActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private	FirebaseAuth.AuthStateListener mAuthListener;

    private TextView user, mpassword;
    private Button login, signup;

    private boolean autentication = false;

    private CallbackManager callbackManager;
    private AccessTokenTracker accessTokenTracker;
    private ProfileTracker profileTracker;
    private LoginButton loginButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FacebookSdk.sdkInitialize(getApplicationContext());
        setContentView(R.layout.activity_main);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        mAuth =	FirebaseAuth.getInstance();
        mAuthListener =	new	FirebaseAuth.AuthStateListener()	{
            @Override
            public	void	onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth)	{
                FirebaseUser user	=	firebaseAuth.getCurrentUser();
                if	(user	!=	null)	{
                    // Toast.makeText(MainActivity.this, "onAuthStateChanged:signed_in:", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(MainActivity.this,	InicioActivity.class));
                }	else	{
                    //	User	is	signed	out
                    //Toast.makeText(MainActivity.this, "onAuthStateChanged:signed_out", Toast.LENGTH_SHORT).show();
                }
            }
        };


        //AppEventsLogger.activateApp(this);
        callbackManager = CallbackManager.Factory.create();
        loginButton = (LoginButton) findViewById(R.id.login_button);
        loginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(final LoginResult loginResult) {
                AccessToken accessToken = loginResult.getAccessToken();
                Profile profile = Profile.getCurrentProfile();

                if(profile!=null){// acá ya tiene todo aceptado y entra primera vez
                    registrar(accessToken);
                    System.out.println("valor es 1: "+profile.getId());
                    System.out.println("valor es 1: "+profile.getProfilePictureUri(70,70));
                    System.out.println("valor es 1: "+profile.getName());
                }
                accessTokenTracker = new AccessTokenTracker() {
                    @Override
                    protected void onCurrentAccessTokenChanged(AccessToken oldAccessToken, AccessToken currentAccessToken) {

                    }
                };

                profileTracker = new ProfileTracker() {
                    @Override
                    protected void onCurrentProfileChanged(Profile oldProfile, Profile currentProfile) {
                        if(currentProfile!=null){
                            // acá primera vez, cuando acepta.
                            registrar(loginResult.getAccessToken());
                            System.out.println("valor es 2: "+currentProfile.toString());
                            System.out.println("valor es 2: "+currentProfile.getProfilePictureUri(50,50));
                            System.out.println("valor es 2: "+currentProfile.getName());

                        }
                    }
                };

                accessTokenTracker.startTracking();
                profileTracker.startTracking();

                loginButton.setReadPermissions("public_profile");

            }

            @Override
            public void onCancel() {

            }

            @Override
            public void onError(FacebookException error) {

            }
        });


      /*  PackageInfo info;
        String hash = null;
        try {
            info = getPackageManager().getPackageInfo("co.edu.javeriana.ciclipointer", PackageManager.GET_SIGNATURES);
            for(Signature s: info.signatures){
                MessageDigest md;
                md = MessageDigest.getInstance("SHA");
                md.update(s.toByteArray());
                hash = new String(Base64.encode(md.digest(),0));
            }
        }catch (Exception e ){
            e.printStackTrace();
        }

        System.out.println("valor es: "+hash);*/
        /*
           - Obtenemos la instancia de firebase para el usuario
           - Agregamos un listener que verifica si el usuario esta
            o no logeado, si lo esta pasa directamente al inicio.
         */


        user = (TextView) findViewById(R.id.nombreUs);
        mpassword = (TextView) findViewById(R.id.passUs);
        login = (Button) findViewById(R.id.Binicio);
        signup = (Button) findViewById(R.id.Bregistro);

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(validateForm()){
                    if(signin()){
                        Intent intent = new Intent(getApplicationContext(),InicioActivity.class);
                        startActivity(intent);
                    }
                }else
                    Toast.makeText(MainActivity.this, "Parámetros incorrectos", Toast.LENGTH_SHORT).show();

            }
        });

        signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(),RegistroActivity.class);
                startActivity(intent);
            }
        });

    }

    /**
     * Descripción: valida los datos ingresados por el usuario.
     * @param
     * @return true si cumple parámetros.
     */
    private	boolean validateForm()	{
        boolean valid	=	true;
        String	email	=	user.getText().toString();
        if	(TextUtils.isEmpty(email))	{
            user.setError("Required.");
            valid	=	false;
        }else{
            if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
                valid	=	false;
                user.setError("Format required.");
            }else
                user.setError(null);
        }
        String	password	=	mpassword.getText().toString();
        if	(TextUtils.isEmpty(password))	 {
            mpassword.setError("Required.");
            valid	=	false;
        }	else	{
            if(mpassword.length()<8){
                mpassword.setError("Min 8 caracteres");
            }else
                mpassword.setError(null);
        }
        return	valid;
    }

    @Override
    public void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener);
        }
    }

    /**
     * Descripción: Autentica ante firebase
     * @param
     * @return true si la autenticación fue correcta.
     */
    private boolean signin(){
        String	email	=	user.getText().toString();
        final String	password	=	mpassword.getText().toString();
        autentication = false;
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (!task.isSuccessful()) {
                            String error = task.getException().getMessage().toString();
                            if(error.contains("The password is invalid or the user does not have a password")){
                                Toast.makeText(MainActivity.this, "Usuario inválido o contraseña incorrecta ", Toast.LENGTH_LONG).show();
                                mpassword.setText("");
                            }else{
                                Toast.makeText(MainActivity.this, "Fallo en la autenticación "+task.getException().getMessage().toString(), Toast.LENGTH_LONG).show();
                                user.setText("");
                                mpassword.setText("");
                            }

                        }else autentication = true;
                    }
                });
        return autentication;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        callbackManager.onActivityResult(requestCode,resultCode,data);
    }

    @Override
    protected void onResume() {
        super.onResume();

        Profile profile = Profile.getCurrentProfile();
        if(profile!=null){// acá tiene ya sesión abierta y abre app
            System.out.println("valor es 3: "+profile.getFirstName());
        }
    }

    private void registrar(AccessToken token){
        AuthCredential credential = FacebookAuthProvider.getCredential(token.getToken());
        mAuth.signInWithCredential(credential).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
            @Override
            public void onSuccess(AuthResult authResult) {
                FirebaseUser userr = mAuth.getCurrentUser();
                System.out.println("valor es:: "+userr.getDisplayName());
                // acá crear usuario firebase
                // cerrar sesión que también cierre de fb
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(MainActivity.this, "Error registrando con Facebook "+e.getMessage().toString(), Toast.LENGTH_SHORT).show();
            }
        });

    }
}
