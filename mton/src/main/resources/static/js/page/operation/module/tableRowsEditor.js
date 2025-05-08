import * as tutorialMessage from "./tutorialMessage.js";
import * as tmessage from "./tmessage.js";
import * as excelParser from "./excelParser.js";

// 수정 전 원래 값을 담는 변수와 삭제 전 삭제할 ID를 담는 변수
let originalValues = {};

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

// 테이블에 행 추가와 삭제 버튼을 추가
function addTableUtilBtn(table) {
    const rows = table.rows;

    for (let i = 1; i < rows.length; i++) {
        addUtilButtons(table, rows[i]);
    }
}

// 수정테이블의 헤더에 조작버튼 추가
function initEditButtons(table) {
    const allDeleteButton = document.createElement("button");
    allDeleteButton.style.padding = "0";
    allDeleteButton.style.display = "flex";
    allDeleteButton.style.justifyContent = "center";
    allDeleteButton.style.alignItems = "center";
    allDeleteButton.style.borderStyle = "none";
    allDeleteButton.style.position = "absolute";
    allDeleteButton.style.top = "8px";
    allDeleteButton.style.right = "6px";
    allDeleteButton.style.width = "24px";
    allDeleteButton.style.height = "24px";
    allDeleteButton.style.fontSize = "24px";
    allDeleteButton.innerHTML = "&times;";
    allDeleteButton.classList.add("opacity-btn");
    allDeleteButton.style.cursor = "pointer";
    allDeleteButton.setAttribute("red-button", "true");

    const allRevertButton = allDeleteButton.cloneNode(true);
    allRevertButton.innerHTML = "&circlearrowright;"
    allRevertButton.style.right = "32px";
    allRevertButton.setAttribute("red-button", "false");

    allDeleteButton.addEventListener("click", function (e) {
        e.preventDefault();
        e.stopPropagation();

        const checkboxes = Array.from(table.querySelectorAll("input[type='checkbox']"));
        const unchecked = checkboxes.filter(box => !box.checked);
        const checked = checkboxes.filter(box => box.checked);

        if (checkboxes.length === unchecked.length) {
            for (const box of checkboxes) {
                box.checked = true;
                box.dispatchEvent(new Event('change'));
            }
        } else if (checkboxes.length === checked.length) {
            for (const box of checkboxes) {
                box.checked = false;
                box.dispatchEvent(new Event('change'));
            }
        } else {
            for (const box of unchecked) {
                box.checked = true;
                box.dispatchEvent(new Event('change'));
            }
        }
    });

    allRevertButton.addEventListener("click", function (e) {
        e.preventDefault();
        e.stopPropagation();

        const reverts = document.querySelectorAll("button[revert='enabled']");
        if (reverts !== null && reverts.length > 0) {
            for (const btn of reverts) {
                btn.dispatchEvent(new Event('click'));
            }
        }
    });

    const firstRow = table.rows[0];
    firstRow.appendChild(allDeleteButton);
    firstRow.appendChild(allRevertButton);

    tutorialMessage.bindTutorialMessage(allDeleteButton, tmessage.editAllDeleteTutorial);
    tutorialMessage.bindTutorialMessage(allRevertButton, tmessage.editAllRevertTutorial);
}

// 행에 수정 관련 버튼을 넣음
function addEditButtons(table, row, ...protectedCols) {
    // 삭제 체크박스 라벨
    // 스타일링은 이녀석에게 할 것
    const deleteLabel = document.createElement("label");
    deleteLabel.style.padding = "0";
    deleteLabel.style.display = "flex";
    deleteLabel.style.justifyContent = "center";
    deleteLabel.style.alignItems = "center";
    deleteLabel.style.borderStyle = "none";
    deleteLabel.style.position = "absolute";
    deleteLabel.style.top = "8px";
    deleteLabel.style.right = "6px";
    deleteLabel.style.width = "24px";
    deleteLabel.style.height = "24px";
    deleteLabel.style.fontSize = "24px";
    deleteLabel.innerHTML = "&times;";
    deleteLabel.classList.add("opacity-btn");
    deleteLabel.style.cursor = "pointer";
    deleteLabel.setAttribute("red-button", "true");

    // 삭제 체크박스
    const deleteCheck = document.createElement("input");
    deleteCheck.style.position = "absolute";
    deleteCheck.type = "checkbox";
    deleteCheck.style.opacity = "0";
    deleteCheck.style.pointerEvents = "none";
    // 라벨에 넣어줌
    deleteLabel.appendChild(deleteCheck);

    // 수정버튼
    const editButton = document.createElement("button");
    editButton.style.padding = "0";
    editButton.style.display = "flex";
    editButton.style.justifyContent = "center";
    editButton.style.alignItems = "center";
    editButton.style.borderStyle = "none";
    editButton.style.position = "absolute";
    editButton.style.top = "8px";
    editButton.style.right = "32px";
    editButton.style.width = "24px";
    editButton.style.height = "24px";
    editButton.style.fontSize = "24px";
    editButton.innerHTML = "&bernou;";
    editButton.classList.add("opacity-btn");

    // 되돌리기 버튼
    const revertButton = editButton.cloneNode(true);
    revertButton.innerHTML = "&circlearrowright;"
    revertButton.style.display = "none";
    revertButton.setAttribute("revert", "disabled");

    // 삭제버튼 이벤트. 실제 삭제 로직은 업로드 버튼 눌렀을 때 이루어짐
    deleteCheck.addEventListener("change", function (e) {
        const isChecked = deleteCheck.checked;

        // 켜고 끔에 따라 수정 버튼도 같이 숨기고 보여야함
        // 행 색상도 바꿔줌
        if (isChecked) {
            row.classList.add("table-danger");
            editButton.style.display = "none";
        } else {
            row.classList.remove("table-danger");
            editButton.style.display = "flex";
        }
    });

    // 수정버튼 이벤트. 셀을 수정 가능할 수 있게 함
    editButton.addEventListener("click", function (e) {
        e.preventDefault();
        e.stopPropagation();

        // 원래의 값을 저장
        const cells = row.cells;
        const id = cells[0].innerText;
        originalValues[id] = Array.from(cells).slice(1).map(cell => cell.innerText);

        // 첫 번째 열을 제외한 행의 전체 셀 내부를 수정 가능하도록 변경
        // 첫 번째 열은 암묵적으로 DB ID 로 간주됨.
        for (let i = 1; i < cells.length; i++) {
            cells[i].setAttribute("contenteditable", "true");
        }

        // 보호하도록 선택한 열은 수정 불가능하게 변경
        if (protectedCols !== null && protectedCols.length > 0) {
            for (const i of protectedCols) {
                cells[i].setAttribute("contenteditable", "false");
            }
        }

        // 행 색상 바꾸기
        row.classList.add("table-info");

        // 수정버튼과 삭제버튼을 숨김처리, 이후 되돌리기 버튼 보이기
        editButton.style.display = "none";
        deleteLabel.style.display = "none";
        revertButton.style.display = "flex";
        revertButton.setAttribute("revert", "enabled");
    });

    // 되돌리기 버튼 동작
    revertButton.addEventListener("click", function (e) {
        e.preventDefault();
        e.stopPropagation();

        const cells = row.cells;
        const id = cells[0].innerText;

        // 원래 값이 있었다면 그걸로 다 바꿔줌
        if (originalValues.hasOwnProperty(id)) {
            for (let i = 1; i < cells.length; i++) {
                const origin = originalValues[id];
                cells[i].innerText = origin[i - 1];
                cells[i].setAttribute("contenteditable", "false");
            }
        }

        // 행 색상 원래대로 돌리기
        row.classList.remove("table-info");

        // 버튼들 다시 보이게 하고 자기 자신은 숨기
        editButton.style.display = "flex";
        deleteLabel.style.display = "flex";
        revertButton.style.display = "none";
        revertButton.setAttribute("revert", "disabled");
    });

    row.appendChild(deleteLabel);
    row.appendChild(editButton);
    row.appendChild(revertButton);

    tutorialMessage.bindTutorialMessage(deleteLabel, tmessage.editDeleteBtnTutorial);
    tutorialMessage.bindTutorialMessage(editButton, tmessage.editModifyBtnTutorial);
    tutorialMessage.bindTutorialMessage(revertButton, tmessage.editRevertBtnTutorial);
}

// 수정 테이블에 관련 버튼을 넣음
function addTableEditButtons(table) {
    const rows = table.rows;

    for (let i = 1; i < rows.length; i++) {
        addEditButtons(table, rows[i]);
    }
}

// 테이블용 json Fetch 함수
async function jsonFetcher(dest, jData) {
    return await fetch(dest, {
        method: "POST",
        headers: {
            "Content-Type": "application/json",
        },
        body: JSON.stringify(jData)
    }).then((res) => {
        return res.json();
    }).then(data => {
        return data;
    }).catch(error => {
        console.log(error);
    });
}

// 수정 테이블의 사항을 데이터베이스에 반영한다.
function uploadEditedTable(table, request) {
    let jsonData;
    let jsonFinalData;
    let deleteDest;
    let updateDest;

    const deleteList = Array.from(table.querySelectorAll("input[type='checkbox']:checked"))
        .map(item => item.parentElement.parentElement.cells[0].innerText);

    if (deleteList === null || deleteList.length === 0) {
        console.log("삭제사항은 없습니다.");
    }

    switch (request) {
        case "mat":
        case "material":
            deleteDest = "/internal/product/delete/mat";
            updateDest = "/internal/product/update/mat";
            break;
        case "prd":
        case "product":
            deleteDest = "/internal/product/delete/prd";
            updateDest = "/internal/product/update/prd";
            break;
        case "pbom":
            deleteDest = "/internal/product/delete/pbom";
            updateDest = "/internal/product/update/pbom";
            break;
        default:
            jsonData = null;
    }

    jsonData = jsonFetcher(deleteDest, deleteList);
    return jsonData.then(res => {
        const unModifyList = Array.from(table.querySelectorAll("button[revert='disabled']")).map(item => item.parentElement);
        for (const unMod of unModifyList) {
            unMod.remove();
        }

        for (const row of table.rows) {
            for (const cell of row.cells) {
                cell.removeAttribute("data-v");
            }
        }

        jsonFinalData = excelParser.tableUpload(updateDest, table);
        return jsonFinalData.then(ress => {
            console.log(ress.message);
            return res;
        }).catch(error => {
            console.log(error);
        });
    }).then(data => {
        console.log(data.message);
        return data;
    }).catch(error => {
        console.log(error);
    });
}

// 등록 테이블에 초기 버튼을 단다.
function initEmptyTable(table) {
    for (const row of table.rows) {
        addUtilButtons(table, row);
        tutorialMessage.bindTutorialMessage(table.rows[1].cells[0], tmessage.matIdCellTutorial);
        table.rows[1].cells[0].style.cursor = "help";
    }
}

// 목록 테이블용 fetch 함수
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
    });
}

// 목록 테이블을 갱신한다.
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

    if (jsonData === null) { return Promise.reject(new Error("jsonData is null")); }

    return jsonData.then((result) => {
        const sheet = XLSX.utils.json_to_sheet(result);
        const htmlSheet = XLSX.utils.sheet_to_html(sheet);
        const startIndex = htmlSheet.indexOf("<tr", htmlSheet.indexOf("<tr") + 1);
        const endIndex = htmlSheet.lastIndexOf("</tr>") + "</tr>".length;
        const htmlRow = htmlSheet.substring(startIndex, endIndex);

        const tbody = table.querySelector("tbody");
        tbody.innerHTML = htmlRow;
    });
}

// 수정 테이블을 갱신한다.
function viewEditTable(table, request) {
    const result = viewTable(table, request);
    result.then((result) => {
        addTableEditButtons(table);
        originalValues = {};
    });
}

// 셀 안에 Select Element 넣기
function makeSelect(cell, ...options) {
    const select = document.createElement("select");
    select.classList.add("indirect-select");

    for (const option of options) {
        const opt = document.createElement("option");
        opt.value = option;
        opt.label = option;
        select.appendChild(opt);
    }

    select.addEventListener("change", function (e) {
        let span = cell.querySelector("span");
        if (span === null) {
            span = document.createElement("span");
            cell.appendChild(span);
        }

        span.innerText = select.value;
    });

    cell.appendChild(select);
}

export {
    initEmptyTable,
    initEditButtons,
    viewTable,
    viewEditTable,
    addTableUtilBtn,
    addTableEditButtons,
    uploadEditedTable,
};
