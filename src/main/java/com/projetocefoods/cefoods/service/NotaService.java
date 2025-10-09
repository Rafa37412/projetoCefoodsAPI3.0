package com.projetocefoods.cefoods.service;

import com.projetocefoods.cefoods.dto.NotaDTO;
import com.projetocefoods.cefoods.model.*;
import com.projetocefoods.cefoods.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class NotaService {

    private final NotaRepository notaRepo;
    private final AnexoRepository anexoRepo;
    private final UsuarioRepository usuarioRepo;
    private final LojaRepository lojaRepo;

    @Transactional
    public Nota criarNota(String titulo, String texto, Long idUsuario, Long idLoja, MultipartFile[] anexos)
            throws IOException {
    Long usuarioId = Objects.requireNonNull(idUsuario, "idUsuario é obrigatório");
    Long lojaId = Objects.requireNonNull(idLoja, "idLoja é obrigatório");

    Usuario u = usuarioRepo.findById(usuarioId)
                .orElseThrow(() -> new IllegalArgumentException("Usuário não encontrado"));

    Loja l = lojaRepo.findById(lojaId)
                .orElseThrow(() -> new IllegalArgumentException("Loja não encontrada"));

        // 🔹 inicializa com lista vazia
        Nota nota = Nota.builder()
                .titulo(titulo)
                .texto(texto)
                .usuario(u)
                .loja(l)
                .dataCriacao(LocalDateTime.now())
                .anexos(new java.util.ArrayList<>()) // garante que não será null
                .build();

        Nota salvo = notaRepo.save(nota);

        if (anexos != null && anexos.length > 0) {
            for (MultipartFile f : anexos) {
                if (f == null || f.isEmpty())
                    continue;

                Anexo a = Anexo.builder()
                        .nota(salvo)
                        .nome_arquivo(f.getOriginalFilename())
                        .tipo(f.getContentType())
                        .tamanho(f.getSize())
                        .dados(f.getBytes())
                        .build();

                // salva anexo e adiciona à lista da nota
                Anexo salvoAnexo = anexoRepo.save(a);
                salvo.getAnexos().add(salvoAnexo);
            }
            // 🔹 força atualização da lista de anexos na nota
            salvo = notaRepo.save(salvo);
        }

        return salvo;
    }

    @Transactional
    public Nota criarNota(NotaDTO.CreateNota dto) throws IOException {
        return criarNota(
                dto.titulo(),
                dto.texto(),
                dto.idUsuario(),
                dto.idLoja(),
                null);
    }

    public List<Nota> listarPorLoja(Long idLoja) {
        return notaRepo.findByLojaIdOrderByDataCriacaoDesc(idLoja);
    }

    public Nota buscarPorId(Long id) {
        return notaRepo.findById(id).orElseThrow(() -> new IllegalArgumentException("Nota não encontrada"));
    }

    @Transactional
    public void deletar(Long id) {
        notaRepo.deleteById(id);
    }

    public Anexo buscarAnexo(Long idAnexo) {
        return anexoRepo.findById(idAnexo).orElseThrow(() -> new IllegalArgumentException("Anexo não encontrado"));
    }

    @Transactional
    public Nota editarNota(Long idNota, String titulo, String texto, MultipartFile[] anexos) throws IOException {
        Nota nota = notaRepo.findById(idNota)
                .orElseThrow(() -> new IllegalArgumentException("Nota não encontrada"));

        if (titulo != null && !titulo.isBlank()) {
            nota.setTitulo(titulo);
        }
        if (texto != null) {
            nota.setTexto(texto);
        }

        // Se recebeu novos anexos, adiciona
        if (anexos != null && anexos.length > 0) {
            for (MultipartFile f : anexos) {
                if (f == null || f.isEmpty())
                    continue;

                Anexo a = Anexo.builder()
                        .nota(nota)
                        .nome_arquivo(f.getOriginalFilename())
                        .tipo(f.getContentType())
                        .tamanho(f.getSize())
                        .dados(f.getBytes())
                        .build();

                Anexo salvoAnexo = anexoRepo.save(a);
                nota.getAnexos().add(salvoAnexo);
            }
        }

        return notaRepo.save(nota);
    }

}
