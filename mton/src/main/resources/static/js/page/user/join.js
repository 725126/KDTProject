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

// 사업자 번호 입력 마스크
Inputmask({
    mask: "999-99-99999",
    placeholder: "",
    showMaskOnHover: false,
    showMaskOnFocus: true,
    autoUnmask: false,
    removeMaskOnSubmit: false,
    jitMasking: true
}).mask("#pBusinessNo");

// 입력값 검증 >> 오류 시 포커스
window.addEventListener('DOMContentLoaded', function() {
    const errorField = document.querySelector('.is-invalid');
    if (errorField) {
        errorField.scrollIntoView({ behavior: 'smooth', block: 'center' }); // 부드럽게 가운데로 스크롤 이동
        errorField.classList.add('shake'); // 흔들기
        setTimeout(() => {
            errorField.classList.remove('shake'); // 흔들기 효과 끝나면 클래스 제거
        }, 500); // 0.5초 후 제거
        errorField.focus(); // 마지막으로 포커스도 잡아줌
    }
});

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

// 사업자등록번호 검증
const businessInput = document.getElementById('pBusinessNo');
const businessCheckResult = document.getElementById('businessCheckResult');
const checkBusinessBtn = document.getElementById('checkBusinessNoDuplicateBtn');

let isBusinessChecked = false;

// 1️⃣ 사업자등록번호 유효성 검증
function isValidBusinessNoFormat(value) {
    const multiply = [1, 3, 7, 1, 3, 7, 1, 3, 5];
    let sum = 0;

    for (let i = 0; i < 9; i++) {
        sum += parseInt(value.charAt(i)) * multiply[i];
    }
    sum += Math.floor((parseInt(value.charAt(8)) * 5) / 10);

    const checkDigit = (10 - (sum % 10)) % 10;
    return checkDigit === parseInt(value.charAt(9));
}

// 2️⃣ 실시간 형식 검증
function validateBusinessNo() {
    const value = businessInput.value.replace(/-/g, ''); // 하이픈(-) 제거

    isBusinessChecked = false;

    if (value.length !== 10 || isNaN(value)) {
        businessCheckResult.textContent = "❌ 올바른 사업자 등록번호 형식이 아닙니다.";
        businessCheckResult.style.color = "red";
        checkBusinessBtn.disabled = true;
        return;
    }

    if (!isValidBusinessNoFormat(value)) {
        businessCheckResult.textContent = "❌ 유효하지 않은 사업자 등록번호입니다.";
        businessCheckResult.style.color = "red";
        checkBusinessBtn.disabled = true;
    } else {
        businessCheckResult.textContent = "사업자등록번호 중복 확인을 해주세요.";
        businessCheckResult.style.color = "gray";
        checkBusinessBtn.disabled = false;
    }
}

if (businessInput != null) {
    // 마스킹 썼으므로 input + keyup 둘 다 걸기
    businessInput.addEventListener('input', validateBusinessNo);
    businessInput.addEventListener('keyup', validateBusinessNo);
}

// 3️⃣ 중복확인 버튼 클릭 시
if (checkBusinessBtn != null) {
    checkBusinessBtn.addEventListener('click', function () {
        const raw = businessInput.value.trim();

        // ✅ 서버에 중복 확인 요청
        fetch(`/check/business-no?businessNo=${encodeURIComponent(raw)}`)
            .then(response => response.json())
            .then(available => {
                if (available) {
                    businessCheckResult.textContent = "✅ 사용 가능한 사업자등록번호입니다.";
                    businessCheckResult.style.color = "green";
                    isBusinessChecked = true;
                } else {
                    businessCheckResult.textContent = "❌ 이미 사용 중인 사업자등록번호입니다.";
                    businessCheckResult.style.color = "red";
                    isBusinessChecked = false;
                }
            })
            .catch(error => {
                console.error("Error:", error);
                businessCheckResult.textContent = "❌ 서버 오류가 발생했습니다.";
                businessCheckResult.style.color = "red";
                isBusinessChecked = false;
            });
    });
}

// 이름 & 회사명 검증
let isNameChecked = false;
let isCompanyChecked = false;

function validateTextField(inputElement, resultElement, type) {
    const value = inputElement.value.trim();

    if (value.length === 0) {
        resultElement.textContent = "❌ 필수 입력입니다.";
        resultElement.style.color = "red";
        updateCheckStatus(type, false);
        return false;
    }

    const hasOnlyChosung = /^[ㄱ-ㅎ]+$/.test(value);
    const validPattern = /^[a-zA-Z가-힣]{2,20}$/.test(value);

    if (hasOnlyChosung) {
        resultElement.textContent = "❌ 초성만 입력할 수 없습니다.";
        resultElement.style.color = "red";
        updateCheckStatus(type, false);
        return false;
    } else if (!validPattern) {
        resultElement.textContent = "❌ 영문 또는 한글로 2자 이상 20자 이하여야 합니다.";
        resultElement.style.color = "red";
        updateCheckStatus(type, false);
        return false;
    } else {
        resultElement.textContent = "";
        updateCheckStatus(type, true);
        return true;
    }
}

// 상태 업데이트 함수
function updateCheckStatus(type, status) {
    if (type === 'name') {
        isNameChecked = status;
    } else if (type === 'company') {
        isCompanyChecked = status;
    }
}

// 이름 검증
const nameInput = document.getElementById('uName');
const nameCheckResult = document.getElementById('nameCheckResult');

nameInput.addEventListener('input', function() {
    validateTextField(nameInput, nameCheckResult, 'name');
});
nameInput.addEventListener('keyup', function() {
    validateTextField(nameInput, nameCheckResult, 'name');
});

// 회사명 검증
const companyInput = document.getElementById('pCompany');
const companyCheckResult = document.getElementById('companyCheckResult');

if (companyInput != null) {
    companyInput.addEventListener('input', function() {
        validateTextField(companyInput, companyCheckResult, 'company');
    });
    companyInput.addEventListener('keyup', function() {
        validateTextField(companyInput, companyCheckResult, 'company');
    });
}

const joinForm = document.getElementById('joinForm');

// ✅ 회원가입 버튼 클릭 시 입력 값 체크
joinForm.addEventListener('submit', function(event) {

    if (!submitFormValidateEmail(event)) {
        return;
    }

    // 비밀번호 검증 함수 호출
    if (!submitFormValidatePassword(event)) {
        return;
    }

    // 담당자 이름
    if (!isNameChecked) {
        event.preventDefault();
        nameInput.focus();
        return;
    }

    // 연락처
    if (!isPhoneChecked) {
        event.preventDefault();
        phoneInput.focus();
        return;
    }

    // 사업자등록번호 (optional)
    if (businessInput != null && !isBusinessChecked) {
        event.preventDefault();
        businessInput.focus();
        return;
    }

    // 회사명 (optional)
    if (companyInput != null && !isCompanyChecked) {
        event.preventDefault();
        companyInput.focus();
        return;
    }

    // 여기까지 아무것도 걸리지 않으면 submit 정상 진행

    // 주소 조합
    const postcode = document.getElementById('postcode').value.trim();
    const address = document.getElementById('address').value.trim();
    const detailAddress = document.getElementById('detailAddress').value.trim();
    const extraAddress = document.getElementById('extraAddress').value.trim();

    // 💡 주소 조합: [12345] 서울시 강남구 테헤란로 123 5층 LG트윈타워
    const fullAddress = [
        postcode ? `[${postcode}]` : '',
        address,
        detailAddress,
        extraAddress
    ]
        .filter(part => part !== '')
        .join(' ');

    document.getElementById('pAddr').value = fullAddress;
});


