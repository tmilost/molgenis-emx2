<script setup lang="ts">
import type {
  InputString,
  InputTextArea,
  InputRef,
  InputPlaceHolder,
} from "#build/components";
import type {
  columnId,
  columnValue,
  CellValueType,
  columnValueObject,
} from "../../../metadata-utils/src/types";

type inputComponent =
  | InstanceType<typeof InputString>
  | InstanceType<typeof InputTextArea>
  | InstanceType<typeof InputRef>
  | InstanceType<typeof InputPlaceHolder>;

defineProps<{
  type: CellValueType;
  id: columnId;
  label: string;
  required: boolean;
  data: columnValue;
  refSchemaId?: string;
  refTableId?: string;
  refLabel?: string;
}>();

defineEmits(["focus", "error", "update:modelValue"]);
defineExpose({ validate });

const input = ref<inputComponent>();

function validate(value: columnValue) {
  if (!input.value) {
    throw new Error("FormFieldInput is not found in dom");
  }
  if (!input.value.validate) {
    throw new Error("FormFieldInput Component is missing validate method");
  }

  input.value.validate(value);
}
</script>

<template>
  <LazyInputString
    v-if="type === 'STRING'"
    ref="input"
    :id="id"
    :label="label"
    :required="required"
    :value="data as string"
    @focus="$emit('focus')"
    @update:modelValue="$emit('update:modelValue', $event)"
    @error="$emit('error', $event)"
  />
  <LazyInputTextArea
    v-else-if="type === 'TEXT'"
    ref="input"
    :id="id"
    :label="label"
    :required="required"
    :value="data as string"
    @focus="$emit('focus')"
    @update:modelValue="$emit('update:modelValue', $event)"
    @error="$emit('error', $event)"
  />
  <LazyInputHyperlink
    v-else-if="type === 'HYPERLINK'"
    ref="input"
    :id="id"
    :label="label"
    :required="required"
    :value="data as string"
    @focus="$emit('focus')"
    @update:modelValue="$emit('update:modelValue', $event)"
    @error="$emit('error', $event)"
  />
  <LazyInputBoolean
    v-else-if="type === 'BOOL'"
    ref="input"
    :id="id"
    :label="label"
    :required="required"
    :modelValue="data === true || data === false ? data : null"
    @focus="$emit('focus')"
    @update:modelValue="$emit('update:modelValue', $event)"
    @error="$emit('error', $event)"
  />
  <LazyInputRef
    v-else-if="type === 'REF_ARRAY' || type === 'REF'"
    :id="id"
    ref="input"
    :modelValue="data as columnValueObject | columnValueObject[]"
    :refSchemaId="refSchemaId as string"
    :refTableId="refTableId as string"
    :refLabel="refLabel as string"
    :isArray="type === 'REF_ARRAY'"
    @update:modelValue="$emit('update:modelValue', $event)"
  >
  </LazyInputRef>
  <LazyInputPlaceHolder v-else ref="input" :type="type" />
</template>
