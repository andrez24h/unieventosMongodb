package dev.andresm.unieventosMongodb.servicios.implement;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.qrcode.QRCodeWriter;
import dev.andresm.unieventosMongodb.servicios.interfaces.QRServicio;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.util.Base64;

@Service
@Transactional
@RequiredArgsConstructor
public class QRServicioImp implements QRServicio {

    /**
     * Genera un código QR a partir de un texto recibido como parámetro.
     * El QR se genera como una imagen y posteriormente se convierte
     * a formato Base64 para que pueda ser enviado fácilmente al frontend
     * dentro de la respuesta de la API.

     * @param contenido Texto que será codificado dentro del QR
     * @return String que representa la imagen del QR en formato Base64
     * @throws Exception si ocurre un error durante la generación del QR
     */
    @Override
    public String generarQR(String contenido) throws Exception {

        // 1. Se crea el generador de códigos QR utilizando la librería ZXing
        QRCodeWriter qrCodeWriter = new QRCodeWriter();

        // 2. Se codifica el contenido dentro de una matriz de bits que representa el QR
        //    Se define el formato QR_CODE y el tamaño de la imagen (300x300)
        var bitMatrix = qrCodeWriter.encode(
                contenido,
                BarcodeFormat.QR_CODE,
                300,
                300
        );

        // 3.Se crea una imagen en memoria donde posteriormente se dibujará el QR
        BufferedImage image = new BufferedImage(
                300,
                300,
                BufferedImage.TYPE_INT_RGB
        );

        // 4️. Se recorren todos los puntos de la matriz para pintar la imagen
        //  Si el bit es verdadero se pinta negro, de lo contrario blanco
        for (int x = 0; x < 300; x++) {
            for (int y = 0; y < 300; y++) {
                image.setRGB(
                        x,
                        y,
                        bitMatrix.get(x, y) ? 0x000000 : 0xFFFFFF
                );
            }
        }

        // 5. Se crea un flujo de salida en memoria para almacenar la imagen generada
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

        // 6. La imagen se escribe en formato PNG dentro del flujo de salida
        ImageIO.write(image, "png", outputStream);

        // 7. Finalmente la imagen se convierte a Base64
        //    Esto permite enviarla fácilmente dentro de respuestas JSON o APIs REST
        return Base64.getEncoder().encodeToString(outputStream.toByteArray());
    }
}
