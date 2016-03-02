package com.mobile.otrcapital.Activities;

import android.app.Activity;
import android.app.IntentService;
import android.app.NotificationManager;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.support.v7.app.NotificationCompat;
import android.util.Log;

import com.mobile.otrcapital.Helpers.ActivityTags;
import com.mobile.otrcapital.Helpers.Broker;
import com.mobile.otrcapital.Helpers.BrokerDatabase;
import com.mobile.otrcapital.Helpers.CustomerViewModel;
import com.mobile.otrcapital.Helpers.RESTAPIs;
import com.mobile.otrcapital.Helpers.RestClient;
import com.mobile.otrcapital.R;

import java.util.ArrayList;
import java.util.List;

import retrofit.Callback;
import retrofit.RequestInterceptor;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class GetBrokers extends IntentService
{
    private static int id = 109574;
    NotificationManager mNotifyManager;
    NotificationCompat.Builder mBuilder;
    private int result = 0;
    private Context context;
    public GetBrokers()
    {
        super("GetBrokers");
    }

    @Override
    protected void onHandleIntent(Intent intent)
    {
        if (intent != null)
        {
            Log.d(ActivityTags.TAG_LOG,"GetCustomers Service started");
            String date = intent.getStringExtra(ActivityTags.TAG_DATE);
            SharedPreferences prefs = getSharedPreferences(ActivityTags.SHARED_PREFS_TAG, 0);
            final String userCredentials =  prefs.getString(ActivityTags.PREFS_USER_CREDENTIALS, "");

            mNotifyManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            mBuilder = new NotificationCompat.Builder(this);
            mBuilder.setContentTitle("Updating database")
                    .setContentText("Download in progress")
                    .setSmallIcon(R.drawable.ic_download);
            mBuilder.setProgress(100, 0, true);
            mNotifyManager.notify(id, mBuilder.build());

            RestClient.getInstance(userCredentials).getApiService().GetCustomers(date, new Callback<List<CustomerViewModel>>() {
                @Override
                public void success(List<CustomerViewModel> customerViewModels, Response response) {
                    Log.d(ActivityTags.TAG_LOG, "Broker list fetched from the server");
                    List<Broker> brokers = new ArrayList<Broker>();

                    for (CustomerViewModel cvm : customerViewModels)
                    {
                        brokers.add(new Broker(cvm.McNumber,cvm.Name,cvm.PKey));
                    }

                    //Save downloaded records to local database in a background AsyncTask
                    UpdateDatabase updateDB = new UpdateDatabase();
                    updateDB.execute(brokers);
                }

                @Override
                public void failure(RetrofitError error) {
                    Log.d(ActivityTags.TAG_LOG, error.toString());
                    result = Activity.RESULT_CANCELED;
                    publishResults (result);
                }
            });
        }
    }

    private void publishResults(int result) {

    }

    private class UpdateDatabase extends AsyncTask<List<Broker>,Integer,Integer>
    {
        BrokerDatabase brokerDB;
        List<Broker> newBrokerList;

        @Override
        protected Integer doInBackground(List<Broker>... params)
        {
            newBrokerList = brokerDB.GetNewBrokersList(params[0]);
            SQLiteDatabase dbWrite = brokerDB.OpenDB(BrokerDatabase.WRITEONLY);

            int totalCount = newBrokerList.size();
            int valuesSaved = 0;
            int percentOld = 0;
            int percentNew = 0;
            for (Broker b:newBrokerList)
            {
                ContentValues values = new ContentValues();
                values.put(BrokerDatabase.KEY_MC_NUMBER, b.get_mcnumber());
                values.put(BrokerDatabase.KEY_BROKER_NAME, b.get_brokerName());
                values.put(BrokerDatabase.KEY_PKEY, b.get_pKey());
                brokerDB.PutBrokerName(dbWrite, values);
                valuesSaved++;
                percentNew = (valuesSaved*100)/totalCount;
                if(percentNew>percentOld)
                {
                    percentOld = percentNew;
                    publishProgress(percentNew);
                }
                Log.d(ActivityTags.TAG_LOG,"%ge: " + percentNew + " pKey: " + b.get_pKey() + "\t MC number: " + b.get_mcnumber() + "\tbroker name: " + b.get_brokerName());
            }

            dbWrite.close();
            Log.d(ActivityTags.TAG_LOG,"Records updated to local database");
            return null;
        }

        @Override
        protected void onPreExecute()
        {
            super.onPreExecute();
            brokerDB = new BrokerDatabase(GetBrokers.this);
            mBuilder.setProgress(100, 0, false)
                    .setContentText("Saving to local device");
            mNotifyManager.notify(id, mBuilder.build());
        }

        @Override
        protected void onPostExecute(Integer integer)
        {
            super.onPostExecute(integer);
            mBuilder.setContentText("Setup complete")
                    .setProgress(0,0,false);
            mNotifyManager.notify(id, mBuilder.build());

        }

        @Override
        protected void onProgressUpdate(Integer... values)
        {
            super.onProgressUpdate(values);
            mBuilder.setProgress(100, values[0], false)
                    .setContentText(values[0] + "% done");
            mNotifyManager.notify(id, mBuilder.build());
        }
    }


}
