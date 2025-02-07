<script setup lang="ts">
import type { FormFieldInput } from "#build/components";
import type {
  columnValue,
  IColumn,
  IFieldError,
} from "../../../metadata-utils/src/types";

const props = defineProps<{
  schemaId: string;
  column: IColumn;
  data: columnValue;
  errors: IFieldError[];
}>();

defineEmits(["error", "update:modelValue", "blur"]);
defineExpose({ validate, id: props.column.id });

const pristine = ref(true);
const dirty = computed(() => !pristine.value);

const touched = ref(false);
const untouched = computed(() => !touched.value);

const hasError = computed(() => props.errors.length > 0);

const formFieldInput = ref<InstanceType<typeof FormFieldInput>>();

function validate(value: columnValue) {
  if (!formFieldInput.value) {
    throw new Error("FormFieldInput is not found in dom");
  }
  if (!formFieldInput.value.validate) {
    throw new Error("FormFieldInput Component is missing validate method");
  }

  formFieldInput.value.validate(value);
}
</script>

<template>
  <div class="flex flex-col gap-1">
    <div>
      <label :for="column.id" class="capitalize text-title font-bold">{{
        column.label
      }}</label>
      <span class="text-disabled text-body-sm ml-3" v-show="column.required"
        >Required</span
      >
    </div>
    <div class="text-input-description text-body-sm" v-if="column.description">
      {{ column.description }}
    </div>
    <div>
      <FormFieldInput
        :type="column.columnType"
        :id="column.id"
        :label="column.label"
        :refSchemaId="column.refSchemaId || schemaId"
        :refTableId="column.refTableId"
        :refLabel="column.refLabel || column.refLabelDefault"
        :data="data"
        :required="!!column.required"
        :aria-invalid="hasError"
        :aria-desribedBy="`${column.id}-input-error`"
        @focus="touched = true"
        @input="pristine = false"
        @update:modelValue="$emit('update:modelValue', $event)"
        @error="$emit('error', $event)"
        @blur="$emit('blur')"
        ref="formFieldInput"
      ></FormFieldInput>
      <div
        v-if="hasError"
        :id="`${column.id}-input-error`"
        class="bg-invalid text-required p-3 font-bold flex items-center gap-1"
      >
        <BaseIcon name="info"></BaseIcon>{{ errors[0].message }}
      </div>
    </div>
  </div>
</template>
