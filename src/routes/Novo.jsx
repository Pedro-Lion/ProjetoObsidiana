import { useParams } from "react-router-dom"

export function Novo() {
  const { item } = useParams()

  return (
    <h1 className="text-2xl">Novo {item}</h1>
  )
}