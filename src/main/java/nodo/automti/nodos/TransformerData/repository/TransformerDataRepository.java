package nodo.automti.nodos.TransformerData.repository;

import nodo.automti.nodos.TransformerData.model.TransformerDataEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TransformerDataRepository extends JpaRepository<TransformerDataEntity, Long> {
}
