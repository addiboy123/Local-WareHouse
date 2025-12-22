let breadcrumbStack = [];

function resetBreadcrumbs() {
  breadcrumbStack = [];
  renderBreadcrumbs();
}

function pushBreadcrumb(folder) {
  breadcrumbStack.push(folder);
  renderBreadcrumbs();
}

function renderBreadcrumbs() {
  const el = document.getElementById("breadcrumbs");
  el.innerHTML = "";

  const root = document.createElement("span");
  root.textContent = "Root";
  root.className = "cursor-pointer hover:underline";
  root.onclick = () => {
    resetBreadcrumbs();
    loadRoot();
  };

  el.appendChild(root);

  breadcrumbStack.forEach((f, i) => {
    el.innerHTML += " / ";
    const span = document.createElement("span");
    span.textContent = f.name;
    span.className = "cursor-pointer hover:underline";
    span.onclick = () => {
      breadcrumbStack = breadcrumbStack.slice(0, i + 1);
      loadFolder(f.id);
      renderBreadcrumbs();
    };
    el.appendChild(span);
  });
}
