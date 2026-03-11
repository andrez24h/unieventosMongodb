package dev.andresm.unieventosMongodb.config;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.FileInputStream;
import java.io.IOException;


/**
 * Clase de configuración para inicializar Firebase en la aplicación.

 * Se encarga de:
 * - Cargar las credenciales del Service Account.
 * - Configurar el bucket de almacenamiento.
 * - Inicializar la instancia de FirebaseApp como Bean de Spring.

 * IMPORTANTE:
 * El archivo JSON de credenciales NO debe subirse al repositorio.
 */
@Configuration
public class FirebaseConfig {

    /**
     * Inicializa FirebaseApp si aún no ha sido creada.
     *
     * @return instancia de FirebaseApp registrada en el contexto de Spring
     * @throws IOException si ocurre un error al leer el archivo de credenciales
     */
    @Bean
    public FirebaseApp initializeFirebase() throws IOException {

        // Carga el archivo JSON de la cuenta de servicio
        FileInputStream serviceAccount = new FileInputStream(
                "src/main/resources/unieventosmongodb-firebase-adminsdk-fbsvc-c09cb90dc2.json"
        );

        // Construye las opciones de configuración de Firebase
        FirebaseOptions options = FirebaseOptions.builder()
                .setCredentials(GoogleCredentials.fromStream(serviceAccount))
                .setStorageBucket("unieventosmongodb.appspot.com")
                .build();

        // Evita inicializar Firebase más de una vez
        if (FirebaseApp.getApps().isEmpty()) {
            return FirebaseApp.initializeApp(options);
        }

        return null;
    }
}
