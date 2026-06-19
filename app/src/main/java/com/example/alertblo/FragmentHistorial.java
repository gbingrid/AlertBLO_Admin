package com.example.alertblo;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import com.google.android.material.chip.Chip;

public class FragmentHistorial extends Fragment{

    private Chip todas, critica, aviso;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState){

        View view = inflater.inflate(R.layout.fragment_historial, container, false);

        todas = view.findViewById(R.id.chipTodas);
        critica = view.findViewById(R.id.chipAlertaCritica);
        aviso = view.findViewById(R.id.chipAlertaNormal);

        if(todas != null){
            todas.setOnClickListener(v -> {
                actualizarEstadosFiltros(todas);
                if(MainActivity.adaptador != null) MainActivity.adaptador.filtrarAlerta("todas");
            });
        }

        if(critica != null){
            critica.setOnClickListener(v -> {
                actualizarEstadosFiltros(critica);
                if(MainActivity.adaptador != null) MainActivity.adaptador.filtrarAlerta("critica");
            });
        }

        if(aviso != null){
            aviso.setOnClickListener(v -> {
                actualizarEstadosFiltros(aviso);
                if(MainActivity.adaptador != null) MainActivity.adaptador.filtrarAlerta("aviso");
            });
        }

        if(todas != null){
            actualizarEstadosFiltros(todas);
        }

        // Configuración del botón Salir
        ImageButton btnSalir = view.findViewById(R.id.btn_salir);

        // Al hacer click, se llama al método público creado en MainActivity
        btnSalir.setOnClickListener(v -> {
            if (getActivity() instanceof MainActivity) {
                ((MainActivity) getActivity()).mostrarDialogoSalirPublico();
            }
        });
        return view;
    }

    // Actualizar estado de los filtros
    private void actualizarEstadosFiltros(Chip chipActivo) {
        if (todas == null || critica == null || aviso == null) return;

        todas.setChecked(false);
        critica.setChecked(false);
        aviso.setChecked(false);

        chipActivo.setChecked(true);

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
