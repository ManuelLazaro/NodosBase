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

    public String execute(String idProyecto, String data) {
        System.out.println("Procesando DataEnvidio con idProyecto: " + idProyecto + " y data: " + data);

        LocalTime currentTime = LocalTime.now();
        String result = currentTime.isBefore(LocalTime.of(13, 0))
                ? "El producto se entregará hoy"
                : "El producto se entregará mañana";

        DataEnvidioEntity entity = repository.findByIdProyecto(idProyecto)
                .orElse(new DataEnvidioEntity());
        entity.setIdProyecto(idProyecto);
        entity.setInputData(data);
        entity.setDeliveryStatus(result);
        repository.save(entity);

        return result;
    }
}