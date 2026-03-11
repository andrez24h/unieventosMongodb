package dev.andresm.unieventosMongodb.servicios.interfaces;

/**
 * Servicio encargado de generar códigos QR
 * para las entradas de los eventos.
 */
public interface QRServicio {

    /**
     * Genera un código QR a partir de un texto.

     * El resultado se devuelve en formato Base64 para que
     * pueda ser enviado al frontend como imagen.
     *
     * @param contenido información que tendrá el QR
     * @return imagen QR en Base64
     * @throws Exception si ocurre un error al generar el QR
     */
    String generarQR(String contenido) throws Exception;
}
