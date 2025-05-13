// 연락처 입력 마스크
Inputmask({
    mask: "010-9999-9999",
    placeholder: "",        // 언더바 없애기
    showMaskOnHover: false,  // 마우스 오버 시 마스크 안보이게
    showMaskOnFocus: true,   // 포커스하면만 마스크 적용
    autoUnmask: false,       // 입력된 값만 표시
    removeMaskOnSubmit: false, // 폼 제출할 때 마스크 유지
    jitMasking: true         // 입력한 만큼만 마스크 적용 (✨핵심!)
}).mask(".phone-mask");

// 연락처 입력 검증
const phoneInput = document.getElementById('uPhone');
const phoneCheckResult = document.getElementById('phoneCheckResult');

let isPhoneChecked = false;

function validatePhone() {
    const phone = phoneInput.value.trim();

    // 기본 정규식 (010, 011, 016~019 / 3~4자리 / 4자리)
    const phoneRegex = /^01([0|1|6|7|8|9])-(\d{3,4})-(\d{4})$/;

    if (phone.length === 0) {
        phoneCheckResult.textContent = "";
        return;
    }

    if (!phoneRegex.test(phone)) {
        phoneCheckResult.textContent = "❌ 연락처 형식이 올바르지 않습니다.";
        phoneCheckResult.style.color = "red";
        isPhoneChecked = false;
    } else {
        phoneCheckResult.textContent = "";
        isPhoneChecked = true;
    }
}

// 마스킹 썼으므로 input + keyup 둘 다 걸기
phoneInput.addEventListener('keyup', validatePhone);
phoneInput.addEventListener('input', validatePhone);

function submitFormValidatePhone(event) {
    validatePhone();

    if (!isPhoneChecked) {
        event.preventDefault();
        phoneInput.focus();
        return false;
    }

    return true;
}


