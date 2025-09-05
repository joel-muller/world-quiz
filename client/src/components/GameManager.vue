<script setup lang="ts">
import { computed, ref } from "vue"
import type { Game } from '@/models/Game.ts'
import { fetchGame } from '@/services/fetchGame.ts'
import type { Card } from '@/models/Card.ts'
import GameDetail from '@/components/GameDetail.vue'

const cards = ref<Card[]>([])
const gameId = ref(-1)
const category = ref(0)
const tags = ref([7])
const loading = ref(false)
const error = ref<string | null>(null)
const active = computed(() => cards.value.length > 0)

async function startGame() {
  loading.value = true
  error.value = null
  try {
    const game = await fetchGame(category.value, tags.value) as Game
    cards.value = game.Cards
    gameId.value = game.Id
    // TODO here see of category is the same as expected
    category.value = game.Category
  } catch (err: unknown) {
    if (err instanceof Error) error.value = err.message
    else error.value = String(err)
  } finally {
    loading.value = false
  }
}

</script>
<template>
  <div v-if="active">
    <h1>You are in a game currently</h1>
    <GameDetail :category=category :cards="cards" :active="active"/>
  </div>
  <div v-else>
    <h1>start a game</h1>
    <button @click="startGame" :disabled="loading">
      {{ loading ? 'Loading...' : 'Start Game' }}
    </button>
    <p v-if="error" class="error">{{ error }}</p>
  </div>


</template>

<style scoped></style>
