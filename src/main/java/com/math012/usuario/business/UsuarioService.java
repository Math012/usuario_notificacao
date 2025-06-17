package com.math012.usuario.business;

import com.math012.usuario.business.converter.UsuarioConverter;
import com.math012.usuario.business.dto.UsuarioDTO;
import com.math012.usuario.infra.entity.Usuario;
import com.math012.usuario.infra.execptions.ConflictException;
import com.math012.usuario.infra.execptions.ResourceNotFoundException;
import com.math012.usuario.infra.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final UsuarioConverter usuarioConverter;
    private final PasswordEncoder passwordEncoder;

    public UsuarioDTO salvarUsuario(UsuarioDTO usuarioDTO){
        emailExiste(usuarioDTO.getEmail());
        usuarioDTO.setSenha(passwordEncoder.encode(usuarioDTO.getSenha()));
        Usuario usuario = usuarioConverter.paraUsuario(usuarioDTO);
        usuario = usuarioRepository.save(usuario);
        return usuarioConverter.paraUsuarioDTO(usuario);
    }

    public Usuario buscarUsuarioPorEmail(String email){
        return usuarioRepository.findByEmail(email).orElseThrow(
                () -> new ResourceNotFoundException("Email não encontrado")
        );
    }

    public void deletarUsuarioPorEmail(String email){
        usuarioRepository.deleteByEmail(email);
    }


    public void emailExiste(String email){
        try {
            if (verificaEmailExistente(email)){
                throw new ConflictException("Email ja cadastrado: " + email);
            }
        }catch (ConflictException e){
            throw new ConflictException("Email ja cadastrado: " + e.getCause());
        }
    }

    public boolean verificaEmailExistente(String email){
        return usuarioRepository.existsByEmail(email);
    }


}
