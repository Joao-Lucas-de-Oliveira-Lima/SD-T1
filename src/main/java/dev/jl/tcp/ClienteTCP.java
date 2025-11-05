package dev.jl.tcp;

import dev.jl.models.PecaRoupa;

import java.io.*;
import java.net.Socket;
import java.util.List;

public class ClienteTCP {
    private static final String SERVER_HOST = "localhost";
    private static final int SERVER_PORT_NATIVA = 12345;

    public static void main(String[] args) {
        testeSerializacaoNativa();
    }

    private static void testeSerializacaoNativa() {

        try (Socket socket = new Socket(SERVER_HOST, SERVER_PORT_NATIVA);
             ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
             ObjectInputStream in = new ObjectInputStream(socket.getInputStream())) {

            String requisicao = "GET_ESTOQUE";
            System.out.println("[Cliente] Empacotando e enviando requisição: '" + requisicao + "'");
            out.writeObject(requisicao);
            out.flush();

            Object resposta = in.readObject();

            System.out.println("[Cliente] Resposta desempacotada recebida.");

            if (resposta instanceof List) {
                List<PecaRoupa> estoque = (List<PecaRoupa>) resposta;
                System.out.println("SUCESSO! Peças de roupa no estoque remoto (Total: " + estoque.size() + "):");
                for (PecaRoupa peca : estoque) {
                    System.out.println(" - " + peca);
                }
            } else {
                System.err.println("Resposta não esperada do servidor: " + resposta);
            }

        } catch (IOException | ClassNotFoundException e) {
            System.err.println("[Cliente] ERRO: Não foi possível conectar ou serializar.");
            System.err.println("Verifique se o ServidorSerializacaoTCP está rodando na porta " + SERVER_PORT_NATIVA);
            e.printStackTrace();
        }
    }
}