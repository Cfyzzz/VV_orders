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

/** Абстрактный вспомогательный класс, реализующий подбор объектов
 *
 * @param <T> Тип объекта
 */
public abstract class AutoCompleteAdapter<T> extends BaseAdapter implements Filterable {

    private final Context mContext;
    private List<T> mResults;

    /** Функция возвращает строковое представление объекта
     *
     * @param entity Объект
     * @return Строковое представление объекта
     */
    public abstract String getNameEntity(T entity);

    /** Функция поиска объектов, должна возвращать список объектов согласно шаблону
     *
     * @param template Шаблон
     * @return Список объектов в соответствии с шаблоном
     */
    public abstract List<T> findEntities(String template);

    public AutoCompleteAdapter(Context context) {
        mContext = context;
        mResults = new ArrayList<T>();
    }

    public Context getContext() {
        return mContext;
    }

    @Override
    public int getCount() {
        return mResults.size();
    }

    @Override
    public T getItem(int index) {
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
        T entity = getItem(position);
        TextView tv = (TextView) convertView.findViewById(R.id.row_col3);
        tv.setText(getNameEntity(entity));
        return convertView;
    }

    @Override
    public Filter getFilter() {
        Filter filter = new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                FilterResults filterResults = new FilterResults();
                if (constraint != null) {
                    List<T> entities = findEntities(constraint.toString());
                    // Assign the data to the FilterResults
                    filterResults.values = entities;
                    filterResults.count = entities.size();
                }
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                if (results != null && results.count > 0) {
                    //noinspection unchecked
                    mResults = (List<T>) results.values;
                    notifyDataSetChanged();
                } else {
                    notifyDataSetInvalidated();
                }
            }};

        return filter;
    }
}
