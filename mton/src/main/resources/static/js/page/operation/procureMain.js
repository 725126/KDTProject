import * as excelParser from "./module/excelParser.js"
import * as tableRowsEditor from "./module/tableRowsEditor.js"
import * as tutorialMessage from "./module/tutorialMessage.js"
import * as tmessage from "./module/tmessage.js"
import * as tableFilter from "./module/tableFilter.js"

// 테이블
const pplanViewTable = document.querySelector("#pplan-table-view");
const pplanEditTable = document.querySelector("#pplan-table-edit");
const pplanInputTable = document.querySelector("#pplan-table");

// 생산계획 PBOM 파싱용
let pbomList = [];

// 계획관리 모드 라디오 버튼 이벤트 등록
(function () {
    tableRowsEditor.initPageTable();
    pplanViewTable.style.display = "table";
    pplanEditTable.style.display = "table";
    pplanInputTable.style.display = "table";

    const iFilters = document.querySelectorAll("input[type='radio'][name='i-filter']");
    const tableCard = document.getElementById("table-card");
    const btnCard = document.querySelector("#btn-card");
    btnCard.classList.add("d-none");

    for (const filter of iFilters) {
        filter.addEventListener("change", function () {
            const enabled = document.querySelector("input[type='radio'][name='i-filter']:checked");
            if (enabled.id === "mod-insert") {
                tableCard.classList.add("max-height-pplan");
                btnCard.classList.remove("d-none");
            } else {
                tableCard.classList.remove("max-height-pplan");
                btnCard.classList.add("d-none");
            }
        });
    }
})();

// 수정, 등록용 테이블 초기화
// 필요없는 열을 삭제하고 수정 테이블에는 초기 내용을 삽입
(function () {
    for (const row of pplanInputTable.rows) {
        row.deleteCell(7);
        row.deleteCell(5);
        row.deleteCell(3);
    }

    for (const row of pplanEditTable.rows) {
        row.deleteCell(5);
        row.deleteCell(3);
    }

    tableRowsEditor.viewPPlanTable(true);
    tableRowsEditor.initEditButtons(pplanEditTable);
})();

(function () {
    const insertFileBtn = document.getElementById("insert-file");
    const insertFileLabel = document.getElementById("insert-file-label");
    const insertUploadBtn = document.getElementById("insert-upload");
    const editRefreshBtn = document.getElementById("edit-refresh");
    const editUploadBtn = document.getElementById("edit-upload");
    const viewRefreshBtn = document.getElementById("view-refresh");
    const viewDownloadBtn = document.getElementById("view-download");

    insertFileBtn.addEventListener("change", function (e) {
        const currentTable = document.querySelector("div[style='display: block;'] table");
        const result = excelParser.sheetToTable(insertFileBtn.files[0], currentTable, true, -2);

        result.then(value => {
            tableRowsEditor.addPPlanTableUtilBtn(currentTable);
        });
    });

    insertUploadBtn.addEventListener("click", function (e) {
        e.preventDefault();
        e.stopPropagation();

        const currentTable = document.querySelector("div[style='display: block;'] table");

        // 조달량 체크 >>
        const insertValues = Array.from(pplanInputTable.rows).map((row) => {
            if (row.cells.length < 4) {
                return {
                    prdplanId: "",
                    matId: "",
                    qty: 0
                }
            }

            return {
                prdplanId: row.cells[1].innerText,
                matId: row.cells[2].innerText,
                qty: Number.parseInt(row.cells[3].innerText)
            };
        });
        insertValues.shift();

        const viewValues = Array.from(pplanViewTable.rows).map((row) => {
            if (row.cells.length < 5) {
                return {
                    prdplanId: "",
                    matId: "",
                    qty: 0
                }
            }

            return {
                prdplanId: row.cells[1].innerText,
                matId: row.cells[2].innerText,
                qty: Number.parseInt(row.cells[4].innerText)
            };
        });
        viewValues.shift();

        for (const value of viewValues) {
            const test = insertValues.filter(i => i.prdplanId === value.prdplanId && i.matId === value.matId);
            if (test.length > 0) {
                test[0].qty += value.qty;
            }
        }

        const pplan = document.getElementById("prdplan-select");
        const pplanIds = Array.from(pplan.options).map(x => x.value);
        console.log(pplanIds);
        const cDest = "/internal/procurement/calc/pplan/all";

        const cResult = jsonFetcherList(cDest, pplanIds);
        cResult.then(res => {
            pbomList = res;
            console.log(pbomList);

            let nameList = [];
            let flag = true;

            for (const value of insertValues) {
                const test = pbomList.filter(p => p.prdplanId === value.prdplanId && p.matId === value.matId && p.pbomMaxQty < value.qty);
                if (test.length > 0) {
                    nameList.push(test[0].prdplanId + ":" + test[0].matId + "(" + value.qty + ":" + test[0].pbomMaxQty + ")");
                }
            }

            if (nameList.length > 0) {
                let message = "";
                for (const name of nameList) {
                    message = message.concat(name + "\n");
                }
                flag = confirm("다음 계획:자재 조합은 (현재치:허용치)를 넘어섰습니다:\n" + message + "\n진행하시겠습니까?");
            }

            if (flag) {
                const dest = "/internal/procurement/register/pplan";
                const result = excelParser.tableUpload(dest, currentTable);
                result.then(res => {
                    alert(res.message);
                    viewRefreshBtn.dispatchEvent(new Event("click"));
                    editRefreshBtn.dispatchEvent(new Event("click"));
                    tableRowsEditor.viewChange();
                });
            } else {
                alert("PPLAN 등록이 취소되었습니다.");
            }
        });
    });

    viewRefreshBtn.addEventListener("click", function (e) {
        e.preventDefault();
        e.stopPropagation();

        tableRowsEditor.viewPPlanTable();
    });

    viewDownloadBtn.addEventListener("click", function (e) {
        e.preventDefault();
        e.stopPropagation();

        const currentTable = document.querySelector("div[style='display: block;'] table");

        const editedTable = currentTable.cloneNode(true);
        for (const row of editedTable.rows) {
            row.deleteCell(7);
            row.deleteCell(5);
            row.deleteCell(3);
        }
        excelParser.tableToFile(editedTable, currentTable.id + ".xlsx");
    });

    editRefreshBtn.addEventListener("click", function (e) {
        e.preventDefault();
        e.stopPropagation();

        tableRowsEditor.viewPPlanTable();
    });

    editUploadBtn.addEventListener("click", function (e) {
        e.preventDefault();
        e.stopPropagation();

        const currentTable = document.querySelector("div[style='display: block;'] table");
        const deleteResult = tableRowsEditor.uploadEditedTable(currentTable, "pplan");

        if (deleteResult === null) { console.log("테이블 참조가 잘못되었습니다."); return; }

        deleteResult.then(res => {
            alert(res.message);
            editRefreshBtn.dispatchEvent(new Event("click"));
            viewRefreshBtn.dispatchEvent(new Event("click"));
        });
    });

    tutorialMessage.bindTutorialMessage(insertFileLabel, tmessage.insertFileBtnTutorial);
    tutorialMessage.bindTutorialMessage(insertUploadBtn, tmessage.insertUploadBtnTutorial);
    tutorialMessage.bindTutorialMessage(viewRefreshBtn, tmessage.viewRefreshBtnTutorial);
    tutorialMessage.bindTutorialMessage(viewDownloadBtn, tmessage.viewDownloadBtnTutorial);
    tutorialMessage.bindTutorialMessage(editRefreshBtn, tmessage.editRefreshBtnTutorial);
    tutorialMessage.bindTutorialMessage(editUploadBtn, tmessage.editUploadBtnTutorial);
})();

(function () {
    tableFilter.applyTableSorting(pplanViewTable);

    // 조달계획 자동생성 버튼 이벤트 등록
    const pplanButton = document.getElementById("pplan-btn");
    pplanButton.addEventListener("click", function (e) {
        e.preventDefault();
        e.stopPropagation();

        const pplanId = document.getElementById("prdplan-select").value;
        const dest = "/internal/procurement/calc/pplan";

        const result = jsonFetcher(dest, pplanId);
        result.then(res => {
            pbomList = res;
            automaticPplan(res, pplanInputTable);
            // 성능 문제로 버튼 다는 것은 포기
            // tableRowsEditor.addPPlanTableUtilBtn(pplanInputTable);
        });
    });

    // 모든 조달계획 자동생성 버튼 이벤트 등록
    const pplanAllBtn = document.getElementById("pplan-btn-all");
    pplanAllBtn.addEventListener("click", function (e) {
        e.preventDefault();
        e.stopPropagation();

        const pplan = document.getElementById("prdplan-select");
        const pplanIds = Array.from(pplan.options).map(x => x.value);
        const dest = "/internal/procurement/calc/pplan/all";

        const result = jsonFetcherList(dest, pplanIds);
        result.then(res => {
            pbomList = res;
            automaticPplan(res, pplanInputTable);
            // 성능 문제로 버튼 다는 것은 포기
            // tableRowsEditor.addPPlanTableUtilBtn(pplanInputTable);
        });
    });
})();

// JSON 페치용
async function jsonFetcher(dest, jData) {
    return await fetch(dest, {
        method: "POST",
        headers: {
            "Content-Type": "text/plain",
            "X-CSRF-TOKEN": document.querySelector('meta[name="_csrf"]').getAttribute('content')
        },
        body: jData
    }).then((res) => {
        return res.json();
    }).then(data => {
        return data;
    }).catch(error => {
        console.log(error);
    });
}

// 여러 항목에 대한 json 페치
async function jsonFetcherList(dest, jDatas) {
    return await fetch(dest, {
        method: "POST",
        headers: {
            "Content-Type": "application/json",
            "X-CSRF-TOKEN": document.querySelector('meta[name="_csrf"]').getAttribute('content')
        },
        body: JSON.stringify(jDatas)
    }).then(res => {
        return res.json();
    }).then(data => {
        return data;
    }).catch(error => {
        console.log(error);
    });
}

// 자동생성 로직
function automaticPplan(jData, table, append = false) {
    const newContent = [];

    for (const data of jData) {
        const row = {
            pplanId: "",
            prdplanId: data.prdplanId,
            matId: data.matId,
            pbomMaxQty: data.pbomMaxQty,
            prdplanEnd: data.prdplanEnd,
        }
        newContent.push(row);
    }

    const sheet = XLSX.utils.json_to_sheet(newContent);
    const htmlSheet = XLSX.utils.sheet_to_html(sheet, {editable: true});
    const startIndex = htmlSheet.indexOf("<tr", htmlSheet.indexOf("<tr") + 1);

    if (startIndex === -1) {
        console.log("file is match with table, but does not have data.");
        return false;
    }

    const endIndex = htmlSheet.lastIndexOf("</tr>") + "</tr>".length;
    const htmlRow = htmlSheet.substring(startIndex, endIndex);

    const tbody = table.querySelector("tbody");
    if (append) {
        tbody.innerHTML = tbody.innerHTML + htmlRow;
    } else {
        tbody.innerHTML = htmlRow;
    }
}
