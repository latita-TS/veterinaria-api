package com.veterinaria.api.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Ruta pública "/" para comprobar que la API está en línea (útil en la nube).
 */
@RestController
public class HomeController {

    @GetMapping("/")
    public Map<String, Object> inicio() {
        Map<String, Object> info = new LinkedHashMap<>();
        info.put("aplicacion", "VetCitas API - Gestión de citas veterinarias");
        info.put("estado", "En línea");
        info.put("login", "POST /api/auth/login");
        info.put("citas", "GET | POST /api/citas  -  GET | PUT | DELETE /api/citas/{id}");
        info.put("formatos", "JSON (por defecto) o XML con la cabecera Accept: application/xml");
        info.put("monitoreo", "GET /actuator/health");
        return info;
    }
}
