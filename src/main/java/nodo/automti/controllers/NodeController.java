package nodo.automti.controllers;

import nodo.automti.service.NodeProcessor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/nodes")
public class NodeController {

    @Autowired
    private NodeProcessor nodeProcessor;

    @PostMapping("/process")
    public Map<String, Object> processNodes(@RequestBody Map<String, Object> requestBody) {
        String idProyecto = (String) requestBody.get("idProyecto");
        String tipoNodo = (String) requestBody.get("tipoNodo");
        String dataFrom = (String) requestBody.get("dataFrom"); // Nuevo parámetro
        String data = (String) requestBody.get("data");
        List<String> nodeConfiguration = (List<String>) requestBody.get("nodeConfiguration");

        return nodeProcessor.processNodes(idProyecto, tipoNodo, dataFrom, data, nodeConfiguration);
    }
}
