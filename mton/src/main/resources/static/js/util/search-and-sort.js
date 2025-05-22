function toggleAll(source) {
    const checkboxes = document.querySelectorAll("#dropdownMenu input[type='checkbox']");
    checkboxes.forEach(cb => {
        if (cb !== source) cb.checked = source.checked;
    });
}

// 필터링 함수
function applyTransactionFilters() {
    const keyword = document.getElementById("searchInput").value.trim();
    const sort = document.getElementById("sortSelect").value;
    const size = document.getElementById("postCountSelect").value;

    // 체크된 항목들 (거래 코드, 발행일, 총 금액)
    const selectedCategory = Array.from(document.querySelectorAll("#dropdownMenu input[type='checkbox']:checked"))
        .filter(cb => cb.id !== 'allCheck')
        .map(cb => cb.id); // ["tranId", "tranDate", "totalAmount"]

    let url = `/external/trans?page=0&size=${size}&sort=${sort}`;

    if (keyword) url += `&keyword=${encodeURIComponent(keyword)}`;
    if (selectedCategory.length > 0) {
        selectedCategory.forEach(cat => url += `&category=${cat}`);
    }

    location.href = url;
}

// 이벤트 바인딩
document.getElementById("sortSelect").addEventListener("change", applyTransactionFilters);
document.getElementById("postCountSelect").addEventListener("change", applyTransactionFilters);
document.getElementById("searchInput").addEventListener("keypress", e => {
    if (e.key === "Enter") applyTransactionFilters();
});


