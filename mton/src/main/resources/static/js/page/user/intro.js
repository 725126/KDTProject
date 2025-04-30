// 비밀번호 재설정 > 토큰 유효성 검사 실패 시
document.addEventListener("DOMContentLoaded", function () {
    const urlParams = new URLSearchParams(window.location.search);
    const errorType = urlParams.get('error');

    if (errorType === 'invalidToken') {
        const modal = new bootstrap.Modal(document.getElementById('errorModal'));
        modal.show();
    }
});