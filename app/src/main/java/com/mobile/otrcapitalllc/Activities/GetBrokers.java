package com.mobile.otrcapitalllc.Activities;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import android.app.Activity;
import android.app.IntentService;
import android.app.NotificationManager;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.support.v7.app.NotificationCompat;

import com.mobile.otrcapitalllc.Helpers.BrokerDatabase;
import com.mobile.otrcapitalllc.Helpers.CrashlyticsHelper;
import com.mobile.otrcapitalllc.Helpers.LogHelper;
import com.mobile.otrcapitalllc.Helpers.PreferenceManager;
import com.mobile.otrcapitalllc.Helpers.RestClient;
import com.mobile.otrcapitalllc.Models.Broker;
import com.mobile.otrcapitalllc.Models.CustomerViewModel;
import com.mobile.otrcapitalllc.R;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class GetBrokers extends IntentService {
    private static int id = 109574;
    NotificationManager mNotifyManager;
    NotificationCompat.Builder mBuilder;
    private int result = 0;

    public GetBrokers() {
        super("GetBrokers");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            LogHelper.logDebug("GetCustomers Service started");

            String date;
            long lastUpdateMillis = PreferenceManager.with(getApplicationContext()).getDbUpdateTimestamp();
            if (lastUpdateMillis == 0) {
                date = "1975/01/01";
            } else {
                date = new SimpleDateFormat("yyyy/MM/dd").format(new Date(lastUpdateMillis));
            }

            final String userCredentials = PreferenceManager.with(getApplicationContext()).getUserCredentials();

            mNotifyManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            mBuilder = new NotificationCompat.Builder(this);
            mBuilder.setContentTitle("Updating database").setContentText("Download in progress").setSmallIcon(R.drawable
                .ic_download);
            mBuilder.setProgress(100, 0, true);
            mNotifyManager.notify(id, mBuilder.build());

            RestClient restClient = new RestClient(this, userCredentials);
            restClient.getApiService().GetCustomers(date, new Callback<List<CustomerViewModel>>() {
                @Override
                public void success(List<CustomerViewModel> customerViewModels, Response response) {
                    LogHelper.logDebug("Broker list fetched from the server");

                    PreferenceManager.with(getApplicationContext()).saveDbUpdateTimestamp(System.currentTimeMillis());
                    List<Broker> brokers = new ArrayList<>();

                    for (CustomerViewModel cvm : customerViewModels) {
                        brokers.add(new Broker(cvm.McNumber, cvm.Name, cvm.PKey, cvm.Factorable));
                    }

                    //Save downloaded records to local database in a background AsyncTask
                    UpdateDatabase updateDB = new UpdateDatabase();
                    updateDB.execute(brokers);
                }

                @Override
                public void failure(RetrofitError error) {
                    CrashlyticsHelper.logException(error);
                    LogHelper.logError(error.toString());
                    result = Activity.RESULT_CANCELED;
                    publishResults(result);
                }
            });
        }
    }

    private void publishResults(int result) {

    }

    private class UpdateDatabase extends AsyncTask<List<Broker>, Integer, Integer> {
        BrokerDatabase brokerDB;
        List<Broker> newBrokerList;

        @Override
        protected Integer doInBackground(List<Broker>... params) {
            newBrokerList = brokerDB.GetNewBrokersList(params[0]);
            SQLiteDatabase dbWrite = brokerDB.OpenDB(BrokerDatabase.WRITEONLY);

            int totalCount = newBrokerList.size();
            int valuesSaved = 0;
            int percentOld = 0;
            int percentNew = 0;
            for (Broker b : newBrokerList) {
                ContentValues values = new ContentValues();
                values.put(BrokerDatabase.KEY_MC_NUMBER, b.get_mcnumber());
                values.put(BrokerDatabase.KEY_BROKER_NAME, b.get_brokerName());
                values.put(BrokerDatabase.KEY_PKEY, b.get_pKey());
                values.put(BrokerDatabase.KEY_FACTORABLE, b.isFactorable());
                brokerDB.PutBrokerName(dbWrite, values);
                valuesSaved++;
                percentNew = (valuesSaved * 100) / totalCount;
                if (percentNew > percentOld) {
                    percentOld = percentNew;
                    publishProgress(percentNew);
                }
            }

            dbWrite.close();
            LogHelper.logDebug("Records updated to local database");

            return null;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            brokerDB = new BrokerDatabase(GetBrokers.this);
            mBuilder.setProgress(100, 0, false).setContentText("Saving to local device");
            mNotifyManager.notify(id, mBuilder.build());
        }

        @Override
        protected void onPostExecute(Integer integer) {
            super.onPostExecute(integer);
            mBuilder.setContentText("Setup complete").setProgress(0, 0, false);
            mNotifyManager.notify(id, mBuilder.build());

        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);
            mBuilder.setProgress(100, values[0], false).setContentText(values[0] + "% done");
            mNotifyManager.notify(id, mBuilder.build());
        }
    }

}
