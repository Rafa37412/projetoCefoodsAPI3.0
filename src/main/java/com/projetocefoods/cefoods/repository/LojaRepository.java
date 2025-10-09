package com.projetocefoods.cefoods.repository;

import com.projetocefoods.cefoods.model.Loja;
import com.projetocefoods.cefoods.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LojaRepository extends JpaRepository<Loja, Long> {
    List<Loja> findByUsuario(Usuario usuario);

    @Query("SELECT DISTINCT l FROM Loja l LEFT JOIN FETCH l.horarios LEFT JOIN FETCH l.usuario")
    List<Loja> findAllCompleto();
}
