package nodo.automti.nodos.DataEnvidio.model;

import jakarta.persistence.*;

@Entity
public class DataEnvidioEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String inputData;
    private String deliveryStatus;

    public DataEnvidioEntity(Long id, String inputData, String deliveryStatus) {
        this.id = id;
        this.inputData = inputData;
        this.deliveryStatus = deliveryStatus;
    }

    public DataEnvidioEntity() {
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

    public String getDeliveryStatus() {
        return deliveryStatus;
    }

    public void setDeliveryStatus(String deliveryStatus) {
        this.deliveryStatus = deliveryStatus;
    }
}
