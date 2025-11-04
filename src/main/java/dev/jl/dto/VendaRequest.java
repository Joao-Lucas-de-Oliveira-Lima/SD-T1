package dev.jl.dto;

import dev.jl.models.PecaRoupa;

import java.io.Serializable;

class VendaRequest implements Serializable {
    private static final long serialVersionUID = 1L;

    public enum TipoOperacao {
        LISTAR_PECAS,
        BUSCAR_PECA,
        BUSCAR_POR_TIPO,
        ADICIONAR_PECA,
        REMOVER_PECA,
        ATUALIZAR_ESTOQUE,
        REALIZAR_VENDA,
        CALCULAR_TOTAL
    }

    private TipoOperacao operacao;
    private int id;
    private String tipo;
    private PecaRoupa peca;
    private int quantidade;
    private int[] ids;
    private int[] quantidades;

    public VendaRequest(TipoOperacao operacao) {
        this.operacao = operacao;
    }

    // Getters e Setters
    public TipoOperacao getOperacao() { return operacao; }
    public void setOperacao(TipoOperacao operacao) { this.operacao = operacao; }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getTipo() { return tipo; }
    public void setTipo(String tipo) { this.tipo = tipo; }

    public PecaRoupa getPeca() { return peca; }
    public void setPeca(PecaRoupa peca) { this.peca = peca; }

    public int getQuantidade() { return quantidade; }
    public void setQuantidade(int quantidade) { this.quantidade = quantidade; }

    public int[] getIds() { return ids; }
    public void setIds(int[] ids) { this.ids = ids; }

    public int[] getQuantidades() { return quantidades; }
    public void setQuantidades(int[] quantidades) { this.quantidades = quantidades; }
}