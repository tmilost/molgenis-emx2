module.exports = {
  root: true,

  env: {
    es2020: true,
    node: true,
    browser: true,
    jest: true,
  },

  extends: ["plugin:vue/essential", "eslint:recommended"],

  parserOptions: {
    ecmaVersion: 2020,
  },

  overrides: [
    {
      files: [
        "**/__tests__/*.{j,t}s?(x)",
        "**/tests/unit/**/*.spec.{j,t}s?(x)",
      ],
      env: {
        jest: true,
      },
    },
  ],
};
