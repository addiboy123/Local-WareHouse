function renameFile(id, oldName) {
  const name = prompt("New name", oldName);
  if (!name) return;

  fetch(`${API}/files/${id}`, {
    method: "PUT",
    headers: {
      Authorization: "Bearer " + token,
      "Content-Type": "application/json"
    },
    body: JSON.stringify({ name })
  }).then(() => loadFolder(currentFolderId));
}

function deleteFile(id) {
  if (!confirm("Delete file?")) return;

  fetch(`${API}/files/${id}`, {
    method: "DELETE",
    headers: { Authorization: "Bearer " + token }
  }).then(() => loadFolder(currentFolderId));
}
