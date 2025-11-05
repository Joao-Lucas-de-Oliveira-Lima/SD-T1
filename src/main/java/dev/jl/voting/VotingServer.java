package dev.jl.voting;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.*;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

class VotingServer {
    private static final int TCP_PORT = 5000;
    private static final String MULTICAST_ADDRESS = "230.0.0.0";
    private static final int MULTICAST_PORT = 6000;

    private static Map<String, String> users = new ConcurrentHashMap<>();
    private static Set<String> admins = ConcurrentHashMap.newKeySet();
    private static List<Candidate> candidates = new CopyOnWriteArrayList<>();
    private static Map<Integer, Integer> votes = new ConcurrentHashMap<>();
    private static Set<String> votedUsers = ConcurrentHashMap.newKeySet();
    private static boolean votingOpen = true;
    private static int candidateIdCounter = 1;

    static {
        users.put("eleitor1", "senha1");
        users.put("eleitor2", "senha2");
        users.put("admin", "admin123");
        admins.add("admin");

        candidates.add(new Candidate(candidateIdCounter++, "João Silva"));
        candidates.add(new Candidate(candidateIdCounter++, "Maria Santos"));
        candidates.add(new Candidate(candidateIdCounter++, "Pedro Costa"));
    }

    public static void main(String[] args) {
        System.out.println("Servidor de Votação iniciado...");
        System.out.println("TCP Port: " + TCP_PORT);
        System.out.println("Multicast: " + MULTICAST_ADDRESS + ":" + MULTICAST_PORT);
        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                closeVoting();
            }
        }, 300000); // 5 minutos

        try (ServerSocket serverSocket = new ServerSocket(TCP_PORT)) {


            while (true) {
                Socket clientSocket = serverSocket.accept();
                new Thread(new ClientHandler(clientSocket)).start();
            }


        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void closeVoting() {
        votingOpen = false;
        System.out.println("\n VOTAÇÃO ENCERRADA!");
        calculateResults();
        sendMulticast("VOTAÇÃO ENCERRADA!");
    }

    private static void calculateResults() {
        int total = votes.values().stream().mapToInt(Integer::intValue).sum();
        System.out.println("Total de votos: " + total);

        for (Candidate c : candidates) {
            int v = votes.getOrDefault(c.id, 0);
            double pct = total > 0 ? (v * 100.0 / total) : 0;
            System.out.printf("%s: %d votos (%.2f%%)\n", c.name, v, pct);
        }

        if (total > 0) {
            Candidate winner = candidates.stream()
                    .max(Comparator.comparingInt(c -> votes.getOrDefault(c.id, 0)))
                    .orElse(null);
            if (winner != null) {
                System.out.println("Vencedor: " + winner.name);
            }
        }
    }

    static void sendMulticast(String message) {
        try {
            InetAddress group = InetAddress.getByName(MULTICAST_ADDRESS);
            MulticastSocket socket = new MulticastSocket();
            byte[] data = message.getBytes();
            DatagramPacket packet = new DatagramPacket(data, data.length, group, MULTICAST_PORT);
            socket.send(packet);
            socket.close();
            System.out.println("Multicast enviado: " + message);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    static class Candidate {
        int id;
        String name;

        Candidate(int id, String name) {
            this.id = id;
            this.name = name;
        }
    }

    static class ClientHandler implements Runnable {
        private Socket socket;
        private DataInputStream in;
        private DataOutputStream out;
        private String currentUser;

        public ClientHandler(Socket socket) {
            this.socket = socket;
        }

        @Override
        public void run() {
            try {
                in = new DataInputStream(socket.getInputStream());
                out = new DataOutputStream(socket.getOutputStream());

                handleClient();
            } catch (IOException e) {
                System.out.println("Cliente desconectado");
            } finally {
                try {
                    socket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        private void handleClient() throws IOException {
            while (true) {
                String command = in.readUTF();

                switch (command) {
                    case "LOGIN":
                        handleLogin();
                        break;
                    case "GET_CANDIDATES":
                        handleGetCandidates();
                        break;
                    case "VOTE":
                        handleVote();
                        break;
                    case "ADD_CANDIDATE":
                        handleAddCandidate();
                        break;
                    case "REMOVE_CANDIDATE":
                        handleRemoveCandidate();
                        break;
                    case "BROADCAST":
                        handleBroadcast();
                        break;
                    case "GET_RESULTS":
                        handleGetResults();
                        break;
                    case "EXIT":
                        return;
                    default:
                        out.writeUTF("ERROR");
                        out.writeUTF("Comando desconhecido");
                }
            }
        }

        private void handleLogin() throws IOException {
            String username = in.readUTF();
            String password = in.readUTF();

            if (users.containsKey(username) && users.get(username).equals(password)) {
                currentUser = username;
                out.writeUTF("SUCCESS");
                out.writeUTF("Login realizado com sucesso");
                out.writeBoolean(admins.contains(username));
                System.out.println("Login: " + username);
            } else {
                out.writeUTF("FAILURE");
                out.writeUTF("Credenciais inválidas");
                out.writeBoolean(false);
            }
        }

        private void handleGetCandidates() throws IOException {
            out.writeInt(candidates.size());
            for (Candidate c : candidates) {
                out.writeInt(c.id);
                out.writeUTF(c.name);
            }
            out.writeBoolean(votingOpen);
        }

        private void handleVote() throws IOException {
            int candidateId = in.readInt();

            if (!votingOpen) {
                out.writeUTF("FAILURE");
                out.writeUTF("Votação encerrada");
                return;
            }

            if (votedUsers.contains(currentUser)) {
                out.writeUTF("FAILURE");
                out.writeUTF("Você já votou");
                return;
            }

            boolean candidateExists = candidates.stream().anyMatch(c -> c.id == candidateId);
            if (!candidateExists) {
                out.writeUTF("FAILURE");
                out.writeUTF("Candidato inválido");
                return;
            }

            votes.put(candidateId, votes.getOrDefault(candidateId, 0) + 1);
            votedUsers.add(currentUser);
            out.writeUTF("SUCCESS");
            out.writeUTF("Voto registrado com sucesso");
            System.out.println("Voto registrado: " + currentUser + " -> Candidato " + candidateId);
        }

        private void handleAddCandidate() throws IOException {
            String name = in.readUTF();

            if (!admins.contains(currentUser)) {
                out.writeUTF("FAILURE");
                out.writeUTF("Acesso negado");
                return;
            }

            candidates.add(new Candidate(candidateIdCounter++, name));
            out.writeUTF("SUCCESS");
            out.writeUTF("Candidato adicionado");
            System.out.println("Candidato adicionado: " + name);
        }

        private void handleRemoveCandidate() throws IOException {
            int id = in.readInt();

            if (!admins.contains(currentUser)) {
                out.writeUTF("FAILURE");
                out.writeUTF("Acesso negado");
                return;
            }

            boolean removed = candidates.removeIf(c -> c.id == id);
            if (removed) {
                out.writeUTF("SUCCESS");
                out.writeUTF("Candidato removido");
                System.out.println("Candidato removido: " + id);
            } else {
                out.writeUTF("FAILURE");
                out.writeUTF("Candidato não encontrado");
            }
        }

        private void handleBroadcast() throws IOException {
            String message = in.readUTF();

            if (!admins.contains(currentUser)) {
                out.writeUTF("FAILURE");
                out.writeUTF("Acesso negado");
                return;
            }

            sendMulticast(message);
            out.writeUTF("SUCCESS");
            out.writeUTF("Mensagem enviada");
        }

        private void handleGetResults() throws IOException {
            int total = votes.values().stream().mapToInt(Integer::intValue).sum();
            out.writeInt(candidates.size());

            for (Candidate c : candidates) {
                int v = votes.getOrDefault(c.id, 0);
                double pct = total > 0 ? (v * 100.0 / total) : 0;
                out.writeUTF(c.name);
                out.writeInt(v);
                out.writeDouble(pct);
            }

            if (total > 0) {
                Candidate winner = candidates.stream()
                        .max(Comparator.comparingInt(c -> votes.getOrDefault(c.id, 0)))
                        .orElse(null);
                out.writeUTF(winner != null ? winner.name : "Nenhum");
            } else {
                out.writeUTF("Nenhum");
            }
            out.writeInt(total);
        }
    }
}