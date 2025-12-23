const API = window.API_BASE;
const token = localStorage.getItem("token");

if (!token) location.href = "login.html";

let currentFolderId = null;

const fileList = document.getElementById("fileList");
const fileInput = document.getElementById("fileInput");
const currentPath = document.getElementById("currentPath");

let currentShareTarget = null;
// Modal Elements
function openShareModal(id, type) {
  currentShareTarget = { id, type };
  shareModal.classList.remove("hidden");

  fetchShares(type, id).then(renderShareList);
}

function closeShareModal() {
  shareModal.classList.add("hidden");
  shareList.innerHTML = "";
}

// Render Share List
function renderShareList(shares) {
  shareList.innerHTML = "";

  shares.forEach(s => {
    shareList.innerHTML += `
      <div class="flex items-center justify-between border p-2 rounded">
        <span>${s.sharedWith.email}</span>

        <div class="flex items-center gap-2">
          <select
            onchange="changePermission('${s.id}', this.value)"
            class="border rounded p-1 text-sm">
            <option value="READ" ${s.permission === "READ" ? "selected" : ""}>Read</option>
            <option value="WRITE" ${s.permission === "WRITE" ? "selected" : ""}>Write</option>
          </select>

          <button onclick="removeShare('${s.id}')"
            class="text-red-600 hover:underline text-xs">
            Remove
          </button>
        </div>
      </div>
    `;
  });
}
// Share Actions
function changePermission(shareId, permission) {
  updateSharePermission(shareId, permission)
    .then(() => console.log("Permission updated"));
}

function removeShare(shareId) {
  revokeShare(shareId)
    .then(() => {
      shareList.querySelector(`[onclick="removeShare('${shareId}')"]`)
        .closest("div").remove();
    });
}


// ---------------- PERMISSION BADGE ---------------- //
function permissionBadge(permission) {
  if (!permission) return "";

  if (permission === "WRITE") {
    return `<span class="ml-2 text-xs px-2 py-0.5 rounded bg-blue-100 text-blue-700">
      WRITE
    </span>`;
  }

  return `<span class="ml-2 text-xs px-2 py-0.5 rounded bg-gray-200 text-gray-700">
    READ
  </span>`;
}


/* ---------------- LOADERS ---------------- */

function loadRoot() {
  currentFolderId = null;
  fetchContents("/folders/root");
  resetBreadcrumbs();
}

function loadFolder(folderId) {
  currentFolderId = folderId;
  fetchContents(`/folders/${folderId}`);
}

function fetchContents(path) {
  fetch(API + path, {
    headers: { Authorization: "Bearer " + token }
  })
    .then(r => {
      if (!r.ok) throw new Error("Failed to load folder");
      return r.json();
    })
    .then(renderContents)
    .catch(e => alert(e.message));
}

/* ---------------- RENDER ---------------- */

function renderContents(data) {
  fileList.innerHTML = "";

  currentPath.textContent =
    data.folderId ? `/folder/${data.folderId}` : "/";

  if (data.folderId && (breadcrumbStack.length == 0 || breadcrumbStack[breadcrumbStack.length - 1].id !== data.folderId)) {
    pushBreadcrumb({ id: data.folderId, name: data.folderName });
  }

  /* -------- FOLDERS -------- */
  data.folders.forEach(folder => {
    fileList.innerHTML += `
      <tr class="border-t hover:bg-gray-50">
        <td class="p-3 text-blue-600 cursor-pointer"
            onclick="loadFolder('${folder.id}')">
          [Folder] ${folder.name}
          ${permissionBadge(folder.permission)}
        </td>

        <td class="p-3 hidden sm:table-cell">—</td>

        <td class="p-3 space-x-2">
          <button class="text-blue-600 hover:underline"
            onclick="loadFolder('${folder.id}')">
            Open
          </button>

          ${folder.permission === "WRITE" ? `
            <button class="text-green-600 hover:underline"
              onclick="openShareModal('${folder.id}', 'folder')">
              Share
            </button>

            <button class="text-gray-600 hover:text-blue-600"
              onclick="renameFolder('${folder.id}', '${folder.name}')">
              Rename
            </button>

            <button class="text-red-600 hover:underline"
              onclick="deleteFolder('${folder.id}')">
              Delete
            </button>
          ` : ""}
        </td>
      </tr>
    `;
  });

  /* -------- FILES -------- */
  data.files.forEach(f => {
    fileList.innerHTML += `
      <tr class="border-t hover:bg-gray-50">
        <td class="p-3">
          [File] ${f.originalName}
          ${permissionBadge(f.permission)}
        </td>

        <td class="p-3 hidden sm:table-cell">
          ${(f.size / 1024).toFixed(1)} KB
        </td>

        <td class="p-3 space-x-2">
          <button class="text-blue-600 hover:underline"
            onclick="downloadFile('${f.id}')">
            Download
          </button>

          ${f.permission === "WRITE" ? `
            <button class="text-green-600 hover:underline"
              onclick="openShareModal('${f.id}', 'file')">
              Share
            </button>

            <button class="text-gray-600 hover:text-blue-600"
              onclick="renameFile('${f.id}', '${f.originalName}')">
              Rename
            </button>

            <button class="text-red-600 hover:underline"
              onclick="deleteFile('${f.id}')">
              Delete
            </button>
          ` : ""}
        </td>
      </tr>
    `;
  });
}

/* ---------------- FILE ACTIONS ---------------- */

function uploadFile() {
  if (!fileInput.files.length) return;

  const data = new FormData();
  data.append("file", fileInput.files[0]);

  if (currentFolderId) {
    data.append("folderId", currentFolderId);
  }

  fetch(`${API}/files/upload`, {
    method: "POST",
    headers: { Authorization: "Bearer " + token },
    body: data
  })
    .then(r => {
      if (!r.ok) throw new Error("Upload failed");
      return r.json();
    })
    .then(() => {
      if (currentFolderId) loadFolder(currentFolderId);
      else loadRoot();
    })
    .catch(e => alert(e.message));
}

function downloadFile(fileId) {
  fetch(`${API}/files/${fileId}/download`, {
    headers: { Authorization: "Bearer " + token }
  })
    .then(r => r.blob())
    .then(blob => {
      const url = window.URL.createObjectURL(blob);
      const a = document.createElement("a");
      a.href = url;
      a.download = "";
      a.click();
      URL.revokeObjectURL(url);
    });
}

/* ---------------- LOGOUT ---------------- */

function logout() {
  localStorage.removeItem("token");
  location.href = "login.html";
}

/* ---------------- INIT ---------------- */

loadRoot();
