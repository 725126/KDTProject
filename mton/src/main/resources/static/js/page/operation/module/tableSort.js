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

export {
    sortTable,
}
