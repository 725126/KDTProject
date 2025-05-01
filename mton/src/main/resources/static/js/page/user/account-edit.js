const passwordChangeForm = document.getElementById('passwordChangeForm');

// 새 비밀번호 & 새 비밀번호 확인 일치 여부 확인
passwordChangeForm.addEventListener('submit', function(event) {
    // 비밀번호 검증 함수 호출
    if (!submitFormValidatePassword(event)) {
        return;
    }
});

const accountEditForm = document.getElementById('accountEditForm');

accountEditForm.addEventListener('submit', function(event) {
    // 이메일 검증 함수 호출
    if (!submitFormValidateEmail(event)) {
        // 이메일 수정 x인 경우는 form 제출 가능하도록
        return;
    }
});

