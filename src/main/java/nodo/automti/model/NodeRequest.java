package nodo.automti.model;

import java.util.List;

public class NodeRequest {

    private String idProyecto;
    private String tipoNodo;
    private String data;
    private List<String> nodeConfiguration;

    public NodeRequest(String idProyecto, String tipoNodo, String data, List<String> nodeConfiguration) {
        this.idProyecto = idProyecto;
        this.tipoNodo = tipoNodo;
        this.data = data;
        this.nodeConfiguration = nodeConfiguration;
    }

    public NodeRequest() {
    }

    // Getters y setters

    public String getIdProyecto() {
        return idProyecto;
    }

    public void setIdProyecto(String idProyecto) {
        this.idProyecto = idProyecto;
    }

    public String getTipoNodo() {
        return tipoNodo;
    }

    public void setTipoNodo(String tipoNodo) {
        this.tipoNodo = tipoNodo;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public List<String> getNodeConfiguration() {
        return nodeConfiguration;
    }

    public void setNodeConfiguration(List<String> nodeConfiguration) {
        this.nodeConfiguration = nodeConfiguration;
    }
}
