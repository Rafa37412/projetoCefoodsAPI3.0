package com.projetocefoods.cefoods.model;

import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "tb_horario_funcionamento")
public class HorarioFuncionamento {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_horario")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_loja")
    private Loja loja;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private DiaSemana dia_semana;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Turno turno;
}
