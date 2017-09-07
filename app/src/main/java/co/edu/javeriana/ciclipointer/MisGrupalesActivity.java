package co.edu.javeriana.ciclipointer;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class MisGrupalesActivity extends AppCompatActivity {

    private RadioButton Anfitrion, Invitado;
    private List<String> invitado = new ArrayList<>();
    private List<String> propios = new ArrayList<>();
    private ListView listView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mis_grupales);
        Anfitrion = (RadioButton) findViewById(R.id.radioBAnfi);
        Invitado = (RadioButton) findViewById(R.id.radioBInvi);
        listView = (ListView)findViewById(R.id.list_mis_grupales);

    }

    public void onRadioButtonClicked(View view) {
        boolean checked = ((RadioButton) view).isChecked();
        switch(view.getId()) {
            case R.id.radioBAnfi:
                if (checked){
                    Invitado.setChecked(false);
                    Toast.makeText(this, "Anfitrión", Toast.LENGTH_SHORT).show();
                    llenarAnfi();
                    ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                            android.R.layout.simple_list_item_1, propios);
                    listView.setAdapter(adapter);
                    listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                            Intent intent = new Intent(getBaseContext(), MisGrupalesAnfitrionActivity.class);
                            intent.putExtra("info",propios.get(i));
                            startActivity(intent);
                        }
                    });
                }
                break;
            case R.id.radioBInvi:
                if (checked){
                    Anfitrion.setChecked(false);
                    Toast.makeText(this,"Invitado", Toast.LENGTH_SHORT).show();
                    llenarInvi();
                    ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                            android.R.layout.simple_list_item_1, invitado);
                    listView.setAdapter(adapter);
                    listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                            Intent intent = new Intent(getBaseContext(), MisGrupalesInvitadoActivity.class);
                            intent.putExtra("info",invitado.get(i));
                            startActivity(intent);
                        }
                    });
                }
                break;
        }
    }

    private void llenarInvi(){
        for(int i = 0; i < 30 ; i++){
            invitado.add("Usuario/ORIGEN/DESTINO "+i);
        }
    }

    private void llenarAnfi(){
        for(int i = 0; i < 30 ; i++){
            propios.add("ORIGEN/DESTINO/Fecha "+i);
        }
    }

}
