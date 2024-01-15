export const initialBiobankReportColumns = [
  {
    label: "Quality labels:",
    column: {
      quality: [{ quality_standard: ["name"] }],
    },
  },
  {
    label: "Collection types:",
    column: {
      collections: [
        "id",
        "size",
        { type: ["label"] },
        {
          quality: [{ quality_standard: ["name"] }],
        },
        { sub_collections: ["id"] },
      ],
    },
  },
  { label: "Juridical person:", column: "juridical_person" },
  { label: "Biobank capabilities:", column: { capabilities: ["label"] } },
  /** properties that are required but should not be rendered as attributes */
  {
    column: [
      "id",
      "name",
      "collections.id",
      "collections.name",
      "collections.size",
    ],
  },
];

export default initialBiobankReportColumns;
