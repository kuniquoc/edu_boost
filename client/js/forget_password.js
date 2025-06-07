import {
    API_BASE_URL,
    addLoginModal,
    addRegisterModal,
} from "../global.js";

import { loadHeader, showDashboard, hideDashboard } from "./header.js";

document.addEventListener("DOMContentLoaded", async () => {
    //#region Load header
    const headerContent = sessionStorage.getItem("headerContent");
    if (headerContent) {
        document.getElementById("header").innerHTML = headerContent;
    }
    loadHeader();
    //#endregion

    //#region Load modal
    addLoginModal();
    addRegisterModal();

    const loginForm = document.getElementById("loginForm");
    loginForm.addEventListener("submit", async function (e) {
        const check = await login(e);
        showDashboard();
        document.getElementById("btnLogout").addEventListener("click", hideDashboard);
        if (check) window.location.reload();
    });

    const registerForm = document.getElementById("registerForm");
    registerForm.addEventListener("submit", register);

    const logoutButton = document.getElementById("btnLogout");
    if (logoutButton) {
        logoutButton.addEventListener("click", function () {
            logout();
            window.location.reload();
        });
    }
    //#endregion
});