package ru.nedovizin.vvorders;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

import ru.nedovizin.vvorders.models.Contragent;

public class ClientsListAdapter extends BaseAdapter {

    private List<Contragent> data;

    public ClientsListAdapter(List<Contragent> data) {
        this.data = data;
    }

    @Override
    public int getCount() {
        return data.size();
    }

    @Override
    public Object getItem(int position) {
        return data.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.table_row, parent, false);
//        TextView textView = (TextView) convertView.findViewById(R.id.col3);
//        textView.setText("New Layout");
        TextView tv = (TextView) convertView.findViewById(R.id.col1);
        tv.setText((position%2==0)?"V":" ");
        tv = (TextView) convertView.findViewById(R.id.col2);
        tv.setText(Integer.toString(position));
        tv = (TextView) convertView.findViewById(R.id.col3);
        tv.setText(data.get(position).name);
        return convertView;
    }
}
