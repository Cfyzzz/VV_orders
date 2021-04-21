package ru.nedovizin.vvorders;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

public class MainActivity extends MenuActivity {

    private Button send_button;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        for (int i = 1; i < 20; i++) {
            TableLayout tableLayout = (TableLayout) findViewById(R.id.table_clients);
            LayoutInflater inflater = LayoutInflater.from(this);
            TableRow tr = (TableRow) inflater.inflate(R.layout.table_row, null);
            TextView tv = (TextView) tr.findViewById(R.id.col1);
            tv.setText((i%2==0)?"V":" ");
            tv = (TextView) tr.findViewById(R.id.col2);
            tv.setText(Integer.toString(i));
            tv = (TextView) tr.findViewById(R.id.col3);
            tv.setText("any predprinimatel any predprinimatel any predprinimatel");
            tableLayout.addView(tr);
        }

        send_button = (Button) findViewById(R.id.send_button);
        send_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), OrderActivity.class);
                startActivity(intent);
            }
        });
    }
}