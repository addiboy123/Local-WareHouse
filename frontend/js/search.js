let searchTimeout;

function search() {
  clearTimeout(searchTimeout);

  searchTimeout = setTimeout(() => {
    const q = document.getElementById("searchBox").value;
    if (!q) {
      currentFolderId ? loadFolder(currentFolderId) : loadRoot();
      return;
    }

    fetch(`${API}/search?q=${encodeURIComponent(q)}`, {
      headers: { Authorization: "Bearer " + token }
    })
      .then(r => r.json())
      .then(data => renderContents(data));
  }, 300);
}
