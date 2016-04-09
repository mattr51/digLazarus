package uk.ac.qub.bookepos;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by Matt Ralphson
 * test log in - test test
 */
public class Login extends Activity {
    EditText name, password;
    String Name, Password;
    String USER = null;
    int ADMIN = 0;

    Context ctx = this;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);
        name = (EditText) findViewById(R.id.main_name);
        password = (EditText) findViewById(R.id.main_password);
    }

    public void main_login(View v) {
        Name = name.getText().toString();
        Password = password.getText().toString();
        BackGround b = new BackGround();
        b.execute(Name, Password);
    }

    /** Radio for mode - removed with simplified screen, now goes straight into BookSearchActivity
     *
     *
     // Radio button check
     public void onRadioButtonClicked(View radio) {
     // Is the button now checked?
     boolean checked = ((RadioButton) radio).isChecked();

     // Check which radio button was clicked
     switch(radio.getId()) {
     case R.id.radioSales:
     if (checked)
     Mode = "Sales";
     break;
     case R.id.radioAdmin:
     if (checked)
     Mode = "Admin";
     break;
     case R.id.radioStock:
     if (checked)
     Mode = "Stock";
     break;
     }
     }
     */


    /**
     * Some part of this must be reusable? Can turn it into a
     * client class like the bookClient ?
     * This is the call to the database and the results from json
     * In this case results are the username and the admin value
     */
    class BackGround extends AsyncTask<String, String, String> {

        @Override
        protected String doInBackground(String... params) {
            String name = params[0];
            String password = params[1];
            String data = "";
            int tmp;
            /**
             *
             */
            try {
                URL url = new URL("http://54.171.237.154/html/bepos/login.php");
                String urlParams = "name=" + name + "&password=" + password;

                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                httpURLConnection.setDoOutput(true);
                OutputStream os = httpURLConnection.getOutputStream();
                os.write(urlParams.getBytes());
                os.flush();
                os.close();

                InputStream is = httpURLConnection.getInputStream();
                while ((tmp = is.read()) != -1) {
                    data += (char) tmp;
                }

                is.close();
                httpURLConnection.disconnect();

                return data;
            } catch (MalformedURLException e) {
                e.printStackTrace();
                return "Exception: " + e.getMessage();
            } catch (IOException e) {
                e.printStackTrace();
                return "Exception: " + e.getMessage();
            }
        }

        @Override
        protected void onPostExecute(String data) {
            String err = null;
            try {
                JSONObject root = new JSONObject(data);
                JSONObject user_data = root.getJSONObject("user_data");
                USER = user_data.getString("user");
                ADMIN = user_data.getInt("admin");
            } catch (JSONException e) {
                e.printStackTrace();
                err = "Exception: " + e.getMessage();
            }

          //  if (USER == Name) {
                Intent i = new Intent(ctx, BookSearchActivity.class);
                i.putExtra("user", USER);
                i.putExtra("admin", ADMIN);

          //  } else {
                Toast toast = Toast.makeText(getApplicationContext(), "Username returned"+ USER +" but logging in anyway", Toast.LENGTH_SHORT);
                toast.show();
                startActivity(i);
           // }
        }
    }
}
