package co.edu.javeriana.ciclipointer;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.icu.util.Calendar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class NuevoGrupalActivity extends AppCompatActivity {

    private RadioButton especial,normal;
    private TextView Thora,Tfecha;
    private int dia,mes, anno,hora,minutos;
    private DatePickerDialog.OnDateSetListener mDateSetListener;
    private TimePickerDialog.OnTimeSetListener mTimeSetLister;
    private List<String> arreglo = new ArrayList<>();
    private ListView listView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nuevo_grupal);

        especial = (RadioButton) findViewById(R.id.radioBEspecial);
        normal = (RadioButton) findViewById(R.id.radioBNormal);
        Thora = (TextView) findViewById(R.id.textViewHora);
        Tfecha = (TextView) findViewById(R.id.textViewFecha);

        Tfecha.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final Calendar c = Calendar.getInstance();
                dia = c.get(Calendar.DAY_OF_MONTH);
                mes = c.get(Calendar.MONTH);
                anno = c.get(Calendar.YEAR);

                DatePickerDialog dialos = new DatePickerDialog(NuevoGrupalActivity.this,
                        android.R.style.Theme_Holo_Light_Dialog_MinWidth,
                        mDateSetListener, anno,mes,dia);
                dialos.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                dialos.show();

            }
        });

        mDateSetListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                Tfecha.setText(day+"/"+(month+1)+"/"+year);
            }
        };

        Thora.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final Calendar c = Calendar.getInstance();
                hora = c.get(Calendar.HOUR_OF_DAY);
                minutos = c.get(Calendar.MINUTE);

                TimePickerDialog dialog = new TimePickerDialog(NuevoGrupalActivity.this,mTimeSetLister,hora,minutos,false);
                dialog.show();
            }
        });

        mTimeSetLister = new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker timePicker, int hour, int minute) {
                Thora.setText(hour+":"+minute);
            }
        };

        llenarLista();
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1, arreglo);

        listView = (ListView)findViewById(R.id.list_rutas_grupales);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent intent = new Intent(getBaseContext(), NuevoGrupalDetalleActivity.class);
                intent.putExtra("rutaGrupal",arreglo.get(i));
                startActivity(intent);
            }
        });

    }

    public void onRadioButtonClicked(View view) {
        boolean checked = ((RadioButton) view).isChecked();
        switch(view.getId()) {
            case R.id.radioBEspecial:
                if (checked){
                    normal.setChecked(false);
                    Toast.makeText(this, "Especial", Toast.LENGTH_SHORT).show();
                }
                    break;
            case R.id.radioBNormal:
                if (checked){
                    especial.setChecked(false);
                    Toast.makeText(this,"Normal", Toast.LENGTH_SHORT).show();
                }
                    break;
        }
    }

    private void llenarLista(){
        for(int i = 0; i < 30 ; i++){
            arreglo.add("ORIGEN/DESTINO/TIEMPO "+i);
        }
    }
}
