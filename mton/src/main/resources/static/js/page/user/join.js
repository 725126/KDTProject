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

const joinForm = document.getElementById('joinForm');

// 회원가입 버튼 클릭 시 입력 값 체크
joinForm.addEventListener('submit', function(event) {
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
    console.log(fullAddress);

    // 아이디 검증 함수 호출
    if (!submitFormValidateEmail(event)) {
        return;
    }

    // 비밀번호 검증 함수 호출
    if (!submitFormValidatePassword(event)) {
        return;
    }

    if (submitFormValidateName(event)) {
        return;
    }

    // 연락처 검증 함수 호출
    if (!submitFormValidatePhone(event)) {
        return;
    }

    // 사업자등록번호 검증 함수 호출
    if (!submitFormValidateBusinessNo(event)) {
        return;
    }

    // 여기까지 아무것도 걸리지 않으면 submit 정상 진행

});


