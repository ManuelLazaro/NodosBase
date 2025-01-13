package nodo.automti.nodos.DataEnvidio.service;

import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class DataEnvidioService {

    public void processNode(String node, String idProyecto, List<Map<String, Object>> ventas) {
        System.out.println("Procesando nodo: " + node + " para el proyecto: " + idProyecto);
        for (Map<String, Object> venta : ventas) {
            System.out.println("Venta procesada: " + venta);
        }
    }
}
