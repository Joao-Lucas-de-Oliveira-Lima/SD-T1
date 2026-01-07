const BASE_URL = "http://localhost:5000";
const readline = require("readline");

const rl = readline.createInterface({
    input: process.stdin,
    output: process.stdout
});

// =======================
// MENU
// =======================

function exibirMenu() {
    console.log("\n===== MENU LOJA =====");
    console.log("1 - Listar peças");
    console.log("2 - Buscar peça por ID");
    console.log("3 - Comprar peça");
    console.log("4 - Adicionar nova peça");
    console.log("0 - Sair");
}

function perguntar(pergunta) {
    return new Promise(resolve => {
        rl.question(pergunta, resposta => resolve(resposta.trim()));
    });
}

// =======================
// AÇÕES
// =======================

async function listarPecas() {
    const res = await fetch(`${BASE_URL}/pecas`);
    const dados = await res.json();
    console.log("\n=== PEÇAS ===");
    console.log(dados);
}

async function buscarPecaPorId() {
    const id = await perguntar("Digite o ID da peça: ");
    const res = await fetch(`${BASE_URL}/pecas/${id}`);
    const dados = await res.json();
    console.log("\n=== PEÇA ===");
    console.log(dados);
}

async function comprarPeca() {
    const id = await perguntar("Digite o ID da peça: ");
    const quantidade = await perguntar("Digite a quantidade: ");

    const res = await fetch(`${BASE_URL}/comprar`, {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({
            id: Number(id),
            quantidade: Number(quantidade)
        })
    });

    const dados = await res.json();
    console.log("\n=== COMPRA ===");
    console.log(dados);
}

async function adicionarPeca() {
    const id = await perguntar("ID da peça: ");
    const nome = await perguntar("Nome: ");
    const preco = await perguntar("Preço: ");
    const tamanho = await perguntar("Tamanho: ");
    const cor = await perguntar("Cor: ");
    const quantidade = await perguntar("Quantidade em estoque: ");

    const json = {
        id: Number(id),
        nome: nome,
        preco: Number(preco),
        tamanho: tamanho,
        cor: cor,
        quantidade: Number(quantidade)
    };

    console.log("\nJSON enviado ao servidor:");
    console.log(json);

    const res = await fetch(`${BASE_URL}/pecas`, {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify(json)
    });

    const dados = await res.json();
    console.log("\n=== RESULTADO ===");
    console.log(dados);
}

// =======================
// LOOP PRINCIPAL
// =======================

async function main() {
    let opcao = "-1";

    while (opcao !== "0") {
        exibirMenu();
        opcao = await perguntar("Escolha uma opção: ");

        try {
            switch (opcao) {
                case "1":
                    await listarPecas();
                    break;
                case "2":
                    await buscarPecaPorId();
                    break;
                case "3":
                    await comprarPeca();
                    break;
                case "4":
                    await adicionarPeca();
                    break;
                case "0":
                    console.log("Encerrando cliente...");
                    rl.close();
                    break;
                default:
                    console.log("Opção inválida.");
            }
        } catch (err) {
            console.log("Erro ao comunicar com o servidor.");
            console.error(err.message);
        }
    }
}

main();
