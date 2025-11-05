package dev.jl.voting;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.Socket;
import java.util.Scanner;

public class VotingClient {
    private static final String SERVER_HOST = "localhost";
    private static final int TCP_PORT = 5000;
    private static final String MULTICAST_ADDRESS = "230.0.0.0";
    private static final int MULTICAST_PORT = 6000;

    private Socket socket;
    private DataInputStream in;
    private DataOutputStream out;
    private boolean isAdmin = false;
    private Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) {
        VotingClient client = new VotingClient();
        client.start();
    }

    public void start() {
        try {
            socket = new Socket(SERVER_HOST, TCP_PORT);
            in = new DataInputStream(socket.getInputStream());
            out = new DataOutputStream(socket.getOutputStream());

            new Thread(this::receiveMulticast).start();

            if (login()) {
                showMenu();
            }

        } catch (IOException e) {
            System.out.println("Erro ao conectar ao servidor: " + e.getMessage());
        } finally {
            closeConnection();
        }
    }

    private boolean login() throws IOException {
        System.out.println("SISTEMA DE VOTAÇÃO\n");
        System.out.print("Usuário: ");
        String username = scanner.nextLine();
        System.out.print("Senha: ");
        String password = scanner.nextLine();

        out.writeUTF("LOGIN");
        out.writeUTF(username);
        out.writeUTF(password);

        String status = in.readUTF();
        String message = in.readUTF();
        isAdmin = in.readBoolean();

        System.out.println(message);

        if (status.equals("SUCCESS")) {
            System.out.println("Tipo: " + (isAdmin ? "Administrador" : "Eleitor"));
            return true;
        }
        return false;
    }

    private void showMenu() {
        while (true) {
            System.out.println("\nMENU");
            System.out.println("1. Ver candidatos");
            System.out.println("2. Votar");
            System.out.println("3. Ver resultados");

            if (isAdmin) {
                System.out.println("4. Adicionar candidato");
                System.out.println("5. Remover candidato");
                System.out.println("6. Enviar nota informativa");
            }

            System.out.println("0. Sair");
            System.out.print("Escolha: ");

            int choice = scanner.nextInt();
            scanner.nextLine();

            try {
                switch (choice) {
                    case 1:
                        viewCandidates();
                        break;
                    case 2:
                        vote();
                        break;
                    case 3:
                        viewResults();
                        break;
                    case 4:
                        if (isAdmin) addCandidate();
                        break;
                    case 5:
                        if (isAdmin) removeCandidate();
                        break;
                    case 6:
                        if (isAdmin) broadcast();
                        break;
                    case 0:
                        out.writeUTF("EXIT");
                        return;
                    default:
                        System.out.println("Opção inválida");
                }
            } catch (IOException e) {
                System.out.println("Erro na comunicação: " + e.getMessage());
                return;
            }
        }
    }

    private void viewCandidates() throws IOException {
        out.writeUTF("GET_CANDIDATES");

        int count = in.readInt();
        System.out.println("\nCANDIDATOS");
        for (int i = 0; i < count; i++) {
            int id = in.readInt();
            String name = in.readUTF();
            System.out.println(id + ". " + name);
        }
        boolean votingOpen = in.readBoolean();
        System.out.println("\nStatus: " + (votingOpen ? "Votação ABERTA" : "Votação ENCERRADA"));
    }

    private void vote() throws IOException {
        viewCandidates();

        System.out.print("\nDigite o ID do candidato: ");
        int candidateId = scanner.nextInt();
        scanner.nextLine();

        out.writeUTF("VOTE");
        out.writeInt(candidateId);

        String status = in.readUTF();
        String message = in.readUTF();
        System.out.println(message);
    }

    private void viewResults() throws IOException {
        out.writeUTF("GET_RESULTS");

        int count = in.readInt();
        System.out.println("\nRESULTADOS");

        for (int i = 0; i < count; i++) {
            String name = in.readUTF();
            int votes = in.readInt();
            double percentage = in.readDouble();
            System.out.printf("%s: %d votos (%.2f%%)\n", name, votes, percentage);
        }

        String winner = in.readUTF();
        int total = in.readInt();

        System.out.println("\nTotal de votos: " + total);
        System.out.println("Vencedor: " + winner);
    }

    private void addCandidate() throws IOException {
        System.out.print("Nome do candidato: ");
        String name = scanner.nextLine();

        out.writeUTF("ADD_CANDIDATE");
        out.writeUTF(name);

        String status = in.readUTF();
        String message = in.readUTF();
        System.out.println(message);
    }

    private void removeCandidate() throws IOException {
        viewCandidates();

        System.out.print("\nID do candidato a remover: ");
        int id = scanner.nextInt();
        scanner.nextLine();

        out.writeUTF("REMOVE_CANDIDATE");
        out.writeInt(id);

        String status = in.readUTF();
        String message = in.readUTF();
        System.out.println(message);
    }

    private void broadcast() throws IOException {
        System.out.print("Mensagem para todos os eleitores: ");
        String message = scanner.nextLine();

        out.writeUTF("BROADCAST");
        out.writeUTF(message);

        String status = in.readUTF();
        String response = in.readUTF();
        System.out.println(response);
    }

    private void receiveMulticast() {
        try {
            InetAddress group = InetAddress.getByName(MULTICAST_ADDRESS);
            MulticastSocket socket = new MulticastSocket(MULTICAST_PORT);
            socket.joinGroup(group);

            byte[] buffer = new byte[1024];

            while (true) {
                DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
                socket.receive(packet);
                String message = new String(packet.getData(), 0, packet.getLength());
                System.out.println("\n[NOTA INFORMATIVA] " + message);
                System.out.print("Escolha: ");
            }
        } catch (IOException e) {
            // Thread
        }
    }

    private void closeConnection() {
        try {
            if (socket != null) socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}