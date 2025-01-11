package nodo.automti.nodos.DataEnvidio.service;

import nodo.automti.nodos.DataEnvidio.model.DataEnvidioEntity;
import nodo.automti.nodos.DataEnvidio.repository.DataEnvidioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalTime;

@Service
public class DataEnvidioService {

    @Autowired
    private DataEnvidioRepository repository;

    public String execute(String data) {
        // Lógica para procesar el nodo DataEnvidio
        System.out.println("Procesando DataEnvidio con la data: " + data);

        LocalTime currentTime = LocalTime.now();
        String result;
        if (currentTime.isBefore(LocalTime.of(13, 0))) {
            result = "El producto se entregará hoy";
        } else {
            result = "El producto se entregará mañana";
        }

        // Almacenar el resultado en la base de datos
        DataEnvidioEntity entity = new DataEnvidioEntity();
        entity.setInputData(data);
        entity.setDeliveryStatus(result);
        repository.save(entity);

        return result;
    }
}