package dev.andresm.unieventosMongodb.servicios.implement;

import com.google.cloud.storage.Blob;
import com.google.cloud.storage.Bucket;
import com.google.firebase.cloud.StorageClient;
import dev.andresm.unieventosMongodb.servicios.interfaces.ImagenesServicio;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

/**
 * Implementación del servicio de gestión de imágenes utilizando Firebase Storage.

 * Esta clase permite interactuar con el bucket de Firebase para realizar
 * operaciones de almacenamiento de archivos, específicamente para:

 * - Subir imágenes al bucket configurado en Firebase.
 * - Eliminar imágenes existentes del almacenamiento.

 * Funcionamiento general:
 * 1. Obtiene el bucket configurado en Firebase.
 * 2. Genera un nombre único para cada archivo usando UUID.
 * 3. Guarda la imagen en Firebase Storage.
 * 4. Retorna la URL pública para acceder a la imagen.

 * Esto permite que la aplicación UniEventos almacene únicamente la URL
 * de la imagen en MongoDB en lugar de guardar el archivo completo.
 */
@Service
public class ImagenesServicioImp implements ImagenesServicio
{
    /**
     * Sube una imagen al bucket de Firebase Storage.

     * Proceso:
     * 1. Obtiene el bucket configurado en Firebase.
     * 2. Genera un nombre único para evitar colisiones de archivos.
     * 3. Crea el archivo dentro del bucket usando los datos del MultipartFile.
     * 4. Retorna la URL pública para acceder a la imagen.

     * @param multipartFile archivo de imagen enviado desde el cliente
     *
     * @return URL pública de la imagen almacenada en Firebase Storage

     * @throws Exception si ocurre algún error durante la subida
     */
    @Override
    public String subirImagen(MultipartFile multipartFile) throws Exception {

        // Obtiene el bucket configurado en Firebase
        Bucket bucket = StorageClient.getInstance().bucket();

        // Genera un nombre único para la imagen usando UUID
        String fileName = String.format( "%s-%s", UUID.randomUUID().toString(), multipartFile.getOriginalFilename());

        // Crea el archivo en Firebase Storage
        Blob blob = bucket.create(fileName, multipartFile.getInputStream(), multipartFile.getContentType());

        // Retorna la URL pública de acceso a la imagen
        return String.format(
                "https://firebasestorage.googleapis.com/v0/b/%s/o/%s?alt=media",
                bucket.getName(),
                blob.getName()
        );
    }

    /**
     * Elimina una imagen del bucket de Firebase Storage.

     * Proceso:
     * 1. Obtiene el bucket configurado.
     * 2. Busca el archivo usando su nombre.
     * 3. Elimina el archivo del almacenamiento.
     *
     * @param nombreImagen nombre del archivo almacenado en Firebase
     *
     * @throws Exception si ocurre algún error al eliminar la imagen
     */
    @Override
    public void eliminarImagen(String nombreImagen) throws Exception {

        // Obtiene el bucket de Firebase
        Bucket bucket = StorageClient.getInstance().bucket();

        // Obtiene el archivo almacenado
        Blob blob = bucket.get(nombreImagen);

        // Elimina el archivo del bucket
        blob.delete();
    }
}
