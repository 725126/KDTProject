// 필터 적용 함수
function applyFilters() {
    const keyword = document.querySelector('input[placeholder="회원명 검색"]').value.trim();
    const role = document.getElementById("departmentSelect").value;
    const status = document.getElementById("accountStatusSelect").value;
    const sort = document.getElementById("sortSelect").value;
    const size = document.getElementById("postCountSelect").value.replace("개 보기", "").trim();

    let url = `/admin/my/user-list?page=0`;

    if (keyword) url += `&keyword=${encodeURIComponent(keyword)}`;
    if (role && role !== '회원 종류') url += `&role=${encodeURIComponent(role)}`;
    if (status) url += `&status=${encodeURIComponent(status)}`;
    if (sort) url += `&sort=${sort}`;
    if (size) url += `&size=${size}`;

    location.href = url;
}

// 드롭다운 변경 시 필터 적용
document.querySelectorAll('#departmentSelect, #accountStatusSelect, #sortSelect, #postCountSelect')
    .forEach(select => select.addEventListener('change', applyFilters));

// 검색창에서 엔터 입력 시 필터 적용
document.querySelector('input[placeholder="회원명 검색"]')
    .addEventListener('keypress', e => {
        if (e.key === 'Enter') applyFilters();
    });

// 초기화 버튼
document.getElementById("resetFilters").addEventListener("click", function () {
    location.href = "/admin/my/user-list";
});
