package dev.jl.remote;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface LojaRemota extends Remote {
    /**
     * processa requisição em byte[] (mensagem textual no protocolo simples) e retorna resposta em byte[]
     */
    byte[] processarRequisicao(byte[] mensagem) throws RemoteException;
}
