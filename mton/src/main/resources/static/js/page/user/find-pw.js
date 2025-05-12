const resetPasswordForm = document.getElementById('resetPasswordForm');
const resendLink = document.getElementById('resendLink');
const resetMessage = document.getElementById('resetMessage');

let isSending = false;

// 상태에 따라 버튼/링크 활성화 제어
const submitBtn = resetPasswordForm.querySelector('button[type="submit"]');
const spinner = submitBtn.querySelector('.spinner-border');

function setSendingState(state) {
    isSending = state;
    submitBtn.disabled = state;
    resendLink.style.pointerEvents = state ? 'none' : 'auto';
    resendLink.style.opacity = state ? '0.5' : '1.0';

    // 스피너 표시 및 버튼 텍스트 변경
    if (state) {
        spinner.classList.remove('d-none');
        submitBtn.lastChild.textContent = ' 이메일 전송 중...'; // 텍스트만 변경
    } else {
        spinner.classList.add('d-none');
        submitBtn.lastChild.textContent = ' 이메일 전송'; // 원상 복귀
    }
}

function sendResetEmail(triggerSource = 'form') {
    const email = document.getElementById('uEmail').value.trim();

    if (!email) {
        resetMessage.textContent = "이메일을 입력해주세요.";
        resetMessage.style.color = "red";
        return;
    }

    if (isSending) return;
    setSendingState(true);

    resetMessage.textContent = triggerSource === 'form'
        ? "📨 이메일을 보내는 중입니다... 잠시만 기다려주세요."
        : "📨 이메일을 다시 보내는 중입니다...";
    resetMessage.style.color = "#1E90FF";

    fetch('/find/pw', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json'
        },
        body: JSON.stringify({ email })
    })
        .then(response => {
            if (!response.ok) throw new Error('네트워크 응답 오류');
            return response.json();
        })
        .then(data => {
            if (data.success) {
                resetMessage.textContent = triggerSource === 'form'
                    ? "✅ 비밀번호 재설정 링크가 메일로 발송되었습니다."
                    : "✅ 메일이 재발송되었습니다.";
                resetMessage.style.color = "green";
            } else if (!data.success) {
                resetMessage.textContent = "❌ 가입된 이메일이 아닙니다.";
                resetMessage.style.color = "red";
            }
        })
        .catch(error => {
            console.error('에러 발생:', error);
            resetMessage.textContent = "❌ 오류가 발생했습니다. 다시 시도해주세요.";
            resetMessage.style.color = "red";
        })
        .finally(() => {
            setSendingState(false); // 무조건 복원
        });
}

resetPasswordForm.addEventListener('submit', function (event) {
    event.preventDefault();

    if (!submitFormValidateEmail(event)) {
        return;
    }

    sendResetEmail('form');
});

resendLink.addEventListener('click', function (e) {
    e.preventDefault();

    if (!submitFormValidateEmail(e)) {
        return;
    }

    sendResetEmail('resend');
});