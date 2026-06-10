package com.example.crudObsidiana.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.*;

@Service
public class FileStorageService {

    @Value("${upload.dir}") //passando o caminho para salvar as imagens
    private String pastaUpload;

    public Path salvarArquivo(MultipartFile arquivo) throws IOException {
        // Criar diretório se não existir:
        Path pasta = Paths.get(pastaUpload);
        if (!Files.exists(pasta)) {
            Files.createDirectories(pasta);
        }
        // Prefixo com timestamp
        String nomeSeguro = System.currentTimeMillis() + "_" + arquivo.getOriginalFilename();
        // Montar o caminho completo das imagens
        Path caminho = pasta.resolve(nomeSeguro);

        // Copiar (se já existir, sobrescreve)
        Files.copy(arquivo.getInputStream(), caminho, StandardCopyOption.REPLACE_EXISTING);
        return caminho;
    }

    public byte[] lerArquivo(String nomeArquivo) throws IOException {
        Path caminho = Paths.get(pastaUpload).resolve(nomeArquivo);
        if (!Files.exists(caminho)) {
            throw new IOException("Arquivo não encontrado");
        }
        return Files.readAllBytes(caminho);
    }

    // Apaga o arquivo do disco se existir. Retorna true se removeu, false se já não existia.
    // Não lança quando o arquivo está ausente — a chamada de DELETE é idempotente do ponto de vista do controller.
    public boolean deletarArquivo(String nomeArquivo) throws IOException {
        if (nomeArquivo == null || nomeArquivo.isEmpty()) return false;
        Path caminho = Paths.get(pastaUpload).resolve(nomeArquivo);
        return Files.deleteIfExists(caminho);
    }
} // FIM CLASSE
