import {
    API_BASE_URL,
    addLoginModal,
    addRegisterModal,
    getSubjectTypes,
    checkRole
} from "../global.js";

import { loadHeader, showDashboard, hideDashboard } from "./header.js";


document.addEventListener("DOMContentLoaded", async () => {
    if (!checkRole("ROLE_ADMIN")) {
        window.location.href = "home.html?toastr=error&toastrMessage=Bạn không có quyền truy cập trang này.";
        return;
    }

    const headerContent = sessionStorage.getItem("headerContent");
    if (headerContent) {
        document.getElementById("header").innerHTML = headerContent;
    }
    loadHeader();

    if (!document.getElementById("link-dashboard")) {
        console.log(document.getElementById("link-dashboard"));
        window.location.href = "home.html";
        return;
    }

    addLoginModal();
    addRegisterModal();

    const token = sessionStorage.getItem('token');

    if (!token) {
        const container = document.querySelector(".container");
        container.style.display = "none";
        showLoginModal();
        document.getElementById("loginForm").addEventListener("submit", async function (e) {
            const check = await login(e);
            if (check) {
                container.style.display = "flex";
                showDashboard();
                document.getElementById("btnLogout").addEventListener("click", function () {
                    hideDashboard();
                    window.location.reload();
                });
            }
        });
        document.getElementById("close-login").addEventListener("click", function () {
            hideLoginModal();
            window.history.back();
        });
        document.getElementById("registerForm").addEventListener("submit", register);
        document.getElementById("close-register").addEventListener("click", function () {
            hideRegisterModal();
            window.history.back();
        });
    }

    document.getElementById("confirm-password").onchange = validatePassword;

    function validatePassword() {
        var pass1 = document.getElementById("password").value;
        var pass2 = document.getElementById("confirm-password").value;
        if (pass1 != pass2)
            document.getElementById("confirm-password").setCustomValidity("Mật khẩu không khớp, vui lòng nhập lại");
        else
            document.getElementById("confirm-password").setCustomValidity('');
    }


    document.getElementById("user-form").addEventListener("submit", async function (e) {
        e.preventDefault();

        const username = document.getElementById("username").value;
        const password = document.getElementById("password").value;

        const registerData = {
            username: username,
            password: password
        };

        try {
            const response = await fetch(API_BASE_URL + '/auth/signup', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json'
                },
                body: JSON.stringify(registerData)
            });

            let result;
            const contentType = response.headers.get("content-type");

            if (contentType && contentType.includes("application/json")) {
                result = await response.json();
            } else {
                result = await response.text();
            }

            if (response.status === 201) {
                toastr.success("Tạo người dùng thành công.");
                setTimeout(() => {
                    window.history.back();
                }, 2000); // Chờ 2 giây trước khi quay lại
            } else {
                toastr.error("Tạo người dùng thất bại. " + result.message);
            }
        } catch (error) {
            console.error("Lỗi: ", error);
        }
    });

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


