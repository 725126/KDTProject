const fileInput = document.getElementById('contract-file');
const fileListContainer = document.getElementById('file-list');

if (fileInput != null) {
    fileInput.addEventListener('change', () => {
        fileListContainer.innerHTML = ''; // 기존 리스트 초기화
        Array.from(fileInput.files).forEach(file => {
            const fileItem = document.createElement('div');
            fileItem.className = 'file-item';
            fileItem.innerHTML = `<i class="bi bi-file-earmark"></i> ${file.name}`;
            fileListContainer.appendChild(fileItem);
        });
    });
}
