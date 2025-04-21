const totalPages = 26;
let currentPage = 2;

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

function updateUI() {
    createPagination(totalPages, currentPage);
    document.getElementById("prevBtn").classList.toggle("disabled", currentPage === 1);
    document.getElementById("nextBtn").classList.toggle("disabled", currentPage === totalPages);
    document.getElementById("gotoSelect").value = currentPage;
}

document.getElementById("prevBtn").onclick = () => {
    if (currentPage > 1) {
        currentPage--;
        updateUI();
    }
};

document.getElementById("nextBtn").onclick = () => {
    if (currentPage < totalPages) {
        currentPage++;
        updateUI();
    }
};

document.getElementById("goBtn").onclick = () => {
    const selected = parseInt(document.getElementById("gotoSelect").value);
    if (!isNaN(selected)) {
        currentPage = selected;
        updateUI();
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