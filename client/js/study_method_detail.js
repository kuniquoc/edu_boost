import {
    API_BASE_URL,
    addLoginModal,
    addRegisterModal,
    getSubjectTypes,
} from "../global.js";

import { loadHeader, showDashboard, hideDashboard } from "./header.js";

document.addEventListener('DOMContentLoaded', async () => {
    const headerContent = sessionStorage.getItem("headerContent");
    if (headerContent) {
        document.getElementById("header").innerHTML = headerContent;
    }
    loadHeader();

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

    const studyMethodId = new URLSearchParams(window.location.search).get('id');
    if (!studyMethodId) {
        container.innerHTML = '<h1>Không tìm thấy phương pháp học</h1>';
        return;
    }

    try {
        const response = await fetch(API_BASE_URL + "/study-methods/public/" + studyMethodId, {
            method: "GET",
            headers: {
                "Content-Type": "application/json",
            },
        })

        if (!response.ok) throw new Error("Không thể nhận thông tin phương pháp học.");
        const data = await response.json();
        const studyMethodData = data.data;
        const container = document.querySelector('.container');

        const subjectTypes = await getSubjectTypes();

        container.innerHTML = `
                <div class="study-method-detail">
                    <div class="content">
                        <div class="content-header">
                            <div class="thumbnail">
                                <img src="${studyMethodData.thumbnail}" alt="${studyMethodData.name}">
                            </div>
                            <h1>${studyMethodData.name}</h1>
                            <p class="type">Loại: <span>${subjectTypes.get(studyMethodData.typeId)}</span></p>
                            <p class="description">${studyMethodData.description}</p>
                        </div>
                        <div class="content-detail">
                            <div>${studyMethodData.detail}</div>
                        </div>
                    </div>
                </div>
            `;
    } catch (error) {
        console.log(error.message);
    }
});
