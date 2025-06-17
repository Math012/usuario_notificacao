package com.math012.usuario.business;

import com.math012.usuario.business.converter.UsuarioComparator;
import com.math012.usuario.business.converter.UsuarioConverter;
import com.math012.usuario.business.dto.EnderecoDTO;
import com.math012.usuario.business.dto.TelefoneDTO;
import com.math012.usuario.business.dto.UsuarioDTO;
import com.math012.usuario.infra.entity.Endereco;
import com.math012.usuario.infra.entity.Telefone;
import com.math012.usuario.infra.entity.Usuario;
import com.math012.usuario.infra.execptions.ConflictException;
import com.math012.usuario.infra.execptions.ResourceNotFoundException;
import com.math012.usuario.infra.repository.EnderecoRepository;
import com.math012.usuario.infra.repository.TelefoneRepository;
import com.math012.usuario.infra.repository.UsuarioRepository;
import com.math012.usuario.infra.security.JwtUtil;
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
    private final JwtUtil jwtUtil;
    private final UsuarioComparator usuarioComparator;
    private final EnderecoRepository enderecoRepository;
    private final TelefoneRepository telefoneRepository;

    public UsuarioDTO salvarUsuario(UsuarioDTO usuarioDTO){
        emailExiste(usuarioDTO.getEmail());
        usuarioDTO.setSenha(passwordEncoder.encode(usuarioDTO.getSenha()));
        Usuario usuario = usuarioConverter.paraUsuario(usuarioDTO);
        usuario = usuarioRepository.save(usuario);
        return usuarioConverter.paraUsuarioDTO(usuario);
    }

    public EnderecoDTO cadastrarEndereco(String token, EnderecoDTO enderecoDTO){
        String email = jwtUtil.extrairEmailToken(token.substring(7));
        Usuario usuario = usuarioRepository.findByEmail(email).orElseThrow(
                () -> new ResourceNotFoundException("Email não encontrado")
        );

        Endereco endereco = usuarioConverter.paraEnderecoEntity(enderecoDTO, usuario.getId());
        return usuarioConverter.paraEnderecoDTO(enderecoRepository.save(endereco));
    }

    public TelefoneDTO cadastrarTelefone(String token, TelefoneDTO telefoneDTO){
        String email = jwtUtil.extrairEmailToken(token.substring(7));
        Usuario usuario = usuarioRepository.findByEmail(email).orElseThrow(
                () -> new ResourceNotFoundException("Email não encontrado")
        );

        Telefone telefone = usuarioConverter.paraTelefoneEntity(telefoneDTO, usuario.getId());
        return usuarioConverter.paraTelefoneDTO(telefoneRepository.save(telefone));
    }

    public UsuarioDTO buscarUsuarioPorEmail(String email){
        Usuario usuario = usuarioRepository.findByEmail(email).orElseThrow(
                () -> new ResourceNotFoundException("Email não encontrado")
        );
        return usuarioConverter.paraUsuarioDTO(usuario);
    }

    public void deletarUsuarioPorEmail(String email){
        usuarioRepository.deleteByEmail(email);
    }

    public UsuarioDTO atualizaDadosUsuario(UsuarioDTO usuarioDTO, String token){
        String email = jwtUtil.extrairEmailToken(token.substring(7));
        Usuario usuarioEntity = usuarioRepository.findByEmail(email).orElseThrow(() ->
                new ResourceNotFoundException("Email não encontrado!")
        );
        return usuarioConverter.paraUsuarioDTO(usuarioRepository.save(usuarioComparator.updateUsuario(usuarioDTO,usuarioEntity)));
    }

    public EnderecoDTO atualizarEndereco(Long idEndereco, EnderecoDTO enderecoDTO){
        Endereco enderecoEntity = enderecoRepository.findById(idEndereco).orElseThrow(()->
                new ResourceNotFoundException("Endereço não encontrado"));
        return usuarioConverter.paraEnderecoDTO(enderecoRepository.save(usuarioComparator.updateEndereco(enderecoDTO,enderecoEntity)));
    }

    public TelefoneDTO atualizarTelefone(Long idTelefone, TelefoneDTO telefoneDTO){
        Telefone telefoneEntity = telefoneRepository.findById(idTelefone).orElseThrow(()->
                new ResourceNotFoundException("Telefone não encontrado"));

        return usuarioConverter.paraTelefoneDTO(telefoneRepository.save(usuarioComparator.updateTelefone(telefoneDTO,telefoneEntity)));
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
