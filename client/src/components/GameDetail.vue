<script setup lang="ts">
import type { Card } from '@/models/Card'
import { ref, computed, onUnmounted, onMounted } from 'vue'

const props = defineProps<{
  category: number
  cards: Card[]
  active: boolean
}>()

const cards = ref<Card[]>([...props.cards])
const seeSolution = ref(false)

const emptyCard = (): Card => ({
  PlaceId: 0,
  Front: '',
  Back: '',
  InfoFront: '',
  InfoBack: '',
})

const firstCard = computed<Card>(() => {
  return cards.value.length > 0 ? cards.value[0] : emptyCard()
})

const deckNotEmpty = computed(() => {
  return cards.value.length > 0
})

function guess(correct: boolean) {
  seeSolution.value = false
  if (cards.value.length > 0) {
    const first = cards.value.shift()
    if (!correct && first) {
      cards.value.push(first)
    }
  }
}

function handleKeydown(event: KeyboardEvent) {
  if (event.code === 'Space') {
    event.preventDefault()
    if (!seeSolution.value) {
      seeSolution.value = true
    } else {
      guess(true)
    }
  } else if (event.code === 'Digit2' && seeSolution.value) {
    guess(false)
  }
}

onMounted(() => {
  window.addEventListener('keydown', handleKeydown)
})

onUnmounted(() => {
  window.removeEventListener('keydown', handleKeydown)
})
</script>

<template>
  <h1>Game Detail Category {{ category }}}</h1>
  >

  <div v-if="deckNotEmpty">
    <img
      v-if="category == 0"
      :src="`/media/maps/${firstCard.Front}`"
      alt="`map of ${firstCard.Back}`"
    />
    <img
      v-if="category == 1"
      :src="`/media/flags/${firstCard.Front}`"
      alt="`map of ${firstCard.Back}`"
    />
    <p v-if="category == 2 || category == 3">{{ firstCard.Front }}</p>
    <p v-if="seeSolution">{{ firstCard.Back }}</p>
    <br />
    <button @click="seeSolution = !seeSolution">Toggle Card</button>
    <button v-if="seeSolution" @click="guess(true)">Guessed Right</button>
    <button v-if="seeSolution" @click="guess(false)">Guessed Wrong</button>
  </div>
  <div v-else>
    <p>You finished all your cards</p>
  </div>
</template>

<style scoped></style>
