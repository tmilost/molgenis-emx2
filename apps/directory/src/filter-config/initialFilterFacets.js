const ontologySchema = "DirectoryOntologies/graphql";

export const initialFilterFacets = [
  {
    facetTitle: "Diagnosis available",
    component: "OntologyFilter",
    sourceSchema: ontologySchema,
    sourceTable: "DiseaseTypes",
    applyToColumn: "collections.diagnosis_available.code",
    filterValueAttribute: "name",
    filterLabelAttribute: "label",
    removeOptions: [],
    ontologyIdentifiers: ["ICD", "ORPHA"],
    sortColumn: "name",
    sortDirection: "asc",
    negotiatorRequestString: "Disease type(s):",
    showFacet: true,
    showMatchTypeSelector: true,
  },
  {
    facetTitle: "Countries",
    component: "CheckboxFilter",
    sourceSchema: ontologySchema,
    sourceTable: "Countries",
    applyToColumn: "country.name",
    filterValueAttribute: "name",
    filterLabelAttribute: "label",
    removeOptions: [],
    showMatchTypeSelector: false,
    negotiatorRequestString: "Countries:",
    adaptive: false,
    sortColumn: "label",
    sortDirection: "asc",
    showFacet: true,
  },
  {
    facetTitle: "Collection type",
    component: "CheckboxFilter",
    sourceSchema: ontologySchema,
    sourceTable: "CollectionTypes",
    applyToColumn: "collections.type.name",
    filterValueAttribute: "name",
    filterLabelAttribute: "label",
    removeOptions: ["other"],
    showMatchTypeSelector: false,
    negotiatorRequestString: "Collection type(s):",
    adaptive: false,
    sortColumn: "label",
    sortDirection: "asc",
    showFacet: true,
  },
  {
    facetTitle: "Categories",
    component: "CheckboxFilter",
    sourceSchema: ontologySchema,
    sourceTable: "Categories",
    applyToColumn: "collections.categories.name",
    filterValueAttribute: "name",
    filterLabelAttribute: "label",
    removeOptions: ["other"],
    showMatchTypeSelector: false,
    negotiatorRequestString: "Categories:",
    adaptive: false,
    sortColumn: "label",
    sortDirection: "asc",
    showFacet: true,
  },
  {
    facetTitle: "Material type",
    component: "CheckboxFilter",
    sourceSchema: ontologySchema,
    sourceTable: "MaterialTypes",
    applyToColumn: "collections.materials.name",
    filterValueAttribute: "name",
    filterLabelAttribute: "label",
    removeOptions: ["other"],
    showMatchTypeSelector: false,
    negotiatorRequestString: "Material type(s):",
    adaptive: false,
    sortColumn: "label",
    sortDirection: "asc",
    showFacet: true,
  },
  {
    facetTitle: "Collaboration type",
    component: "ToggleFilter",
    applyToColumn: "collections.commercial_use",
    showMatchTypeSelector: false,
    trueOption: {
      text: "Available to commercial use",
      value: true,
    },
    negotiatorRequestString: "Available to commercial use",
    showFacet: true,
  },
  {
    facetTitle: "Biobank services",
    component: "CheckboxFilter",
    sourceSchema: ontologySchema,
    sourceTable: "Capabilities",
    applyToColumn: "capabilities.name",
    filterValueAttribute: "name",
    filterLabelAttribute: "label",
    negotiatorRequestString: "Biobank services:",
    sortColumn: "label",
    sortDirection: "asc",
    showFacet: true,
  },
  {
    facetTitle: "Quality labels",
    component: "CheckboxFilter",
    sourceSchema: ontologySchema,
    sourceTable: "AssessmentLevels",
    applyToColumn: "collections.combined_quality.name",
    filterValueAttribute: "name",
    filterLabelAttribute: "label",
    negotiatorRequestString: "Quality label(s):",
    sortColumn: "label",
    sortDirection: "asc",
    showFacet: true,
  },
  {
    facetTitle: "Network",
    component: "CheckboxFilter",
    sourceTable: "Networks",
    applyToColumn: "collections.combined_network.id",
    filterValueAttribute: "id",
    filterLabelAttribute: "name",
    negotiatorRequestString: "Network(s):",
    sortColumn: "name",
    sortDirection: "asc",
    showFacet: true,
  },
  {
    facetTitle: "Data category",
    component: "CheckboxFilter",
    sourceSchema: ontologySchema,
    sourceTable: "DataCategories",
    applyToColumn: "collections.data_categories.name",
    filterValueAttribute: "name",
    filterLabelAttribute: "label",
    removeOptions: ["other"],
    negotiatorRequestString: "Data categories:",
    sortColumn: "name",
    sortDirection: "asc",
    showFacet: true,
  },
  {
    facetTitle: "search",
    component: "StringFilter",
    negotiatorRequestString: "Text search is",
    builtIn: true /** search should not be generated */,
  },
];
