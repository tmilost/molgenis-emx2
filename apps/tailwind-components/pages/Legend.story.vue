<template>
  <div class="flex flex-row">
    <div class="basis-1/2 bg-sidebar-gradient">
      <FormLegend :sections="sections" @go-to-section="handleGoToRequest" />
    </div>
    <div class="basis-1/2 text-title p-4">
      <h3>Active section: {{ sections.find((s) => s.isActive) }}</h3>
    </div>
  </div>
</template>

<script lang="ts" setup>
import type { IFormLegendSection } from "../../metadata-utils/src/types";

function handleGoToRequest(section: IFormLegendSection) {
  sections.value.forEach((s) => {
    if (s.domId === section.domId) {
      s.isActive = true;
    } else {
      s.isActive = false;
    }
  });
}

const sections = ref<IFormLegendSection[]>([
  {
    label: "Overview",
    domId: "overview",
    isActive: true,
    errorCount: 1,
  },
  {
    label: "Population",
    domId: "population",
    errorCount: 2,
  },
  {
    label: "Contents",
    domId: "contents",
  },
  {
    label: "Access",
    domId: "access",
  },
  {
    label: "Information",
    domId: "information",
  },
]);
</script>
