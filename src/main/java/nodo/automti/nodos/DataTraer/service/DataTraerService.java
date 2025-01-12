package nodo.automti.nodos.DataTraer.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class DataTraerService {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    public List<Map<String, Object>> getData(String entity) {
        String query;
        if ("producto".equalsIgnoreCase(entity)) {
            query = "SELECT * FROM productos";
        } else if ("cliente".equalsIgnoreCase(entity)) {
            query = "SELECT * FROM clientes";
        } else {
            throw new IllegalArgumentException("Tipo de entidad no soportado: " + entity);
        }
        return jdbcTemplate.queryForList(query);
    }
}
