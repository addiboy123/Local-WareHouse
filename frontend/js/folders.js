function createFolderPrompt() {
  const name = prompt("Folder name");
  if (!name) return;

  fetch(`${API}/folders`, {
    method: "POST",
    headers: {
      Authorization: "Bearer " + token,
      "Content-Type": "application/json"
    },
    body: JSON.stringify({
      name,
      parentId: currentFolderId
    })
  }).then(() => {
    currentFolderId ? loadFolder(currentFolderId) : loadRoot();
  });
}
function renameFolder(id, oldName) {
  const name = prompt("New folder name", oldName);
  if (!name) return;

  fetch(`${API}/folders/${id}`, {
    method: "PUT",
    headers: {
      Authorization: "Bearer " + token,
      "Content-Type": "application/json"
    },
    body: JSON.stringify({ name })
  }).then(() => loadFolder(currentFolderId));
}

function deleteFolder(id) {
  if (!confirm("Delete folder recursively?")) return;

  fetch(`${API}/folders/${id}`, {
    method: "DELETE",
    headers: { Authorization: "Bearer " + token }
  }).then(() => loadRoot());
}
