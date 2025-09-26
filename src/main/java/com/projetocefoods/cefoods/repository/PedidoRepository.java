/*package com.projetocefoods.cefoods.repository;

import com.projetocefoods.cefoods.model.Pedido;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PedidoRepository extends JpaRepository<Pedido, Long> {}*/

package com.projetocefoods.cefoods.repository;

import com.projetocefoods.cefoods.model.Pedido;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

public interface PedidoRepository extends JpaRepository<Pedido, Long> {
    List<Pedido> findByUsuarioId(Long id_usuario);

    List<Pedido> findByLojaId(Long id_loja);

}
