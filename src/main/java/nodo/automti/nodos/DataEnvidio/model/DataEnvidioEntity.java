package nodo.automti.nodos.DataEnvidio.model;

import jakarta.persistence.*;

@Entity
@Table(name = "DataEnvidioEntity")
public class DataEnvidioEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "idProyecto", unique = true, nullable = false)
    private String idProyecto;

    @Column(name = "inputData", nullable = false)
    private String inputData;

    @Column(name = "deliveryStatus", nullable = false)
    private String deliveryStatus;

    public DataEnvidioEntity(String idProyecto, String inputData, String deliveryStatus, Long id) {
        this.idProyecto = idProyecto;
        this.inputData = inputData;
        this.deliveryStatus = deliveryStatus;
        this.id = id;
    }

    public DataEnvidioEntity() {
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

    public String getDeliveryStatus() {
        return deliveryStatus;
    }

    public void setDeliveryStatus(String deliveryStatus) {
        this.deliveryStatus = deliveryStatus;
    }
}