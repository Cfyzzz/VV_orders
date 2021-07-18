package ru.nedovizin.vvorders.ui;

import androidx.fragment.app.Fragment;

/** Фрагмент для главного окна
 *
 */
public class MainActivity extends SingleFragmentActivity {

    @Override
    protected Fragment createFragment() {
        return new ClientMainFragment();
    }
}