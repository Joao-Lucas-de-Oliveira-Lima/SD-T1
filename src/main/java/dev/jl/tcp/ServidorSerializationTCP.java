package dev.jl.tcp;

import dev.jl.models.Camiseta;
import dev.jl.models.Loja;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;


public class ServidorSerializationTCP {
    private static final int PORT = 12345;

    private static Loja loja = new Loja();

    static {
        loja.adicionarPeca(new Camiseta(100, "Camiseta Remota", 79.99, "G", "Verde", 0));
        loja.adicionarPeca(new Camiseta(101, "Camiseta Básica", 49.90, "M", "Azul", 1));
    }

    public static void main(String[] args) {
        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            System.out.println("Servidor TCp. Escutando na porta " + PORT + "...");

            while (true) {
                try (Socket clientSocket = serverSocket.accept()) {
                    System.out.println("\n[Servidor] Cliente conectado: " + clientSocket.getInetAddress());

                    ObjectOutputStream out = new ObjectOutputStream(clientSocket.getOutputStream());
                    ObjectInputStream in = new ObjectInputStream(clientSocket.getInputStream());

                    String requisicao = (String) in.readObject();
                    System.out.println("[Servidor] Requisicao desempacotada: '" + requisicao + "'");

                    Object resposta = null;

                    if ("GET_ESTOQUE".equals(requisicao)) {
                        System.out.println("[Servidor] Executando serviço: Retornar Estoque.");
                        resposta = loja.getEstoque();
                    } else {
                        resposta = "Comando desconhecido: " + requisicao;
                    }

                    out.writeObject(resposta);
                    out.flush();
                    System.out.println("[Servidor] Resposta (Objeto List<PecaRoupa>) empacotada e enviada.");

                } catch (IOException | ClassNotFoundException e) {
                    System.err.println("[Servidor] Erro na comunicação ou serialização: " + e.getMessage());
                }
            }
        } catch (IOException e) {
            System.err.println("Erro ao iniciar o servidor: " + e.getMessage());
        }
    }
}