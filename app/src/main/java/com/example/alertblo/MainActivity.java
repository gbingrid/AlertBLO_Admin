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
import com.google.android.material.switchmaterial.SwitchMaterial;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MainActivity extends AppCompatActivity {
    public static String ID_DISPOSITIU;    // ID del dispositiu que s'enviarà al servidor
    public static final String IP_SERVIDOR = "http://13.63.226.223"; // IP del servidor
    public static ExecutorService netSendThread = Executors.newSingleThreadExecutor();
    private Button crearAlerta;
    private EditText textoAlerta;
    private SwitchMaterial tipoAlerta;
    private Spinner spnIdioma;
    private ImageButton btnSalir;


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

        crearAlerta = findViewById(R.id.btt_crear);
        textoAlerta = findViewById(R.id.alerta);
        tipoAlerta = findViewById(R.id.sw_critica);
        spnIdioma = findViewById(R.id.spn_idioma);
        btnSalir = findViewById(R.id.btn_salir);

        crearAlerta.setOnClickListener( v -> crearAlerta());
        btnSalir.setOnClickListener(v -> mostrarDialogoSalir());

        GestorIdioma.configurarSpinner(spnIdioma, this);
    }

    // Crea una nova alerta al servidor
    private void crearAlerta() {
        String texto = textoAlerta.getText().toString().trim();

        if(texto.isEmpty()){
            Toast.makeText(this, "Error: Debe insertar una descripción.", Toast.LENGTH_SHORT).show();
            return;
        }

        int silencio = tipoAlerta.isChecked() ? 0 : 1;

        netSendThread.submit(() -> {
            // Cridar al servidor per crear l'alerta
            boolean ok = Servidor.crearAlerta(ID_DISPOSITIU, texto, silencio);

            runOnUiThread(() -> {
                if(ok){
                    Toast.makeText(this, "Alerta creada.", Toast.LENGTH_SHORT).show();
                    textoAlerta.setText("");
                    tipoAlerta.setChecked(false);
                } else {
                    Toast.makeText(this, "Error al crear alerta", Toast.LENGTH_SHORT).show();
                }
            });
        });
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


    // Funció que prepara l'app: obté l'ID del dispositiu, demana permisos i arranca el servei en segon pla.
    private void PrepararApp() {
        // Obté l'identificador únic d'aquest dispositiu
        ID_DISPOSITIU = Settings.Secure.getString(
                getContentResolver(),
                Settings.Secure.ANDROID_ID
        );

    }
}