const { app, BrowserWindow, ipcMain } = require('electron');
const path = require('path');
let win;

function createWindow() {
    win = new BrowserWindow({
        width: 1920,
        height: 1080,
        fullscreenable: true,
        webPreferences: {
            nodeIntegration: false,
            contextIsolation: true,
        },
    });



    win.loadURL('file://' + path.join(__dirname, 'html/home.html'));
}

ipcMain.on('load-new-page', (event, page) => {
    // Nhận thông điệp từ renderer và tải trang mới
    win.loadURL('file://' + path.join(__dirname, 'html/' + page)).catch(err => {
        console.error("Lỗi tải trang:", err);
    });
});

app.whenReady().then(() => {
    createWindow();

    app.on('activate', () => {
        if (BrowserWindow.getAllWindows().length === 0) {
            createWindow();
        }
    });
});

app.on('window-all-closed', () => {
    if (process.platform !== 'darwin') {
        app.quit();
    }
});