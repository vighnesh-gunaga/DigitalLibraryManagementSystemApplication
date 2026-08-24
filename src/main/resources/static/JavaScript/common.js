/* =========================================================
   LibraByte - Common Frontend Utilities
   ========================================================= */

const API_BASE = "/api";

const ENDPOINTS = {
    auth: {
        login: "/auth/login",
        register: "/auth/register",
        forgot: "/auth/forgot-password",
        reset: "/auth/reset-password"
    },
    books: {
        all: "/book/allbooks",
        one: id => `/book/book/${id}`,
        add: "/book/addbook",
        update: id => `/book/updatebook/${id}`,
        delete: id => `/book/deletebook/${id}`
    },
    categories: {
        all: "/category/getallcategory",
        add: "/category/addcategory",
        update: id => `/category/updatecategory/${id}`,
        delete: id => `/category/deletecategory/${id}`
    },
    publishers: {
        all: "/publisher/allpublisher",
        add: "/publisher/addpublisher",
        update: id => `/publisher/updatepublisher/${id}`,
        delete: id => `/publisher/deletepublisher/${id}`
    },
    reservations: {
        create: "/reservations/create",
        user: id => `/reservations/user/${id}`,
        all: "/reservations/all",
        cancel: id => `/reservations/cancel/${id}`,
        fulfill: id => `/reservations/fulfill/${id}`
    },
    issued: {
        issue: "/issued-books/issue",
        user: id => `/issued-books/user/${id}`,
        return: id => `/issued-books/return/${id}`
    }
};

function getToken() {
    return localStorage.getItem("token");
}

function getUserId() {
    return localStorage.getItem("userId");
}

function getRole() {
    return (localStorage.getItem("role") || "").replace(/^ROLE_/i, "").toUpperCase();
}

function getUsername() {
    return localStorage.getItem("username");
}

function getEmail() {
    return localStorage.getItem("email");
}

function escapeHtml(value) {
    return String(value ?? "").replace(/[&<>"']/g, ch => ({
        "&": "&amp;",
        "<": "&lt;",
        ">": "&gt;",
        '"': "&quot;",
        "'": "&#039;"
    }[ch]));
}

function formatDate(value) {
    if (!value) return "—";
    const d = new Date(value + (String(value).length === 10 ? "T00:00:00" : ""));
    return Number.isNaN(d.getTime()) ? escapeHtml(value) : d.toLocaleDateString();
}

function showToast(message, type = "") {
    let el = document.getElementById("toast");

    if (!el) {
        el = document.createElement("div");
        el.id = "toast";
        el.className = "toast";
        document.body.appendChild(el);
    }

    el.textContent = message;
    el.className = `toast ${type}`;

    clearTimeout(window.__toastTimer);
    window.__toastTimer = setTimeout(() => {
        if (el && el.parentNode) el.remove();
    }, 3500);
}

function saveAuth(data, email) {
    localStorage.setItem("token", data.token || "");
    localStorage.setItem("userId", data.userId ?? "");
    localStorage.setItem("username", data.username || "");
    localStorage.setItem("role", String(data.role || "USER").replace(/^ROLE_/i, "").toUpperCase());

    if (email) {
        localStorage.setItem("email", email);
    }
}

function clearAuth() {
    ["token", "userId", "username", "role", "email"].forEach(key => {
        localStorage.removeItem(key);
    });
}

function logout() {
    clearAuth();
    location.href = "/HTML/login.html";
}

function requireLogin() {
    if (!getToken()) {
        location.href = "/HTML/login.html";
        return false;
    }
    return true;
}

function requireAdmin() {
    if (!requireLogin()) return false;

    if (getRole() !== "ADMIN") {
        showToast("Your current login is not an ADMIN account. Please login again with an admin account.", "error");
        return false;
    }

    return true;
}

async function readResponse(response) {
    if (!response) return null;

    const text = await response.text();

    if (!text) return null;

    try {
        return JSON.parse(text);
    } catch (_) {
        return text;
    }
}

/*
 * All API requests go through this function.
 * It automatically attaches the JWT.
 */
async function apiFetch(path, options = {}) {
    const headers = new Headers(options.headers || {});

    if (options.body && !(options.body instanceof FormData)) {
        headers.set("Content-Type", "application/json");
    }

    const token = getToken();

    if (token) {
        headers.set("Authorization", `Bearer ${token}`);
    }

    let response;

    try {
        response = await fetch(API_BASE + path, {
            ...options,
            headers
        });
    } catch (error) {
        showToast("Cannot connect to Spring Boot server on port 8081.", "error");
        throw error;
    }

    /*
     * 401 = token missing/invalid/expired.
     * Clear the stale token and force a fresh login.
     */
    if (response.status === 401) {
        clearAuth();

        if (!location.pathname.endsWith("/login.html") &&
            !location.pathname.endsWith("/register.html")) {
            showToast("Your login session has expired. Please login again.", "error");
            setTimeout(() => {
                location.href = "/HTML/login.html";
            }, 600);
        }

        return null;
    }

    /*
     * Do NOT hide the actual 403 response here.
     * The individual page will read the backend message and display it.
     */
    return response;
}

function bookCard(book) {
    return `<article class="book-card">
        <span class="badge">${escapeHtml(book.categoryName || "Uncategorized")}</span>
        <h3>${escapeHtml(book.title)}</h3>
        <div class="author">By ${escapeHtml(book.author)}</div>
        <div class="muted">ISBN: ${escapeHtml(book.isbn || "—")}</div>
        <div class="muted">Available: <strong>${book.availableQuantity ?? 0}</strong> / ${book.quantity ?? 0}</div>
        <div class="actions">
            <a class="btn btn-primary" href="/HTML/book-details.html?id=${encodeURIComponent(book.id)}">View Details</a>
        </div>
    </article>`;
}

function setupPasswordToggle(buttonId, inputId) {
    const button = document.getElementById(buttonId);
    const input = document.getElementById(inputId);

    if (button && input) {
        button.onclick = () => {
            input.type = input.type === "password" ? "text" : "password";
            button.textContent = input.type === "password" ? "Show" : "Hide";
        };
    }
}

function setActiveNav() {
    document.querySelectorAll(".nav-links a").forEach(a => {
        if (a.pathname === location.pathname) {
            a.style.color = "var(--primary)";
        }
    });
}

document.addEventListener("DOMContentLoaded", () => {
    setActiveNav();

    document.querySelectorAll("[data-logout]").forEach(button => {
        button.addEventListener("click", logout);
    });
});
