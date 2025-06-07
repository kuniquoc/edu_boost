import {
    API_BASE_URL,
    addLoginModal,
    addRegisterModal,
    getSubjectTypes,
    checkRole
} from "../global.js";

import { loadHeader, showDashboard, hideDashboard } from "./header.js";


let quill

document.addEventListener("DOMContentLoaded", async () => {
    if (!checkRole("ROLE_ADMIN")) {
        alert("Bạn không có quyền truy cập trang này.");
        window.location.href = "home.html";
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

    // Lấy danh sách loại môn học
    const subjectTypes = await getSubjectTypes();
    const subjectTypeSelect = document.getElementById("typeId");
    for (const [key, value] of subjectTypes.entries()) {
        const option = document.createElement("option");
        option.value = key;
        option.innerText = value;
        subjectTypeSelect.appendChild(option);
    }

    // Tải hình ảnh lên server
    window.thumbnail_url = "";
    var thumbnailInput = document.getElementById("thumbnail");

    thumbnailInput.addEventListener("change", async function () {  // Sử dụng async ở đây
        var thumbnail = thumbnailInput.files[0];

        if (thumbnail) {
            var formData = new FormData();
            formData.append("image", thumbnail);
            try {
                // Gửi yêu cầu POST tới server Spring Boot
                const response = await fetch(API_BASE_URL + "/upload-image", {
                    method: "POST",
                    headers: {
                        "Authorization": `Bearer ${token}`
                    },
                    body: formData
                });

                const url = await response.text();
                window.thumbnail_url = url;
                document.getElementById("thumbnail-preview").src = window.thumbnail_url;
            } catch (error) {
                console.error("Có lỗi xảy ra:", error);
            }
        }
    });



    // Khởi tạo Quill
    quill = new Quill('#detail-editor', {
        theme: 'snow',
        placeholder: 'Nhập chi tiết phương pháp...',
        modules: {
            toolbar: [
                [{ 'header': '1' }, { 'header': '2' }, { 'header': '3' }],  // Thêm các nút header
                ['bold', 'italic', 'underline'], // in đậm, nghiêng, gạch chân, gạch ngang
                [{ 'list': 'ordered' }, { 'list': 'bullet' }], // danh sách số/bullet
                ['link', 'image'], // chèn link, hình ảnh
                [{ 'align': [] }], // căn chỉnh
                ['clean'] // làm sạch
            ]
        }
    });

    document.getElementById("study-method-form").addEventListener("submit", async function (e) {
        e.preventDefault();

        const id = parseInt(new URLSearchParams(window.location.search).get('id'));
        const name = document.getElementById("name").value;
        const description = document.getElementById("description").value;
        const thumbnail = window.thumbnail_url;
        const typeId = parseInt(document.getElementById("typeId").value);
        const detail = quill.root.innerHTML;

        const studyMethod = { id, name, description, thumbnail, typeId, detail };

        try {
            const response = await fetch(API_BASE_URL + '/study-methods', {
                method: 'PUT',
                headers: {
                    'Content-Type': 'application/json',
                    'Authorization': `Bearer ${token}`,
                },
                body: JSON.stringify(studyMethod),
            });

            if (response.ok) {
                const result = await response.json();
                document.getElementById("message").innerText = result.message;
                document.getElementById("message").style.color = "green";
                // setTimeout(function () {
                //     // Import ipcRenderer để gửi thông điệp đến main process
                //     const { ipcRenderer } = require('electron');
                //     ipcRenderer.send('load-new-page', 'study_method_detail.html?id=' + result.data.id);
                // }, 1000);  // 1000ms = 1 giây
                window.location.href = "study_method_detail.html?id=" + result.data.id;
            } else {
                document.getElementById("message").innerText = "Lỗi khi cập nhật phương pháp học.";
                document.getElementById("message").style.color = "red";
            }
        } catch (error) {
            console.error("Lỗi:", error);
            document.getElementById("message").innerText = "Có lỗi xảy ra khi kết nối.";
            document.getElementById("message").style.color = "red";
        }
    });

    const urlParams = new URLSearchParams(window.location.search);
    const id = urlParams.get('id');

    await fetchStudyMethod(id);
});


async function fetchStudyMethod(id) {
    if (!id) {
        window.history.back();
        return;
    }

    const token = sessionStorage.getItem('token');
    if (!token) {
        window.history.back();
        return;
    }

    try {
        const response = await fetch(API_BASE_URL + '/study-methods/' + id, {
            method: 'GET',
            headers: {
                'Content-Type': 'application/json',
                'Authorization': `Bearer ${token}`,
            },
        });
        if (response.ok) {
            const data = await response.json();
            const studyMethod = data.data;
            document.getElementById("name").value = studyMethod.name;
            document.getElementById("description").value = studyMethod.description;
            window.thumbnail_url = studyMethod.thumbnail;
            document.getElementById("thumbnail-preview").src = studyMethod.thumbnail;
            document.getElementById("typeId").value = studyMethod.typeId;
            quill.root.innerHTML = studyMethod.detail;
        } else {
            alert("Không tìm thấy phương pháp học.");
            window.location.href = "home.html";
        }
    } catch (error) {
        console.error("Lỗi:", error);
        alert("Có lỗi xảy ra khi kết nối.");
        window.history.back();
    }
}

