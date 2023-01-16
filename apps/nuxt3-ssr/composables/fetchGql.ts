import { DocumentNode } from "graphql";

export const fetchGql = (query: string | DocumentNode, variables?: object) => {
  const queryValue = typeof query !== "string" ? loadGql(query) : query;

  let body: { query: string; variables?: object } = {
    query: queryValue,
  };

  if (variables) {
    body.variables = variables;
  }

  const route = useRoute();
  const config = useRuntimeConfig();
  return $fetch(`/${route.params.schema}/catalogue/graphql`, {
    method: "POST",
    baseURL: config.public.apiBase,
    body,
  });
};
