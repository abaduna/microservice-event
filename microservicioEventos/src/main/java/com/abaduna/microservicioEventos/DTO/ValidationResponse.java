package com.abaduna.microservicioEventos.DTO;

import com.abaduna.microservicioEventos.models.Rol;
import lombok.Data;

@Data
public class ValidationResponse {
    private Integer userId;
    private String email;
    private Long iat;
    private Long exp;
    private String error;
    public boolean isError() {
        return error != null;
    }
    private Rol rol;
}
