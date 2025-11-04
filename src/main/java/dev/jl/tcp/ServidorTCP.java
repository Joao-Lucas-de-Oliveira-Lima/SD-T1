package dev.jl.tcp;

import java.io.*;
import java.net.*;

/**
 * Servidor TCP para o Sistema de Gestão de Loja de Roupas
 * Exercício 4: Serialização com Socket TCP
 */
public class ServidorTCP {
    private static final int PORTA = 12345;
    private ServerSocket serverSocket;
    private ServicoVendas servicoVendas;
    private boolean rodando;

    public ServidorTCP() {
        inicializarLoja();
    }

    /**
     * Inicializa a loja com dados de exemplo
     */
    private void inicializarLoja() {
        Loja loja = new Loja("Loja de Roupas Fashion", "12.345.678/0001-90");

        // Adiciona peças de exemplo
        loja.adicionarPeca(new Camiseta(1, "Camiseta Básica Branca", 49.90, "M", "Branco", 15, "Curta", "Redonda"));
        loja.adicionarPeca(new Camiseta(2, "Camiseta Polo", 79.90, "G", "Azul", 10, "Curta", "Polo"));
        loja.adicionarPeca(new Calca(3, "Calça Jeans Skinny", 159.90, "42", "Azul", 8, "Skinny", true));
        loja.adicionarPeca(new Calca(4, "Calça Social", 189.90, "44", "Preto", 5, "Social", true));
        loja.adicionarPeca(new Vestido(5, "Vestido Floral", 189.90, "P", "Rosa", 6, "Midi", "Casual"));
        loja.adicionarPeca(new Vestido(6, "Vestido de Festa", 349.90, "M", "Vermelho", 3, "Longo", "Festa"));
        loja.adicionarPeca(new Jaqueta(7, "Jaqueta Jeans", 229.90, "M", "Azul", 4, "Jeans", false));
        loja.adicionarPeca(new Jaqueta(8, "Jaqueta de Couro", 599.90, "G", "Preto", 2, "Couro", true));
        loja.adicionarPeca(new Acessorio(9, "Cinto de Couro", 89.90, "Único", "Marrom", 12, "Cinto", "Couro"));
        loja.adicionarPeca(new Acessorio(10, "Bolsa Transversal", 149.90, "Único", "Preto", 7, "Bolsa", "Sintético"));

        this.servicoVendas = new ServicoVendas(loja);
    }

    /**
     * Inicia o servidor
     */
    public void iniciar() {
        try {
            serverSocket = new ServerSocket(PORTA);
            rodando = true;
            System.out.println("========================================");
            System.out.println("  SERVIDOR DE GESTÃO DE LOJA DE ROUPAS");
            System.out.println("========================================");
            System.out.println("Servidor iniciado na porta " + PORTA);
            System.out.println("Loja: " + servicoVendas.getLoja());
            System.out.println("Aguardando conexões...\n");

            while (rodando) {
                try {
                    Socket clienteSocket = serverSocket.accept();
                    System.out.println("Cliente conectado: " + clienteSocket.getInetAddress().getHostAddress());

                    // Processa requisição em uma nova thread
                    Thread thread = new Thread(() -> processarCliente(clienteSocket));
                    thread.start();

                } catch (SocketException e) {
                    if (!rodando) {
                        System.out.println("Servidor encerrado.");
                    }
                }
            }

        } catch (IOException e) {
            System.err.println("Erro ao iniciar servidor: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Processa requisições do cliente
     */
    private void processarCliente(Socket clienteSocket) {
        try (ObjectOutputStream out = new ObjectOutputStream(clienteSocket.getOutputStream());
             ObjectInputStream in = new ObjectInputStream(clienteSocket.getInputStream())) {

            // Desempacota a mensagem de requisição
            VendaRequest request = (VendaRequest) in.readObject();
            System.out.println("Requisição recebida: " + request.getOperacao());

            // Processa a requisição
            VendaResponse response = processarRequisicao(request);

            // Empacota e envia a mensagem de resposta
            out.writeObject(response);
            out.flush();

            System.out.println("Resposta enviada: " + response.getMensagem() + "\n");

        } catch (IOException | ClassNotFoundException e) {
            System.err.println("Erro ao processar cliente: " + e.getMessage());
        } finally {
            try {
                clienteSocket.close();
            } catch (IOException e) {
                System.err.println("Erro ao fechar socket: " + e.getMessage());
            }
        }
    }

    /**
     * Processa uma requisição e retorna a resposta
     */
    private VendaResponse processarRequisicao(VendaRequest request) {
        VendaResponse response = new VendaResponse();

        try {
            switch (request.getOperacao()) {
                case LISTAR_PECAS:
                    response.setPecas(servicoVendas.listarPecas());
                    response.setSucesso(true);
                    response.setMensagem("Lista de peças recuperada com sucesso");
                    break;

                case BUSCAR_PECA:
                    PecaRoupa peca = servicoVendas.buscarPeca(request.getId());
                    if (peca != null) {
                        response.setPeca(peca);
                        response.setSucesso(true);
                        response.setMensagem("Peça encontrada");
                    } else {
                        response.setSucesso(false);
                        response.setMensagem("Peça não encontrada");
                    }
                    break;

                case BUSCAR_POR_TIPO:
                    response.setPecas(servicoVendas.buscarPorTipo(request.getTipo()));
                    response.setSucesso(true);
                    response.setMensagem("Busca por tipo realizada");
                    break;

                case ADICIONAR_PECA:
                    boolean adicionado = servicoVendas.adicionarPeca(request.getPeca());
                    response.setSucesso(adicionado);
                    response.setMensagem(adicionado ? "Peça adicionada com sucesso" : "Erro ao adicionar peça");
                    break;

                case REMOVER_PECA:
                    boolean removido = servicoVendas.removerPeca(request.getId());
                    response.setSucesso(removido);
                    response.setMensagem(removido ? "Peça removida com sucesso" : "Erro ao remover peça");
                    break;

                case ATUALIZAR_ESTOQUE:
                    boolean atualizado = servicoVendas.atualizarEstoque(request.getId(), request.getQuantidade());
                    response.setSucesso(atualizado);
                    response.setMensagem(atualizado ? "Estoque atualizado" : "Erro ao atualizar estoque");
                    break;

                case REALIZAR_VENDA:
                    boolean vendido = servicoVendas.realizarVenda(request.getId(), request.getQuantidade());
                    response.setSucesso(vendido);
                    response.setMensagem(vendido ? "Venda realizada com sucesso" : "Estoque insuficiente");
                    break;

                case CALCULAR_TOTAL:
                    double total = servicoVendas.calcularTotal(request.getIds(), request.getQuantidades());
                    response.setValor(total);
                    response.setSucesso(true);
                    response.setMensagem("Total calculado: R$ " + String.format("%.2f", total));
                    break;

                default:
                    response.setSucesso(false);
                    response.setMensagem("Operação não suportada");
            }
        } catch (Exception e) {
            response.setSucesso(false);
            response.setMensagem("Erro no servidor: " + e.getMessage());
        }

        return response;
    }

    /**
     * Para o servidor
     */
    public void parar() {
        rodando = false;
        try {
            if (serverSocket != null && !serverSocket.isClosed()) {
                serverSocket.close();
            }
        } catch (IOException e) {
            System.err.println("Erro ao parar servidor: " + e.getMessage());
        }
    }

    public static void main(String[] args) {
        ServidorTCP servidor = new ServidorTCP();
        servidor.iniciar();
    }
}