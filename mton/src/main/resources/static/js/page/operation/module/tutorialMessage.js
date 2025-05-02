const tutorial = document.getElementById("tutorial-box");

tutorial.addEventListener("click", function() {
    tutorial.style.opacity = "0";
});

// 해당 요소에 새 튜토리얼 메시지 이벤트를 바인드한다.
function bindTutorialMessage(element, message) {
    element.addEventListener("mouseenter", function () {
       tutorial.innerText = message;
       tutorial.style.opacity = "1";
    });

    element.addEventListener("mouseleave", function () {
        tutorial.style.opacity = "0";
    });
}

// 강제로 원하는 메시지를 표시한다.
function forceShowMessage(message = "") {
    tutorial.innerText = message;
    tutorial.style.opacity = "1";
}

// 강제로 메시지 창을 숨긴다.
function forceHideMessage() {
    tutorial.style.opacity = "0";
}

export {
    bindTutorialMessage,
    forceShowMessage,
    forceHideMessage,
};
