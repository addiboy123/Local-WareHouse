function loadSharedFiles(page = 0) {
  fetch(`${API}/shared/files?page=${page}&size=10`, {
    headers: { Authorization: "Bearer " + token }
  })
    .then(r => r.json())
    .then(data => renderContents({
      folders: [],
      files: data.content
    }));
}

function loadSharedFolders(page = 0) {
  fetch(`${API}/shared/folders?page=${page}&size=10`, {
    headers: { Authorization: "Bearer " + token }
  })
    .then(r => r.json())
    .then(data => renderContents({
      folders: data.content,
      files: []
    }));
}
