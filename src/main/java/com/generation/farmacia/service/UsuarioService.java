package com.generation.farmacia.service; 
// Pacote onde o serviço de Usuário está localizado
// Serviços em Spring são classes que contêm a lógica de negócios da aplicação

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.generation.farmacia.model.Usuario;
import com.generation.farmacia.model.UsuarioLogin;
import com.generation.farmacia.repository.UsuarioRepository;
import com.generation.farmacia.security.JwtService;

@Service
// Indica que esta classe é um componente do tipo Service do Spring
// Ela será automaticamente gerenciada e injetada em outras partes do sistema
public class UsuarioService {

    // Injeção de dependência do repositório de usuários (interface JPA)
    @Autowired
    private UsuarioRepository usuarioRepository;

    // Injeção de dependência do serviço responsável por gerar e validar tokens JWT
    @Autowired
    private JwtService jwtService;

    // Injeção do gerenciador de autenticação do Spring Security
    @Autowired
    private AuthenticationManager authenticationManager;

    // Injeção do codificador de senhas (BCrypt)
    @Autowired
    private PasswordEncoder passwordEncoder;

    // Retorna todos os usuários cadastrados no banco de dados
    public List<Usuario> getAll() {
        return usuarioRepository.findAll();
    }

    // Busca um usuário específico pelo ID
    public Optional<Usuario> getById(Long id) {
        return usuarioRepository.findById(id);
    }

    // Método para cadastrar um novo usuário
    public Optional<Usuario> cadastrarUsuario(Usuario usuario) {

        // Verifica se já existe um usuário com o mesmo nome de login
        if (usuarioRepository.findByUsuario(usuario.getUsuario()).isPresent()) {
            return Optional.empty(); // Retorna vazio se já existir
        }

        // Codifica (criptografa) a senha antes de salvar
        usuario.setSenha(passwordEncoder.encode(usuario.getSenha()));

        // Garante que o ID será gerado automaticamente
        usuario.setId(null);

        // Salva o novo usuário e retorna o objeto salvo
        return Optional.of(usuarioRepository.save(usuario));
    }

    // Método para atualizar um usuário existente
    public Optional<Usuario> atualizarUsuario(Usuario usuario) {

        // Verifica se o ID informado existe no banco
        if (!usuarioRepository.findById(usuario.getId()).isPresent()) {
            return Optional.empty(); // Se não existir, retorna vazio
        }

        // Verifica se o nome de usuário já está sendo usado por outro ID
        Optional<Usuario> usuarioExistente = usuarioRepository.findByUsuario(usuario.getUsuario());

        if (usuarioExistente.isPresent() && !usuarioExistente.get().getId().equals(usuario.getId())) {
            // Lança erro 400 (Bad Request) se o nome de usuário já estiver em uso
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Usuário já existe!", null);
        }

        // Atualiza a senha codificando novamente
        usuario.setSenha(passwordEncoder.encode(usuario.getSenha()));

        // Salva e retorna o usuário atualizado
        return Optional.of(usuarioRepository.save(usuario));
    }

    // Método responsável por autenticar um usuário no login
    public Optional<UsuarioLogin> autenticarUsuario(Optional<UsuarioLogin> usuarioLogin) {
        
        // Caso o objeto seja nulo, retorna vazio
        if (!usuarioLogin.isPresent()) {
            return Optional.empty();
        }

        // Extrai o objeto UsuarioLogin de dentro do Optional
        UsuarioLogin login = usuarioLogin.get();

        try {
            // Tenta autenticar o usuário com o AuthenticationManager
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(login.getUsuario(), login.getSenha()));

            // Caso a autenticação seja bem-sucedida, busca o usuário no banco
            return usuarioRepository.findByUsuario(login.getUsuario())
                    // e constrói a resposta de login com o token JWT
                    .map(usuario -> construirRespostaLogin(login, usuario));

        } catch (Exception e) {
            // Se ocorrer qualquer erro (usuário/senha inválidos), retorna vazio
            return Optional.empty();
        }
    }

    // Monta o objeto UsuarioLogin com as informações necessárias para o retorno do login
    private UsuarioLogin construirRespostaLogin(UsuarioLogin usuarioLogin, Usuario usuario) {
        usuarioLogin.setId(usuario.getId());
        usuarioLogin.setNome(usuario.getNome());
        usuarioLogin.setFoto(usuario.getFoto());
        usuarioLogin.setSenha(""); // Limpa a senha antes de retornar (por segurança)
        usuarioLogin.setToken(gerarToken(usuario.getUsuario())); // Gera o token JWT

        return usuarioLogin;
    }

    // Gera o token JWT e adiciona o prefixo "Bearer " antes de retorná-lo
    private String gerarToken(String usuario) {
        return "Bearer " + jwtService.generateToken(usuario);
    }

}
