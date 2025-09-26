package com.projetocefoods.cefoods.service;

import com.projetocefoods.cefoods.dto.ItemPedidoDTO.*;
import com.projetocefoods.cefoods.model.ItemPedido;
import com.projetocefoods.cefoods.model.ItemPedidoId;
import com.projetocefoods.cefoods.model.Pedido;
import com.projetocefoods.cefoods.model.Produto;
import com.projetocefoods.cefoods.repository.ItemPedidoRepository;
import com.projetocefoods.cefoods.repository.PedidoRepository;
import com.projetocefoods.cefoods.repository.ProdutoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ItemPedidoService {

    private final ItemPedidoRepository itemPedidoRepo;
    private final PedidoRepository pedidoRepo;
    private final ProdutoRepository produtoRepo;

    public ItemPedido criar(ItemPedidoCreate dto) {
        Pedido pedido = pedidoRepo.findById(dto.id_pedido())
                .orElseThrow(() -> new IllegalArgumentException("Pedido não encontrado"));

        Produto produto = produtoRepo.findById(dto.id_produto())
                .orElseThrow(() -> new IllegalArgumentException("Produto não encontrado"));

        ItemPedido item = ItemPedido.builder()
                .pedido(pedido)
                .produto(produto)
                .quantidade(dto.quantidade())
                .preco_unitario(dto.preco_unitario())
                .build();

        return itemPedidoRepo.save(item);
    }

    public List<ItemPedido> listarTodos() {
        return itemPedidoRepo.findAll();
    }

    public List<ItemPedido> listarPorPedido(Long idPedido) {
        return itemPedidoRepo.findByPedidoId(idPedido);
    }

    public ItemPedido buscarPorId(Long idPedido, Long idProduto) {
        ItemPedidoId id = new ItemPedidoId(idPedido, idProduto);
        return itemPedidoRepo.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Item do pedido não encontrado"));
    }

    public ItemPedido atualizar(Long idPedido, Long idProduto, ItemPedidoUpdate dto) {
        ItemPedidoId id = new ItemPedidoId(idPedido, idProduto);
        ItemPedido item = itemPedidoRepo.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Item do pedido não encontrado"));

        if (dto.quantidade() != null) {
            item.setQuantidade(dto.quantidade());
        }

        if (dto.preco_unitario() != null) {
            item.setPreco_unitario(dto.preco_unitario());
        }

        // Não precisa calcular subtotal aqui pois será calculado na entidade
        // automaticamente

        return itemPedidoRepo.save(item);
    }

    public void deletar(Long idPedido, Long idProduto) {
        ItemPedidoId id = new ItemPedidoId(idPedido, idProduto);
        if (!itemPedidoRepo.existsById(id)) {
            throw new IllegalArgumentException("Item do pedido não encontrado");
        }
        itemPedidoRepo.deleteById(id);
    }
}
