package nodo.automti.nodos.DataEnvidio.service;

import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class DataEnvidioService {

    public void processNode(String node, String idProyecto, List<Map<String, Object>> ventas) {
        System.out.println("Procesando nodo: " + node + " para el proyecto: " + idProyecto);

        // Validación inicial de las ventas
        if (ventas == null || ventas.isEmpty()) {
            System.out.println("No hay ventas para procesar en el nodo: " + node);
            return;
        }

        // Procesar cada venta de forma independiente
        for (Map<String, Object> venta : ventas) {
            Map<String, Object> cliente = (Map<String, Object>) venta.get("cliente");
            List<Map<String, Object>> productos = (List<Map<String, Object>>) venta.get("productos");

            // Etiquetar cliente
            if (cliente != null) {
                cliente.put("etiqueta", "cliente procesado");
            }

            // Etiquetar productos
            if (productos != null) {
                for (Map<String, Object> producto : productos) {
                    producto.put("etiqueta", "producto procesado");
                }
            }

            // Confirmar las etiquetas en la consola
            System.out.println("Venta procesada: " + venta);
        }

        // Confirmar finalización del procesamiento
        System.out.println("Procesamiento completado para el nodo: " + node);
    }
}
