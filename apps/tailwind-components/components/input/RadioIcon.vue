<template>
  <svg
    height="20"
    width="20"
    view-box="0 0 20 20"
    xmlns="http://www.w3.org/2000/svg"
    class="mt-1.5"
  >
    <circle
      cx="10"
      cy="10"
      r="9"
      stroke-width="1"
      fill="none"
      class="stroke-current"
      :class="{
        'fill-input hover:fill-input-checked hover:stroke-none focus:fill-input-checked focus:stroke-none':
          !checked,
        'fill-input-checked stroke-none': checked,
      }"
    />
    <circle
      cx="10"
      cy="10"
      r="3"
      stroke-width="1"
      stroke="none"
      class="fill-transparent"
      :class="{
        '!fill-gray-900': checked,
      }"
    />
  </svg>
</template>

<script lang="ts" setup>
defineProps<{
  checked?: boolean;
}>();
</script>
