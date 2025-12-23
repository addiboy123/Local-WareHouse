const API = window.API_BASE;
const token = localStorage.getItem("token");

function fetchShares(type, id) {
  return fetch(`${API}/${type}s/${id}/shares`, {
    headers: { Authorization: "Bearer " + token }
  }).then(r => r.json());
}

function updateSharePermission(shareId, permission) {
  return fetch(`${API}/shares/${shareId}`, {
    method: "PUT",
    headers: {
      "Content-Type": "application/json",
      Authorization: "Bearer " + token
    },
    body: JSON.stringify({ permission })
  });
}

function revokeShare(shareId) {
  return fetch(`${API}/shares/${shareId}`, {
    method: "DELETE",
    headers: { Authorization: "Bearer " + token }
  });
}
