package com.projetocefoods.cefoods.service;

import com.projetocefoods.cefoods.dto.HorarioFuncionamentoDTO;
import com.projetocefoods.cefoods.dto.LojaDTO.CreateLoja;
import com.projetocefoods.cefoods.dto.LojaDTO.UpdateLoja;
import com.projetocefoods.cefoods.dto.LojaDTO.UpdateLojaStatusReq;
import com.projetocefoods.cefoods.dto.LojaResponse;
import com.projetocefoods.cefoods.dto.UsuarioResponse;
import com.projetocefoods.cefoods.model.HorarioFuncionamento;
import com.projetocefoods.cefoods.model.Loja;
import com.projetocefoods.cefoods.model.Usuario;
import com.projetocefoods.cefoods.repository.HorarioFuncionamentoRepository;
import com.projetocefoods.cefoods.repository.LojaRepository;
import com.projetocefoods.cefoods.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class LojaService {
    private final LojaRepository lojaRepo;
    private final UsuarioRepository usuarioRepo;
    private final HorarioFuncionamentoRepository horarioRepo;

    public LojaResponse criar(CreateLoja dto) {
        if (dto.id_usuario() == null) {
            throw new IllegalArgumentException("id_usuario não pode ser null");
        }

        Usuario u = usuarioRepo.findById(dto.id_usuario())
                .orElseThrow(() -> new IllegalArgumentException("Usuário não encontrado"));

        Loja novaLoja = Loja.builder()
                .usuario(u)
                .nome_fantasia(dto.nome_fantasia())
                .descricao(dto.descricao())
                .foto_capa(dto.foto_capa())
                .localizacao(dto.localizacao())
                .status(dto.status() != null ? dto.status() : false)
                .manual_override(false) // iniciar sem override
                .visivel(dto.visivel() != null ? dto.visivel() : true)
                .aceita_pix(Boolean.TRUE.equals(dto.aceita_pix()))
                .aceita_dinheiro(Boolean.TRUE.equals(dto.aceita_dinheiro()))
                .aceita_cartao(Boolean.TRUE.equals(dto.aceita_cartao()))
                .data_criacao(LocalDateTime.now())
                .qtd_produtos_vendidos(0)
                .avaliacao_media(0.0)
                .build();

        Loja lojaSalva = lojaRepo.save(novaLoja);

        if (dto.horarios_funcionamento() != null && !dto.horarios_funcionamento().isEmpty()) {
            log.debug("Processando {} horários para a nova loja id={}...", dto.horarios_funcionamento().size(), lojaSalva.getId());

            var builderLista = new java.util.ArrayList<HorarioFuncionamento>();
            int idx = 0;
            for (HorarioFuncionamentoDTO hDto : dto.horarios_funcionamento()) {
                log.trace("Horario[{}] recebido: diaSemana={}, turno={}", idx, hDto != null ? hDto.diaSemana() : null, hDto != null ? hDto.turno() : null);
                if (hDto == null) {
                    throw new IllegalArgumentException("Elemento de horário null na posição " + idx);
                }
                if (hDto.diaSemana() == null) {
                    throw new IllegalArgumentException("diaSemana não pode ser null (índice=" + idx + ")");
                }
                if (hDto.turno() == null) {
                    throw new IllegalArgumentException("turno não pode ser null (diaSemana=" + hDto.diaSemana() + ", índice=" + idx + ")");
                }
                builderLista.add(HorarioFuncionamento.builder()
                        .loja(lojaSalva)
                        .dia_semana(hDto.diaSemana())
                        .turno(hDto.turno())
                        .build());
                idx++;
            }
            horarioRepo.saveAll(builderLista);
            log.debug("{} horários persistidos para a loja id={}", builderLista.size(), lojaSalva.getId());
        } else {
            log.debug("Nenhum horário informado para a loja id={}", lojaSalva.getId());
        }

        return toResponse(lojaSalva);
    }

    public List<LojaResponse> listar() {
        return lojaRepo.findAll().stream().map(this::toResponse).toList();
    }

    public List<LojaResponse> listarPorUsuario(Long idUsuario) {
        Usuario u = usuarioRepo.findById(idUsuario)
            .orElseThrow(() -> new RuntimeException("Usuário não encontrado com ID: " + idUsuario));
        return lojaRepo.findByUsuario(u).stream().map(this::toResponse).toList();
    }

    public LojaResponse atualizar(Long id, UpdateLoja dto) {
        Loja loja = lojaRepo.findById(id).orElseThrow();

        loja.setNome_fantasia(dto.nome_fantasia());
        loja.setDescricao(dto.descricao());
        loja.setFoto_capa(dto.foto_capa());
        loja.setLocalizacao(dto.localizacao());
        loja.setStatus(dto.status());
        loja.setVisivel(dto.visivel());
        loja.setAceita_pix(dto.aceita_pix());
        loja.setAceita_dinheiro(dto.aceita_dinheiro());
        loja.setAceita_cartao(dto.aceita_cartao());

        return toResponse(lojaRepo.save(loja));
    }

    @Transactional
    public void desativarLoja(Long idLoja) {
        Loja loja = lojaRepo.findById(idLoja)
                .orElseThrow(() -> new IllegalArgumentException("Loja não encontrada"));

        // Desativa a loja
        loja.setStatus(false);
        loja.setVisivel(false);
        lojaRepo.save(loja);

        // Atualiza o usuário para refletir que ele não possui mais loja
        Usuario usuario = loja.getUsuario();
        usuario.setPossui_loja(false);
        // Ajuste no tipo de perfil - idealmente seria um Enum
        // usuario.setTipo_perfil(TipoPerfil.COMPRADOR);
        usuarioRepo.save(usuario);
    }

    @Transactional
    public LojaResponse atualizarStatus(Long idLoja, UpdateLojaStatusReq dto) {
        Loja loja = lojaRepo.findById(idLoja)
                .orElseThrow(() -> new IllegalArgumentException("Loja não encontrada"));

        loja.setStatus(dto.status());
        if (dto.manual_override() != null) {
            loja.setManual_override(dto.manual_override());
        }
        
        return toResponse(lojaRepo.save(loja));
    }

    public List<HorarioFuncionamentoDTO> listarHorarios(Long idLoja) {
        Loja loja = lojaRepo.findById(idLoja)
                .orElseThrow(() -> new IllegalArgumentException("Loja não encontrada"));

        return horarioRepo.findByLoja(loja).stream()
                .map(h -> new HorarioFuncionamentoDTO(h.getDia_semana(), h.getTurno()))
                .toList();
    }

    public void atualizarHorarios(Long idLoja, List<HorarioFuncionamentoDTO> novosHorarios) {
        Loja loja = lojaRepo.findById(idLoja)
                .orElseThrow(() -> new IllegalArgumentException("Loja não encontrada"));

        horarioRepo.deleteByLoja(loja);

        if (novosHorarios != null && !novosHorarios.isEmpty()) {
            log.debug("Atualizando {} horários para loja id={}", novosHorarios.size(), idLoja);
            var builderLista = new java.util.ArrayList<HorarioFuncionamento>();
            int idx = 0;
            for (HorarioFuncionamentoDTO hDto : novosHorarios) {
                log.trace("(Atualização) Horario[{}]: diaSemana={}, turno={}", idx, hDto != null ? hDto.diaSemana() : null, hDto != null ? hDto.turno() : null);
                if (hDto == null) {
                    throw new IllegalArgumentException("Elemento de horário null na posição " + idx);
                }
                if (hDto.diaSemana() == null) {
                    throw new IllegalArgumentException("diaSemana não pode ser null (índice=" + idx + ")");
                }
                if (hDto.turno() == null) {
                    throw new IllegalArgumentException("turno não pode ser null (diaSemana=" + hDto.diaSemana() + ", índice=" + idx + ")");
                }
                builderLista.add(HorarioFuncionamento.builder()
                        .loja(loja)
                        .dia_semana(hDto.diaSemana())
                        .turno(hDto.turno())
                        .build());
                idx++;
            }
            horarioRepo.saveAll(builderLista);
            log.debug("{} horários atualizados para a loja id={}", builderLista.size(), idLoja);
        } else {
            log.debug("Lista de novos horários vazia ou null para loja id={}", idLoja);
        }
    }

    public LojaResponse buscarPorId(Long id) {
        Loja loja = lojaRepo.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Loja não encontrada"));
        return toResponse(loja);
    }

    // Roda a cada 5 minutos; respeita manualOverride
    @Scheduled(fixedRate = 300000) // 300.000ms = 5 minutos
    @Transactional
    public void atualizarStatusAutomatico() {
        List<Loja> lojas = lojaRepo.findAll();

        for (Loja loja : lojas) {
            // se o dono fechou/abriu manualmente, respeitamos (override)
            if (Boolean.TRUE.equals(loja.getManual_override())) {
                continue;
            }

            // se não tiver horários configurados -> não altera status
            List<HorarioFuncionamento> horarios = horarioRepo.findByLoja(loja);
            if (horarios.isEmpty()) {
                continue;
            }

            boolean aberta = verificarSeAberta(horarios);
            loja.setStatus(aberta);
            lojaRepo.save(loja);
        }
    }

    private boolean verificarSeAberta(List<HorarioFuncionamento> horarios) {
        var agora = LocalDateTime.now();
        var hora = agora.getHour();
        var dia = agora.getDayOfWeek().name(); // DOMINGO, SEGUNDA, etc.

        return horarios.stream().anyMatch(h -> {
            if (!h.getDia_semana().name().equalsIgnoreCase(dia)) return false;

            return switch (h.getTurno()) {
                case MANHA -> hora >= 7 && hora < 12;
                case TARDE -> hora >= 12 && hora < 18;
                case NOITE -> hora >= 18 && hora <= 22;
            };
        });
    }
    
    // MÉTODO AUXILIAR MOVIDO PARA O LUGAR CORRETO
    private LojaResponse toResponse(Loja l) {
        Usuario u = l.getUsuario();
        UsuarioResponse usuarioDto = new UsuarioResponse(
                u.getId(),
                u.getNome(),
                u.getEmail(),
                u.getLogin());

        var horarios = horarioRepo.findByLoja(l).stream()
                .map(h -> new HorarioFuncionamentoDTO(h.getDia_semana(), h.getTurno()))
                .toList();

        return new LojaResponse(
                l.getId(),
                l.getNome_fantasia(),
                l.getDescricao(),
                l.getFoto_capa(),
                l.getLocalizacao(),
                l.getStatus(),
                l.getVisivel(),
                l.getAceita_pix(),
                l.getAceita_dinheiro(),
                l.getAceita_cartao(),
                l.getData_criacao(),
                l.getQtd_produtos_vendidos(),
                l.getAvaliacao_media(),
                usuarioDto,
                l.getManual_override() != null ? l.getManual_override() : false,
                horarios);
    }
}