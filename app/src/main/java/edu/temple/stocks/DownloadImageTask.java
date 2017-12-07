package edu.temple.stocks;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.ImageView;

import java.io.InputStream;

/**
 * Created by Shiloh on 12/5/2017.
 */
    //class written to retrieve stock chart from url


    public class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
        ImageView stockChart;

        public DownloadImageTask(ImageView stockChart) {
            this.stockChart = stockChart;
        }

        protected Bitmap doInBackground(String... urls) {
            String urlDisplay = urls[0];
            Bitmap bitmapStock = null;
            try {
                InputStream in = new java.net.URL(urlDisplay).openStream();
                bitmapStock = BitmapFactory.decodeStream(in);
            } catch (Exception e) {
                Log.e("Error", e.getMessage());
                e.printStackTrace();
            }
            return bitmapStock;
        }

        protected void onPostExecute(Bitmap result) {
            stockChart.setImageBitmap(result);
        }
    }

