const API = "http://localhost:8080/api";
const token = localStorage.getItem("token");

if (!token) location.href = "login.html";

function loadFiles() {
  fetch(`${API}/files`, {
    headers: { Authorization: "Bearer " + token }
  })
  .then(r => r.json())
  .then(files => {
    fileList.innerHTML = "";
    files.forEach(f => {
      fileList.innerHTML += `
        <tr class="border-t">
          <td class="p-3">${f.name}</td>
          <td class="p-3 hidden sm:table-cell">${(f.size/1024).toFixed(1)} KB</td>
          <td class="p-3">
            <a class="text-blue-600 hover:underline"
               href="${API}/files/${f.id}/download?token=${token}">
               Download
            </a>
          </td>
        </tr>
      `;
    });
  });
}

function uploadFile() {
  const data = new FormData();
  data.append("file", fileInput.files[0]);

  fetch(`${API}/files/upload`, {
    method: "POST",
    headers: { Authorization: "Bearer " + token },
    body: data
  }).then(loadFiles);
}

function logout() {
  localStorage.removeItem("token");
  location.href = "login.html";
}

loadFiles();
