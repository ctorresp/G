package cl.duoc.rednorte.pabellon.dto;

import java.math.BigDecimal;
import java.util.List;

public class ReportePerdidasDTO {

    private long totalCirugiasPeriodo;
    private long totalCirugiasCanceladas;
    private BigDecimal ingresosBrutosPerdidos;
    private BigDecimal ingresosNetosPerdidos;
    private List<TopMedicoCancelacion> topMedicos;

    public long getTotalCirugiasPeriodo() { return totalCirugiasPeriodo; }
    public void setTotalCirugiasPeriodo(long v) { this.totalCirugiasPeriodo = v; }
    public long getTotalCirugiasCanceladas() { return totalCirugiasCanceladas; }
    public void setTotalCirugiasCanceladas(long v) { this.totalCirugiasCanceladas = v; }
    public BigDecimal getIngresosBrutosPerdidos() { return ingresosBrutosPerdidos; }
    public void setIngresosBrutosPerdidos(BigDecimal v) { this.ingresosBrutosPerdidos = v; }
    public BigDecimal getIngresosNetosPerdidos() { return ingresosNetosPerdidos; }
    public void setIngresosNetosPerdidos(BigDecimal v) { this.ingresosNetosPerdidos = v; }
    public List<TopMedicoCancelacion> getTopMedicos() { return topMedicos; }
    public void setTopMedicos(List<TopMedicoCancelacion> v) { this.topMedicos = v; }

    public static class TopMedicoCancelacion {
        private String medicoRut;
        private String medicoNombre;
        private long totalCancelaciones;

        public String getMedicoRut() { return medicoRut; }
        public void setMedicoRut(String v) { this.medicoRut = v; }
        public String getMedicoNombre() { return medicoNombre; }
        public void setMedicoNombre(String v) { this.medicoNombre = v; }
        public long getTotalCancelaciones() { return totalCancelaciones; }
        public void setTotalCancelaciones(long v) { this.totalCancelaciones = v; }
    }
}
