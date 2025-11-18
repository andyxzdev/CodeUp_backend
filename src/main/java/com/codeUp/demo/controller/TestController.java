// Adicione isso no seu controller package
package com.codeUp.demo.controller;

import com.codeUp.demo.dto.LoginDTO;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/test")
@CrossOrigin(origins = "*") // temporariamente, para testes
public class TestController {

    @GetMapping("/conexao")
    public String testarConexao() {
        return "✅ Backend Java conectado com React Native!";
    }

    @PostMapping("/login-teste")
    public String testeLogin(@RequestBody LoginDTO login) {
        return "Login recebido: " + login.getEmail();
    }
}