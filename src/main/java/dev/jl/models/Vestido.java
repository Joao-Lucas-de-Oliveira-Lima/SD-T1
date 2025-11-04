package dev.jl.models;

import java.io.Serializable;

public class Vestido extends PecaRoupa implements Serializable {

    public Vestido(int id, String nome, double preco, String tamanho, String cor, int quantidadeEstoque) {
        super(id, nome, preco, tamanho, cor, quantidadeEstoque);
    }
}
