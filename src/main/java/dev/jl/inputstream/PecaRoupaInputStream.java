package dev.jl.inputstream;

import dev.jl.models.PecaRoupa;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

/**
 * Subclasse de InputStream que lê dados de PecaRoupa
 * Exercício 3: Subclasse de InputStream
 */
public class PecaRoupaInputStream extends InputStream {
    private InputStream origem;
    private int numeroPecas;
    private int[] bytesAtributos;
    private boolean headerLido;

    /**
     * Construtor conforme especificação do exercício
     * @param origem InputStream de onde as sequências de bytes serão lidas
     */
    public PecaRoupaInputStream(InputStream origem) {
        this.origem = origem;
        this.bytesAtributos = new int[3]; // id, nome, preco
        this.headerLido = false;
    }

    @Override
    public int read() throws IOException {
        return origem.read();
    }

    @Override
    public int read(byte[] b) throws IOException {
        return origem.read(b);
    }

    @Override
    public int read(byte[] b, int off, int len) throws IOException {
        return origem.read(b, off, len);
    }

    /**
     * Lê o cabeçalho com informações sobre a transmissão
     */
    private void lerHeader() throws IOException {
        if (headerLido) return;

        // Lê número de peças (4 bytes)
        numeroPecas = (origem.read() << 24) |
                (origem.read() << 16) |
                (origem.read() << 8) |
                origem.read();

        // Lê configuração de bytes para cada atributo
        for (int i = 0; i < 3; i++) {
            bytesAtributos[i] = (origem.read() << 8) | origem.read();
        }

        headerLido = true;
    }

    /**
     * Lê todas as peças de roupa
     */
    public List<PecaRoupa> lerPecas() throws IOException {
        lerHeader();

        List<PecaRoupa> pecas = new ArrayList<>();

        for (int i = 0; i < numeroPecas; i++) {
            PecaRoupa peca = lerPeca();
            if (peca != null) {
                pecas.add(peca);
            }
        }

        return pecas;
    }

    /**
     * Lê uma peça individual
     */
    private PecaRoupa lerPeca() throws IOException {
        // Lê ID
        int id = lerInteiro(bytesAtributos[0]);

        // Lê Nome
        String nome = lerString(bytesAtributos[1]);

        // Lê Preço
        double preco = lerDouble(bytesAtributos[2]);

        // Cria uma PecaRoupa básica com os dados lidos
        return new PecaRoupa(id, nome, preco, "", "", 0);
    }

    /**
     * Lê um inteiro usando o número especificado de bytes
     */
    private int lerInteiro(int numBytes) throws IOException {
        int valor = 0;
        for (int i = numBytes - 1; i >= 0; i--) {
            int b = origem.read();
            if (b == -1) throw new EOFException("Fim inesperado do stream");
            valor |= (b << (i * 8));
        }
        return valor;
    }

    /**
     * Lê uma String usando o número especificado de bytes
     */
    private String lerString(int numBytes) throws IOException {
        // Lê tamanho da string (4 bytes)
        int tamanho = lerInteiro(4);

        // Lê os bytes da string
        byte[] bytes = new byte[tamanho];
        int lidos = origem.read(bytes);
        if (lidos != tamanho) {
            throw new IOException("Não foi possível ler todos os bytes da string");
        }

        // Lê os bytes de padding
        for (int i = tamanho; i < numBytes - 4; i++) {
            origem.read();
        }

        return new String(bytes, StandardCharsets.UTF_8);
    }

    /**
     * Lê um double usando o número especificado de bytes
     */
    private double lerDouble(int numBytes) throws IOException {
        long bits = 0;
        for (int i = numBytes - 1; i >= 0; i--) {
            int b = origem.read();
            if (b == -1) throw new EOFException("Fim inesperado do stream");
            bits |= ((long) b << (i * 8));
        }
        return Double.longBitsToDouble(bits);
    }

    public int getNumeroPecas() {
        return numeroPecas;
    }

    public int[] getBytesAtributos() {
        return bytesAtributos;
    }

    @Override
    public void close() throws IOException {
        origem.close();
    }
}