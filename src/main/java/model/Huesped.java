package model;

import java.time.LocalDate;

public class Huesped {

    private int idHuesped;
    private String nombre;
    private String documento;
    private String email;
    private String telefono;
    private LocalDate fechaRegistro;

    public Huesped() {
    }

    public Huesped(int idHuesped, String nombre, String documento, String email, String telefono) {
        this.idHuesped = idHuesped;
        this.nombre = nombre;
        this.documento = documento;
        this.email = email;
        this.telefono = telefono;
        this.fechaRegistro = LocalDate.now();
    }

    public int getIdHuesped() { return idHuesped; }
    public void setIdHuesped(int idHuesped) { this.idHuesped = idHuesped; }
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    public String getDocumento() { return documento; }
    public void setDocumento(String documento) { this.documento = documento; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getTelefono() { return telefono; }
    public void setTelefono(String telefono) { this.telefono = telefono; }
    public LocalDate getFechaRegistro() { return fechaRegistro; }
    public void setFechaRegistro(LocalDate fechaRegistro) { this.fechaRegistro = fechaRegistro; }

    @Override
    public String toString() {
        return idHuesped + " - " + nombre + " (" + documento + ")";
    }
}
