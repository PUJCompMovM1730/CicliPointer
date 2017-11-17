package co.edu.javeriana.ciclipointer;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

public class ProgrmarMarcadorActivity extends AppCompatActivity {


    private Button continuar;
    private TextView tFecha;
    private EditText tNombre,tDescripcion;
    private String fecha = "",nombre = "", descripcion = "";
    private DatePickerDialog.OnDateSetListener mDateSetListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_progrmar_marcador);

        tFecha = (TextView) findViewById(R.id.textViewFechaProgramado);
        tNombre = (EditText) findViewById(R.id.textViewNombre);
        tDescripcion = (EditText) findViewById(R.id.textViewDescripcion);

        continuar = (Button) findViewById(R.id.buttonContinuarMap);
        Date date = new Date();
        final int dia = date.getDate()+1,mes=date.getMonth(),
                anno=(date.getYear()+1900),hora=date.getHours(),minutos=date.getMinutes();
        tFecha.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DatePickerDialog dialos = new DatePickerDialog(ProgrmarMarcadorActivity.this,
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
                    Toast.makeText(ProgrmarMarcadorActivity.this, "Fecha invalida", Toast.LENGTH_SHORT).show();
                    tFecha.setText("Seleccionar fecha");
                }
                else {
                    tFecha.setText(fecha);
                }

            }
        };

        continuar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                nombre = tNombre.getText().toString();
                descripcion = tDescripcion.getText().toString();
                if(!fecha.equals("")&&!nombre.equals("")&&!descripcion.equals("")){
                    Intent intent = new Intent(getApplicationContext(),MarcadorMapActivity.class);
                    intent.putExtra("fecha",fecha);
                    intent.putExtra("nombre",nombre);
                    intent.putExtra("descripcion",descripcion);
                    startActivity(intent);
                }else{
                    Toast.makeText(ProgrmarMarcadorActivity.this, "Primero seleccione fecha, nombre y descripción", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }


}
