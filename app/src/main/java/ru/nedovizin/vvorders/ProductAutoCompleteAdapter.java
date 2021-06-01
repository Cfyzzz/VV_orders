package ru.nedovizin.vvorders;

import android.content.Context;
import android.os.Build;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import androidx.annotation.RequiresApi;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import ru.nedovizin.vvorders.models.ClientLab;
import ru.nedovizin.vvorders.models.Product;

public class ProductAutoCompleteAdapter extends BaseAdapter implements Filterable {

    private final Context mContext;
    private List<Product> mResults;

    public ProductAutoCompleteAdapter(Context context) {
        mContext = context;
        mResults = new ArrayList<Product>();
    }

    @Override
    public int getCount() {
        return mResults.size();
    }

    @Override
    public Product getItem(int index) {
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
            convertView = inflater.inflate(R.layout.table_row, parent, false);
        }
        Product product = getItem(position);
        TextView tv = convertView.findViewById(R.id.col3);
        tv.setText(product.name);
        return convertView;
    }

    @Override
    public Filter getFilter() {
        Filter filter = new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                FilterResults filterResults = new FilterResults();
                if (constraint != null) {
                    List<Product> products = findProducts(constraint.toString());
                    // Assign the data to the FilterResults
                    filterResults.values = products;
                    filterResults.count = products.size();
                }
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                if (results != null && results.count > 0) {
                    mResults = (List<Product>) results.values;
                    notifyDataSetChanged();
                } else {
                    notifyDataSetInvalidated();
                }
            }};

        return filter;
    }

    private List<Product> findProducts(String productTitle) {
        List<Product> products = new ArrayList<>();
        ClientLab clietnLab = ClientLab.get(mContext);

        String product = productTitle.trim();
        String[] words = product.split(" ");
        int idxEndWord = words.length - 1;
        if (idxEndWord > 0 && words[idxEndWord].matches("\\d*")) {
            // Если последнее слово - число и оно не первое
            words = Arrays.copyOfRange(words, 0, idxEndWord);
            product = TextUtils.join(" ", words);
            products = clietnLab.getProductsByLikeWords(product);
        }
        return products;
    }
}
