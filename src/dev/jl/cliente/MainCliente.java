package dev.jl.cliente;

import dev.jl.remote.LojaRemota;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.Scanner;

public class MainCliente {
    public static void main(String[] args) {
        try {
            Registry reg = LocateRegistry.getRegistry("localhost", 1099);
            LojaRemota svc = (LojaRemota) reg.lookup("LojaService");
            System.out.println("Conectado ao serviço LojaService (localhost:1099).");

            Scanner sc = new Scanner(System.in);
            while (true) {
                System.out.println("\nMenu:");
                System.out.println("1 - Listar peças");
                System.out.println("2 - Adicionar calça");
                System.out.println("3 - Buscar por nome");
                System.out.println("0 - Sair");
                System.out.print("> ");
                String opt = sc.nextLine().trim();
                if ("0".equals(opt)) break;

                switch (opt) {
                    case "1": {
                        String req = "objectReference=loja;methodID=listar;args=[]";
                        byte[] resp = svc.processarRequisicao(req.getBytes("UTF-8"));
                        System.out.println("Resposta:\n" + new String(resp, "UTF-8"));
                        break;
                    }
                    case "2": {
                        System.out.print("id (inteiro): ");
                        String id = sc.nextLine().trim();
                        System.out.print("nome: ");
                        String name = sc.nextLine().trim();
                        System.out.print("preco (ex: 120.0): ");
                        String price = sc.nextLine().trim();
                        System.out.print("tamanho: ");
                        String tamanho = sc.nextLine().trim();
                        System.out.print("cor: ");
                        String cor = sc.nextLine().trim();
                        System.out.print("quantidade: ");
                        String qtd = sc.nextLine().trim();

                        String argsStr = String.format("id:%s,name:%s,price:%s,tamanho:%s,cor:%s,quantidade:%s",
                                id, escape(name), price, escape(tamanho), escape(cor), qtd);

                        String req = "objectReference=loja;methodID=adicionar;args=" + argsStr;
                        byte[] resp = svc.processarRequisicao(req.getBytes("UTF-8"));
                        System.out.println("Resposta:\n" + new String(resp, "UTF-8"));
                        break;
                    }
                    case "3": {
                        System.out.print("Nome para buscar: ");
                        String name = sc.nextLine().trim();
                        String argsStr = "name:" + escape(name);
                        String req = "objectReference=loja;methodID=buscar;args=" + argsStr;
                        byte[] resp = svc.processarRequisicao(req.getBytes("UTF-8"));
                        System.out.println("Resposta:\n" + new String(resp, "UTF-8"));
                        break;
                    }
                    default:
                        System.out.println("Opção inválida.");
                }
            }
            sc.close();
            System.out.println("Cliente finalizando.");
        } catch (Exception e) {
            System.err.println("Erro no cliente: " + e);
            e.printStackTrace();
        }
    }

    // pequenas proteções para evitar ; ou , na string que quebraria parsing simples
    private static String escape(String s) {
        return s.replace(";", "").replace(",", "").replace(":", "");
    }
}
