package dev.jl.servidor;

import dev.jl.remote.LojaRemota;
import dev.jl.models.Loja;
import dev.jl.models.Calca;
import dev.jl.protocolo.ProtocoloMensagem;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

public class LojaRemotaImpl extends UnicastRemoteObject implements LojaRemota {

    private static final long serialVersionUID = 1L;
    private final Loja loja;

    protected LojaRemotaImpl() throws RemoteException {
        super();
        this.loja = new Loja();
        // roupas iniciais (exemplos)
        loja.adicionarPeca(new Calca(1, "Calça Jeans", 120.00, "M", "Azul", 5));
        loja.adicionarPeca(new Calca(2, "Calça Social Preta", 150.00, "G", "Preto", 3));
    }

    @Override
    public byte[] processarRequisicao(byte[] mensagem) throws RemoteException {
        return ProtocoloMensagem.processar(mensagem, loja);
    }
}
