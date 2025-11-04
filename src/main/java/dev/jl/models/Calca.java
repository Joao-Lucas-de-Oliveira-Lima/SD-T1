package dev.jl.models;

import java.io.Serializable;

public class Calca extends PecaRoupa implements Serializable {
    public Calca(int id, String nome, double preco, String tamanho, String cor, int quantidadeEstoque) {
        super(id, nome, preco, tamanho, cor, quantidadeEstoque);
    }
}
