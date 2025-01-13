package nodo.automti.service;

import nodo.automti.nodos.DataEnvidio.service.DataEnvidioService;
import nodo.automti.nodos.TransformerData.service.TransformerDataService;
import nodo.automti.nodos.DataTraer.service.DataTraerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

@Component
public class NodeProcessor {
    private static final Logger logger = LoggerFactory.getLogger(NodeProcessor.class);

    @Autowired
    private TransformerDataService transformerDataService;

    @Autowired
    private DataEnvidioService dataEnvidioService;

    @Autowired
    private DataTraerService dataTraerService;

    private final ObjectMapper objectMapper = new ObjectMapper();

    public Map<String, Object> processNodes(String idProyecto, String tipoNodo, String data, List<String> nodeConfiguration) {
        logger.info("Iniciando procesamiento para el proyecto: {}", idProyecto);

        Map<String, Object> response = initializeResponse(idProyecto, tipoNodo);

        try {
            List<Map<String, Object>> ventas = new ArrayList<>();
            for (String node : nodeConfiguration) {
                Map<String, Object> nodeResult = processNode(node, idProyecto, data, ventas);

                if (nodeResult.containsKey("error")) {
                    return nodeResult; // Finaliza si hay error en un nodo
                }

                if (nodeResult.containsKey("ventas")) {
                    ventas = (List<Map<String, Object>>) nodeResult.get("ventas");
                }

                nodeResult.forEach(response::putIfAbsent);
            }

            return response;

        } catch (Exception e) {
            logger.error("Error en el procesamiento de nodos: ", e);
            response.put("error", "Error en el procesamiento: " + e.getMessage());
            return response;
        }
    }

    private Map<String, Object> initializeResponse(String idProyecto, String tipoNodo) {
        Map<String, Object> response = new HashMap<>();
        response.put("idProyecto", idProyecto);
        response.put("tipoNodo", tipoNodo);
        return response;
    }

    private Map<String, Object> processNode(String node, String idProyecto, String data, List<Map<String, Object>> ventas) {
        try {
            switch (getNodeCategory(node)) {
                case "DataTraer":
                    return processDataTraerNode(node, idProyecto);
                case "TransformerData":
                    return processTransformerNode(node, idProyecto, data, ventas);
                case "DataEnvidio":
                    return processDataEnvidioNode(node, idProyecto, ventas);
                default:
                    return generateErrorResponse("Tipo de nodo no soportado: " + node);
            }
        } catch (Exception e) {
            logger.error("Error procesando nodo {}: ", node, e);
            return generateErrorResponse("Error procesando nodo " + node + ": " + e.getMessage());
        }
    }

    private String getNodeCategory(String node) {
        return node.contains(".") ? node.split("\\.")[0] : node;
    }

    private Map<String, Object> processDataTraerNode(String node, String idProyecto) {
        List<Map<String, Object>> result = dataTraerService.processNode(node, idProyecto);
        Map<String, Object> response = new HashMap<>();
        response.put("ventas", result);
        return response;
    }

    private Map<String, Object> processTransformerNode(String node, String idProyecto, String data, List<Map<String, Object>> ventas) {
        if (data == null || data.trim().isEmpty()) {
            return generateErrorResponse("El código Python no puede estar vacío");
        }

        Map<String, Object> params = new HashMap<>();
        params.put("id_proyecto", idProyecto);
        params.put("ventas", ventas);

        try {
            String functionName = node.substring(node.lastIndexOf('.') + 1);
            return transformerDataService.processNode(functionName, idProyecto, data, params);
        } catch (Exception e) {
            logger.error("Error procesando TransformerData: ", e);
            return generateErrorResponse("Error procesando TransformerData: " + e.getMessage());
        }
    }

    private Map<String, Object> processDataEnvidioNode(String node, String idProyecto, List<Map<String, Object>> ventas) {
        dataEnvidioService.processNode(node, idProyecto, ventas);
        Map<String, Object> response = new HashMap<>();
        response.put("ventas", ventas);
        return response;
    }

    private Map<String, Object> generateErrorResponse(String message) {
        Map<String, Object> errorResponse = new HashMap<>();
        errorResponse.put("error", message);
        return errorResponse;
    }
}
