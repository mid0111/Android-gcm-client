package com.mid.android_gcm_client.app;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import com.google.android.gms.gcm.GoogleCloudMessaging;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends Activity {

    private static final String TAG = "MainActivity";
    private Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        context = getApplicationContext();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * Pushボタン押下時に呼び出される処理.
     * @param view view
     */
    public void sendPushRequest(View view) {

        Log.i(TAG, "push button!!");
        new AsyncTask() {
            @Override
            protected String doInBackground(Object[] params) {
                // registration id 取得
                String regid;
                try {
                    regid = GoogleCloudMessaging.getInstance(context).register(
                            Config.getProjectNumber());
                } catch (IOException e) {
                    Log.e(TAG, "Failed to get registration id.", e);
                    return null;
                }

                // Push通知リクエスト実行
                HttpPost request = new HttpPost(Config.getGcmServerHost());
                try {
                    Log.i("Button", "registration id: " + regid);
                    List<NameValuePair> body = new ArrayList<NameValuePair>();
                    body.add(new BasicNameValuePair("registrationId", regid));
                    request.setEntity(new UrlEncodedFormEntity(body));

                    DefaultHttpClient httpClient = new DefaultHttpClient();
                    httpClient.execute(request, new ResponseHandler<String>() {
                        @Override
                        public String handleResponse(HttpResponse response)
                                throws IOException {
                            Log.i("Button", "status code: " + response.getStatusLine().getStatusCode());
                            Log.i("Button", "response: " + EntityUtils.toString(response.getEntity(), "UTF-8"));
                            return null;
                        }
                    });
                } catch (IOException e) {
                    Log.e(TAG, "Failed to push.", e);
                }
                return null;
            }
        }.execute(null, null, null);
    }
}
