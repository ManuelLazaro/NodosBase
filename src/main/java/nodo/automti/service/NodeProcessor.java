package nodo.automti.service;

import nodo.automti.nodos.DataEnvidio.service.DataEnvidioService;
import nodo.automti.nodos.TransformerData.service.TransformerDataService;
import nodo.automti.nodos.DataTraer.service.DataTraerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class NodeProcessor {

    @Autowired
    private TransformerDataService transformerDataService;

    @Autowired
    private DataEnvidioService dataEnvidioService;

    @Autowired
    private DataTraerService dataTraerService;

    public Map<String, Object> processNodes(String idProyecto, String tipoNodo, String data, List<String> nodeConfiguration) {
        System.out.println("Iniciando procesamiento para el proyecto: " + idProyecto);
        Map<String, Object> response = new HashMap<>();
        response.put("idProyecto", idProyecto);
        response.put("tipoNodo", tipoNodo);

        String processedData = data;
        Map<String, List<Map<String, Object>>> fetchedData = new HashMap<>();

        // Procesamiento de nodos y obtención de datos
        for (String node : nodeConfiguration) {
            if (node.startsWith("DataTraer.")) {
                String entidad = node.split("\\.")[1].toLowerCase();
                List<Map<String, Object>> entidadData = dataTraerService.getData(entidad); // Renombrar la variable

                // Clasificación y etiquetado interno
                for (Map<String, Object> item : entidadData) {
                    item.put("etiqueta", entidad.equals("cliente") ? "cliente nuevo" : "producto nuevo");
                }

                fetchedData.put(entidad, entidadData);
            } else if ("TransformerData".equalsIgnoreCase(node)) {
                processedData = transformerDataService.execute(idProyecto, data, (List<Map<String, Object>>) fetchedData);
            } else if (node.startsWith("DataEnvidio.")) {
                String entidad = node.split("\\.")[1].toLowerCase();
                if (fetchedData.containsKey(entidad)) {
                    dataEnvidioService.execute(idProyecto, fetchedData.get(entidad));
                } else {
                    System.err.println("Datos para la entidad '" + entidad + "' no encontrados en fetchedData.");
                }
            }
        }

        response.put("data", processedData);
        response.put("fetchedData", fetchedData);

        return response;
    }
}