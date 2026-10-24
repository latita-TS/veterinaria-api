package com.veterinaria.api.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Pruebas de los servicios web completos (login + CRUD), igual que en Postman
 * pero automáticas.
 */
@SpringBootTest
@AutoConfigureMockMvc
class ApiIntegracionTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper mapper;

    private String obtenerToken() throws Exception {
        String respuesta = mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"username\":\"admin\",\"password\":\"admin123\"}"))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();
        return mapper.readTree(respuesta).get("token").asText();
    }

    private String jsonCita(String mascota, LocalDateTime fecha, String estado) {
        String fechaTexto = fecha.format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm"));
        return """
                {
                  "nombreMascota": "%s",
                  "especie": "Perro",
                  "nombreDueno": "Juan Pérez",
                  "telefonoDueno": "987654321",
                  "fechaHora": "%s",
                  "motivo": "Vacunación",
                  "veterinario": "Dr. Carlos Ruiz"%s
                }
                """.formatted(mascota, fechaTexto, estado == null ? "" : ",\n  \"estado\": \"" + estado + "\"");
    }

    @Test
    @DisplayName("Login correcto devuelve un token JWT")
    void loginCorrecto() throws Exception {
        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"username\":\"admin\",\"password\":\"admin123\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").isNotEmpty())
                .andExpect(jsonPath("$.tipo").value("Bearer"));
    }

    @Test
    @DisplayName("Login con contraseña incorrecta responde 401")
    void loginIncorrecto() throws Exception {
        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"username\":\"admin\",\"password\":\"incorrecta\"}"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.mensaje").value("Usuario o contraseña incorrectos"));
    }

    @Test
    @DisplayName("Login con campos vacíos responde 400 con los errores de validación")
    void loginCamposVacios() throws Exception {
        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"username\":\"\",\"password\":\"\"}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.detalles.username").exists())
                .andExpect(jsonPath("$.detalles.password").exists());
    }

    @Test
    @DisplayName("Acceder a /api/citas sin token responde 401")
    void citasSinToken() throws Exception {
        mockMvc.perform(get("/api/citas"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("Flujo CRUD completo: POST, GET, PUT y DELETE")
    void flujoCrudCompleto() throws Exception {
        String token = "Bearer " + obtenerToken();
        LocalDateTime fecha = LocalDateTime.now().plusDays(5).withHour(10).withMinute(30);

        // POST
        String creada = mockMvc.perform(post("/api/citas")
                        .header("Authorization", token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonCita("Toby", fecha, null)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.estado").value("PENDIENTE"))
                .andReturn().getResponse().getContentAsString();
        JsonNode nodo = mapper.readTree(creada);
        long id = nodo.get("id").asLong();

        // GET por id
        mockMvc.perform(get("/api/citas/" + id).header("Authorization", token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nombreMascota").value("Toby"));

        // GET lista
        mockMvc.perform(get("/api/citas").header("Authorization", token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());

        // PUT
        mockMvc.perform(put("/api/citas/" + id)
                        .header("Authorization", token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonCita("Toby", fecha, "ATENDIDA")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.estado").value("ATENDIDA"));

        // DELETE
        mockMvc.perform(delete("/api/citas/" + id).header("Authorization", token))
                .andExpect(status().isOk());

        // Ya no existe -> 404
        mockMvc.perform(get("/api/citas/" + id).header("Authorization", token))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Con Accept: application/xml la API responde en XML")
    void respuestaEnXml() throws Exception {
        String token = "Bearer " + obtenerToken();
        mockMvc.perform(get("/api/citas")
                        .header("Authorization", token)
                        .accept(MediaType.APPLICATION_XML))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_XML));
    }

    @Test
    @DisplayName("El endpoint de monitoreo /actuator/health es público y responde UP")
    void healthCheck() throws Exception {
        mockMvc.perform(get("/actuator/health"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("UP"));
    }

    @Test
    @DisplayName("Registrar una cita con fecha pasada responde 400")
    void citaFechaPasada() throws Exception {
        String token = "Bearer " + obtenerToken();
        mockMvc.perform(post("/api/citas")
                        .header("Authorization", token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonCita("Luna", LocalDateTime.now().minusDays(1), null)))
                .andExpect(status().isBadRequest());
    }
}
