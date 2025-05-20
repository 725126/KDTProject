let isNameChecked = false;
let isCompanyChecked = false;

function validateTextField(inputElement, resultElement, type) {
    console.log("validateTextField: " + inputElement);

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

function submitFormValidateName(event) {
    console.log("submitFormValidateName: " + nameInput);

    const isNameValid = validateTextField(nameInput, nameCheckResult, 'name');
    // 담당자 이름

    if (!isNameValid) {
        event.preventDefault();
        nameInput.focus();

        return false;
    }

    if (companyInput != null) {
        const isCompanyValid = validateTextField(companyInput, companyCheckResult, 'company');

        // 회사명 (optional)
        if (!isCompanyValid) {
            event.preventDefault();
            companyInput.focus();
            return false;
        }
    }

    return true;
}

