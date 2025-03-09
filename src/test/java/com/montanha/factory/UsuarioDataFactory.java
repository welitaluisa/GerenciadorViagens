package com.montanha.factory;

import com.montanha.pojo.Usuario;

public class UsuarioDataFactory {
    public static Usuario criarUsuarioAdministrador() {
        Usuario usuarioAdmin = new Usuario();
        usuarioAdmin.setEmail("admin@email.com");
        usuarioAdmin.setSenha("654321");
        return usuarioAdmin;
    }

    public static Usuario criarUsuario() {
        Usuario usuarioAdmin = new Usuario();
        usuarioAdmin.setEmail("usuario@email.com");
        usuarioAdmin.setSenha("123456");
        return usuarioAdmin;
    }

}
