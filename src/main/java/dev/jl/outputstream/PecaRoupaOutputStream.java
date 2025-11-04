package dev.jl.outputstream;

import dev.jl.models.PecaRoupa;

import java.io.*;
import java.nio.charset.StandardCharsets;

/**
 * Subclasse de OutputStream que envia dados de PecaRoupa
 * Exercício 2: Subclasse de OutputStream
 */
public class PecaRoupaOutputStream extends OutputStream {
    private OutputStream destino;
    private PecaRoupa[] pecas;
    private int numeroPecas;
    private int[] bytesAtributos; // bytes para id, nome, preco
    private int indiceAtual;
    private boolean headerEnviado;

    /**
     * Construtor conforme especificação do exercício
     * @param pecas Array de objetos a serem transmitidos
     * @param numeroPecas Número de objetos que terão dados enviados
     * @param bytesAtributos Array com número de bytes para cada atributo (id, nome, preco)
     * @param destino OutputStream de destino
     */
    public PecaRoupaOutputStream(PecaRoupa[] pecas, int numeroPecas, int[] bytesAtributos, OutputStream destino) {
        this.pecas = pecas;
        this.numeroPecas = Math.min(numeroPecas, pecas.length);
        this.bytesAtributos = bytesAtributos;
        this.destino = destino;
        this.indiceAtual = 0;
        this.headerEnviado = false;
    }

    @Override
    public void write(int b) throws IOException {
        destino.write(b);
    }

    @Override
    public void write(byte[] b) throws IOException {
        destino.write(b);
    }

    @Override
    public void write(byte[] b, int off, int len) throws IOException {
        destino.write(b, off, len);
    }

    /**
     * Envia o cabeçalho com informações sobre a transmissão
     */
    private void enviarHeader() throws IOException {
        if (headerEnviado) return;

        // Envia número de peças
        destino.write(numeroPecas >> 24);
        destino.write(numeroPecas >> 16);
        destino.write(numeroPecas >> 8);
        destino.write(numeroPecas);

        // Envia configuração de bytes para cada atributo
        for (int bytes : bytesAtributos) {
            destino.write(bytes >> 8);
            destino.write(bytes);
        }

        headerEnviado = true;
    }

    /**
     * Envia todas as peças de roupa
     */
    public void enviarPecas() throws IOException {
        enviarHeader();

        for (int i = 0; i < numeroPecas; i++) {
            enviarPeca(pecas[i]);
        }

        destino.flush();
    }

    /**
     * Envia uma peça individual
     */
    private void enviarPeca(PecaRoupa peca) throws IOException {
        // Envia ID (bytesAtributos[0] bytes)
        enviarInteiro(peca.getId(), bytesAtributos[0]);

        // Envia Nome (bytesAtributos[1] bytes)
        enviarString(peca.getNome(), bytesAtributos[1]);

        // Envia Preço (bytesAtributos[2] bytes)
        enviarDouble(peca.getPreco(), bytesAtributos[2]);
    }

    /**
     * Envia um inteiro usando o número especificado de bytes
     */
    private void enviarInteiro(int valor, int numBytes) throws IOException {
        for (int i = numBytes - 1; i >= 0; i--) {
            destino.write((valor >> (i * 8)) & 0xFF);
        }
    }

    /**
     * Envia uma String usando o número especificado de bytes
     */
    private void enviarString(String str, int numBytes) throws IOException {
        byte[] bytes = str.getBytes(StandardCharsets.UTF_8);
        int tamanho = Math.min(bytes.length, numBytes - 4);

        // Envia tamanho da string (4 bytes)
        enviarInteiro(tamanho, 4);

        // Envia os bytes da string
        destino.write(bytes, 0, tamanho);

        // Preenche com zeros se necessário
        for (int i = tamanho; i < numBytes - 4; i++) {
            destino.write(0);
        }
    }

    /**
     * Envia um double usando o número especificado de bytes
     */
    private void enviarDouble(double valor, int numBytes) throws IOException {
        long bits = Double.doubleToLongBits(valor);
        for (int i = numBytes - 1; i >= 0; i--) {
            destino.write((int) ((bits >> (i * 8)) & 0xFF));
        }
    }

    @Override
    public void flush() throws IOException {
        destino.flush();
    }

    @Override
    public void close() throws IOException {
        destino.close();
    }
}