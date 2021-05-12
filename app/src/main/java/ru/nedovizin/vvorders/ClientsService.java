package ru.nedovizin.vvorders;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;

import androidx.annotation.Nullable;

import java.util.List;


public class ClientsService extends Service {

    private Context mContext;
    private int mMaxResults;

    private Binder myBinder = new MyBinder();

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return myBinder;
    }

    public class MyBinder extends  Binder {
        ClientsService getService() {
            return ClientsService.this;
        }
    }

    public ClientsService(Context context, int maxResults) {
        mContext = context;
        mMaxResults = maxResults;
    }

    public List<Contragent> findClients(String word) {
        ClientLab clietnLab = new ClientLab(getBaseContext());
        List<Contragent> clients = clietnLab.getClientsByLikeName(word);
        return clients;
    }
}