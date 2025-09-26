package com.projetocefoods.cefoods.service;

import com.projetocefoods.cefoods.dto.ProdutoDTO.CreateProduto;
import com.projetocefoods.cefoods.model.Produto;
import com.projetocefoods.cefoods.model.Usuario;
import com.projetocefoods.cefoods.model.Loja;
import com.projetocefoods.cefoods.model.Categoria;
import com.projetocefoods.cefoods.repository.ProdutoRepository;
import com.projetocefoods.cefoods.repository.LojaRepository;
import com.projetocefoods.cefoods.repository.CategoriaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ProdutoService {

    private final ProdutoRepository produtoRepo;
    private final LojaRepository lojaRepo;
    private final CategoriaRepository categoriaRepo;
    private final NotificacaoService notificacaoService;

    public Produto criar(CreateProduto dto) {
        Loja loja = lojaRepo.findById(dto.id_loja())
                .orElseThrow(() -> new IllegalArgumentException("Loja não encontrada"));

        Categoria categoria = categoriaRepo.findById(dto.id_categoria())
                .orElseThrow(() -> new IllegalArgumentException("Categoria não encontrada"));

        Produto produto = Produto.builder()
                .loja(loja)
                .categoria(categoria)
                .nome(dto.nome())
                .descricao(dto.descricao())
                .preco(dto.preco())
                .imagem(dto.imagem())
                .estoque(dto.estoque() != null ? dto.estoque() : 0)
                .estoque_minimo(dto.estoque_minimo() != null ? dto.estoque_minimo() : 0)
                .disponivel(dto.disponivel() != null ? dto.disponivel() : true)
                .data_cadastro(LocalDateTime.now())
                .vezes_vendido(0)
                .avaliacao_media(0.0)
                .build();

        return produtoRepo.save(produto);
    }

    public List<Produto> listar() {
        return produtoRepo.findAll();
    }

    public Optional<Produto> buscarPorId(Long id) {
        return produtoRepo.findById(id);
    }

    public Produto atualizar(Long id, CreateProduto dto) {
        Produto produto = produtoRepo.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Produto não encontrado"));

        Loja loja = lojaRepo.findById(dto.id_loja())
                .orElseThrow(() -> new IllegalArgumentException("Loja não encontrada"));

        Categoria categoria = categoriaRepo.findById(dto.id_categoria())
                .orElseThrow(() -> new IllegalArgumentException("Categoria não encontrada"));

        produto.setId(id);
        produto.setLoja(loja);
        produto.setCategoria(categoria);
        produto.setNome(dto.nome());
        produto.setDescricao(dto.descricao());
        produto.setPreco(dto.preco());
        produto.setImagem(dto.imagem());
        produto.setEstoque(dto.estoque() != null ? dto.estoque() : 0);
        produto.setEstoque_minimo(dto.estoque_minimo() != null ? dto.estoque_minimo() : 0);
        produto.setDisponivel(dto.disponivel() != null ? dto.disponivel() : true);



        if (produto.getEstoque() <= produto.getEstoque_minimo()) {
            Usuario donoLoja = produto.getLoja().getUsuario();
            notificacaoService.criarNotificacaoParaUsuario(
                    "LOW_STOCK",
                    "Estoque baixo",
                    "Alerta! " + produto.getNome() + " está com estoque baixo.",
                    donoLoja,
                    produto.getLoja(),
                    null,
                    produto.getId(),
                    null);
        }

        return produtoRepo.save(produto);

    }

    public void deletar(Long id) {
        if (!produtoRepo.existsById(id)) {
            throw new IllegalArgumentException("Produto não encontrado para exclusão");
        }
        produtoRepo.deleteById(id);
    }

    public List<Produto> listarPorLoja(Long id_loja) {
        Loja loja = lojaRepo.findById(id_loja)
                .orElseThrow(() -> new IllegalArgumentException("Loja não encontrada"));

        return produtoRepo.findByLoja(loja);
    }

    public List<Produto> listarPorCategoria(Integer id_categoria) {
        return produtoRepo.findByCategoria_Id(id_categoria);
    }

}
