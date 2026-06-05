package com.example.alertblo;

import android.os.Bundle;
import android.provider.Settings;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
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


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        PrepararApp();

        crearAlerta = findViewById(R.id.btt_crear);
        textoAlerta = findViewById(R.id.alerta);
        tipoAlerta = findViewById(R.id.sw_critica);

        crearAlerta.setOnClickListener( v -> crearAlerta());
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

    // Funció que prepara l'app: obté l'ID del dispositiu, demana permisos i arranca el servei en segon pla.
    private void PrepararApp() {
        // Obté l'identificador únic d'aquest dispositiu
        ID_DISPOSITIU = Settings.Secure.getString(
                getContentResolver(),
                Settings.Secure.ANDROID_ID
        );

    }
}