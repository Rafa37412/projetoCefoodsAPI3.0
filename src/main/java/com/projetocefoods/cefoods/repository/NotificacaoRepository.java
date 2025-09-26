package com.projetocefoods.cefoods.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.projetocefoods.cefoods.model.Notificacao;

import java.util.List;

public interface NotificacaoRepository extends JpaRepository<Notificacao, Long> {
    List<Notificacao> findByUsuariodestino_IdOrderByDataCriacaoDesc(Long id_usuario);
    List<Notificacao> findByLojadestino_IdOrderByDataCriacaoDesc(Long id_loja);
    List<Notificacao> findByUsuariodestino_IdAndLidaFalse(Long id_usuario);
}
