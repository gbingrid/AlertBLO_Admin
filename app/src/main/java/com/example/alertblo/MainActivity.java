package com.example.alertblo;

import android.content.Context;
import android.os.Bundle;
import android.provider.Settings;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.switchmaterial.SwitchMaterial;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MainActivity extends AppCompatActivity {
    public static String ID_DISPOSITIU;    // ID del dispositiu que s'enviarà al servidor
    public static final String IP_SERVIDOR = "http://13.63.226.223"; // IP del servidor
    public static ExecutorService netSendThread = Executors.newSingleThreadExecutor();
    public static Adaptador adaptador;
    public static RecyclerView alertasEnviadas;

    // Aplica el idioma antes de que se infle el layout
    @Override
    protected void attachBaseContext(Context base){
        super.attachBaseContext(GestorIdioma.cargarIdiomaGuardado(base));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        PrepararApp();

        alertasEnviadas = new RecyclerView(this);
        alertasEnviadas.setLayoutManager(new LinearLayoutManager(this));
        adaptador = new Adaptador();

        adaptador.cargarAlertasDelDispositivo(this);
        alertasEnviadas.setAdapter(adaptador);

        // Cargar FragmentHome por defecto
        if(savedInstanceState == null){
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.contenedor_fragmentos, new FragmentHome())
                    .commit();
        }

        // Configurar la barra de navegación inferior
        BottomNavigationView bottomNavigationView = findViewById(R.id.btn_navegacion);
        bottomNavigationView.setOnNavigationItemSelectedListener(item -> {
            androidx.fragment.app.Fragment fragmentSelecionado = null;
            int id = item.getItemId();

            if(id == R.id.ic_nav_home){
                fragmentSelecionado = new FragmentHome();
            }else if(id == R.id.ic_nav_historial){
                fragmentSelecionado = new FragmentHistorial();
            }else if(id == R.id.ic_nav_idioma){
                mostrarDialogoIdioma();
                return false;
            }

            if(fragmentSelecionado != null){
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.contenedor_fragmentos, fragmentSelecionado)
                        .commit();
                return true;
            }
            return false;
        });
    }

    private void mostrarDialogoIdioma(){
        Spinner spinner = new Spinner(this);

        // Añadir margen interno
        int paddingPx = (int) (16 * getResources().getDisplayMetrics().density);
        spinner.setPadding(paddingPx, paddingPx, paddingPx, paddingPx);

        // Usar la clase GestorIdioma pasando el Spinner creado y el Activity
        GestorIdioma.configurarSpinner(spinner, this);

        // Construir y lanzar AlertDialog
        new AlertDialog.Builder(this)
                .setTitle(getString(R.string.tit_menu_idioma))
                .setView(spinner)
                .setPositiveButton(getString(R.string.txt_aceptar), (dialog, which) -> {
                    if(adaptador != null){
                        adaptador.guardarAlertasEnDispositivo(this);
                    }
                    dialog.dismiss();
                })
                .show();
    }

    // ALERTDIALOG PARA MOSTRAR DIALOGO SALIR
    private void mostrarDialogoSalir(){
        new AlertDialog.Builder(this)
                .setTitle(getString(R.string.txt_salir))
                .setMessage(getString(R.string.txt_confirmar_salir))
                .setPositiveButton(getString(R.string.txt_si), (d, w) -> finishAffinity())
                .setNegativeButton(getString(R.string.txt_cancelar), null)
                .show();
    }


    @Override
    protected void onDestroy(){
        if(adaptador != null){
            adaptador.guardarAlertasEnDispositivo(this);
        }
        super.onDestroy();
    }

    // Funció que prepara l'app: obté l'ID del dispositiu, demana permisos i arranca el servei en segon pla.
    private void PrepararApp() {
        // Obté l'identificador únic d'aquest dispositiu
        ID_DISPOSITIU = Settings.Secure.getString(
                getContentResolver(),
                Settings.Secure.ANDROID_ID
        );

    }

    public void mostrarDialogoSalirPublico(){
        mostrarDialogoSalir();
    }
}