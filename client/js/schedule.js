import {
    API_BASE_URL,
    addLoginModal,
    addRegisterModal,
    formatDateToYYYYMMDD,
    getMonday
} from "../global.js";

import { loadHeader, showDashboard, hideDashboard } from "./header.js";

document.addEventListener("DOMContentLoaded", async () => {
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
            window.location.reload();
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
    // Hàm cập nhật hiển thị tuần
    function updateWeekDisplay() {
        const weekDisplay = document.getElementById("week-display");
        const currentWeekStartField = document.getElementById("current-week-start");
        const currentWeekStart = new Date(currentWeekStartField.value);
        const currentWeekEnd = new Date(currentWeekStart);
        currentWeekEnd.setDate(currentWeekStart.getDate() + 6);

        const options = { year: 'numeric', month: 'long', day: 'numeric' };
        weekDisplay.textContent = `${currentWeekStart.toLocaleDateString('vi-VN', options)} - ${currentWeekEnd.toLocaleDateString('vi-VN', options)}`;
        viewSchedule(document.getElementById("schedule-id").value, document.getElementById("schedule-name-title").textContent);
    }
    window.updateWeekDisplay = updateWeekDisplay;

    const prevWeekButton = document.getElementById("prev-week");
    const nextWeekButton = document.getElementById("next-week");
    const currentWeekStartField = document.getElementById("current-week-start");
    // Điều hướng tuần trước
    prevWeekButton.addEventListener("click", () => {
        const currentWeekStart = new Date(currentWeekStartField.value);
        currentWeekStart.setDate(currentWeekStart.getDate() - 7);
        currentWeekStartField.value = formatDateToYYYYMMDD(currentWeekStart);
        updateWeekDisplay();
    });

    // Điều hướng tuần sau
    nextWeekButton.addEventListener("click", () => {
        const currentWeekStart = new Date(currentWeekStartField.value);
        currentWeekStart.setDate(currentWeekStart.getDate() + 7);
        currentWeekStartField.value = formatDateToYYYYMMDD(currentWeekStart);
        updateWeekDisplay();
    });



    // Lấy và hiển thị danh sách lịch trình
    await fetchAndDisplaySchedules();

    const scheduleId = new URLSearchParams(window.location.search).get('scheduleId');
    if (scheduleId) {
        const groupEvent = document.querySelector(`.group-event[data-schedule-id="${scheduleId}"]`);
        const scheduleName = groupEvent.dataset.name;
        if (!currentWeekStartField.value) {
            currentWeekStartField.value = getMonday(groupEvent.dataset.startDate); // Lấy ngày bắt đầu của tuần chứa sự kiện được chọn
        }
        viewSchedule(scheduleId, scheduleName);
    } else {
        // Lấy và hiển thị dữ liệu lịch trình đầu tiên nếu có
        const firstGroupEvent = document.querySelector('.group-events .group-event');
        if (!firstGroupEvent) {
            currentWeekStartField.value = getMonday(formatDateToYYYYMMDD(new Date())); // Lấy ngày bắt đầu của tuần hiện tại
        }
        else {
            const firstScheduleId = firstGroupEvent.dataset.scheduleId;
            const firstStartDate = firstGroupEvent.dataset.startDate;
            const firstName = firstGroupEvent.dataset.name;

            document.getElementById("schedule-id").value = firstScheduleId;

            if (!currentWeekStartField.value) {
                currentWeekStartField.value = getMonday(firstStartDate); // Lấy ngày bắt đầu của tuần chứa sự kiện đầu tiên
            }
            updateWeekDisplay();
            viewSchedule(firstScheduleId, firstName);
        }
    }


    const calendarBody = document.querySelector(".calendar-body");
    calendarBody.scrollTop = 7 * 35; // Cuộn tới vị trí mong muốn

    // Gắn sự kiện click vào events-grid
    const eventsGrid = document.querySelector('.events-grid');
    eventsGrid.addEventListener('click', addEvent);

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

function showListSchedule() {
    const sidebar = document.querySelector(".sidebar");
    sidebar.style.display = "block";

    const btnScheduleList = document.getElementById("btn-schedule-list");
    btnScheduleList.removeEventListener("click", showListSchedule);
    btnScheduleList.addEventListener("click", hideListSchedule);
}

function hideListSchedule() {
    const sidebar = document.querySelector(".sidebar");
    sidebar.style.display = "none";

    const btnScheduleList = document.getElementById("btn-schedule-list");
    btnScheduleList.removeEventListener("click", hideListSchedule);
    btnScheduleList.addEventListener("click", showListSchedule);
}

function addSchedule() {
    // Elements
    const modal = document.getElementById("schedule-modal");
    const closeModal = document.getElementById("close-modal");
    const scheduleForm = document.getElementById("schedule-form");
    const scheduleNameInput = document.getElementById("schedule-name");


    modal.style.display = "flex";
    scheduleNameInput.value = ""; // Reset input field

    // Close the modal
    closeModal.addEventListener("click", () => {
        modal.style.display = "none";
    });

    // Close modal when clicking outside of it
    window.addEventListener("click", (event) => {
        if (event.target === modal) {
            modal.style.display = "none";
        }
    });

    // Handle form submission
    scheduleForm.addEventListener("submit", async (event) => {
        event.preventDefault(); // Prevent default form submission behavior

        const token = sessionStorage.getItem("token");
        if (!token) {
            return;
        }

        const scheduleName = scheduleNameInput.value.trim();
        if (scheduleName) {
            try {
                // Call API to create a new schedule
                const response = await fetch(API_BASE_URL + "/schedules", {
                    method: "POST",
                    headers: {
                        "Content-Type": "application/json",
                        "Authorization": `Bearer ${token}`,
                    },
                    body: scheduleName,
                });

                if (response.ok) {
                    toastr.success("Lịch trình đã được tạo thành công!");
                    setTimeout(() => {
                        window.location.reload();
                    }, 2000);
                } else {
                    toastr.error("Đã xảy ra lỗi khi tạo lịch trình.");
                }
            } catch (error) {
                toastr.error("Lỗi kết nối đến máy chủ.");
            } finally {
                modal.style.display = "none"; // Close the modal
            }
        } else {
            toastr.error("Vui lòng nhập tên lịch trình.");
        }
    });
}

function updateSchedule(scheduleId) {
    // Elements
    const modal = document.getElementById("schedule-modal");
    const closeModal = document.getElementById("close-modal");
    const scheduleForm = document.getElementById("schedule-form");
    const scheduleNameInput = document.getElementById("schedule-name");
    const scheduleFormName = document.getElementById("schedule-form-name");

    modal.style.display = "flex";
    scheduleFormName.textContent = "Cập nhật lịch trình";
    scheduleNameInput.value = ""; // Reset input field

    // Close the modal
    closeModal.addEventListener("click", () => {
        modal.style.display = "none";
    });

    // Close modal when clicking outside of it
    window.addEventListener("click", (event) => {
        if (event.target === modal) {
            modal.style.display = "none";
        }
    });

    // Handle form submission
    scheduleForm.addEventListener("submit", async (event) => {
        event.preventDefault(); // Prevent default form submission behavior

        const token = sessionStorage.getItem("token");
        if (!token) {
            return;
        }

        const scheduleName = scheduleNameInput.value.trim();
        if (scheduleName) {
            try {
                // Call API to create a new schedule
                const response = await fetch(API_BASE_URL + `/schedules/${scheduleId}`, {
                    method: "PUT",
                    headers: {
                        "Content-Type": "application/json",
                        "Authorization": `Bearer ${token}`,
                    },
                    body: scheduleName,
                });

                if (response.ok) {
                    toastr.success("Lịch trình đã được cập nhật thành công!");
                    setTimeout(() => {
                        window.location.reload();
                    }, 2000);
                } else {
                    toastr.error("Đã xảy ra lỗi khi cập nhật lịch trình.");
                }
            } catch (error) {
                toastr.error("Lỗi kết nối đến máy chủ.");
            } finally {
                modal.style.display = "none"; // Close the modal
            }
        } else {
            toastr.error("Vui lòng nhập tên lịch trình.");
        }
    });
}

function printSchedule(scheduleId) {
    const groupEvent = document.querySelector(`.group-event[data-schedule-id="${scheduleId}"]`);

    // Xử lý lỗi: Kiểm tra xem groupEvents có tồn tại hay không
    if (!groupEvent) {
        console.error("Không tìm thấy phần tử '.group-event'.");
        return;
    }

    // Xử lý lỗi: Kiểm tra xem các phần tử cần thiết có tồn tại trước khi truy cập
    const eventSubject = groupEvent.querySelector('.group-event-subject')?.innerText || "Không có tiêu đề";
    const eventDay = groupEvent.querySelector('.group-event-day')?.innerText || "Không có ngày";


    const printWindow = window.open('', '', 'height=600,width=800');

    if (!printWindow) {
        console.error("Không thể mở cửa sổ in.");
        return; // Thoát nếu không thể mở cửa sổ
    }

    // Tạo HTML để in - Chỉ in sự kiện được chọn, không phải toàn bộ lịch
    let printContent = `<!DOCTYPE html>
                        <html><head>
                        <title>In Lịch</title>
                        <link rel="stylesheet" href="../css/print_schedule.css">
                        </head><body>`;

    printContent += `<div class="print-container">`;
    printContent += `<div class="print-title">`;
    printContent += `<h1 id="eventSubject">${eventSubject}</h1>`;
    printContent += `<h2 id="eventDay">${eventDay}</h2>`;
    printContent += `</div>`;

    // Sao chép sự kiện để in mà không ảnh hưởng đến DOM
    const calendarGrid = document.querySelector('.calendar-grid');
    printContent += calendarGrid.cloneNode(true).outerHTML;


    printContent += `</div></body></html>`;

    printWindow.document.write(printContent);
    printWindow.document.close();

    printWindow.onload = function () {
        printWindow.print();
    };
}

function deleteSchedule(scheduleId) {
    if (confirm('Bạn có chắc chắn muốn xóa lịch trình này không?')) {
        try {
            const token = sessionStorage.getItem("token");
            if (!token) {
                return;
            }

            // Gửi yêu cầu DELETE đến API
            const response = fetch(API_BASE_URL + `/schedules/${scheduleId}`, {
                method: 'DELETE',
                headers: {
                    'Authorization': `Bearer ${token}`,
                },
            });
            toastr.success("Lịch trình đã được xóa thành công!");
            setTimeout(() => {
                window.location.reload();
            }, 2000);
        } catch (error) {
            console.error('Lỗi:', error);
            toastr.error("Đã xảy ra lỗi khi xóa lịch trình.");
        }
    }
}

function viewSchedule(scheduleId, scheduleName) {
    document.getElementById("schedule-id").value = scheduleId;
    const scheduleNameTitle = document.getElementById("schedule-name-title");
    scheduleNameTitle.textContent = scheduleName;

    const currentWeekStart = document.getElementById('current-week-start').value;

    // Gọi hàm fetchScheduleData để lấy dữ liệu lịch trình từ API
    fetchScheduleData(scheduleId, currentWeekStart);
}

// Hàm để lấy và hiển thị danh sách lịch trình
async function fetchAndDisplaySchedules() {
    try {
        const token = sessionStorage.getItem('token');
        if (!token) {
            return;
        }

        const response = await fetch(API_BASE_URL + '/schedules', {
            method: 'GET',
            headers: {
                "Content-Type": "application/json",
                "Authorization": `Bearer ${token}`
            },
        });

        if (!response.ok) {
            throw new Error(`HTTP error! status: ${response.status}`);
        }
        const schedules = await response.json();

        const groupEventsDiv = document.querySelector('.group-events');
        groupEventsDiv.innerHTML = ''; // Xóa nội dung cũ trước khi hiển thị nội dung mới

        schedules.forEach(schedule => {
            const groupEventDiv = document.createElement('div');
            groupEventDiv.className = 'group-event';
            groupEventDiv.dataset.scheduleId = schedule.id;
            groupEventDiv.dataset.startDate = formatDateToYYYYMMDD(new Date(schedule.startDate));
            groupEventDiv.dataset.name = schedule.name;

            const eventSubjectDayDiv = document.createElement('div');
            eventSubjectDayDiv.className = 'event-subject-day';

            const subjectDiv = document.createElement('div');
            subjectDiv.className = 'group-event-subject';
            subjectDiv.textContent = schedule.name;

            const dayDiv = document.createElement('div');
            dayDiv.className = 'group-event-day';
            if (schedule.startDate && schedule.endDate) {
                const startDateString = formatDateToYYYYMMDD(new Date(schedule.startDate));
                const endDateString = formatDateToYYYYMMDD(new Date(schedule.endDate));
                dayDiv.textContent = `${startDateString} đến ${endDateString}`; // Giả sử API trả về 'startDate' và 'endDate'
            }
            else {
                dayDiv.textContent = '';
            }

            eventSubjectDayDiv.appendChild(subjectDiv);
            eventSubjectDayDiv.appendChild(dayDiv);


            const optionDiv = document.createElement('div');
            optionDiv.className = 'option';

            const addBtn = document.createElement('button');
            addBtn.type = 'button';
            addBtn.className = 'icon-btn view-btn';
            addBtn.innerHTML = '<i class="fas fa-eye"></i>';
            addBtn.onclick = function () { viewSchedule(schedule.id, schedule.name) }; // Truyền ID lịch trình vào hàm fetchSchedule

            const updateBtn = document.createElement('button');
            updateBtn.type = 'button';
            updateBtn.className = 'icon-btn update-btn';
            updateBtn.innerHTML = '<i class="fas fa-edit"></i>';
            updateBtn.onclick = function () { updateSchedule(schedule.id); }; // Truyền ID lịch trình vào hàm updateSchedule

            const printBtn = document.createElement('button');
            printBtn.type = 'button';
            printBtn.className = 'icon-btn print-btn';
            printBtn.innerHTML = '<i class="fas fa-print"></i>';
            printBtn.onclick = function () { printSchedule(schedule.id); }; // Truyền ID lịch trình vào hàm printSchedule

            const deleteBtn = document.createElement('button');
            deleteBtn.type = 'button';
            deleteBtn.className = 'icon-btn delete-btn';
            deleteBtn.innerHTML = '<i class="fas fa-trash-alt"></i>';
            deleteBtn.onclick = function () { deleteSchedule(schedule.id); };// thêm hàm xóa lịch


            optionDiv.appendChild(addBtn);
            optionDiv.appendChild(updateBtn);
            optionDiv.appendChild(printBtn);
            optionDiv.appendChild(deleteBtn);


            groupEventDiv.appendChild(eventSubjectDayDiv);
            groupEventDiv.appendChild(optionDiv);
            groupEventsDiv.appendChild(groupEventDiv);
        });
    } catch (error) {
        console.error('Error fetching schedules:', error);
        // Xử lý lỗi, ví dụ: hiển thị thông báo lỗi cho người dùng
        const groupEventsDiv = document.querySelector('.group-events');
        groupEventsDiv.innerHTML = '<p>Error fetching schedules. Please try again later.</p>';
    }
}

function getDayOfWeek(dateString) {
    const date = new Date(dateString);
    const dayOfWeek = date.getDay();
    return dayOfWeek;
}



function timeToMinutes(timeString) {
    const [hours, minutes] = timeString.split(':').map(Number);
    return hours * 60 + minutes;
}

function displayEvent(event) {
    const dayOfWeek = getDayOfWeek(event.date);
    const eventColumn = document.querySelector(`#event-column-${dayOfWeek}`);
    if (!eventColumn) {
        console.error("Không tìm thấy cột sự kiện cho thứ:", dayOfWeek);
        return;
    }


    const eventElement = document.createElement('div');
    eventElement.classList.add('event');
    eventElement.onclick = function () {
        window.location.href = `schedule_event.html?eventId=${event.id}`;
    }

    const startTimeString = `${event.startTime[0].toString().padStart(2, '0')}:${event.startTime[1].toString().padStart(2, '0')}`;
    const endTimeString = `${event.endTime[0].toString().padStart(2, '0')}:${event.endTime[1].toString().padStart(2, '0')}`;
    const startTimeMinutes = timeToMinutes(startTimeString);
    const endTimeMinutes = timeToMinutes(endTimeString);
    const durationMinutes = endTimeMinutes - startTimeMinutes;

    if (durationMinutes <= 0) {
        console.error("Thời lượng sự kiện không hợp lệ:", durationMinutes);
        return;
    }

    eventElement.style.top = `calc(${startTimeMinutes * (35 / 60)}px)`;
    eventElement.style.height = `calc(${durationMinutes * (35 / 60)}px)`;


    const eventSubject = document.createElement('div');
    eventSubject.classList.add('event-subject');
    eventSubject.textContent = event.title;

    const eventTime = document.createElement('div');
    eventTime.classList.add('event-time');
    eventTime.textContent = `${startTimeString} - ${endTimeString}`; // Hiển thị thời gian bắt đầu và kết thúc

    eventElement.appendChild(eventSubject);
    eventElement.appendChild(eventTime);
    eventColumn.appendChild(eventElement);
}

// Hàm để cập nhật các sự kiện từ API
async function fetchScheduleData(scheduleId, startDate) {
    const token = sessionStorage.getItem('token');
    if (!token) {
        return;
    }

    // Xóa tất cả các sự kiện hiện có trên lịch
    const eventColumns = document.querySelectorAll('.events-column');
    eventColumns.forEach(column => {
        column.innerHTML = '';
    });

    try {
        const response = await fetch(API_BASE_URL + `/schedules/${scheduleId}?startDate=${startDate}`, {
            method: 'GET',
            headers: {
                "Content-Type": "application/json",
                "Authorization": `Bearer ${token}`
            },
        });
        const data = await response.json(); // Chuyển dữ liệu trả về thành JSON
        data.forEach(event => {
            displayEvent(event);
        });
    } catch (error) {
        console.error('Error fetching schedule data:', error);
    }
}

function addEvent(event) {
    const eventsGrid = document.querySelector('.events-grid');
    const rect = eventsGrid.getBoundingClientRect();
    const x = event.clientX - rect.left;
    const y = event.clientY - rect.top;

    const columnWidth = eventsGrid.offsetWidth / 7; // Chiều rộng của mỗi cột
    const columnIndex = Math.floor(x / columnWidth); // Chỉ số cột (0 - 6)
    const rowHeight = 35; // Chiều cao của mỗi hàng (giả sử 35px)
    const rowIndex = Math.floor(y / rowHeight); // Chỉ số hàng (0 - 23)

    // Lấy ngày trong tuần từ columnIndex và ngày bắt đầu tuần hiện tại
    const currentWeekStart = new Date(document.getElementById('current-week-start').value);
    const eventDate = new Date(currentWeekStart);
    eventDate.setDate(currentWeekStart.getDate() + columnIndex);
    document.getElementById('event-date').value = formatDateToYYYYMMDD(eventDate);
    document.getElementById('event-start-time').value = `${rowIndex.toString().padStart(2, '0')}:00`;


    const modal = document.getElementById("event-modal");
    const eventForm = document.getElementById("event-form");

    modal.style.display = "flex";
    // Xử lý sự kiện submit form trong modal
    eventForm.addEventListener('submit', function (e) {
        e.preventDefault(); // Ngăn chặn hành vi mặc định của form
        const token = sessionStorage.getItem('token');
        if (!token) {
            return;
        }

        const newEvent = {
            title: document.getElementById('event-title').value,
            date: document.getElementById('event-date').value,
            startTime: document.getElementById('event-start-time').value,
            endTime: document.getElementById('event-end-time').value,
        };

        const scheduleId = document.getElementById('schedule-id').value;

        fetch(API_BASE_URL + `/events/${scheduleId}`, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
                'Authorization': `Bearer ${token}`,
            },
            body: JSON.stringify(newEvent)
        })
            .then(response => {
                if (!response.ok) {
                    throw new Error('Lỗi khi thêm sự kiện');
                }
                return response.json();
            })
            .then(data => {
                displayEvent(data);  // Hiển thị sự kiện mới trên lịch
                closeModal(); // Đóng modal sau khi thêm sự kiện thành công
                window.location.href = `schedule.html?scheduleId=${scheduleId}`; // Chuyển đến trang lịch trình
            })
            .catch(error => {
                console.error('Lỗi:', error);
                // Xử lý lỗi, ví dụ: hiển thị thông báo lỗi cho người dùng
                toastr.error('Đã xảy ra lỗi khi thêm sự kiện.');
            });
    });
}

function closeModal() {
    document.getElementById('event-modal').style.display = 'none';
    // Reset form sau khi đóng modal
    document.getElementById('event-form').reset();

}

window.viewSchedule = viewSchedule;
window.addSchedule = addSchedule;
window.printSchedule = printSchedule;
window.showListSchedule = showListSchedule;
window.hideListSchedule = hideListSchedule;
window.closeModal = closeModal;



