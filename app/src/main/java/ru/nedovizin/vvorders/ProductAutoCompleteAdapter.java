package ru.nedovizin.vvorders;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import ru.nedovizin.vvorders.models.ClientLab;
import ru.nedovizin.vvorders.models.Product;

/** Вспомогательный класс для подбора продуктов
 *
 */
public class ProductAutoCompleteAdapter extends BaseAdapter implements Filterable {

    private final Context mContext;
    private List<ProductItem> mResults;

    public ProductAutoCompleteAdapter(Context context) {
        mContext = context;
        mResults = new ArrayList<ProductItem>();
    }

    @Override
    public int getCount() {
        return mResults.size();
    }

    @Override
    public ProductItem getItem(int index) {
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
        ProductItem productItem = getItem(position);
        Product product = productItem.product;
        TextView tv = convertView.findViewById(R.id.row_col3);
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
                    List<ProductItem> productItems = findProducts(constraint.toString());
                    // Assign the data to the FilterResults
                    filterResults.values = productItems;
                    filterResults.count = productItems.size();
                }
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                if (results != null && results.count > 0) {
                    mResults = (List<ProductItem>) results.values;
                    notifyDataSetChanged();
                } else {
                    notifyDataSetInvalidated();
                }
            }
        };

        return filter;
    }

    private List<ProductItem> findProducts(String productTitle) {
        List<Product> products = new ArrayList<>();
        List<ProductItem> productItems = new ArrayList<>();
        ClientLab clientLab = ClientLab.get(mContext);
        String quantity = "1";

        String product = productTitle.trim()
                .toLowerCase()
                .replace(" м ", " ( ");
        String[] words = product.split(" ");
        int idxEndWord = words.length - 1;
        if (idxEndWord > 0 && words[idxEndWord].matches("\\d*")) {
            // Если последнее слово - число и оно не первое
            quantity = words[idxEndWord];
            words = Arrays.copyOfRange(words, 0, idxEndWord);
            product = TextUtils.join(" ", words);
            products = clientLab.getProductsByLikeWords(product);
        }
        for (Product prod : products) {
            ProductItem prodItem = new ProductItem(prod);
            prodItem.quantity = quantity;
            productItems.add(prodItem);
        }
        return productItems;
    }
}
