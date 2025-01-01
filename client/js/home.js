import {
    addLoginModal,
    addRegisterModal,
} from "../global.js";

import { loadHeader, showDashboard, hideDashboard } from "./header.js";

document.addEventListener("DOMContentLoaded", () => {
    const headerContent = sessionStorage.getItem("headerContent");
    if (headerContent) {
        document.getElementById("header").innerHTML = headerContent;
    }
    loadHeader();

    addLoginModal();
    document.getElementById("loginForm").addEventListener("submit", async function (e) {
        await login(e);
        showDashboard();
        document.getElementById("btnLogout").addEventListener("click", hideDashboard);
    });
    addRegisterModal();
    document.getElementById("registerForm").addEventListener("submit", register);
});