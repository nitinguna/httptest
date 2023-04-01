package com.example.httptest;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.DialogInterface;
import android.net.ConnectivityManager;
import android.net.ProxyInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.Authenticator;
import java.net.HttpURLConnection;
import java.net.InetSocketAddress;
import java.net.PasswordAuthentication;
import java.net.Proxy;
import java.net.URL;

import java.nio.charset.Charset;
import java.util.Random;

public class MainActivity extends AppCompatActivity implements View.OnClickListener  {

    private static final String TAG = "HTTP_TEST";
    private Button btn_one;
    private Button btn_two;
    private Button btn_three;
    private Button btn_four;
    private Button btn_five;
    private Button btn_six;
    private Button btn_seven;
    private TextView status_textview;
    private ImageView jpg_image;
    private ConnectivityManager cm = null;
    public ProxyInfo m_proxy_info = null;

    private Context context;

    private final Integer FETCH_URL_PROXY = 0;
    private final Integer FETCH_URL_WO_PROXY = 1;

    private void showWebView(){
        AlertDialog.Builder alert = new AlertDialog.Builder(this);
        alert.setTitle("Title here");

        WebView wv = new WebView(this);

        wv.loadUrl("https://www.google.com/"+"search?q="+getAlphaNumericString(7));
        wv.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url);

                return true;
            }
        });

        alert.setView(wv);
        alert.setNegativeButton("Close", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                dialog.dismiss();
            }
        });
        alert.show();
    }

    private void showHTMMLWebView(String unencodedHtml){
        AlertDialog.Builder alert = new AlertDialog.Builder(this);
        alert.setTitle("Title here");

        WebView wv = new WebView(this);

        String encodedHtml = Base64.encodeToString(unencodedHtml.getBytes(),
                Base64.NO_PADDING);
        wv.loadData(encodedHtml, "text/html", "base64");

        alert.setView(wv);
        alert.setNegativeButton("Close", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                dialog.dismiss();
            }
        });
        alert.show();
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        context = getApplicationContext();
        btn_one = (Button) findViewById(R.id.button);
        btn_one.setOnClickListener(this);
        btn_two = (Button) findViewById(R.id.button2);
        btn_two.setOnClickListener(this);
        btn_three = (Button) findViewById(R.id.button3);
        btn_three.setOnClickListener(this);
        btn_four = (Button) findViewById(R.id.button4);
        btn_four.setOnClickListener(this);
        btn_five = (Button) findViewById(R.id.button5);
        btn_five.setOnClickListener(this);
        btn_six = (Button) findViewById(R.id.button6);
        btn_six.setOnClickListener(this);
        btn_seven = (Button) findViewById(R.id.button7);
        btn_seven.setOnClickListener(this);
        status_textview = (TextView) findViewById(R.id.textView_status);
        jpg_image = (ImageView)findViewById(R.id.imageView);
        jpg_image.setImageResource(android.R.color.transparent);
        cm = (ConnectivityManager) this.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (cm != null){
            m_proxy_info = cm.getDefaultProxy();
            if (m_proxy_info != null)
            {
                MY_LOG("onCreate[getDefaultProxy][+]: " + m_proxy_info.toString());
            }
            else
            {
                MY_LOG("onCreate[FAIL]: getDefaultProxy");
            }
        }

    }

    // function to generate a random string of length n
    static String getAlphaNumericString(int n)
    {

        // choose a Character random from this String
        String AlphaNumericString = "ABCDEFGHIJKLMNOPQRSTUVWXYZ"
                + "0123456789"
                + "abcdefghijklmnopqrstuvxyz";

        // create StringBuffer size of AlphaNumericString
        StringBuilder sb = new StringBuilder(n);

        for (int i = 0; i < n; i++) {

            // generate a random number between
            // 0 to AlphaNumericString variable length
            int index
                    = (int)(AlphaNumericString.length()
                    * Math.random());

            // add Character one by one in end of sb
            sb.append(AlphaNumericString
                    .charAt(index));
        }

        return sb.toString();
    }

    private String fetchUsingProxy(Integer useProxy) throws IOException {


       // URL url = new URL("https://www.google.com/"+"search?q="+getAlphaNumericString(7));
        //URL url = new URL("https://172.26.14.2");
        URL url = new URL("https://www.zebra.com/ap/en.html");

        HttpURLConnection urlConnection;
        Authenticator authenticator = new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication("aldo@yoursite.com", "password".toCharArray());
            }
        };
        Authenticator.setDefault(authenticator);
        Proxy proxy = new Proxy(Proxy.Type.HTTP,
                new InetSocketAddress("192.168.1.6", 3080));
        if (useProxy==FETCH_URL_PROXY){
            Log.v(TAG, "FETCH_URL_PROXY");
            urlConnection = (HttpURLConnection) url.openConnection(proxy);

        }
        else
        {
            Log.v(TAG, "FETCH_URL_WITHOUT_PROXY");
            urlConnection = (HttpURLConnection) url.openConnection();
        }
        InputStream in = urlConnection.getInputStream();
        ByteArrayOutputStream out = new ByteArrayOutputStream();

        int bytesRead = 0;
        byte[] buffer = new byte[1024];
        out.write(buffer, 0, bytesRead);

        while((bytesRead = in.read(buffer)) > 0){
            out.write(buffer, 0, bytesRead);
        }

        out.close();
        out.flush();
        in.close();
        Log.v(TAG, out.toString());
        urlConnection.disconnect();

        return out.toString();
    }

    private class JSONAsyncTask extends AsyncTask<Integer, Void, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(Integer... option) {
            String htmlPage=null;
            try {

                 htmlPage = fetchUsingProxy(option[0]);

            } catch (IOException e) {
                e.printStackTrace();
            }
            return htmlPage;
        }

        protected void onPostExecute(String result) {
            showHTMMLWebView(result.toString());

        }
    }

    private ProxyObj proxy_cmd = null;

    @Override
    protected void onPause()
    {
        super.onPause();

        MY_LOG("onPause");

        if (proxy_cmd != null)
        {
            proxy_cmd.onPause();
        }
    }

    @Override
    protected void onResume()
    {
        super.onResume();

        MY_LOG("onResume");

        if (proxy_cmd != null)
        {
            proxy_cmd.onResume();
        }
    }

    @Override
    public void onClick(View v) {
        JSONAsyncTask asyncTask=new JSONAsyncTask();
        jpg_image.setImageResource(android.R.color.transparent);
        int iId = v.getId();
        if (iId == R.id.button) {
            //asyncTask.execute(FETCH_URL_PROXY);
            showWebView();
        }else if (iId == R.id.button2) {
            asyncTask.execute(FETCH_URL_WO_PROXY);
        }else if (iId == R.id.button3) {
            proxy_cmd=new ProxyObj(this, ProxyObj.COMMAND.DOWNLOAD_MGR);
            proxy_cmd.Execute();
        }
        else if (iId == R.id.button4) {
            proxy_cmd=new ProxyObj(this, ProxyObj.COMMAND.PROXY_DIRECT);
            proxy_cmd.Execute();
        }
        else if (iId == R.id.button5) {
            proxy_cmd=new ProxyObj(this, ProxyObj.COMMAND.DOWNLOAD_AOSP);
            proxy_cmd.Execute();
        }
    }

    ///////////////////////////////////////////////////////////////////////////////////////////////
    //
    // Log functions
    //

    public void MY_LOG(String msg)
    {
        String display = msg + "\n" + status_textview.getText().toString();
        status_textview.setText(display);
        Log.i(TAG, msg);
    }

    public void MY_LOG_ThreadSafe(String msg)
    {
        final String message = msg;
        runOnUiThread(new Runnable()
        {
            @Override
            public void run()
            {
                MY_LOG(message);
            }
        });
    }
}