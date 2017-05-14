package com.githubappli.antoi.github_appli;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.TextureView;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

public class Accueil extends AppCompatActivity {

    public static final String EXTRA_MESSAGE="";
    private String TAG = Accueil.class.getSimpleName();
    private ProgressDialog pDialogConnect;
    private static String url = "https://api.github.com/user";
    private String avatarURL="";
    private String strUsername="";
    private String strPassword="";
    private String pseudo="";

    public void connexionGit(){
        new BasicAuth().execute();
    }

    public void accesGit(){
        changeActivityAcces();
    }

    public void changeActivityConnexion(String message){
        Intent intent = new Intent(this, MenuApplication.class);
        intent.putExtra(EXTRA_MESSAGE, message);
        startActivity(intent);
    }

    public void changeActivityAcces(){
        Intent intent = new Intent(this, MenuApplication.class);
        Button button = (Button) findViewById(R.id.accesButton);
        String message = button.getText().toString();
        intent.putExtra(EXTRA_MESSAGE, message);
        startActivity(intent);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_accueil);
        Button button = (Button) findViewById(R.id.connectButton);
        button.setOnClickListener(myhandler1);
        Button button2 = (Button) findViewById(R.id.accesButton);
        button2.setOnClickListener(myhandler2);
    }


    View.OnClickListener myhandler1 = new View.OnClickListener() {
        public void onClick(View v) {
            connexionGit();
        }
    };

    View.OnClickListener myhandler2 = new View.OnClickListener() {
        public void onClick(View v) {
            accesGit();
        }
    };


    /*
    Classe asynchrone pour gérer l'affichage des repositories
    */
    private class BasicAuth extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // Showing progress dialog
            pDialogConnect = new ProgressDialog(Accueil.this);
            pDialogConnect.setMessage("Connexion...");
            pDialogConnect.setCancelable(false);
            pDialogConnect.show();

        }

        @Override
        protected Void doInBackground(Void... args) {
            MyHttpHandler sh = new MyHttpHandler();
            // Making a request to url and getting response
            EditText mail = (EditText) findViewById(R.id.mail);
            strUsername= mail.getText().toString();
            EditText password = (EditText) findViewById(R.id.password);
            strPassword= password.getText().toString();
            String jsonStr = sh.authRequest(url, strUsername, strPassword);
            Log.e(TAG, "réponse API : " + jsonStr);
            jsonStr = "{user:" + jsonStr + "}";

            if (jsonStr != null) {
                try {
                    JSONObject c = new JSONObject(jsonStr);
                    c = c.getJSONObject("user");
                        pseudo = c.getString("login");
                        avatarURL = c.getString("avatar_url");
                } catch (final JSONException e) {
                    Log.e(TAG, "Json parsing error: " + e.getMessage());
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                           /* Toast.makeText(getApplicationContext(),
                                    "Json parsing error: " + e.getMessage(),
                                    Toast.LENGTH_LONG)
                                    .show();*/
                        }
                    });
                }
            } else {
                Log.e(TAG, "Couldn't get json from server.");
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        /*Toast.makeText(getApplicationContext(),
                                "Couldn't get json from server. Check LogCat for possible errors!",
                                Toast.LENGTH_LONG)
                                .show();*/
                    }
                });
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            // Dismiss the progress dialog
            if (pDialogConnect.isShowing())
                pDialogConnect.dismiss();
            //gestion après connexion
            if(pseudo!="") {
                changeActivityConnexion(pseudo);
            }else{
                Toast.makeText(getApplicationContext(),
                        "Identifiants incorrects",
                        Toast.LENGTH_LONG)
                        .show();
            }
        }
    }


    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onStart(){
        super.onStart();
    }

    @Override
    protected void onRestart(){
        super.onRestart();
    }

    @Override
    protected void onStop(){
        super.onStop();
    }

    @Override
    protected void onDestroy(){
        super.onDestroy();
    }

}
