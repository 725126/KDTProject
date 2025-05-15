import {
    insertFileBtnTutorial,
    insertUploadBtnTutorial,
    editRefreshBtnTutorial,
    editUploadBtnTutorial,
    viewRefreshBtnTutorial,
    viewDownloadBtnTutorial
} from '/js/page/operation/module/tmessage.js';

const tooltipMap = {
    "insert-file-label": insertFileBtnTutorial,
    "insert-upload": insertUploadBtnTutorial,
    "edit-refresh": editRefreshBtnTutorial,
    "edit-upload": editUploadBtnTutorial,
    "view-refresh": viewRefreshBtnTutorial,
    "view-download": viewDownloadBtnTutorial
};

// 각 버튼에 툴팁 속성 주입
Object.entries(tooltipMap).forEach(([id, message]) => {
    const el = document.getElementById(id);
    if (el) {
        // el.setAttribute("title", text);
        el.setAttribute("data-bs-toggle", "tooltip");
        el.setAttribute("data-bs-placement", "left");
        el.setAttribute("data-bs-html", "true"); // 줄바꿈 지원
        el.setAttribute("data-bs-title", message); // title 대신 이걸 사용
    }
});

// Bootstrap 툴팁 활성화
document.querySelectorAll('[data-bs-toggle="tooltip"]').forEach(el => {
    new bootstrap.Tooltip(el, {
        customClass: 'tooltip-custom'
    });
});
