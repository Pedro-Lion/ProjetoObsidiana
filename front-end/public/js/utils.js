const urlBase = "http://localhost:3000/usuarios"

async function fazerRequisicao(metodo = "", corpo = {}) {
  if (metodo == "") metodo = "GET"
  else metodo = metodo.toUpperCase();

  try {
    const request = await fetch(urlBase, {
      method: metodo,
      headers: {
        "Content-Type": "application/json"
      },
      body: JSON.stringify(corpo)
    });

    const dados = await request.json();

    return dados;
  } catch (error) {
    console.log(error);
  }
}

function alertarErro(dados = {}) {
  if(campoVazio(dados)) {
    alert("Preencha todos os campos.");
    return true;
  }

  if(!emailValido(dados.email)) {
    alert("O email digitado é inválido.");
    return true;
  }

  if(!senhasCoincidem(dados.senha, dados.confirmarSenha)) {
    alert("As senhas não coincidem.");
    return true;
  }
}

function campoVazio(campos = {}) {
  campos = Object.values(campos);
  
  for (let i = 0; i < campos.length; i++) {
    if (campos[i].trim() == "") return true;
  }

  return false;
}

function emailValido(email = "") {
  return email.includes("@");
}

function senhasCoincidem(senha = "", confirmarSenha = "") {
  return senha == confirmarSenha;
}

export { fazerRequisicao, alertarErro }