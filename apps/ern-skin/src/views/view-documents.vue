<template>
  <Page id="page-documents">
    <CustomPageHeader
      class="erras-header"
      title="ERN-Skin Registry"
      subtitle="Download Documents"
      imageSrc="img/erras-header.jpg"
      height="xlarge"
      title-position-x="center"
      title-position-y="center"
    />
    <PageSection
      id="section-documents"
      aria-labelledby="section-documents-title"
      :verticalPadding="2"
    >
      <h2 id="section-documents-title"></h2>
      <p>Download additional information about the ERRAS Registry.</p>
      <FileList table="Files" labelsColumn="name" fileColumn="file" />
    </PageSection>
  </Page>
</template>

<script setup lang="ts">
// @ts-ignore
import { Page, PageSection, FileList } from "molgenis-viz";
import CustomPageHeader from "../components/CustomPageHeader.vue";
</script>
