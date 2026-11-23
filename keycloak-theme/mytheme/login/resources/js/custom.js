// =========================
// KEYCLOAK REGISTER JS
// =========================

document.addEventListener("DOMContentLoaded", () => {

  initPasswordToggle();
  initFormValidationHighlight();

});


// =========================
// PASSWORD TOGGLE (opzionale)
// =========================

function initPasswordToggle() {

  const passwordField = document.querySelector('input[type="password"]');

  if (!passwordField) return;

  // crea bottone toggle
  const toggle = document.createElement("span");

  toggle.innerText = "👁";
  toggle.style.cursor = "pointer";
  toggle.style.marginLeft = "10px";

  passwordField.parentNode.appendChild(toggle);

  let visible = false;

  toggle.addEventListener("click", () => {

    visible = !visible;
    passwordField.type = visible ? "text" : "password";

  });

}


// =========================
// SIMPLE VALIDATION UI FEEDBACK
// =========================

function initFormValidationHighlight() {

  const form = document.querySelector("#kc-register-form");

  if (!form) return;

  form.addEventListener("submit", (e) => {

    const email = form.querySelector('input[name="email"]');
    const password = form.querySelector('input[name="password"]');

    let valid = true;

    if (!email || !email.value.includes("@")) {
      email?.classList.add("is-invalid");
      valid = false;
    }

    if (!password || password.value.length < 6) {
      password?.classList.add("is-invalid");
      valid = false;
    }

    if (!valid) {
      e.preventDefault();
    }

  });

}

// VALIDAZIONE DATA DI NASCITA IN REGISTER
document.addEventListener("DOMContentLoaded", function () {

    const input = document.getElementById("birthDate");
    if (!input) return;

    const today = new Date();
    today.setDate(today.getDate() - 1);

    const maxDate = today.toISOString().split("T")[0];

    input.setAttribute("max", maxDate);

});