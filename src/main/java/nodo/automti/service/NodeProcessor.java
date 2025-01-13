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
        List<Map<String, Object>> ventas = new ArrayList<>();

        try {
            for (String node : nodeConfiguration) {
                Map<String, Object> nodeResult = processNode(node, idProyecto, data, ventas);

                if (nodeResult.containsKey("error")) {
                    return nodeResult;
                }

                if (nodeResult.containsKey("ventas")) {
                    ventas = (List<Map<String, Object>>) nodeResult.get("ventas");
                }

                nodeResult.forEach((key, value) -> {
                    if (!"ventas".equals(key)) {
                        response.put(key, value);
                    }
                });
            }

            if (!response.containsKey("error") && !response.containsKey("ventasResumen")) {
                response.put("ventas", ventas);
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
            if (node.startsWith("DataTraer.")) {
                return processDataTraerNode(node, idProyecto);
            } else if (node.startsWith("TransformerData.")) {
                return processTransformerNode(node, idProyecto, data, ventas);
            } else if (node.startsWith("DataEnvidio.")) {
                return processDataEnvidioNode(node, idProyecto, ventas);
            }

            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("error", "Tipo de nodo no soportado: " + node);
            return errorResponse;

        } catch (Exception e) {
            logger.error("Error procesando nodo {}: ", node, e);
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("error", "Error procesando nodo " + node + ": " + e.getMessage());
            return errorResponse;
        }
    }

    private Map<String, Object> processDataTraerNode(String node, String idProyecto) {
        List<Map<String, Object>> result = dataTraerService.processNode(node, idProyecto);
        Map<String, Object> response = new HashMap<>();
        response.put("ventas", result);
        return response;
    }

    private Map<String, Object> processTransformerNode(String node, String idProyecto, String data, List<Map<String, Object>> ventas) {
        Map<String, Object> response = new HashMap<>();

        if (data == null || data.trim().isEmpty()) {
            response.put("error", "El código Python no puede estar vacío");
            return response;
        }

        Map<String, Object> params = new HashMap<>();
        params.put("id_proyecto", idProyecto);
        params.put("ventas", ventas);

        try {
            String functionName = node.substring(node.lastIndexOf('.') + 1);

            Map<String, Object> result = transformerDataService.processNode(functionName, idProyecto, data, params);

            if (result.containsKey("error")) {
                return result;
            }

            response.putAll(result);
            return response;
        } catch (Exception e) {
            logger.error("Error procesando nodo TransformerData: ", e);
            response.put("error", "Error procesando TransformerData: " + e.getMessage());
            return response;
        }
    }

    private Map<String, Object> processDataEnvidioNode(String node, String idProyecto, List<Map<String, Object>> ventas) {
        dataEnvidioService.processNode(node, idProyecto, ventas);
        Map<String, Object> response = new HashMap<>();
        response.put("ventas", ventas);
        return response;
    }
}
