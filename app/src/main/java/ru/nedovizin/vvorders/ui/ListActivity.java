package ru.nedovizin.vvorders.ui;

import android.os.Bundle;

import ru.nedovizin.vvorders.R;


public class ListActivity extends MenuActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);
    }

    @Override
    protected void onPause() {
        super.onPause();

        // TODO - здесь нужно сохранить базу ClientLab
//        ClientLab.get(getActivity())
//                .updateCrime(mCrime);
    }
}