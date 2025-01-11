package nodo.automti.nodos.TransformerData.model;

import jakarta.persistence.*;

@Entity
@Table(name = "TransformerDataEntity")
public class TransformerDataEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "idProyecto", unique = true, nullable = false)
    private String idProyecto;

    @Column(name = "inputData", nullable = false)
    private String inputData;

    @Column(name = "outputData")
    private String outputData;

    public TransformerDataEntity(Long id, String idProyecto, String inputData, String outputData) {
        this.id = id;
        this.idProyecto = idProyecto;
        this.inputData = inputData;
        this.outputData = outputData;
    }

    public TransformerDataEntity() {
    }

    // Getters y Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getIdProyecto() {
        return idProyecto;
    }

    public void setIdProyecto(String idProyecto) {
        this.idProyecto = idProyecto;
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