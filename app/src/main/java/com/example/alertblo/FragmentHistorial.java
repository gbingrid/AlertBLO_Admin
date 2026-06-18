package com.example.alertblo;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import com.google.android.material.chip.Chip;

public class FragmentHistorial extends Fragment{

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState){

        View view = inflater.inflate(R.layout.fragment_historial, container, false);

        Chip todas = view.findViewById(R.id.chipTodas);
        Chip critica = view.findViewById(R.id.chipAlertaCritica);
        Chip aviso = view.findViewById(R.id.chipAlertaNormal);

        if(todas != null){
            todas.setOnClickListener(v -> {
                if(MainActivity.adaptador != null) MainActivity.adaptador.filtrarAlerta("todas");
            });
        }

        if(critica != null){
            critica.setOnClickListener(v -> {
                if(MainActivity.adaptador != null) MainActivity.adaptador.filtrarAlerta("critica");
            });
        }

        if(aviso != null){
            aviso.setOnClickListener(v -> {
                if(MainActivity.adaptador != null) MainActivity.adaptador.filtrarAlerta("aviso");
            });
        }

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState){
        super.onViewCreated(view, savedInstanceState);

        ViewGroup contenedorLista = view.findViewById(R.id.contenedor_lista_historial);

        if(MainActivity.alertasEnviadas != null && MainActivity.alertasEnviadas.getParent() != null){
            ((ViewGroup) MainActivity.alertasEnviadas.getParent()).removeView(MainActivity.alertasEnviadas);
        }

        if(MainActivity.alertasEnviadas != null){

            contenedorLista.addView(MainActivity.alertasEnviadas);

            if(MainActivity.adaptador != null){
                MainActivity.adaptador.filtrarAlerta("todas");
                MainActivity.adaptador.notifyDataSetChanged();
            }

        }





    }
}
