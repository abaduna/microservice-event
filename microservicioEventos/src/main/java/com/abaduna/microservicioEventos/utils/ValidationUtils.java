package com.abaduna.microservicioEventos.utils;

import com.abaduna.microservicioEventos.DTO.ValidationResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

public class ValidationUtils {

    private static final String VALIDACION_USUARIO_URL = "http://localhost:3000/api/users/validarUsuario";

    // RestTemplate debe ser inyectado o configurado como un bean
    private static final RestTemplate restTemplate = new RestTemplate();

    /**
     * Valida un token llamando al servicio de validación de usuarios.
     *
     * @param token El token a validar.
     * @return Un objeto ValidationResponse con los datos del token o un mensaje de error.
     */
    public static ValidationResponse validarToken(String token) {
        try {
            String url = VALIDACION_USUARIO_URL + "?token=" + token;
            ResponseEntity<ValidationResponse> response = restTemplate.getForEntity(
                    url,
                    ValidationResponse.class
            );

            // Verifica si la respuesta es un error
            if (response.getBody() != null && response.getBody().isError()) {
                ValidationResponse validationResponse = new ValidationResponse();
                validationResponse.setError("Token inválido");
                return validationResponse; // Token inválido
            }

            return response.getBody();

        } catch (HttpClientErrorException e) {  // Captura errores 4xx/5xx
            if (e.getStatusCode() == HttpStatus.UNAUTHORIZED) {
                ValidationResponse validationResponse = new ValidationResponse();
                validationResponse.setError("Token no autorizado: " + e.getMessage());
                return validationResponse;
            }
            throw new RuntimeException("Error de validación: " + e.getMessage());
        } catch (Exception e) {
            throw new RuntimeException("Error al validar usuario: " + e.getMessage());
        }
    }
}