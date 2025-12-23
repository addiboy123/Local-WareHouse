async function login() {
  const email = document.getElementById("email").value;
  const password = document.getElementById("password").value;

  try {
    const res = await apiFetch("/auth/login", {
      method: "POST",
      body: JSON.stringify({ email, password })
    });

    const data = await res.json();
    localStorage.setItem("token", data.token);

    window.location.href = "/dashboard.html";
  } catch (err) {
    alert(err.message);
  }
}

function logout() {
  localStorage.removeItem("token");
  window.location.href = "/login.html";
}

async function register() {
  const username = document.getElementById("username").value;
  const email = document.getElementById("email").value;
  const password = document.getElementById("password").value;

  if (!username || !email || !password) {
    alert("Username, email and password are required");
    return;
  }

  try {
    const res = await apiFetch("/auth/register", {
      method: "POST",
      body: JSON.stringify({ username, email, password })
    });

    // If backend returns token on register
    const data = await res.json();

    if (data.token) {
      localStorage.setItem("token", data.token);
      window.location.href = "/dashboard.html";
    } else {
      // fallback: redirect to login
      window.location.href = "/login.html";
    }

  } catch (err) {
    alert(err.message);
  }
}
