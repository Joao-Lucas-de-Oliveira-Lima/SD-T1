import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Scanner;

public class MainCliente {

    private static final String BASE_URL = "http://localhost:5000";
    private static final Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) {

        int opcao = -1;

        while (opcao != 0) {
            exibirMenu();
            opcao = lerInt("Escolha uma opção: ");

            try {
                switch (opcao) {
                    case 1:
                        listarPecas();
                        break;
                    case 2:
                        buscarPecaPorId();
                        break;
                    case 3:
                        comprarPeca();
                        break;
                    case 4:
                        adicionarPeca();
                        break;
                    case 0:
                        System.out.println("Encerrando cliente...");
                        break;
                    default:
                        System.out.println("Opção inválida.");
                }
            } catch (Exception e) {
                System.out.println("Erro na comunicação com o servidor.");
                e.printStackTrace();
            }
        }
    }

    // =======================
    // MENU
    // =======================

    private static void exibirMenu() {
        System.out.println("\n===== MENU LOJA =====");
        System.out.println("1 - Listar peças");
        System.out.println("2 - Buscar peça por ID");
        System.out.println("3 - Comprar peça");
        System.out.println("4 - Adicionar nova peça");
        System.out.println("0 - Sair");
    }

    // =======================
    // LEITURA SEGURA
    // =======================

    private static int lerInt(String msg) {
        System.out.print(msg);
        int valor = scanner.nextInt();
        scanner.nextLine(); // limpa buffer
        return valor;
    }

    private static String lerString(String msg) {
        System.out.print(msg);
        return scanner.nextLine();
    }

    // =======================
    // AÇÕES
    // =======================

    private static void listarPecas() throws Exception {
        requisicaoGET("/pecas");
    }

    private static void buscarPecaPorId() throws Exception {
        int id = lerInt("Digite o ID da peça: ");
        requisicaoGET("/pecas/" + id);
    }

    private static void comprarPeca() throws Exception {
        int id = lerInt("Digite o ID da peça: ");
        int qtd = lerInt("Digite a quantidade: ");

        String json = "{"
                + "\"id\":" + id + ","
                + "\"quantidade\":" + qtd
                + "}";

        requisicaoPOST("/comprar", json);
    }

    private static void adicionarPeca() throws Exception {
        int id = lerInt("ID da peça: ");
        String nome = lerString("Nome: ");
        double preco = Double.parseDouble(lerString("Preço: "));
        String tamanho = lerString("Tamanho: ");
        String cor = lerString("Cor: ");
        int quantidade = lerInt("Quantidade em estoque: ");

        String json = "{"
                + "\"id\":" + id + ","
                + "\"nome\":\"" + nome + "\","
                + "\"preco\":" + preco + ","
                + "\"tamanho\":\"" + tamanho + "\","
                + "\"cor\":\"" + cor + "\","
                + "\"quantidade\":" + quantidade
                + "}";

        System.out.println("\nJSON enviado ao servidor:");
        System.out.println(json);

        requisicaoPOST("/pecas", json);
    }

    // =======================
    // HTTP
    // =======================

    private static void requisicaoGET(String endpoint) throws Exception {
        URL url = new URL(BASE_URL + endpoint);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");

        BufferedReader in = new BufferedReader(
                new InputStreamReader(conn.getInputStream())
        );

        String linha;
        while ((linha = in.readLine()) != null) {
            System.out.println(linha);
        }
        in.close();
    }

    private static void requisicaoPOST(String endpoint, String json) throws Exception {
        URL url = new URL(BASE_URL + endpoint);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();

        conn.setRequestMethod("POST");
        conn.setRequestProperty("Content-Type", "application/json");
        conn.setDoOutput(true);

        OutputStream os = conn.getOutputStream();
        os.write(json.getBytes());
        os.flush();
        os.close();

        BufferedReader in;

        if (conn.getResponseCode() >= 400) {
            in = new BufferedReader(new InputStreamReader(conn.getErrorStream()));
        } else {
            in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
        }

        String linha;
        while ((linha = in.readLine()) != null) {
            System.out.println(linha);
        }
        in.close();
    }
}
