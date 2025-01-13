package nodo.automti.service;

import nodo.automti.nodos.DataEnvidio.service.DataEnvidioService;
import nodo.automti.nodos.TransformerData.service.TransformerDataService;
import nodo.automti.nodos.DataTraer.service.DataTraerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;

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
        List<Map<String, Object>> ventas = new ArrayList<>();

        // Mantener un conjunto de clientes únicos
        Set<Integer> processedClientes = new HashSet<>();

        // Procesamiento de nodos y obtención de datos
        for (String node : nodeConfiguration) {
            if (node.startsWith("DataTraer.")) {
                // Obtener datos de clientes y productos
                List<Map<String, Object>> clientes = dataTraerService.getData("cliente");
                List<Map<String, Object>> productos = dataTraerService.getData("producto");

                if (productos.isEmpty() || clientes.isEmpty()) {
                    System.out.println("No hay datos suficientes para procesar ventas.");
                    continue;
                }

                int productosIndex = 0;

                // Asignar productos a clientes únicos
                for (Map<String, Object> cliente : clientes) {
                    int clienteId = (int) cliente.get("id");
                    if (processedClientes.contains(clienteId)) {
                        continue; // Saltar clientes ya procesados
                    }

                    processedClientes.add(clienteId); // Registrar cliente como procesado

                    List<Map<String, Object>> productosPorCliente = new ArrayList<>();
                    for (int i = 0; i < 3 && productosIndex < productos.size(); i++) {
                        productosPorCliente.add(productos.get(productosIndex));
                        productosIndex = (productosIndex + 1) % productos.size();
                    }

                    Map<String, Object> venta = new HashMap<>();
                    venta.put("cliente", cliente);
                    venta.put("productos", productosPorCliente);
                    ventas.add(venta);
                }
            } else if (node.startsWith("TransformerData.")) {
                String entidad = node.split("\\.")[1].toLowerCase();
                processedData = transformerDataService.execute(idProyecto, data, ventas);
            } else if (node.startsWith("DataEnvidio.")) {
                // Enviar datos procesados
                dataEnvidioService.execute(idProyecto, ventas);
            }
        }

        response.put("data", processedData);
        response.put("ventas", ventas);

        return response;
    }
}
