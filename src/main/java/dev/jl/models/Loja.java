package dev.jl.models;

import java.util.ArrayList;
import java.util.List;
import java.io.Serializable;

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
}