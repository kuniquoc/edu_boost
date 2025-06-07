
import {
    API_BASE_URL,
    addLoginModal,
    addRegisterModal,
    getSubjectTypes
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
        searchStudyMethods();
    }

    //#region load filter
    const filter_select = document.getElementById("filter");

    const subjectTypes = await getSubjectTypes();

    subjectTypes.entries().forEach(([key, value]) => {
        const option = document.createElement("option");
        option.value = key;
        option.text = value;
        filter_select.appendChild(option);
    });
    //#endregion

    await fetchStudyMethods();
});

// Function to fetch study methods from API
async function fetchStudyMethods() {
    try {
        const page = new URLSearchParams(window.location.search).get('page') || 1;
        const typeId = new URLSearchParams(window.location.search).get('typeId') || 0;
        document.getElementById("filter").value = typeId;
        const search = new URLSearchParams(window.location.search).get('search') || '';
        document.getElementById("searchInput").value = search;
        const favorite = new URLSearchParams(window.location.search).get('favorite') || false;
        document.getElementById("searchFavorite").checked = favorite === 'true';
        let api_url;
        let response;
        const token = sessionStorage.getItem('token');
        if (token) {
            api_url = API_BASE_URL + `/study-methods?page=${page}&typeId=${typeId}&search=${search}&favorite=${favorite}`;
            response = await fetch(api_url, {
                method: 'GET',
                headers: {
                    'Content-Type': 'application/json',
                    'Authorization': `Bearer ${token}`,
                },
            });
        } else {
            api_url = API_BASE_URL + `/study-methods/public?page=${page}&typeId=${typeId}&search=${search}&favorite=${favorite}`;
            response = await fetch(api_url, {
                method: 'GET',
                headers: {
                    'Content-Type': 'application/json',
                },
            });
        }


        const data = await response.json();

        // Update pagination
        updatePagination(data.data.currentPage, data.data.totalPages);

        // Get the container where the study methods will be displayed
        const studyMethodsContainer = document.getElementById('study-methods');

        // Clear previous content
        studyMethodsContainer.innerHTML = '';

        const subjectTypes = await getSubjectTypes();

        // Iterate over the methods and create HTML elements to display them
        data.data.studyMethodElementDtos.forEach(method => {
            const card = document.createElement('div');
            card.classList.add('card');
            card.innerHTML = `
            <div class="card-thumbnail">
                <img src="${method.thumbnail}" alt="${method.name}" class="thumbnail" onclick="window.location.href = 'study_method_detail.html?id=${method.id}'">
            </div>
            <div class="card-content">
                <h2 onclick="window.location.href = 'study_method_detail.html?id=${method.id}'">${method.name}</h2>
                <p>${method.description}</p>
                <div class="card-footer">
                    <span class="badge">${subjectTypes.get(method.typeId)}</span>
                    <span class="heart-icon" onclick="favorite(${method.id})">
                        <svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 24 24" width="24" height="24">
                            <path id="heart-path-${method.id}" d="M12 21.35l-1.45-1.32C5.4 15.36 2 12.28 2 8.5 2 5.42 4.42 3 7.5 3c1.74 0 3.41.81 4.5 2.09C13.09 3.81 14.76 3 16.5 3 19.58 3 22 5.42 22 8.5c0 3.78-3.4 6.86-8.55 11.54L12 21.35z"
                            fill="none" stroke="currentColor" stroke-width="2"/>
                        </svg>
                    </span>
                </div>
            </div>
            `;

            // Append the card to the study methods container
            studyMethodsContainer.appendChild(card);

            if (method.isFavorite) {
                changeHeartColor(method.id, "red");
            }
        });
    } catch (error) {
        console.error('Error fetching study methods:', error);
    }
}

function changeHeartColor(id, color) {
    if (color === "red") {
        const heartPath = document.getElementById("heart-path-" + id);
        heartPath.setAttribute("fill", "red");
    } else if (color === "none") {
        const heartPath = document.getElementById("heart-path-" + id);
        heartPath.setAttribute("fill", "none");
    }
}


async function favorite(id) {
    const state = document.getElementById("heart-path-" + id).getAttribute("fill");
    if (state === "red") {
        await removeFavorite(id);
    } else {
        await addFavorite(id);
    }
}

async function addFavorite(id) {
    const token = sessionStorage.getItem('token');
    if (!token) {
        alert("Vui lòng đăng nhập để thực hiện chức năng này");
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
        alert('Đã xảy ra lỗi khi thêm vào yêu thích');
        return;
    }

    changeHeartColor(id, "red");
}

async function removeFavorite(id) {
    const token = sessionStorage.getItem('token');
    if (!token) {
        alert("Vui lòng đăng nhập để thực hiện chức năng này");
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
        alert('Đã xảy ra lỗi khi xóa khỏi yêu thích');
        return;
    }

    changeHeartColor(id, "none");
}

function searchStudyMethods() {
    const page = document.getElementById("pageNumber").dataset.page;
    const search = document.getElementById("searchInput").value;
    const typeId = document.getElementById("filter").value;
    const favorite = document.getElementById("searchFavorite").checked
    let url = 'study_method.html';
    if (page) url += `?page=${page}`; else url += '?page=1';
    if (typeId != '0') url += `&typeId=${typeId}`;
    if (search !== '') url += `&search=${search}`;
    if (favorite === true) url += `&favorite=${favorite}`;
    window.location.href = url;
}

window.favorite = favorite;
window.searchStudyMethods = searchStudyMethods;


