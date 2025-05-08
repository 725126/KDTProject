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
    // 사업자등록번호 > 하이픈(-) 제거
    const value = businessInput.value.replace(/-/g, '');
    // url
    const path = window.location.pathname;

    isBusinessChecked = false;

    if (value.length !== 10 || isNaN(value)) {
        businessCheckResult.textContent = "❌ 올바른 사업자 등록번호 형식이 아닙니다.";
        businessCheckResult.style.color = "red";
        checkBusinessBtnDisabled(true);
        return;
    }

    if (!isValidBusinessNoFormat(value)) {
        businessCheckResult.textContent = "❌ 유효하지 않은 사업자 등록번호입니다.";
        businessCheckResult.style.color = "red";
        checkBusinessBtnDisabled(true);
    } else {
        if (path === "/join/partner") {
            businessCheckResult.textContent = "사업자등록번호 중복 확인을 해주세요.";
            businessCheckResult.style.color = "gray";
            checkBusinessBtnDisabled(false);
        } else { // 아이디 찾기
            businessCheckResult.textContent = "";
            isBusinessChecked = true;
        }
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

// form 제출 시 입력값 유효성 & 중복 확인 여부 검사
function submitFormValidateBusinessNo(event) {
    if (businessInput != null && !isBusinessChecked) {
        event.preventDefault();
        businessInput.focus();
        return false;
    }

    return true;
}

function checkBusinessBtnDisabled(result) {
    if (checkBusinessBtn != null){
        checkBusinessBtn.disabled = result;
    }
}