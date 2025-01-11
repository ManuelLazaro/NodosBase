package nodo.automti.nodos.TransformerData;

import org.springframework.stereotype.Service;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import java.io.StringWriter;
import java.io.PrintWriter;

@Service
public class TransformerDataService {

    public String execute(String data) {
        // Lógica para procesar el nodo TransformerData
        System.out.println("Procesando TransformerData con la data: " + data);

        ScriptEngine engine = new ScriptEngineManager().getEngineByName("python");
        StringWriter outputWriter = new StringWriter();
        engine.getContext().setWriter(new PrintWriter(outputWriter));

        try {
            // Ejecutar el código Python contenido en data
            engine.eval(data);
            String output = outputWriter.toString().trim();
            System.out.println("Resultado de ejecución del código Python: " + output);
            return output.isEmpty() ? "Sin salida" : output;
        } catch (ScriptException e) {
            System.err.println("Error ejecutando el código Python: " + e.getMessage());
            return "Error en la ejecución del código Python: " + e.getMessage();
        }
    }
}