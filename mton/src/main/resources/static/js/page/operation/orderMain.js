import * as excelParser from "./module/excelParser.js"
import * as tableRowsEditor from "./module/tableRowsEditor.js"
import * as tutorialMessage from "./module/tutorialMessage.js"
import * as tmessage from "./module/tmessage.js"
import * as tableFilter from "./module/tableFilter.js"

const orderViewTable = document.querySelector("#order-table-view");
const orderEditTable = document.querySelector("#order-table-edit");
const orderInputTable = document.querySelector("#order-table");

let bomList = [];

(function () {
    tableRowsEditor.initPageTable();
    orderViewTable.style.display = "table";
    orderEditTable.style.display = "table";
    orderInputTable.style.display = "table";

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
    tableRowsEditor.viewOrderingTable(true);
    tableRowsEditor.initEditButtons(orderEditTable);
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
            tableRowsEditor.addOrderTableUtilBtn(currentTable);
        });
    });

    insertUploadBtn.addEventListener("click", function (e) {
        e.preventDefault();
        e.stopPropagation();

        const currentTable = document.querySelector("div[style='display: block;'] table");
        let overFlag = false;

        // 소요량 체크
        const insertValues = Array.from(orderInputTable.rows).map((row) => {
            if (row.cells.length < 7) {
                return {
                    pplanId: "",
                    cmtId: "",
                    qty: 0
                }
            }

            if (row.cells[5].innerText.includes("기한초과")) {
                overFlag = true;
                return 0;
            }

            return {
                pplanId: row.cells[1].innerText,
                cmtId: row.cells[2].innerText,
                qty: Number.parseInt(row.cells[3].innerText)
            }
        });

        if (overFlag) {
            alert("기한을 초과하는 계약자재가 있습니다.");
            return;
        }
        insertValues.shift();

        const viewValues = Array.from(orderViewTable.rows).filter(x => x.cells[6].innerText === "발주중" || x.cells[6].innerText === "진행중").map((row) => {
            if (row.cells.length < 7) {
                return {
                    pplanId: "",
                    cmtId: "",
                    qty: 0
                }
            }

            return {
                pplanId: row.cells[1].innerText,
                cmtId: row.cells[2].innerText,
                qty: Number.parseInt(row.cells[3].innerText)
            }
        });
        viewValues.shift();
        console.log(viewValues);
        console.log(insertValues);

        for (const value of viewValues) {
            const test = insertValues.filter(i => i.pplanId === value.pplanId && i.cmtId === value.cmtId);
            console.log(test);
            if (test.length > 0) {
                test[0].qty += value.qty;
            }
        }

        const ord = document.getElementById("pplan-select");
        const ordIds = Array.from(ord.options).map(x => x.value);
        const cDest = "/internal/procurement/calc/order/all";

        const cResult = jsonFetcherList(cDest, ordIds);
        cResult.then(res => {
            bomList = res;
            console.log(bomList);

            let nameList = [];
            let flag = true;
            console.log(insertValues);

            for (const value of insertValues) {
                const test = bomList.filter(p => p.pplanId === value.pplanId && p.cmtId === value.cmtId && p.ppmatQty < value.qty);
                if (test.length > 0) {
                    nameList.push(test[0].pplanId + ":" + test[0].cmtId + "(" + value.qty + ":" + test[0].ppmatQty + ")");
                }
            }

            if (nameList.length > 0) {
                let message = "";
                for (const name of nameList) {
                    message = message.concat(name + "\n");
                }
                flag = confirm("다음 계획:자재 조합은 (현재치:허용치)를 넘어섰습니다:\n" + message + "\n" + "진행하시겠습니까?");
            }

            if (flag) {
                const dest = "/internal/procurement/register/order";
                const result = excelParser.tableUpload(dest, currentTable);
                result.then(res => {
                    alert(res.message);
                    viewRefreshBtn.dispatchEvent(new Event("click"));
                    editRefreshBtn.dispatchEvent(new Event("click"));
                    tableRowsEditor.viewChange();
                });
            } else {
                alert("발주가 취소되었습니다.");
            }
        });

    });

    viewRefreshBtn.addEventListener("click", function (e) {
        e.preventDefault();
        e.stopPropagation();

        tableRowsEditor.viewOrderingTable();
    });

    viewDownloadBtn.addEventListener("click", function (e) {
        e.preventDefault();
        e.stopPropagation();

        const currentTable = document.querySelector("div[style='display: block;'] table");

        const editedTable = currentTable.cloneNode(true);
        excelParser.tableToFile(editedTable, currentTable.id + ".xlsx");
    });

    editRefreshBtn.addEventListener("click", function (e) {
        e.preventDefault();
        e.stopPropagation();

        tableRowsEditor.viewOrderingTable();
    });

    editUploadBtn.addEventListener("click", function (e) {
        e.preventDefault();
        e.stopPropagation();

        const currentTable = document.querySelector("div[style='display: block;'] table");
        const del = "/internal/procurement/delete/order";
        const upd = "/internal/procurement/update/order";
        const deleteResult = tableRowsEditor.uploadEditedTable(currentTable, "", del, upd);

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
    tutorialMessage.bindTutorialMessage(viewDownloadBtn, tmessage.viewDownloadBtnTutorial)
    tutorialMessage.bindTutorialMessage(editRefreshBtn, tmessage.editRefreshBtnTutorial);
    tutorialMessage.bindTutorialMessage(editUploadBtn, tmessage.editUploadBtnTutorial);
})();

(function () {
    tableFilter.applyTableSorting(orderViewTable);

    // 자재발주 자동생성 버튼 이벤트 등록
    const ordButton = document.getElementById("ord-btn");
    ordButton.addEventListener("click", function (e) {
        e.preventDefault();
        e.stopPropagation();

        const ordId = document.getElementById("pplan-select").value;
        const dest = "/internal/procurement/calc/order";

        const result = jsonFetcher(dest, ordId);
        result.then(res => {
            bomList = res;

            if (res === null) {
                alert("계획에 사용 가능한 계약자재들이 없습니다. 먼저 계약자재를 등록해주세요.");
                return;
            }

            const flag = automaticOrder(res, orderInputTable);
            if (flag) {
                tableRowsEditor.addOrderTableUtilBtn(orderInputTable);
            } else {
                alert("계약자재는 있지만, 납기일에 맞출 수 없습니다.");
            }
        });
    });

    const ordAllBtn = document.getElementById("ord-btn-all");
    ordAllBtn.addEventListener("click", function (e) {
        e.preventDefault();
        e.stopPropagation();

        const ord = document.getElementById("pplan-select");
        const ordIds = Array.from(ord.options).map(x => x.value);
        const dest = "/internal/procurement/calc/order/all";

        const result = jsonFetcherList(dest, ordIds);
        result.then(res => {
            console.log(res);
            bomList = res;

            if (res === null) {
                alert("계획에 사용 가능한 계약자재들이 없습니다. 먼저 계약자재를 등록해주세요.");
                return;
            }

            const flag = automaticOrder(res, orderInputTable);
            if (flag) {
                tableRowsEditor.addOrderTableUtilBtn(orderInputTable);
            } else {
                alert("계약자재는 있지만, 납기일에 맞출 수 있는 경우가 없습니다.");
            }

            if(ordIds.length !== orderInputTable.rows.length - 1) {
                alert("일부 계획에 맞출 수 있는 계약자재들이 없어 발주를 생성하지 못했습니다. 빠진 내용을 확인해주세요.");
            }
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
function automaticOrder(jData, table, append = false) {
    const newContent = [];
    const today = new Date();

    for (const data of jData) {
        const endDay = new Date();
        endDay.setDate(endDay.getDate() + data.cmtReq);
        const ordEnd = new Date(data.pplanEnd);

        const row = {
            orderId: "",
            pplanId: data.pplanId,
            cmtId: data.cmtId,
            ppmatQty: data.ppmatQty,
            orderDate: today.toISOString().substring(0, 10),
            orderEnd: endDay < ordEnd ? endDay.toISOString().substring(0, 10)
                : Math.floor((endDay - ordEnd) / (60 * 60 * 24 * 1000)) === 0 ? endDay.toISOString().substring(0, 10)
                    : "기한초과(" + Math.floor((endDay - ordEnd) / (60 * 60 * 24 * 1000)) + "일)",
            orderStat: "발주중",
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
    return true;
}
