/*package com.projetocefoods.cefoods.repository;

import com.projetocefoods.cefoods.model.Pedido;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PedidoRepository extends JpaRepository<Pedido, Long> {}*/

package com.projetocefoods.cefoods.repository;

import com.projetocefoods.cefoods.model.Pedido;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

public interface PedidoRepository extends JpaRepository<Pedido, Long> {
    List<Pedido> findByUsuarioIdUsuario(Long idUsuario);

    List<Pedido> findByLojaIdLoja(Long idLoja);

}
