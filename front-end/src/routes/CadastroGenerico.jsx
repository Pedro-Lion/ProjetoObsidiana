import { useParams } from "react-router-dom"

export function CadastroGenerico() {
  const { item } = useParams()

  return (
    <h1 className="text-2xl">Cadastrar {item}</h1>
  )
}