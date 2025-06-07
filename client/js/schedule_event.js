import {
    API_BASE_URL,
    addLoginModal,
    addRegisterModal,
    getSubjectTypes,
    checkRole,
    formatDateToYYYYMMDD
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
            await login(e);
            showDashboard();
            document.getElementById("btnLogout").addEventListener("click", hideDashboard);
            container.style.display = "block";
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

    const logoutButton = document.getElementById("btnLogout");
    if (logoutButton) {
        logoutButton.addEventListener("click", function () {
            logout();
            window.location.reload();
        });
    }
    const eventId = new URLSearchParams(window.location.search).get('eventId');
    if (eventId) {
        fetchEvent(eventId);
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

async function fetchEvent(eventId) {
    const token = sessionStorage.getItem('token');
    const response = await fetch(API_BASE_URL + '/events/' + eventId, {
        headers: {
            'Content-Type': 'application/json',
            'Authorization': `Bearer ${token}`
        }
    });
    const event = await response.json();
    document.getElementById('eventTitle').value = event.title;
    document.getElementById('eventDescription').value = event.description;
    document.getElementById('eventDate').value = formatDateToYYYYMMDD(new Date(event.date));
    const startTimeString = `${event.startTime[0].toString().padStart(2, '0')}:${event.startTime[1].toString().padStart(2, '0')}`;
    const endTimeString = `${event.endTime[0].toString().padStart(2, '0')}:${event.endTime[1].toString().padStart(2, '0')}`;
    document.getElementById('eventStartTime').value = startTimeString;
    document.getElementById('eventEndTime').value = endTimeString;

    const todoList = document.getElementById('todoList');
    event.todoItems.forEach(todo => {
        const li = document.createElement('li');
        li.dataset.id = todo.id;
        li.dataset.completed = todo.completed;
        li.innerHTML = `
            <input type="text" class="todo-list-item" value="${todo.description}">
            <button class="done-btn" onclick="markAsDone(this)">Đã xong</button>
            <button class="delete-btn" onclick="deleteTodo(this)">Xóa</button>
            `;
        todoList.appendChild(li);
        if (todo.completed == true) {
            const button = li.querySelector('button.done-btn');
            let completed = li.dataset.completed;

            const input = li.querySelector('input');
            if (input) {
                if (completed) {
                    input.classList.add('done');
                    button.textContent = 'Hoàn tác';
                } else {
                    input.classList.remove('done');
                    button.textContent = 'Đã xong';
                }
            }
        }
    });

    const methodList = document.getElementById('methodList');
    event.studyMethods.forEach(method => {
        const li = document.createElement('li');
        li.dataset.id = method.id;
        li.innerHTML = `<span class="method-link"><a href="study_method_detail.html?id=${method.id}">${method.name}</a></span>
                        <button class="delete-btn" onclick="deleteMethod(this)">Xóa</button>`;
        methodList.appendChild(li);
    });
    const reminderList = document.getElementById('reminderList');
    event.reminders.forEach(reminder => {
        const li = document.createElement('li');
        li.dataset.id = reminder.id;

        const [year, month, day, hours, minutes] = reminder.scheduledTime;

        // Tạo đối tượng Date từ các giá trị trong mảng
        const date = new Date(year, month - 1, day, hours, minutes);

        // Lấy các thành phần và đảm bảo có đủ 2 chữ số
        const formattedMonth = String(date.getMonth() + 1).padStart(2, '0');
        const formattedDay = String(date.getDate()).padStart(2, '0');
        const formattedYear = date.getFullYear();
        const formattedHours = String(date.getHours()).padStart(2, '0');
        const formattedMinutes = String(date.getMinutes()).padStart(2, '0');

        // Trả về định dạng MM-DD-YYYYTHH:mm:ss
        const dateTimeString = `${formattedYear}-${formattedMonth}-${formattedDay}T${formattedHours}:${formattedMinutes}`;

        li.innerHTML = `<input type="datetime-local" class="reminder-list-item" value="${dateTimeString}">
        <button class="delete-btn" onclick="deleteReminder(this)">Xóa</button>`;
        reminderList.appendChild(li);
    });
}

async function saveEvent() {
    const title = document.getElementById('eventTitle').value;
    const description = document.getElementById('eventDescription').value;
    const date = document.getElementById('eventDate').value;
    const startTime = document.getElementById('eventStartTime').value;
    const endTime = document.getElementById('eventEndTime').value;
    const todoList = document.getElementById('todoList');
    const methodList = document.getElementById('methodList');

    const todoItems = [];
    const todoItemList = todoList.querySelectorAll('li');
    if (todoItems) {
        todoItemList.forEach(item => {
            const id = item.dataset.id;
            const completed = (item.dataset.completed === 'true') ? true : false;
            const input = item.querySelector('input');
            const description = input.value;
            if (!description) {
                return;
            }
            todoItems.push({ id, description, completed });
        });
    }


    const reminders = [];
    const reminderItems = reminderList.querySelectorAll('li');
    if (reminders) {
        reminderItems.forEach(item => {
            const id = item.dataset.id;
            const input = item.querySelector('input');
            const scheduledTime = input.value;
            reminders.push({ id, scheduledTime });
        });
    }

    const studyMethodIds = [];
    const methodItems = methodList.querySelectorAll('li');
    if (methodItems) {
        methodItems.forEach(item => {
            studyMethodIds.push(item.dataset.id);
        });
    }

    const eventId = new URLSearchParams(window.location.search).get('eventId');
    const event = {
        id: eventId,
        title,
        date,
        startTime,
        endTime,
        description,
        todoItems,
        reminders,
        studyMethodIds
    };


    const token = sessionStorage.getItem('token');
    const respone = await fetch(API_BASE_URL + `/events/${eventId}`, {
        method: 'PUT',
        headers: {
            'Content-Type': 'application/json',
            'Authorization': `Bearer ${token}`
        },
        body: JSON.stringify(event)
    });

    if (!respone.ok) {
        toastr.error('Đã xảy ra lỗi khi lưu sự kiện');
        return;
    }
    toastr.success('Đã lưu sự kiện');
    setTimeout(() => {
        window.history.back();
    }, 2000);
}

async function deleteEvent() {
    if (confirm('Bạn có chắc chắn muốn xóa sự kiện này không?')) {
        const token = sessionStorage.getItem('token');
        const eventId = new URLSearchParams(window.location.search).get('eventId');
        const respone = await fetch(API_BASE_URL + `/events/${eventId}`, {
            method: 'DELETE',
            headers:
            {
                'Content-Type': 'application/json',
                'Authorization': `Bearer ${token}`
            }
        });
        if (!respone.ok) {
            toastr.error('Đã xảy ra lỗi khi xóa sự kiện');
            return;
        }
        toastr.success('Đã xóa sự kiện');
        setTimeout(() => {
            window.history.back();
        }, 2000);
    }
}

// Thêm công việc mới vào danh sách To-Do
function addTodo(id) {
    const todoList = document.getElementById('todoList');
    const li = document.createElement('li');
    li.dataset.id = id; // ID mặc định là 0 (chưa lưu vào CSDL)
    li.innerHTML = `
        <input type="text" class="todo-list-item">
        <button class="done-btn" onclick="markAsDone(this)">Đã xong</button>
        <button class="delete-btn" onclick="deleteTodo(this)">Xóa</button>
        `;
    todoList.appendChild(li);

    const input = li.querySelector('input.todo-list-item'); // Lấy phần tử input
    input.focus(); // Đặt focus vào input
}

function markAsDone(button) {
    const listItem = button.parentElement;
    let completed = listItem.dataset.completed;

    // Xử lý trường hợp thuộc tính chưa tồn tại
    if (completed === undefined) {
        completed = 'false'; // hoặc 'true', tùy thuộc vào logic ban đầu
    }

    // Chuyển đổi chuỗi thành boolean và ĐẢO NGƯỢC giá trị
    completed = (completed === 'false'); // Đảo ngược giá trị boolean
    listItem.dataset.completed = completed.toString(); // Chuyển đổi lại thành chuỗi trước khi lưu vào dataset


    const input = listItem.querySelector('input');
    if (input) {
        if (completed) {
            input.classList.add('done');
            button.textContent = 'Hoàn tác';
        } else {
            input.classList.remove('done');
            button.textContent = 'Đã xong';
        }
    }
}

function deleteTodo(button) {
    const listItem = button.parentElement;
    listItem.remove();
}

async function fetchTypes() {
    const types = await getSubjectTypes();

    const typeSelect = document.getElementById("filter");

    types.entries().forEach(([key, value]) => {
        const option = document.createElement("option");
        option.value = key
        option.text = value;
        typeSelect.appendChild(option);
    });
}


async function fetchStudyMethods(page, typeId, search, favorite) {
    try {
        const studyMethodsTableBody = document.querySelector("#studyMethodTable tbody");

        const token = sessionStorage.getItem("token");
        if (!token) {
            return;
        }

        // Fetch user profile data
        const response = await fetch(API_BASE_URL + `/study-methods?page=${page}&typeId=${typeId}&search=${search}&favorite=${favorite}`, {
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
        studyMethodsTableBody.innerHTML = "";

        const subjectTypes = await getSubjectTypes();

        // Thêm dữ liệu người dùng vào bảng
        data.data.studyMethodElementDTOs.forEach(study_method => {
            const row = document.createElement("tr");

            row.innerHTML = `
                    <td>${study_method.id}</td>
                    <td>${study_method.name}</td>
                    <td>${study_method.description}</td>
                    <td>${subjectTypes.get(study_method.typeId)}</td>
                    <td>
                        <button class="choose-btn" onclick="chooseStudyMethod(${study_method.id}, '${study_method.name}')">Chọn</button>
                    </td>
                `;

            studyMethodsTableBody.appendChild(row);
        });
    } catch (error) {
        console.error(error);
    }
}

async function showStudyMethodModal() {
    try {
        const modal = document.getElementById('studyMethodModal');
        modal.style.display = 'block';
        modal.addEventListener('click', function (e) {
            if (e.target === modal) {
                modal.style.display = 'none';
            }
        });

        const searchButton = document.getElementById('searchButton');
        searchButton.addEventListener('click', function () {
            const typeId = document.getElementById('filter').value;
            const search = document.getElementById('searchInput').value;
            const favorite = document.getElementById('searchFavorite').checked;
            fetchStudyMethods(1, typeId, search, favorite);
        });

        //Lấy type từ API
        await fetchTypes();

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

        async function navigateToPage(page) {
            if (page < 1 || page > totalPages) return;
            const pageNumber = document.getElementById('pageNumber');
            pageNumber.dataset.page = page;
            pageNumber.textContent = page;

            const typeId = document.getElementById('filter').value;
            const search = document.getElementById('searchInput').value;
            const favorite = document.getElementById('searchFavorite').checked;
            await fetchStudyMethods(page, typeId, search, favorite);
        }
        navigateToPage(1);
    }
    catch (error) {
        console.error(error);
    }
}

// Chọn phương pháp học và đóng modal
function chooseStudyMethod(id, name) {
    // Lấy id của các phương pháp học đã chọn
    const methodList = document.getElementById('methodList');
    const studyMethodIds = [];
    const methodItems = methodList.querySelectorAll('li');
    if (methodItems) {
        methodItems.forEach(item => {
            studyMethodIds.push(item.dataset.id);
        });
    }
    if (studyMethodIds.includes(String(id))) {
        toastr.success('Phương pháp học đã được chọn');
        return;
    }
    const modal = document.getElementById('studyMethodModal');
    modal.style.display = 'none';
    addMethod(id, name);
}

// Thêm phương pháp mới vào danh sách phương pháp
function addMethod(id, name) {
    const methodList = document.getElementById('methodList');
    const li = document.createElement('li');
    li.dataset.id = id; // ID mặc định là 0 (chưa lưu vào CSDL)
    li.innerHTML = `<span class="method-link"><a href="study_method_detail.html?id=${id}">${name}</a></span>
                        <button class="delete-btn" onclick="deleteMethod(this)">Xóa</button>`;
    methodList.appendChild(li);
}


function deleteMethod(button) {
    // Tìm phần tử `li` chứa nút đã nhấn
    const listItem = button.parentElement;

    // Xóa `li` khỏi danh sách
    listItem.remove();
}

function addReminder(reminder) {
    const reminderList = document.getElementById('reminderList');
    const li = document.createElement('li');
    li.dataset.id = reminder.id || 0; // ID mặc định là 0 (chưa lưu vào CSDL)
    li.innerHTML = `<input type="datetime-local" class="reminder-list-item">
    <button class="delete-btn" onclick="deleteReminder(this)">Xóa</button>`;

    reminderList.appendChild(li);

    const input = li.querySelector('input.reminder-list-item'); // Lấy phần tử input
    const now = reminder.scheduledTime ? new Date(reminder.scheduledTime) : new Date(); // Lấy thời gian hiện tại

    // Đảm bảo định dạng ngày và giờ đúng cho input datetime-local (YYYY-MM-DDTHH:mm)
    const year = now.getFullYear();
    const month = String(now.getMonth() + 1).padStart(2, '0'); // Tháng bắt đầu từ 0
    const day = String(now.getDate()).padStart(2, '0');
    const hours = String(now.getHours()).padStart(2, '0');
    const minutes = String(now.getMinutes()).padStart(2, '0');

    // Cập nhật giá trị cho input
    input.value = `${year}-${month}-${day}T${hours}:${minutes}`;
}

function deleteReminder(button) {
    const listItem = button.parentElement;
    listItem.remove();
}

window.saveEvent = saveEvent;
window.deleteEvent = deleteEvent;
window.addTodo = addTodo;
window.markAsDone = markAsDone;
window.deleteTodo = deleteTodo;
window.addMethod = addMethod;
window.deleteMethod = deleteMethod;
window.addReminder = addReminder;
window.deleteReminder = deleteReminder;
window.showStudyMethodModal = showStudyMethodModal;
window.chooseStudyMethod = chooseStudyMethod;
