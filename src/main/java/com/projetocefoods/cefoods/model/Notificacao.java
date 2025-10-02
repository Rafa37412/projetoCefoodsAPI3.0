package com.projetocefoods.cefoods.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

@Entity
@Table(name = "tb_notificacao") // Ajustado para seguir o padrão
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Notificacao {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String tipo; // e.g. ORDER_RECEIVED, LOW_STOCK, COMMENT, ORDER_STATUS

    private String titulo;

    @Column(columnDefinition = "TEXT")
    private String mensagem;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_usuario_destino")
    private Usuario usuariodestino;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_loja_destino")
    private Loja lojadestino;

    private Long pedido_id;

    private Long produto_id;

    // Armazena payload dinâmico em JSON (PostgreSQL jsonb). Usar String aqui evita dependência de conversores custom.
    @JdbcTypeCode(SqlTypes.JSON)
    @Column(columnDefinition = "jsonb")

    // so fazer biuld msm.
    private String dados;

    @Builder.Default
    private Boolean lida = false;

    @Builder.Default
    private LocalDateTime dataCriacao = LocalDateTime.now();
}