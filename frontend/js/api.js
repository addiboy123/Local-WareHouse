const API_BASE = window.location.origin + "/api";
window.API_BASE = API_BASE;

function getToken() {
  return localStorage.getItem("token");
}

async function apiFetch(path, options = {}) {
  const headers = options.headers || {};

  const token = getToken();
  if (token) {
    headers["Authorization"] = "Bearer " + token;
  }

  if (!(options.body instanceof FormData)) {
    headers["Content-Type"] = "application/json";
  }

  const response = await fetch(API_BASE + path, {
    ...options,
    headers
  });

  if (response.status === 401) {
    localStorage.removeItem("token");
    window.location.href = "/login.html";
    return;
  }

  if (!response.ok) {
    const err = await response.text();
    throw new Error(err || "Request failed");
  }

  return response;
}
