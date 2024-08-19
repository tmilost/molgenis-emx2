import { test, expect } from "@playwright/test";

test.beforeEach(async ({ context, baseURL }) => {
  await context.addCookies([
    {
      name: "mg_allow_analytics",
      value: "false",
      domain: new URL(baseURL as string).hostname,
      path: "/"
    }
  ]);
});

test("filter variables by cohort", async ({ page }) => {
  await page.goto("/catalogue-demo/ssr-catalogue/testNetwork1/variables");
  await expect(page.getByRole('heading', { name: 'testVarCategorical_' })).toBeVisible();
  await page.getByText("testCohort1").click();
  await expect(page.getByRole("main")).toContainText("testVarRepeats_");
  await expect(page.locator('div').filter({ hasText: /^testVarVir$/ })).toBeVisible();
  await page.getByLabel('testCohort1').check();
  await expect(page.locator('div').filter({ hasText: /^testVarNoRepeats$/ })).toBeVisible();
  await page.getByRole('button', { name: 'Harmonisations' }).click();
  //await expect(page.getByText('Available', { exact: true })).toBeVisible();
  //await expect(page.locator('div').filter({ hasText: /^testVarNoRepeats$/ })).toBeVisible();
});
