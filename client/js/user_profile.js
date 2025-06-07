import {
    API_BASE_URL,
    addLoginModal,
    addRegisterModal,
    formatDateToYYYYMMDD,
} from "../global.js";

import { loadHeader, showDashboard, hideDashboard } from "./header.js";

document.addEventListener("DOMContentLoaded", () => {
    const headerContent = sessionStorage.getItem("headerContent");
    if (headerContent) {
        document.getElementById("header").innerHTML = headerContent;
    }
    loadHeader();

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
                showUserInfo();
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
    else {
        showUserInfo();
    }

    const logoutButton = document.getElementById("btnLogout");
    if (logoutButton) {
        logoutButton.addEventListener("click", function () {
            logout();
            window.location.reload();
        });
    }

    const viewProfileTab = document.getElementById("viewProfileTab");
    const changePasswordTab = document.getElementById("changePasswordTab");
    const viewProfile = document.getElementById("viewProfile");
    const changePassword = document.getElementById("changePassword");

    // Tab switching
    viewProfileTab.addEventListener("click", () => {
        viewProfile.classList.add("active");
        changePassword.classList.remove("active");

        // Cập nhật trạng thái của menu sidebar
        document.querySelector(".sidebar li.active").classList.remove("active");
        viewProfileTab.parentElement.classList.add("active");
    });

    changePasswordTab.addEventListener("click", () => {
        changePassword.classList.add("active");
        viewProfile.classList.remove("active");

        // Cập nhật trạng thái của menu sidebar
        document.querySelector(".sidebar li.active").classList.remove("active");
        changePasswordTab.parentElement.classList.add("active");
    });

    const userInfoForm = document.getElementById("userInfoForm");
    const passwordForm = document.getElementById("passwordForm");

    // Submit updated user information
    userInfoForm.addEventListener("submit", (event) => {
        event.preventDefault();

        function removeVerified(text) {
            const suffix = " (Đã xác thực)";
            return text.endsWith(suffix) ? text.slice(0, -suffix.length) : text;
        }

        const updatedInfo = {
            fullName: userInfoForm.fullName.value,
            birthday: userInfoForm.birthday.value,
            email: removeVerified(userInfoForm.email.value),
            phone: userInfoForm.phone.value,
            gender: userInfoForm.gender.value,
        };

        fetch(API_BASE_URL + "/user/profile", {
            method: "PUT",
            headers: {
                "Content-Type": "application/json",
                "Authorization": `Bearer ${token}`
            },
            body: JSON.stringify(updatedInfo)
        })
            .then(response => {
                if (!response.ok) throw new Error("Không thể cập nhật thông tin.");
                toastr.success("Thông tin đã được cập nhật thành công!");
                setTimeout(() => {
                    window.location.reload();
                }, 2000);
            })
            .catch(error => toastr.error(error.message));
    });

    // Submit updated password
    passwordForm.addEventListener("submit", (event) => {
        event.preventDefault();

        const updatedInfo = {
            oldPassword: passwordForm.currentPassword.value,
            newPassword: passwordForm.newPassword.value
        };

        fetch(API_BASE_URL + "/user/password", {
            method: "PUT",
            headers: {
                "Content-Type": "application/json",
                "Authorization": `Bearer ${token}`
            },
            body: JSON.stringify(updatedInfo)
        })
            .then(response => {
                if (!response.ok) throw new Error("Không thể đổi mật khẩu.");
                toastr.success("Mật khẩu đã được cập nhật thành công!");
            })
            .catch(error => toastr.error(error.message));
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


function showUserInfo() {
    const token = sessionStorage.getItem("token");
    const userInfoForm = document.getElementById("userInfoForm");

    // Fetch user profile data
    fetch(API_BASE_URL + "/user/profile", {
        method: "GET",
        headers: {
            "Content-Type": "application/json",
            "Authorization": `Bearer ${token}`
        }
    })
        .then(response => {
            if (!response.ok) throw new Error("Không thể tải thông tin người dùng.");
            return response.json();
        })
        .then(data => {
            const { fullName, birthday, email, phone, gender, emailVerified } = data.data;
            userInfoForm.fullName.value = fullName;
            userInfoForm.birthday.value = formatDateToYYYYMMDD(new Date(birthday));
            userInfoForm.email.value = email;
            userInfoForm.phone.value = phone;
            userInfoForm.gender.value = gender;
            if (emailVerified === true) {
                userInfoForm.email.value = email + " (Đã xác thực)";
                userInfoForm.email.readOnly = true;
            }
            else {
                userInfoForm.email.value = email;
                const authEmailForm = document.getElementById("authEmailForm");
                authEmailForm.classList.remove("hidden");
            }
        })
        .catch(error => console.error(error));
}

function sendCode() {
    const token = sessionStorage.getItem("token");
    const email = document.getElementById("userInfoForm").email.value;

    fetch(API_BASE_URL + "/auth/code-resend", {
        method: "POST",
        headers: {
            "Content-Type": "application/json",
            "Authorization": `Bearer ${token}`
        },
        body: email
    })
        .then(response => {
            if (!response.ok) throw new Error("Không thể gửi mã xác thực.");
            toastr.success("Mã xác thực đã được gửi đến email của bạn.");
            // Vô hiệu hóa nút và bật lại sau 1 phút
            const sendCodeButton = document.getElementById("sendAuthEmail");
            sendCodeButton.disabled = true;
            setTimeout(() => {
                sendCodeButton.disabled = false;  // Bật lại nút sau 1 phút
            }, 60000);  // 60000ms = 1 phút
        })
        .catch(error => toastr.error(error.message));
}

function verifyEmail() {
    const token = sessionStorage.getItem("token");
    const code = document.getElementById("authCode").value;
    const email = document.getElementById("userInfoForm").email.value;

    const emailVerifyData = {
        code: code,
        email: email
    };

    fetch(API_BASE_URL + "/auth/email-verify", {
        method: "POST",
        headers: {
            "Content-Type": "application/json",
            "Authorization": `Bearer ${token}`
        },
        body: JSON.stringify(emailVerifyData)
    })
        .then(response => {
            if (!response.ok) throw new Error("Không thể xác thực email.");
            toastr.success("Email đã được xác thực thành công.");
            setTimeout(() => {
                window.location.reload();
            }, 2000);
        })
        .catch(error => toastr.error(error.message));
}

window.sendCode = sendCode;
window.verifyEmail = verifyEmail;