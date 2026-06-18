package com.example.alertblo;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import com.google.android.material.switchmaterial.SwitchMaterial;

public class FragmentHome extends Fragment {
    private EditText textoAlerta;
    private SwitchMaterial tipoAlerta;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState){
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        // Vincular componentes del Layout fragment_home.xml
        textoAlerta = view.findViewById(R.id.alerta);
        tipoAlerta = view.findViewById(R.id.sw_critica);
        Button btnEnviar = view.findViewById(R.id.btt_crear);
        ImageButton btnSalir = view.findViewById(R.id.btn_salir);

        // Configurar acción para botón Enviar Alerta
        btnEnviar.setOnClickListener(v -> procesarCrearAlerta());

        btnSalir.setOnClickListener(v -> {
            if(getActivity() instanceof MainActivity){
                ((MainActivity) getActivity()).mostrarDialogoSalirPublico();
            }
        });
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState){
        super.onViewCreated(view, savedInstanceState);

        // Desenganchar de forma segura el RecyclerView
        if(MainActivity.alertasEnviadas != null && MainActivity.alertasEnviadas.getParent() != null){
            ((ViewGroup) MainActivity.alertasEnviadas.getParent()).removeView(MainActivity.alertasEnviadas);
        }
    }

    private void procesarCrearAlerta(){
        String texto = textoAlerta.getText().toString().trim();
        if(texto.isEmpty()){
            Toast.makeText(getContext(), "Error: Debe insertar una descripción.", Toast.LENGTH_SHORT).show();
            return;
        }

        int silencio = tipoAlerta.isChecked() ? 0 : 1;

        MainActivity.netSendThread.submit(() -> {
            boolean ok = Servidor.crearAlerta(MainActivity.ID_DISPOSITIU, texto, silencio);
            if(getActivity() != null){
                getActivity().runOnUiThread(() -> {
                    if(ok){
                        Toast.makeText(getContext(), "Alerta enviada.", Toast.LENGTH_SHORT).show();

                        // Guardar alerta local en memoria
                        Alerta nuevaAlerta = new Alerta(0, texto, silencio);
                        nuevaAlerta.setTimestamp(System.currentTimeMillis());

                        if(MainActivity.adaptador != null){
                            MainActivity.adaptador.addAlerta(nuevaAlerta);
                        }

                        // Limpiar campos del formulario
                        textoAlerta.setText("");
                        tipoAlerta.setChecked(false);
                    }else{
                        Toast.makeText(getContext(), "Error al enviar alerta", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }

}
