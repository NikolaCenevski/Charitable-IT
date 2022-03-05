var imagePreviewRegion = document.getElementById("image-preview");
var titleInput = document.getElementById("titleImage");
var imagesInput = document.getElementById("images");
var moderatorInput = document.getElementById("moderatorImages");

titleInput.addEventListener("change", function () {
    imagePreviewRegion.innerHTML = "";
    handleFiles(titleInput);
    handleFiles(imagesInput);
    handleFiles(moderatorInput);
})

imagesInput.addEventListener("change", function () {
    imagePreviewRegion.innerHTML = "";
    handleFiles(titleInput);
    handleFiles(imagesInput);
    handleFiles(moderatorInput);
})

moderatorInput.addEventListener("change", function () {
    imagePreviewRegion.innerHTML = "";
    handleFiles(titleInput);
    handleFiles(imagesInput);
    handleFiles(moderatorInput);
})

function preventDefault(e) {
    e.preventDefault();
    e.stopPropagation();
}

function handleFiles(input) {
    var files = input.files;
    for (var i = 0, len = files.length; i < len; i++) {
        if (validateImage(files[i]))
            previewAnduploadImage(files[i]);
        else
            input.value="";
    }
}

function validateImage(image) {
    var validTypes = ['image/jpeg', 'image/png', 'image/gif'];
    if (validTypes.indexOf(image.type) === -1) {
        alert("Invalid File Type");
        return false;
    }

    var maxSizeInBytes = 10e6; // 10MB
    if (image.size > maxSizeInBytes) {
        alert("File too large");
        return false;
    }

    return true;
}

function previewAnduploadImage(image) {

    var imgView = document.createElement("div");
    imgView.className = "image-view";
    imagePreviewRegion.appendChild(imgView);

    var img = document.createElement("img");
    imgView.appendChild(img);

    var reader = new FileReader();
    reader.onload = function (e) {
        img.src = e.target.result;
    }
    reader.readAsDataURL(image);
}