package dev.jl.dto;

import dev.jl.models.PecaRoupa;

import java.io.Serializable;
import java.util.List;

class VendaResponse implements Serializable {
    private static final long serialVersionUID = 1L;

    private boolean sucesso;
    private String mensagem;
    private List<PecaRoupa> pecas;
    private PecaRoupa peca;
    private double valor;

    public VendaResponse() {}

    public VendaResponse(boolean sucesso, String mensagem) {
        this.sucesso = sucesso;
        this.mensagem = mensagem;
    }

    // Getters e Setters
    public boolean isSucesso() { return sucesso; }
    public void setSucesso(boolean sucesso) { this.sucesso = sucesso; }

    public String getMensagem() { return mensagem; }
    public void setMensagem(String mensagem) { this.mensagem = mensagem; }

    public List<PecaRoupa> getPecas() { return pecas; }
    public void setPecas(List<PecaRoupa> pecas) { this.pecas = pecas; }

    public PecaRoupa getPeca() { return peca; }
    public void setPeca(PecaRoupa peca) { this.peca = peca; }

    public double getValor() { return valor; }
    public void setValor(double valor) { this.valor = valor; }
}