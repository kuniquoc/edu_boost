const API_BASE_URL = 'http://localhost:8080/api';


//#region Login, Register, Logout

function addLoginModal() {
    const loginModal = document.getElementById("loginModal");
    loginModal.innerHTML = `
        <div class="modal-content">
            <span class="close" id="close-login" onclick="hideLoginModal()">&times;</span>
            <h2 class="form-name">Đăng nhập</h2>
            <form id="loginForm" class="logForm">
                <div class="form-group">
                    <label for="username">Tên đăng nhập</label>
                    <input type="text" id="username-login" name="username" placeholder="Nhập tên đăng nhập" required>
                </div>
                <div class="form-group">
                    <label for="password">Mật khẩu</label>
                    <input type="password" id="password-login" name="password" placeholder="Nhập mật khẩu" required>
                </div>
                <button type="submit" id="submitLogin" class="submit">Đăng nhập</button>
                <p id="error-message1" class="error-message"></p>
            </form>
            <div class="changeModal">
                <p>Chưa có tài khoản? </p>
                <a href="javascript:void(0);" id="registerButton" onclick="changeToRegister()">Đăng ký</a>
            </div>
        </div>
    `;
}

function changeToRegister() {
    showRegisterModal();
    document.getElementById("registerForm").addEventListener("submit", register);
}

async function login(e) {
    e.preventDefault();
    const username_login = document.getElementById('username-login');
    const password_login = document.getElementById('password-login');

    const username = username_login.value;
    const password = password_login.value;

    username_login.value = '';
    password_login.value = '';

    const loginData = {
        username: username,
        password: password
    };

    try {
        const response = await fetch(API_BASE_URL + '/auth/signin', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify(loginData)
        });

        let result;
        const contentType = response.headers.get("content-type");

        if (contentType && contentType.includes("application/json")) {
            result = await response.json();
        } else {
            result = await response.text();
        }

        if (response.ok) {
            // Lưu token vào sessionStorage sau khi đăng nhập thành công
            sessionStorage.setItem('token', result.data.token);
            // Lưu quyền của user vào sessionStorage
            sessionStorage.setItem('roles', JSON.stringify(result.data.roles));
        } else {
            if (response.status === 404) {
                const errorMessage = document.getElementById("error-message1");
                errorMessage.innerText = 'Tài khoản không tồn tại.';
                return false;
            } else {
                const errorMessage = document.getElementById("error-message1");
                errorMessage.innerText = 'Đã có lỗi xảy ra! Vui lòng thử lại sau.';
                return false;
            }
        }
    } catch (error) {
        console.error('Error:', error);
        const errorMessage = document.getElementById("error-message1");
        errorMessage.innerText = 'Đã có lỗi xảy ra! Vui lòng thử lại sau.';
        return false;
    }

    hideLoginModal();

    const btnLogin = document.getElementById("btnLogin");
    const btnRegister = document.getElementById("btnRegister");
    const btnLogout = document.getElementById("btnLogout");
    const userAvatar = document.getElementById("userAvatar");

    btnLogin.style.display = "none";
    btnRegister.style.display = "none";
    btnLogout.style.display = "inline-block";
    userAvatar.style.display = "inline-block";

    // Lưu lại header sau khi đăng nhập
    sessionStorage.setItem("headerContent", document.getElementById("header").innerHTML);

    return true;
}

function addRegisterModal() {
    const registerModal = document.getElementById("registerModal");
    registerModal.innerHTML = `
        <div class="modal-content">
            <span class="close" id="close-register" onclick="hideRegisterModal()">&times;</span>
            <h2 class="form-name">Đăng ký</h2>
            <form id="registerForm" class="logForm">
                <div class="form-group">
                    <label for="username">Tên đăng nhập</label>
                    <input type="text" id="username-register" name="username" placeholder="Nhập tên đăng nhập" required>
                </div>
                <div class="form-group">
                    <label for="password">Mật khẩu</label>
                    <input type="password" id="password-register" name="password" placeholder="Nhập mật khẩu" required>
                </div>
                <div class="form-group">
                    <label for="confirmPassword">Xác nhận mật khẩu</label>
                    <input type="password" id="confirmPassword" name="confirmPassword" placeholder="Nhập lại mật khẩu" required>
                </div>
                <button type="submit" id="submitRegister" class="submit">Đăng ký</button>
                <p id="error-message2" class="error-message"></p>
                <p id="success-message2" class="success-message"></p>
            </form>
            <div class="changeModal">
                <p>Đã có tài khoản? </p>
                <a href="javascript:void(0);" id="loginButton" onclick="changeToLogin()">Đăng nhập</a>
            </div>
        </div>
    `;
}

function changeToLogin() {
    showLoginModal();
    document.getElementById("loginForm").addEventListener("submit", login);
}

async function register(e) {
    e.preventDefault();

    const username_register = document.getElementById('username-register');
    const password_register = document.getElementById('password-register');
    const confirmPassword_register = document.getElementById('confirmPassword');

    const username = username_register.value;
    const password = password_register.value;
    const confirmPassword = confirmPassword_register.value;

    username_register.value = '';
    password_register.value = '';
    confirmPassword_register.value = '';

    if (password !== confirmPassword) {
        const errorMessage = document.getElementById("error-message2");
        errorMessage.innerText = 'Mật khẩu không khớp.';
        return;
    }

    const registerData = {
        username: username,
        password: password
    };

    try {
        const response = await fetch(API_BASE_URL + '/auth/signup', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify(registerData)
        });

        let result;
        const contentType = response.headers.get("content-type");

        if (contentType && contentType.includes("application/json")) {
            result = await response.json();
        } else {
            result = await response.text();
        }

        if (response.status === 201) {
            document.getElementById('success-message2').innerText = 'Đăng ký thành công!';

            // Delay 2s khi đăng ký thành công, rồi hiển thị form đăng nhập
            setTimeout(function () {
                showLoginModal();
            }, 2000);
        } else {
            if (response.status === 409) {
                const errorMessage = document.getElementById("error-message2");
                errorMessage.innerText = 'Tên người dùng đã tồn tại.';
                return false;
            } else {
                const errorMessage = document.getElementById("error-message2");
                errorMessage.innerText = 'Đã có lỗi xảy ra.';
                return false;
            }
        }
    } catch (error) {
        console.error('Error:', error);
        const errorMessage = document.getElementById("error-message2");
        errorMessage.innerText = 'Đã có lỗi xảy ra! Vui lòng thử lại sau.';
        return false;
    }

    showLoginModal();

    return true;
}


function showLoginModal() {
    hideRegisterModal();
    const loginModal = document.getElementById("loginModal");
    loginModal.style.display = "flex";
}

function hideLoginModal() {
    const modal = document.getElementById("loginModal");
    modal.style.display = "none";
}

function showRegisterModal() {
    hideLoginModal();
    const registerModal = document.getElementById("registerModal");
    registerModal.style.display = "flex";
}

function hideRegisterModal() {
    const registerModal = document.getElementById("registerModal");
    registerModal.style.display = "none";
}

function logout() {
    sessionStorage.removeItem("token");
    const btnLogin = document.getElementById("btnLogin");
    const btnRegister = document.getElementById("btnRegister");
    const btnLogout = document.getElementById("btnLogout");
    const userAvatar = document.getElementById("userAvatar");

    btnLogin.style.display = "inline-block";
    btnRegister.style.display = "inline-block";
    btnLogout.style.display = "none";
    userAvatar.style.display = "none";

    sessionStorage.removeItem("headerContent");
    sessionStorage.removeItem("roles");
    sessionStorage.removeItem("token");
}

//#endregion

//#region Study Methods
async function getSubjectTypes() {
    try {
        const response = await fetch(API_BASE_URL + '/subject-types');
        const data = await response.json();
        return new Map(data.map(type => [type.id, type.name]));
    } catch (error) {
        console.error('Error:', error);
        return new Map();
    }
}



//#endregion

async function checkRole(role) {
    try {
        const token = sessionStorage.getItem('token');
        if (!token) { return false; }

        const response = await fetch(API_BASE_URL + '/user/role', {
            method: 'GET',
            headers: {
                "Content-Type": "application/json",
                "Authorization": `Bearer ${token}`,
            }
        });

        if (!response.ok) {
            console.error("Lỗi: ", response.statusText);
            return false;
        }
        const data = await response.json();
        const roles = data.data;
        if (roles) {
            return roles.some(r => r.authority === role);
        }
        return false;
    } catch (error) {
        console.error("Lỗi: ", error);
    }
}

function formatDateToYYYYMMDD(date) {
    if (!(date instanceof Date) && typeof date !== 'string') {
        console.error("Input phải là một đối tượng Date hoặc một chuỗi ngày hợp lệ.");
        return null; // Hoặc xử lý lỗi theo cách khác
    }

    let dateObj;
    if (typeof date === 'string') {
        try {
            dateObj = new Date(date);
            if (isNaN(dateObj)) {
                throw new Error("Chuỗi ngày không hợp lệ.");
            }
        } catch (error) {
            console.error("Lỗi khi tạo đối tượng Date:", error.message);
            return null;
        }
    } else {
        dateObj = new Date(date); // Tạo một bản sao để tránh thay đổi đối tượng Date ban đầu
    }


    const year = dateObj.getFullYear();
    const month = String(dateObj.getMonth() + 1).padStart(2, '0');
    const day = String(dateObj.getDate()).padStart(2, '0');

    return `${year}-${month}-${day}`;
}

function getMonday(dateString) {
    const firstDate = new Date(dateString);
    const dayOfWeek = firstDate.getDay(); // 0 (Chủ nhật) đến 6 (Thứ bảy)

    let diff = 1 - dayOfWeek; // Số ngày chênh lệch so với thứ Hai

    if (dayOfWeek === 0) {
        diff = -6; // Nếu là Chủ nhật, lấy thứ Hai của tuần hiện tại (6 ngày trước đó)
    } else if (diff === 0) { // diff === 0 thì firstDate đã là thứ 2 
        return dateString;
    }
    const mondayDate = new Date(firstDate);
    mondayDate.setDate(firstDate.getDate() + diff);
    return formatDateToYYYYMMDD(mondayDate);
}

export {
    API_BASE_URL,
    addLoginModal,
    addRegisterModal,
    getSubjectTypes,
    checkRole,
    formatDateToYYYYMMDD,
    getMonday
}

window.showLoginModal = showLoginModal;
window.hideLoginModal = hideLoginModal;
window.showRegisterModal = showRegisterModal;
window.hideRegisterModal = hideRegisterModal;
window.changeToRegister = changeToRegister;
window.changeToLogin = changeToLogin;
window.login = login;
window.register = register;
window.logout = logout;