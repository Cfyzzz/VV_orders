package ru.nedovizin.vvorders;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;

import androidx.annotation.Nullable;

import java.util.List;

import ru.nedovizin.vvorders.models.ClientLab;
import ru.nedovizin.vvorders.models.Contragent;


// TODO - Пока не используется, но планируется для выделения получения из базы клиентов выделить
//  в отдельный сервис
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
        ClientLab clietnLab = ClientLab.get(getBaseContext());
        List<Contragent> clients = clietnLab.getClientsByLikeName(word);
        return clients;
    }
}