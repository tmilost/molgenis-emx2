<script setup lang="ts">
import type { FormField } from "#build/components";
import type {
  columnId,
  columnValue,
  IColumn,
  IFieldError,
  ITableMetaData,
} from "../../../metadata-utils/src/types";

const props = defineProps<{
  schemaId: string;
  metadata: ITableMetaData;
  data: Record<columnId, columnValue>[];
}>();

const emit = defineEmits(["error", "update:modelValue"]);

const status = reactive({
  pristine: true,
  touched: false,
});

interface IChapter {
  title: string | "_NO_CHAPTERS";
  columns: IColumn[];
}

const chapters = computed(() => {
  return props.metadata.columns.reduce((acc, column) => {
    if (column.columnType === "HEADING") {
      acc.push({
        title: column.id,
        columns: [],
      });
    } else {
      if (acc.length === 0) {
        acc.push({
          title: "_NO_CHAPTERS",
          columns: [],
        });
      }
      acc[acc.length - 1].columns.push(column);
    }
    return acc;
  }, [] as IChapter[]);
});

const dataMap = reactive<Record<columnId, columnValue>>(
  Object.fromEntries(
    props.metadata.columns
      .filter((column) => column.columnType !== "HEADING")
      .map((column) => [column.id, ""])
  )
);

const errorMap = reactive<Record<columnId, IFieldError[]>>(
  Object.fromEntries(
    props.metadata.columns
      .filter((column) => column.columnType !== "HEADING")
      .map((column) => [column.id, []])
  )
);

const numberOffFieldsWithErrors = computed(() =>
  Object.values(errorMap).reduce((acc, errors) => acc + errors.length, 0)
);

const numberOfRequiredFields = computed(
  () => props.metadata.columns.filter((column) => column.required).length
);

const numberOfRequiredFieldsWithData = computed(
  () =>
    props.metadata.columns.filter(
      (column) => column.required && dataMap[column.id]
    ).length
);

const recordLabel = computed(() => props.metadata.label);

const formFields = ref<InstanceType<typeof FormField>[]>([]);

function validate() {
  formFields.value.forEach((formField) => {
    formField.validate(dataMap[formField.id]);
  });
}

function onUpdate(column: IColumn, $event: columnValue) {
  dataMap[column.id] = $event;
  emit("update:modelValue", dataMap);
}
function onErrors(column: IColumn, $event: IFieldError[]) {
  errorMap[column.id] = $event;
  emit("error", errorMap);
}

defineExpose({ validate });
</script>
<template>
  <div>
    <div class="first:pt-0 pt-10" v-for="chapter in chapters">
      <h2
        class="font-display md:text-heading-5xl text-heading-5xl text-form-header pb-8 scroll-mt-20"
        :id="`${chapter.title}-chapter-title`"
        v-if="chapter.title !== '_NO_CHAPTERS'"
      >
        {{ chapter.title }}
      </h2>
      <div class="pb-8" v-for="column in chapter.columns">
        <FormField
          :id="`${column.id}-form-field`"
          :schemaId="schemaId"
          :column="column"
          :data="dataMap[column.id]"
          :errors="errorMap[column.id]"
          @update:modelValue="onUpdate(column, $event)"
          @error="onErrors(column, $event)"
          @blur="validate"
          ref="formFields"
        ></FormField>
      </div>
    </div>
    <div class="bg-red-500 p-3 font-bold">
      {{ numberOffFieldsWithErrors }} fields require your attention before you
      can save this {{ recordLabel }} ( temporary section for dev)
    </div>
    <div class="bg-gray-200 p-3">
      {{ numberOfRequiredFieldsWithData }} /
      {{ numberOfRequiredFields }} required fields left ( temporary section for
      dev)
    </div>
  </div>
</template>
