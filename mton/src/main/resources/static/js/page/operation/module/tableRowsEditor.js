import * as tutorialMessage from "./tutorialMessage.js";
import * as tmessage from "./tmessage.js";

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

    tutorialMessage.bindTutorialMessage(newRow.cells[0], tmessage.matIdCellTutorial);
    newRow.cells[0].style.cursor = "help";

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

    tutorialMessage.bindTutorialMessage(addButton, tmessage.matRowAddTutorial);
    tutorialMessage.bindTutorialMessage(dltButton, tmessage.matRowRemoveTutorial);

    row.appendChild(addButton);
    row.appendChild(dltButton);
}

function addTableUtilBtn(table) {
    const rows = table.rows;

    for (let i = 1; i < rows.length; i++) {
        addUtilButtons(table, rows[i]);
    }
}

function initEmptyTable(table) {
    for (const row of table.rows) {
        addUtilButtons(table, row);
        tutorialMessage.bindTutorialMessage(table.rows[1].cells[0], tmessage.matIdCellTutorial);
        table.rows[1].cells[0].style.cursor = "help";
    }
}

async function refreshTableView(dest, request = "") {
    return await fetch(dest, {
        method: "POST",
        headers: {
            "Content-Type": "text/plain",
        },
        body: request
    }).then(response => {
        return response.json();
    }).then(data => {
        return data;
    }).catch(error => {
        console.error(error);
    })
}

function viewTable(table, request) {
    let jsonData;

    switch (request) {
        case "mat":
        case "material":
            jsonData = refreshTableView("/internal/product/view/mat", "material");
            break;
        case "prd":
        case "product":
            jsonData = refreshTableView("/internal/product/view/prd", "product");
            break;
        case "pbom":
            jsonData = refreshTableView("/internal/product/view/pbom", "pbom");
            break;
        default:
            jsonData = null;
    }

    if (jsonData === null) { return; }

    jsonData.then((result) => {
        const sheet = XLSX.utils.json_to_sheet(result);
        const htmlSheet = XLSX.utils.sheet_to_html(sheet);
        const startIndex = htmlSheet.indexOf("<tr", htmlSheet.indexOf("<tr") + 1);
        const endIndex = htmlSheet.lastIndexOf("</tr>") + "</tr>".length;
        const htmlRow = htmlSheet.substring(startIndex, endIndex);

        const tbody = table.querySelector("tbody");
        tbody.innerHTML = htmlRow;
    });
}

export {
    initEmptyTable,
    viewTable,
    addTableUtilBtn,
};
