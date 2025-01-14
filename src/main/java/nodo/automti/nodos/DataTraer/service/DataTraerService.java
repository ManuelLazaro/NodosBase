package nodo.automti.nodos.DataTraer.service;

import org.python.core.PyDictionary;
import org.python.core.PyObject;
import org.python.util.PythonInterpreter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.util.*;

@Service
public class DataTraerService {
    private static final Logger logger = LoggerFactory.getLogger(DataTraerService.class);

    @Autowired
    private JdbcTemplate jdbcTemplate;

    private final ObjectMapper objectMapper = new ObjectMapper();

    public List<Map<String, Object>> processNode(String node, String idProyecto, String pythonCode) {
        logger.info("Procesando nodo con Python code: {}", pythonCode != null ? "presente" : "null");

        if (!node.contains("DataTraer")) {
            throw new IllegalArgumentException("Nodo no válido: " + node);
        }

        // Usar datos simulados por ahora
        List<Map<String, Object>> clientes = getMockClientes();
        List<Map<String, Object>> productos = getMockProductos();

        try (PythonInterpreter python = new PythonInterpreter()) {
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            PrintStream printStream = new PrintStream(outputStream, true, StandardCharsets.UTF_8.name());
            python.setOut(printStream);

            // Configurar el intérprete Python
            python.exec("# -*- coding: utf-8 -*-");
            python.exec("import json");

            // Convertir los datos a formato JSON para Python
            String clientesJson = objectMapper.writeValueAsString(clientes);
            String productosJson = objectMapper.writeValueAsString(productos);

            // Pasar los datos como variables a Python
            python.set("clientes_json", clientesJson);
            python.set("productos_json", productosJson);
            python.set("id_proyecto", idProyecto);

            // Preparar el entorno Python
            python.exec("import json");
            python.exec("clientes = json.loads(clientes_json)");
            python.exec("productos = json.loads(productos_json)");

            // Ejecutar el código Python proporcionado
            python.exec(pythonCode);

            // Capturar la salida
            String result = outputStream.toString(StandardCharsets.UTF_8.name()).trim();
            logger.debug("Resultado Python: {}", result);

            if (result.isEmpty()) {
                logger.warn("No se generó salida del código Python");
                return new ArrayList<>();
            }

            // Convertir el resultado a List<Map<String, Object>>
            return objectMapper.readValue(result, List.class);

        } catch (Exception e) {
            logger.error("Error ejecutando código Python: ", e);
            throw new RuntimeException("Error ejecutando código Python: " + e.getMessage(), e);
        }
    }

    private List<Map<String, Object>> getMockClientes() {
        List<Map<String, Object>> clientes = new ArrayList<>();

        Map<String, Object> cliente1 = new HashMap<>();
        cliente1.put("id", 1);
        cliente1.put("nombre", "Juan Pérez");
        cliente1.put("email", "juan@ejemplo.com");
        clientes.add(cliente1);

        Map<String, Object> cliente2 = new HashMap<>();
        cliente2.put("id", 2);
        cliente2.put("nombre", "María García");
        cliente2.put("email", "maria@ejemplo.com");
        clientes.add(cliente2);

        return clientes;
    }

    private List<Map<String, Object>> getMockProductos() {
        List<Map<String, Object>> productos = new ArrayList<>();

        Map<String, Object> producto1 = new HashMap<>();
        producto1.put("id", 1);
        producto1.put("nombre", "Laptop");
        producto1.put("precio", 999.99);
        productos.add(producto1);

        Map<String, Object> producto2 = new HashMap<>();
        producto2.put("id", 2);
        producto2.put("nombre", "Smartphone");
        producto2.put("precio", 599.99);
        productos.add(producto2);

        Map<String, Object> producto3 = new HashMap<>();
        producto3.put("id", 3);
        producto3.put("nombre", "Tablet");
        producto3.put("precio", 399.99);
        productos.add(producto3);

        return productos;
    }

    private List<Map<String, Object>> getData(String entity) {
        // Por ahora retornamos datos mock en lugar de consultar la base de datos
        if ("producto".equalsIgnoreCase(entity)) {
            return getMockProductos();
        } else if ("cliente".equalsIgnoreCase(entity)) {
            return getMockClientes();
        } else {
            throw new IllegalArgumentException("Tipo de entidad no soportado: " + entity);
        }
    }
}