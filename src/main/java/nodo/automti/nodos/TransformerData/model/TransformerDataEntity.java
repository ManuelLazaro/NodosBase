package nodo.automti.nodos.TransformerData.model;

import jakarta.persistence.*;

@Entity
public class TransformerDataEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String inputData;
    private String outputData;

    public TransformerDataEntity(Long id, String outputData, String inputData) {
        this.id = id;
        this.outputData = outputData;
        this.inputData = inputData;
    }

    public TransformerDataEntity() {
    }
    // Getters y setters

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getInputData() {
        return inputData;
    }

    public void setInputData(String inputData) {
        this.inputData = inputData;
    }

    public String getOutputData() {
        return outputData;
    }

    public void setOutputData(String outputData) {
        this.outputData = outputData;
    }
}