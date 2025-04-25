const reasonSelect = document.getElementById('reasonSelect');
const customReason = document.getElementById('customReason');
const confirmCheckbox = document.getElementById('confirm');
const withdrawBtn = document.getElementById('withdrawBtn');

// 기타 선택 시 텍스트창 보이기
reasonSelect.addEventListener('change', function () {
    if (this.value === 'other') {
        customReason.classList.remove('hidden');
    } else {
        customReason.classList.add('hidden');
    }
    updateWithdrawButtonState();
});

// 체크박스 상태 변경 시
confirmCheckbox.addEventListener('change', updateWithdrawButtonState);

// 텍스트 입력 시에도 버튼 상태 업데이트 (기타일 때만 필요)
customReason.addEventListener('input', updateWithdrawButtonState);

function updateWithdrawButtonState() {
    const reasonSelected = reasonSelect.value !== '';
    const confirmed = confirmCheckbox.checked;
    const isOther = reasonSelect.value === 'other';
    const otherReasonValid = !isOther || (isOther && customReason.value.trim() !== '');

    if (reasonSelected && confirmed && otherReasonValid) {
        withdrawBtn.disabled = false;
        withdrawBtn.classList.remove('bg-gray-300', 'cursor-not-allowed');
        withdrawBtn.classList.add('bg-red-700', 'hover:bg-red-800', 'cursor-pointer');
    } else {
        withdrawBtn.disabled = true;
        withdrawBtn.classList.remove('bg-red-700', 'hover:bg-red-800', 'cursor-pointer');
        withdrawBtn.classList.add('bg-gray-300', 'cursor-not-allowed');
    }
}