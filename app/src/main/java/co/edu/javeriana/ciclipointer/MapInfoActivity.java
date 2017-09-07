package co.edu.javeriana.ciclipointer;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

public class MapInfoActivity extends AppCompatActivity {

    private TextView Opcion;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map_info);
        Opcion = (TextView) findViewById(R.id.textViewInfoMap);
        Opcion.setText(getIntent().getStringExtra("infoo"));
    }
}
