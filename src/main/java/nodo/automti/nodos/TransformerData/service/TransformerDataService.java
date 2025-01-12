
package nodo.automti.nodos.TransformerData.service;

import nodo.automti.nodos.TransformerData.model.TransformerDataEntity;
import nodo.automti.nodos.TransformerData.repository.TransformerDataRepository;
import org.python.core.PyDictionary;
import org.python.core.PyList;
import org.python.core.PyObject;
import org.python.core.PyString;
import org.python.util.PythonInterpreter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.StringWriter;
import java.math.BigDecimal;
import java.sql.Date;
import java.sql.Timestamp;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class TransformerDataService {

    @Autowired
    private TransformerDataRepository repository;

    public String execute(String idProyecto, String data, List<Map<String, Object>> fetchedData) {
        try (PythonInterpreter python = new PythonInterpreter()) {
            StringWriter output = new StringWriter();
            python.setOut(output);

            // Convertir todos los tipos de datos a formatos compatibles con Python
            List<Map<String, PyObject>> convertedData = fetchedData.stream()
                    .map(item -> item.entrySet().stream()
                            .collect(Collectors.toMap(
                                    Map.Entry::getKey,
                                    entry -> convertValue(entry.getValue())
                            )))
                    .collect(Collectors.toList());

            // Crear una lista Python con los datos convertidos
            PyList pyList = new PyList();
            for (Map<String, PyObject> item : convertedData) {
                PyDictionary pyDict = new PyDictionary();
                item.forEach((key, value) -> pyDict.__setitem__(new PyString(key), value));
                pyList.append(pyDict);
            }

            // Registrar los datos convertidos en el intérprete de Python
            python.set("fetched_data", pyList);

            // Configurar variables adicionales útiles
            python.exec("import json");
            python.exec("from datetime import datetime");

            // Agregar un wrapper de seguridad alrededor del código del usuario
            String wrappedCode = String.format(
                    "try:\n" +
                            "    %s\n" +
                            "except Exception as e:\n" +
                            "    print('Error en la ejecución del código Python:', str(e))",
                    data.replace("\n", "\n    ")  // Indentar el código del usuario
            );

            // Ejecutar el código Python
            python.exec(wrappedCode);

            String result = output.toString().trim();
            if (result.isEmpty()) {
                result = "La ejecución fue exitosa pero no produjo ninguna salida.";
            }

            // Guardar en la base de datos
            TransformerDataEntity entity = repository.findByIdProyecto(idProyecto)
                    .orElse(new TransformerDataEntity());
            entity.setIdProyecto(idProyecto);
            entity.setInputData(data);
            entity.setOutputData(result);
            repository.save(entity);

            return result;

        } catch (Exception e) {
            String errorMsg = "Error en la ejecución: " + e.getMessage();
            // Guardar el error en la base de datos
            TransformerDataEntity entity = repository.findByIdProyecto(idProyecto)
                    .orElse(new TransformerDataEntity());
            entity.setIdProyecto(idProyecto);
            entity.setInputData(data);
            entity.setOutputData(errorMsg);
            repository.save(entity);
            return errorMsg;
        }
    }

    private PyObject convertValue(Object value) {
        if (value instanceof BigDecimal) {
            return new org.python.core.PyFloat(((BigDecimal) value).doubleValue());
        } else if (value instanceof Timestamp) {
            return new PyString(value.toString());
        } else if (value instanceof Date) {
            return new PyString(value.toString());
        } else if (value instanceof byte[]) {
            return new PyString(new String((byte[]) value));
        } else if (value == null) {
            return new PyString("");
        }
        // Para otros tipos, convertir a String
        return new PyString(value.toString());
    }
}
