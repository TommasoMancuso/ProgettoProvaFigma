// =========================
// INIT
// =========================

document.addEventListener("DOMContentLoaded", () => {
  initMobileMenu();
  initHeroSlider();
  initSmoothScroll();
  initActiveNavOnScroll();
});


// =========================
// MOBILE MENU
// =========================

function initMobileMenu() {
  const menuToggle = document.getElementById("menu-toggle");
  const mobileMenu = document.getElementById("mobile-menu");

  if (!menuToggle || !mobileMenu) return;

  menuToggle.addEventListener("click", () => {
    mobileMenu.classList.toggle("hidden");
  });

  // Chiudi menu quando clicchi un link mobile
  document.querySelectorAll("#mobile-menu a").forEach(link => {
    link.addEventListener("click", () => {
      mobileMenu.classList.add("hidden");
    });
  });

  // Reset automatico tornando desktop
  window.addEventListener("resize", () => {

    if (window.innerWidth > 768) {
      mobileMenu.classList.add("hidden");
    }

  });
}


// =========================
// HERO SLIDER
// =========================

function initHeroSlider() {
  const hero = document.querySelector(".hero");
  const nextBtn = document.getElementById("next-slide");
  const prevBtn = document.getElementById("prev-slide");

  if (!hero) return;

  const images = [
    "https://images.unsplash.com/photo-1769028886710-3ecefa6d0acd?crop=entropy&cs=tinysrgb&fit=max&fm=jpg&q=80&w=1080",
    "https://images.unsplash.com/photo-1760256996909-e9138af0e95a?crop=entropy&cs=tinysrgb&fit=max&fm=jpg&q=80&w=1080",
    "https://images.unsplash.com/photo-1758887698531-15c11a7f3dbb?crop=entropy&cs=tinysrgb&fit=max&fm=jpg&q=80&w=1080"
  ];

  let current = 0;

  function changeSlide(index) {
    hero.style.background = `url('${images[index]}') center/cover no-repeat`;
  }

  function nextSlide() {
    current = (current + 1) % images.length;
    changeSlide(current);
  }

  function prevSlide() {
    current = (current - 1 + images.length) % images.length;
    changeSlide(current);
  }

  // init first slide
  changeSlide(current);

  if (nextBtn) nextBtn.addEventListener("click", nextSlide);
  if (prevBtn) prevBtn.addEventListener("click", prevSlide);

  setInterval(nextSlide, 5000);
}


// =========================
// SMOOTH SCROLL
// =========================

function initSmoothScroll() {
  document.querySelectorAll('a[href^="#"]').forEach(anchor => {
    anchor.addEventListener("click", function (e) {
      const target = document.querySelector(this.getAttribute("href"));
      if (!target) return;

      e.preventDefault();

      target.scrollIntoView({
        behavior: "smooth"
      });
    });
  });
}


// =========================
// ACTIVE NAV ON SCROLL
// =========================

function initActiveNavOnScroll() {
  const sections = document.querySelectorAll("section[id]");
  const navLinks = document.querySelectorAll(".nav-links a");

  if (!sections.length || !navLinks.length) return;

  window.addEventListener("scroll", () => {
    let scrollPos = window.scrollY + 120;

    sections.forEach(section => {
      const top = section.offsetTop;
      const bottom = top + section.offsetHeight;

      if (scrollPos >= top && scrollPos < bottom) {
        navLinks.forEach(link => {
          link.classList.remove("active");

          if (link.getAttribute("href") === `#${section.id}`) {
            link.classList.add("active");
          }
        });
      }
    });
  });
}