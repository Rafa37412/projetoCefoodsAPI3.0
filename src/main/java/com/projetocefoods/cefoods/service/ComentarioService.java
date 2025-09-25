package com.projetocefoods.cefoods.service;

import com.projetocefoods.cefoods.dto.ComentarioDTO.CreateComentario;
import com.projetocefoods.cefoods.model.Comentario;
import com.projetocefoods.cefoods.model.Produto;
import com.projetocefoods.cefoods.model.Usuario;
import com.projetocefoods.cefoods.repository.ComentarioRepository;
import com.projetocefoods.cefoods.repository.ProdutoRepository;
import com.projetocefoods.cefoods.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
/*
@Service
@RequiredArgsConstructor
public class ComentarioService {

    private final ComentarioRepository comentarioRepo;
    private final ProdutoRepository produtoRepo;
    private final UsuarioRepository usuarioRepo;

    public Comentario criar(CreateComentario dto) {
        Produto produto = produtoRepo.findById(dto.idProduto())
                .orElseThrow(() -> new IllegalArgumentException("Produto não encontrado"));
        Usuario usuario = usuarioRepo.findById(dto.idUsuario())
                .orElseThrow(() -> new IllegalArgumentException("Usuário não encontrado"));

        Comentario comentario = Comentario.builder()
                .texto(dto.texto())
                .produto(produto)
                .usuario(usuario)
                .build();

        return comentarioRepo.save(comentario);
    }

    public List<Comentario> listarPorProduto(Long idProduto) {
        Produto produto = produtoRepo.findById(idProduto)
                .orElseThrow(() -> new IllegalArgumentException("Produto não encontrado"));
        return comentarioRepo.findByProduto(produto);
    }

    public List<Comentario> listarTodos() {
        return comentarioRepo.findAll();
    }
}
*/

@Service
@RequiredArgsConstructor
public class ComentarioService {

    private final ComentarioRepository comentarioRepo;
    private final ProdutoRepository produtoRepo;
    private final UsuarioRepository usuarioRepo;
    private final NotificacaoService notificacaoService;

    public Comentario criar(CreateComentario dto) {
        Produto produto = produtoRepo.findById(dto.id_produto())
                .orElseThrow(() -> new IllegalArgumentException("Produto não encontrado"));
        Usuario usuario = usuarioRepo.findById(dto.id_usuario())
                .orElseThrow(() -> new IllegalArgumentException("Usuário não encontrado"));

        Comentario comentario = Comentario.builder()
                .texto(dto.texto())
                .produto(produto)
                .usuario(usuario)
                .build();

        Comentario salvo = comentarioRepo.save(comentario);

        // 🔔 Notificar o dono da loja
        notificacaoService.criarNotificacaoParaUsuario(
                "COMMENT",
                "Novo comentário",
                "O produto " + produto.getNome() + " recebeu um comentário de " + usuario.getNome(),
                produto.getLoja().getUsuario(),
                produto.getLoja(),
                null,
                produto.getId_produto(),
                null
        );

        return salvo;
    }

    public List<Comentario> listarPorProduto(Long idProduto) {
        Produto produto = produtoRepo.findById(idProduto)
                .orElseThrow(() -> new IllegalArgumentException("Produto não encontrado"));
        return comentarioRepo.findByProduto(produto);
    }

    public List<Comentario> listarTodos() {
        return comentarioRepo.findAll();
    }
}
