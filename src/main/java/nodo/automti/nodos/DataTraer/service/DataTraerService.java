package nodo.automti.nodos.DataTraer.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Service
public class DataTraerService {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    public List<Map<String, Object>> processNode(String node, String idProyecto) {
        System.out.println("Procesando nodo: " + node + " para el proyecto: " + idProyecto);
        List<Map<String, Object>> clientes = getData("cliente");
        List<Map<String, Object>> productos = getData("producto");

        List<Map<String, Object>> ventas = new ArrayList<>();
        Set<Integer> processedClientes = new HashSet<>();
        int productosIndex = 0;

        for (Map<String, Object> cliente : clientes) {
            int clienteId = (int) cliente.get("id");
            if (processedClientes.contains(clienteId)) {
                continue;
            }

            processedClientes.add(clienteId);
            List<Map<String, Object>> productosPorCliente = new ArrayList<>();

            for (int i = 0; i < 3 && productosIndex < productos.size(); i++) {
                productosPorCliente.add(productos.get(productosIndex));
                productosIndex = (productosIndex + 1) % productos.size();
            }

            Map<String, Object> venta = Map.of(
                    "cliente", cliente,
                    "productos", productosPorCliente
            );
            ventas.add(venta);
        }

        return ventas;
    }

    private List<Map<String, Object>> getData(String entity) {
        String query;
        if ("producto".equalsIgnoreCase(entity)) {
            query = "SELECT * FROM productos";
        } else if ("cliente".equalsIgnoreCase(entity)) {
            query = "SELECT * FROM clientes";
        } else {
            throw new IllegalArgumentException("Tipo de entidad no soportado: " + entity);
        }
        return jdbcTemplate.queryForList(query);
    }
}