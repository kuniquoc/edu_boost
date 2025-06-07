import {
    API_BASE_URL,
    addLoginModal,
    addRegisterModal,
    getSubjectTypes,
    checkRole
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
    fetchEvent(eventId);
});

async function fetchEvent(eventId) {
    const token = sessionStorage.getItem('token');
    const response = await fetch(API_BASE_URL + '/events/' + eventId, {
        headers: {
            'Content-Type': 'application/json',
            'Authorization': `Bearer ${token}`
        }
    });
    const data = await response.json();
    const event = data.data;
    document.getElementById('eventTitle').value = event.title;
    document.getElementById('eventDescription').value = event.description;
    document.getElementById('eventDate').value = event.date;
    document.getElementById('eventStartTime').value = event.startTime;
    document.getElementById('eventEndTime').value = event.endTime;
}

async function saveEvent() {
    const title = document.getElementById('eventTitle').value;
    const description = document.getElementById('eventDescription').value;
    const date = document.getElementById('eventDate').value;
    const startTime = document.getElementById('eventStartTime').value;
    const endTime = document.getElementById('eventEndTime').value;
    const todoList = document.getElementById('todoList');
    const methodList = document.getElementById('methodList');

    const todos = [];
    const todoItems = todoList.querySelectorAll('li');
    todoItems.forEach(item => {
        const id = item.dataset.id;
        const isCompleted = item.dataset.isCompleted;
        const input = item.querySelector('input');
        const description = input.value;
        if (!description) {
            return;
        }
        todos.push({ id, description, isCompleted });
    });

    const methodIds = [];
    const methodItems = methodList.querySelectorAll('li');
    methodItems.forEach(item => {
        methodIds.push(item.dataset.id);
    });

    const event = {
        title,
        date,
        startTime,
        endTime,
        description,
        todoIds,
        methodIds
    };

    const token = sessionStorage.getItem('token');
    const respone = await fetch(API_BASE_URL + '/events', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json',
            'Authorization': `Bearer ${token}`
        },
        body: JSON.stringify(event)
    });

    if (!respone.ok) {
        alert('Đã xảy ra lỗi khi lưu sự kiện');
        return;
    }

    alert('Đã lưu sự kiện');
    window.history.back();
}

async function deleteEvent() {
    if (confirm('Bạn có chắc chắn muốn xóa sự kiện này không?')) {
        const token = sessionStorage.getItem('token');
        const eventId = new URLSearchParams(window.location.search).get('id');
        const respone = await fetch(API_BASE_URL + '/events/' + eventId, {
            method: 'DELETE',
            headers:
            {
                'Content-Type': 'application/json',
                'Authorization': `Bearer ${token}`
            }
        });
        if (!respone.ok) {
            alert('Đã xảy ra lỗi khi xóa sự kiện');
            return;
        }
        alert('Đã xóa sự kiện');
        window.history.back();
    }
}

// Thêm công việc mới vào danh sách To-Do
function addTodo() {
    const todoList = document.getElementById('todoList');
    const li = document.createElement('li');
    li.dataset.id = "0"; // ID mặc định là 0 (chưa lưu vào CSDL)
    li.innerHTML = `<input type="text" class="todo-list-item"> <button class="done-btn" onclick="markAsDone(this)">Đã xong</button>`;
    todoList.appendChild(li);

    const input = li.querySelector('input.todo-list-item'); // Lấy phần tử input
    input.focus(); // Đặt focus vào input
}

function markAsDone(button) {
    const listItem = button.parentElement;
    let isCompleted = listItem.dataset.isCompleted;

    // Xử lý trường hợp thuộc tính chưa tồn tại
    if (isCompleted === undefined) {
        isCompleted = 'false'; // hoặc 'true', tùy thuộc vào logic ban đầu
    }

    // Chuyển đổi chuỗi thành boolean và ĐẢO NGƯỢC giá trị
    isCompleted = (isCompleted === 'false'); // Đảo ngược giá trị boolean
    listItem.dataset.isCompleted = isCompleted.toString(); // Chuyển đổi lại thành chuỗi trước khi lưu vào dataset


    const input = listItem.querySelector('input');
    if (input) {
        if (isCompleted) {
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

// Thêm phương pháp mới vào danh sách phương pháp
function addMethod() {
    const methodList = document.getElementById('methodList');

}


function deleteMethod(button) {
    // Tìm phần tử `li` chứa nút đã nhấn
    const listItem = button.parentElement;

    // Xóa `li` khỏi danh sách
    listItem.remove();
}

window.saveEvent = saveEvent;
window.deleteEvent = deleteEvent;
window.addTodo = addTodo;
window.markAsDone = markAsDone;
window.deleteTodo = deleteTodo;
window.addMethod = addMethod;
window.deleteMethod = deleteMethod;

