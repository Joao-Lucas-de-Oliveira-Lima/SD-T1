package dev.jl.models;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Loja implements Serializable {
    private static final long serialVersionUID = 1L;
    private List<PecaRoupa> estoque;

    public Loja() {
        this.estoque = new ArrayList<>();
    }

    public void adicionarPeca(PecaRoupa peca) {
        this.estoque.add(peca);
    }

    public List<PecaRoupa> getEstoque() {
        return estoque;
    }

    public PecaRoupa buscarPorId(int id) {
        return estoque.stream()
                .filter(p -> p.getId() == id)
                .findFirst()
                .orElse(null);
    }

    public PecaRoupa buscarPorNome(String nome) {
        return estoque.stream()
                .filter(p -> p.getNome().equalsIgnoreCase(nome))
                .findFirst()
                .orElse(null);
    }

    public String listarComoTexto() {
        if (estoque.isEmpty()) return "Nenhuma peça cadastrada.";
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < estoque.size(); i++) {
            sb.append("[").append(i+1).append("] ").append(estoque.get(i).toString()).append("\n");
        }
        return sb.toString();
    }
}
