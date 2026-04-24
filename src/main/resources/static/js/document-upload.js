document.addEventListener("DOMContentLoaded", () => {
    const form = document.querySelector("[data-document-upload]");

    if (!form) {
        return;
    }

    const dropzone = form.querySelector("[data-document-dropzone]");
    const fileInput = form.querySelector("[data-document-file-input]");
    const selectionText = form.querySelector("[data-document-selection]");

    if (!dropzone || !fileInput || !selectionText) {
        return;
    }

    const defaultSelectionText = selectionText.textContent.trim();

    function updateSelectionText(file, sourceLabel) {
        if (!file) {
            selectionText.textContent = defaultSelectionText;
            selectionText.classList.remove("has-file");
            return;
        }

        const suffix = sourceLabel ? ` (${sourceLabel})` : "";
        selectionText.textContent = `${file.name}${suffix}`;
        selectionText.classList.add("has-file");
    }

    function setSelectedFile(file, sourceLabel) {
        if (!file) {
            return;
        }

        const transfer = new DataTransfer();
        transfer.items.add(file);
        fileInput.files = transfer.files;
        updateSelectionText(file, sourceLabel);
    }

    function createScreenshotFile(blob) {
        const extension = blob.type === "image/jpeg" ? "jpg" : "png";
        const timestamp = new Date()
            .toISOString()
            .replace(/[-:]/g, "")
            .replace("T", "-")
            .slice(0, 15);

        return new File([blob], `screenshot-${timestamp}.${extension}`, { type: blob.type || "image/png" });
    }

    function extractImageFile(items) {
        if (!items) {
            return null;
        }

        for (const item of items) {
            if (!item.type || !item.type.startsWith("image/")) {
                continue;
            }

            const blob = item.getAsFile();

            if (!blob) {
                continue;
            }

            if (blob.name) {
                return blob;
            }

            return createScreenshotFile(blob);
        }

        return null;
    }

    function activateDropzone() {
        dropzone.classList.add("is-dragover");
    }

    function deactivateDropzone() {
        dropzone.classList.remove("is-dragover");
    }

    dropzone.addEventListener("click", () => {
        fileInput.click();
    });

    dropzone.addEventListener("keydown", (event) => {
        if (event.key !== "Enter" && event.key !== " ") {
            return;
        }

        event.preventDefault();
        fileInput.click();
    });

    fileInput.addEventListener("change", () => {
        const [file] = fileInput.files;
        updateSelectionText(file, file ? "vald via filväljare" : "");
    });

    ["dragenter", "dragover"].forEach((eventName) => {
        dropzone.addEventListener(eventName, (event) => {
            event.preventDefault();
            activateDropzone();
        });
    });

    ["dragleave", "dragend", "drop"].forEach((eventName) => {
        dropzone.addEventListener(eventName, () => {
            deactivateDropzone();
        });
    });

    dropzone.addEventListener("drop", (event) => {
        event.preventDefault();

        const [file] = event.dataTransfer.files;

        if (!file) {
            return;
        }

        setSelectedFile(file, "släppt i uppladdningsytan");
    });

    document.addEventListener("paste", (event) => {
        const activeElement = document.activeElement;
        const isTypingTarget = activeElement instanceof HTMLInputElement
            || activeElement instanceof HTMLTextAreaElement
            || activeElement?.isContentEditable;

        const imageFile = extractImageFile(event.clipboardData?.items);

        if (!imageFile) {
            return;
        }

        if (isTypingTarget && activeElement !== fileInput) {
            event.preventDefault();
        }

        setSelectedFile(imageFile, "inklistrad från clipboard");
        dropzone.focus();
    });
});
