package nodo.automti.nodos.TransformerData.repository;

import nodo.automti.nodos.TransformerData.model.TransformerDataEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface TransformerDataRepository extends JpaRepository<TransformerDataEntity, Long> {
    Optional<TransformerDataEntity> findByIdProyecto(String idProyecto);
}