package nodo.automti.nodos.DataEnvidio;

import org.springframework.stereotype.Service;

import java.time.LocalTime;

@Service
public class DataEnvidioService {

    public String execute(String data) {
        // Lógica para procesar el nodo DataEnvidio
        System.out.println("Procesando DataEnvidio con la data: " + data);

        LocalTime currentTime = LocalTime.now();
        if (currentTime.isBefore(LocalTime.of(13, 0))) {
            return "El producto se entregará hoy";
        } else {
            return "El producto se entregará mañana";
        }
    }
}
