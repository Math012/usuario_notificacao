package com.math012.usuario.controller;

import com.math012.usuario.business.UsuarioService;
import com.math012.usuario.business.dto.EnderecoDTO;
import com.math012.usuario.business.dto.TelefoneDTO;
import com.math012.usuario.business.dto.UsuarioDTO;
import com.math012.usuario.infra.entity.Usuario;
import com.math012.usuario.infra.security.JwtUtil;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/usuario")
@AllArgsConstructor
public class UsuarioController {

    private final UsuarioService usuarioService;

    private final AuthenticationManager authenticationManager;

    private final JwtUtil jwtUtil;

    @PostMapping
    public ResponseEntity<UsuarioDTO> salvaUsuario(@RequestBody UsuarioDTO usuarioDTO){
        return ResponseEntity.ok(usuarioService.salvarUsuario(usuarioDTO));
    }

    @PostMapping("/login")
    public String login(@RequestBody UsuarioDTO usuarioDTO){
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(usuarioDTO.getEmail(),usuarioDTO.getSenha())
        );
        return "Bearer " + jwtUtil.generateToken(authentication.getName());
    }

    @GetMapping
    public ResponseEntity<UsuarioDTO> buscarUsuarioPorEmail(@RequestParam("email") String email){
        return ResponseEntity.ok(usuarioService.buscarUsuarioPorEmail(email));
    }

    @PutMapping
    public ResponseEntity<UsuarioDTO> atualizarDadosUsuario(
            @RequestBody UsuarioDTO usuarioDTO,
            @RequestHeader("Authorization") String token){
        return ResponseEntity.ok(usuarioService.atualizaDadosUsuario(usuarioDTO,token));
    }

    @DeleteMapping("/{email}")
    public ResponseEntity<Void> deletarUsuarioPorEmail(@PathVariable String email){
        usuarioService.deletarUsuarioPorEmail(email);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/endereco")
    public ResponseEntity<EnderecoDTO> atualizarEndereco(@RequestBody EnderecoDTO enderecoDTO, @RequestParam("id")Long id){
        return ResponseEntity.ok(usuarioService.atualizarEndereco(id, enderecoDTO));
    }

    @PutMapping("/telefone")
    public ResponseEntity<TelefoneDTO> atualizarTelefone(@RequestBody TelefoneDTO telefoneDTO, @RequestParam("id")Long id){
        return ResponseEntity.ok(usuarioService.atualizarTelefone(id, telefoneDTO));
    }

    @PostMapping("/endereco")
    public ResponseEntity<EnderecoDTO> cadastrarEndereco(@RequestBody EnderecoDTO enderecoDTO, @RequestHeader("Authorization") String token){
        return ResponseEntity.ok(usuarioService.cadastrarEndereco(token,enderecoDTO));
    }

    @PostMapping("/telefone")
    public ResponseEntity<TelefoneDTO> cadastrarEndereco(@RequestBody TelefoneDTO telefoneDTO, @RequestHeader("Authorization") String token){
        return ResponseEntity.ok(usuarioService.cadastrarTelefone(token,telefoneDTO));
    }

}
