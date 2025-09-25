package com.projetocefoods.cefoods.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.projetocefoods.cefoods.model.Notificacao;

import java.util.List;

public interface NotificacaoRepository extends JpaRepository<Notificacao, Long> {
    List<Notificacao> findByUsuario_destinoId_usuarioOrderByData_criacaoDesc(Long id_usuario);
    List<Notificacao> findByLoja_destinoId_lojaOrderByData_criacaoDesc(Long id_loja);
    List<Notificacao> findByUsuario_destinoId_usuarioAndLidaFalse(Long id_usuario);
}
