package dev.jl.dto;

import dev.jl.models.PecaRoupa;

import java.util.List;

public interface Vendas {
    // Operações de consulta
    List<PecaRoupa> listarPecas();
    PecaRoupa buscarPeca(int id);
    List<PecaRoupa> buscarPorTipo(String tipo);

    // Operações de estoque
    boolean adicionarPeca(PecaRoupa peca);
    boolean removerPeca(int id);
    boolean atualizarEstoque(int id, int quantidade);

    // Operações de venda
    boolean realizarVenda(int idPeca, int quantidade);
    double calcularTotal(int[] idsPecas, int[] quantidades);
}