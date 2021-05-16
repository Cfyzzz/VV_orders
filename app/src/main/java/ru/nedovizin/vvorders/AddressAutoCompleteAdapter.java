package ru.nedovizin.vvorders;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;

import java.util.List;

import ru.nedovizin.vvorders.models.Contragent;

public class AddressAutoCompleteAdapter extends BaseAdapter implements Filterable {

    private final Context mContext;
    private List<Contragent> mResults;

    public AddressAutoCompleteAdapter(Context context, List<Contragent> results) {
        mContext = context;
        mResults = results;
    }

    @Override
    public int getCount() {
        return 0;
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        return null;
    }

    @Override
    public Filter getFilter() {
        return null;
    }
}
