document.addEventListener("DOMContentLoaded", function () {
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
    const inputName = document.getElementById("uName");
    const inputPhone = document.getElementById("uPhone");
    const findResult = document.getElementById("findResult");
    const inputUserRole = document.getElementById('userRole');

    form.addEventListener("submit", function (event) {
        event.preventDefault(); // 폼 기본 제출 막기

        const uName = inputName.value.trim();
        const uPhone = inputPhone.value.trim();
        const userRole = inputUserRole.value;
        let businessNo = "";

        // 이름 검증
        if (!submitFormValidateName(event)) {
            return;
        }

        // 연락처 검증
        if (!submitFormValidatePhone(event)) {
            return;
        }

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