import * as tutorialMessage from "./tutorialMessage.js";

// 테이블 열 개수 파악하고 새 행과 열을 삽입
function insertRowCells(table, index) {
    // 원하는 index 위치에 행 삽입
    const newRow = table.insertRow(index);
    newRow.style.position = 'relative';

    const cellCount = table.rows[0].cells.length;
    // 셀이야 항상 끝까지 채워야하니 index 쓸 필요 없음
    for (let i = 0; i < cellCount; i++) {
        const newCell = newRow.insertCell(-1);
        const newSpan = document.createElement("span");
        newSpan.setAttribute("contenteditable", "true");
        newCell.appendChild(newSpan);
    }

    addUtilButtons(table, newRow);
    return newRow;
}

// 행에 추가 버튼과 삭제 버튼을 넣음
function addUtilButtons(table, row) {
    // 행 추가 버튼
    const addButton = document.createElement("button");
    addButton.style.padding = "0";
    addButton.style.display = "flex";
    addButton.style.justifyContent = "center";
    addButton.style.alignItems = "center";
    addButton.style.borderStyle = "none";
    addButton.style.position = "absolute";
    addButton.style.top = "8px";
    addButton.style.right = "32px";
    addButton.style.width = "24px";
    addButton.style.height = "24px";
    addButton.style.fontSize = "24px";
    addButton.innerHTML = "&crarr;";
    addButton.classList.add("opacity-btn");

    // 행 삭제 버튼
    const dltButton = addButton.cloneNode(true);
    dltButton.style.right = "6px";
    dltButton.innerHTML = "&times;";
    dltButton.setAttribute("red-button", "true");

    addButton.addEventListener("click", function (e) {
        e.preventDefault();
        e.stopPropagation();

        const currentRow = row.rowIndex;
        insertRowCells(table, currentRow + 1);
    });

    dltButton.addEventListener("dblclick", function (e) {
        e.preventDefault();
        e.stopPropagation();

        const currentRow = row.rowIndex;
        if (currentRow === 0) {
            insertRowCells(table, 1);
            while (table.rows.length > 2) {
                table.deleteRow(-1);
            }
        } else if (table.rows.length === 2) {
            insertRowCells(table, 1);
            table.deleteRow(2);
        } else {
            table.deleteRow(currentRow);
        }

        tutorialMessage.forceHideMessage();
    });

    tutorialMessage.bindTutorialMessage(addButton, "아래에 새 행을 추가합니다.");
    tutorialMessage.bindTutorialMessage(dltButton, "빠르게 두 번 클릭하여 해당 행을 지웁니다.\n헤더에서 하면 모든 행을 지웁니다.")

    row.appendChild(addButton);
    row.appendChild(dltButton);
}

function initEmptyTable(table) {
    for (const row of table.rows) {
        addUtilButtons(table, row);
    }
}

// 테이블 행 개수 파악하고 선택한 행을 삭제한다.
// 헤더를 제외하고 최소 1개의 행은 남아있어야 한다.
function deleteRowCells(table, index) {
    const rowCount = table.rows.length;
    if (rowCount > 2) {
        table.deleteRow(index);
    }
}

export {
    insertRowCells,
    deleteRowCells,
    initEmptyTable,
};
