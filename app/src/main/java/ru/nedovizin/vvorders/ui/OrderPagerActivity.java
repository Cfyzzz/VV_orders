package ru.nedovizin.vvorders.ui;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.viewpager.widget.ViewPager;

import java.util.Date;
import java.util.List;

import ru.nedovizin.vvorders.R;
import ru.nedovizin.vvorders.models.ClientLab;
import ru.nedovizin.vvorders.models.Order;

public class OrderPagerActivity extends AppCompatActivity {
    public static final String EXTRA_ORDER_ID = "ru.nedovizin.vvorders.ui.order_id";
    public static final String EXTRA_DATE = "Date";

    private ViewPager mViewPager;
    private List<Order> mOrders;

    /** Получить настроенный интент активности
     *
     * @param packageContext Текущий контекст
     * @param orderId Поле код {@code code} текущей заявки
     * @param date Рабочая дата
     * @return Настроенный интент активности
     */
    public static Intent newIntent(Context packageContext, String orderId, Date date) {
        Intent intent = new Intent(packageContext, OrderPagerActivity.class);
        intent.putExtra(EXTRA_ORDER_ID, orderId);
        intent.putExtra(EXTRA_DATE, date);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_pager);
        mViewPager = findViewById(R.id.order_view_pager);

        String orderId = (String) getIntent().getSerializableExtra(EXTRA_ORDER_ID);
        Date date = (Date) getIntent().getSerializableExtra(EXTRA_DATE);

        mOrders = ClientLab.get(this).getOrdersByDate(date);
        FragmentManager fragmentManager = getSupportFragmentManager();
        mViewPager.setAdapter(new FragmentStatePagerAdapter(fragmentManager, FragmentStatePagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {
            @Override
            public Fragment getItem(int position) {
                Order order = mOrders.get(position);
                return OrderFragment.newInstance(order.code);
            }

            @Override
            public int getCount() {
                return mOrders.size();
            }
        });

        for (int i = 0; i < mOrders.size(); i++) {
            if (mOrders.get(i).code.equals(orderId)) {
                mViewPager.setCurrentItem(i);
                break;
            }
        }
    }
}
