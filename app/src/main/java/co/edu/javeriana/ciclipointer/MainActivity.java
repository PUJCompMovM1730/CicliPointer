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
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.HttpMethod;
import com.facebook.Profile;
import com.facebook.ProfileTracker;
import com.facebook.appevents.AppEventsLogger;
import com.facebook.login.LoginManager;
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
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import org.json.JSONException;
import org.json.JSONObject;

import java.security.MessageDigest;
import java.util.Arrays;

import entities.Usuario;

public class MainActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private	FirebaseAuth.AuthStateListener mAuthListener;

    private TextView user, mpassword;
    private Button login, signup;

    private boolean autentication = false;
    private boolean tipo = false;
    private CallbackManager callbackManager;
    private AccessTokenTracker accessTokenTracker;
    private ProfileTracker profileTracker;
    private LoginButton loginButton;
    private UserProfileChangeRequest.Builder upcrb = null;
    private String email = null;
    private DatabaseReference myRef;
    private int cont = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FacebookSdk.sdkInitialize(getApplicationContext());
        setContentView(R.layout.activity_main);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        myRef = FirebaseDatabase.getInstance().getReference();
        mAuth =	FirebaseAuth.getInstance();
        mAuthListener =	new	FirebaseAuth.AuthStateListener()	{
            @Override
            public	void	onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth)	{
                FirebaseUser user	=	firebaseAuth.getCurrentUser();
                if	(user	!=	null)	{
                    myRef.child("users/"+mAuth.getCurrentUser().getUid()).addListenerForSingleValueEvent(new	ValueEventListener(){
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot)	 {
                            Usuario myUser =	dataSnapshot.getValue(Usuario.class);
                            if(myUser.getTipo()!=null){
                                startActivity(new Intent(MainActivity.this,InicioTurismo.class));
                            }else{
                                startActivity(new Intent(MainActivity.this,	InicioActivity.class));
                            }
                        }
                        @Override
                        public void onCancelled(DatabaseError databaseError)	{
                            Toast.makeText(MainActivity.this, "Error", Toast.LENGTH_SHORT).show();
                            autentication = false;
                        }
                    });
                    // Toast.makeText(MainActivity.this, "onAuthStateChanged:signed_in:", Toast.LENGTH_SHORT).show();


                }	else	{


                    //	User	is	signed	out
                    //Toast.makeText(MainActivity.this, "onAuthStateChanged:signed_out", Toast.LENGTH_SHORT).show();
                }
            }
        };

        if(FirebaseAuth.getInstance().getCurrentUser()==null) {
            callbackManager = CallbackManager.Factory.create();
            loginButton = (LoginButton) findViewById(R.id.login_button);
            loginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
                @Override
                public void onSuccess(final LoginResult loginResult) {
                    LoginManager.getInstance().logInWithReadPermissions(
                            MainActivity.this, Arrays.asList("email")
                    );

                    final AccessToken accessToken = loginResult.getAccessToken();
                    final Profile profile = Profile.getCurrentProfile();

                    GraphRequest request = GraphRequest.newMeRequest(AccessToken.getCurrentAccessToken(), new GraphRequest.GraphJSONObjectCallback() {
                        @Override
                        public void onCompleted(JSONObject object, GraphResponse response) {
                            final JSONObject json = response.getJSONObject();
                            try {
                                if (json != null) {
                                    // System.out.println("email es: "+json.getString("email"));
                                    email = json.getString("email");
                                    if (profile != null && cont == 0) {// acá ya tiene todo aceptado y entra primera vez
                                        cont ++;
                                        registrar(accessToken);
                                    }
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    });

                    Bundle parameters = new Bundle();
                    parameters.putString("fields", "email");
                    request.setParameters(parameters);
                    request.executeAsync();

                }

                @Override
                public void onCancel() {

                }

                @Override
                public void onError(FacebookException error) {

                }
            });

        }
        user = (TextView) findViewById(R.id.nombreUs);
        mpassword = (TextView) findViewById(R.id.passUs);
        login = (Button) findViewById(R.id.Binicio);
        signup = (Button) findViewById(R.id.Bregistro);

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(validateForm()){
                    if(signin()){
                        if(!tipo) {
                            Intent intent = new Intent(getApplicationContext(), InicioActivity.class);
                            startActivity(intent);
                        }else{
                            Intent intent = new Intent(getApplicationContext(), InicioTurismo.class);
                            startActivity(intent);
                        }
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

                        }else {
                            autentication = true;
                            myRef.child("users/"+mAuth.getCurrentUser().getUid()).addListenerForSingleValueEvent(new	ValueEventListener(){
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot)	 {
                                    Usuario myUser =	dataSnapshot.getValue(Usuario.class);
                                    if(myUser.getTipo()!=null){
                                        tipo = true;
                                    }
                                }
                                @Override
                                public void onCancelled(DatabaseError databaseError)	{
                                    Toast.makeText(MainActivity.this, "Error", Toast.LENGTH_SHORT).show();
                                    autentication = false;
                                }
                            });
                        }
                    }
                });
        return autentication;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        callbackManager.onActivityResult(requestCode,resultCode,data);
    }

    private void registrar(AccessToken token){
        AuthCredential credential = FacebookAuthProvider.getCredential(token.getToken());
        mAuth.signInWithCredential(credential).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
            @Override
            public void onSuccess(AuthResult authResult) {
                final FirebaseUser userr = mAuth.getCurrentUser();
                myRef.child("users/"+mAuth.getCurrentUser().getUid())
                        .addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {

                                if (!dataSnapshot.hasChildren()){
                                    final Profile profile = Profile.getCurrentProfile();
                                    upcrb = new UserProfileChangeRequest.Builder();
                                    upcrb.setDisplayName(profile.getName().toUpperCase());
                                    upcrb.setPhotoUri(null);
                                    userr.updateEmail(email).addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {

                                        }
                                    });
                                    userr.updateProfile(upcrb.build()).addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            Usuario us = new Usuario();
                                            us.setAltura(0);
                                            us.setEdad(0);
                                            us.setPeso(0);
                                            us.setRH("");
                                            us.setKm(0);
                                            us.setNombre(profile.getName().toUpperCase());
                                            us.setCorreo(email);
                                            myRef.child("users/"+mAuth.getCurrentUser().getUid())
                                                    .setValue(us).addOnFailureListener(new OnFailureListener() {
                                                @Override
                                                public void onFailure(@NonNull Exception exception) {
                                                    Toast.makeText(MainActivity.this, "Error guardar datos en servidor", Toast.LENGTH_SHORT).show();
                                                }
                                            });
                                        }
                                    });
                                }
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });


            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(MainActivity.this, "Error registrando con Facebook "+e.getMessage().toString(), Toast.LENGTH_SHORT).show();
            }
        });

    }
}
