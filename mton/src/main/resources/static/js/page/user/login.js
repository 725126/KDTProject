document.addEventListener('DOMContentLoaded', function() {
    const uEmailInput = document.getElementById('uEmail');
    const saveIdCheckbox = document.getElementById('saveId');

    // 페이지 로딩될 때 localStorage에 저장된 아이디가 있으면 불러오기
    const savedEmail = localStorage.getItem('savedEmail');
    if (savedEmail) {
        uEmailInput.value = savedEmail;
        saveIdCheckbox.checked = true;
    }

    // 로그인 버튼 누를 때 저장 여부 체크
    const loginForm = document.getElementById('loginForm');
    loginForm.addEventListener('submit', function() {
        if (saveIdCheckbox.checked) {
            localStorage.setItem('savedEmail', uEmailInput.value);
        } else {
            localStorage.removeItem('savedEmail');
        }
    });

    const alert = new URLSearchParams(window.location.search).get("alert");

    if (alert != null) {
        const alertMap = {
            resetSuccess: {
                headerClass: "bg-success text-white",
                title: "✅ 비밀번호 변경 완료",
                message: "비밀번호가 성공적으로 변경되었습니다.<br>새 비밀번호로 로그인해주세요.",
            },

            joinSuccess: {
                headerClass: "bg-success text-white",
                title: "✅ 회원가입 완료",
                message: "회원가입이 완료되었습니다.<br>관리자 승인 후 로그인 가능합니다."
            }
        };

        if (alert && alertMap[alert]) {
            const config = alertMap[alert];
            document.getElementById("alertModalHeader").className = `modal-header ${config.headerClass}`;
            document.getElementById("alertModalLabel").textContent = config.title;
            document.getElementById("alertModalBody").innerHTML = config.message;

            const modal = new bootstrap.Modal(document.getElementById("alertModal"));
            modal.show();
        }
    }
});