document.addEventListener("DOMContentLoaded", function () {
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

    // 회원종류 선택 > 협력업체 선택 시 사업자등록번호 입력창 생김
    const userRoleSelect = document.getElementById('userRole');
    // businessNoInput 선언
    const businessNoInput = document.getElementById('pBusinessNo');
    const pBusinessNoGroup = document.getElementById('pBusinessNoGroup');

    userRoleSelect.addEventListener('change', function () {
        if (this.value === 'PARTNER') {
            pBusinessNoGroup.classList.remove('d-none');
            businessNoInput.setAttribute('required', 'required');
        } else {
            pBusinessNoGroup.classList.add('d-none');
            businessNoInput.removeAttribute('required');
        }
    });

    const form = document.querySelector("form");
    const inputName = document.getElementById("inputName");
    const inputPhone = document.getElementById("inputPhone");
    const findResult = document.getElementById("findResult");
    const inputUserRole = document.getElementById('userRole');

    form.addEventListener("submit", function (event) {
        event.preventDefault(); // 폼 기본 제출 막기

        const uName = inputName.value.trim();
        const uPhone = inputPhone.value.trim();
        const userRole = inputUserRole.value;
        let businessNo = "";

        // 회원종류: 협력업체 인 경우에만 사업자번호 입력 필수
        if (userRole === 'PARTNER') {
            businessNo = businessNoInput.value;
            if (!submitFormValidateBusinessNo(event)) {
                return;
            }
        }

        const dto = {
            uName: uName,
            uPhone: uPhone,
            userRole: userRole,
            pBusinessNo: businessNo
        };

        console.log("전송 dto:", dto);

        fetch(`/find/id`, {
            method: "POST",
            headers: {
                "Content-Type": "application/json"
            },
            body: JSON.stringify(dto),
        })
            .then(response => response.json())
            .then(data => {
                if (data.foundEmail) {
                    findResult.innerHTML = `
                    <div class="alert alert-success">회원님의 아이디는 <strong>${data.foundEmail}</strong> 입니다.</div>
                `;
                } else if (data.notFound) {
                    findResult.innerHTML = `
                    <div class="alert alert-warning">입력하신 정보와 일치하는 계정을 찾을 수 없습니다.</div>
                `;
                }
            })
            .catch(error => {
                console.error("에러 발생:", error);
                findResult.innerHTML = `
                <div class="alert alert-danger">일시적인 오류가 발생했습니다. 다시 시도해주세요.</div>
            `;
            });
    });

});