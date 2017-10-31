package co.edu.javeriana.ciclipointer;

import android.content.Context;
import android.support.annotation.IdRes;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckedTextView;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import entities.Usuario;

public class UserAdapter extends ArrayAdapter<Usuario> {

    public UserAdapter(Context context, ArrayList<Usuario> users) {
        super(context, 0, users);
    }



    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Get the data item for this position
        Usuario user = getItem(position);
        // Check if an existing view is being reused, otherwise inflate the view
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.user_adapter, parent, false);
        }
        // Lookup view for data population
        ImageView im = (ImageView)convertView.findViewById(R.id.imageC);
        TextView tvName = (TextView) convertView.findViewById(R.id.nombre);
        TextView tvEmail = (TextView) convertView.findViewById(R.id.correo);
        final CheckedTextView checkedTextView = (CheckedTextView)convertView.findViewById(R.id.text1);
        // Populate the data into the template view using the data object
        tvName.setText(user.getNombre());
        tvEmail.setText(user.getCorreo());
        checkedTextView.setChecked(false);


        return convertView;
    }


}
