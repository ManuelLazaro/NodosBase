package nodo.automti.nodos.TransformerData.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import nodo.automti.nodos.TransformerData.repository.TransformerDataRepository;
import org.python.core.*;
import org.python.util.PythonInterpreter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.util.*;

@Service
public class TransformerDataService {

    @Autowired
    private TransformerDataRepository repository;

    private static final ObjectMapper objectMapper = new ObjectMapper();

    public Map<String, Object> processNode(String functionName, String idProyecto, String pythonCode, Map<String, Object> params) {
        if (pythonCode == null || pythonCode.trim().isEmpty()) {
            return createErrorResponse("No se proporcionó código Python en el parámetro 'data'");
        }

        try (PythonInterpreter python = new PythonInterpreter()) {
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            PrintStream printStream = new PrintStream(outputStream, true, StandardCharsets.UTF_8.name());

            python.setOut(printStream);

            // Configuración inicial del intérprete de Python
            python.exec("# -*- coding: utf-8 -*-");
            python.exec("import sys");
            python.exec("import json");
            python.exec("from io import StringIO");

            // Pasar parámetros como kwargs
            PyDictionary pyParams = new PyDictionary();
            params.forEach((key, value) -> pyParams.put(Py.newString(key), convertToPyObject(value)));
            python.set("kwargs", pyParams);

            // Ejecutar el código Python
            python.exec(pythonCode);

            // Capturar la salida del código Python
            String result = outputStream.toString(StandardCharsets.UTF_8.name()).trim();

            // Imprimir la salida para depuración
            System.out.println("Salida del código Python: " + result);

            // Validar que la salida no esté vacía y sea válida
            if (result.isEmpty()) {
                return createErrorResponse("No se generó ningún resultado");
            }

            // Intentar decodificar la salida
            try {
                return objectMapper.readValue(result, Map.class);
            } catch (Exception e) {
                return createErrorResponse("Salida no válida de Python: " + result);
            }
        } catch (Exception e) {
            return createErrorResponse("Error en la transformación: " + e.getMessage());
        }
    }

    private PyObject convertToPyObject(Object value) {
        if (value == null) return Py.None;

        if (value instanceof Map) {
            PyDictionary dict = new PyDictionary();
            ((Map<?, ?>) value).forEach((k, v) -> dict.put(convertToPyObject(k), convertToPyObject(v)));
            return dict;
        } else if (value instanceof List) {
            PyList list = new PyList();
            ((List<?>) value).forEach(item -> list.append(convertToPyObject(item)));
            return list;
        } else if (value instanceof Number) {
            return value instanceof Integer || value instanceof Long ? Py.newInteger(((Number) value).intValue()) : Py.newFloat(((Number) value).doubleValue());
        } else if (value instanceof Boolean) {
            return (Boolean) value ? Py.True : Py.False;
        } else if (value instanceof String) {
            return Py.newString((String) value);
        }

        return Py.newString(value.toString());
    }

    private Map<String, Object> createErrorResponse(String message) {
        Map<String, Object> errorResponse = new HashMap<>();
        errorResponse.put("error", message);
        return errorResponse;
    }
}
