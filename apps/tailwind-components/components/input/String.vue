<script setup lang="ts">
import type { columnValue } from "../../../metadata-utils/src/types";

const props = withDefaults(
  defineProps<{
    id: string;
    label?: string;
    modelValue?: string;
    placeholder?: string;
    disabled?: boolean;
    required?: boolean;
    valid?: boolean;
    hasError?: boolean;
  }>(),
  {
    disabled: false,
    required: false,
    hasError: false,
    valid: false,
  }
);

const emit = defineEmits(["focus", "blur", "error", "update:modelValue"]);
defineExpose({ validate });

function validate(value: columnValue) {
  if (props.required && value === "") {
    const errors = [
      { message: `${props.label || props.id} required to complete the form` },
    ];
    emit("error", errors);
    return errors;
  } else {
    emit("error", []);
    return [];
  }
}

function onInput(event: Event) {
  const inputElement = event.target as HTMLInputElement;
  emit("update:modelValue", inputElement.value);
  validate(inputElement.value);
}
</script>

<template>
  <input
    :id="id"
    :required="required"
    :placeholder="placeholder"
    :disabled="disabled"
    class="w-full h-[56px] pr-4 font-sans text-black text-gray-300 outline-none ring-red-500 pl-3 shadow-search-input focus:shadow-search-input hover:shadow-search-input search-input-mobile border border-solid rounded-input"
    :class="{
      'border-invalid text-invalid': hasError,
      'border-valid text-valid': valid,
      'border-disabled text-disabled bg-disabled': disabled,
      'bg-white': !disabled,
    }"
    :value="modelValue"
    @input="onInput"
    @focus="$emit('focus')"
    @blur="$emit('blur')"
  />
</template>
