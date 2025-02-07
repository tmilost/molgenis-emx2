<script setup lang="ts">
const modules = import.meta.glob("../**/*.story.vue", {
  import: "default",
  eager: true,
});

const stories = Object.keys(modules)
  .map((module: string) => {
    const name: string = module.split("/").reverse()[0];
    const path: string = module.replace("../pages/", "/");
    const dir: string = path
      .split("/")
      .filter((path: string) => path !== "" && path !== name)[0];
    const nameCleaned: string = name.replace(".story.vue", "");
    return {
      name: dir
        ? `${dir.charAt(0).toUpperCase() + dir.slice(1)}${nameCleaned}`
        : nameCleaned,
      dir: dir,
      path: path.replace(".vue", ""),
    };
  })
  .sort((current: Record<string, any>, next: Record<string, any>) => {
    return current.name.localeCompare(next.name);
  });
</script>

<template>
  <div
    class="overflow-x-clip min-h-screen bg-base-gradient relative after:bg-app-wrapper after:w-full after:h-[166px] after:top-0 after:absolute after:opacity-20 after:z-20 xl:after:hidden pt-15"
  >
    <div
      class="absolute top-0 left-0 z-10 w-screen h-screen overflow-hidden opacity-background-gradient"
    >
      <BackgroundGradient class="z-10" />
    </div>
    <div class="z-30 relative min-h-screen flex flex-col">
      <main class="mb-auto">
        <div id="header-place-holder"></div>
        <div class="xl:flex">
          <aside
            class="xl:min-w-95 xl:w-95 hidden xl:block pl-6 bg-sidebar-gradient"
          >
            <h2 class="text-2xl text-title font-bold my-5">Theme Styles</h2>
            <NuxtLink class="hover:underline text-title" to="/Styles.other"
              >Theme styles</NuxtLink
            >
            <h2 class="text-2xl text-title font-bold my-5">Components</h2>
            <ul class="list-none">
              <li class="py-2" v-for="story in stories">
                <NuxtLink class="hover:underline text-title" :to="story.path">{{
                  story.name
                }}</NuxtLink>
              </li>
            </ul>

            <div class="pr-6 my-6">
              <hr />
            </div>
            <h2 class="text-2xl text-title font-bold my-5">Other</h2>
            <NuxtLink class="hover:underline text-title" to="/DataFetch.other"
              >Data fetching</NuxtLink
            >
          </aside>
          <div class="xl:pl-7.5 grow p-6">
            <slot></slot>
          </div>
        </div>
      </main>
    </div>
  </div>
</template>
