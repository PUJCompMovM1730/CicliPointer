package services;

import android.app.IntentService;
import android.content.Intent;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Date;
import java.util.StringTokenizer;

import co.edu.javeriana.ciclipointer.NuevoProgramadoActivity;
import entities.RutaProgramada;

/**
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 * <p>
 * TODO: Customize class - update intent actions, extra parameters and static
 * helper methods.
 */
public class ProgramadoService extends IntentService {

    private DatabaseReference myRef;
    private FirebaseAuth mAuth;
    private boolean trabajando = true;
    private FirebaseUser user = null;
    private int min =0;
    private int menor = 100;

    public ProgramadoService() {
        super("ProgramandoService");

        mAuth =	FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();
        myRef = FirebaseDatabase.getInstance().getReference();
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        try {
             while(trabajando){
                myRef.child("programados/"+user.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        for(DataSnapshot single:dataSnapshot.getChildren()){
                            RutaProgramada ru = single.getValue(RutaProgramada.class);
                            StringTokenizer tok = new StringTokenizer(ru.getFecha(),"/");
                            int year,month,day,hora,minutos;
                            day = Integer.parseInt(tok.nextToken());
                            month = Integer.parseInt(tok.nextToken())-1;
                            year = Integer.parseInt(tok.nextToken())-1900;
                            tok = new StringTokenizer(ru.getHora(),":");
                            hora =Integer.parseInt( tok.nextToken());
                            minutos = Integer.parseInt( tok.nextToken());
                            Date date = new Date(year,month,day);
                            date.setHours(hora);
                            date.setMinutes(minutos);
                            Date dateActual = new Date();
                           // System.err.println("valor es comparando: "+dateActual.compareTo(date));
                           // System.err.println("valor es  "+date.toString());
                           // System.err.println("valor es de actual "+dateActual.toString());

                            if(year == dateActual.getYear()&&
                                    month == dateActual.getMonth()&&
                                    day == dateActual.getDate()){
                                if(dateActual.getHours()<hora){

                                }else if(dateActual.getHours()>hora){
                                    borrarAntiguo(single.getKey());
                                }else if(dateActual.getHours() == hora){
                                    if(dateActual.getMinutes()<minutos){
                                        if(minutos-dateActual.getMinutes()<5&&minutos-dateActual.getMinutes()<menor){
                                            menor = minutos-dateActual.getMinutes();
                                            min = minutos-dateActual.getMinutes();
                                            System.out.println("valor es min 1 "+min);
                                            Toast.makeText(ProgramadoService.this, "En "+min+" inicia recorrido", Toast.LENGTH_SHORT).show();
                                        }else
                                            min = 0;
                                    }else if(dateActual.getMinutes()==minutos){
                                        Intent intent = new Intent(ProgramadoService.this, NuevoProgramadoActivity.class);
                                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                        intent.putExtra("latOrigen",ru.getLatOrigen());
                                        intent.putExtra("longOrigen",ru.getLongOrigen());
                                        intent.putExtra("latDesti",ru.getLatDestino());
                                        intent.putExtra("longDesti",ru.getLongDestino());
                                        intent.putExtra("ruta",ru.getRuta());
                                        intent.putExtra("tipo",ru.getTipo());
                                        intent.putExtra("key",single.getKey());
                                        trabajando = false;
                                        startActivity(intent);
                                        break;
                                    }else if(dateActual.getMinutes()>minutos){
                                        borrarAntiguo(single.getKey());
                                    }
                                }
                            }else{
                                if(dateActual.getYear()<=year){
                                    if(dateActual.getYear()==year){
                                        if(dateActual.getMonth()<=month){
                                            if(dateActual.getMonth()==month){
                                                if(dateActual.getDate()<=day){
                                                    if(dateActual.getDate()==day){
                                                        if(dateActual.getHours()<hora){

                                                        }else if(dateActual.getHours()>hora){
                                                            borrarAntiguo(single.getKey());
                                                        }else if(dateActual.getHours() == hora){
                                                            if(dateActual.getMinutes()<minutos){
                                                                if(minutos-dateActual.getMinutes()<5&&minutos-dateActual.getMinutes()<menor){
                                                                    menor = minutos-dateActual.getMinutes();
                                                                    min = minutos-dateActual.getMinutes();
                                                                    System.out.println("valor es min 2 "+min);
                                                                    Toast.makeText(ProgramadoService.this, "En "+min+" inicia recorrido", Toast.LENGTH_SHORT).show();
                                                                }else
                                                                    min = 0;
                                                            }else if(dateActual.getMinutes()==minutos){
                                                                Intent intent = new Intent(ProgramadoService.this, NuevoProgramadoActivity.class);
                                                                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                                                intent.putExtra("latOrigen",ru.getLatOrigen());
                                                                intent.putExtra("longOrigen",ru.getLongOrigen());
                                                                intent.putExtra("latDesti",ru.getLatDestino());
                                                                intent.putExtra("longDesti",ru.getLongDestino());
                                                                intent.putExtra("ruta",ru.getRuta());
                                                                intent.putExtra("tipo",ru.getTipo());
                                                                intent.putExtra("key",single.getKey());
                                                                trabajando = false;
                                                                startActivity(intent);
                                                                break;
                                                            }else if(dateActual.getMinutes()>minutos){
                                                                borrarAntiguo(single.getKey());
                                                            }
                                                        }
                                                    }
                                                }else{
                                                    borrarAntiguo(single.getKey());
                                                }
                                            }
                                        }else{
                                            borrarAntiguo(single.getKey());
                                        }
                                    }
                                }else{
                                    borrarAntiguo(single.getKey());
                                }
                            }


                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });

                 int seg;
                 System.out.println("valor es min 3 "+min);
                 if(min == 0){
                     seg = 5;
                 }else{
                     seg = min*60;
                 }
                 int milis = seg*1000;
                 System.out.println("valor es "+milis);
                Thread.sleep(milis);
            }

        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    private void borrarAntiguo(String key){
        myRef.child("programados/"+user.getUid()+"/"+key).removeValue();
    }
}
