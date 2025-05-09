// 회원가입 승인
let pendingEmail = null; // 승인할 이메일 저장용

document.querySelectorAll(".approve-btn").forEach(button => {
    button.addEventListener("click", function () {
        pendingEmail = this.closest(".card").querySelector(".text-muted").textContent.trim();

        const confirmModal = new bootstrap.Modal(document.getElementById('approveConfirmModal'));
        confirmModal.show();
    });
});

// "확인" 버튼 클릭 시 fetch 실행
document.getElementById("confirmApproveBtn").addEventListener("click", function () {
    if (!pendingEmail) return;

    fetch("/admin/my/approve", {
        method: "PUT",
        headers: {
            "Content-Type": "application/json",
            "X-CSRF-TOKEN": document.querySelector('meta[name="_csrf"]').getAttribute('content')
        },
        body: JSON.stringify({uEmail: pendingEmail})
    })
        .then(res => {
            const modal = new bootstrap.Modal(document.getElementById('approveResultModal'));
            const modalBody = document.getElementById('approveResultModalBody');

            if (res.ok) {
                modalBody.innerText = "승인 완료되었습니다.";
                modal.show();
                setTimeout(() => location.reload(), 2000);
            } else {
                modalBody.innerText = "승인 실패. 다시 시도해주세요.";
                modal.show();
            }
        });

    // 승인 확인 모달 닫기
    bootstrap.Modal.getInstance(document.getElementById('approveConfirmModal')).hide();
});

// 필터링
function applyFilters() {
    const keyword = document.querySelector('input[placeholder="회원명 검색"]').value.trim();
    const role = document.getElementById("departmentSelect").value;
    const sort = document.getElementById("sortSelect").value;
    const size = document.getElementById("postCountSelect").value.replace("개 보기", "").trim();

    let url = `/admin/my/join-list?page=0`;

    if (keyword) url += `&keyword=${encodeURIComponent(keyword)}`;
    if (role) url += `&role=${encodeURIComponent(role)}`;
    if (sort) url += `&sort=${sort}`;
    if (size) url += `&size=${size}`;

    location.href = url;
}

document.querySelectorAll('#departmentSelect, #sortSelect, #postCountSelect')
    .forEach(select => select.addEventListener('change', applyFilters));

document.querySelector('input[placeholder="회원명 검색"]')
    .addEventListener('keypress', e => {
        if (e.key === 'Enter') applyFilters();
    });

document.getElementById("resetFilters").addEventListener("click", function () {
    location.href = "/admin/my/join-list";
});


