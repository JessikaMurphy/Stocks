package edu.temple.stocks;


import android.content.Context;
import android.os.Bundle;
import android.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;


/**
 * A simple {@link Fragment} subclass.
 */
public class DetailsFragment extends Fragment {

    TextView detailsText;
    ImageView chartImage;



    String name= "";
    String price= "";
    String symbol= "";

    Context context;

    public DetailsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v= inflater.inflate(R.layout.fragment_details, container, false);

        chartImage= (ImageView) v.findViewById(R.id.chartImage);

        detailsText=  (TextView)v.findViewById(R.id.details);
        // Inflate the layout for this fragment
        return v;
    }
    void setMessage(String message, int ppos, Context context) throws JSONException {


        this.context = context;


        JSONArray jArray = new JSONArray(readFromFile());
        JSONObject company = (JSONObject) jArray.get(ppos);
        name= company.optString("Name");
        price= company.optString("LastPrice");
        symbol= company.optString("Symbol");

        new DownloadImageTask(chartImage).execute("https://finance.google.com/finance/getchart?p=5d&q="+symbol);
        detailsText.setText(getString(R.string.name)+" "+name+"\n"+getString(R.string.price)+" $"+price);


        //json functions
    }
    public String readFromFile() {
        String returnString = "";

        try {
            InputStream inputStream = context.openFileInput("stockJson.txt");

            if (inputStream != null){
                InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
                String receiveString = "";
                StringBuilder stringBuilder = new StringBuilder();

                while ( (receiveString = bufferedReader.readLine()) != null ) {
                    stringBuilder.append(receiveString);
                }

                inputStream.close();
                returnString = stringBuilder.toString();
            }
        }
        catch (FileNotFoundException e) {
            Log.e("login activity", "File not found: " + e.toString());
        } catch (IOException e) {
            Log.e("login activity", "Can not read file: " + e.toString());
        }

        return returnString;
    }

}
