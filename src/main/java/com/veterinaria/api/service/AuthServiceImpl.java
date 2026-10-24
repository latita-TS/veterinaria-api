package com.veterinaria.api.service;

import com.veterinaria.api.dto.LoginRequest;
import com.veterinaria.api.dto.LoginResponse;
import com.veterinaria.api.dto.MensajeResponse;
import com.veterinaria.api.dto.RegistroRequest;
import com.veterinaria.api.entity.Usuario;
import com.veterinaria.api.exception.ReglaNegocioException;
import com.veterinaria.api.repository.UsuarioRepository;
import com.veterinaria.api.security.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final AuthenticationManager authenticationManager;
    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    /**
     * 1. Spring Security busca el usuario en la BD.
     * 2. Compara la contraseña escrita con el hash BCrypt guardado.
     * 3. Si coinciden, se genera el token JWT.
     */
    @Override
    public LoginResponse login(LoginRequest request) {
        String username = request.getUsername().trim();
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(username, request.getPassword()));
        } catch (AuthenticationException e) {
            throw new BadCredentialsException("Usuario o contraseña incorrectos");
        }

        Usuario usuario = usuarioRepository.findByUsername(username)
                .orElseThrow(() -> new BadCredentialsException("Usuario o contraseña incorrectos"));

        String token = jwtUtil.generarToken(usuario.getUsername(), usuario.getRol());

        return LoginResponse.builder()
                .mensaje("Inicio de sesión exitoso")
                .token(token)
                .tipo("Bearer")
                .expiraEnMs(jwtUtil.getExpiracionMs())
                .username(usuario.getUsername())
                .nombreCompleto(usuario.getNombreCompleto())
                .rol(usuario.getRol())
                .build();
    }

    /** Registra un usuario guardando su contraseña cifrada con BCrypt. */
    @Override
    @Transactional
    public MensajeResponse registrar(RegistroRequest request) {
        String username = request.getUsername().trim();
        if (usuarioRepository.existsByUsername(username)) {
            throw new ReglaNegocioException("El usuario '" + username + "' ya existe");
        }

        Usuario usuario = Usuario.builder()
                .username(username)
                .password(passwordEncoder.encode(request.getPassword()))
                .nombreCompleto(request.getNombreCompleto().trim())
                .rol("RECEPCION")
                .build();

        usuarioRepository.save(usuario);
        return new MensajeResponse("Usuario '" + username + "' registrado correctamente");
    }
}
