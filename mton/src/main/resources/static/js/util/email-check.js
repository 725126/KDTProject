// 이메일(회원 아이디) 중복 여부 검증
const emailInput = document.getElementById('uEmail');
const emailCheckResult = document.getElementById('emailCheckResult');
const checkEmailDuplicateBtn = document.getElementById('checkEmailDuplicateBtn');
const pwResetMailSendBtn = document.getElementById('pwResetMailSendBtn');

let isEmailChecked = false;

// 이메일 형식 검증 정규식
function isValidEmailFormat(email) {
    const emailRegex = /^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\.[a-zA-Z]{2,}$/;
    return emailRegex.test(email);
}

// 이메일 입력 시: 실시간 형식 검증 안내
emailInput.addEventListener('input', function () {
    const path = window.location.pathname;
    const email = emailInput.value.trim();
    isEmailChecked = false;

    if (email === "") {
        emailCheckResult.textContent = "❌ 이메일을 입력해 주세요.";
        emailCheckResult.style.color = "red";

        if (path === "/join/partner" || path === "/join/inner" || path === "/internal/my/account-edit") {
            checkEmailDuplicateBtn.disabled = true;
        } else if (path === "/find/pw") {
            pwResetMailSendBtn.disabled = true;
        }
    } else if (!isValidEmailFormat(email)) {
        emailCheckResult.textContent = "❌ 이메일 형식이 올바르지 않습니다.";
        emailCheckResult.style.color = "red";

        if (path === "/join/partner" || path === "/join/inner" || path === "/internal/my/account-edit") {
            checkEmailDuplicateBtn.disabled = true;
        } else if (path === "/find/pw") {
            pwResetMailSendBtn.disabled = true;
        }
    } else {
        if (path === "/join/partner" || path === "/join/inner" || path === "/internal/my/account-edit") {
            emailCheckResult.textContent = "이메일 중복 확인을 해주세요.";
            emailCheckResult.style.color = "gray";
            checkEmailDuplicateBtn.disabled = false;
        } else if (path === "/find/pw") {
            emailCheckResult.textContent = "";
            pwResetMailSendBtn.disabled = false;
            isEmailChecked = true;
        }
    }
});

// 중복확인 버튼 클릭 시: fetch 실행
if(checkEmailDuplicateBtn != null) {
    checkEmailDuplicateBtn.addEventListener('click', function () {
        const email = emailInput.value.trim();

        if (email === "") {
            emailCheckResult.textContent = "❌ 이메일을 입력해 주세요.";
            emailCheckResult.style.color = "red";
            return;
        }

        if (!isValidEmailFormat(email)) {
            emailCheckResult.textContent = "❌ 이메일 형식이 올바르지 않습니다.";
            emailCheckResult.style.color = "red";
            return;
        }

        fetch(`/check/email?email=${encodeURIComponent(email)}`)
            .then(response => response.json())
            .then(available => {
                if (available) {
                    emailCheckResult.textContent = "✅ 사용 가능한 이메일입니다.";
                    emailCheckResult.style.color = "green";
                    isEmailChecked = true;
                } else {
                    emailCheckResult.textContent = "❌ 이미 사용 중인 이메일입니다.";
                    emailCheckResult.style.color = "red";
                    isEmailChecked = false;
                }
            })
            .catch(error => {
                console.error('Error:', error);
                emailCheckResult.textContent = "❌ 오류가 발생했습니다.";
                emailCheckResult.style.color = "red";
                isEmailChecked = false;
            });
    });
}

function submitFormValidateEmail(event) {
    if (!isEmailChecked) {
        event.preventDefault();
        emailInput.focus();
        return false;
    }

    return true;
}