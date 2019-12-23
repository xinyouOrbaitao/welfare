function getCode() {
    debugger;
    $.ajax({

        type: 'GET',
        dataType: 'json',
        url: '/getCode',
        error: function (data) {
            alert("网络异常，请重试!");
            location.reload();
        },
        success: function (data) {
            $("#codeImg").attr("src",data);
        }
    });
}
