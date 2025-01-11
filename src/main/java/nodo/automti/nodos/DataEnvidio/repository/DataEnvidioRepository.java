package nodo.automti.nodos.DataEnvidio.repository;

import nodo.automti.nodos.DataEnvidio.model.DataEnvidioEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface DataEnvidioRepository extends JpaRepository<DataEnvidioEntity, Long> {
    Optional<DataEnvidioEntity> findByIdProyecto(String idProyecto);
}