package dev.andresm.unieventosMongodb.servicios.interfaces;

import org.springframework.web.multipart.MultipartFile;

/**
 * Interfaz que define los servicios relacionados con la gestión de imágenes
 * en un servicio de almacenamiento en la nube (Firebase Storage).

 * Esta interfaz establece las operaciones básicas que se pueden realizar
 * sobre las imágenes utilizadas en la aplicación UniEventos.

 * Funcionalidades principales:
 * - Subir imágenes al almacenamiento en la nube.
 * - Eliminar imágenes previamente almacenadas.

 * NOTA:
 * En la base de datos únicamente se almacena la URL de la imagen,
 * no los bytes del archivo. Esto mejora el rendimiento de la base
 * de datos y evita sobrecargarla con archivos binarios.
 */
public interface ImagenesServicio {

    /**
     * Sube una imagen al servicio de almacenamiento en la nube.

     * @param imagen archivo de imagen recibido desde el cliente
     *               (por ejemplo desde un formulario en el frontend).

     * @return URL pública de la imagen almacenada en Firebase Storage.

     * @throws Exception si ocurre un error durante la subida de la imagen.
     */
    String subirImagen(MultipartFile imagen) throws Exception;


    /**
     * Elimina una imagen del almacenamiento en la nube.

     * @param nombreImagen nombre o identificador del archivo
     *                     almacenado en Firebase Storage.

     * @throws Exception si ocurre un error al intentar eliminar la imagen.
     */
    void eliminarImagen(String nombreImagen) throws Exception;
}
