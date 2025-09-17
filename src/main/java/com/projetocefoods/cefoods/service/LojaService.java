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
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class LojaService {
    private final LojaRepository lojaRepo;
    private final UsuarioRepository usuarioRepo;
    private final HorarioFuncionamentoRepository horarioRepo;

    public LojaResponse criar(CreateLoja dto) {
        if (dto.idUsuario() == null) {
            throw new IllegalArgumentException("idUsuario não pode ser null");
        }

        Usuario u = usuarioRepo.findById(dto.idUsuario())
                .orElseThrow(() -> new IllegalArgumentException("Usuário não encontrado"));

        Loja novaLoja = Loja.builder()
                .usuario(u)
                .nomeFantasia(dto.nomeFantasia())
                .descricao(dto.descricao())
                .fotoCapa(dto.fotoCapa())
                .localizacao(dto.localizacao())
                .status(dto.status() != null ? dto.status() : false)
                .manualOverride(false) // iniciar sem override
                .visivel(dto.visivel() != null ? dto.visivel() : true)
                .aceitaPix(Boolean.TRUE.equals(dto.aceitaPix()))
                .aceitaDinheiro(Boolean.TRUE.equals(dto.aceitaDinheiro()))
                .aceitaCartao(Boolean.TRUE.equals(dto.aceitaCartao()))
                .dataCriacao(LocalDateTime.now())
                .qtdProdutosVendidos(0)
                .avaliacaoMedia(0.0)
                .build();

        Loja lojaSalva = lojaRepo.save(novaLoja);

        if (dto.horariosFuncionamento() != null && !dto.horariosFuncionamento().isEmpty()) {
            var horarios = dto.horariosFuncionamento().stream().map(h -> HorarioFuncionamento.builder()
                    .loja(lojaSalva)
                    .diaSemana(h.diaSemana())
                    .turno(h.turno())
                    .build()).toList();

            horarioRepo.saveAll(horarios);
        }

        return toResponse(lojaSalva);
    }

    public List<LojaResponse> listar() {
        return lojaRepo.findAll().stream().map(this::toResponse).toList();
    }

    public List<LojaResponse> listarPorUsuario(Long idUsuario) {
        Usuario u = usuarioRepo.findById(idUsuario).orElseThrow();
        return lojaRepo.findByUsuario(u).stream().map(this::toResponse).toList();
    }

    public LojaResponse atualizar(Long id, UpdateLoja dto) {
        Loja loja = lojaRepo.findById(id).orElseThrow();

        loja.setNomeFantasia(dto.nomeFantasia());
        loja.setDescricao(dto.descricao());
        loja.setFotoCapa(dto.fotoCapa());
        loja.setLocalizacao(dto.localizacao());
        loja.setStatus(dto.status());
        loja.setVisivel(dto.visivel());
        loja.setAceitaPix(dto.aceitaPix());
        loja.setAceitaDinheiro(dto.aceitaDinheiro());
        loja.setAceitaCartao(dto.aceitaCartao());

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
        usuario.setPossuiLoja(false);
        usuario.setTipoPerfil("comprador");
        usuarioRepo.save(usuario);
    }

    private LojaResponse toResponse(Loja l) {
        Usuario u = l.getUsuario();
        UsuarioResponse usuarioDto = new UsuarioResponse(
                u.getIdUsuario(),
                u.getNome(),
                u.getEmail(),
                u.getLogin());

        var horarios = horarioRepo.findByLoja(l).stream()
                .map(h -> new HorarioFuncionamentoDTO(h.getDiaSemana(), h.getTurno()))
                .toList();

        return new LojaResponse(
                l.getIdLoja(),
                l.getNomeFantasia(),
                l.getDescricao(),
                l.getFotoCapa(),
                l.getLocalizacao(),
                l.getStatus(),
                l.getVisivel(),
                l.getAceitaPix(),
                l.getAceitaDinheiro(),
                l.getAceitaCartao(),
                l.getDataCriacao(),
                l.getQtdProdutosVendidos(),
                l.getAvaliacaoMedia(),
                usuarioDto,
                l.getManualOverride() != null ? l.getManualOverride() : false,
                horarios);
    }

    public LojaResponse atualizarStatus(Long idLoja, UpdateLojaStatusReq dto) {
        Loja loja = lojaRepo.findById(idLoja)
                .orElseThrow(() -> new IllegalArgumentException("Loja não encontrada"));

        loja.setStatus(dto.status());
        if (dto.manualOverride() != null) {
            loja.setManualOverride(dto.manualOverride());
        }

        return toResponse(lojaRepo.save(loja));
    }

    public List<HorarioFuncionamentoDTO> listarHorarios(Long idLoja) {
        Loja loja = lojaRepo.findById(idLoja)
                .orElseThrow(() -> new IllegalArgumentException("Loja não encontrada"));

        return horarioRepo.findByLoja(loja).stream()
                .map(h -> new HorarioFuncionamentoDTO(h.getDiaSemana(), h.getTurno()))
                .toList();
    }

    public void atualizarHorarios(Long idLoja, List<HorarioFuncionamentoDTO> novosHorarios) {
        Loja loja = lojaRepo.findById(idLoja)
                .orElseThrow(() -> new IllegalArgumentException("Loja não encontrada"));

        horarioRepo.deleteByLoja(loja);

        if (novosHorarios != null && !novosHorarios.isEmpty()) {
            var horarios = novosHorarios.stream().map(h -> HorarioFuncionamento.builder()
                    .loja(loja)
                    .diaSemana(h.diaSemana())
                    .turno(h.turno())
                    .build()).toList();

            horarioRepo.saveAll(horarios);
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
            if (Boolean.TRUE.equals(loja.getManualOverride())) {
                continue;
            }

            // se não tiver horários configurados → não altera status
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
            if (!h.getDiaSemana().name().equalsIgnoreCase(dia)) return false;

            return switch (h.getTurno()) {
                case MANHA -> hora >= 7 && hora < 12;
                case TARDE -> hora >= 12 && hora < 18;
                case NOITE -> hora >= 18 && hora <= 22;
            };
        });
    }
}
