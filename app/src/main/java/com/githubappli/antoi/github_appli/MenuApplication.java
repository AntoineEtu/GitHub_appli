package com.githubappli.antoi.github_appli;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

public class MenuApplication extends AppCompatActivity {

    private ProgressDialog pDialogRepo;
    private ListView repoList;
    private String TAG = Accueil.class.getSimpleName();
    private String userName = "";
    // URL de l'API
    private static String url = "https://api.github.com/";
    ArrayList<HashMap<String, String>> reposHashMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu_application);
        userName = getPseudoConnected();
        initComponents();
        //test pour savoir si connecté ou pas
        if(userName!=""){
            Toast.makeText(getApplication(),
                    "Connexion établie", Toast.LENGTH_LONG).show();
        }else{
            Toast.makeText(getApplication(),
                    "accès simple", Toast.LENGTH_LONG).show();
        }
    }

    public void initComponents(){
        //initialisation de la liste des repos
        reposHashMap = new ArrayList<>();
        repoList = (ListView) findViewById(R.id.repo_list);
        new GetRepos().execute();
    }

    public String getPseudoConnected(){
        Intent intent = getIntent();
        String message = intent.getStringExtra(Accueil.EXTRA_MESSAGE);
        return message;
    }
    /*
    Classe asynchrone pour gérer l'affichage des repositories
    */
    private class GetRepos extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // Showing progress dialog
            pDialogRepo = new ProgressDialog(MenuApplication.this);
            pDialogRepo.setMessage("Chargement...");
            pDialogRepo.setCancelable(false);
            pDialogRepo.show();

        }

        @Override
        protected Void doInBackground(Void... arg0) {
            MyHttpHandler sh = new MyHttpHandler();
            String endURL = "users/"+userName+"/repos";
            url=url+endURL;
            // Making a request to url and getting response
            String jsonStr = sh.launchRequest(url);

            Log.e(TAG, "communication avec : " + jsonStr);
            jsonStr = "{repos:"+jsonStr+"}";

            if (jsonStr != null) {
                try {
                    JSONObject jsonObj = new JSONObject(jsonStr);

                    JSONArray jsonRepos = jsonObj.getJSONArray("repos");

                    // looping through All repos
                    for (int i = 0; i < jsonRepos.length(); i++) {
                        JSONObject c = jsonRepos.getJSONObject(i);

                        String id = c.getString("id");
                        String name = c.getString("name");
                        String full_name = c.getString("full_name");
                        /* permet de récupérer un objet dans un objet

                        // Phone node is JSON Object
                        JSONObject phone = c.getJSONObject("phone");
                        String mobile = phone.getString("mobile");
                        String home = phone.getString("home");
                        String office = phone.getString("office");*/

                        // tmp hash map for single contact
                        HashMap<String, String> reposHashmap = new HashMap<>();

                        // on ajoute tous les répos à  HashMap key => value
                        reposHashmap.put("id", id);
                        reposHashmap.put("name", name);
                        reposHashmap.put("full_name", full_name);
                        //contact.put("mobile", mobile);

                        // ajoute la hashmap du repo a la listString
                        reposHashMap.add(reposHashmap);
                    }
                } catch (final JSONException e) {
                    Log.e(TAG, "Json parsing error: " + e.getMessage());
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getApplicationContext(),
                                    "Json parsing error: " + e.getMessage(),
                                    Toast.LENGTH_LONG)
                                    .show();
                        }
                    });

                }
            } else {
                Log.e(TAG, "Couldn't get json from server.");
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getApplicationContext(),
                                "Couldn't get json from server. Check LogCat for possible errors!",
                                Toast.LENGTH_LONG)
                                .show();
                    }
                });

            }

            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            // Dismiss the progress dialog
            if (pDialogRepo.isShowing())
                pDialogRepo.dismiss();
            /**
             * Updating parsed JSON data into ListView
             * */
            ListAdapter adapter = new SimpleAdapter(
                    MenuApplication.this, reposHashMap,
                    R.layout.list_repositories, new String[]{"name",
                    "full_name"}, new int[]{R.id.repo_name, R.id.repo_full_name});

            repoList.setAdapter(adapter);
        }

    }
}
