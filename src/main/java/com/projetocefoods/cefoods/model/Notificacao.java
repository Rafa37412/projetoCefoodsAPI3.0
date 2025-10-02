package com.projetocefoods.cefoods.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

// REMOVIDAS AS IMPORTAÇÕES NÃO UTILIZADAS DO HIBERNATE
// import org.hibernate.annotations.JdbcTypeCode;
// import org.hibernate.type.SqlTypes;

@Entity
@Table(name = "tb_notificacao")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Notificacao {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String tipo;

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

    // Campo 'dados' agora como TEXT para evitar dependência de jsonb em ambientes sem suporte.
    // Se quiser voltar a usar jsonb posteriormente: alterar para @Column(name = "dados", columnDefinition = "jsonb")
    @Column(name = "dados", columnDefinition = "TEXT")
    private String dados;

    @Builder.Default
    private Boolean lida = false;

    @Builder.Default
    private LocalDateTime dataCriacao = LocalDateTime.now();
}