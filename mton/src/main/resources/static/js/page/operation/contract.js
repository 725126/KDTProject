Inputmask({
    alias: "numeric",
    groupSeparator: ",",
    autoGroup: true,
    digits: 0,               // 소수점 없음
    digitsOptional: false,
    prefix: "",              // 통화기호 안 붙임 (예: ₩ 생략)
    placeholder: "0",
    allowMinus: false,
    rightAlign: false,
    removeMaskOnSubmit: true, // 서버 전송 시 마스크 제거
}).mask(".cmt-price");

Inputmask({
    alias: "integer",
    placeholder: "",
    min: 1,
    max: 999, // 최대 3자리까지 허용
    allowMinus: false,
    rightAlign: false,
    showMaskOnHover: false,
    showMaskOnFocus: true
}).mask(".leadtime-day");

const previewBtn = document.getElementById("previewSubmitBtn");
const confirmGroup = document.getElementById("confirmButtonGroup");
const editBackBtn = document.getElementById("editBackBtn");
const submitBtn = document.getElementById("submitBtn");

// 개별 등록 form 미리표기 표시 여부
let previewed = false; // 전역 변수로 선언 필요

// 미리보기 버튼 클릭 시: 미리보기 숨기고 등록/수정 표시
previewBtn.addEventListener("click", function () {
    handlePreviewSubmit(); // 유효성 검사 통과 여부 반환

    if (previewed) {
        previewBtn.classList.add("d-none");
        confirmGroup.classList.remove("d-none");
    }
});

// 수정 버튼 클릭 시: 등록/수정 숨기고 다시 미리보기 표시
editBackBtn.addEventListener("click", function () {
    const mode = editBackBtn.dataset.editMode;

    if (mode === 'edit-false') {
        previewed = false;
        toggleReadonlyFields(previewed);
        // 버튼 색 변경
        editBackBtn.classList.remove("btn-secondary");
        editBackBtn.classList.add("btn-primary");
        editBackBtn.innerHTML = `<i class="bi bi-pencil-square me-1"></i> 수정완료`;

        // 수정 가능으로
        editBackBtn.dataset.editMode = 'edit-true';
    } else if (mode === 'edit-true') {
        handlePreviewSubmit();

        toggleReadonlyFields(previewed);
        editBackBtn.classList.add("btn-secondary");
        editBackBtn.classList.remove("btn-primary");
        editBackBtn.innerHTML = `<i class="bi bi-pencil-square me-1"></i> 수정하기`;

        // 수정 불가로
        editBackBtn.dataset.editMode = 'edit-false';
    }
});

// 등록 버튼 클릭 시 > 등록 진행
submitBtn.addEventListener("click", function () {
    const mode = editBackBtn.dataset.editMode;

    if (mode === 'edit-false') {
        handlePreviewSubmit();
    } else {
        const validationToastBox = document.getElementById("validationToastBox");
        validationToastBox.classList.remove('top-52');
        validationToastBox.classList.remove('start-80');
        validationToastBox.classList.add('top-57');
        validationToastBox.classList.add('start-74');
        showToast("수정을 완료해주세요.");

        return;
    }
});

// 계약 코드
let contractCode = null;
// 협력업체 ID
let partnerId = null;
let partnerCompany = null;
const materialCodes = [], materialPrices = [], materialQtys = [],
    materialSchedules = [], materialExplains = [];
let previewDataRows = [];

const materialNames = [
    // "모터 어셈블리", "배터리 팩 48V", "컨트롤러 보드",
    // "프레임 알루미늄 A형", "디스크 브레이크 세트",
    // "핸들바 일체형", "충전기 42V 2A", "킥스탠드",
    // "LED 전조등 모듈", "타이어 20인치 튜브리스", "페달 세트"
];

document.getElementById('materialFile').addEventListener('change', function (e) {
    const file = e.target.files[0];
    if (!file) return;

    const allowedTypes = [
        'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet',
        'application/vnd.ms-excel'
    ];
    if (!allowedTypes.includes(file.type)) {
        alert("📂 올바른 Excel 파일(xls, xlsx)만 업로드해주세요.");
        return;
    }

    const reader = new FileReader();
    reader.onload = function (e) {
        const data = new Uint8Array(e.target.result);
        const workbook = XLSX.read(data, {type: 'array'});
        const sheet = workbook.Sheets[workbook.SheetNames[0]];
        const json = XLSX.utils.sheet_to_json(sheet, {header: 1});

        const requiredHeaders = ["계약코드", "협력업체 ID", "자재코드", "단가(원)", "수량", "소요일(일)", "합의내용"];
        const actualHeaders = json[0];
        const isHeaderValid = requiredHeaders.every((header, idx) => header === actualHeaders[idx]);
        if (!isHeaderValid) {
            alert("❗ Excel 양식이 올바르지 않습니다. 제공된 예시 파일 형식을 준수해주세요.");
            return;
        }

        materialCodes.length = materialPrices.length = materialQtys.length = materialSchedules.length = 0;
        previewDataRows = json.slice(1);
        let lastContractCode = null, lastPartnerId = null;
        const tbody = document.getElementById('previewBody');
        tbody.innerHTML = '';

        previewDataRows.forEach(row => {
            const conCode = row[0] !== undefined && row[0] !== '' ? row[0] : lastContractCode;
            const pId = row[1] !== undefined && row[1] !== '' ? row[1] : lastPartnerId;
            if (!conCode || !pId) return;

            // 전체 row 값 중 유의미한 값이 하나라도 있는지 검사
            const isEmptyRow = row.every(cell => cell === undefined || cell === null || cell.toString().trim() === '');
            if (isEmptyRow) return;

            lastContractCode = conCode;
            lastPartnerId = pId;
            if (contractCode === null) contractCode = conCode;
            if (partnerId === null) partnerId = pId;

            materialCodes.push(row[2]);
            materialPrices.push(Number(row[3]));
            materialQtys.push(Number(row[4]));
            materialSchedules.push(Number(row[5]));
            materialExplains.push(row[6]);

            const tr = document.createElement('tr');
            for (let j = 0; j < 7; j++) {
                const td = document.createElement('td');
                td.textContent = (row[j] !== undefined && row[j] !== null && row[j].toString().trim() !== '') ? row[j] : '-';
                tr.appendChild(td);
            }
            tbody.appendChild(tr);
        });

        document.getElementById('previewMaterialIconBtn').classList.remove('d-none');
        new bootstrap.Modal(document.getElementById('previewMaterialModal')).show();

        console.log({ contractCode, partnerId, materialCodes, materialPrices, materialQtys, materialSchedules });
        document.getElementById("conCode").value = contractCode;

        loadMaterialNames()
            .then(names => {
                materialNames.length = 0;
                names.forEach(name => materialNames.push(name));

                console.log("✅ 자재명 갱신 완료:", materialNames);
            });

        getPartnerCompanyName(partnerId)
            .then(companyName => {
                console.log("✅ 협력업체 회사명:", companyName);
                partnerCompany = companyName;
            })
            .catch(err => {
                console.error("❌ 회사명 조회 실패:", err);
            });
    };
    reader.readAsArrayBuffer(file);
});

document.getElementById('previewMaterialIconBtn').addEventListener('click', function () {
    const tbody = document.getElementById('previewBody');
    tbody.innerHTML = '';
    previewDataRows.forEach(row => {
        // 전체 row 값 중 유의미한 값이 하나라도 있는지 검사
        const isEmptyRow = row.every(cell => cell === undefined || cell === null || cell.toString().trim() === '');
        if (isEmptyRow) return;

        const tr = document.createElement('tr');
        for (let j = 0; j < 7; j++) {
            const td = document.createElement('td');
            td.textContent = (row[j] !== undefined && row[j] !== null && row[j].toString().trim() !== '') ? row[j] : '-';
            tr.appendChild(td);
        }
        tbody.appendChild(tr);
    });
    new bootstrap.Modal(document.getElementById('previewMaterialModal')).show();
});

// 개별 등록
function handlePreviewSubmit() {
    const conCode = document.getElementById("conCode").value.trim();
    const startDate = document.getElementById("startDate").value;
    const endDate = document.getElementById("endDate").value;
    const fileContractInput = document.getElementById("contractFile");
    const fileName = fileContractInput.files.length > 0 ? fileContractInput.files[0].name : "없음";

    // ✅ 유효성 검사
    if (!conCode || !startDate || !endDate) {
        showToast("모든 항목을 빠짐없이 입력해주세요.");
        return;
    }

    if (fileContractInput.files.length === 0) {
        showToast("계약서 파일을 반드시 첨부해주세요.");
        return;
    }

    if (new Date(startDate) > new Date(endDate)) {
        showToast("계약일자는 만료일자보다 빠르거나 같아야 합니다.");
        return;
    }

    const tbody = document.querySelector("#previewTableBody");
    tbody.innerHTML = "";

    if (!previewed) {
        console.log("✅ 렌더링 시작됨");

        for (let i = 0; i < materialCodes.length; i++) {
            console.log("→ 렌더링할 자재:", materialNames[i]); // 확인용

            const row = `
          <tr>
            <td>${conCode}</td>
            <td>${partnerCompany}</td>
            <td>${materialNames[i]}</td>
            <td>${materialPrices[i]}</td>
            <td>${materialQtys[i]}</td>
            <td>${materialSchedules[i]}</td>
            <td>${startDate}</td>
            <td>${endDate}</td>
            <td>${materialExplains[i]}</td>
            <td><span class="file-span"><i class="bi bi-paperclip"></i> ${fileName}</span></td>
          </tr>
        `;
            tbody.insertAdjacentHTML("beforeend", row);
        }
        previewed = true;

    } else {
        // JSON 데이터 + 파일을 FormData에 담음
        const formData = new FormData();
        formData.append("contractFile", fileContractInput.files[0]);

        const jsonData = {
            conCode,
            partnerId,
            materialCodes,
            materialPrices,
            materialQtys,
            materialSchedules,
            materialExplains,
            startDate,
            endDate
        };

        formData.append("data", new Blob([JSON.stringify(jsonData)], {type: "application/json"}));

        fetch("/internal/procurement/contract/submit", {
            method: "POST",
            headers: {
                'X-CSRF-TOKEN': document.querySelector('meta[name="_csrf"]').getAttribute('content')
            },
            body: formData
        })
            // .then(response => response.json())
            .then(result => {
                console.log("등록 결과:", result);

                if (result.status === "success") {
                    // 개별 등록 후
                    const modal = new bootstrap.Modal(document.getElementById('submitModal'));
                    modal.show();

                    document.getElementById("contractForm").reset();
                    document.getElementById("previewTableBody").innerHTML = "";
                    previewed = false;

                    document.querySelector("#submitModal .btn[data-bs-dismiss='modal']")
                        .addEventListener("click", function () {
                            location.reload(); // 사용자가 모달을 닫을 때 새로고침
                        });
                } else {
                    showToast("문제가 발생했습니다.");
                }
            })
            .catch(error => {
                console.error("서버 오류:", error);
                showToast("서버와 통신 중 오류가 발생했습니다.");
            });
    }

    toggleReadonlyFields(previewed);
}

function toggleReadonlyFields(isReadonly) {
    const fields = [
        "conCode", "startDate", "endDate"
    ];

    fields.forEach(id => {
        const el = document.getElementById(id);
        if (el) {
            el.readOnly = isReadonly;
        }
    });

    // 파일은 readonly 개념 없으므로 disable 처리
    const fileInput = document.getElementById("contractFile");
    if (fileInput) {
        fileInput.disabled = isReadonly;
    }

    const fileMaterialInput = document.getElementById("materialFile");
    if (fileMaterialInput) {
        fileMaterialInput.disabled = isReadonly;
    }
}

// 계약서 파일 미리보기 관련 JS
let selectedFile;

document.getElementById("contractFile").addEventListener("change", (e) => {
    const file = e.target.files[0];

    if (!file) return;

    selectedFile = file;
    showPreviewModal(selectedFile);

});

document.getElementById('previewIconBtn').addEventListener('click', function () {
    showPreviewModal(selectedFile);
});

document.addEventListener("click", function (e) {
    const span = e.target.closest(".file-span");
    if (span) {
        if (!selectedFile) {
            showToast("📂 파일이 선택되지 않았습니다");
            return;
        }
        showPreviewModal(selectedFile);
    }
});

// 계약서 파일 미리보기 모달 JS
function showPreviewModal(file) {
    const reader = new FileReader();
    const preview = document.getElementById("previewContent");

    if (file.type.includes("pdf")) {
        reader.onload = function (e) {
            preview.innerHTML = `<iframe src="${e.target.result}#navpanes=0#zoom=100" width="100%" height="600px"></iframe>`;
        }
        reader.readAsDataURL(file);
    } else if (file.type.includes("image")) {
        reader.onload = function (e) {
            preview.innerHTML = `<img src="${e.target.result}" class="img-fluid">`;
        }
        reader.readAsDataURL(file);
    } else {
        preview.innerHTML = "지원하지 않는 파일 형식입니다.";
    }

    document.getElementById('previewIconBtn').classList.remove('d-none');
    const modal = new bootstrap.Modal(document.getElementById("previewModal"));
    modal.show();
}

function showToast(message) {
    const toastMsg = document.getElementById("toastMsg");
    toastMsg.textContent = message;

    const toastElement = document.getElementById("validationToast");
    const toast = new bootstrap.Toast(toastElement);
    toast.show();
}

// 자재코드로 자재명을 가져오는 함수
async function loadMaterialNames() {
    const response = await fetch('/internal/procurement/contract/find/mt-names', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json',
            'X-CSRF-TOKEN': document.querySelector('meta[name="_csrf"]').getAttribute('content')
        },
        body: JSON.stringify(materialCodes)
    });

    if (!response.ok) throw new Error("자재명 조회 실패");

    const names = await response.json();
    return names; // 배열 반환
}

async function getPartnerCompanyName(partnerId) {
    const response = await fetch(`/internal/procurement/contract/company-name/${partnerId}`, {
        method: 'GET',
        headers: {
            'X-CSRF-TOKEN': document.querySelector('meta[name=\"_csrf\"]').getAttribute('content')
        }
    });

    if (!response.ok) throw new Error("회사명 조회 실패");

    return await response.text();
}


// 필터링
function applyContractFilters() {
    const keyword = document.querySelector('input[placeholder="업체명 또는 계약자재 검색"]').value.trim();
    const sort = document.getElementById("sortSelect").value;
    const size = document.getElementById("postCountSelect").value.replace("개 보기", "").trim();

    let url = `/internal/procurement/contract?page=0`;

    if (keyword) url += `&keyword=${encodeURIComponent(keyword)}`;
    if (sort) url += `&sort=${sort}`;
    if (size) url += `&size=${size}`;

    location.href = url;
}

document.querySelectorAll('#sortSelect, #postCountSelect')
    .forEach(select => select.addEventListener('change', applyContractFilters));

document.querySelector('input[placeholder="업체명 또는 계약자재 검색"]')
    .addEventListener('keypress', e => {
        if (e.key === 'Enter') applyContractFilters();
    });

// document.getElementById("resetFilters").addEventListener("click", function () {
//     location.href = "/internal/procurement/contract";
// });












