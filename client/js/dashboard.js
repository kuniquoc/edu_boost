import {
    API_BASE_URL,
    addLoginModal,
    addRegisterModal,
    getSubjectTypes,
    checkRole
} from "../global.js";

import { loadHeader, showDashboard, hideDashboard } from "./header.js";

document.addEventListener("DOMContentLoaded", async () => {
    if (!await checkRole("ROLE_ADMIN")) {
        document.body.innerHTML = "";
        alert("Bạn không có quyền truy cập trang này.");
        window.location.href = "home.html";
        return;
    }

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
                fetchUsers();
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
        fetchUsers();
    }

    const logoutButton = document.getElementById("btnLogout");
    if (logoutButton) {
        logoutButton.addEventListener("click", function () {
            logout();
            window.location.reload();
        });
    }


    const manageUsersTab = document.getElementById("manageUsersTab");
    const manageStudyMethodsTab = document.getElementById("manageStudyMethodsTab");
    const manageUsers = document.getElementById("manageUsers");
    const manageStudyMethods = document.getElementById("manageStudyMethods");

    // Tab switching
    manageUsersTab.addEventListener("click", async () => {
        manageUsers.classList.add("active");
        manageStudyMethods.classList.remove("active");

        // Cập nhật trạng thái của menu sidebar
        document.querySelector(".sidebar li.active").classList.remove("active");
        manageUsersTab.parentElement.classList.add("active");

        sessionStorage.setItem("tabActive", "manageUsers");

        await fetchRoles();
        await fetchUsers();
    });

    manageStudyMethodsTab.addEventListener("click", async () => {
        manageStudyMethods.classList.add("active");
        manageUsers.classList.remove("active");

        // Cập nhật trạng thái của menu sidebar
        document.querySelector(".sidebar li.active").classList.remove("active");
        manageStudyMethodsTab.parentElement.classList.add("active");

        sessionStorage.setItem("tabActive", "manageStudyMethods");

        await fetchTypes();
        fetchStudyMethods();
    });

    const tabActive = sessionStorage.getItem("tabActive");
    if (!tabActive) {
        sessionStorage.setItem("tabActive", "manageUsers");
    }
    if (tabActive === "manageUsers") {
        document.getElementById("manageUsersTab").click();
    } else {
        document.getElementById("manageStudyMethodsTab").click();
    }


    //Pagination
    const prevPageButton = document.getElementById('prevPage');
    const nextPageButton = document.getElementById('nextPage');
    const pageNumberSpan = document.getElementById('pageNumber');
    let currentPage = 1;
    let totalPages = 1;

    function updatePagination(page, total) {
        currentPage = page;
        totalPages = total;
        pageNumberSpan.dataset.page = currentPage;
        pageNumberSpan.textContent = currentPage;
        prevPageButton.disabled = currentPage <= 1;
        nextPageButton.disabled = currentPage >= totalPages;
    }
    window.updatePagination = updatePagination;

    prevPageButton.addEventListener('click', () => navigateToPage(currentPage - 1));
    nextPageButton.addEventListener('click', () => navigateToPage(currentPage + 1));

    function navigateToPage(page) {
        if (page < 1 || page > totalPages) return;
        const pageNumber = document.getElementById('pageNumber');
        pageNumber.dataset.page = page;
        pageNumber.textContent = page;
        const tabActive = sessionStorage.getItem("tabActive");
        if (tabActive === "manageUsers") {
            searchUsers();
        } else {
            searchStudyMethods();
        }
    }


});


async function fetchRoles() {
    const token = sessionStorage.getItem("token");
    const response = await fetch(API_BASE_URL + "/users/roles", {
        method: "GET",
        headers: {
            "Content-Type": "application/json",
            "Authorization": `Bearer ${token}`
        }
    })
    if (!response.ok) throw new Error("Không thể tải thông tin các vai trò.");

    const data = await response.json();
    const roles = data.data

    const roleSelect = document.getElementById("roleSelect");
    roles.forEach(role => {
        const option = document.createElement("option");
        option.value = role.id;
        option.text = role.roleName;
        roleSelect.appendChild(option);
    });
}

async function fetchTypes() {
    const types = await getSubjectTypes();

    const typeSelect = document.getElementById("typeSelect");

    types.entries().forEach(([key, value]) => {
        const option = document.createElement("option");
        option.value = key
        option.text = value;
        typeSelect.appendChild(option);
    });
}

function fetchUsers() {
    const token = sessionStorage.getItem("token");
    const manageUsersTableBody = document.querySelector("#manageUsers table tbody");

    // Fetch user profile data
    fetch(API_BASE_URL + "/users", {
        method: "GET",
        headers: {
            "Content-Type": "application/json",
            "Authorization": `Bearer ${token}`
        }
    })
        .then(response => {
            if (!response.ok) throw new Error("Không thể tải thông tin các người dùng.");
            return response.json();
        })
        .then(response => {
            // Xóa nội dung cũ trong bảng
            manageUsersTableBody.innerHTML = "";

            // Thêm dữ liệu người dùng vào bảng
            response.data.forEach(user => {
                const row = document.createElement("tr");

                row.innerHTML = `
                    <td>${user.id}</td>
                    <td>${user.fullName}</td>
                    <td>${user.birthday}</td>
                    <td>${user.email}</td>
                    <td>${user.phone}</td>
                    <td>${user.gender}</td>
                    <td>${user.roles.map(role => `${role.roleName}`).join(",")}</td>
                    <td>
                        <button class="view-btn" data-id="${user.id}">
                            <i class="fas fa-eye"></i> <!-- Biểu tượng Xem -->
                        </button>
                        <button class="edit-btn" data-id="${user.id}">
                            <i class="fas fa-edit"></i> <!-- Biểu tượng Chỉnh sửa -->
                        </button>
                        <button class="delete-btn" data-id="${user.id}">
                            <i class="fas fa-trash"></i> <!-- Biểu tượng Xoá -->
                        </button>
                    </td>
                `;

                // Thêm sự kiện cho các nút (nếu cần)
                row.querySelector(".view-btn").addEventListener("click", () => viewUser(user.id));
                row.querySelector(".edit-btn").addEventListener("click", () => editUser(user.id));
                row.querySelector(".delete-btn").addEventListener("click", () => deleteUser(user.id));

                manageUsersTableBody.appendChild(row);
            });
        })
        .catch(error => console.error(error));
}

// Các hàm hỗ trợ cho sự kiện nút
function viewUser(userId) {
    alert("Xem thông tin người dùng với ID:" + userId);
}

function editUser(userId) {
    alert("Chỉnh sửa thông tin người dùng với ID:" + userId);
}

function deleteUser(userId) {
    alert("Xóa người dùng với ID:" + userId);
}

async function fetchStudyMethods() {
    try {
        const manageStudyMethodsTableBody = document.querySelector("#manageStudyMethods table tbody");

        const token = sessionStorage.getItem("token");
        if (!token) {
            return;
        }

        const page = new URLSearchParams(window.location.search).get('page') || 1;
        const typeId = new URLSearchParams(window.location.search).get('typeId') || 0;
        document.getElementById("typeSelect").value = typeId;
        const search = new URLSearchParams(window.location.search).get('search') || '';
        document.getElementById("searchStudyMethod").value = search;

        // Fetch user profile data
        const response = await fetch(API_BASE_URL + `/study-methods?page=${page}&typeId=${typeId}&search=${search}`, {
            method: "GET",
            headers: {
                "Content-Type": "application/json",
                "Authorization": `Bearer ${token}`
            }
        });

        if (!response.ok) throw new Error("Không thể tải thông tin các phương pháp học.");

        const data = await response.json();

        // Update pagination
        updatePagination(data.data.currentPage, data.data.totalPages);

        // Xóa nội dung cũ trong bảng
        manageStudyMethodsTableBody.innerHTML = "";

        const subjectTypes = await getSubjectTypes();

        // Thêm dữ liệu người dùng vào bảng
        data.data.studyMethodElementDtos.forEach(study_method => {
            const row = document.createElement("tr");

            row.innerHTML = `
                    <td>${study_method.id}</td>
                    <td>${study_method.name}</td>
                    <td>${study_method.description}</td>
                    <td>${subjectTypes.get(study_method.typeId)}</td>
                    <td>
                        <button class="view-btn" data-id="${study_method.id}">
                            <i class="fas fa-eye"></i> <!-- Biểu tượng Xem -->
                        </button>
                        <button class="edit-btn" data-id="${study_method.id}">
                            <i class="fas fa-edit"></i> <!-- Biểu tượng Chỉnh sửa -->
                        </button>
                        <button class="delete-btn" data-id="${study_method.id}">
                            <i class="fas fa-trash"></i> <!-- Biểu tượng Xoá -->
                        </button>
                    </td>
                `;

            // Thêm sự kiện cho các nút (nếu cần)
            row.querySelector(".view-btn").addEventListener("click", () => viewStudyMethod(study_method.id));
            row.querySelector(".edit-btn").addEventListener("click", () => editStudyMethod(study_method.id));
            row.querySelector(".delete-btn").addEventListener("click", () => deleteStudyMethod(study_method.id));

            manageStudyMethodsTableBody.appendChild(row);
        });
    } catch (error) {
        console.error(error);
    }
}

// Các hàm hỗ trợ cho sự kiện nút
function viewStudyMethod(smId) {
    window.location.href = `study_method_detail.html?id=${smId}`;
}

function editStudyMethod(smId) {
    window.location.href = `update_study_method.html?id=${smId}`;
}

function deleteStudyMethod(smId) {
    if (confirm("Bạn có chắc chắn muốn xóa phương pháp học này không?")) {
        const token = sessionStorage.getItem("token");
        const response = fetch(API_BASE_URL + `/study-methods/${smId}`, {
            method: "DELETE",
            headers: {
                "Content-Type": "application/json",
                "Authorization": `Bearer ${token}`
            }
        });
        alert("Xóa phương pháp học thành công.");
        fetchStudyMethods();
    }
}

function searchUsers() {
    const search = document.getElementById("searchUser").value;
    const page = document.getElementById("pageNumber").dataset.page;
    const roleId = document.getElementById("roleSelect").value;
    let url = 'dashboard.html';
    if (page) url += `?page=${page}`; else url += '?page=1';
    if (roleId != '0') url += `&roleId=${roleId}`;
    if (search !== '') url += `&search=${search}`;
    window.location.href = url;
}

function searchStudyMethods() {
    const page = document.getElementById("pageNumber").dataset.page;
    const search = document.getElementById("searchStudyMethod").value;
    const typeId = document.getElementById("typeSelect").value;
    let url = 'dashboard.html';
    if (page) url += `?page=${page}`; else url += '?page=1';
    if (typeId != '0') url += `&typeId=${typeId}`;
    if (search !== '') url += `&search=${search}`;
    window.location.href = url;
}

window.searchUsers = searchUsers;
window.searchStudyMethods = searchStudyMethods;