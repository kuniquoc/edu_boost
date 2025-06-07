import {
    API_BASE_URL,
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

        const updatedInfo = {
            fullName: userInfoForm.fullName.value,
            birthday: userInfoForm.birthday.value,
            email: userInfoForm.email.value,
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
                alert("Thông tin đã được cập nhật thành công!");
                window.location.reload();
            })
            .catch(error => alert(error.message));
    });

    // Submit updated password
    passwordForm.addEventListener("submit", (event) => {
        event.preventDefault();

        const updatedInfo = {
            currentPassword: passwordForm.currentPassword.value,
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
                alert("Mật khẩu đã được cập nhật thành công!");
            })
            .catch(error => alert(error.message));
    });
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
            const { fullName, birthday, email, phone, gender } = data.data;
            userInfoForm.fullName.value = fullName;
            userInfoForm.birthday.value = birthday;
            userInfoForm.email.value = email;
            userInfoForm.phone.value = phone;
            userInfoForm.gender.value = gender;
        })
        .catch(error => console.error(error));
}