<script setup lang="ts">
import type { FormFields } from "#build/components";
import type {
  columnValue,
  IColumn,
  IFieldError,
  ISchemaMetaData,
} from "../../metadata-utils/src/types";
import { useRoute } from "#app/composables/router";

type Resp<T> = {
  data: Record<string, T[]>;
};

interface Schema {
  id: string;
  label: string;
  description: string;
}

const route = useRoute();
const schemaId = ref((route.query.schema as string) ?? "pet store");
const tableId = ref((route.query.table as string) ?? "Pet");

const { data: schemas } = await useFetch<Resp<Schema>>("/graphql", {
  key: "schemas",
  method: "POST",
  body: { query: `{ _schemas { id,label,description } }` },
});

const schemaIds = computed(
  () =>
    schemas.value?.data?._schemas
      .sort((a, b) => a.label.localeCompare(b.label))
      .map((s) => s.id) ?? []
);

const {
  data: schemaMeta,
  refresh,
  status,
} = await useAsyncData("form sample", () => fetchMetadata(schemaId.value));

const schemaTablesIds = computed(() =>
  (schemaMeta.value as ISchemaMetaData)?.tables.map((table) => table.id)
);

const tableMeta = computed(() => {
  return schemaMeta.value === null
    ? null
    : schemaMeta.value.tables.find((table) => table.id === tableId.value);
});

const data = ref([] as Record<string, columnValue>[]);

const formFields = ref<InstanceType<typeof FormFields>>();

const formValues = ref<Record<string, columnValue>>({});

function onModelUpdate(value: Record<string, columnValue>) {
  formValues.value = value;
}

const errors = ref<Record<string, IFieldError[]>>({});

function onErrors(newErrors: Record<string, IFieldError[]>) {
  errors.value = newErrors;
}

function chapterFieldIds(chapterId: string) {
  const chapterFieldIds = [];
  let inChapter = false;
  const columns = tableMeta.value ? tableMeta.value.columns : [];

  for (const column of columns) {
    if (column.columnType === "HEADING" && column.id === chapterId) {
      inChapter = true;
    } else if (column.columnType === "HEADING" && column.id !== chapterId) {
      inChapter = false;
    } else if (inChapter) {
      chapterFieldIds.push(column.id);
    }
  }
  return chapterFieldIds;
}

function chapterErrorCount(chapterId: string) {
  const counted = chapterFieldIds(chapterId).reduce((acc, fieldId) => {
    return acc + (errors.value[fieldId]?.length ?? 0);
  }, 0);
  return counted;
}

const currentSectionDomId = ref("");

const sections = computed(() => {
  return tableMeta.value
    ? tableMeta.value.columns
        .filter((column: IColumn) => column.columnType == "HEADING")
        .map((column: IColumn) => {
          return {
            label: column.label,
            domId: column.id,
            isActive: currentSectionDomId.value.startsWith(column.id),
            errorCount: chapterErrorCount(column.id),
          };
        })
    : [];
});

function setUpChapterIsInViewObserver() {
  if (import.meta.client) {
    const observer = new IntersectionObserver(
      (entries) => {
        entries.forEach((entry) => {
          const id = entry.target.getAttribute("id");
          if (id && entry.intersectionRatio > 0) {
            currentSectionDomId.value = id;
          }
        });
      },
      {
        root: formFields.value?.$el,
        rootMargin: "0px",
        threshold: 0.5,
      }
    );

    document.querySelectorAll("[id$=chapter-title]").forEach((section) => {
      observer.observe(section);
    });
  }
}

onMounted(() => setUpChapterIsInViewObserver());

watch(
  () => tableMeta.value,
  async () => {
    await nextTick();
    setUpChapterIsInViewObserver();
  }
);

watch(
  () => schemaId.value,
  async () => {
    if (schemaMeta.value) {
      await refresh();
      tableId.value = schemaMeta.value.tables[0].id;
      useRouter().push({
        query: {
          ...useRoute().query,
          schema: schemaId.value,
          table: tableId.value,
        },
      });
    }
  }
);

watch(
  () => tableId.value,
  async () => {
    useRouter().push({
      query: {
        ...useRoute().query,
        schema: schemaId.value,
        table: tableId.value,
      },
    });
  }
);

const fieldsKey = computed(() => `${schemaId.value}-${tableId.value}-fields`);
</script>

<template>
  <div class="flex flex-row">
    <div id="mock-form-contaner" class="basis-2/3 flex flex-row border">
      <div class="basis-1/3">
        <FormLegend
          v-if="sections && sections.length"
          class="bg-sidebar-gradient mx-4"
          :sections="sections"
        />
      </div>

      <FormFields
        :key="fieldsKey"
        v-if="schemaId && tableMeta && status === 'success'"
        class="basis-2/3 p-8 border-l overflow-y-auto h-screen"
        ref="formFields"
        :schemaId="schemaId"
        :metadata="tableMeta"
        :data="data"
        @update:model-value="onModelUpdate"
        @error="onErrors($event)"
      />
    </div>

    <div class="basis-1/3 ml-2 h-screen overflow-y-scroll">
      <h2>Demo controls, settings and status</h2>

      <div class="p-4 border-2 mb-2 flex flex-col gap-4">
        <div class="flex flex-col">
          <label for="table-select" class="text-title font-bold"
            >Schema:
          </label>
          <select
            id="table-select"
            v-model="schemaId"
            class="border border-black"
          >
            <option v-for="schemaId in schemaIds" :value="schemaId">
              {{ schemaId }}
            </option>
          </select>
        </div>

        <div class="flex flex-col">
          <label for="table-select" class="text-title font-bold">Table: </label>
          <select
            id="table-select"
            v-model="tableId"
            class="border border-black"
          >
            <option v-for="tableId in schemaTablesIds" :value="tableId">
              {{ tableId }}
            </option>
          </select>
        </div>

        <button
          class="border-gray-900 border-[1px] p-2 bg-gray-200"
          @click="formFields?.validate"
        >
          External Validate
        </button>

        <div class="mt-4 flex flex-row">
          <div v-if="Object.keys(formValues).length" class="basis-1/2">
            <h3 class="text-label">Values</h3>
            <dl class="flex flex-col">
              <template v-for="(value, key) in formValues">
                <dt class="font-bold">{{ key }}:</dt>
                <dd v-if="value !== null && value !== undefined" class="pl-3">
                  {{ value }}
                </dd>
              </template>
            </dl>
          </div>
          <div v-if="Object.keys(errors).length" class="basis-1/2">
            <h3 class="text-label">Errors</h3>

            <dl class="flex flex-col">
              <template v-for="(value, key) in errors">
                <dt class="font-bold">{{ key }}:</dt>
                <dd v-if="value.length" class="ml-1">{{ value }}</dd>
              </template>
            </dl>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>
