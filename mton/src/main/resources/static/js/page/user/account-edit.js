// 내부회원 공통
const nameNewInput = document.getElementById('uName');
const emailNewInput = document.getElementById('uEmail');
const phoneNewInput = document.getElementById('uPhone');

// 협력업체 전용
const companyNewInput = document.getElementById('pCompany');
const addressNewInput = document.getElementById('address');

const roleSelect = document.getElementById("userRole");

// 버튼
const submitBtn = document.getElementById('accountEditBtn');

// 초기값 저장
const originalValues = {
    name: nameNewInput?.value.trim() || "",
    email: emailNewInput?.value.trim() || "",
    phone: phoneNewInput ? normalizePhone(phoneNewInput.value.trim()) : "",
    company: companyNewInput?.value.trim() || "",
    address: addressNewInput?.value.trim() || "",
    userRole: roleSelect?.value || ""
};

// 폰 번호 마스킹 제거
function normalizePhone(phone) {
    return phone.replace(/[^0-9]/g, '');
}

// 변경 감지 함수
function checkIfFormChanged() {
    const isChanged =
        nameNewInput?.value.trim() !== originalValues.name ||
        emailNewInput?.value.trim() !== originalValues.email ||
        normalizePhone(phoneNewInput?.value.trim()) !== originalValues.phone ||
        companyNewInput?.value.trim() !== originalValues.company ||
        addressNewInput?.value.trim() !== originalValues.address ||
        (roleSelect?.value || "") !== originalValues.userRole;

    submitBtn.disabled = !isChanged;
}

// 이벤트 바인딩 (협력업체 여부와 무관하게 null-safe)
[
    nameNewInput, emailNewInput, phoneNewInput,
    companyNewInput, addressNewInput
].forEach(input => {
    if (input) {
        input.addEventListener('input', checkIfFormChanged);
        input.addEventListener('keyup', checkIfFormChanged);
    }
});

[nameNewInput, emailNewInput, phoneNewInput, companyNewInput, addressNewInput].forEach(input => {
    if (input) {
        input.addEventListener("input", checkIfFormChanged);
    }
});

if (roleSelect) {
    roleSelect.addEventListener("change", checkIfFormChanged);
}

// 비밀번호
const passwordChangeForm = document.getElementById('passwordChangeForm');

// 새 비밀번호 & 새 비밀번호 확인 일치 여부 확인
passwordChangeForm.addEventListener('submit', function(event) {
    // 비밀번호 검증 함수 호출
    if (!submitFormValidatePassword(event)) {
        return;
    }
});

// 수정 폼
const accountEditForm = document.getElementById('accountEditForm');

accountEditForm.addEventListener('submit', function(event) {
    // 이메일 검증 함수 호출
    if (!submitFormValidateEmail(event)) {
        // 이메일 수정 x인 경우는 form 제출 가능하도록
        return;
    }

    // 이름 검증 함수 호출
    if (!submitFormValidateName(event)) {
        return;
    }

    // 연락처 검증 함수 호출
    if (!submitFormValidatePhone(event)) {
        return;
    }
});

//// 주소
function openAddressModal() {
    const addressModal = new bootstrap.Modal(document.getElementById('addressModal'));
    addressModal.show();
}

// 다음 주소 API 열기
function addressDaumPostcode() {
    new daum.Postcode({
        oncomplete: function(data) {
            document.getElementById('postcode').value = data.zonecode;
            document.getElementById('address').value = data.roadAddress;
        }
    }).open();
}

// 주소 조합해서 외부 input에 입력
function applyFullAddress() {
    const postcode = document.getElementById('postcode').value.trim();
    const address = document.getElementById('address').value.trim();
    const detailAddress = document.getElementById('detailAddress').value.trim();
    const extraAddress = document.getElementById('extraAddress').value.trim();

    const fullAddress = [postcode ? `[${postcode}]` : '', address, detailAddress, extraAddress]
        .filter(part => part !== '')
        .join(' ');

    document.getElementById('fullAddressDisplay').value = fullAddress;


    // input 이벤트 수동 발생 (input 감지용)
    addressNewInput.dispatchEvent(new Event('input', { bubbles: true }));

    // 모달 닫기
    const addressModal = bootstrap.Modal.getInstance(document.getElementById('addressModal'));
    addressModal.hide();
}


