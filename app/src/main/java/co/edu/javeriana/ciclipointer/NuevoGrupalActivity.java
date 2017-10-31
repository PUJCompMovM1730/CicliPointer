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
import java.util.Date;
import java.util.List;

public class NuevoGrupalActivity extends AppCompatActivity {

    private RadioButton especial,normal;
    private TextView tHora, tFecha;
    private Button continuar;
    private DatePickerDialog.OnDateSetListener mDateSetListener;
    private TimePickerDialog.OnTimeSetListener mTimeSetLister;
    private String fecha = "",time="",tipoGru = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nuevo_grupal);

        especial = (RadioButton) findViewById(R.id.radioBEspecial);
        normal = (RadioButton) findViewById(R.id.radioBNormal);
        tHora = (TextView) findViewById(R.id.textViewHoraProgramado);
        tFecha = (TextView) findViewById(R.id.textViewFechaProgramado);
        continuar = (Button) findViewById(R.id.buttonContinuarMap);
        Date date = new Date();
        final int dia = date.getDate()+1,mes=date.getMonth(),
                anno=(date.getYear()+1900),hora=date.getHours(),minutos=date.getMinutes();
        tHora.setClickable(false);
        tFecha.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
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
                Date date = new Date();
                //System.err.println("valor es dia:"+date.getYear()+"/"+date.getMonth()+"/"+date.getDate());
                //System.err.println("valor es dia2:"+year+"/"+month+"/"+day);
                Date se = new Date(year-1900,month,day);
                if(se.compareTo(date)==1){
                    fecha = day+"/"+(month+1)+"/"+year;
                }else{
                    fecha = "";
                }
                if(fecha.equals("")) {
                    Toast.makeText(NuevoGrupalActivity.this, "Fecha invalida", Toast.LENGTH_SHORT).show();
                    tFecha.setText("Seleccionar fecha");
                }
                else {
                    tFecha.setText(fecha);
                    tHora.setClickable(true);
                }

            }
        };

        tHora.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                TimePickerDialog dialog = new TimePickerDialog(NuevoGrupalActivity.this,mTimeSetLister,hora,minutos,false);
                dialog.show();
            }
        });

        mTimeSetLister = new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker timePicker, int hour, int minute) {
                time = hour+":"+minute;
                tHora.setText(time);
            }
        };

        continuar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!fecha.equals("")&&!time.equals("")&&!tipoGru.equals("")){
                    Intent intent = new Intent(getApplicationContext(),NuevoGrupalDetalleActivity.class);
                    intent.putExtra("fecha",fecha);
                    intent.putExtra("hora",time);
                    intent.putExtra("tipo","Programado-Grupal");
                    intent.putExtra("tipoGrup",tipoGru);
                    startActivity(intent);
                }else{
                    Toast.makeText(NuevoGrupalActivity.this, "Primero seleccione fecha, hora y tipo de ruta", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
    public void onRadioButtonClicked(View view) {
        boolean checked = ((RadioButton) view).isChecked();
        switch(view.getId()) {
            case R.id.radioBEspecial:
                if (checked){
                    normal.setChecked(false);
                    tipoGru = "Especial";
                }
                    break;
            case R.id.radioBNormal:
                if (checked){
                    especial.setChecked(false);
                    tipoGru = "Normal";
                }
                    break;
        }
    }


}
