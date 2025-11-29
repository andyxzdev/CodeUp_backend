package com.codeUp.demo.service;

import com.codeUp.demo.model.Usuario;
import com.codeUp.demo.repository.UsuarioRepository;
import jakarta.inject.Inject;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

// o termo "mock" é usado pra simular comportamentos de objetos reais
// mockito serve pra criar testes unitários

@ExtendWith(MockitoExtension.class) // serve pra rodar os testes com Mockito
class UsuarioServiceTest {

    @InjectMocks // serve pra rodar os testes, sem precisar do projeto
    // completo na pasta teste
    private UsuarioService usuarioService;

    @Mock // deve ser usado pra todas as dependencias da classe UsuarioService
    private UsuarioRepository usuarioRepository;

    // todas as dependencias originais da classe UsuarioService
    // devem estar aqui obrigatoriamente

    @Test
    public void DeveCriarUsuario() {
        usuarioService.criar(new Usuario());
    }

    @Test
    void RetornarTodosUsuarios() {
        List<Usuario> listaUsuarios = usuarioService.findAll();
        System.out.println(listaUsuarios);
    }
}