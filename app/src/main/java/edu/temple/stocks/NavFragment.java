package edu.temple.stocks;


import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.app.Fragment;
import android.support.design.widget.FloatingActionButton;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static android.view.View.GONE;


/**
 * A simple {@link Fragment} subclass.
 */
public class NavFragment extends Fragment {

//    TextView textView;
    List<String> stockList= Collections.synchronizedList(new ArrayList<String>());
    ArrayAdapter<String> adapter;

    RefreshService RS= new RefreshService();
    NavInterface activity;
    View v;



    @Override
    public void onAttach(Activity c) {
        super.onAttach(c);
        activity = (NavInterface) c;
    }




    public interface NavInterface {
        public void acceptMessage(String message, int position) throws JSONException;
        public void updateSymbolList(String message);
    }

    public NavFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.fragment_nav, container, false);
        TextView emptyMessage = v.findViewById(R.id.emptyStuff);
        emptyMessage.setText(R.string.empty_list);
        try {
            String temp="";
            JSONArray jarray = new JSONArray(readFromFile());

            if(!jarray.isNull(0)) {
                emptyMessage.setVisibility(GONE);

                System.out.println("length: "+jarray.length());
                for (int i = 0; i < jarray.length(); i++) {
                    JSONObject sym = (JSONObject) jarray.get(i);
                    temp= sym.optString("Symbol");

                    System.out.println("LOADING2: "+stockList.contains(temp));

                    if (stockList.contains(temp.toLowerCase())) {

                    }
                    else if(stockList.contains(temp.toUpperCase())){

                    }
                    else if(stockList.contains(temp)){

                    }
                    else{
                        stockList.add(temp);
                        activity.updateSymbolList(temp);
                    }
                }

            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        adapter = new ArrayAdapter<String>(v.getContext(), android.R.layout.simple_list_item_1, stockList);

        // Inflate the layout for this fragment
        FloatingActionButton myFab = (FloatingActionButton) (v.findViewById(R.id.floatingActionButton));


        myFab.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                // get prompts.xml view
                LayoutInflater li = LayoutInflater.from(v.getContext());
                View promptView = li.inflate(R.layout.prompt, null);

                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                        v.getContext());

                // set prompts.xml to alertdialog builder
                alertDialogBuilder.setView(promptView);

                final EditText userInput = (EditText) promptView
                        .findViewById(R.id.dialogUserInput);

                // set dialog message
                alertDialogBuilder
                        .setCancelable(false)
                        .setPositiveButton(R.string.ok,
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog,int id) {

                                        System.out.println(userInput.getText());
                                        stockList.add(userInput.getText().toString());
                                        activity.updateSymbolList(userInput.getText().toString());
                                        RS.setSymbol(stockList);
                                        writeToFile(stockList.toString());
                                        adapter.notifyDataSetChanged();
                                    }
                                })
                        .setNegativeButton(R.string.cancel,
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog,int id) {
                                        dialog.cancel();
                                    }
                                });

                // create alert dialog
                AlertDialog alertDialog = alertDialogBuilder.create();

                // show it
                alertDialog.show();
            }
        });
        adapter=new ArrayAdapter<String>(v.getContext(), android.R.layout.simple_list_item_1, stockList);
        ListView lv = (ListView)v.findViewById(R.id.list);
        lv.setAdapter(adapter);
        TextView emptyText = (TextView)v.findViewById(android.R.id.empty);

        lv.setEmptyView(emptyText);
        lv.setOnItemClickListener(
                new AdapterView.OnItemClickListener(){
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id){
                        String ChoreString = String.valueOf(parent.getItemAtPosition(position));

                        System.out.println("What was picked: "+ChoreString);
                        try {
                            System.out.println("position: "+position);
                            activity.acceptMessage(ChoreString, position);
                            //System.out.println("position: "+position);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                }

        );


        return v;
    }
    private void writeToFile(String data) {
        try {
            System.out.println("DATA: "+data);
            File mFolder = new File("/data/user/0/edu.temple.stocks/files");
            File imgFile = new File(mFolder.getAbsolutePath() + "/stockSymbols.txt");

            FileOutputStream fOut = new FileOutputStream(imgFile, false);/// may have to delete false
            OutputStreamWriter myOutWriter = new OutputStreamWriter(fOut);
            //myOutWriter.append(data);
            myOutWriter.write(data);

            myOutWriter.close();

            fOut.flush();
            fOut.close();

        } catch (IOException e) {
            Log.e("Exception", "File write failed: " + e.toString());
        }
    }
    public String readFromFile() {

        String ret = "";

        try {
            InputStream inputStream = v.getContext().openFileInput("stockJson.txt");

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

}
