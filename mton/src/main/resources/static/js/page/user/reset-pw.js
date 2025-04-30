document.addEventListener('DOMContentLoaded', function () {
    const resetPwForm = document.getElementById('resetPwForm');

    resetPwForm.addEventListener('submit', function(event) {
        // 비밀번호 검증 함수 호출
        if (!submitFormValidatePassword(event)) return;
    });
});