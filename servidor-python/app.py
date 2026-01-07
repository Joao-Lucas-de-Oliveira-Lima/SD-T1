from flask import Flask, jsonify, request, render_template_string

app = Flask(__name__)

# =========================
# MODELS
# =========================

class PecaRoupa:
    def __init__(self, id, nome, preco, tamanho, cor, quantidade):
        self.id = id
        self.nome = nome
        self.preco = preco
        self.tamanho = tamanho
        self.cor = cor
        self.quantidade = quantidade

    def to_dict(self):
        return {
            "id": self.id,
            "nome": self.nome,
            "preco": self.preco,
            "tamanho": self.tamanho,
            "cor": self.cor,
            "quantidadeEstoque": self.quantidade
        }


class Loja:
    def __init__(self):
        self.estoque = []

    def adicionar(self, peca):
        self.estoque.append(peca)

    def listar(self):
        return self.estoque

    def buscar_por_id(self, id):
        for p in self.estoque:
            if p.id == id:
                return p
        return None


# =========================
# DADOS INICIAIS
# =========================

loja = Loja()
loja.adicionar(PecaRoupa(1, "Calça Jeans", 120.0, "M", "Azul", 5))
loja.adicionar(PecaRoupa(2, "Calça Social Preta", 150.0, "G", "Preto", 3))


# =========================
# API REST
# =========================

@app.route("/pecas", methods=["GET", "POST"])
def pecas():
    # LISTAR PEÇAS
    if request.method == "GET":
        return jsonify([p.to_dict() for p in loja.listar()])

    # ADICIONAR PEÇA
    try:
        dados = request.get_json(force=True)

        nova = PecaRoupa(
            id=int(dados["id"]),
            nome=str(dados["nome"]),
            preco=float(dados["preco"]),
            tamanho=str(dados["tamanho"]),
            cor=str(dados["cor"]),
            quantidade=int(dados["quantidade"])
        )

        loja.adicionar(nova)

        return jsonify({
            "mensagem": "Peça adicionada com sucesso",
            "peca": nova.to_dict()
        }), 201

    except Exception as e:
        return jsonify({
            "erro": "Dados inválidos",
            "detalhe": str(e)
        }), 400


@app.route("/pecas/<int:id>", methods=["GET"])
def buscar_peca(id):
    peca = loja.buscar_por_id(id)
    if not peca:
        return jsonify({"erro": "Peça não encontrada"}), 404
    return jsonify(peca.to_dict())


@app.route("/comprar", methods=["POST"])
def comprar():
    try:
        dados = request.get_json(force=True)
        id = int(dados["id"])
        quantidade = int(dados["quantidade"])

        peca = loja.buscar_por_id(id)
        if not peca:
            return jsonify({"erro": "Peça não encontrada"}), 404

        if peca.quantidade < quantidade:
            return jsonify({"erro": "Estoque insuficiente"}), 400

        peca.quantidade -= quantidade

        return jsonify({
            "mensagem": "Compra realizada com sucesso",
            "restante": peca.quantidade
        })

    except Exception as e:
        return jsonify({
            "erro": "Erro ao processar compra",
            "detalhe": str(e)
        }), 400


# =========================
# INTERFACE WEB (NAVEGADOR)
# =========================

@app.route("/")
def home():
    html = """
    <!DOCTYPE html>
    <html>
    <head>
        <title>Loja - Sistemas Distribuídos</title>
        <style>
            body { font-family: Arial; margin: 40px; }
            table { border-collapse: collapse; width: 100%; }
            th, td { border: 1px solid #ccc; padding: 8px; text-align: center; }
            th { background-color: #eee; }
        </style>
    </head>
    <body>
        <h1>Estoque da Loja</h1>
        <table>
            <tr>
                <th>ID</th>
                <th>Nome</th>
                <th>Preço</th>
                <th>Tamanho</th>
                <th>Cor</th>
                <th>Quantidade</th>
            </tr>
            {% for p in pecas %}
            <tr>
                <td>{{ p.id }}</td>
                <td>{{ p.nome }}</td>
                <td>R$ {{ "%.2f"|format(p.preco) }}</td>
                <td>{{ p.tamanho }}</td>
                <td>{{ p.cor }}</td>
                <td>{{ p.quantidade }}</td>
            </tr>
            {% endfor %}
        </table>
    </body>
    </html>
    """
    return render_template_string(html, pecas=loja.listar())


# =========================
# MAIN
# =========================

if __name__ == "__main__":
    app.run(port=5000, debug=True)
