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
        const token = sessionStorage.getItem('token');
        let response;
        if (!token) {
            response = await fetch(API_BASE_URL + "/study-methods/public/" + studyMethodId, {
                method: "GET",
                headers: {
                    "Content-Type": "application/json",
                },
            })
        } else {
            response = await fetch(API_BASE_URL + "/study-methods/" + studyMethodId, {
                method: "GET",
                headers: {
                    "Content-Type": "application/json",
                    "Authorization": `Bearer ${token}`
                },
            });
        }

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

        console.log(studyMethodData.favorite);
        if (studyMethodData.favorite === true) {
            document.getElementById("favorite-button").textContent = "Bỏ yêu thích";
        } else {
            document.getElementById("favorite-button").textContent = "Thêm vào yêu thích";
        }
    } catch (error) {
        console.log(error.message);
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

async function favorite() {
    const id = new URLSearchParams(window.location.search).get('id');

    const favoriteButton = document.getElementById("favorite-button");
    if (favoriteButton.textContent === "Thêm vào yêu thích") {
        await addFavorite(id);
    } else {
        await removeFavorite(id);
    }
}

async function addFavorite(id) {
    const token = sessionStorage.getItem('token');
    if (!token) {
        toastr.error("Vui lòng đăng nhập để thực hiện chức năng này");
        return;
    }
    const respone = await fetch(API_BASE_URL + '/favorites/' + id, {
        method: 'POST',
        headers:
        {
            'Content-Type': 'application/json',
            'Authorization': `Bearer ${token}`
        }
    });
    if (!respone.ok) {
        toastr.error('Đã xảy ra lỗi khi thêm vào yêu thích');
        return;
    }

    document.getElementById("favorite-button").textContent = "Bỏ yêu thích";
}

async function removeFavorite(id) {
    const token = sessionStorage.getItem('token');
    if (!token) {
        toastr.error("Vui lòng đăng nhập để thực hiện chức năng này");
        return;
    }
    const respone = await fetch(API_BASE_URL + '/favorites/' + id, {
        method: 'DELETE',
        headers:
        {
            'Content-Type': 'application/json',
            'Authorization': `Bearer ${token}`
        }
    });
    if (!respone.ok) {
        toastr.error('Đã xảy ra lỗi khi xóa khỏi yêu thích');
        return;
    }

    document.getElementById("favorite-button").textContent = "Thêm vào yêu thích";
}

window.favorite = favorite;
