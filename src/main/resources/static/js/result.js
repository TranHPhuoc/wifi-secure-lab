(() => {
    const cards = Array.from(document.querySelectorAll(".step-card"));
    const eventButtons = Array.from(document.querySelectorAll(".event-item"));

    if (cards.length === 0) {
        document.getElementById("playbackStatus").textContent =
            "Không có sự kiện để phát.";
        return;
    }

    const previousButton = document.getElementById("previousButton");
    const nextButton = document.getElementById("nextButton");
    const playButton = document.getElementById("playButton");
    const pauseButton = document.getElementById("pauseButton");
    const restartButton = document.getElementById("restartButton");
    const progressLabel = document.getElementById("progressLabel");
    const progressCount = document.getElementById("progressCount");
    const progressFill = document.getElementById("progressFill");
    const progressBar = document.getElementById("progressBar");
    const playbackStatus = document.getElementById("playbackStatus");

    let currentIndex = 0;
    let timerId = null;

    function stopPlayback() {
        if (timerId !== null) {
            window.clearInterval(timerId);
            timerId = null;
        }

        playButton.disabled = false;
        pauseButton.disabled = true;
    }

    function describeActors(actor) {
        const normalized = (actor || "").toLowerCase();
        const left = document.getElementById("actorClient");
        const right = document.getElementById("actorServer");
        const leftName = document.getElementById("leftActorName");
        const rightName = document.getElementById("rightActorName");
        const rightRole = document.getElementById("rightActorRole");

        left.classList.remove("active");
        right.classList.remove("active");

        if (normalized.includes("radius")) {
            leftName.textContent = "Authenticator";
            rightName.textContent = "RADIUS Server";
            rightRole.textContent = "Máy chủ xác thực";
            right.classList.add("active");

            if (normalized.includes("supplicant")) {
                leftName.textContent = "Supplicant";
                left.classList.add("active");
            }

            if (normalized.includes("authenticator")) {
                left.classList.add("active");
            }
        } else if (normalized.includes("access point")) {
            leftName.textContent = "Client";
            rightName.textContent = "Access Point";
            rightRole.textContent = "Điểm truy cập";
            right.classList.add("active");
        } else if (normalized.includes("client")) {
            leftName.textContent = "Client";
            rightName.textContent = "Access Point";
            rightRole.textContent = "Điểm truy cập";
            left.classList.add("active");
            right.classList.add("active");
        } else if (normalized.includes("supplicant")) {
            leftName.textContent = "Supplicant";
            rightName.textContent = "Authenticator";
            rightRole.textContent = "Thiết bị kiểm soát truy cập";
            left.classList.add("active");
        } else {
            leftName.textContent = "Client";
            rightName.textContent = "Access Point";
            rightRole.textContent = "Thành phần giao thức";
        }
    }

    function showStep(index) {
        currentIndex = Math.max(0, Math.min(index, cards.length - 1));

        cards.forEach((card, i) => {
            card.hidden = i !== currentIndex;
        });

        eventButtons.forEach((button, i) => {
            const active = i === currentIndex;
            button.classList.toggle("active", active);

            if (active) {
                button.setAttribute("aria-current", "step");
            } else {
                button.removeAttribute("aria-current");
            }
        });

        const card = cards[currentIndex];
        const actor = card.dataset.actor || "";
        const stepNumber = card.dataset.step || String(currentIndex + 1);
        const progress = ((currentIndex + 1) / cards.length) * 100;

        progressLabel.textContent = "Bước " + stepNumber;
        progressCount.textContent =
            (currentIndex + 1) + " / " + cards.length;
        progressFill.style.width = progress + "%";
        progressBar.setAttribute("aria-valuenow", String(Math.round(progress)));

        previousButton.disabled = currentIndex === 0;
        nextButton.disabled = currentIndex === cards.length - 1;

        describeActors(actor);

        if (currentIndex === cards.length - 1) {
            playbackStatus.textContent =
                "Đã đến bước cuối cùng của kịch bản mô phỏng.";
        } else {
            playbackStatus.textContent =
                "Đang xem bước " + stepNumber + ".";
        }
    }

    previousButton.addEventListener("click", () => {
        stopPlayback();
        showStep(currentIndex - 1);
    });

    nextButton.addEventListener("click", () => {
        stopPlayback();
        showStep(currentIndex + 1);
    });

    eventButtons.forEach((button) => {
        button.addEventListener("click", () => {
            stopPlayback();
            showStep(Number(button.dataset.index));
        });
    });

    playButton.addEventListener("click", () => {
        if (timerId !== null) return;

        if (currentIndex === cards.length - 1) {
            showStep(0);
        }

        playButton.disabled = true;
        pauseButton.disabled = false;
        playbackStatus.textContent = "Đang phát tự động...";

        timerId = window.setInterval(() => {
            if (currentIndex >= cards.length - 1) {
                stopPlayback();
                showStep(currentIndex);
                playbackStatus.textContent =
                    "Đã phát xong kịch bản mô phỏng.";
                return;
            }

            showStep(currentIndex + 1);
        }, 1800);
    });

    pauseButton.addEventListener("click", () => {
        stopPlayback();
        playbackStatus.textContent = "Đã tạm dừng.";
    });

    restartButton.addEventListener("click", () => {
        stopPlayback();
        showStep(0);
        playbackStatus.textContent = "Đã khởi động lại kịch bản.";
    });

    showStep(0);
})();
