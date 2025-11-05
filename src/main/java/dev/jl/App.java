package dev.jl;

import dev.jl.inputstream.PecaRoupaInputStream;
import dev.jl.models.Calca;
import dev.jl.models.Camiseta;
import dev.jl.models.PecaRoupa;
import dev.jl.models.Vestido;
import dev.jl.outputstream.PecaRoupaOutputStream;

import java.io.*;
import java.util.List;

public class App {

    public static void main(String[] args) {
        // Cria dados de teste
        PecaRoupa[] pecas = criarPecasTeste();

        // Define número de bytes para cada atributo
        int[] bytesAtributos = {4, 100, 8};

        System.out.println("=== TESTE DE STREAMS ===\n");

        // (System.out)
        System.out.println("1. Teste com System.out:");
        testeSystemOut(pecas, bytesAtributos);

        //FileOutputStream
        System.out.println("\n2. Teste com Arquivo:");
        testeArquivo(pecas, bytesAtributos);
    }

    private static PecaRoupa[] criarPecasTeste() {
        return new PecaRoupa[] {
                new Camiseta(1, "Camiseta Básica", 49.90, "M", "Azul", 10),
                new Calca(2, "Calça Jeans", 159.90, "42", "Azul", 5),
                new Vestido(3, "Vestido Floral", 189.90, "P", "Rosa", 3)
        };
    }

    private static void testeSystemOut(PecaRoupa[] pecas, int[] bytesAtributos) {
        try {
            System.out.println("   Enviando dados para System.out (binário)...");
            PecaRoupaOutputStream out = new PecaRoupaOutputStream(pecas, 3, bytesAtributos, System.out);
            out.enviarPecas();
            out.flush();
            System.out.println("\n   Dados enviados com sucesso!");
        } catch (IOException e) {
            System.err.println("   Erro: " + e.getMessage());
        }
    }


    private static void testeArquivo(PecaRoupa[] pecas, int[] bytesAtributos) {
        String arquivo = "pecas_roupa.dat";

        try {
            // Escrita no arquivo
            System.out.println("   Escrevendo no arquivo: " + arquivo);
            FileOutputStream fos = new FileOutputStream(arquivo);
            PecaRoupaOutputStream out = new PecaRoupaOutputStream(pecas, 3, bytesAtributos, fos);
            out.enviarPecas();
            out.close();
            System.out.println("   Dados escritos com sucesso!");

            // Leitura do arquivo
            System.out.println("\n   Lendo do arquivo: " + arquivo);
            FileInputStream fis = new FileInputStream(arquivo);
            PecaRoupaInputStream in = new PecaRoupaInputStream(fis);
            List<PecaRoupa> pecasLidas = in.lerPecas();
            in.close();

            System.out.println("   Dados lidos com sucesso!\n");
            for (PecaRoupa peca : pecasLidas) {
                System.out.println("   - " + peca);
            }

        } catch (IOException e) {
            System.err.println("   Erro: " + e.getMessage());
            e.printStackTrace();
        }
    }
}