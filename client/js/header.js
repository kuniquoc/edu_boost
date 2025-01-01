if (sessionStorage.getItem("headerContent") === null) {
    sessionStorage.setItem("headerContent", `
        <div class="logo">  
            <a href="home.html">
                <img src="../images/Logo.png" alt="logo">
            </a>
        </div>
        <nav class="navigation">
            <div class="link" onload="loadHeader()">
                <a id="link-home" href="home.html">Trang chủ</a>
                <a id="link-schedule" href="schedule.html">Lập lịch</a>
                <a id="link-study-method" href="study_method.html">Phương pháp học</a>
                <a id="link-user-profile" href="user_profile.html">Tài khoản</a>
                <a id="link-dashboard" href="dashboard.html" style="display:none;">Bảng điều khiển</a>
            </div>
        </nav>
        <div class="login-header">
            <button type="button" id="btnLogin" class="btnLog" onclick="showLoginModal()">Đăng nhập</button>
            <button type="button" id="btnRegister" class="btnLog" onclick="showRegisterModal()">Đăng ký</button>
            <button type="button" id="btnLogout" class="btnLog" onclick="logout()" style="display: none">Đăng
                xuất</button>
            <img id="userAvatar" class="user-avatar" src="../images/default-avatar.png" alt="Avatar"
                style="display: none;">
        </div>
        `);
}

function storeHeader() {
    sessionStorage.setItem("headerContent", document.getElementById("header").innerHTML);
}

function loadHeader() {
    const currentFileName = window.location.pathname.split('/').pop();
    const clickedElement = document.getElementById("link-" + currentFileName.split('.')[0].replace("_", "-"));

    if (clickedElement) {
        const navLinks = document.querySelectorAll(".link a");
        navLinks.forEach(link => {
            link.style.color = "#c3c3c3";
            link.style.borderBottom = "none";
        });

        clickedElement.style.color = "#000000";
        clickedElement.style.borderBottom = "2px solid #000000";

        storeHeader();
    }


};

function showDashboard() {
    const roles = JSON.parse(sessionStorage.getItem("roles"));
    roles.forEach(role => {
        if (role.authority === "ROLE_ADMIN") {
            document.getElementById("link-dashboard").style.display = "flex";
            storeHeader();
            return;
        }
    });
}

function hideDashboard() {
    document.getElementById("link-dashboard").style.display = "none";
    storeHeader();
}

export { loadHeader, showDashboard, hideDashboard };