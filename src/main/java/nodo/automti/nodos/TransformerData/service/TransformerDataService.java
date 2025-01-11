package nodo.automti.nodos.TransformerData.service;

import nodo.automti.nodos.TransformerData.model.TransformerDataEntity;
import nodo.automti.nodos.TransformerData.repository.TransformerDataRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import java.io.StringWriter;
import java.io.PrintWriter;


@Service
public class TransformerDataService {

    @Autowired
    private TransformerDataRepository repository;

    public String execute(String idProyecto, String data) {
        System.out.println("Procesando TransformerData con idProyecto: " + idProyecto + " y data: " + data);

        ScriptEngine engine = new ScriptEngineManager().getEngineByName("python");
        StringWriter outputWriter = new StringWriter();
        engine.getContext().setWriter(new PrintWriter(outputWriter));

        String output;
        try {
            engine.eval(data);
            output = outputWriter.toString().trim();
            System.out.println("Resultado de ejecución del código Python: " + output);
        } catch (ScriptException e) {
            System.err.println("Error ejecutando el código Python: " + e.getMessage());
            output = "Error en la ejecución del código Python: " + e.getMessage();
        }

        TransformerDataEntity entity = repository.findByIdProyecto(idProyecto)
                .orElse(new TransformerDataEntity());
        entity.setIdProyecto(idProyecto);
        entity.setInputData(data);
        entity.setOutputData(output);
        repository.save(entity);

        return output.isEmpty() ? "Sin salida" : output;
    }
}