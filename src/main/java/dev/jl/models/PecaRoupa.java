package dev.jl.models;

import java.io.Serializable;

/**
 * Superclasse que representa uma peça de roupa
 */
public class PecaRoupa implements Serializable {
    private static final long serialVersionUID = 1L;

    private int id;
    private String nome;
    private double preco;
    private String tamanho;
    private String cor;
    private int quantidadeEstoque;

    public PecaRoupa() {}

    public PecaRoupa(int id, String nome, double preco, String tamanho, String cor, int quantidadeEstoque) {
        this.id = id;
        this.nome = nome;
        this.preco = preco;
        this.tamanho = tamanho;
        this.cor = cor;
        this.quantidadeEstoque = quantidadeEstoque;
    }

    // Getters e Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }

    public double getPreco() { return preco; }
    public void setPreco(double preco) { this.preco = preco; }

    public String getTamanho() { return tamanho; }
    public void setTamanho(String tamanho) { this.tamanho = tamanho; }

    public String getCor() { return cor; }
    public void setCor(String cor) { this.cor = cor; }

    public int getQuantidadeEstoque() { return quantidadeEstoque; }
    public void setQuantidadeEstoque(int quantidadeEstoque) { this.quantidadeEstoque = quantidadeEstoque; }

    @Override
    public String toString() {
        return String.format("ID: %d | Nome: %s | Preço: R$ %.2f | Tamanho: %s | Cor: %s | Estoque: %d",
                id, nome, preco, tamanho, cor, quantidadeEstoque);
    }
}