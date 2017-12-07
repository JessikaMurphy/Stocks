package edu.temple.stocks;

import android.app.IntentService;
import android.content.Intent;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;


import edu.temple.stocks.R;

public class RefreshService extends IntentService {
    /**
     * Creates an IntentService.  Invoked by your subclass's constructor.
     */
    //List symbol;
    List<String> symbol= Collections.synchronizedList(new ArrayList<String>());

    Handler mHandler = new Handler();

    private final IBinder refreshBinder = new refreshBinder();

    public void setSymbol(List s) {
        symbol = s;
        getQuote(symbol);

    }

    public void settSymbol(List s) {
        symbol = s;
        gettQuote(symbol);

    }

    public void setter(List s) {
        symbol = s;
        System.out.println("THE LIST Items: "+symbol);

    }

    public class refreshBinder extends Binder {
        RefreshService getService(){
            return RefreshService.this;
        }
    }


    @Override
    public IBinder onBind(Intent intent){

        new Thread(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    try {
                        System.out.println("running!");
                        Thread.sleep(60000);
                        String s1=readFromFile();
                        System.out.println("running data: "+s1);

                        String replace = s1.replace("[","");

                        String replace1 = replace.replace("]","");
                        final List<String> myList = new ArrayList<String>(Arrays.asList(replace1.split(",")));
                        System.out.println("final string:"+myList.toString());
                        System.out.println("running2");
                        mHandler.post(new Runnable() {

                            @Override
                            public void run() {
                                System.out.println("list item: "+myList);

                                settSymbol(myList);

                                System.out.println("Updated");
                            }
                        });
                    } catch (Exception e) {
                    }
                }
            }
        }).start();

        return refreshBinder;
    }


    public RefreshService() {

        super("RefreshService");
        System.out.println("here");
        //watch();
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        System.out.println("check 2");
        getQuote(intent.getStringArrayListExtra("stock_symbol"));
    }


    public void gettQuote(final List symbol) {

        writeToFile("[");
        Thread t = new Thread() {
            @Override
            public void run() {
                URL stockQuoteUrl;
                //writeToFile("[");
                int temp = 0;
                String finalresponse="[";
                String t="";
                for (int i = 0; i < symbol.size(); i++) {

                    try {

                        if (i == 0) {
                            System.out.println("Current Symbol: " + symbol.get(i).toString().toUpperCase());
                            t=symbol.get(i).toString().toUpperCase();
                        }
                        else{
                            System.out.println("Current Symbol: " + symbol.get(i).toString().toUpperCase().substring(1));
                            t=symbol.get(i).toString().toUpperCase().substring(1);
                        }

                        stockQuoteUrl = new URL("http://dev.markitondemand.com/MODApis/Api/v2/Quote/json/?symbol=" + t);

                        BufferedReader reader = new BufferedReader(
                                new InputStreamReader(
                                        stockQuoteUrl.openStream()));

                        String response = "", tmpResponse;

                        tmpResponse = reader.readLine();
                        while (tmpResponse != null) {
                            response = response + tmpResponse;
                            tmpResponse = reader.readLine();
                        }

                        JSONObject stockObject = new JSONObject(response);
                        System.out.println("The stock: " + stockObject.toString());

                        finalresponse= finalresponse+stockObject.toString();
                        Log.d("Saved stock data", stockObject.toString());
                        temp = i + 1;
                        if (temp < symbol.size()) {
                            finalresponse=finalresponse+",";


                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                finalresponse=finalresponse+"]";


                Handler handler = new Handler(Looper.getMainLooper());
                handler.post(new Runnable() {

                    @Override
                    public void run() {
                        Toast.makeText(getApplicationContext(),
                                getString(R.string.updated),
                                Toast.LENGTH_SHORT).show();
                    }
                });

                writeToFile(finalresponse);

            }
        };
        t.start();


    }


    public void getQuote(final List symbol) {


        writeToFile("[");
        Thread t = new Thread() {
            @Override
            public void run() {
                URL stockQuoteUrl;
                //writeToFile("[");
                int temp = 0;
                String finalresponse="[";
                for (int i = 0; i < symbol.size(); i++) {

                    try {

                        System.out.println("Current Symbol: " + symbol.get(i).toString().toUpperCase());

                        stockQuoteUrl = new URL("http://dev.markitondemand.com/MODApis/Api/v2/Quote/json/?symbol=" + symbol.get(i).toString().toUpperCase());

                        BufferedReader reader = new BufferedReader(
                                new InputStreamReader(
                                        stockQuoteUrl.openStream()));

                        String response = "", tmpResponse;

                        tmpResponse = reader.readLine();
                        while (tmpResponse != null) {
                            response = response + tmpResponse;
                            tmpResponse = reader.readLine();
                        }

                        JSONObject stockObject = new JSONObject(response);
                        System.out.println("THE STOCK: " + stockObject.toString());
                        finalresponse= finalresponse+stockObject.toString();
                        Log.d("Saved stock data", stockObject.toString());
                        temp = i + 1;
                        if (temp < symbol.size()) {
                            finalresponse=finalresponse+",";


                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                finalresponse=finalresponse+"]";
                System.out.println("What it should be: "+finalresponse);
                writeToFile(finalresponse);

            }
        };
        t.start();

    }

    private void writeToFile(String data) {
        try {
            File mFolder = new File("/data/user/0/edu.temple.stocks/files");
            File imgFile = new File(mFolder.getAbsolutePath() + "/stockJson.txt");

            FileOutputStream fOut = new FileOutputStream(imgFile, false);
            OutputStreamWriter myOutWriter = new OutputStreamWriter(fOut);
            myOutWriter.write(data);

            myOutWriter.close();

            fOut.flush();
            fOut.close();

        } catch (IOException e) {
            Log.e("Exception", "writing failed: " + e.toString());
        }
    }


    public String readFromFile() {

        String ret = "";

        try {
            InputStream inputStream = getApplicationContext().openFileInput("stockSymbols.txt");

            if ( inputStream != null ) {
                InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
                String receiveString = "";
                StringBuilder stringBuilder = new StringBuilder();

                while ( (receiveString = bufferedReader.readLine()) != null ) {
                    stringBuilder.append(receiveString);
                }

                inputStream.close();
                ret = stringBuilder.toString();
            }
        }
        catch (FileNotFoundException e) {
            Log.e("login activity", "File not found: " + e.toString());
        } catch (IOException e) {
            Log.e("login activity", "Can not read file: " + e.toString());
        }

        return ret;

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}