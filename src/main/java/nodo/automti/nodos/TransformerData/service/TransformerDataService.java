package nodo.automti.nodos.TransformerData.service;

import nodo.automti.nodos.TransformerData.model.TransformerDataEntity;
import nodo.automti.nodos.TransformerData.repository.TransformerDataRepository;
import org.python.core.*;
import org.python.util.PythonInterpreter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.StringWriter;
import java.math.BigDecimal;
import java.sql.Date;
import java.sql.Timestamp;
import java.util.List;
import java.util.Map;

@Service
public class TransformerDataService {

    @Autowired
    private TransformerDataRepository repository;

    public String execute(String idProyecto, String data, List<Map<String, Object>> entityData) {
        try (PythonInterpreter python = new PythonInterpreter()) {
            StringWriter output = new StringWriter();
            python.setOut(output);

            // Preparar datos para kwargs
            PyDictionary pyKwargs = new PyDictionary();
            pyKwargs.__setitem__(new PyString("clientes"), convertToPyList(entityData));

            // Registrar kwargs en el intérprete
            python.set("kwargs", pyKwargs);

            // Ejecutar el código Python
            String wrappedCode = String.format(
                    "try:\n" +
                            "    %s\n" +
                            "except Exception as e:\n" +
                            "    print('Error en la ejecución del código Python:', str(e))",
                    data.replace("\n", "\n    ")  // Indentar el código del usuario
            );

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

    private PyList convertToPyList(List<Map<String, Object>> data) {
        PyList pyList = new PyList();
        for (Map<String, Object> item : data) {
            PyDictionary pyDict = new PyDictionary();
            item.forEach((key, value) -> pyDict.__setitem__(new PyString(key), convertValue(value)));
            pyList.append(pyDict);
        }
        return pyList;
    }

    private PyObject convertValue(Object value) {
        if (value instanceof BigDecimal) {
            return new PyFloat(((BigDecimal) value).doubleValue());
        } else if (value instanceof Timestamp) {
            return new PyString(value.toString());
        } else if (value instanceof Date) {
            return new PyString(value.toString());
        } else if (value instanceof byte[]) {
            return new PyString(new String((byte[]) value));
        } else if (value == null) {
            return Py.None;
        }
        return new PyString(value.toString());
    }
}
