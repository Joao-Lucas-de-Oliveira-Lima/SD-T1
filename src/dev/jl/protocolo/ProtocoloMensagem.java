package dev.jl.protocolo;

import dev.jl.models.*;
import java.util.HashMap;
import java.util.Map;

/**
 * Protocolo simples:
 * Requisição (string):
 *   objectReference=loja;methodID=listar;args=[]
 *   objectReference=loja;methodID=adicionar;args=id:1,name:Calca Azul,price:120.0,tamanho:M,cor:Azul,quantidade:10
 *   objectReference=loja;methodID=buscar;args=name:Calca Azul
 *
 * Resposta: texto simples (mensagem human-readable)
 */

public class ProtocoloMensagem {

    public static byte[] processar(byte[] mensagemBytes, Loja loja) {
        try {
            String msg = new String(mensagemBytes, "UTF-8").trim();
            Map<String, String> mapa = parsePairs(msg);

            String objectRef = mapa.getOrDefault("objectReference", "loja");
            String methodId = mapa.get("methodID");
            String args = mapa.getOrDefault("args", "");

            if (!"loja".equalsIgnoreCase(objectRef)) {
                return ("ERROR: objectReference desconhecido: " + objectRef).getBytes("UTF-8");
            }
            if (methodId == null) {
                return "ERROR: methodID ausente".getBytes("UTF-8");
            }

            switch (methodId) {
                case "listar":
                    return loja.listarComoTexto().getBytes("UTF-8");

                case "adicionar":
                    Map<String, String> amap = parseArgList(args);
                    // Esperamos chaves: id,name,price,tamanho,cor,quantidade
                    String idS = amap.get("id");
                    String name = amap.get("name");
                    String priceS = amap.get("price");
                    String tamanho = amap.get("tamanho");
                    String cor = amap.get("cor");
                    String qtdS = amap.get("quantidade");
                    if (idS == null || name == null) {
                        return "ERROR: argumentos insuficientes (id e name são obrigatórios)".getBytes("UTF-8");
                    }
                    int id = safeParseInt(idS, -1);
                    double price = safeParseDouble(priceS, 0.0);
                    int quantidade = safeParseInt(qtdS, 0);

                    // Criar Calca (opção A: só Calca)
                    Calca c = new Calca(id, name, price, tamanho==null? "U":tamanho, cor==null? "N/A":cor, quantidade);
                    loja.adicionarPeca(c);
                    return ("OK: peça adicionada -> " + c.toString()).getBytes("UTF-8");

                case "buscar":
                    Map<String, String> amap2 = parseArgList(args);
                    String qname = amap2.get("name");
                    String idq = amap2.get("id");
                    if (qname != null) {
                        PecaRoupa found = loja.buscarPorNome(qname);
                        if (found == null) return ("OK: não encontrada peça com nome \"" + qname + "\"").getBytes("UTF-8");
                        return ("OK: encontrada -> " + found.toString()).getBytes("UTF-8");
                    } else if (idq != null) {
                        int iid = safeParseInt(idq, -1);
                        PecaRoupa found = loja.buscarPorId(iid);
                        if (found == null) return ("OK: não encontrada peça com id " + idq).getBytes("UTF-8");
                        return ("OK: encontrada -> " + found.toString()).getBytes("UTF-8");
                    } else {
                        return "ERROR: fornecer name ou id em args para buscar".getBytes("UTF-8");
                    }

                default:
                    return ("ERROR: methodID desconhecido: " + methodId).getBytes("UTF-8");
            }
        } catch (Exception e) {
            return ("ERROR: exceção no servidor: " + e.toString()).getBytes();
        }
    }

    private static Map<String,String> parsePairs(String s) {
        Map<String,String> out = new HashMap<>();
        String[] parts = s.split(";");
        for (String p : parts) {
            String[] kv = p.split("=", 2);
            if (kv.length == 2) {
                out.put(kv[0].trim(), kv[1].trim());
            }
        }
        return out;
    }

    private static Map<String,String> parseArgList(String s) {
        Map<String,String> out = new HashMap<>();
        if (s == null) return out;
        String clean = s.trim();
        if (clean.startsWith("[") && clean.endsWith("]")) {
            clean = clean.substring(1, clean.length()-1).trim();
        }
        if (clean.isEmpty()) return out;
        String[] items = clean.split(",");
        for (String item : items) {
            String[] kv = item.split(":", 2);
            if (kv.length == 2) {
                out.put(kv[0].trim(), kv[1].trim());
            }
        }
        return out;
    }

    private static int safeParseInt(String s, int def) {
        if (s == null) return def;
        try { return Integer.parseInt(s); } catch (NumberFormatException e) { return def; }
    }

    private static double safeParseDouble(String s, double def) {
        if (s == null) return def;
        try { return Double.parseDouble(s); } catch (NumberFormatException e) { return def; }
    }
}
