const compareNumber = (a, b) => a < b ? -1 : a === b ? 0 : 1;
const toBoolean = (value) => value === "true";
const reverseBoolean = (value) => value === "true" ? "false" : "true";

function sortTable(table, columnIndex = 0, reverse = false) {
    const tbody = table.tBodies[0];
    const rows = Array.from(tbody.rows);

    rows.sort((a, b) => {
        const valA = a.cells[columnIndex].textContent.trim();
        const valB = b.cells[columnIndex].textContent.trim();

        const isNumeric = !isNaN(valA) && !isNaN(valB);
        const isDate = !isNaN(Date.parse(valA)) && !isNaN(Date.parse(valB));

        const aVal = isNumeric ? parseFloat(valA) : isDate ? Date.parse(valA) : valA;
        const bVal = isNumeric ? parseFloat(valB) : isDate ? Date.parse(valB) : valB;

        if (aVal < bVal) { return reverse ? 1 : -1; }
        if (aVal > bVal) { return reverse ? -1 : 1; }
        return 0
    });

    rows.forEach(row => tbody.appendChild(row));
}

function filterTableByKeyword(table, columnIndex = 0, filterClass = "", keyword = "") {
    if (filterClass === null || filterClass.length === 0) {
        return;
    }

    const tbody = table.tBodies[0];
    const rows = Array.from(tbody.rows);

    if (keyword === null || keyword.length === 0) {
        for (const row of rows) {
            row.classList.remove(filterClass);
        }
        return;
    }

    for (const row of rows) {
        const cell = row.cells[columnIndex];
        if (!cell.innerText.toLowerCase().includes(keyword.toLowerCase())) {
            cell.classList.add(filterClass);
        }
    }
}

function filterTableByValue(table, columnIndex = 0, filterClass = "", mod = 0, value = "") {
    if (filterClass === null || filterClass.length === 0) {
        return;
    }

    const tbody = table.tBodies[0];
    const rows = Array.from(tbody.rows);

    if (value === null || value.length === 0) {
        for (const row of rows) {
            row.classList.remove(filterClass);
        }
        return;
    }

    const modNumber = compareNumber(mod, 0);
    for (const row of rows) {
        const cell = row.cells[columnIndex];
        if (compareNumber(Number.parseInt(cell.innerText), Number.parseInt(value)) !== modNumber) {
            cell.classList.add(filterClass);
        }
    }
}

function applyTableSorting(table) {
    for (const cell of table.rows[0].cells) {
        if (cell.cellIndex === 0) {
            cell.setAttribute("reverse", "true");
            cell.setAttribute("sorting", "true");
        } else {
            cell.setAttribute("reverse", "false");
            cell.setAttribute("sorting", "false");
        }

        cell.style.cursor = "pointer";

        cell.addEventListener("click", function (e) {
            e.preventDefault();
            e.stopPropagation();

            sortTable(table, cell.cellIndex, toBoolean(cell.getAttribute("reverse")));
            cell.setAttribute("reverse", reverseBoolean(cell.getAttribute("reverse")));
            cell.setAttribute("sorting", "true");

            for (const inCell of table.rows[0].cells) {
                if (inCell.cellIndex !== cell.cellIndex) {
                    inCell.setAttribute("reverse", "false");
                    inCell.setAttribute("sorting", "false");
                }
            }
        });
    }
}

export {
    applyTableSorting,
}
