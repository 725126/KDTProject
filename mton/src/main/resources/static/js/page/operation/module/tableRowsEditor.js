import * as tutorialMessage from "./tutorialMessage.js";
import * as tmessage from "./tmessage.js";
import * as excelParser from "./excelParser.js";
import * as dbElementNames from "./dbElementNames.js";

// DB 에서 가져온 각 테이블 데이터들
let matDBData;
let prdDBData;
let pbomDBData;
let prdplanDBData;
let prdplanDBEditData;
let pplanDBData;
let pplanDBEditData;
let orderDBData;
let contmatDBData;

// 수정 전 원래 값을 담는 변수와 삭제 전 삭제할 ID를 담는 변수
let originalValues = {};

// 등록 이후 목록으로 넘어가는 이벤트 발생용 함수
function viewChange() {
    document.querySelector("#mod-view").checked = true;
    document.querySelector("#manage-mod").dispatchEvent(new Event("click"));
    document.querySelector("#mod-view").dispatchEvent(new Event("change"));
}

// 테이블 열 개수 파악하고 새 행과 열을 삽입.
// 일반적으로 등록 테이블에서만 사용한다.
function insertRowCells(table, index, ...optionFn) {
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

    tutorialMessage.bindTutorialMessage(newRow.cells[0], tmessage.idCellTutorial);
    newRow.cells[0].style.cursor = "help";

    addUtilButtons(table, newRow, optionFn.flat());
    return newRow;
}

// 행에 추가 버튼과 삭제 버튼을 넣음
function addUtilButtons(table, row, ...optionFn) {
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
        insertRowCells(table, currentRow + 1, optionFn.flat());
    });

    dltButton.addEventListener("dblclick", function (e) {
        e.preventDefault();
        e.stopPropagation();

        const currentRow = row.rowIndex;
        if (currentRow === 0) {
            insertRowCells(table, 1, optionFn.flat());
            while (table.rows.length > 2) {
                table.deleteRow(-1);
            }
        } else if (table.rows.length === 2) {
            insertRowCells(table, 1, optionFn.flat());
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

    for (const option of optionFn.flat()) {
        const cell = option(row);
        if (cell.tagName !== "TH") {
            cell.setAttribute("contenteditable", "true");
        }
    }

    return row;
}

// 테이블에 행 추가와 삭제 버튼을 추가
function addTableUtilBtn(table) {
    const rows = table.rows;

    for (let i = 1; i < rows.length; i++) {
        addUtilButtons(table, rows[i]);
    }
}

function addPbomTableUtilBtn(table) {
    initEmptyPbomTable(table, true);
}

function addPrdPlanTableUtilBtn(table) {
    initEmptyPrdPlanTable(table, true);
}

function addPPlanTableUtilBtn(table) {
    initEmptyPPlanTable(table, true);
}

function addOrderTableUtilBtn(table) {
    initEmptyOrderingTable(table, true);
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

        const checkboxes = Array.from(table.querySelectorAll("tr:not(.table-info) input[type='checkbox']"));
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
    deleteCheck.addEventListener("change", function () {
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
                const span = cells[i].querySelector("span");

                if (span !== null) {
                    span.innerText = origin[i - 1];
                } else {
                    cells[i].innerText = origin[i - 1];
                }

                const select = cells[i].querySelector("select");
                if (select !== null) {
                    select.value = "";
                }

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
            "X-CSRF-TOKEN": document.querySelector('meta[name="_csrf"]').getAttribute('content')
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
function uploadEditedTable(table, request, del, upd) {
    let jsonData;
    let jsonFinalData;
    let deleteDest;
    let updateDest;

    const deleteList = Array.from(table.querySelectorAll("input[type='checkbox']:checked"))
        .map(item => item.parentElement.parentElement.cells[0].innerText);

    console.log(deleteList);

    if (deleteList === null || deleteList.length === 0) {
        console.log("삭제사항은 없습니다.");
    }

    // 왠만하면 여기에 경우의 수를 추가하지 말 것. 써보니까 엄청 힘듦.
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
        case "prdplan":
        case "productionplan":
            deleteDest = "/internal/product/delete/prdplan";
            updateDest = "/internal/product/update/prdplan";
            break;
        case "pplan":
        case "procurementplan":
            deleteDest = "/internal/procurement/delete/pplan";
            updateDest = "/internal/procurement/update/pplan";
            break;
        default:
            deleteDest = del;
            updateDest = upd;
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
        tutorialMessage.bindTutorialMessage(table.rows[1].cells[0], tmessage.idCellTutorial);
        table.rows[1].cells[0].style.cursor = "help";
    }
}

function initEmptyPbomTable(table, isFile = false) {
    const matOptions = matDBData.map(data => [data[dbElementNames.pbomMatId], data[dbElementNames.matName]]);
    const prdOptions = prdDBData.map(data => [data[dbElementNames.pbomProdId], data[dbElementNames.prodName]]);

    const makeMatSelect = (row) => {
        makeSelectForRawCell(row.cells[1], matOptions);
        return row.cells[1];
    }
    const makePrdSelect = (row) => {
        makeSelectForRawCell(row.cells[2], prdOptions);
        return row.cells[2];
    }

    for (const row of table.rows) {
        if (isFile && row.rowIndex === 0) {
            continue;
        }
        addUtilButtons(table, row, makeMatSelect, makePrdSelect);
        tutorialMessage.bindTutorialMessage(table.rows[1].cells[0], tmessage.idCellTutorial);
        table.rows[1].cells[0].style.cursor = "help";
    }
}

function initEmptyPrdPlanTable(table, isFile = false) {
    const prdOptions = prdDBData.map(data => [data[dbElementNames.prodId], data[dbElementNames.prodName]]);

    const makePrdSelect = (row) => {
        makeSelectForRawCell(row.cells[1], prdOptions);
        return row.cells[1];
    }

    const makeDate = (row) => {
        makeDatePicker(row.cells[3]);
        return row.cells[3];
    }

    for (const row of table.rows) {
        if (isFile && row.rowIndex === 0) {
            continue;
        }
        addUtilButtons(table, row, makePrdSelect, makeDate);
        tutorialMessage.bindTutorialMessage(table.rows[1].cells[0], tmessage.idCellTutorial);
        table.rows[1].cells[0].style.cursor = "help";
    }
}

function initEmptyPPlanTable(table, isFile = false) {
    const prdplanOptions = prdplanDBData.map(data => data[dbElementNames.prdplanId]);
    const matOptions = matDBData.map(data => data[dbElementNames.matId]);

    const makePrdplanSelect = (row) => {
        makeSelectForRawCell(row.cells[1], prdplanOptions);
        return row.cells[1];
    }

    const makeMatSelect = (row) => {
        makeSelectForRawCell(row.cells[2], matOptions);
        return row.cells[2];
    }

    const makeDate = (row) => {
        makeDatePicker(row.cells[4]);
        return row.cells[4];
    }

    for (const row of table.rows) {
        if (isFile && row.rowIndex === 0) {
            continue;
        }

        addUtilButtons(table, row, makePrdplanSelect, makeMatSelect, makeDate);
    }
}

function initEmptyOrderingTable(table, isFile = false) {
    const conmatOptions = contmatDBData.map(data => data[dbElementNames.conmatId]);
    const pplanOptions = pplanDBData.map(data => data[dbElementNames.pplanId]);

    const makeConmatSelect = (row) => {
        makeSelectForRawCell(row.cells[1], conmatOptions);
        return row.cells[1];
    }

    const makePPlanSelect = (row) => {
        makeSelectForRawCell(row.cells[2], pplanOptions);
        return row.cells[2];
    }

    const makeStartDate = (row) => {
        makeDatePicker(row.cells[4]);
        return row.cells[4];
    }

    const makeEndDate = (row) => {
        makeDatePicker(row.cells[5]);
        return row.cells[5];
    }

    const makeStatSelect = (row) => {
        makeSelectForRawCell(row.cells[6], ["진행중", "완료"]);
        return row.cells[6];
    }

    const makeMin = (row) => {
        const dates = row.querySelectorAll("input[type='date']");
        console.log(dates);
        dates[0].addEventListener("change", function () {
            dates[1].min = dates[0].value;
            dates[1].value = dates[1].value < dates[0].value ? dates[0].value : dates[1].value;
            dates[1].dispatchEvent(new Event("change"));
        });
        return row.cells[5];
    }

    for (const row of table.rows) {
        if (isFile && row.rowIndex === 0) {
            continue;
        }

        addUtilButtons(table, row, makeConmatSelect, makePPlanSelect, makeStartDate, makeEndDate, makeStatSelect, makeMin);
    }
}

// 목록 테이블용 fetch 함수
async function refreshTableView(dest, request = "") {
    return await fetch(dest, {
        method: "POST",
        headers: {
            // 토큰 추가
            'X-CSRF-TOKEN': document.querySelector('meta[name="_csrf"]').getAttribute('content'),
            "Content-Type": "text/plain"
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

// DB 받아온 내용으로 테이블을 갱신한다.
function reloadTable(table, request, tar) {
    let targetDB;

    switch (request) {
        case "mat":
        case "material":
            targetDB = matDBData;
            break;
        case "prd":
        case "product":
            targetDB = prdDBData;
            break;
        case "pbom":
            targetDB = pbomDBData;
            break;
        case "prdplan":
        case "productionplan":
            targetDB = prdplanDBData;
            break;
        case "prdplanedit":
        case "productionplanedit":
            targetDB = prdplanDBEditData;
            break;
        case "pplan":
        case "procureplan":
            targetDB = pplanDBData;
            break;
        case "pplanedit":
        case "procureplanedit":
            targetDB = pplanDBEditData;
            break;
        default:
            targetDB = tar;
    }

    const sheet = XLSX.utils.json_to_sheet(targetDB);
    const htmlSheet = XLSX.utils.sheet_to_html(sheet);
    const startIndex = htmlSheet.indexOf("<tr", htmlSheet.indexOf("<tr") + 1);
    const endIndex = htmlSheet.lastIndexOf("</tr>") + "</tr>".length;
    const htmlRow = htmlSheet.substring(startIndex, endIndex);

    const tbody = table.querySelector("tbody");
    tbody.innerHTML = htmlRow;
}

// 수정 테이블을 갱신한다.
function viewEditTable(table, request) {
    const result = viewTable(table, request);
    result.then((result) => {
        addTableEditButtons(table);
        originalValues = {};
    });
}

// Product 페이지에 필요한 DB 갱신한다.
async function refreshProductDBDataAll() {
    const result = Promise.all([
        refreshTableView("/internal/product/view/mat", "material"),
        refreshTableView("/internal/product/view/prd", "product"),
        refreshTableView("/internal/product/view/pbom", "pbom"),
    ]);

    return result.then(
        (values) => {
            matDBData = values[0];
            prdDBData = values[1];
            pbomDBData = values[2];

            return values;
        },
        (reason) => {
            return reason;
        },
    );
}

// 생산계획 페이지에 필요한 DB 갱신
async function refreshPrdPlanDBDataAll() {
    const result = Promise.all([
        refreshTableView("/internal/product/view/prdplan", "prdplan"),
        refreshTableView("/internal/product/view/prd", "product"),
        ]);

    return result.then(
        (values) => {
            prdplanDBData = values[0];
            prdDBData = values[1];
            prdplanDBEditData = Array.from(values[0]).map(prd => {
                const keys = Object.keys(prd);
                const ePrd = {};
                ePrd[keys[0]] = prd[keys[0]];
                ePrd[keys[1]] = prd[keys[1]];
                ePrd[keys[3]] = prd[keys[3]];
                ePrd[keys[5]] = prd[keys[5]];
                return ePrd;
            });

            return values;
        },
        (reason) => {
            return reason;
        }
    );
}

async function refreshPPlanDBDataAll() {
    const result = Promise.all([
        refreshTableView("/internal/procurement/view/pplan", "procure"),
        refreshTableView("/internal/product/view/prdplan", "prdplan"),
        refreshTableView("/internal/product/view/mat", "material")
    ]);

    return result.then(
        (values) => {
            pplanDBData = values[0];
            prdplanDBData = values[1];
            matDBData = values[2];
            pplanDBEditData = Array.from(values[0]).map(pplan => {
                const keys = Object.keys(pplan);
                const ePPlan = {};
                ePPlan[keys[0]] = pplan[keys[0]];
                ePPlan[keys[1]] = pplan[keys[1]];
                ePPlan[keys[2]] = pplan[keys[2]];
                ePPlan[keys[4]] = pplan[keys[4]];
                ePPlan[keys[6]] = pplan[keys[6]];
                ePPlan[keys[7]] = pplan[keys[7]];

                console.log(ePPlan);
                return ePPlan;
            });

            return values;
        },
        (reason) => {
            return reason;
        }
    );
}

async function refreshOrderingDBDataAll() {
    const result = Promise.all([
        refreshTableView("/internal/procurement/view/order", "order"),
        refreshTableView("/internal/procurement/view/pplan", "pplan"),
        refreshTableView("/internal/procurement/view/contmat", "contmat")
    ]);

    return result.then(
        (values) => {
            orderDBData = values[0];
            pplanDBData = values[1];
            contmatDBData = values[2];
            console.log(values);
            return values;
        },
        (reason) => {
            return reason;
        }
    );
}

function makeUniCellMessage(table, message) {
    const tr = document.createElement("tr");
    tr.insertCell(-1);

    tr.cells[0].colSpan = table.rows[0].cells.length;
    tr.cells[0].innerText = message;

    table.tBodies[0].innerHTML = "";

    table.tBodies[0].appendChild(tr);
}

// Product 페이지의 모든 수정 및 목록 테이블 갱신
function viewAllProductTable() {
    const refresh = refreshProductDBDataAll();
    refresh.then(value => {
        const matViewTable = document.querySelector("#mat-table-view");
        const prdViewTable = document.querySelector("#prd-table-view");
        const pbomViewTable = document.querySelector("#pbom-table-view");

        const matEditTable = document.querySelector("#mat-table-edit");
        const prdEditTable = document.querySelector("#prd-table-edit");
        const pbomEditTable = document.querySelector("#pbom-table-edit");

        const pbomInputTable = document.querySelector("#pbom-table");

        console.log(matDBData);
        console.log(prdDBData);

        if (value[0].length > 0) {
            reloadTable(matViewTable, "material");
            reloadTable(matEditTable, "material");

            addTableEditButtons(matEditTable);
        } else {
            makeUniCellMessage(matViewTable, "등록된 내용이 없습니다.");
            makeUniCellMessage(matEditTable, "수정할 내용이 없습니다.");
        }

        if (value[1].length > 0) {
            reloadTable(prdViewTable, "product");
            reloadTable(prdEditTable, "product");

            addTableEditButtons(prdEditTable);
        } else {
            makeUniCellMessage(prdViewTable, "등록된 내용이 없습니다.");
            makeUniCellMessage(prdEditTable, "수정할 내용이 없습니다.");
        }

        if (value[2].length > 0) {
            reloadTable(pbomViewTable, "pbom");
            reloadTable(pbomEditTable, "pbom");

            addTableEditButtons(pbomEditTable);

            makeTableCellDBSelectWithLabel(pbomEditTable, 1, matDBData, dbElementNames.pbomMatId, dbElementNames.matName);
            makeTableCellDBSelectWithLabel(pbomEditTable, 2, prdDBData, dbElementNames.pbomProdId, dbElementNames.prodName);
        } else {
            makeUniCellMessage(pbomViewTable, "등록된 내용이 없습니다.");
            makeUniCellMessage(pbomEditTable, "수정할 내용이 없습니다.");
        }

        originalValues = {};

        initEmptyPbomTable(pbomInputTable);
    });
}

// 생산계획 테이블 목록 갱신
function viewPrdPlanTable() {
    const refresh = refreshPrdPlanDBDataAll();
    refresh.then(value => {
        const prdplanViewTable = document.querySelector("#prdplan-table-view");
        const prdplanEditTable = document.querySelector("#prdplan-table-edit");
        const prdplanInputTable = document.querySelector("#prdplan-table");

        if (value[0].length > 0) {
            reloadTable(prdplanViewTable, "prdplan");
            reloadTable(prdplanEditTable, "prdplanedit");

            addTableEditButtons(prdplanEditTable);

            makeTableCellDBSelectWithLabel(prdplanEditTable, 1, prdDBData, dbElementNames.prodId, dbElementNames.prodName);
            makeTableCellDatePicker(prdplanEditTable, 3);
        } else {
            makeUniCellMessage(prdplanViewTable, "등록된 내용이 없습니다.");
            makeUniCellMessage(prdplanEditTable, "수정할 내용이 없습니다.");
        }
        originalValues = {};

        initEmptyPrdPlanTable(prdplanInputTable);
    });
}

function viewPPlanTable() {
    const refresh = refreshPPlanDBDataAll();
    refresh.then(value => {
        const pplanViewTable = document.querySelector("#pplan-table-view");
        const pplanEditTable = document.querySelector("#pplan-table-edit");
        const pplanInputTable = document.querySelector("#pplan-table");

        if (value[0].length > 0) {
            reloadTable(pplanViewTable, "pplan");
            reloadTable(pplanEditTable, "pplanedit");

            addTableEditButtons(pplanEditTable);

            makeTableCellDBSelect(pplanEditTable, 1, prdplanDBData, dbElementNames.prdplanId);
            makeTableCellDBSelect(pplanEditTable, 2, matDBData, dbElementNames.matId);
        } else {
            makeUniCellMessage(pplanViewTable, "등록된 내용이 없습니다.");
            makeUniCellMessage(pplanEditTable, "수정할 내용이 없습니다.");
        }

        originalValues = {};

        initEmptyPPlanTable(pplanInputTable);
    });
}

function viewOrderingTable() {
    const refresh = refreshOrderingDBDataAll();
    refresh.then(value => {
        const orderViewTable = document.querySelector("#order-table-view");
        const orderEditTable = document.querySelector("#order-table-edit");
        const orderInputTable = document.querySelector("#order-table");

        if (value[0].length > 0) {
            reloadTable(orderViewTable, "", orderDBData);
            reloadTable(orderEditTable, "", orderDBData);

            addTableEditButtons(orderEditTable);

            makeTableCellDBSelect(orderEditTable, 1, contmatDBData, dbElementNames.conmatId);
            makeTableCellDBSelect(orderEditTable, 2, pplanDBData, dbElementNames.pplanId);
            makeTableCellDatePicker(orderEditTable, 4);
            makeTableCellDatePicker(orderEditTable, 5);
            makeTableCellSelect(orderEditTable, 6, "진행중", "완료");

            for (const row of orderEditTable.rows) {
                const dates = row.querySelectorAll("input[type='date']");
                if (dates === null || dates.length === 0) {
                    continue;
                }
                dates[0].addEventListener("change", function () {
                    dates[1].min = dates[0].value;
                    dates[1].value = dates[1].value < dates[0].value ? dates[0].value : dates[1].value;
                    dates[1].dispatchEvent(new Event("change"));
                });
            }
        } else {
            makeUniCellMessage(orderViewTable, "등록된 내용이 없습니다.");
            makeUniCellMessage(orderEditTable, "수정할 내용이 없습니다.");
        }

        originalValues = {};

        initEmptyOrderingTable(orderInputTable);
    });
}

// span 을 자식으로 갖지 않은 셀에 select 추가하기
function makeSelectForRawCell(cell, ...options) {
    let value = cell.innerText;
    const span = document.createElement("span");
    span.innerHTML = value;
    cell.innerText = "";
    cell.appendChild(span);
    makeSelect(cell, options[0]);
}

// 셀 안에 Select Element 넣기
function makeSelect(cell, ...options) {
    const select = document.createElement("select");
    select.classList.add("indirect-select");

    for (const option of options[0]) {
        const opt = document.createElement("option");

        if (Array.isArray(option)) {
            opt.value = option[0];
            opt.label = option[1] + " (" + option[0] + ")";
        } else {
            opt.value = option;
            opt.label = option;
        }
        select.appendChild(opt);
    }

    select.addEventListener("change", function () {
        let span = cell.querySelector("span");
        if (span === null) {
            span = document.createElement("span");
            cell.appendChild(span);
        }

        span.innerText = select.value;
    });
    select.value = "";
    cell.appendChild(select);
}

// DB 데이터를 참조하여 셀을 Select 로 만든다.
function makeTableCellDBSelect(table, cellIndex, dbData, dbElement) {
    const rows = table.rows;
    const options = dbData.map(data => data[dbElement]);

    for (let i = 1; i < rows.length; i++) {
        const cell = rows[i].cells[cellIndex];
        makeSelectForRawCell(cell, options);
    }
}

function makeTableCellDBSelectWithLabel(table, cellIndex, dbData, dbElement, dbLabel) {
    const rows = table.rows;
    const options = dbData.map(data => [data[dbElement], data[dbLabel]]);

    for (let i = 1; i < rows.length; i++) {
        const cell = rows[i].cells[cellIndex];
        makeSelectForRawCell(cell, options);
    }
}

function makeTableCellDatePicker(table, cellIndex) {
    const rows = table.rows;
    const result = [];

    for (let i = 1; i < rows.length; i++) {
        const cell = rows[i].cells[cellIndex];
        result[i - 1] = makeDatePickerRawCell(cell);
    }

    return result;
}

function makeDatePickerRawCell(cell) {
    let value = cell.innerText;
    const span = document.createElement("span");
    span.innerHTML = value;
    cell.innerText = "";
    cell.appendChild(span);
    return makeDatePicker(cell);
}

function makeDatePicker(cell) {
    const date = document.createElement("input");
    const today = new Date().toISOString();
    date.type = "date";
    date.classList.add("indirect-time");
    date.setAttribute("min", today.substring(0, today.indexOf("T")));

    date.addEventListener("change", function () {
        let span = cell.querySelector("span");
        if (span === null) {
            span = document.createElement("span");
            cell.appendChild(span);
        }

        span.innerText = date.value;
    });
    cell.appendChild(date);

    return date;
}

// 임의의 option 을 사용하여 셀을 select 로 만든다.
function makeTableCellSelect(table, cellIndex, ...options) {
    const rows = table.rows;

    for (let i = 1; i < rows.length; i++) {
        const cell = rows[i].cells[cellIndex];
        makeSelectForRawCell(cell, options);
    }
}

// 등록, 수정, 목록 전환 버튼
function initPageTable() {
    const viewGroup = document.getElementById("tables-view");
    const editGroup = document.getElementById("tables-edit");
    const inputGroup = document.getElementById("tables-input");

    const inputManageBtn = document.getElementById("input-manage-btn");
    const editManageBtn = document.getElementById("edit-manage-btn");
    const viewManageBtn = document.getElementById("view-manage-btn");

    const manageMod = document.getElementById("manage-mod");
    const insertLabel = manageMod.querySelector("#mod-insert");
    const editLabel = manageMod.querySelector("#mod-edit");
    const viewLabel = manageMod.querySelector("#mod-view");

    // 등록, 수정, 목록 라디오 간 전환 시각효과
    manageMod.addEventListener("click", function () {
        const labels = manageMod.querySelectorAll("label");

        for (const label of labels) {
            if (label.control.checked) {
                label.classList.add("pe-none");
                label.classList.remove("btn-outline-secondary");
                label.classList.add("btn-primary");
            } else {
                label.classList.remove("pe-none");
                label.classList.add("btn-outline-secondary");
                label.classList.remove("btn-primary");
            }
        }
    });

    // 등록, 수정, 목록 버튼 클릭시 테이블과 우측 패널 전환 이벤트
    insertLabel.addEventListener("change", function () {
        inputGroup.style.display = "block";
        editGroup.style.display = "none";
        viewGroup.style.display = "none";

        inputManageBtn.style.display = "flex";
        editManageBtn.style.display = "none";
        viewManageBtn.style.display = "none";

        const initial = document.querySelector("input[type='radio'][name='v-filter']:checked");
        if (initial !== null) {
            initial.dispatchEvent(new Event('change'));
        }
    });

    editLabel.addEventListener("change", function () {
        inputGroup.style.display = "none";
        editGroup.style.display = "block";
        viewGroup.style.display = "none";

        inputManageBtn.style.display = "none";
        editManageBtn.style.display = "flex";
        viewManageBtn.style.display = "none";

        const initial = document.querySelector("input[type='radio'][name='v-filter']:checked");
        if (initial !== null) {
            initial.dispatchEvent(new Event('change'));
        }
    });

    viewLabel.addEventListener("change", function () {
        inputGroup.style.display = "none";
        editGroup.style.display = "none";
        viewGroup.style.display = "block";

        inputManageBtn.style.display = "none";
        editManageBtn.style.display = "none";
        viewManageBtn.style.display = "flex";

        const initial = document.querySelector("input[type='radio'][name='v-filter']:checked");
        if (initial !== null) {
            initial.dispatchEvent(new Event('change'));
        }
    });

    // 툴팁 메시지 바인딩
    tutorialMessage.bindTutorialMessage(insertLabel, tmessage.manageInsertTutorial);
    tutorialMessage.bindTutorialMessage(editLabel, tmessage.manageEditTutorial);
    tutorialMessage.bindTutorialMessage(viewLabel, tmessage.manageViewTutorial);

    // 화면 초기화용 이벤트 발생
    const initial = document.querySelector("input[type='radio'][name='i-filter']:checked");
    initial.dispatchEvent(new Event('change'));
}

export {
    initPageTable,
    initEmptyTable,
    initEditButtons,
    viewAllProductTable,
    viewPrdPlanTable,
    viewPPlanTable,
    viewOrderingTable,
    addTableUtilBtn,
    addTableEditButtons,
    addPbomTableUtilBtn,
    addPrdPlanTableUtilBtn,
    addPPlanTableUtilBtn,
    addOrderTableUtilBtn,
    uploadEditedTable,
    viewChange,
};
