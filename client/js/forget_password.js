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

    const emailInput = document.getElementById("email");
    const codeInput = document.getElementById("code");
    const sendButton = document.getElementById("sendCode");
    const verifyButton = document.getElementById("verifyCode");
    sendButton.onclick = async function () {
        if (emailInput.value === "") {
            toastr.error("Hãy nhập email!");
            return;
        }
        const response = await fetch(`${API_BASE_URL}/auth/code-resend`, {
            method: "POST",
            headers: {
                "Content-Type": "application/json",
            },
            body: emailInput.value,
        });
        const data = await response.json();
        if (response.ok) {
            toastr.success(data.message);
        }
        else {
            toastr.error(data.message);
        }
    }

    verifyButton.onclick = async function () {
        if (emailInput.value === "") {
            toastr.error("Hãy nhập email!");
            return;
        }

        if (codeInput.value === "") {
            toastr.error("Hãy nhập mã xác nhận!");
            return;
        }
        const response = await fetch(`${API_BASE_URL}/auth/code-verify`, {
            method: "POST",
            headers: {
                "Content-Type": "application/json",
            },
            body: JSON.stringify({
                email: emailInput.value,
                code: codeInput.value,
            }),
        });
        const data = await response.json();
        if (response.ok) {
            toastr.success("Mật khẩu đã được gửi đến email của bạn!");
            window.location.href = "home.html";
        } else {
            toastr.error("Mã xác nhận không chính xác!");
        }
    }

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