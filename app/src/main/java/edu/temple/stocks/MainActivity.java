package edu.temple.stocks;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Handler;
import android.os.IBinder;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;

import org.json.JSONException;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class MainActivity extends AppCompatActivity implements NavFragment.NavInterface{
    boolean twoPanes;
    private boolean connected;

    final Context context = this;
    private Button button;
    private EditText result;

    final Handler mHandler = new Handler();

    //the list of stocks
    List<String> stockSymbolList= Collections.synchronizedList(new ArrayList<String>());


    Handler handler = new Handler();
    NavFragment navFragment = new NavFragment();
    FragmentManager fragmentManager = getFragmentManager();
    DetailsFragment details = new DetailsFragment();

    //
    ArrayAdapter<String> adapter;
    RefreshService RS= new RefreshService();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Bundle bundle = new Bundle();
        bundle.putString("newStock", "");

        // not twoPanes if it cannot see details fragment
        twoPanes = (findViewById(R.id.fragment_details) != null);

    //issue where sometime the details fragment will overlap stock list fragment, click a stock again to correct
        //rotate between landscape and portrait to fix nav frag's stock list update issue


        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.add(R.id.fragment_nav, navFragment);
        navFragment.setArguments(bundle);
        fragmentTransaction.commit();
        getFragmentManager()
                .executePendingTransactions();

        //if in landscape mode
        if (twoPanes){
            details = new DetailsFragment();
            fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.add(R.id.fragment_details,details);
            fragmentTransaction.commit();
        }
        //create 2 folders, each with a file to hold stock JSONs and stock symbols
        File mFolder = new File("/data/user/0/edu.temple.stocks/files");
        File imgFile = new File(mFolder.getAbsolutePath() + "/stockJson.txt");
        if (!mFolder.exists()) {
            mFolder.mkdir();
        }
        if (!imgFile.exists()) {
            try {
                imgFile.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        File mmFolder = new File("/data/user/0/edu.temple.stocks/files");
        File immgFile = new File(mFolder.getAbsolutePath() + "/stockSymbols.txt");
        if (!mmFolder.exists()) {
            mmFolder.mkdir();
        }
        if (!immgFile.exists()) {
            try {
                immgFile.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        Intent intent = new Intent(MainActivity.this,RS.getClass());
        bindService(intent, connection, Context.BIND_AUTO_CREATE);
    }
    private ServiceConnection connection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName className,
                                       IBinder service) {
            // bound to LocalService, cast the IBinder and get LocalService instance
            RefreshService.refreshBinder binder = (RefreshService.refreshBinder) service;
            RS = binder.getService();
            connected = true;
            System.out.println("Initial Connection: "+connected);
        }

        @Override
        public void onServiceDisconnected(ComponentName arg0) {
            connected = false;
        }
    };
    @Override
    public void onStop(){
        System.out.println("Stop Connection: "+connected);
        super.onStop();
        unbindService(connection);

    }

    public void updateSymbolList(String message){
        if (stockSymbolList.contains(message.toLowerCase())) {

        }
        else if(stockSymbolList.contains(message.toUpperCase())){

        }
        else if(stockSymbolList.contains(message)){

        }
        else{
            stockSymbolList.add(message);
            RS.setter(stockSymbolList);

        }

        System.out.println("THE List Items: "+stockSymbolList);
    }
    public void acceptMessage(String message, int pos) throws JSONException {
        System.out.println("From Activity:  "+message);

        DetailsFragment receiver= new DetailsFragment();

        if (!twoPanes) {
            getFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragment_nav, receiver)
                    .addToBackStack(null)
                    .commit();
            getFragmentManager()
                    .executePendingTransactions();

            System.out.println("got to here");
            receiver.setMessage(message, pos, context);
        }
        else{
            details.setMessage(message, pos, context);
        }
    }
}
