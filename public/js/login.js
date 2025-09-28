import * as u from "./utils.js";

document.getElementById("btn_cadastrar").addEventListener("click", (e) => {
  e.preventDefault();
  cadastrar();
})

function cadastrar() {
  const nome = document.getElementById("nome").value;
  const email = document.getElementById("email").value;
  const senha = document.getElementById("senha").value;
  const confirmarSenha = document.getElementById("confirmar_senha").value;

  if(u.alertarErro({nome, email, senha, confirmarSenha})) return;

  u.fazerRequisicao("POST", {
    nome, email, senha
  });

  alert("Cadastro realizado com sucesso!");
}