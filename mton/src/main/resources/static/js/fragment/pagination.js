// const totalPages = 26;
// let currentPage = 2;

function createPagination(total, current) {
    const container = document.getElementById("pagination");
    container.innerHTML = "";

    const createBtn = (text, page, isActive = false) => {
        const btn = document.createElement("button");
        btn.textContent = text;
        btn.className = `page-btn${isActive ? " active" : ""}`;
        btn.onclick = () => {
            currentPage = page;
            updateUI();
        };
        return btn;
    };

    for (let i = 1; i <= total; i++) {
        if (
            i === 1 ||
            i === total ||
            (i >= current - 1 && i <= current + 1)
        ) {
            container.appendChild(createBtn(i, i, i === current));
        } else if (
            (i === current - 2 && current > 3) ||
            (i === current + 2 && current < total - 2)
        ) {
            const dots = document.createElement("span");
            dots.className = "dots";
            dots.textContent = "...";
            container.appendChild(dots);
        }
    }
}

// function updateUI() {
//     createPagination(totalPages, currentPage);
//     document.getElementById("prevBtn").classList.toggle("disabled", currentPage === 1);
//     document.getElementById("nextBtn").classList.toggle("disabled", currentPage === totalPages);
//     document.getElementById("gotoSelect").value = currentPage;
// }
function updateUI() {
    createPagination(totalPages, currentPage);
    document.getElementById("prevBtn").classList.toggle("disabled", currentPage === 1);
    document.getElementById("nextBtn").classList.toggle("disabled", currentPage === totalPages);
    document.getElementById("gotoSelect").value = currentPage;

    // 페이지 이동 시 location.href로 쿼리 파라미터 갱신
    document.querySelectorAll(".page-btn").forEach(btn => {
        btn.onclick = () => {
            const page = parseInt(btn.textContent) - 1; // Spring은 0-based
            location.href = `/admin/my/join-list?page=${page}`;
        };
    });
}

// document.getElementById("prevBtn").onclick = () => {
//     if (currentPage > 1) {
//         currentPage--;
//         updateUI();
//     }
// };
//
// document.getElementById("nextBtn").onclick = () => {
//     if (currentPage < totalPages) {
//         currentPage++;
//         updateUI();
//     }
// };
//
// document.getElementById("goBtn").onclick = () => {
//     const selected = parseInt(document.getElementById("gotoSelect").value);
//     if (!isNaN(selected)) {
//         currentPage = selected;
//         updateUI();
//     }
// };
document.getElementById("prevBtn").onclick = () => {
    if (currentPage > 1) {
        location.href = `/admin/my/join-list?page=${currentPage - 2}`; // -1 (1-based to 0-based)
    }
};
document.getElementById("nextBtn").onclick = () => {
    if (currentPage < totalPages) {
        location.href = `/admin/my/join-list?page=${currentPage}`; // 그대로
    }
};

document.getElementById("goBtn").onclick = () => {
    const selected = parseInt(document.getElementById("gotoSelect").value);
    if (!isNaN(selected)) {
        location.href = `/admin/my/join-list?page=${selected - 1}`;
    }
};

function populateSelect() {
    const select = document.getElementById("gotoSelect");
    for (let i = 1; i <= totalPages; i++) {
        const option = document.createElement("option");
        option.value = i;
        option.textContent = i;
        select.appendChild(option);
    }
}

populateSelect();
updateUI();