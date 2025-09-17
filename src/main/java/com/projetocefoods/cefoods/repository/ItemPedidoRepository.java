package com.projetocefoods.cefoods.repository;

import com.projetocefoods.cefoods.model.ItemPedido;
import com.projetocefoods.cefoods.model.ItemPedidoId;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ItemPedidoRepository extends JpaRepository<ItemPedido, ItemPedidoId> {
    List<ItemPedido> findByPedidoIdPedido(Long idPedido);
}
