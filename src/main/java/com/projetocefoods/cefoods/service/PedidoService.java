package com.projetocefoods.cefoods.service;

import com.projetocefoods.cefoods.dto.ItemPedidoDTO.ItemPedidoResponse;
import com.projetocefoods.cefoods.dto.PedidoDTO.PedidoResponse;
import com.projetocefoods.cefoods.model.*;
import com.projetocefoods.cefoods.repository.PedidoItemRepository;
import com.projetocefoods.cefoods.repository.PedidoRepository;
import com.projetocefoods.cefoods.repository.ProdutoRepository;
import com.projetocefoods.cefoods.repository.UsuarioRepository;
import com.projetocefoods.cefoods.repository.LojaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PedidoService {

    private final PedidoRepository pedidoRepo;
    private final PedidoItemRepository pedidoItemRepo;
    private final ProdutoRepository produtoRepo;
    private final NotificacaoService notificacaoService;
    private final UsuarioRepository usuarioRepository;
    private final LojaRepository lojaRepository;

    /**
     * Cria um pedido garantindo que Usuario e Loja sejam entidades gerenciadas (buscadas do banco)
     * evitando associar entidades transientes apenas com ID setado manualmente.
     */
    @Transactional
    public Pedido criarPedido(Long idUsuario, Long idLoja, String nomeCliente,
                  String formaPagamento, Double total, String horarioRetirada,
                  List<PedidoItem> itensPreCriados) {

    Usuario usuario = usuarioRepository.findById(idUsuario)
        .orElseThrow(() -> new jakarta.persistence.EntityNotFoundException("Usuário não encontrado"));

    Loja loja = lojaRepository.findById(idLoja)
        .orElseThrow(() -> new jakarta.persistence.EntityNotFoundException("Loja não encontrada"));

    Pedido pedido = Pedido.builder()
        .usuario(usuario)
        .loja(loja)
        .nome_cliente(nomeCliente)
        .forma_pagamento(formaPagamento)
        .total(total)
        .status("PENDING")
        .data_pedido(LocalDateTime.now())
        .horario_retirada(horarioRetirada)
        .build();

    pedido = pedidoRepo.save(pedido);

        // 🔔 notificações
        Usuario donoLoja = pedido.getLoja().getUsuario();
        Usuario comprador = pedido.getUsuario();

        notificacaoService.criarNotificacaoParaUsuario(
                "ORDER_RECEIVED",
                "Pedido recebido",
                "Você recebeu um pedido de " + comprador.getNome(),
                donoLoja,
                pedido.getLoja(),
                pedido.getId(),
                null,
                null);

        notificacaoService.criarNotificacaoParaUsuario(
                "ORDER_PENDING",
                "Pedido em análise",
                "Seu pedido " + pedido.getId() + " foi enviado com sucesso.",
                comprador,
                pedido.getLoja(),
                pedido.getId(),
                null,
                null);

        // Ajuste estoque e salva itens
        for (PedidoItem item : itensPreCriados) {
            Produto p = produtoRepo.findById(item.getProduto().getId())
                    .orElseThrow(() -> new IllegalArgumentException(
                            "Produto não encontrado: " + item.getProduto().getId()));

            if (p.getEstoque() < item.getQuantidade()) {
                throw new IllegalArgumentException("Estoque insuficiente para " + p.getNome());
            }
            p.setEstoque(p.getEstoque() - item.getQuantidade());
            produtoRepo.save(p);

            PedidoItem pi = PedidoItem.builder()
                    .pedido(pedido)
                    .produto(p)
                    .nome(p.getNome())
                    .preco(item.getPreco())
                    .quantidade(item.getQuantidade())
                    .build();

            pedidoItemRepo.save(pi);

            // notificação de estoque baixo
            if (p.getEstoque() <= p.getEstoque_minimo()) {
                notificacaoService.criarNotificacaoParaUsuario(
                        "LOW_STOCK",
                        "Estoque baixo",
                        "Alerta! " + p.getNome() + " está com estoque baixo.",
                        p.getLoja().getUsuario(),
                        p.getLoja(),
                        null,
                        p.getId(),
                        null);
            }
        }

        return pedido;
    }

    @Transactional
    public Optional<PedidoResponse> atualizarStatus(Long id, String status) {
        return pedidoRepo.findById(id).map(p -> {
            String s = status != null ? status.trim().toUpperCase() : null;
            p.setStatus(s);
            Pedido salvo = pedidoRepo.save(p);

            Usuario comprador = salvo.getUsuario();

            if ("ACEITO".equals(s) || "ACCEPTED".equals(s)) {
                notificacaoService.criarNotificacaoParaUsuario(
                        "ORDER_ACCEPTED",
                        "Pedido aceito",
                        "Seu pedido " + salvo.getId() + " foi aceito. Encontre-se com o vendedor para retirá-lo.",
                        comprador,
                        salvo.getLoja(),
                        salvo.getId(),
                        null,
                        null);
            }

            else if ("RECUSADO".equals(s) || "CANCELLED".equals(s) || "DECLINED".equals(s)) {
                notificacaoService.criarNotificacaoParaUsuario(
                        "ORDER_DECLINED",
                        "Pedido recusado",
                        "Seu pedido " + salvo.getId() + " foi recusado.",
                        comprador,
                        salvo.getLoja(),
                        salvo.getId(),
                        null,
                        null);

                List<PedidoItem> itens = pedidoItemRepo.findByPedido(salvo);
                if (itens != null) {
                    for (PedidoItem item : itens) {
                        Produto produto = produtoRepo.findById(item.getProduto().getId()).orElse(null);
                        if (produto != null) {
                            produto.setEstoque(produto.getEstoque() + item.getQuantidade());
                            produtoRepo.save(produto);
                        }
                    }
                }
            }

            else if ("CONCLUIDO".equals(s) || "COMPLETED".equals(s)) {
                notificacaoService.criarNotificacaoParaUsuario(
                        "ORDER_COMPLETED",
                        "Pedido finalizado",
                        "Seu pedido " + salvo.getId() + " foi concluído com sucesso.",
                        comprador,
                        salvo.getLoja(),
                        salvo.getId(),
                        null,
                        null);
            }

            return toDTO(salvo);
        });
    }

    private PedidoResponse toDTO(Pedido pedido) {
        List<ItemPedidoResponse> itensDto = pedidoItemRepo.findByPedido(pedido)
                .stream()
                .map(i -> new ItemPedidoResponse(
                        pedido.getId(),
                        i.getProduto().getId(),
                        i.getNome(),
                        i.getQuantidade(),
                        i.getPreco(),
                        i.getPreco() * i.getQuantidade()))
                .collect(Collectors.toList());

        return new PedidoResponse(
                pedido.getId(),
                pedido.getUsuario().getId(),
                pedido.getLoja().getId(),
                pedido.getNome_cliente(),
                pedido.getForma_pagamento(),
                pedido.getTotal(),
                pedido.getStatus(),
                pedido.getData_pedido(),
                pedido.getHorario_retirada(),
                itensDto);
    }

    public List<Pedido> listarTodos() {
        return pedidoRepo.findAll();
    }

    public Optional<Pedido> buscarPorId(Long id) {
        return pedidoRepo.findById(id);
    }

    public List<PedidoResponse> listarPorUsuario(Long idUsuario) {
        // Implementar busca por usuário
        return pedidoRepo.findAll().stream()
                .filter(p -> p.getUsuario().getId().equals(idUsuario))
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    public List<PedidoResponse> listarPorLoja(Long idLoja) {
        // Implementar busca por loja
        return pedidoRepo.findAll().stream()
                .filter(p -> p.getLoja().getId().equals(idLoja))
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    @Transactional
    public PedidoResponse criarPedidoFromController(Pedido pedido) {
        // Garante que usuário e loja sejam carregados do banco antes de criar
        Long idUsuario = pedido.getUsuario() != null ? pedido.getUsuario().getId() : null;
        Long idLoja = pedido.getLoja() != null ? pedido.getLoja().getId() : null;
        if (idUsuario == null) {
            throw new IllegalArgumentException("ID do usuário é obrigatório para criar pedido");
        }
        if (idLoja == null) {
            throw new IllegalArgumentException("ID da loja é obrigatório para criar pedido");
        }

        Pedido novo = criarPedido(
                idUsuario,
                idLoja,
                pedido.getNome_cliente(),
                pedido.getForma_pagamento(),
                pedido.getTotal(),
                pedido.getHorario_retirada(),
                pedido.getItens());
        return toDTO(novo);
    }
}