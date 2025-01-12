package nodo.automti.nodos.DataEnvidio.service;

import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class DataEnvidioService {

    public void execute(String idProyecto, List<Map<String, Object>> datos) {
        System.out.println("Procesando datos para el proyecto: " + idProyecto);
        for (Map<String, Object> item : datos) {
            System.out.println("Elemento procesado: " + item);
        }
    }
}
