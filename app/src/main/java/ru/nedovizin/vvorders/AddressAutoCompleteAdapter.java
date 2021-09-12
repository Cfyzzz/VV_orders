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

import ru.nedovizin.vvorders.models.Address;
import ru.nedovizin.vvorders.models.ClientLab;

/** Вспомогательный класс для подбора адресов
 *
 */
public class AddressAutoCompleteAdapter extends BaseAdapter implements Filterable {

    private final Context mContext;
    private List<Address> mResults;

    public AddressAutoCompleteAdapter(Context context) {
        mContext = context;
        mResults = new ArrayList<Address>();
    }

    @Override
    public int getCount() {
        return mResults.size();
    }

    @Override
    public Address getItem(int index) {
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
        Address address = getItem(position);
        TextView tv = (TextView) convertView.findViewById(R.id.row_col3);
        tv.setText(address.name);
        return convertView;
    }

    @Override
    public Filter getFilter() {
        Filter filter = new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                FilterResults filterResults = new FilterResults();
                if (constraint != null) {
                    List<Address> addresses = findAddresses(constraint.toString());
                    // Assign the data to the FilterResults
                    filterResults.values = addresses;
                    filterResults.count = addresses.size();
                }
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                if (results != null && results.count > 0) {
                    mResults = (List<Address>) results.values;
                    notifyDataSetChanged();
                } else {
                    notifyDataSetInvalidated();
                }
            }};

        return filter;
    }

    private List<Address> findAddresses(String addressTitle) {
        ClientLab clietnLab = ClientLab.get(mContext);
        List<Address> addresses = clietnLab.getAddressesByLikeName(addressTitle);
        return addresses;
    }
}
