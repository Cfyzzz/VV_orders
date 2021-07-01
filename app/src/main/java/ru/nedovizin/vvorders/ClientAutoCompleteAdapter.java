package ru.nedovizin.vvorders;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import ru.nedovizin.vvorders.models.ClientLab;
import ru.nedovizin.vvorders.models.Contragent;

public class ClientAutoCompleteAdapter extends BaseAdapter implements Filterable {

    private static final int MAX_RESULTS = 10;

    private final Context mContext;
    private List<Contragent> mResults;

//    private ServiceConnection mServiceConnection = new ServiceConnection() {
//        @Override
//        public void onServiceConnected(ComponentName name, IBinder service) {
//            ClientsService.MyBinder binder = (ClientsService.MyBinder) service;
//            ClientsService clientsService = binder.getService();
//        }
//
//        @Override
//        public void onServiceDisconnected(ComponentName name) {
//
//        }
//    };

    public ClientAutoCompleteAdapter(Context context) {
        mContext = context;
        mResults = new ArrayList<Contragent>();
    }

    @Override
    public int getCount() {
        return mResults.size();
    }

    @Override
    public Contragent getItem(int index) {
        return mResults.get(index);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            LayoutInflater inflater = LayoutInflater.from(mContext);
            convertView = inflater.inflate(R.layout.table_simple_row, parent, false);
        }
        Contragent client = getItem(position);
        TextView tv = (TextView) convertView.findViewById(R.id.row_col3);
        tv.setText(client.name);
        return convertView;
    }

    @Override
    public Filter getFilter() {
        Filter filter = new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                FilterResults filterResults = new FilterResults();
                if (constraint != null) {
                    List<Contragent> clients = findClients(constraint.toString());
                    // Assign the data to the FilterResults
                    filterResults.values = clients;
                    filterResults.count = clients.size();
                }
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                if (results != null && results.count > 0) {
                    mResults = (List<Contragent>) results.values;
                    notifyDataSetChanged();
                } else {
                    notifyDataSetInvalidated();
                }
            }};

        return filter;
    }

    /**
     * Returns a search result for the given book title.
     */
    private List<Contragent> findClients(String clientTitle) {
        ClientLab clietnLab = ClientLab.get(mContext);
        List<Contragent> clients = clietnLab.getClientsByLikeName(clientTitle);
        return clients;
    }
}


// ref: https://habr.com/ru/post/243853/
