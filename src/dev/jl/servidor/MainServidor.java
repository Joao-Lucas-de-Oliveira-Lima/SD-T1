package dev.jl.servidor;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public class MainServidor {
    public static void main(String[] args) {
        try {
            System.out.println("Criando registry RMI (porta 1099)...");
            Registry reg = LocateRegistry.createRegistry(1099);

            LojaRemotaImpl impl = new LojaRemotaImpl();
            reg.rebind("LojaService", impl);
            System.out.println("Servidor pronto. Serviço 'LojaService' registrado.");
            System.out.println("Aguardando requisições...");
        } catch (Exception e) {
            System.err.println("Erro ao iniciar servidor: " + e);
            e.printStackTrace();
        }
    }
}
