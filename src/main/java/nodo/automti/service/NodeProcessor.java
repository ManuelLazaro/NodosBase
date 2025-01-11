package nodo.automti.service;

import nodo.automti.nodos.DataEnvidio.service.DataEnvidioService;
import nodo.automti.nodos.TransformerData.service.TransformerDataService;
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

    public Map<String, Object> processNodes(String idProyecto, String tipoNodo, String data, List<String> nodeConfiguration) {
        System.out.println("Iniciando procesamiento para el proyecto: " + idProyecto);
        Map<String, Object> response = new HashMap<>();
        response.put("idProyecto", idProyecto);
        response.put("tipoNodo", tipoNodo);

        String processedData = data;
        String tempEntrega = null;

        if ("TRIGGER".equalsIgnoreCase(tipoNodo)) {
            for (String node : nodeConfiguration) {
                if ("TransformerData".equalsIgnoreCase(node)) {
                    processedData = transformerDataService.execute(processedData);
                } else if ("DataEnvidio".equalsIgnoreCase(node)) {
                    tempEntrega = dataEnvidioService.execute(processedData);
                }
            }
        } else if ("ACCION".equalsIgnoreCase(tipoNodo)) {
            if (!nodeConfiguration.isEmpty()) {
                String node = nodeConfiguration.get(0);
                if ("TransformerData".equalsIgnoreCase(node)) {
                    processedData = transformerDataService.execute(processedData);
                } else if ("DataEnvidio".equalsIgnoreCase(node)) {
                    tempEntrega = dataEnvidioService.execute(processedData);
                }
            }
        } else {
            response.put("error", "Tipo de nodo no reconocido: " + tipoNodo);
        }

        response.put("data", processedData);
        response.put("nodeConfiguration", nodeConfiguration);
        if (tempEntrega != null) {
            response.put("tempEntrega", tempEntrega);
        }

        return response;
    }
}
