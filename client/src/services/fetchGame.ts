import type { Game } from '@/models/Game.ts'

export async function fetchGame(category :number, tags: number[]): Promise<Game> {
  const res = await fetch("http://localhost:8080/game", {
    method: "POST",
    headers: { "Content-Type": "application/json" },
    body: JSON.stringify({ Category: category, Tags: tags })
  })

  if (!res.ok) throw new Error(`Server error: ${res.status}`)

  return await res.json() as Promise<Game>
}
