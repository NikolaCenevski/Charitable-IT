let url = window.location.href.toString().split(window.location.host)[1].split("?")[0];

let pages = parseInt(document.getElementById("helper").getAttribute("data-pages"));
let currentPage = parseInt(findGetParameter("page"));
let sort = findGetParameter("sort");
let order = findGetParameter("order");
let group = findGetParameter("groupBy")

let sortByInput = document.getElementById("sortBy");
sortByInput.value = sort;

let orderInput = document.getElementById("order");
orderInput.value = order;

let groupByInput = document.getElementById("groupBy") === null ? document.createElement("input") : document.getElementById("groupBy");
groupByInput.value = group;

sortByInput.addEventListener("change", function () {
    window.location.href = url + "?page=" + currentPage + "&sort=" + sortByInput.value + "&order=" + orderInput.value + "&groupBy=" + groupByInput.value;
})

orderInput.addEventListener("change", function() {
    window.location.href = url + "?page=" + currentPage + "&sort=" + sortByInput.value + "&order=" + orderInput.value + "&groupBy=" + groupByInput.value;
})

groupByInput.addEventListener("change", function () {
    window.location.href = url + "?page=" + 1 + "&sort=" + sortByInput.value + "&order=" + orderInput.value + "&groupBy=" + groupByInput.value;
})

document.getElementById('pagination').innerHTML = createPagination(pages, currentPage);

function createPagination(pages, page) {
    let str = '<ul class="pagination justify-content-center">';
    let active;
    let pagesBefore = page - 1;
    let pagesAfter = page + 1;

    if (page > 1) {
        str += '<li class="page-item"><a class="page-link" href="'+url+'?page='+(page-1)+'&sort='+sort+'&order='+order+'&groupBy='+group+'">Previous</a></li>';
    }

    if (pages < 6) {
        for (let p = 1; p <= pages; p++) {
            active = page == p ? "active" : "";
            str += '<li class="page-item '+active+'"><a class="page-link" href="'+url+'?page='+p+'&sort='+sort+'&order='+order+'&groupBy='+group+'">'+ p +'</a></li>';
        }
    }

    else {
        if (page > 2) {
            str += '<li class="page-item"><a class="page-link" href="'+url+'?page=1&sort='+sort+'&order='+order+'&groupBy='+group+'">1</a></li>';
            if (page > 3) {
                str += '<li class="page-item"><a class="page-link" href="'+url+'?page='+(page-2)+'&sort='+sort+'&order='+order+'&groupBy='+group+'">...</a></li>';
            }
        }

        if (page === 1) {
            pagesAfter += 2;
        } else if (page === 2) {
            pagesAfter += 1;
        }

        if (page === pages) {
            pagesBefore -= 2;
        } else if (page === pages-1) {
            pagesBefore -= 1;
        }

        for (let p = pagesBefore; p <= pagesAfter; p++) {
            if (p === 0) {
                p += 1;
            }
            if (p > pages) {
                continue
            }
            active = page == p ? "active" : "";
            str += '<li class="page-item '+active+'"><a class="page-link" href="'+url+'?page='+p+'&sort='+sort+'&order='+order+'&groupBy='+group+'">'+ p +'</a></li>';
        }

        if (page < pages-1) {
            if (page < pages-2) {
                str += '<li class="page-item"><a class="page-link" href="'+url+'?page='+(page+2)+'&sort='+sort+'&order='+order+'&groupBy='+group+'">...</a></li>';
            }
            str += '<li class="page-item"><a class="page-link" href="'+url+'?page='+pages+'&sort='+sort+'&order='+order+'&groupBy='+group+'">'+pages+'</a></li>';
        }
    }

    if (page < pages) {
        str += '<li class="page-item"><a class="page-link" href="'+url+'?page='+(page+1)+'&sort='+sort+'&order='+order+'&groupBy='+group+'">Next</a></li>';
    }
    str += '</ul>';

    return str;
}

function findGetParameter(parameterName) {
    let result = null,
        tmp = [];
    location.search
        .substr(1)
        .split("&")
        .forEach(function (item) {
            tmp = item.split("=");
            if (tmp[0] === parameterName) result = tmp[1];
        });
    return result;
}