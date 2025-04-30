// 비밀번호 확인 입력창 검증
const password = document.getElementById('password');
const passwordConfirm = document.getElementById('passwordConfirm');
const passwordStrength = document.getElementById('passwordStrength');
const passwordError = document.getElementById('passwordError');
const submitButton = document.querySelector('button[type="submit"]');

// 비밀번호 입력 체크
let isPasswordChecked = false;

// 비밀번호 확인 일치 체크
let isPasswordConfirmChecked = false;

function validatePassword() {
    const value = password.value;
    let strength = "";

    const hasNumber = /[0-9]/.test(value);
    const hasLetter = /[A-Za-z]/.test(value);
    const hasSpecialChar = /[^A-Za-z0-9]/.test(value);
    const isLengthValid = value.length >= 5 && value.length <= 16;

    if (!isLengthValid) {
        strength = "❌ 5~16자 이내로 입력해주세요.";
        passwordStrength.style.color = "red";
        submitButton.disabled = true;
        isPasswordChecked = false;
    } else if (hasSpecialChar) {
        strength = "❌ 특수문자 없이 입력해주세요.";
        passwordStrength.style.color = "red";
        submitButton.disabled = true;
        isPasswordChecked = false;
    } else if (hasLetter && hasNumber) {
        strength = "✅ 사용 가능한 비밀번호입니다.";
        passwordStrength.style.color = "green";
        submitButton.disabled = false; // 사용 가능하면 버튼 활성화
        isPasswordChecked = true;
    } else {
        strength = "❌ 영문+숫자를 모두 포함해주세요.";
        passwordStrength.style.color = "red";
        submitButton.disabled = true;
        isPasswordChecked = false;
    }

    passwordStrength.textContent = strength;
}

// 비밀번호 입력할 때마다 강도 체크 + 버튼 상태 업데이트
password.addEventListener('input', validatePassword);

// 비밀번호 확인 일치 여부 체크
passwordConfirm.addEventListener('input', function() {
    if (password.value !== passwordConfirm.value) {
        passwordError.textContent = "❌ 비밀번호가 일치하지 않습니다.";
        passwordError.style.color = "red";
        isPasswordConfirmChecked = false;
    } else {
        passwordError.textContent = "";
        isPasswordConfirmChecked = true;
    }
});

function submitFormValidatePassword(event) {
    if (!isPasswordChecked) {
        event.preventDefault();
        password.focus();
        return false;
    }

    if (!isPasswordConfirmChecked) {
        event.preventDefault();
        passwordConfirm.focus();
        return false;
    }

    return true;
}


