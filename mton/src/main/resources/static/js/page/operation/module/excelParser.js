// Codes below need sheetjs dependency:
// <script lang="javascript" src="https://cdn.sheetjs.com/xlsx-0.20.3/package/dist/xlsx.full.min.js"></script>

// 파일을 읽은 다음 워크북을 리턴한다.
async function parseExcel(file) {
    try {
        const data = await file.arrayBuffer();
        return XLSX.read(data);
    } catch (e) {
        throw new Error(`Failed to parseSheet(${e.message})`);
    }
}

// 시트의 첫 번째 행, 즉 시트 테이블 헤더를 반환한다.
function sheetHeaders(sheet) {
    return XLSX.utils.sheet_to_json(sheet, { header: 1 })[0];
}

// 테이블의 첫 번째 행, 즉 html 테이블 헤더를 반환한다.
function tableHeaders(table) {
    return Array.from(table.querySelector("tr").children).map(x => x.textContent);
}

// 두 배열이 같은 내용을 가지고 있는지 확인한다.
function compareArrays(arr1, arr2) {
    return arr1.length === arr2.length && arr1.every((element, index) => element === arr2[index]);
}

// 테이블 검증 및 내용을 삽입한다.
// edit 에 true 를 입력하면 삽입된 td 가 수정 가능 상태가 된다.
// 대상 테이블에는 thead, th, tbody 가 포함되어야 한다.
function sheetToTable(file, table, edit = false) {
    const workbook = parseExcel(file);
    workbook.then((value) => {
        const sheet = value.Sheets[value.SheetNames[0]];

        if (!compareArrays(sheetHeaders(sheet), tableHeaders(table))) {
            console.log("file does not match with table.");
        }

        const htmlSheet = XLSX.utils.sheet_to_html(sheet, {editable: edit});
        const startIndex = htmlSheet.indexOf("<tr", htmlSheet.indexOf("<tr") + 1);

        if (startIndex === -1) {
            console.log("file is match with table, but does not have data.");
        }

        const endIndex = htmlSheet.lastIndexOf("</tr>") + "</tr>".length;
        const htmlRow = htmlSheet.substring(startIndex, endIndex);

        const tbody = table.querySelector("tbody");
        tbody.innerHTML = htmlRow;
    });
}

// 지정한 테이블을 Excel 로 다운로드한다.
// 파일 이름을 지정하지 않을 경우 "document.xlsx"로 다운로드하며,
// 파일 이름을 지정할 경우 ".xlsx" 확장자가 필요하다.
function tableToFile(table, filename = "") {
    // 워크북, 시트, JSON 변환된 시트, 셀 내용의 길이
    const workbook = XLSX.utils.table_to_book(table);
    const worksheet = workbook.Sheets[workbook.SheetNames[0]];
    const jsheet = XLSX.utils.sheet_to_json(worksheet, {header: 1});
    const lens = jsheet.map(x => x.map(y => y.toString().length));
    const maxchar = Array(lens[0].length).fill(0);

    // 최대 내용 길이를 maxchar 에 넣음
    for (let i = 0; i < maxchar.length; i++) {
        for (const row of lens) {
            maxchar[i] = Math.max(maxchar[i], row[i]);
        }
    }

    // 열의 너비를 px 단위로 설정
    // 영문일 경우 wch 를 쓰면 되겠지만 그놈의 한글 너비가 문제다
    for (let i = 0; i < maxchar.length; i++) {
        worksheet["!cols"][i] = {wpx: maxchar[i] * 12};
    }

    let downFile = "document.xlsx";
    if (filename.length > 0) {
        downFile = filename;
    }

    XLSX.writeFile(workbook, downFile);
}

// JSON 데이터를 지정한 url 로 업로드.
// 이후 response 데이터를 받아 넘겨준다.
async function uploadJsonPost(dest, jdata) {
    return await fetch(dest, {
        method: "POST",
        headers: {
            "Content-Type": "application/json",
        },
        body: JSON.stringify(jdata)
    }).then(response => {
        return response.json();
    }).then(data => {
        return data;
    }).catch(error => {
        console.log(error);
    });
}

// 실제 보이는 테이블 데이터를 업로드.
// Response 받아서 뭔가 더 할 예정이라면 그냥 이 함수를 복사해서 수정하면 된다.
function tableUpload(dest, table) {
    const sheet = XLSX.utils.table_to_sheet(table);
    const jsheet = XLSX.utils.sheet_to_json(sheet);

    const result = uploadJsonPost(dest, jsheet);
    result.then((res) => {
        // console.log(res) 를 지우고 원하는 코드를 넣을 것.
        console.log(res);
    }).catch(error => {
        // console.log(error) 를 지우고 원하는 코드를 넣을 것.
        console.log(error);
    });
}

export {
    sheetToTable,
    tableToFile,
    tableUpload
};
