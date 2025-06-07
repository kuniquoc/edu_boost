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

    toastr.options = {
        "closeButton": false,
        "debug": false,
        "newestOnTop": true,
        "progressBar": true,
        "positionClass": "toast-top-right",
        "preventDuplicates": false,
        "onclick": null,
        "showDuration": "300",
        "hideDuration": "1000",
        "timeOut": "2000",
        "extendedTimeOut": "1000",
        "showEasing": "swing",
        "hideEasing": "linear",
        "showMethod": "fadeIn",
        "hideMethod": "fadeOut"
    }

    const toastrState = new URLSearchParams(window.location.search).get("toastr");
    if (toastrState) {
        const toastrMessage = new URLSearchParams(window.location.search).get("toastrMessage");
        if (toastrState === "success") {
            toastr.success(toastrMessage);
        } else if (toastrState === "error") {
            toastr.error(toastrMessage);
        }
    }
});